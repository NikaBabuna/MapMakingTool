<!--
  File: docs/architecture/engine/diagnostics.md
  Purpose: EngineDiagnostics and its bindings — the engine's observability port
  Audience: Agents and humans
  Update when: The EngineDiagnostics methods, a binding, or the calls in Engine.runStep and PoolComputeContext.emit change
-->

# Diagnostics

The engine reports what it is doing to an observer it does not depend on. The observer can log, record, or ignore those reports, and nothing it does can change the outcome of a step.

## What it reads

Five kinds of report: a step started (with its index), a step settled (with its index), an event emitted, an event claimed by a claimer, and an event no claimer claimed.

## What it writes

Whatever the binding does with a report: an SLF4J log line, an in-memory record, or nothing. The engine never reads a report back.

## Model

A binding is a sink $\mathcal{D}$ that receives a sequence of reports over the alphabet

$$\mathit{Reports} = \{\, \mathsf{started}(k),\ \mathsf{settled}(k),\ \mathsf{emitted}(e),\ \mathsf{claimed}(e, \mathit{cl}),\ \mathsf{unmatched}(e) \,\},$$

and the engine's state transition does not depend on $\mathcal{D}$: for any two bindings $\mathcal{D}_1, \mathcal{D}_2$, $\mathrm{step}_{\mathcal{D}_1}(S) = \mathrm{step}_{\mathcal{D}_2}(S)$. One step reports, in order,

$$\mathsf{started}(k)\;\; \mathsf{emitted}(e)^{*}\;\; \mathsf{claimed}(e,\mathit{cl})^{*}\;\; \mathsf{unmatched}(e)^{*}\;\; \mathsf{settled}(k),$$

with the emitted reports in emission order, and the unmatched reports in buffer order. The claimed reports are grouped by claimer in an unspecified order ([events](events.md)). A composite binding $\mathcal{D}_1 \otimes \mathcal{D}_2$ forwards each report to $\mathcal{D}_1$, then to $\mathcal{D}_2$.

## Procedure

1. The engine reports the start of step $k$ before any other work, and the settle of step $k$ after the view has run. [`EngineDiagnostics.stepStarted`](../../../engine/src/main/java/com/aethelgard/engine/diag/EngineDiagnostics.java).
2. Every emission reports through `eventEmitted`, from inside `PoolComputeContext.emit`. [`EngineDiagnostics.eventEmitted`](../../../engine/src/main/java/com/aethelgard/engine/diag/EngineDiagnostics.java).
3. After claiming, the engine reports each claimed pair through `eventClaimed`, then each unmatched event through `unmatchedEvent`. [`EngineDiagnostics.unmatchedEvent`](../../../engine/src/main/java/com/aethelgard/engine/diag/EngineDiagnostics.java).
4. The default binding, from `slf4j()`, logs to the logger named `com.aethelgard.engine`: step, emission, and claim reports at debug level, and unmatched events at warn level. [`Slf4jDiagnostics`](../../../engine/src/main/java/com/aethelgard/engine/diag/Slf4jDiagnostics.java).
5. `noop()` discards every report. [`NoopDiagnostics`](../../../engine/src/main/java/com/aethelgard/engine/diag/NoopDiagnostics.java).
6. `compose(first, second)` forwards each report to both, first then second. [`CompositeDiagnostics`](../../../engine/src/main/java/com/aethelgard/engine/diag/CompositeDiagnostics.java).
7. `RecordingDiagnostics` appends a `Record(kind, stepIndex, event, claimer)` per report. The index is −1 for event reports, and the event and claimer are null where the kind has none. `records`, `unmatchedEvents`, and `clear` read and reset it. [`RecordingDiagnostics`](../../../engine/src/main/java/com/aethelgard/engine/diag/RecordingDiagnostics.java).

## What is true afterwards

A recording binding shows that the step started, which events fired, which were claimed and by whom, which were unmatched, and that the step settled. The Pool is the same as it would be under any other binding. The engine module compiles against the SLF4J API only; a process that wants log lines supplies a binding outside the engine.

The product's hub of named timing and memory collectors is a different mechanism: [session diagnostics](../session/diagnostics.md).

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Port | `EngineDiagnostics` | `stepStarted`, `stepSettled`, `eventEmitted`, `eventClaimed`, `unmatchedEvent`, `slf4j`, `noop`, `compose` | [`engine/src/main/java/com/aethelgard/engine/diag/EngineDiagnostics.java`](../../../engine/src/main/java/com/aethelgard/engine/diag/EngineDiagnostics.java) |
| Default binding | `Slf4jDiagnostics` | `Slf4jDiagnostics` | [`engine/src/main/java/com/aethelgard/engine/diag/Slf4jDiagnostics.java`](../../../engine/src/main/java/com/aethelgard/engine/diag/Slf4jDiagnostics.java) |
| Silent binding | `NoopDiagnostics` | `NoopDiagnostics.INSTANCE` | [`engine/src/main/java/com/aethelgard/engine/diag/NoopDiagnostics.java`](../../../engine/src/main/java/com/aethelgard/engine/diag/NoopDiagnostics.java) |
| Fan-out | `CompositeDiagnostics` | `CompositeDiagnostics` | [`engine/src/main/java/com/aethelgard/engine/diag/CompositeDiagnostics.java`](../../../engine/src/main/java/com/aethelgard/engine/diag/CompositeDiagnostics.java) |
| Test sink | `RecordingDiagnostics` | `Kind`, `Record`, `records`, `unmatchedEvents`, `RecordingDiagnostics.clear` | [`engine/src/main/java/com/aethelgard/engine/diag/RecordingDiagnostics.java`](../../../engine/src/main/java/com/aethelgard/engine/diag/RecordingDiagnostics.java) |

Parent: [one engine step](README.md). Why the engine logs through the API only: [ADR-008](../../paperwork/decisions/ADR-008-diagnostics.md).
