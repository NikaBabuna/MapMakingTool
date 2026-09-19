/*
 * File: ui/web/src/lib/viewport.ts
 * Purpose: Pure pan/zoom math for sphere-on-rectangle map stage ↔ world cells (F-045)
 * Audience: MapCanvas + unit witness
 * Update when: Viewport model changes
 */

export type Viewport = {
  scale: number;
  tx: number;
  ty: number;
};

export const IDENTITY_VIEWPORT: Viewport = { scale: 1, tx: 0, ty: 0 };

export const MAX_SCALE = 16;

export function floorMod(n: number, period: number): number {
  if (period <= 0) {
    return n;
  }
  return ((n % period) + period) % period;
}

/** Scale that fits the whole map in the stage (contain). */
export function fitScale(stageW: number, stageH: number, displayW: number, displayH: number): number {
  if (displayW <= 0 || displayH <= 0 || stageW <= 0 || stageH <= 0) {
    return 1;
  }
  return Math.min(stageW / displayW, stageH / displayH);
}

export function clampScale(scale: number, minScale: number): number {
  return Math.min(MAX_SCALE, Math.max(minScale, scale));
}

/** Vertically center the map in the stage at the given scale (tx unchanged). */
export function lockVertical(vp: Viewport, stageH: number, displayH: number): Viewport {
  return {
    ...vp,
    ty: (stageH - displayH * vp.scale) / 2,
  };
}

/**
 * Clamp vertical pan at polar edges when the map fits or is only slightly taller.
 * When {@code mapH <= stageH}, lock centered (no Y travel). When zoomed in past the stage,
 * callers may still use antipodal {@link wrapPan}; this helper keeps the map from leaving
 * the polar band for the non-wrap path.
 */
export function clampVertical(vp: Viewport, stageH: number, displayH: number): Viewport {
  const mapH = displayH * vp.scale;
  if (mapH <= stageH) {
    return lockVertical(vp, stageH, displayH);
  }
  const minTy = stageH - mapH;
  const maxTy = 0;
  return {
    ...vp,
    ty: Math.min(maxTy, Math.max(minTy, vp.ty)),
  };
}

/** True when the map is fully visible in the stage (fitted / zoomed out). */
export function isZoomedOut(vp: Viewport, stageH: number, displayH: number): boolean {
  return displayH * vp.scale <= stageH;
}

/** Center the map in the stage at the given scale. */
export function centeredViewport(
  stageW: number,
  stageH: number,
  displayW: number,
  displayH: number,
  scale: number,
): Viewport {
  return {
    scale,
    tx: (stageW - displayW * scale) / 2,
    ty: (stageH - displayH * scale) / 2,
  };
}

export function fittedViewport(
  stageW: number,
  stageH: number,
  displayW: number,
  displayH: number,
): Viewport {
  const scale = fitScale(stageW, stageH, displayW, displayH);
  return centeredViewport(stageW, stageH, displayW, displayH, scale);
}

/**
 * Wrap pan into one map period on X and Y. Crossing a vertical period shifts tx by half a
 * horizontal period (antipodal longitude — F-045 C2).
 */
export function wrapPan(vp: Viewport, displayW: number, displayH: number): Viewport {
  const periodX = displayW * vp.scale;
  const periodY = displayH * vp.scale;
  if (periodX <= 0 || periodY <= 0) {
    return vp;
  }
  let tx = vp.tx;
  let ty = vp.ty;
  while (ty >= periodY) {
    ty -= periodY;
    tx -= periodX / 2;
  }
  while (ty < 0) {
    ty += periodY;
    tx += periodX / 2;
  }
  tx = floorMod(tx, periodX);
  return { scale: vp.scale, tx, ty };
}

/**
 * Zoom toward a point in stage (CSS) coordinates; scale clamped to [minScale, MAX_SCALE].
 * Zoomed out: Y locked (no polar travel). Zoomed in: antipodal Y wrap retained.
 */
export function zoomAt(
  vp: Viewport,
  stageX: number,
  stageY: number,
  factor: number,
  minScale: number,
  displayW: number,
  displayH: number,
  stageH: number,
): Viewport {
  const nextScale = clampScale(vp.scale * factor, minScale);
  const worldX = (stageX - vp.tx) / vp.scale;
  const worldY = (stageY - vp.ty) / vp.scale;
  const next = wrapPanX(
    {
      scale: nextScale,
      tx: stageX - worldX * nextScale,
      ty: stageY - worldY * nextScale,
    },
    displayW,
  );
  if (isZoomedOut(next, stageH, displayH)) {
    return clampVertical(next, stageH, displayH);
  }
  return wrapPan(next, displayW, displayH);
}

/** Pan: X always wraps. Y locked when zoomed out; antipodal wrap when zoomed in. */
export function panBy(
  vp: Viewport,
  dx: number,
  dy: number,
  displayW: number,
  displayH: number,
  stageH: number,
): Viewport {
  const moved = { ...vp, tx: vp.tx + dx, ty: vp.ty + (isZoomedOut(vp, stageH, displayH) ? 0 : dy) };
  if (isZoomedOut(moved, stageH, displayH)) {
    return clampVertical(wrapPanX(moved, displayW), stageH, displayH);
  }
  return wrapPan(moved, displayW, displayH);
}

/** Wrap horizontal pan only. */
export function wrapPanX(vp: Viewport, displayW: number): Viewport {
  const periodX = displayW * vp.scale;
  if (periodX <= 0) {
    return vp;
  }
  return { ...vp, tx: floorMod(vp.tx, periodX) };
}

/** Map stage (CSS) point to world cell indices (sphere wrap X+Y with antipodal Y). */
export function stageToCell(
  vp: Viewport,
  stageX: number,
  stageY: number,
  worldW: number,
  worldH: number,
  displayW: number,
  displayH: number,
): { x: number; y: number } | null {
  if (displayW <= 0 || displayH <= 0 || worldW <= 0 || worldH <= 0) {
    return null;
  }
  let localX = (stageX - vp.tx) / vp.scale;
  let localY = (stageY - vp.ty) / vp.scale;
  while (localY >= displayH) {
    localY -= displayH;
    localX += displayW / 2;
  }
  while (localY < 0) {
    localY += displayH;
    localX -= displayW / 2;
  }
  localX = floorMod(localX, displayW);
  const x = Math.floor((localX / displayW) * worldW);
  const y = Math.floor((localY / displayH) * worldH);
  if (y < 0 || y >= worldH) {
    return null;
  }
  return { x: ((x % worldW) + worldW) % worldW, y };
}

export function cssTransform(vp: Viewport): string {
  return `translate(${vp.tx}px, ${vp.ty}px) scale(${vp.scale})`;
}
