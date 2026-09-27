<!--
  File: product/src/main/java/com/aethelgard/product/session/diagnostics/README.md
  Purpose: Door to the session diagnostics: the hub of bounded sample collectors, their ids, and the timing of phases
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Diagnostics

Measures a running session without changing it: a hub of named, bounded sample collectors, the ids of the default ones, and the wrapper that times a phase.

**Paper:** [Session diagnostics](../../../../../../../../../docs/architecture/session/diagnostics.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

The phases are timed from inside the engine's step, but the numbers belong to the session that runs it. Keeping the collectors, their ids, and the timing wrapper in one package lets both the world's wiring and the session reach them without reaching each other's code, which keeps the packages free of a cycle. What a sample means for the world is not decided here; the collectors only keep numbers.

## How it works

1. `DiagnosticsHub.withDefaults` registers the ten collectors named by `DiagnosticIds`, each a `RingDiagnosticCollector` of `DEFAULT_CAPACITY` samples. A product can add its own through `register`, since a collector is any `DiagnosticCollector`.
2. `record(id, value)` forwards a sample to the collector with that id, and does nothing for an unknown id or a disabled collector.
3. A ring writes at its cursor and keeps at most its capacity; `latest`, `mean`, and `samples` read it, and `summaryLine` prints it. `report` and `listReport` print every collector.
4. `PhaseTiming.withHub` binds the hub to the current thread for the length of one action, and `PhaseTiming.record` writes to the bound hub, if any.
5. A `TimingSubSystem` wraps a phase: it keeps the phase's id and write range, so the engine sees the phase unchanged, and records the wall time of each run under its diagnostic id.

**Start reading at:** `DiagnosticsHub.withDefaults` in [DiagnosticsHub.java](DiagnosticsHub.java).

## Depends on

- [engine systems](../../../../../../../../../engine/src/main/java/com/aethelgard/engine/systems/README.md) — the `SubSystem` port that `TimingSubSystem` wraps

## Used by

- [session/](../README.md) — the session builds the hub, binds it for each step, and records the step's time and heap
- [world/](../../world/README.md) — the setup wraps six phases in `TimingSubSystem`
- [cli](../../../../../../../../../cli/README.md) — the command language reports and toggles collectors
- [ui](../../../../../../../../../ui/README.md) — the studio records its paint time and shows the report
- [the diagnostics tests](../../../../../../../test/java/com/aethelgard/product/session/diagnostics/README.md) — what is recorded, how much is kept, and that the world is unchanged

## Where each step happens

### [Session diagnostics](../../../../../../../../../docs/architecture/session/diagnostics.md)

| Step | Member | File |
|------|--------|------|
| 1. The ten default collectors are registered | `DiagnosticsHub.withDefaults`, `DiagnosticIds` | [DiagnosticsHub.java](DiagnosticsHub.java), [DiagnosticIds.java](DiagnosticIds.java) |
| 2. A sample goes to the collector with its id | `DiagnosticsHub.record` | [DiagnosticsHub.java](DiagnosticsHub.java) |
| 3. A ring writes at its cursor and keeps at most its capacity | `RingDiagnosticCollector.record` | [RingDiagnosticCollector.java](RingDiagnosticCollector.java) |
| 4. A collector is any implementation of the port | `DiagnosticCollector` | [DiagnosticCollector.java](DiagnosticCollector.java) |
| 5. The report prints one line per collector | `DiagnosticsHub.report`, `RingDiagnosticCollector.summaryLine` | [DiagnosticsHub.java](DiagnosticsHub.java), [RingDiagnosticCollector.java](RingDiagnosticCollector.java) |
| 6. The hub is bound to the thread for one action | `PhaseTiming.withHub` | [PhaseTiming.java](PhaseTiming.java) |
| 7. A wrapped phase records its wall time | `TimingSubSystem.execute` | [TimingSubSystem.java](TimingSubSystem.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [DiagnosticsHub.java](DiagnosticsHub.java) | The registry of collectors: record, enable, clear, and report | `DiagnosticsHub`, `withDefaults`, `record`, `register`, `report` |
| [DiagnosticCollector.java](DiagnosticCollector.java) | The port a collector implements | `DiagnosticCollector` |
| [RingDiagnosticCollector.java](RingDiagnosticCollector.java) | A collector that keeps its latest samples in a ring | `RingDiagnosticCollector`, `record`, `mean`, `summaryLine` |
| [DiagnosticIds.java](DiagnosticIds.java) | The ids of the ten default collectors | `DiagnosticIds`, `ADVANCE_WALL`, `PHASE_TRACE` |
| [PhaseTiming.java](PhaseTiming.java) | Binds a hub to the current thread so phases can record into it | `PhaseTiming`, `withHub`, `record` |
| [TimingSubSystem.java](TimingSubSystem.java) | Wraps a phase and records its wall time | `TimingSubSystem`, `execute` |
