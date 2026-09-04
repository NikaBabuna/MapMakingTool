<!--
  File: engine/src/main/java/com/aethelgard/engine/diag/README.md
  Purpose: Package folder index for diag
  Audience: Agents
  Update when: Package contents change
-->

# `com.aethelgard.engine.diag`

Observability port (F-003 / ADR-008). Does not affect Pool determinism.

| Type | Role |
|------|------|
| `EngineDiagnostics` | Port (+ `slf4j()`, `noop()`, `compose`) |
| `RecordingDiagnostics` | Test sink |
| `Slf4jDiagnostics` | Default SLF4J bridge |

Compile dep: `slf4j-api` only. Bindings live in adapters / test scope.
