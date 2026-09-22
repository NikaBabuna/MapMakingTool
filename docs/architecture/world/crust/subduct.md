<!--
  File: docs/architecture/world/crust/subduct.md
  Purpose: Subduct occupancy correction after advection
  Audience: Agents and humans
  Update when: Subduct.correct changes
-->

# Subduct

`Subduct.correct` runs inside `PlateKinematics.advect`, after occupancy has been carried to destination cells and before ridge mint. It is not its own sub-system. It edits the destination occupancy array in place.

## What it reads

Destination occupancy, standing occupancy, standing boundaries, lockers, the registry, post-flux plates, and velocities.

## What it writes

The same destination occupancy array. Lockers are not appended here.

## Procedure

`consumeCollide` walks `COLLIDE` contacts. [Precedence](precedence.md) supplies the loser. `NONE` skips the contact. Otherwise the destination cell that the loser moved into receives the survivor's locker id.

`unshareSeparate` walks `SEPARATE` contacts. A locker that the rift would copy onto more than one destination cell keeps one copy. The extra destinations become `PlateKinematics.UNRESOLVED` (`-1`). [Ridge](ridge.md) mints a new thickness-8 locker for those cells instead of stretching the border locker across the gap.

## What is true afterwards

An oceanic loser does not keep a private locker on top of the plate that overrode it. A rift does not paint the neighboring mountain's locker into the new gap. Continental contacts with no loser are untouched by consume.

## Where it lives

`Subduct` in `product/src/main/java/com/aethelgard/product/Subduct.java`. Called from `PlateKinematics`.

Parent: [crust](README.md). The advection that calls it: [motion](../motion.md).
