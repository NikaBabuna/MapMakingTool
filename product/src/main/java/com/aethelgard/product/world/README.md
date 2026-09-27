<!--
  File: product/src/main/java/com/aethelgard/product/world/README.md
  Purpose: Door to the wiring of the world into the engine: its category, its tick, the nine phases of a generation, and the reference pipeline
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# World

Wires the world into the engine: the product's event category, the tick that fires it, the nine phases of one generation in their order, step 0, and a reference pipeline that runs the same generation without the engine.

**Paper:** [World](../../../../../../../../docs/architecture/world/README.md) · [Wiring](../../../../../../../../docs/architecture/world/wiring.md) · [Seed](../../../../../../../../docs/architecture/world/seed.md) · [Reference pipeline](../../../../../../../../docs/architecture/world/reference.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

This package is the only place that knows the whole generation at once: it assembles the phases that live in the packages below it and hands them to the engine in order. Keeping the order here lets each phase package know nothing of the phases before or after it. A new phase is written in the package of its mechanism and only registered here; the values a phase reads and writes belong in `fields/`, not here.

## How it works

1. `ProductHost.create` builds step 0 from a `WorldSpec`: the flat elevation, the seeded plates, the occupancy, the locker table, and the velocities (all from [fields/](fields/README.md)), then counts the registry, traces the first contacts, computes the first budgets and intents, and creates the engine with that setup and those nine values as field seeds.
2. `ProductHost.setup` builds the category tree from `ProductCategories.tree` and one `EngineSystem` named by `TECTONICS_SYSTEM_ID`, holding the nine phases in the order they run: `BoundaryTracing`, `BoundaryInteraction`, `VelocityIntegration`, `GeometryApplication`, `Orogeny`, `RidgeCreation`, `MarginRelief`, `ContinentalCollision`, `Isostasy`. Six of them are wrapped in a `TimingSubSystem`, so their wall time is recorded. It also sets the conflict order of the phases and declares the nine fields `STATIC`.
3. `GenerationTickPolicy.emitEvents` fires the tectonics category on every update from the second on, so the nine phases run once per step after step 0.
4. `ProductGeneration.advance` runs the same nine phases directly on plain values, without the engine, and returns a `Snapshot`; the tests hold the engine's generation to it.

**Start reading at:** `ProductHost.setup` in [ProductHost.java](ProductHost.java).

## Depends on

- [fields/](fields/README.md) — the world's values, `WorldSpec`, and the step-0 seeds
- [boundaries/](boundaries/README.md) — the tracing phase, and the contacts of step 0
- [interaction/](interaction/README.md) — the interaction phase, and the budgets and intents of step 0
- [motion/](motion/README.md) — the integration and geometry phases
- [crust/](crust/README.md) — the orogeny, ridge, margin, collision, and isostasy phases
- [session/diagnostics/](../session/diagnostics/README.md) — `TimingSubSystem` and the phase-timing ids of `DiagnosticIds`
- [engine event](../../../../../../../../engine/src/main/java/com/aethelgard/engine/event/README.md) — the category tree
- [engine pool](../../../../../../../../engine/src/main/java/com/aethelgard/engine/pool/README.md) — the engine, its setup and configuration, and the emission policy
- [engine system](../../../../../../../../engine/src/main/java/com/aethelgard/engine/system/README.md) — `EngineSystem` and the `SubSystem` port each phase implements
- [engine merge](../../../../../../../../engine/src/main/java/com/aethelgard/engine/merge/README.md) — the field schema

## Used by

- [session/](../session/README.md) — `ProductSession` creates its engine with `ProductHost.create`
- [the world tests](../../../../../../test/java/com/aethelgard/product/world/README.md) — step 0, a whole generation, and the reference pipeline

## Where each step happens

### [Wiring](../../../../../../../../docs/architecture/world/wiring.md)

| Step | Member | File |
|------|--------|------|
| 1. The category tree holds the tectonics path and its parent | `ProductCategories.tree` | [ProductCategories.java](ProductCategories.java) |
| 2. The nine phases are built, and six are timed | `ProductHost.setup` | [ProductHost.java](ProductHost.java) |
| 3. One system holds the phases in order, with their conflict order | `ProductHost.setup` | [ProductHost.java](ProductHost.java) |
| 4. The nine fields are declared, and the tick is set as the emission policy | `ProductHost.setup` | [ProductHost.java](ProductHost.java) |
| 5. The tick fires the tectonics category from the second update on | `GenerationTickPolicy.emitEvents` | [GenerationTickPolicy.java](GenerationTickPolicy.java) |

### [Seed](../../../../../../../../docs/architecture/world/seed.md)

| Step | Member | File |
|------|--------|------|
| 1. A null spec is refused, and every step-0 value is built | `ProductHost.create` | [ProductHost.java](ProductHost.java) |
| 7. The registry is counted, the contacts traced, and the budgets and intents computed | `ProductHost.create` | [ProductHost.java](ProductHost.java) |
| 8. The engine is created with the nine values as field seeds, and runs step 0 | `ProductHost.create` | [ProductHost.java](ProductHost.java) |

### [Reference pipeline](../../../../../../../../docs/architecture/world/reference.md)

| Step | Member | File |
|------|--------|------|
| 1. The registry is counted from the given plates and velocities | `ProductGeneration.advance` | [ProductGeneration.java](ProductGeneration.java) |
| 2. Contacts, budgets, and intents are computed, and the velocities integrated | `ProductGeneration.advance` | [ProductGeneration.java](ProductGeneration.java) |
| 3. The geometry pass runs, and the orogeny stamps are computed | `ProductGeneration.advance` | [ProductGeneration.java](ProductGeneration.java) |
| 4. Plates and crust keys are advected, with the subduction corrections | `ProductGeneration.advance` | [ProductGeneration.java](ProductGeneration.java) |
| 5. Ridge, margin, collision, the moved registry, and isostasy follow | `ProductGeneration.advance` | [ProductGeneration.java](ProductGeneration.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [ProductHost.java](ProductHost.java) | Builds step 0 and the engine setup: the nine phases, the fields, and the tick | `ProductHost`, `create`, `setup`, `TECTONICS_SYSTEM_ID` |
| [ProductCategories.java](ProductCategories.java) | The product's event category tree | `ProductCategories`, `TECTONICS`, `tree` |
| [GenerationTickPolicy.java](GenerationTickPolicy.java) | Fires the tectonics category on every step after step 0 | `GenerationTickPolicy`, `INSTANCE`, `emitEvents` |
| [ProductGeneration.java](ProductGeneration.java) | Runs one generation's phases directly on plain values, as a reference | `ProductGeneration`, `advance`, `Snapshot` |
| [fields/](fields/README.md) | The world's values: its size, its grids and tables, and how step 0 seeds them | — |
| [topology/](topology/README.md) | How the rectangular map joins as a sphere | — |
| [boundaries/](boundaries/README.md) | Finds and classifies every contact between two plates | — |
| [interaction/](interaction/README.md) | Turns the contacts into area budgets and preferred velocity changes | — |
| [motion/](motion/README.md) | Moves the plates: velocities, sinking and claiming, flooding, splitting, and advection | — |
| [crust/](crust/README.md) | Changes the crust: thickening, new ocean, margins, arcs and sutures, subduction, and height | — |
