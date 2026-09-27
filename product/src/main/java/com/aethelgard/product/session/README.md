<!--
  File: product/src/main/java/com/aethelgard/product/session/README.md
  Purpose: Door to the session: the owner of one running world, its reads, and the text dump of a settled world
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Session

Owns one running world: creates its engine, advances it one caller at a time, answers reads of its settled values, and prints it as canonical text.

**Paper:** [Session](../../../../../../../../docs/architecture/session/README.md) · [Run](../../../../../../../../docs/architecture/session/run.md) · [Dump](../../../../../../../../docs/architecture/session/dump.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The command line and the studio both drive a world, and neither should know how the engine is wired. The session is the one object they hold: it serialises the steps and the reads under one lock, so a reader never sees half a step. How the world is built belongs to `world/`; what is measured while it runs belongs to `diagnostics/`.

## How it works

1. `new ProductSession(spec)`, or `ProductSession.ofDefault`, keeps the spec and creates the engine with `ProductHost.create`, which runs step 0, and builds a `DiagnosticsHub` with its default collectors.
2. `advance` takes the lock and runs the given number of single steps. Each step runs under `PhaseTiming.withHub`, so the timed phases record into the hub; the step's wall time and the heap in use are recorded after it.
3. The reads — `elevation`, `plates`, `plateVelocities`, `plateRegistry`, `boundaries`, `areaFlux`, `motionIntent`, and `field` — take the lock and return a settled value. `fieldNames`, `schemaTypes`, `systemIds`, and `systemDetail` describe the wiring.
4. `settledWorld` returns the text of `WorldDump.of`: a header, three grids, and the per-plate tables, the same for the same fields.

**Start reading at:** `ProductSession.advance` in [ProductSession.java](ProductSession.java).

## Depends on

- [world/](../world/README.md) — `ProductHost.create`
- [world/fields/](../world/fields/README.md) — the values the reads return
- [world/boundaries/](../world/boundaries/README.md), [world/interaction/](../world/interaction/README.md) — the contacts, budgets, and intents the reads and the dump return
- [diagnostics/](diagnostics/README.md) — the hub and the phase timing
- [engine pool](../../../../../../../../engine/src/main/java/com/aethelgard/engine/pool/README.md) — the engine it drives
- [engine merge](../../../../../../../../engine/src/main/java/com/aethelgard/engine/merge/README.md), [engine systems](../../../../../../../../engine/src/main/java/com/aethelgard/engine/systems/README.md) — the schema and systems its construction reads describe

## Used by

- [cli](../../../../../../../../cli/README.md) — the runner and the command language drive a session
- [ui](../../../../../../../../ui/README.md) — the map controller and the map host drive a session
- [the session tests](../../../../../../test/java/com/aethelgard/product/session/README.md) — ownership, advancing, and the dump

## Where each step happens

### [Run](../../../../../../../../docs/architecture/session/run.md)

| Step | Member | File |
|------|--------|------|
| 1. The engine is created, running step 0, and the hub is built | `ProductSession` | [ProductSession.java](ProductSession.java) |
| 2. Steps run one at a time under the lock, timed | `ProductSession.advance` | [ProductSession.java](ProductSession.java) |
| 3. The reads take the lock and return a settled value | `ProductSession.field` | [ProductSession.java](ProductSession.java) |
| 4. The settled world is returned as text | `ProductSession.settledWorld` | [ProductSession.java](ProductSession.java) |
| 5. The construction reads describe the wiring | `ProductSession.systemDetail` | [ProductSession.java](ProductSession.java) |
| 6. The spec and the hub are returned without the lock | `ProductSession.diagnostics` | [ProductSession.java](ProductSession.java) |

### [Dump](../../../../../../../../docs/architecture/session/dump.md)

| Step | Member | File |
|------|--------|------|
| 1. The nine settled fields and the step index are read | `WorldDump.of` | [WorldDump.java](WorldDump.java) |
| 2. The parts are checked and written: header, grids, tables | `WorldDump.format`, `WorldDump.appendGrid` | [WorldDump.java](WorldDump.java) |
| 3. The shorter forms fill in what they are not given | `WorldDump.format` | [WorldDump.java](WorldDump.java) |
| 4. A grid of another size than the spec is refused | `WorldDump.requireGeometry` | [WorldDump.java](WorldDump.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [ProductSession.java](ProductSession.java) | Owns one running world, advances it, and answers reads under one lock | `ProductSession`, `advance`, `field`, `settledWorld`, `diagnostics` |
| [WorldDump.java](WorldDump.java) | Prints a settled world as canonical text | `WorldDump`, `of`, `format`, `CANONICAL_STEPS` |
| [diagnostics/](diagnostics/README.md) | Measures a running session without changing it | — |
