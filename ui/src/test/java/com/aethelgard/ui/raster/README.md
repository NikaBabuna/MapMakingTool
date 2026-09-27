<!--
  File: ui/src/test/java/com/aethelgard/ui/raster/README.md
  Purpose: Door to the tests of the studio's raster: water and land ramps, hill shading, and the plate stroke
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Raster tests

Proves the colours the studio paints for each layer, against colours worked out from the paper and not from the code.

**Paper:** [raster](../../../../../../../../docs/architecture/studio/raster.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

Painting is a pure function, so these tests need no controller, session, or server: they build small grids by hand and paint them. The expected colours are computed in the test from the ramp stops and the formulas of the paper, so a slip in the code cannot also slip into what the test expects. How the controller decides when to paint is proved by the [controller tests](../controller/README.md).

## How it works

`RasterTest` keeps its own copy of the paper's ramp stops (`OCEAN_E`, `OCEAN_C`, `LAND_E`, `LAND_C`) and its own `ramp`, `shade`, and `third`, which follow the Model of the raster page. It builds grids with `grid` and `zeros`, paints them with `ElevationRaster.paint` for a `MapLayer`, and compares cells. The stroke tests find the cells that face another plate with `neighbour` and `distanceToOtherPlate`, on the sphere's wrap rules. `platesDrawABoldStrokeAlongEveryContact` is red on purpose: it shows that plates meeting across the north pole get no stroke.

**Start reading at:** `RasterTest.landFollowsTheRamp` in [RasterTest.java](RasterTest.java).

## Depends on

- [raster](../../../../../../main/java/com/aethelgard/ui/raster/README.md) — `ElevationRaster` and `MapLayer`, the code under test
- [product world fields](../../../../../../../../product/src/main/java/com/aethelgard/product/world/fields/README.md) — `Grid`, the grids it paints

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [RasterTest.java](RasterTest.java) | Proves the water and land ramps, hill shading on land only, repeatable colours, and the plate stroke in the Plates and Overlay layers | `RasterTest` |
