<!--
  File: docs/architecture/studio/raster.md
  Purpose: ElevationRaster packing, ramps, and hillshade
  Audience: Agents and humans
  Update when: ElevationRaster stops or hillshade change
-->

# Raster

`ElevationRaster` turns grids into a flat `int[]`, one `0xRRGGBB` per cell, row-major. Division that scales a channel is integer and truncating. The same grids and the same layer produce the same ints.

## What it reads

`elevation`, and `plates` when the layer needs them. `MapLayer` selects the picture.

## What it writes

The packed buffer. `paint` reuses the caller's `int[]` when the length still matches `width * height`. It does not write the Pool.

## Procedure

`CLAMP` is 64. `OCEAN_FLOOR` is −64.

Ocean (`elevation < 0`) interpolates `OCEAN_STOP_E` at −64, −32, −16, −8, −1. The RGB stops run from `(24, 64, 104)` to `(110, 190, 226)`. The shallow swatch `OCEAN_RGB` is the color at −1. Ocean cells are not hillshaded.

Land (`elevation >= 0`) interpolates `LAND_STOP_E` at 0, 12, 24, 40, 64. The RGB stops run from `(150, 196, 120)` to `(255, 250, 236)`. Values above 64 stay on the last stop.

Hillshade applies to land on the elevation layer and on overlay land. It reads the toroidal west neighbor and the north neighbor. Flat land (`dw = dn = 0`) keeps the ramp. The shade factor is clamped between `HILLSHADE_MIN` (6) and `HILLSHADE_MAX` (18), with flat at `HILLSHADE_FLAT` (12).

The plates layer paints interiors with `PLATE_INTERIOR_RGB` `(88, 92, 96)` and a darker stroke `PLATE_BOUNDARY_RGB` `(36, 38, 42)` where a neighbor's plate id differs. Overlay darkens that stroke on top of the elevation picture.

## What is true afterwards

The buffer length is `width * height`. A second paint of the same grids into a reused buffer overwrites those ints and does not allocate a new array when the size matches.

## Where it lives

`ElevationRaster` and `MapLayer` in `ui/src/main/java/com/aethelgard/ui/`.

Parent: [studio](README.md). The grid it paints: [../world/crust/isostasy.md](../world/crust/isostasy.md).
