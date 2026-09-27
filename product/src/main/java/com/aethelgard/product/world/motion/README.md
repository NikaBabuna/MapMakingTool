<!--
  File: product/src/main/java/com/aethelgard/product/world/motion/README.md
  Purpose: Door to plate motion: velocity integration, the geometry pass that sinks, claims, floods, splits, and renumbers plates, and advection
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Motion

Moves the plates each generation: nudges their velocities toward their intents, reshapes them along their contacts, and carries every cell and crust key forward by its plate's velocity.

**Paper:** [Motion](../../../../../../../../../docs/architecture/world/motion/README.md) · [Integrate](../../../../../../../../../docs/architecture/world/motion/integrate.md) · [Sink and claim](../../../../../../../../../docs/architecture/world/motion/sink.md) · [Flood](../../../../../../../../../docs/architecture/world/motion/flood.md) · [Fission](../../../../../../../../../docs/architecture/world/motion/fission.md) · [Advect](../../../../../../../../../docs/architecture/world/motion/advect.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

Everything that changes which plate owns a cell happens here, in the motion chapter of the paper, so a question about where a plate went is answered in one package. The geometry pass is one phase because its stages share one working grid and one skip mask; advection runs inside it so the moved plates and crust keys are staged together. What happens to the crust itself — its thickness, new ocean, margins — belongs to `crust/`.

## How it works

1. The phase `VelocityIntegration` moves each plate's velocity one unit toward its intent, per axis, with `integrate`, and stages the new velocities and a registry that keeps the settled areas.
2. The phase `GeometryApplication` reads the settled plates and occupancy, the staged velocities, registry, contacts, budgets, and lockers. `apply` runs its stages on a copy of the plates, in order: at each collision the loser's contact cell sinks (`applyCollide`); at each rift both plates claim a sunken cell near the contact (`applySeparate`); every cell still sunken floods to the neighbouring plate that touches it most (`floodSink`); a plate in several pieces splits, and tiny pieces are absorbed (`fissionAndCrumbs`, `absorbCrumbs`); plates with no cell are dropped and the rest renumbered (`remapDense`).
3. `GeometryApplication.execute` then calls `PlateKinematics.advect`: every cell moves by its plate's velocity through `SphereTopology.advectCell`, the gaps are filled with `fillUnresolvedFlood`, `Subduction.correct` fixes the crust keys at collisions and rifts, and every plate that crossed a pole turns round. The registry is recounted, and plates, registry, velocities, and occupancy are staged.

`PlateKinematics` also implements a phase of its own, but the setup does not register it; only `advect` runs.

**Start reading at:** `GeometryApplication.execute` in [GeometryApplication.java](GeometryApplication.java).

## Depends on

- [fields/](../fields/README.md) — plates, occupancy, lockers, registry, velocities, and the field names
- [boundaries/](../boundaries/README.md) — the contacts
- [interaction/](../interaction/README.md) — the budgets the geometry pass spends, and the intents the integration follows
- [topology/](../topology/README.md) — neighbours and moves on the sphere
- [crust/](../crust/README.md) — `CrustPrecedence` for the sinking side, and `Subduction` inside advection; the crust and motion phases are peers ([conventions](../../../../../../../../../docs/architecture/conventions.md))
- [engine systems](../../../../../../../../../engine/src/main/java/com/aethelgard/engine/systems/README.md) — the `SubSystem` port the phases implement

## Used by

- [world/](../README.md) — the two phases in the setup, and the reference pipeline
- [crust/](../crust/README.md) — the ridge and subduction read `PlateKinematics.UNRESOLVED`
- [the motion tests](../../../../../../../test/java/com/aethelgard/product/world/motion/README.md) — velocities, rifts, fission, and crumbs

## Where each step happens

### [Integrate](../../../../../../../../../docs/architecture/world/motion/integrate.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads the velocities, the registry, and the intent | `VelocityIntegration.execute` | [VelocityIntegration.java](VelocityIntegration.java) |
| 2. Each velocity moves one unit toward its intent, per axis, and is clamped | `VelocityIntegration.integrate` | [VelocityIntegration.java](VelocityIntegration.java) |
| 3. If no plate moves, plate 0 is set moving | `VelocityIntegration.integrate` | [VelocityIntegration.java](VelocityIntegration.java) |
| 4. The new velocities and the registry are staged | `VelocityIntegration.execute` | [VelocityIntegration.java](VelocityIntegration.java) |

### [Sink and claim](../../../../../../../../../docs/architecture/world/motion/sink.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads its inputs, computes the generation, and creates the skip mask | `GeometryApplication.execute` | [GeometryApplication.java](GeometryApplication.java) |
| 2. The stages run in order on a copy of the plates | `GeometryApplication.apply` | [GeometryApplication.java](GeometryApplication.java) |
| 3. At each collision the loser's contact cell sinks | `GeometryApplication.applyCollide`, `GeometryApplication.nibbleSink` | [GeometryApplication.java](GeometryApplication.java) |
| 4. At each rift both plates claim a sunken cell near the contact | `GeometryApplication.applySeparate`, `GeometryApplication.claimSinkNear`, `GeometryApplication.nibbleClaim` | [GeometryApplication.java](GeometryApplication.java) |
| 5. Every changed cell is marked in the skip mask | `GeometryApplication.markSkip` | [GeometryApplication.java](GeometryApplication.java) |

### [Flood](../../../../../../../../../docs/architecture/world/motion/flood.md)

| Step | Member | File |
|------|--------|------|
| 1. Sweeps fill every empty cell until a sweep fills none | `GeometryApplication.floodSink` | [GeometryApplication.java](GeometryApplication.java) |
| 2. The owner is the neighbouring plate that touches the cell most | `GeometryApplication.pickFloodOwner` | [GeometryApplication.java](GeometryApplication.java) |
| 3. A cell still empty after the sweeps is an error | `GeometryApplication.floodSink` | [GeometryApplication.java](GeometryApplication.java) |

### [Fission, crumbs, and remap](../../../../../../../../../docs/architecture/world/motion/fission.md)

| Step | Member | File |
|------|--------|------|
| 1. A plate in several pieces keeps one, and the others become new plates | `GeometryApplication.fissionAndCrumbs`, `GeometryApplication.floodComponent` | [GeometryApplication.java](GeometryApplication.java) |
| 2. Pieces below the crumb bar join their longest neighbour | `GeometryApplication.absorbCrumbs`, `GeometryApplication.longestNeighbor` | [GeometryApplication.java](GeometryApplication.java) |
| 3. The velocities of the pass travel together | `GeometryApplication.Lifecycle` | [GeometryApplication.java](GeometryApplication.java) |
| 4. Plates with no cell are dropped, and the rest renumbered | `GeometryApplication.remapDense` | [GeometryApplication.java](GeometryApplication.java) |

### [Advect](../../../../../../../../../docs/architecture/world/motion/advect.md)

| Step | Member | File |
|------|--------|------|
| 1. Every cell moves by its plate's velocity | `PlateKinematics.advect` | [PlateKinematics.java](PlateKinematics.java) |
| 2. Cells no plate reached are filled by flood | `PlateKinematics.fillUnresolvedFlood` | [PlateKinematics.java](PlateKinematics.java) |
| 3. The crust keys at collisions and rifts are corrected | `PlateKinematics.advect` | [PlateKinematics.java](PlateKinematics.java) |
| 4. Plates that crossed a pole turn round, and the result is returned | `PlateKinematics.AdvectResult` | [PlateKinematics.java](PlateKinematics.java) |
| 5. The registry is recounted, and the moved world is staged | `GeometryApplication.execute` | [GeometryApplication.java](GeometryApplication.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [VelocityIntegration.java](VelocityIntegration.java) | The phase that nudges each plate's velocity toward its intent | `VelocityIntegration`, `execute`, `integrate` |
| [GeometryApplication.java](GeometryApplication.java) | The phase that sinks, claims, floods, splits, and renumbers plates, then advects them | `GeometryApplication`, `execute`, `apply`, `Result`, `CRUMB_DENOMINATOR` |
| [PlateKinematics.java](PlateKinematics.java) | Advection: moves plates and crust keys, fills gaps, and turns plates at the poles | `PlateKinematics`, `advect`, `AdvectResult`, `UNRESOLVED` |
