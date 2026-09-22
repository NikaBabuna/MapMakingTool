<!--
  File: docs/architecture/world/crust/ridge.md
  Purpose: RidgeCreate mint of thin oceanic lockers
  Audience: Agents and humans
  Update when: RidgeCreate.apply changes
-->

# Ridge

`RidgeCreate` fills occupancy that advection and [subduct](subduct.md) left unresolved. It does not change `plates`. Plate flood has already covered every plate cell.

## What it reads

Staged or pool `occupancy`, and `lockers`.

## What it writes

`occupancy` and `lockers`.

## Procedure

`RidgeCreate.apply` counts cells whose locker id is `PlateKinematics.UNRESOLVED` (`-1`). It appends that many lockers with `Lockers.appendOceanic`, each at thickness `T_ocean` (8). Each unresolved cell receives one new id, in scan order. Cells that already have a locker id are left alone, including cells that merely share a locker with a neighbor.

## What is true afterwards

No occupancy cell is unresolved. New crust in a gap is thickness 8. It does not copy the thickness of the locker that bordered the rift. The plate that owns the cell is unchanged.

## Where it lives

`RidgeCreate` in `product/src/main/java/com/aethelgard/product/RidgeCreate.java`.

Parent: [crust](README.md).
