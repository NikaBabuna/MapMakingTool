<!--
  File: docs/architecture/studio/web/canvas.md
  Purpose: MapCanvas, resetViewport — drawing the raster with an east–west wrap, and turning wheel, drag, click, and hover into zoom, pan, inspection, and coordinates
  Audience: Agents and humans
  Update when: MapCanvas's paint, its pointer or wheel handlers, the tile loop, or its overlays change
-->

# Canvas

The map is drawn at its own resolution into a hidden canvas once per new raster, then copied, scaled and shifted by the camera, into the visible canvas. Three copies are laid side by side, so panning east or west never reaches an edge. North and south of the map stays dark. The wheel zooms around the pointer, dragging pans, a click without a drag inspects the cell under it, and the corner shows the cell under the pointer.

## What it reads

The raster bytes, the viewport $\mathit{vp} = (\zeta, t_x, t_y)$ ([viewport](viewport.md)), the stage's size, and pointer and wheel events. Optionally the current layer and the layer list, for the layer switch.

## What it writes

Pixels on the visible canvas; `onViewportChange` with a new viewport on zoom or pan; `onCell(x, y)` on a click; `onStageMetrics` with the stage and display sizes whenever they change; `onLayer` from the layer switch. A raster that fails to decode throws from its effect.

## Model

The source canvas holds the map at its resolution, $D_w \times D_h = W \times H$. The view canvas is the stage's size $V_w \times V_h$ in CSS pixels. With smoothing off (nearest-neighbour), the view shows the source through the affine map $p = \zeta\,m + t$, where $\zeta$ is the camera's scale, three times, one period apart:

$$\mathrm{view} = \bigcup_{i \in \{-1, 0, 1\}} \bigl(\zeta\,(\mathrm{source} + (i\,D_w, 0)) + (t_x, t_y)\bigr).$$

After a pan or a zoom the camera has $t_x \in [0, \zeta D_w)$ ([viewport](viewport.md)); the copies then span $[t_x - \zeta D_w,\; t_x + 2 \zeta D_w) \supseteq [0, 2 \zeta D_w)$, so the whole stage is covered whenever $V_w \le 2 \zeta D_w$.

`MapCanvas` (paint) in [`MapCanvas.tsx`](../../../../ui/web/src/components/MapCanvas.tsx):

```tsx
ctx.setTransform(1, 0, 0, 1, 0, 0);
ctx.clearRect(0, 0, stageW, stageH);
ctx.imageSmoothingEnabled = false;
ctx.setTransform(scale, 0, 0, scale, tx, ty);
// Horizontal loop tiles only; N/S of the map band stay dark (blank margins)
for (let i = -1; i <= 1; i++) {
  ctx.drawImage(source, i * dw, 0);
}
```

**Input.** A wheel step zooms by $f = 1.12$ (or $1/1.12$) around the pointer, never below the fit scale; a press starts a drag, which pans once the pointer has moved more than 2 px on an axis; a release that did not pan inspects the cell under the pointer.

## Procedure

1. A new raster is decoded with `decodePackedRaster`, put into the source canvas (resized to $W \times H$ when needed), and the view is repainted and the sizes reported. [`MapCanvas`](../../../../ui/web/src/components/MapCanvas.tsx).
2. A resize observer on the stage records its size, reports it, and repaints; a new viewport repaints. [`MapCanvas`](../../../../ui/web/src/components/MapCanvas.tsx).
3. Paint sizes the view canvas to the stage, clears it, turns smoothing off, applies the camera transform, and draws the three copies. [`MapCanvas`](../../../../ui/web/src/components/MapCanvas.tsx).
4. The wheel handler computes the fit scale and calls `zoomAt` with the pointer's stage position. The pointer handlers capture the pointer, pan with `panBy`, and on a click call `stageToCell` and `onCell`. Hover calls `stageToCell` to show `x <x> · y <y>` in the corner, next to the scale `×<s>`. [`MapCanvas`](../../../../ui/web/src/components/MapCanvas.tsx).
5. The layer switch renders one button per layer, marks the current one, and stops its pointer events from starting a pan. [`MapCanvas`](../../../../ui/web/src/components/MapCanvas.tsx).
6. `resetViewport` returns the fitted viewport for given stage and display sizes; the root calls it to reset the view. [`resetViewport`](../../../../ui/web/src/components/MapCanvas.tsx).

## What is true afterwards

The visible canvas shows the latest raster through the current camera, with square pixels and no blur at any zoom. A click inspects exactly the cell drawn under the pointer, including across the east–west seam. The frame, graticule, and edge ticks drawn over the stage are fixed to the stage, not to map coordinates, so they do not move with the map ([styles](styles.md)). The view canvas is sized in CSS pixels, without a device-pixel-ratio factor.

## Cost

Decoding is $O(WH)$ per raster. A repaint draws three scaled copies of the source.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Canvas | `MapCanvas` | `MapCanvas`, `resetViewport` | [`ui/web/src/components/MapCanvas.tsx`](../../../../ui/web/src/components/MapCanvas.tsx) |

Parent: [web front](README.md). The camera it applies: [viewport](viewport.md).
