<!--
  File: docs/architecture/studio/raster.md
  Purpose: Raster — one packed colour per cell for the Elevation, Plates, and Overlay layers
  Audience: Agents and humans
  Update when: The paint, a colour ramp, the hillshade, or the boundary stroke changes
-->

# Raster

The map is painted cell by cell: sea by depth, from dark to pale blue; land by height, from green through tan to near white, shaded so slopes facing the north-west light catch it; plate edges as dark strokes. The same grids and the same layer always give the same colours, so the picture is as reproducible as the world.

## What it reads

The settled `elevation` grid, the `plates` grid (for the Plates and Overlay layers), a `MapLayer`, and optionally a buffer to paint into.

## What it writes

An `ElevationRaster`: width, height, and a row-major `int` array holding one packed colour $\texttt{0xRRGGBB}$ per cell, written into the given buffer when its length is $WH$, or into a new array otherwise. Refusals: a null elevation or layer, or a null or differently sized plate grid for a layer that needs one, throws; reading the colour of a cell outside the raster throws `IllegalArgumentException`.

## Model

**Ramps.** For stops $(e_0, \mathit{col}_0), \dots, (e_4, \mathit{col}_4)$ and a clamped height $e \in [e_0, e_4]$ with $e_i \le e \le e_{i+1}$, each channel is interpolated in integers, with $\operatorname{trunc}$ rounding toward zero:

$$\mathrm{ramp}(e) = \mathit{col}_i + \operatorname{trunc}\!\Bigl(\frac{(\mathit{col}_{i+1} - \mathit{col}_i)(e - e_i)}{e_{i+1} - e_i}\Bigr) \quad \text{per channel}.$$

| Ramp | Heights $e_i$ | Colours $\mathit{col}_i$ (R, G, B) | Clamp |
|------|---------------|-------------------------|-------|
| Ocean | $-64, -32, -16, -8, -1$ | (24, 64, 104), (44, 98, 146), (66, 132, 178), (86, 162, 206), (110, 190, 226) | $e \in [-64, -1]$ |
| Land | $0, 12, 24, 40, 64$ | (150, 196, 120), (186, 208, 132), (214, 190, 124), (232, 198, 140), (255, 250, 236) | $e \in [0, 64]$ |

**Hillshade.** Land ($e \ge 0$) is lit by its rise over its west and north neighbours on the [topology](../world/topology.md):

$$\ell = \mathrm{clamp}\bigl(12 + 2(e - e_{\mathrm{W}}) + 2(e - e_{\mathrm{N}}),\; 6,\; 18\bigr), \qquad \mathit{ch}' = \min\bigl(255,\; \lfloor \mathit{ch}\,\ell / 12 \rfloor\bigr) \text{ for each channel } \mathit{ch}.$$

Flat land ($\ell = 12$) keeps its ramp colour; ocean is never shaded.

**Boundaries.** A cell is a core boundary cell when its east or south neighbour is on another plate. A cell is on the stroke when it, or one of its four neighbours, is a core boundary cell, so the stroke is at least two cells wide:

$$\mathrm{core}(c) \iff P(c) \ne P(\nu_{\mathrm{E}}(c)) \vee P(c) \ne P(\nu_{\mathrm{S}}(c)), \qquad \mathrm{stroke}(c) \iff \mathrm{core}(c) \vee \exists d \in D_4:\ \mathrm{core}(\nu(c, d)).$$

**Layers.**

$$\mathrm{Elevation}(c) = \begin{cases} \mathrm{ocean}(e) & e < 0 \\ \mathrm{shade}(\mathrm{land}(e)) & e \ge 0 \end{cases}, \qquad \mathrm{Plates}(c) = \begin{cases} (36, 38, 42) & \mathrm{stroke}(c) \\ (88, 92, 96) & \text{otherwise} \end{cases}, \qquad \mathrm{Overlay}(c) = \begin{cases} \lfloor \mathrm{Elevation}(c) / 3 \rfloor & \mathrm{stroke}(c) \\ \mathrm{Elevation}(c) & \text{otherwise,} \end{cases}$$

where the division by 3 applies to each channel.

## Procedure

1. The paint checks its arguments, takes the reuse buffer when its length is $WH$ (or allocates), and fills it row by row with the layer's cell colour. A paint without a buffer, and the paint of the Elevation layer alone, allocate.
2. An Elevation cell of negative height takes the ocean ramp. A land cell takes the land ramp, reads its west and north neighbours, computes the light $\ell$, and applies it.
3. A Plates cell takes the boundary colour on the stroke, which is built on the core test, and the interior colour elsewhere.
4. An Overlay cell takes the Elevation colour, darkened on the stroke.
5. The unshaded colour of a height, and the packing of a colour from its channels, are also available alone. A hashed colour per plate id exists, and no layer uses it.
6. A raster answers its width, its height, the colour of cell $(x, y)$, its pixel array (the array itself, not a copy), and whether its array is a given buffer.

## What is true afterwards

The array has length $WH$, and its colours are a function of the grids and the layer alone. A paint into a reused buffer overwrites every cell, so nothing of the previous picture survives. Heights above 64 take the ramp colour of 64, though their shading still uses the true height differences, and depths below −64 take the colour of −64.

## Cost

$O(WH)$ per paint; the stroke test looks at up to five core tests per cell.

Code: [raster/](../../../ui/src/main/java/com/aethelgard/ui/raster/README.md)
Parent: [studio](README.md). The grid it paints: [isostasy](../world/crust/isostasy.md). The look it serves: [the style guide](../../product/style-guide.md).
