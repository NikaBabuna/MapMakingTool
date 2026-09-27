<!--
  File: product/src/main/java/com/aethelgard/product/world/crust/README.md
  Purpose: Door to the crust: the phases that thicken, create, shape, and lift the crust, the precedence rule, and subduction
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Crust

Changes the crust that rides on the plates: thickens it at contacts, mints new ocean in gaps, shapes the ocean floor near margins, raises arcs and sutures, sinks ocean under continents, and reads the height of the land from the thickness.

**Paper:** [Crust](../../../../../../../../../docs/architecture/world/crust/README.md) · [Precedence](../../../../../../../../../docs/architecture/world/crust/precedence.md) · [Subduct](../../../../../../../../../docs/architecture/world/crust/subduct.md) · [Orogeny](../../../../../../../../../docs/architecture/world/crust/orogeny.md) · [Ridge](../../../../../../../../../docs/architecture/world/crust/ridge.md) · [Margin](../../../../../../../../../docs/architecture/world/crust/margin.md) · [Collide](../../../../../../../../../docs/architecture/world/crust/collide.md) · [Isostasy](../../../../../../../../../docs/architecture/world/crust/isostasy.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

The crust is a separate material from the plates: plates own cells, and crust columns, keyed by occupancy and measured in the locker table, ride with them. Every rule that changes a column's thickness or key lives here, in the crust chapter of the paper, so height always has one cause. Which plate owns a cell is decided in `motion/`, and the values themselves live in `fields/`.

## How it works

The five crust phases run after the geometry pass, in this order:

1. `Orogeny` ranks every contact cell (rift, loser, winner, pass-by) with `ranks` and adds the matching thickness change to the column under it with `applyToLockers`.
2. `RidgeCreation` gives every cell left without a column a new thin oceanic column, in row-major order, and returns both parts in a `Result`.
3. `MarginRelief` measures each oceanic column's distance to the nearest rift and collision with `distances`, and shapes the floor: a trough at rifts, a slope toward collisions, and a lip where `selectsLip` picks it.
4. `ContinentalCollision` raises an arc where two oceans meet and thickens both sides of a suture where two continents meet, up to `CAP`.
5. `Isostasy` reads the height of every cell from the thickness of its column.

Two rules are called from other phases. `CrustPrecedence` decides which side of a collision sinks: continent beats ocean, two continents sink neither (`NONE`), and two oceans leave it to the area rule of `interaction/`. `Subduction.correct` runs inside advection: the losing ocean takes the surviving column, and the extra copies of a rift's column become unresolved, so `RidgeCreation` mints fresh ocean there.

**Start reading at:** `Orogeny.execute` in [Orogeny.java](Orogeny.java).

## Depends on

- [fields/](../fields/README.md) — occupancy, lockers, plates, registry, velocities, and the field names
- [boundaries/](../boundaries/README.md) — the contacts
- [topology/](../topology/README.md) — neighbours on the sphere
- [interaction/](../interaction/README.md) — the area rule; the crust and interaction phases are peers ([conventions](../../../../../../../../../docs/architecture/conventions.md))
- [motion/](../motion/README.md) — `PlateKinematics.UNRESOLVED`, the mark of a gap; the crust and motion phases are peers
- [engine systems](../../../../../../../../../engine/src/main/java/com/aethelgard/engine/systems/README.md) — the `SubSystem` port the phases implement

## Used by

- [world/](../README.md) — the five phases in the setup, and the reference pipeline
- [boundaries/](../boundaries/README.md) — `Orogeny.closing` classifies each contact
- [interaction/](../interaction/README.md) — `CrustPrecedence` picks a collision's loser
- [motion/](../motion/README.md) — `CrustPrecedence` in the geometry pass, and `Subduction` inside advection
- [the crust tests](../../../../../../../test/java/com/aethelgard/product/world/crust/README.md) — riding, rifts, sinking, arcs and sutures, margins, and height

## Where each step happens

### [Precedence](../../../../../../../../../docs/architecture/world/crust/precedence.md)

| Step | Member | File |
|------|--------|------|
| 1. A column, or the column under a cell, is tested against the land threshold | `CrustPrecedence.isContinental`, `CrustPrecedence.isContinentalCell` | [CrustPrecedence.java](CrustPrecedence.java) |
| 2. Both cells of a collision are tested, and the loser returned | `CrustPrecedence.collideLoser` | [CrustPrecedence.java](CrustPrecedence.java) |

### [Subduct](../../../../../../../../../docs/architecture/world/crust/subduct.md)

| Step | Member | File |
|------|--------|------|
| 1. The arguments are checked, and the two corrections run in order | `Subduction.correct` | [Subduction.java](Subduction.java) |
| 2. At each collision, the losing ocean takes the surviving column | `Subduction.consumeCollide` | [Subduction.java](Subduction.java) |
| 3. At each rift, extra copies of a column become unresolved | `Subduction.unshareSeparate`, `Subduction.rememberKeep` | [Subduction.java](Subduction.java) |

### [Orogeny](../../../../../../../../../docs/architecture/world/crust/orogeny.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads the occupancy, lockers, registry, and contacts | `Orogeny.execute` | [Orogeny.java](Orogeny.java) |
| 2. Every contact cell gets its highest rank | `Orogeny.ranks` | [Orogeny.java](Orogeny.java) |
| 3. The rank's thickness change is added to the column under each cell | `Orogeny.applyToLockers` | [Orogeny.java](Orogeny.java) |
| 4. The grid forms stamp a grid of numbers instead of columns | `Orogeny.apply` | [Orogeny.java](Orogeny.java) |

### [Ridge](../../../../../../../../../docs/architecture/world/crust/ridge.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads the occupancy and lockers, and stages both parts | `RidgeCreation.execute` | [RidgeCreation.java](RidgeCreation.java) |
| 2. The gaps are counted; with none, nothing changes | `RidgeCreation.apply` | [RidgeCreation.java](RidgeCreation.java) |
| 3. Every gap gets the next column id, as thin ocean | `RidgeCreation.apply` | [RidgeCreation.java](RidgeCreation.java) |

### [Margin](../../../../../../../../../docs/architecture/world/crust/margin.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads the occupancy, lockers, plates, and velocities | `MarginRelief.execute` | [MarginRelief.java](MarginRelief.java) |
| 2. Distances grow from the contacts of one kind | `MarginRelief.distances` | [MarginRelief.java](MarginRelief.java) |
| 3. Each column keeps its least rift and collision distance | `MarginRelief.apply` | [MarginRelief.java](MarginRelief.java) |
| 4. Oceanic columns get the trough, the slope, and the lip | `MarginRelief.apply` | [MarginRelief.java](MarginRelief.java) |

### [Collide](../../../../../../../../../docs/architecture/world/crust/collide.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads the occupancy, lockers, plates, and velocities | `ContinentalCollision.execute` | [ContinentalCollision.java](ContinentalCollision.java) |
| 2. The collisions mark suture and arc columns | `ContinentalCollision.apply` | [ContinentalCollision.java](ContinentalCollision.java) |
| 3. Each marked column thickens once, up to the cap | `ContinentalCollision.apply` | [ContinentalCollision.java](ContinentalCollision.java) |

### [Isostasy](../../../../../../../../../docs/architecture/world/crust/isostasy.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads the occupancy and lockers | `Isostasy.execute` | [Isostasy.java](Isostasy.java) |
| 2. The height of every cell is read from its column's thickness | `Isostasy.apply` | [Isostasy.java](Isostasy.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [Orogeny.java](Orogeny.java) | The phase that thickens or thins the crust at contact cells, and the closing speed of two plates | `Orogeny`, `execute`, `applyToLockers`, `closing` |
| [RidgeCreation.java](RidgeCreation.java) | The phase that mints thin oceanic columns in the gaps advection leaves | `RidgeCreation`, `apply`, `Result` |
| [MarginRelief.java](MarginRelief.java) | The phase that shapes the ocean floor near rifts and collisions | `MarginRelief`, `apply`, `trough`, `selectsLip` |
| [ContinentalCollision.java](ContinentalCollision.java) | The phase that raises arcs and thickens sutures | `ContinentalCollision`, `apply`, `ARC`, `SUTURE`, `CAP` |
| [Isostasy.java](Isostasy.java) | The phase that reads height from crust thickness | `Isostasy`, `apply` |
| [CrustPrecedence.java](CrustPrecedence.java) | Which side of a collision sinks, from the crust on each side | `CrustPrecedence`, `collideLoser`, `isContinental`, `NONE` |
| [Subduction.java](Subduction.java) | Corrects the crust keys at collisions and rifts during advection | `Subduction`, `correct` |
