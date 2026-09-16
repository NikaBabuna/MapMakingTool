<!--
  File: engine/src/main/java/com/aethelgard/engine/pool/README.md
  Purpose: Package folder index for pool
  Audience: Agents
  Update when: Package contents change
-->

# `com.aethelgard.engine.pool`

Step loop and Pool (F-002+). Pluggable compute (F-010). Systems + merge wired through F-004.

| Type | Role |
|------|------|
| `EngineConfig` | Step 0 seed + emissions + optional typed field seeds |
| `EngineSetup` | Tree, claimers, Systems, schema, user layer, `PoolCompute`, diagnostics |
| `Engine` | `create` / `advance` / `settled` / claim + Step output + claim/finish + input/view |
| `Pool` | `update()` once per Step via `PoolCompute`; typed fields after merge |
| `PoolCompute` | Pluggable update strategy |
| `PoolComputeContext` | Compute API: value, fields, Input View, emit |
| `SkeletonPoolCompute` | Default heartbeat / `nudge` / scripted emissions |
| `PoolSnapshot` | Settled read-out (`value`, `updateCount`, `fields`) |

See [package-info.java](package-info.java) and [docs/engine/architecture.md](../../../../../../../../docs/engine/architecture.md).
