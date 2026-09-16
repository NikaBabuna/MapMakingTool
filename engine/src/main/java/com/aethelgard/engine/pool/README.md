<!--
  File: engine/src/main/java/com/aethelgard/engine/pool/README.md
  Purpose: Package folder index for pool
  Audience: Agents
  Update when: Package contents change
-->

# `com.aethelgard.engine.pool`

Step loop and Pool (F-002+). Pluggable compute (F-010) and emission (F-012).

| Type | Role |
|------|------|
| `EngineConfig` | Step 0 seed + emissions + optional typed field seeds |
| `EngineSetup` | Tree, claimers, Systems, schema, user layer, compute, emission, diagnostics |
| `Engine` | `create` / `advance` / `settled` / claim + Step output + claim/finish + input/view |
| `Pool` | `update()` once per Step via `PoolCompute`; typed fields after merge |
| `PoolCompute` | Pluggable update strategy |
| `EventEmissionPolicy` | Pluggable which-events-fire strategy |
| `PoolComputeContext` | Compute/policy API: value, fields, Input View, emit / emitPath |
| `SkeletonPoolCompute` | Default heartbeat / `nudge` / `applyEmissionPolicy()` |
| `ScriptedEventEmissionPolicy` | Default: emit scripted config paths |
| `PoolSnapshot` | Settled read-out (`value`, `updateCount`, `fields`) |

See [package-info.java](package-info.java) and [docs/engine/architecture.md](../../../../../../../../docs/engine/architecture.md).
