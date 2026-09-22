<!--
  File: docs/architecture/world/crust/collide.md
  Purpose: ContinentalCollide arc and suture
  Audience: Agents and humans
  Update when: ContinentalCollide.ARC, SUTURE, or CAP changes
-->

# Continental collide

`ContinentalCollide` thickens lockers at ocean-ocean and continent-continent contacts. It does not consume a plate. Plate consumption already happened in [motion](../motion.md) and [subduct](subduct.md).

## What it reads

Staged or pool `occupancy`, `lockers`, `plates`, and `plate_velocity`. It retraces contacts from those plates and snapshots thickness before any bump, so both sides of one contact see the pre-bump values.

## What it writes

`lockers`.

## Procedure

| Constant | Value | Meaning |
|----------|-------|---------|
| `ARC` | 8 | Added to the ocean-ocean winner. |
| `SUTURE` | 4 | Added once to each side of a continent-continent contact. |
| `CAP` | 32 | Thickness does not rise above this. A locker already at 32 is left alone. |

A locker is continental when its pre-bump thickness is `>= 16`.

Ocean meets ocean: `AreaFlux.winner` (the plate that is not the smaller-area loser; the lower id loses a tie) gains 8. If it is still under 16, it is raised to 16. One such meeting can make land.

Continent meets continent: each side gains 4, then the cap. Neither side is removed. The plates are not welded into one plate.

Ocean meets continent: this phase adds neither arc nor suture. The ocean already lost under [precedence](precedence.md).

## What is true afterwards

An ocean-ocean winner is at least 16 and at most 32. A suture has thickened both sides by 4 unless the cap stopped it. Pass-by and one-sided ocean-continent contacts are unchanged here.

## Where it lives

`ContinentalCollide` in `product/src/main/java/com/aethelgard/product/ContinentalCollide.java`.

Parent: [crust](README.md).
