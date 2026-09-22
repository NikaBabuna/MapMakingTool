<!--
  File: docs/architecture/host/diagnostics.md
  Purpose: EngineDiagnostics port
  Audience: Agents and humans
  Update when: The diagnostics port changes
-->

# Diagnostics

`EngineDiagnostics` is an observability port on the engine step. It does not write Pool fields, and it does not decide claiming or merge.

## What it reads

The step index at start and at settle, each claimed `(event, claimer)` pair, and each unmatched event.

## What it writes

Whatever the binding records. The engine does not read those records back.

## Procedure

`runStep` calls `stepStarted` before any other work, `eventClaimed` and `unmatchedEvent` after claiming, and `stepSettled` after the user view. `EngineSetup` wires the binding.

| Binding | Behavior |
|---------|----------|
| `Slf4jDiagnostics` | Default. Forwards to the SLF4J API. |
| `RecordingDiagnostics` | Keeps the calls for tests. |
| `NoopDiagnostics` | Discards them. |
| `CompositeDiagnostics` | Forwards to two bindings. |

The engine module depends on the SLF4J API. It does not depend on a binding such as Logback. A process that wants log lines supplies the binding outside `engine`. The reason is [ADR-008](../../paperwork/decisions/ADR-008-diagnostics.md).

The product keeps a separate hub of named collectors for advance time, heap, paint time, and phase time. That hub is not this port. It lives on [the session](../studio/session.md).

## What is true afterwards

A recorded binding can show that the step started, which events were claimed, which were unmatched, and that the step settled. The Pool is unchanged by these calls.

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| Port | `EngineDiagnostics` | `engine/.../diag/EngineDiagnostics.java` |
| Default | `Slf4jDiagnostics` | `engine/.../diag/Slf4jDiagnostics.java` |

Parent: [one engine step](README.md).
