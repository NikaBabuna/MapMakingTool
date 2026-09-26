<!--
  File: docs/architecture/studio/raster.md
  Purpose: ElevationRaster — one packed colour per cell for the Elevation, Plates, and Overlay layers
  Audience: Agents and humans
  Update when: ElevationRaster.paint, a colour ramp, the hillshade, or the boundary stroke changes
-->

# Raster

The map is painted cell by cell: sea by depth, from dark to pale blue; land by height, from green through tan to near white, shaded so slopes facing the north-west light catch it; plate edges as dark strokes. The same grids and the same layer always give the same colours, so the picture is as reproducible as the world.

## What it reads

The settled `elevation` grid, the `plates` grid (for the Plates and Overlay layers), a `MapLayer`, and optionally a buffer to paint into.

## What it writes

An `ElevationRaster`: width, height, and a row-major `int` array holding one packed colour $\texttt{0xRRGGBB}$ per cell, written into the given buffer when its length is $WH$, or into a new array otherwise. Refusals: a null elevation or layer, or a null or differently sized plate grid for a layer that needs one, throws; `rgb(x, y)` outside the raster throws `IllegalArgumentException`.

## Model

**Ramps.** For stops $(e_0, \mathit{col}_0), \dots, (e_4, \mathit{col}_4)$ and a clamped height $e \in [e_0, e_4]$ with $e_i \le e \le e_{i+1}$, each channel is interpolated in integers, with $\operatorname{trunc}$ rounding toward zero:

$$\mathrm{ramp}(e) = \mathit{col}_i + \operatorname{trunc}\!\Bigl(\frac{(\mathit{col}_{i+1} - \mathit{col}_i)(e - e_i)}{e_{i+1} - e_i}\Bigr) \quad \text{per channel}.$$

| Ramp | Heights $e_i$ | Colours $\mathit{col}_i$ (R, G, B) | Clamp |
|------|---------------|-------------------------|-------|
| Ocean | $-64, -32, -16, -8, -1$ | (24, 64, 104), (44, 98, 146), (66, 132, 178), (86, 162, 206), (110, 190, 226) | $e \in [-64, -1]$ |
| Land | $0, 12, 24, 40, 64$ | (150, 196, 120), (186, 208, 132), (214, 190, 124), (232, 198, 140), (255, 250, 236) | $e \in [0, 64]$ |

`ElevationRaster.landRamp` (one segment) in [`ElevationRaster.java`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java):

```java
if (e <= e1) {
  int span = e1 - e0;
  int t = span == 0 ? 0 : e - e0;
  int r = LAND_STOP_R[i] + ((LAND_STOP_R[i + 1] - LAND_STOP_R[i]) * t) / span;
  int g = LAND_STOP_G[i] + ((LAND_STOP_G[i + 1] - LAND_STOP_G[i]) * t) / span;
  int b = LAND_STOP_B[i] + ((LAND_STOP_B[i + 1] - LAND_STOP_B[i]) * t) / span;
  return pack(r, g, b);
```

**Hillshade.** Land ($e \ge 0$) is lit by its rise over its west and north neighbours on the [topology](../world/topology.md):

$$\ell = \mathrm{clamp}\bigl(12 + 2(e - e_{\mathrm{W}}) + 2(e - e_{\mathrm{N}}),\; 6,\; 18\bigr), \qquad \mathit{ch}' = \min\bigl(255,\; \lfloor \mathit{ch}\,\ell / 12 \rfloor\bigr) \text{ for each channel } \mathit{ch}.$$

Flat land ($\ell = 12$) keeps its ramp colour; ocean is never shaded.

`ElevationRaster.hillshadeLit` in [`ElevationRaster.java`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java):

```java
int lit = HILLSHADE_FLAT + 2 * dw + 2 * dn;
if (lit < HILLSHADE_MIN) {
  return HILLSHADE_MIN;
}
if (lit > HILLSHADE_MAX) {
  return HILLSHADE_MAX;
}
return lit;
```

**Boundaries.** A cell is a core boundary cell when its east or south neighbour is on another plate. A cell is on the stroke when it, or one of its four neighbours, is a core boundary cell, so the stroke is at least two cells wide:

$$\mathrm{core}(c) \iff P(c) \ne P(\nu_{\mathrm{E}}(c)) \vee P(c) \ne P(\nu_{\mathrm{S}}(c)), \qquad \mathrm{stroke}(c) \iff \mathrm{core}(c) \vee \exists d \in D_4:\ \mathrm{core}(\nu(c, d)).$$

**Layers.**

$$\mathrm{Elevation}(c) = \begin{cases} \mathrm{ocean}(e) & e < 0 \\ \mathrm{shade}(\mathrm{land}(e)) & e \ge 0 \end{cases}, \qquad \mathrm{Plates}(c) = \begin{cases} (36, 38, 42) & \mathrm{stroke}(c) \\ (88, 92, 96) & \text{otherwise} \end{cases}, \qquad \mathrm{Overlay}(c) = \begin{cases} \lfloor \mathrm{Elevation}(c) / 3 \rfloor & \mathrm{stroke}(c) \\ \mathrm{Elevation}(c) & \text{otherwise,} \end{cases}$$

where the division by 3 applies to each channel.

## Procedure

1. `paint` checks its arguments, picks the reuse buffer when its length is $WH$ (or allocates), and fills it row by row with the layer's cell colour. `paint` without a buffer and `of` (the Elevation layer) allocate. [`ElevationRaster.paint`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java).
2. `elevationCell` returns `oceanRamp` of a negative height. For land it takes `landRamp`, reads the west and north neighbours, and applies `hillshadeLit` and `applyHillshade`. [`ElevationRaster.elevationCell`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java).
3. `plateBoundaryCell` paints `PLATE_BOUNDARY_RGB` on the stroke (`isPlateBoundary`, built on `isPlateBoundaryCore`) and `PLATE_INTERIOR_RGB` elsewhere. [`ElevationRaster.plateBoundaryCell`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java).
4. `overlayCell` paints the elevation colour and applies `darken` on the stroke. [`ElevationRaster.overlayCell`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java).
5. `rgbOf` is the unshaded colour of a height, and `pack` builds a colour from its channels. `plateRgb`, a hashed colour per plate id (with `PLATE_GOLDEN`), is not used by any layer. [`ElevationRaster.rgbOf`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java).
6. A raster answers `width`, `height`, `rgb(x, y)`, `pixels` (the array itself, not a copy), and `usesBuffer`, which is true when its array is the given buffer. [`ElevationRaster.pixels`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java).

## What is true afterwards

The array has length $WH$, and its colours are a function of the grids and the layer alone. A paint into a reused buffer overwrites every cell, so nothing of the previous picture survives. Heights above 64 take the ramp colour of 64, though their shading still uses the true height differences, and depths below −64 take the colour of −64.

## Cost

$O(WH)$ per paint; the stroke test looks at up to five core tests per cell.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Paint | `ElevationRaster` | `paint`, `of`, `rgbOf`, `oceanRamp`, `landRamp`, `plateRgb`, `isPlateBoundaryCore`, `isPlateBoundary`, `plateBoundaryCell`, `hillshadeLit`, `applyHillshade`, `darken`, `elevationCell`, `overlayCell`, `width`, `height`, `rgb`, `usesBuffer`, `pixels`, `pack`, `CLAMP`, `OCEAN_FLOOR`, `OCEAN_RGB`, `OCEAN_STOP_E`, `OCEAN_STOP_R`, `OCEAN_STOP_G`, `OCEAN_STOP_B`, `LAND_STOP_E`, `LAND_STOP_R`, `LAND_STOP_G`, `LAND_STOP_B`, `HILLSHADE_FLAT`, `HILLSHADE_MIN`, `HILLSHADE_MAX`, `PLATE_GOLDEN`, `PLATE_INTERIOR_RGB`, `PLATE_BOUNDARY_RGB` | [`ui/src/main/java/com/aethelgard/ui/ElevationRaster.java`](../../../ui/src/main/java/com/aethelgard/ui/ElevationRaster.java) |

Parent: [studio](README.md). The grid it paints: [isostasy](../world/crust/isostasy.md). The look it serves: [the style guide](../../product/style-guide.md).
