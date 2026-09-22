<!--
  File: docs/architecture/host/systems.md
  Purpose: EngineSystem, sub-system order, claim/finish barrier
  Audience: Agents and humans
  Update when: EngineSystem.run or the barrier changes
-->

# Systems

A system is an `EngineSystem`: an id, one category, an ordered list of `SubSystem`s, and an optional conflict resolver. It runs only when it claimed at least one event. It returns `OUT_SYS`, a map from field name to value.

## What it reads

The `PoolSnapshot` taken after compute and before merge. Every system in the step receives that same snapshot. A sub-system also reads the staging map written by earlier sub-systems in the same system.

## What it writes

Staging writes inside the system, then one `OUT_SYS` map. The engine copies each entry into the step output buffer as a `ProvenancedWrite(systemId, value)`. The system does not write the Pool.

## Procedure

`EngineSystem.run` orders sub-systems, then executes them. Each `SubSystem` declares `writeRanges`. Two sub-systems overlap when they share a field name.

Disjoint ranges keep registration order. The outcome of disjoint writers does not depend on that order, because they do not share fields. Overlapping ranges require a `ConflictResolutionSubSystem`. `resolveOrder` must return each conflicting sub-system once. The engine splices that order in at the first conflicting member and leaves the non-conflicting members in registration order around it. A missing resolver, or a resolver that drops or duplicates a member, throws.

Each sub-system receives a `SubSystemIo`: the pool snapshot, the staging map so far, and its own write ranges. One sub-system's staging output is visible to the next. That is the only chaining in the step. Systems do not chain. No system reads another system's `OUT_SYS` in the same step.

The claim/finish barrier counts systems, not sub-systems. `onClaimed` runs before `run`. `onFinished` runs after. `requireBalanced` throws when `claimCount != finishCount`. Today the loop is synchronous: a system that has started returns before the next system starts, so the counts match unless `run` throws. A policy for a system that does not finish is not implemented. That gap is [open question 1](open-questions.md).

## What is true afterwards

`OUT_SYS` holds the last staging value per field. Overlapping sub-systems have run in the resolver's order. The Pool is unchanged until [merge](merge.md).

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| System | `EngineSystem` | `engine/.../system/EngineSystem.java` |
| Wiring | `SystemConfig` | `engine/.../system/SystemConfig.java` |
| Block | `SubSystem` | `engine/.../system/SubSystem.java` |
| Resolver | `ConflictResolutionSubSystem` | `engine/.../system/ConflictResolutionSubSystem.java` |
| Barrier | `ClaimFinishBarrier` | `engine/.../system/ClaimFinishBarrier.java` |

Parent: [one engine step](README.md).
