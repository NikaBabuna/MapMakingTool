<!--
  File: ui/src/main/java/com/aethelgard/ui/raster/README.md
  Purpose: Door to the studio's raster: grids painted into one packed colour per cell, for each map layer
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Raster

Paints the world's grids into one packed `0xRRGGBB` colour per cell, for the Elevation, Plates, and Overlay layers.

**Paper:** [raster](../../../../../../../../docs/architecture/studio/raster.md) · [controller](../../../../../../../../docs/architecture/studio/controller.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

Painting is a pure function of two grids and a layer, so it lives apart from the controller that decides when to paint and from the host that sends the result: a test can check every colour without a session or a server. The layer choice, `MapLayer`, lives here because the paint is what reads it. When to repaint and which buffer to use belong to the [controller](../controller/README.md), and packing the colours into bytes for the page belongs to the [http](../http/README.md) package.

## How it works

1. `ElevationRaster.paint` takes the elevation grid, the plate grid, a `MapLayer`, and optionally a buffer. It checks its arguments, reuses the buffer when its length is width times height, and fills it row by row.
2. Each cell's colour comes from one function per layer: `elevationCell` for Elevation, `plateBoundaryCell` for Plates, and `overlayCell` for Overlay.
3. `elevationCell` takes `oceanRamp` below zero. On land it takes `landRamp`, reads the west and north neighbours, computes the light with `hillshadeLit`, and applies it with `applyHillshade`.
4. `plateBoundaryCell` and `overlayCell` test the stroke with `isPlateBoundary`, which widens `isPlateBoundaryCore`. The core test looks east and south through `SphereTopology.neighbor`. `overlayCell` darkens the elevation colour with `darken`.
5. The result is an `ElevationRaster` that answers `width`, `height`, `rgb`, `pixels`, and `usesBuffer`.

**Start reading at:** `ElevationRaster.paint` in [ElevationRaster.java](ElevationRaster.java).

## Depends on

- [product world fields](../../../../../../../../product/src/main/java/com/aethelgard/product/world/fields/README.md) — `Grid`, the elevation and plate grids it paints
- [product world topology](../../../../../../../../product/src/main/java/com/aethelgard/product/world/topology/README.md) — `SphereTopology.neighbor`, for the east and south neighbours of the stroke test

## Used by

- [controller](../controller/README.md) — `MapController` paints through `ElevationRaster.paint` and holds the `MapLayer` in force
- [http](../http/README.md) — `MapHost` packs `ElevationRaster.pixels` into the raster body, and parses `MapLayer` names
- [raster tests](../../../../../../test/java/com/aethelgard/ui/raster/README.md) — `RasterTest` checks the colours

## Where each step happens

### [Controller](../../../../../../../../docs/architecture/studio/controller.md)

| Step | Member | File |
|------|--------|------|
| 12. The layers Elevation, Plates, and Overlay | `MapLayer.ELEVATION`, `MapLayer.PLATES`, `MapLayer.OVERLAY`, `MapLayer.label` | [MapLayer.java](MapLayer.java) |

The speeds of step 12, and the other steps, are in the [controller](../controller/README.md) package.

### [Raster](../../../../../../../../docs/architecture/studio/raster.md)

| Step | Member | File |
|------|--------|------|
| 1. The paint checks its arguments, reuses or allocates the buffer, and fills it row by row | `ElevationRaster.paint`, `ElevationRaster.of` | [ElevationRaster.java](ElevationRaster.java) |
| 2. An Elevation cell takes the ocean ramp, or the land ramp with its light | `ElevationRaster.elevationCell`, `ElevationRaster.oceanRamp`, `ElevationRaster.landRamp`, `ElevationRaster.hillshadeLit`, `ElevationRaster.applyHillshade` | [ElevationRaster.java](ElevationRaster.java) |
| 3. A Plates cell takes the boundary colour on the stroke and the interior colour elsewhere | `ElevationRaster.plateBoundaryCell`, `ElevationRaster.isPlateBoundary`, `ElevationRaster.isPlateBoundaryCore`, `ElevationRaster.PLATE_BOUNDARY_RGB`, `ElevationRaster.PLATE_INTERIOR_RGB` | [ElevationRaster.java](ElevationRaster.java) |
| 4. An Overlay cell takes the Elevation colour, darkened on the stroke | `ElevationRaster.overlayCell`, `ElevationRaster.darken` | [ElevationRaster.java](ElevationRaster.java) |
| 5. The unshaded colour of a height, the packing of a colour, and the unused colour per plate id | `ElevationRaster.rgbOf`, `ElevationRaster.pack`, `ElevationRaster.plateRgb`, `ElevationRaster.PLATE_GOLDEN` | [ElevationRaster.java](ElevationRaster.java) |
| 6. A raster answers its size, a cell's colour, its array, and whether its array is a given buffer | `ElevationRaster.width`, `ElevationRaster.height`, `ElevationRaster.rgb`, `ElevationRaster.pixels`, `ElevationRaster.usesBuffer` | [ElevationRaster.java](ElevationRaster.java) |

The ramp stops of the Model are `ElevationRaster.OCEAN_STOP_E`, `OCEAN_STOP_R`, `OCEAN_STOP_G`, `OCEAN_STOP_B`, `LAND_STOP_E`, `LAND_STOP_R`, `LAND_STOP_G`, and `LAND_STOP_B`; the light is bounded by `HILLSHADE_MIN`, `HILLSHADE_FLAT`, and `HILLSHADE_MAX`; and the clamps are `CLAMP` and `OCEAN_FLOOR`.

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [ElevationRaster.java](ElevationRaster.java) | Paints the grids into one packed colour per cell for a layer, and holds the result | `ElevationRaster`, `paint`, `elevationCell`, `plateBoundaryCell`, `overlayCell` |
| [MapLayer.java](MapLayer.java) | The map layers a person can choose, each with its label | `MapLayer` |
