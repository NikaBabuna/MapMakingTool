<!--
  File: docs/architecture/world/crust/isostasy.md
  Purpose: ThicknessToElevation
  Audience: Agents and humans
  Update when: The isostasy formula changes
-->

# Isostasy

`ThicknessToElevation` is the last tectonics sub-system and the only writer of `elevation`. Height is a reading of locker thickness at the occupancy key. It is not stored as an independent history of where plates used to meet.

## What it reads

Staged or pool `occupancy`, and `lockers`.

## What it writes

`elevation`, a `Grid` of the same width and height.

## Procedure

For each cell, `elevation = lockers.thickness(occupancy) − Lockers.T_OCEAN`. `T_OCEAN` is 8. The subtraction is integer. There is no extra slope in this formula. Troughs and caps are already in the thickness.

## What is true afterwards

A locker at thickness 8 has elevation 0. Land, thickness at least 16, has elevation at least 8. A locker at the cap of 32 has elevation 24. Thickness below 8, including a negative stamp from orogeny, has negative elevation. The same occupancy and the same lockers produce the same grid.

## Where it lives

`ThicknessToElevation` in `product/src/main/java/com/aethelgard/product/ThicknessToElevation.java`. Sub-system id `isostasy`.

Parent: [crust](README.md). How the grid becomes pixels: [../../studio/raster.md](../../studio/raster.md).
