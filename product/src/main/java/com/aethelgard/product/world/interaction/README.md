<!--
  File: product/src/main/java/com/aethelgard/product/world/interaction/README.md
  Purpose: Door to the interaction: turning the contacts into per-plate area budgets and preferred velocity changes
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Interaction

Turns the classified contacts into what each plate should do this generation: how many cells it gains or loses, and which way its velocity should lean.

**Paper:** [Interaction](../../../../../../../../../docs/architecture/world/interaction.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

Two later phases need the same verdict on every contact — who loses a collision, and how much area each plate trades — so it is computed once, in its own phase, and stored as two tables. The geometry pass spends the budgets and the integration spends the intents; neither decides them. The rule for which crust loses a collision is shared with the crust phases, and lives in `crust/`.

## How it works

1. The phase `BoundaryInteraction` reads the contacts (the staged ones first), the registry, the occupancy, and the lockers, and stages the two tables.
2. `AreaFlux.from` folds the contacts into a budget per plate and a sink count. When the crust is given, each collision asks `CrustPrecedence.collideLoser` which side sinks; `NONE`, two continents meeting, sinks neither. Without the crust, the area rule `AreaFlux.loser` decides, and precedence itself falls back to it when two oceans meet.
3. `MotionIntent.from` folds the same contacts into a preferred change of velocity per plate, with the same loser rule.
4. The tables are read with `deltaArea`, `sinkDelta`, and `netCells`, and with `ix` and `iy`.

**Start reading at:** `AreaFlux.from` in [AreaFlux.java](AreaFlux.java).

## Depends on

- [boundaries/](../boundaries/README.md) — the contacts
- [fields/](../fields/README.md) — the registry, the occupancy, the lockers, and the field names
- [crust/](../crust/README.md) — `CrustPrecedence`, which crust loses a collision; the crust and interaction phases are peers ([conventions](../../../../../../../../../docs/architecture/conventions.md))
- [engine system](../../../../../../../../../engine/src/main/java/com/aethelgard/engine/system/README.md) — the `SubSystem` port the phase implements

## Used by

- [world/](../README.md) — the phase in the setup, and the budgets and intents of step 0 and of the reference pipeline
- [motion/](../motion/README.md) — the geometry pass spends the budgets, and the integration follows the intents
- [crust/](../crust/README.md) — precedence and collision read the area rule
- [session/](../../session/README.md) — the session's reads and the world dump
- [cli](../../../../../../../../../cli/README.md) — the command language prints the budgets and intents

## Where each step happens

### [Interaction](../../../../../../../../../docs/architecture/world/interaction.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads the contacts, registry, occupancy, and lockers, and stages both tables | `BoundaryInteraction.execute` | [BoundaryInteraction.java](BoundaryInteraction.java) |
| 2. The contacts are folded into budgets and a sink count | `AreaFlux.from` | [AreaFlux.java](AreaFlux.java) |
| 3. The area rule picks the loser and the winner | `AreaFlux.loser` | [AreaFlux.java](AreaFlux.java) |
| 4. The contacts are folded into intents | `MotionIntent.from` | [MotionIntent.java](MotionIntent.java) |
| 5. The tables are read | `AreaFlux.netCells`, `MotionIntent.ix` | [AreaFlux.java](AreaFlux.java), [MotionIntent.java](MotionIntent.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [BoundaryInteraction.java](BoundaryInteraction.java) | The phase that computes the budgets and intents every generation | `BoundaryInteraction`, `execute` |
| [AreaFlux.java](AreaFlux.java) | The area budget of every plate, and the area rule for a collision's loser | `AreaFlux`, `from`, `loser`, `winner`, `deltaArea`, `netCells` |
| [MotionIntent.java](MotionIntent.java) | The preferred change of velocity of every plate | `MotionIntent`, `from`, `ix`, `iy` |
