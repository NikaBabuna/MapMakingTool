<!--
  File: product/src/main/java/com/aethelgard/product/world/fields/README.md
  Purpose: Door to the world's values: its size, its grids and per-plate tables, the names of its fields, and how step 0 seeds them
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Fields

Holds the world's values — its size, its grids and per-plate tables, the names under which the engine stores them — and the functions that give them their step-0 values.

**Paper:** [Fields](../../../../../../../../../docs/architecture/world/fields.md) · [Seed](../../../../../../../../../docs/architecture/world/seed.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

Every phase of a generation reads and writes the same few kinds of value, so they live in one package that depends on nothing else in the product. This is the lowest layer of the world: the phase packages call down into it, and it never calls up into them. A value that only one phase uses stays in that phase's package; the seed of step 0 lives here because it is the first value of these same tables.

## How it works

`WorldSpec` fixes the width, the height, and the seed of a run; `DEFAULT` is the 8 by 8 world of the dumps and `VIEW` the full map. `WorldFields` names the nine fields the engine stores. The values are immutable: a `Grid` holds one integer per cell, `PlateVelocities` and `PlateRegistry` hold one velocity and one area per plate, `Occupancy` gives each cell the key of the crust column under it, and `Lockers` holds the thickness of every column, with the oceanic thickness `T_OCEAN` and the land threshold `T_LAND`.

Step 0 is seeded here: `Plates.seed` places one site per plate from the seed and gives every cell to its nearest site; `Occupancy.seed` keys each cell to its own column; `Lockers.oceanic` makes every column thin ocean; `PlateVelocities.seed` draws each plate's velocity from the same seed; and `PlateRegistry.from` counts each plate's cells.

**Start reading at:** `WorldFields` in [WorldFields.java](WorldFields.java).

## Depends on

- nothing in this repository

## Used by

- [world/](../README.md) — step 0, the setup, and the reference pipeline
- [boundaries/](../boundaries/README.md), [interaction/](../interaction/README.md), [motion/](../motion/README.md), [crust/](../crust/README.md) — every phase reads and writes these values
- [session/](../../session/README.md) — the session's reads and the world dump
- [cli](../../../../../../../../../cli/README.md) — the command language prints and inspects these values
- [ui](../../../../../../../../../ui/README.md) — the studio paints and inspects these values

## Where each step happens

### [Seed](../../../../../../../../../docs/architecture/world/seed.md)

| Step | Member | File |
|------|--------|------|
| 2. The elevation is the all-zero grid | `Grid.zeros` | [Grid.java](Grid.java) |
| 3. One site per plate is placed from the seed | `Plates.seed` | [Plates.java](Plates.java) |
| 4. Every cell goes to its nearest site, the lowest index on a tie | `Plates.assign` | [Plates.java](Plates.java) |
| 5. Each cell keys its own crust column, and every column is thin ocean | `Occupancy.seed` | [Occupancy.java](Occupancy.java) |
| 6. Each plate's velocity is drawn from the seed | `PlateVelocities.seed` | [PlateVelocities.java](PlateVelocities.java) |

### [Fields](../../../../../../../../../docs/architecture/world/fields.md)

| Step | Member | File |
|------|--------|------|
| 1. A run is sized by a spec | `WorldSpec` | [WorldSpec.java](WorldSpec.java) |
| 2. The nine field names | `WorldFields` | [WorldFields.java](WorldFields.java) |
| 3. A grid holds one number per cell | `Grid` | [Grid.java](Grid.java) |
| 4. Per-plate velocities | `PlateVelocities` | [PlateVelocities.java](PlateVelocities.java) |
| 5. The per-plate registry of areas and velocities | `PlateRegistry.from` | [PlateRegistry.java](PlateRegistry.java) |
| 6. The key of a cell's crust column | `Occupancy.id` | [Occupancy.java](Occupancy.java) |
| 7. The thickness of every crust column | `Lockers` | [Lockers.java](Lockers.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [WorldSpec.java](WorldSpec.java) | The size and the seed of a run | `WorldSpec`, `DEFAULT`, `VIEW` |
| [WorldFields.java](WorldFields.java) | The names of the nine fields the engine stores | `WorldFields`, `PLATES`, `LOCKERS` |
| [Grid.java](Grid.java) | An immutable grid of one number per cell | `Grid`, `get`, `zeros` |
| [Plates.java](Plates.java) | Seeds the plates of step 0: one site per plate, each cell to its nearest site | `Plates`, `seed`, `assign`, `mix` |
| [PlateVelocities.java](PlateVelocities.java) | The velocity of every plate, and its step-0 seed | `PlateVelocities`, `seed`, `vx`, `vy`, `hasMovingPlate` |
| [PlateRegistry.java](PlateRegistry.java) | The area and velocity of every plate | `PlateRegistry`, `from`, `area`, `withVelocities` |
| [Occupancy.java](Occupancy.java) | The key of the crust column under each cell | `Occupancy`, `id`, `seed` |
| [Lockers.java](Lockers.java) | The thickness of every crust column | `Lockers`, `oceanic`, `thickness`, `T_OCEAN`, `T_LAND` |
