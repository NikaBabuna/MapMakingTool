/*
 * File: ui/web/src/lib/viewport.ts
 * Purpose: Pure pan/zoom math for toroidal map stage ↔ world cells
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

/** Wrap horizontal pan into one map period; ty left unchanged. */
export function wrapPan(vp: Viewport, displayW: number, _displayH: number): Viewport {
  const periodX = displayW * vp.scale;
  if (periodX <= 0) {
    return vp;
  }
  return {
    scale: vp.scale,
    tx: floorMod(vp.tx, periodX),
    ty: vp.ty,
  };
}

/**
 * Zoom toward a point in stage (CSS) coordinates; scale clamped to [minScale, MAX_SCALE].
 * Vertical translation is re-locked to stage center (horizontal-only camera).
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
  const next = wrapPan(
    {
      scale: nextScale,
      tx: stageX - worldX * nextScale,
      ty: vp.ty,
    },
    displayW,
    displayH,
  );
  return lockVertical(next, stageH, displayH);
}

/** Pan horizontally only; dy is ignored. Vertical position stays locked via caller/lockVertical. */
export function panBy(
  vp: Viewport,
  dx: number,
  _dy: number,
  displayW: number,
  displayH: number,
): Viewport {
  return wrapPan({ ...vp, tx: vp.tx + dx }, displayW, displayH);
}

/** Map stage (CSS) point to world cell indices (toroidal in x; y uses locked viewport). */
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
  const localX = floorMod((stageX - vp.tx) / vp.scale, displayW);
  const localY = (stageY - vp.ty) / vp.scale;
  if (localY < 0 || localY >= displayH) {
    return null;
  }
  const x = Math.floor((localX / displayW) * worldW) % worldW;
  const y = Math.floor((localY / displayH) * worldH);
  if (y < 0 || y >= worldH) {
    return null;
  }
  return { x, y };
}

export function cssTransform(vp: Viewport): string {
  return `translate(${vp.tx}px, ${vp.ty}px) scale(${vp.scale})`;
}
