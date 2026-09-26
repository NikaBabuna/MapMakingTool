<!--
  File: docs/architecture/studio/web/viewport.md
  Purpose: viewport.ts — the camera: fit, zoom around a point, pan with an east–west wrap and a north–south clamp, and stage points to map cells
  Audience: Agents and humans
  Update when: A viewport.ts function or MAX_SCALE changes
-->

# Viewport

The camera says how large the map is drawn and where. Zooming keeps the point under the pointer still. Panning east or west wraps round for ever. Panning north or south stops at the map's edge, and a map smaller than the stage stays centred between dark margins. The same camera turns a point on the screen back into a map cell.

## What it reads

A viewport, the stage size $V_w \times V_h$ in CSS pixels, the display size $D_w \times D_h$ of the map (its resolution), the world size $W \times H$, and a stage point, a zoom factor, or a pan offset.

## What it writes

A new viewport, or a map cell (or nothing). Every function is pure: it returns a value and changes nothing.

## Model

A viewport is $\mathit{vp} = (\zeta, t_x, t_y)$: a scale $\zeta$ and a translation $t$, so a map point $m$ is drawn at the stage point

$$p = \zeta\,m + t, \qquad m = \frac{p - t}{\zeta}.$$

With $\bmod$ the floor modulus ($\operatorname{floorMod}(n, P) = n$ when $P \le 0$):

$$\mathrm{fit} = \min\Bigl(\frac{V_w}{D_w}, \frac{V_h}{D_h}\Bigr), \qquad \mathrm{clampScale}(\zeta) = \min\bigl(16, \max(\zeta_{\min}, \zeta)\bigr), \qquad \mathrm{wrap}(\mathit{vp}) = \bigl(\zeta,\; t_x \bmod \zeta D_w,\; t_y\bigr),$$

$$\mathrm{clampV}(\mathit{vp}) = \begin{cases} \bigl(\zeta,\; t_x,\; (V_h - \zeta D_h)/2\bigr) & \zeta D_h \le V_h \\ \bigl(\zeta,\; t_x,\; \min(0, \max(V_h - \zeta D_h,\; t_y))\bigr) & \text{otherwise.} \end{cases}$$

**Zoom at a point.** For a stage point $p$ and a factor $f$, with $m = (p - t)/\zeta$ the map point under $p$:

$$\zeta' = \mathrm{clampScale}(\zeta f), \qquad \mathit{vp}' = \mathrm{clampV}\bigl(\mathrm{wrap}(\zeta',\; p - \zeta' m)\bigr),$$

so $m$ stays under $p$, up to the wrap and the clamp.

`zoomAt` in [`viewport.ts`](../../../../ui/web/src/lib/viewport.ts):

```ts
const nextScale = clampScale(vp.scale * factor, minScale);
const worldX = (stageX - vp.tx) / vp.scale;
const worldY = (stageY - vp.ty) / vp.scale;
const next = wrapPan(
  {
    scale: nextScale,
    tx: stageX - worldX * nextScale,
    ty: stageY - worldY * nextScale,
  },
  displayW,
  displayH,
);
return clampVertical(next, stageH, displayH);
```

**Pan.** $\mathit{vp}' = \mathrm{clampV}(\mathrm{wrap}(\zeta, t + d))$.

**Stage to cell.** With $m_x = ((p_x - t_x)/\zeta) \bmod D_w$ and $m_y = (p_y - t_y)/\zeta$, the cell is

$$(x, y) = \Bigl(\bigl\lfloor \tfrac{m_x}{D_w} W \bigr\rfloor \bmod W,\;\; \bigl\lfloor \tfrac{m_y}{D_h} H \bigr\rfloor\Bigr) \quad \text{when } 0 \le m_y < D_h, \text{ and none otherwise.}$$

## Procedure

1. `fitScale` returns the fit scale, and 1 when any size is not positive. `fittedViewport` centres the map at that scale through `centeredViewport`. [`fittedViewport`](../../../../ui/web/src/lib/viewport.ts).
2. `clampScale` bounds a scale between the given minimum and `MAX_SCALE` (16). [`clampScale`](../../../../ui/web/src/lib/viewport.ts).
3. `wrapPan` reduces $t_x$ modulo one map width at the current scale, with `floorMod`. [`wrapPan`](../../../../ui/web/src/lib/viewport.ts).
4. `clampVertical` centres the map vertically with `lockVertical` when it fits in height, and otherwise keeps it between its top and bottom edges. [`clampVertical`](../../../../ui/web/src/lib/viewport.ts).
5. `zoomAt` and `panBy` compose these as in the Model. [`zoomAt`](../../../../ui/web/src/lib/viewport.ts), [`panBy`](../../../../ui/web/src/lib/viewport.ts).
6. `stageToCell` returns the cell under a stage point, or `null` in the margins or for a non-positive size. [`stageToCell`](../../../../ui/web/src/lib/viewport.ts).
7. `IDENTITY_VIEWPORT` is $(1, 0, 0)$, the camera before the first fit. `cssTransform` writes a viewport as a CSS transform string and is not used by the page. [`cssTransform`](../../../../ui/web/src/lib/viewport.ts).

## What is true afterwards

After a pan or a zoom, $0 \le t_x < \zeta D_w$, the scale is between the fit scale and 16, and the map never leaves a gap above or below it: it either fills the stage's height or is centred in it. Every stage point inside the map's band maps to one cell, and the column wraps, so the seam between copies is invisible to inspection.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Camera | `Viewport` | `Viewport`, `IDENTITY_VIEWPORT`, `MAX_SCALE`, `floorMod`, `fitScale`, `clampScale`, `lockVertical`, `clampVertical`, `centeredViewport`, `fittedViewport`, `wrapPan`, `zoomAt`, `panBy`, `stageToCell`, `cssTransform` | [`ui/web/src/lib/viewport.ts`](../../../../ui/web/src/lib/viewport.ts) |

Parent: [web front](README.md). Where the camera is applied: [canvas](canvas.md).
