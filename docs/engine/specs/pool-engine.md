<!--
  File: docs/engine/specs/pool-engine.md
  Purpose: Pool as engine object
  Audience: Agents implementing the engine
  Update when: Pool API changes
-->

# Pool as engine

> **Code status (through F-012 / G-002):** `Pool.update()` runs once per Step and delegates world rules to a pluggable `PoolCompute` (default `SkeletonPoolCompute`). Events during compute are decided by an `EventEmissionPolicy` (default `ScriptedEventEmissionPolicy` from config paths). Typed fields are `Object` values merged via `FieldMergeType`. See [architecture.md](../architecture.md) § Host extension points.

The Pool is an engine object exposing an `update()` method invoked once at the start of every Step's computation, plus any other lifecycle methods a given implementation needs.

What happens inside and around `update()` is defined in [step-lifecycle.md](step-lifecycle.md).

## Step 0 bootstrap

Step 0 has no prior Step. The Pool is seeded from a starting **config object** supplied by the caller (tests, CLI, or UI). That config is the sole initial configuration for the first `update()` (ADR-005).

## Host ports (product does not edit Pool internals)

| Port | Role |
|------|------|
| `PoolCompute` | How the Pool updates state each Step |
| `EventEmissionPolicy` | Which events enter the buffer during that update |
| `FieldSchema` / `FieldMergeType` | Declared fields and how System writes merge |

Defaults preserve the G-001 skeleton demo (heartbeat, optional `nudge`, scripted emissions). Product supplies replacements via `EngineSetup`.
