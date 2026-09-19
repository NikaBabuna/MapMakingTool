/*
 * File: ui/web/src/lib/viewport.ts
 * Purpose: Pure pan/zoom math for map stage ↔ world cells
 * Audience: MapCanvas + unit witness
 * Update when: Viewport model changes
 */

export type Viewport = {
  scale: number;
  tx: number;
  ty: number;
};

export const IDENTITY_VIEWPORT: Viewport = { scale: 1, tx: 0, ty: 0 };

export const MIN_SCALE = 0.25;
export const MAX_SCALE = 16;

export function clampScale(scale: number): number {
  return Math.min(MAX_SCALE, Math.max(MIN_SCALE, scale));
}

/** Zoom toward a point in stage (CSS) coordinates. */
export function zoomAt(vp: Viewport, stageX: number, stageY: number, factor: number): Viewport {
  const nextScale = clampScale(vp.scale * factor);
  if (nextScale === vp.scale) {
    return vp;
  }
  const worldX = (stageX - vp.tx) / vp.scale;
  const worldY = (stageY - vp.ty) / vp.scale;
  return {
    scale: nextScale,
    tx: stageX - worldX * nextScale,
    ty: stageY - worldY * nextScale,
  };
}

export function panBy(vp: Viewport, dx: number, dy: number): Viewport {
  return { ...vp, tx: vp.tx + dx, ty: vp.ty + dy };
}

/** Map stage (CSS) point to world cell indices. */
export function stageToCell(
  vp: Viewport,
  stageX: number,
  stageY: number,
  worldW: number,
  worldH: number,
  displayW: number,
  displayH: number,
): { x: number; y: number } | null {
  if (displayW <= 0 || displayH <= 0) {
    return null;
  }
  const localX = (stageX - vp.tx) / vp.scale;
  const localY = (stageY - vp.ty) / vp.scale;
  const x = Math.floor((localX / displayW) * worldW);
  const y = Math.floor((localY / displayH) * worldH);
  if (x < 0 || y < 0 || x >= worldW || y >= worldH) {
    return null;
  }
  return { x, y };
}

export function cssTransform(vp: Viewport): string {
  return `translate(${vp.tx}px, ${vp.ty}px) scale(${vp.scale})`;
}
