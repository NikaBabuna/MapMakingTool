<!--
  File: engine/src/main/java/com/aethelgard/engine/pool/README.md
  Purpose: Package folder index for pool
  Audience: Agents
  Update when: Package contents change
-->

# `com.aethelgard.engine.pool`

Step loop and Pool heartbeat (F-002+).

| Type | Role |
|------|------|
| `EngineConfig` | Step 0 seed + optional emission paths |
| `EngineSetup` | Tree, claimers, diagnostics wiring |
| `Engine` | `create` / `advance` / `settled` / `lastClaimResult` |
| `Pool` | `update()` once per Step |
| `PoolSnapshot` | Settled read-out |

See [package-info.java](package-info.java) and [docs/engine/architecture.md](../../../../../../../../docs/engine/architecture.md).
