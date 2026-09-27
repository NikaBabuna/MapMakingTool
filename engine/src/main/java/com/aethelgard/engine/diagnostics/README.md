<!--
  File: engine/src/main/java/com/aethelgard/engine/diagnostics/README.md
  Purpose: Door to the engine's diagnostics: the port that reports steps, events, claims, and unmatched events, and its bindings
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Diagnostics

Reports what a step does — its start and settle, every emitted event, every claim, and every event nobody claimed — through one port, with bindings that log, stay silent, fan out, or record.

**Paper:** [Diagnostics](../../../../../../../../docs/architecture/engine/diagnostics.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

An unmatched event must never vanish silently, but the engine must not depend on how a host wants it reported. So the reports go through one small port in its own package, and a host picks the binding. This is the engine's report port; the product's measures of a running session are a different mechanism, in the product's session diagnostics.

## How it works

1. `EngineDiagnostics` is the port: `stepStarted`, `eventEmitted`, `eventClaimed`, `unmatchedEvent`, and `stepSettled`.
2. The engine reports the start of a step before any other work, each emission from inside the compute context, each claimed pair and then each unmatched event after claiming, and the settle last.
3. The bindings: `Slf4jDiagnostics`, the default from `EngineDiagnostics.slf4j`, logs to SLF4J; `NoopDiagnostics` discards; `CompositeDiagnostics`, from `EngineDiagnostics.compose`, forwards to two ports in order; `RecordingDiagnostics` keeps every report for tests.

**Start reading at:** `EngineDiagnostics` in [EngineDiagnostics.java](EngineDiagnostics.java).

## Depends on

- [events/](../events/README.md) — the event and the claimer a report names

## Used by

- [pool/](../pool/README.md) — the engine and the compute context report through the port
- [the diagnostics tests](../../../../../../test/java/com/aethelgard/engine/diagnostics/README.md) — what a step reports, and that reporting never changes the Pool

## Where each step happens

### [Diagnostics](../../../../../../../../docs/architecture/engine/diagnostics.md)

| Step | Member | File |
|------|--------|------|
| 1. The start and the settle of a step are reported | `EngineDiagnostics.stepStarted` | [EngineDiagnostics.java](EngineDiagnostics.java) |
| 2. Every emission is reported | `EngineDiagnostics.eventEmitted` | [EngineDiagnostics.java](EngineDiagnostics.java) |
| 3. Every claim, then every unmatched event, is reported | `EngineDiagnostics.unmatchedEvent` | [EngineDiagnostics.java](EngineDiagnostics.java) |
| 4. The default binding logs | `Slf4jDiagnostics` | [Slf4jDiagnostics.java](Slf4jDiagnostics.java) |
| 5. The silent binding discards | `NoopDiagnostics` | [NoopDiagnostics.java](NoopDiagnostics.java) |
| 6. The composite binding forwards to both, in order | `CompositeDiagnostics` | [CompositeDiagnostics.java](CompositeDiagnostics.java) |
| 7. The recording binding keeps every report | `RecordingDiagnostics` | [RecordingDiagnostics.java](RecordingDiagnostics.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [EngineDiagnostics.java](EngineDiagnostics.java) | The report port, and its factories | `EngineDiagnostics`, `slf4j`, `noop`, `compose` |
| [Slf4jDiagnostics.java](Slf4jDiagnostics.java) | The default binding: log to SLF4J | `Slf4jDiagnostics` |
| [NoopDiagnostics.java](NoopDiagnostics.java) | The silent binding | `NoopDiagnostics` |
| [CompositeDiagnostics.java](CompositeDiagnostics.java) | Forwards every report to two ports | `CompositeDiagnostics` |
| [RecordingDiagnostics.java](RecordingDiagnostics.java) | Keeps every report, for tests | `RecordingDiagnostics`, `records` |
