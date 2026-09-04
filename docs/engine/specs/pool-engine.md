<!--
  File: docs/engine/specs/pool-engine.md
  Purpose: Pool as engine object
  Audience: Agents implementing the engine
  Update when: Pool API changes
-->

# Pool as engine

The Pool is an engine object exposing an `update()` method invoked once at the start of every Step's computation, plus any other lifecycle methods a given implementation needs.

What happens inside and around `update()` is defined in [step-lifecycle.md](step-lifecycle.md).

## Step 0 bootstrap

Step 0 has no prior Step. The Pool is seeded from a starting **config object** supplied by the caller (tests, CLI, or UI). That config is the sole initial configuration for the first `update()` (ADR-005).
