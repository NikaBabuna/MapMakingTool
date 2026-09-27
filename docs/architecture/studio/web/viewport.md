<!--
  File: docs/architecture/studio/web/viewport.md
  Purpose: Viewport — the camera: fit, zoom around a point, pan with an east–west wrap and a north–south clamp, and stage points to map cells
  Audience: Agents and humans
  Update when: The camera's fit, zoom, pan, clamp, or cell rule changes, or its largest scale changes
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

**Pan.** $\mathit{vp}' = \mathrm{clampV}(\mathrm{wrap}(\zeta, t + d))$.

**Stage to cell.** With $m_x = ((p_x - t_x)/\zeta) \bmod D_w$ and $m_y = (p_y - t_y)/\zeta$, the cell is

$$(x, y) = \Bigl(\bigl\lfloor \tfrac{m_x}{D_w} W \bigr\rfloor \bmod W,\;\; \bigl\lfloor \tfrac{m_y}{D_h} H \bigr\rfloor\Bigr) \quad \text{when } 0 \le m_y < D_h, \text{ and none otherwise.}$$

## Procedure

1. The fit scale is $\mathrm{fit}$, and 1 when any size is not positive. The fitted viewport centres the map at that scale.
2. A scale is bounded between the given minimum and the largest scale, 16.
3. Wrapping reduces $t_x$ modulo one map width at the current scale, with the floor modulus.
4. The vertical clamp centres the map when it fits in height, and otherwise keeps it between its top and bottom edges.
5. Zoom at a point and pan compose these as in the Model.
6. Stage to cell returns the cell under a stage point, or none in the margins or for a non-positive size.
7. The identity viewport is $(1, 0, 0)$, the camera before the first fit. A viewport can also be written as a CSS transform string, which the page does not use.

## What is true afterwards

After a pan or a zoom, $0 \le t_x < \zeta D_w$, the scale is between the fit scale and 16, and the map never leaves a gap above or below it: it either fills the stage's height or is centred in it. Every stage point inside the map's band maps to one cell, and the column wraps, so the seam between copies is invisible to inspection.

Code: [lib/](../../../../ui/web/src/lib/README.md)
Parent: [web front](README.md). Where the camera is applied: [canvas](canvas.md).
