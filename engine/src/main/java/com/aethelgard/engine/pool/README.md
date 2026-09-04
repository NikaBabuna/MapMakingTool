<!--
  File: engine/src/main/java/com/aethelgard/engine/pool/README.md
  Purpose: Package folder index for pool
  Audience: Agents
  Update when: Package contents change
-->

# `com.aethelgard.engine.pool`

Step loop and Pool heartbeat (F-002+). Systems + merge wired through F-004.

| Type | Role |
|------|------|
| `EngineConfig` | Step 0 seed + emissions + optional typed field seeds |
| `EngineSetup` | Tree, claimers, Systems, field schema, user layer, diagnostics |
| `Engine` | `create` / `advance` / `settled` / claim + Step output + claim/finish + input/view |
| `Pool` | `update()` once per Step; typed fields after merge |
| `PoolSnapshot` | Settled read-out (`value`, `updateCount`, `fields`) |

See [package-info.java](package-info.java) and [docs/engine/architecture.md](../../../../../../../../docs/engine/architecture.md).
