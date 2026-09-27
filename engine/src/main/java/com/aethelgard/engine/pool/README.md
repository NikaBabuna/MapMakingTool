<!--
  File: engine/src/main/java/com/aethelgard/engine/pool/README.md
  Purpose: Door to the stepper: the engine that runs each step, how it is set up, the Pool of fields it steps, and the compute and emission that run inside the Pool
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Pool

Runs a simulation step by step: the engine that drives each step in its order, the setup and configuration that build it, the Pool of named fields it steps, and the compute and event emission that run inside each update of the Pool.

**Paper:** [One engine step](../../../../../../../../docs/architecture/engine/README.md) · [Setup and lifecycle](../../../../../../../../docs/architecture/engine/setup.md) · [Pool](../../../../../../../../docs/architecture/engine/pool.md) · [Events](../../../../../../../../docs/architecture/engine/events.md) · [Determinism](../../../../../../../../docs/architecture/engine/determinism.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The engine drives the Pool through operations no other code may call — update, apply the merged fields, take a snapshot — so the two share a package and those operations stay package-private. The setup that builds them, and the compute and emission policy that run inside a Pool update, belong to the same unit. How events are claimed, how systems run, how writes merge, and how input is staged are the packages beside this one; the engine only calls them in order.

## How it works

1. `Engine.create` takes an `EngineConfig` (the step-0 seed, scripted category paths, field seeds) and an `EngineSetup` (the ports: category tree, claimers, systems, field schema, user layer, compute, emission policy, diagnostics; `EngineSetup.defaults` fills every missing port). It builds the `Pool` and runs step 0.
2. `Engine.advance` runs further steps; each is `runStep`, whose stages are the level page's: report the start, check that the event buffer is empty, stage the input view, update the Pool, consume persistent input, claim the events, run the claiming systems on one snapshot, check the barrier, merge the writes, clear the buffer, show the settled Pool to the user view, and report the settle.
3. In the update, `Pool.update` counts it and calls the wired `PoolCompute` (by default `SkeletonPoolCompute`) with a `PoolComputeContext`, the compute's whole API: it reads and sets the heartbeat and fields, reads the input view, and emits events through the `EventEmissionPolicy` (by default `ScriptedEventEmissionPolicy`, which emits the scripted paths).
4. After merge, `Pool.applyFields` stores the merged fields, and `Pool.snapshot` gives the immutable `PoolSnapshot` that callers read through `Engine.settled`, beside `stepIndex` and the last claim, input, and barrier results.

**Start reading at:** `Engine.create` in [Engine.java](Engine.java).

## Depends on

- [events/](../events/README.md) — the category tree, the event buffer, claimers, and claiming
- [systems/](../systems/README.md) — the systems the engine runs, and the claim/finish barrier; the pool and systems packages are peers ([conventions](../../../../../../../../docs/architecture/conventions.md))
- [merge/](../merge/README.md) — the field schema, the output buffer, and the typed merge
- [user/](../user/README.md) — the input register and the view port; the pool and user packages are peers
- [diagnostics/](../diagnostics/README.md) — the reporting port

## Used by

- [product world/](../../../../../../../../product/src/main/java/com/aethelgard/product/world/README.md) — the product builds its setup and creates the engine
- [product session/](../../../../../../../../product/src/main/java/com/aethelgard/product/session/README.md) — the session advances the engine and reads its settled Pool
- [systems/](../systems/README.md), [user/](../user/README.md) — both read the `PoolSnapshot`
- [the pool tests](../../../../../../test/java/com/aethelgard/engine/pool/README.md) — creation, stepping, determinism, and the host ports

## Where each step happens

### [Pool](../../../../../../../../docs/architecture/engine/pool.md)

| Step | Member | File |
|------|--------|------|
| 1. Construction sets the heartbeat and update count, and seeds every declared field | `Pool` | [Pool.java](Pool.java) |
| 2. The update keeps the staged view, counts itself, and runs the compute | `Pool.update` | [Pool.java](Pool.java) |
| 3. The context is the compute's whole API for one update | `PoolComputeContext` | [PoolComputeContext.java](PoolComputeContext.java) |
| 4. The default compute adds to the heartbeat, nudges, and applies the emission policy | `SkeletonPoolCompute.compute` | [SkeletonPoolCompute.java](SkeletonPoolCompute.java) |
| 5. After merge, the field map is replaced by the merged fields | `Pool.applyFields` | [Pool.java](Pool.java) |
| 6. A snapshot of the heartbeat, count, and fields is taken | `PoolSnapshot` | [PoolSnapshot.java](PoolSnapshot.java) |

### [Events](../../../../../../../../docs/architecture/engine/events.md)

| Step | Member | File |
|------|--------|------|
| 3. During compute, the wired policy decides which categories fire | `ScriptedEventEmissionPolicy.emitEvents`, `EventEmissionPolicy.emitEvents` | [ScriptedEventEmissionPolicy.java](ScriptedEventEmissionPolicy.java), [EventEmissionPolicy.java](EventEmissionPolicy.java) |

### [Setup and lifecycle](../../../../../../../../docs/architecture/engine/setup.md)

| Step | Member | File |
|------|--------|------|
| 1. The configuration copies its paths and seeds | `EngineConfig` | [EngineConfig.java](EngineConfig.java) |
| 2. The wiring replaces each missing port with its default | `EngineSetup` | [EngineSetup.java](EngineSetup.java) |
| 3. Creating without a setup uses the default setup | `Engine.create` | [Engine.java](Engine.java) |
| 4. Creating resolves the scripted paths, builds the Pool, and runs step 0 | `Engine.create` | [Engine.java](Engine.java) |
| 5. An advance runs one step, or n steps | `Engine.advance` | [Engine.java](Engine.java) |
| 6. Each step runs the stages of the level page | `Engine.runStep` | [Engine.java](Engine.java) |
| 7. A caller reads the run | `Engine.settled` | [Engine.java](Engine.java) |

### [Determinism](../../../../../../../../docs/architecture/engine/determinism.md)

| Step | Member | File |
|------|--------|------|
| 1. Step 0 starts from the configuration and the schema alone | `Engine.create` | [Engine.java](Engine.java) |
| 3. The compute and the emission policy are functions of what they read | `PoolCompute.compute` | [PoolCompute.java](PoolCompute.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [Engine.java](Engine.java) | Drives each step: stage, compute, claim, run, barrier, merge, view | `Engine`, `create`, `advance`, `settled`, `stepIndex` |
| [EngineConfig.java](EngineConfig.java) | The step-0 seed, the scripted category paths, and the field seeds | `EngineConfig` |
| [EngineSetup.java](EngineSetup.java) | The engine's ports, each defaulted when not given | `EngineSetup`, `defaults` |
| [Pool.java](Pool.java) | The shared state: heartbeat, update count, and named fields | `Pool`, `update`, `applyFields`, `snapshot` |
| [PoolSnapshot.java](PoolSnapshot.java) | The immutable settled Pool after a step | `PoolSnapshot`, `field`, `fieldOrZero` |
| [PoolCompute.java](PoolCompute.java) | The port of the Pool's update strategy | `PoolCompute`, `compute` |
| [PoolComputeContext.java](PoolComputeContext.java) | What a compute and an emission policy may read and do in one update | `PoolComputeContext`, `emit`, `emitPath`, `field` |
| [SkeletonPoolCompute.java](SkeletonPoolCompute.java) | The default compute: heartbeat, nudge, then the emission policy | `SkeletonPoolCompute` |
| [EventEmissionPolicy.java](EventEmissionPolicy.java) | The port that decides which events fire in an update | `EventEmissionPolicy`, `emitEvents` |
| [ScriptedEventEmissionPolicy.java](ScriptedEventEmissionPolicy.java) | The default policy: emit the scripted category paths | `ScriptedEventEmissionPolicy` |
