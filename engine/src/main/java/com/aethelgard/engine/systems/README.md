<!--
  File: engine/src/main/java/com/aethelgard/engine/systems/README.md
  Purpose: Door to the systems: claiming systems, their sub-systems and the order they run in, their io, and the claim/finish barrier
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Systems

Runs the systems of a step: a system claims events of its category, runs its sub-systems in a defined order against the step's one snapshot, and hands back its writes; a barrier checks that every claiming system finished before merge.

**Paper:** [Systems](../../../../../../../../docs/architecture/engine/systems.md) · [Determinism](../../../../../../../../docs/architecture/engine/determinism.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

A system is the engine's unit of pluggable work, and the rules that make a run repeatable — one shared snapshot, a defined sub-system order when writes overlap, a balanced claim and finish — belong together. A product writes its own sub-systems against the `SubSystem` port here; how events reach a system is `events/`, and how its writes are settled is `merge/`.

## How it works

1. A `SystemConfig` names a system, its category, and its sub-systems, and may carry a `ConflictResolutionSubSystem` that orders sub-systems whose write ranges overlap.
2. An `EngineSystem` builds its `EventClaimer` once. When it claimed an event in a step, the engine runs it: `run` orders the sub-systems (`orderSubSystems`, using `findOverlapping` and the resolver), gives each a `SubSystemIo` over the step's `PoolSnapshot` and the staging so far, and returns the staged writes.
3. Through the io, a sub-system reads the snapshot, reads what earlier sub-systems staged, and writes its declared fields.
4. A `ClaimFinishBarrier` counts claims and finishes around each claiming system, and `requireBalanced` refuses merge when they differ; its `ClaimFinishSnapshot` is kept after the step.

**Start reading at:** `EngineSystem.run` in [EngineSystem.java](EngineSystem.java).

## Depends on

- [events/](../events/README.md) — the claimer and the category
- [pool/](../pool/README.md) — the `PoolSnapshot` every sub-system reads; the pool and systems packages are peers ([conventions](../../../../../../../../docs/architecture/conventions.md))

## Used by

- [pool/](../pool/README.md) — the engine runs the claiming systems and checks the barrier
- [product world/](../../../../../../../../product/src/main/java/com/aethelgard/product/world/README.md) and its phase packages — every phase is a `SubSystem`, and the wiring builds one `EngineSystem`
- [product session/](../../../../../../../../product/src/main/java/com/aethelgard/product/session/README.md), [product session diagnostics/](../../../../../../../../product/src/main/java/com/aethelgard/product/session/diagnostics/README.md) — the session describes the systems, and the phase timer wraps a `SubSystem`
- [the systems tests](../../../../../../test/java/com/aethelgard/engine/systems/README.md) — claiming, the shared snapshot, the order, and the barrier

## Where each step happens

### [Systems](../../../../../../../../docs/architecture/engine/systems.md)

| Step | Member | File |
|------|--------|------|
| 1. The configuration keeps an immutable copy of the sub-systems | `SystemConfig` | [SystemConfig.java](SystemConfig.java) |
| 2. A system builds its claimer once | `EngineSystem.claimer` | [EngineSystem.java](EngineSystem.java) |
| 3. Sub-systems are grouped by the fields they write | `EngineSystem.findOverlapping` | [EngineSystem.java](EngineSystem.java) |
| 4. The order is kept, or set by the resolver when writes overlap | `EngineSystem.orderSubSystems`, `ConflictResolutionSubSystem.resolveOrder` | [EngineSystem.java](EngineSystem.java), [ConflictResolutionSubSystem.java](ConflictResolutionSubSystem.java) |
| 5. Each sub-system runs with an io over the snapshot and the staging | `SubSystem.execute` | [SubSystem.java](SubSystem.java) |
| 6. A sub-system reads and writes through its io | `SubSystemIo.write` | [SubSystemIo.java](SubSystemIo.java) |
| 7. The staged writes are returned | `EngineSystem.run` | [EngineSystem.java](EngineSystem.java) |
| 8. Claims and finishes are counted and must balance | `ClaimFinishBarrier.requireBalanced`, `ClaimFinishSnapshot` | [ClaimFinishBarrier.java](ClaimFinishBarrier.java), [ClaimFinishSnapshot.java](ClaimFinishSnapshot.java) |

### [Determinism](../../../../../../../../docs/architecture/engine/determinism.md)

| Step | Member | File |
|------|--------|------|
| 5. Every system reads one snapshot, and orders its sub-systems the same way every run | `EngineSystem.orderSubSystems` | [EngineSystem.java](EngineSystem.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [SystemConfig.java](SystemConfig.java) | A system's id, category, sub-systems, and resolver | `SystemConfig` |
| [EngineSystem.java](EngineSystem.java) | A claiming system that runs its sub-systems | `EngineSystem`, `run`, `claimer` |
| [SubSystem.java](SubSystem.java) | The port of one block of work, with its write range | `SubSystem`, `execute`, `writeRanges` |
| [SubSystemIo.java](SubSystemIo.java) | What one sub-system may read and write | `SubSystemIo`, `readPool`, `readStaging`, `write` |
| [ConflictResolutionSubSystem.java](ConflictResolutionSubSystem.java) | The port that orders sub-systems whose writes overlap | `ConflictResolutionSubSystem`, `resolveOrder` |
| [ClaimFinishBarrier.java](ClaimFinishBarrier.java) | Counts claims and finishes in a step, and refuses merge when they differ | `ClaimFinishBarrier`, `requireBalanced` |
| [ClaimFinishSnapshot.java](ClaimFinishSnapshot.java) | The counts of a completed step | `ClaimFinishSnapshot`, `isBalanced` |
