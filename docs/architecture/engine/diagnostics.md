<!--
  File: docs/architecture/engine/diagnostics.md
  Purpose: Diagnostics — the engine's observability port and its bindings
  Audience: Agents and humans
  Update when: The reports of the port, a binding, or where the engine reports, changes
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

1. The engine reports the start of step $k$ before any other work, and the settle of step $k$ after the view has run.
2. Every emission is reported as it is made, from inside the compute context.
3. After claiming, the engine reports each claimed pair, then each unmatched event.
4. The default binding logs to the logger named `com.aethelgard.engine`: step, emission, and claim reports at debug level, and unmatched events at warn level.
5. The silent binding discards every report.
6. The composite binding forwards each report to both ports, first then second.
7. `RecordingDiagnostics` appends a `Record` of the kind, the step index, the event, and the claimer per report. The index is −1 for event reports, and the event and claimer are null where the kind has none. Its records, its unmatched events, and a reset can be read or done.

## What is true afterwards

A recording binding shows that the step started, which events fired, which were claimed and by whom, which were unmatched, and that the step settled. The Pool is the same as it would be under any other binding. The engine module compiles against the SLF4J API only; a process that wants log lines supplies a binding outside the engine.

The product's hub of named timing and memory collectors is a different mechanism: [session diagnostics](../session/diagnostics.md).

Code: [engine/diagnostics/](../../../engine/src/main/java/com/aethelgard/engine/diagnostics/README.md)  
Parent: [one engine step](README.md). Why the engine logs through the API only: [ADR-008](../../paperwork/decisions/ADR-008-diagnostics.md).
