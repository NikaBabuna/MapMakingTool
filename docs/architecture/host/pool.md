<!--
  File: docs/architecture/host/pool.md
  Purpose: Pool update, the three host ports, and the skeleton compute
  Audience: Agents and humans
  Update when: Pool.update, PoolCompute, or EngineSetup changes
-->

# Pool

The Pool is the shared state object. `Pool.update` runs once at the start of each engine step and delegates the update to a `PoolCompute`. The product does not edit `Pool` to change what a step computes.

## What it reads

`PoolComputeContext` exposes the current heartbeat `value`, the typed field map, the staged `InputView`, the category tree, and emit methods. `EngineConfig` is the only seed for step 0: an initial value, optional scripted emission paths, and optional field seeds. There is no prior step to read.

## What it writes

`PoolCompute` may replace `value` and may emit events into the shared buffer. It does not apply system field writes. Those arrive later, through typed merge.

## Procedure

`EngineSetup` wires the run: category tree, extra claimers, systems, field schema, user input, user view, `PoolCompute`, `EventEmissionPolicy`, and diagnostics. A null port keeps the default.

| Port | Default | What the default does |
|------|---------|------------------------|
| `PoolCompute` | `SkeletonPoolCompute` | `value = value + 1`. If the input view has action `nudge` active, also `value = value + 100`. Then `applyEmissionPolicy()`. |
| `EventEmissionPolicy` | `ScriptedEventEmissionPolicy` | Emits the category paths listed on `EngineConfig`. |
| `FieldSchema` | Caller-supplied | Maps each field name to a `FieldMergeType`. |

A custom `PoolCompute` may ignore the emission policy and call `emit` or `emitPath` itself. `SkeletonPoolCompute` always calls `applyEmissionPolicy()`.

## What is true afterwards

The heartbeat has advanced by at least 1, plus 100 when `nudge` was active, unless a replacement compute set a different value. Events emitted during this update are in the buffer. Field values are still the pre-merge standing values. `stepIndex()` on the engine is the 0-based index of the last completed step: create finishes at 0, and each `advance` adds 1.

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| Step driver | `Engine` | `engine/.../pool/Engine.java` |
| State | `Pool` | `engine/.../pool/Pool.java` |
| Update strategy | `PoolCompute` | `engine/.../pool/PoolCompute.java` |
| Default update | `SkeletonPoolCompute` | `engine/.../pool/SkeletonPoolCompute.java` |
| Wiring | `EngineSetup` | `engine/.../pool/EngineSetup.java` |
| Step 0 seed | `EngineConfig` | `engine/.../pool/EngineConfig.java` |
| Settled read | `PoolSnapshot` | `engine/.../pool/PoolSnapshot.java` |

Parent: [one engine step](README.md). Emission rules: [events](events.md). Field apply: [merge](merge.md).
