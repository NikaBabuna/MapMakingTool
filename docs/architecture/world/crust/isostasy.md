<!--
  File: docs/architecture/world/crust/isostasy.md
  Purpose: Isostasy — phase 9: height is read from crust thickness, and nothing else writes height
  Audience: Agents and humans
  Update when: What isostasy computes changes
-->

# Isostasy

Height is never painted on. Thick crust floats high and thin crust floats low, so the height of every cell is read from the thickness of the crust column under it, at the end of every generation.

## What it reads

The staged `occupancy` of the ridge and the staged `lockers` of the collision (the settled values when none were staged).

## What it writes

`elevation`, a new grid. It is the only phase with `elevation` in its write range. A cell whose locker id is outside the table throws `IllegalArgumentException`; a field of the wrong type throws `IllegalStateException`.

## Model

$$E(c) = T\bigl(O(c)\bigr) - T_{\mathrm{ocean}} \qquad \text{for every } c \in \Omega,$$

so fresh ocean ($T = 8$) stands at 0, the land threshold ($T = 16$) at 8, and the collision cap ($T = 32$) at 24. Thinned crust stands below 0.

## Procedure

1. The phase reads the occupancy and lockers (staged first) and stages the new elevation.
2. The height of every cell is computed from the thickness of its locker.

## What is true afterwards

Invariant I5 of [fields](../fields.md) holds: the settled elevation agrees with the settled occupancy and lockers, cell for cell. Two cells that share a locker have the same height. The [studio](../../studio/raster.md) paints this grid.

## Cost

$O(WH)$.

Code: [world/crust/](../../../../product/src/main/java/com/aethelgard/product/world/crust/README.md)  
Parent: [crust](README.md). Why height is read from thickness: [ADR-013](../../../paperwork/decisions/ADR-013-crust-topology.md).
