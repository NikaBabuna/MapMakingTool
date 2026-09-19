/*
 * File: ui/web/src/lib/layout.ts
 * Purpose: Resizable runner layout sizes + rail visibility persistence (F-053)
 * Audience: MapTool
 * Update when: Layout regions, clamps, or storage keys change
 *
 * Alpha: keys may still change (no ADR yet).
 */

export type LayoutRegion = "leftRail" | "rightRail" | "terminal";
export type RailSide = "left" | "right";

export type LayoutSizes = Record<LayoutRegion, number>;

/** Pixels. */
export const DEFAULT_LAYOUT: LayoutSizes = {
  leftRail: 220,
  rightRail: 280,
  terminal: 176,
};

export const LAYOUT_LIMITS: Record<LayoutRegion, { min: number; max: number }> = {
  leftRail: { min: 168, max: 420 },
  rightRail: { min: 208, max: 520 },
  terminal: { min: 96, max: 520 },
};

export const LAYOUT_KEY_PREFIX = "aethelgard.layout.";

export const RAIL_OPEN_KEYS: Record<RailSide, string> = {
  left: "aethelgard.rail.left.open",
  right: "aethelgard.rail.right.open",
};

export function layoutKey(region: LayoutRegion): string {
  return `${LAYOUT_KEY_PREFIX}${region}`;
}

export function clampSize(region: LayoutRegion, value: number): number {
  const { min, max } = LAYOUT_LIMITS[region];
  if (!Number.isFinite(value)) {
    return DEFAULT_LAYOUT[region];
  }
  return Math.min(max, Math.max(min, Math.round(value)));
}

export function readLayout(): LayoutSizes {
  if (typeof window === "undefined") {
    return { ...DEFAULT_LAYOUT };
  }
  const next = { ...DEFAULT_LAYOUT };
  for (const region of Object.keys(DEFAULT_LAYOUT) as LayoutRegion[]) {
    const raw = window.localStorage.getItem(layoutKey(region));
    if (raw !== null) {
      next[region] = clampSize(region, Number.parseInt(raw, 10));
    }
  }
  return next;
}

export function writeLayout(sizes: LayoutSizes): void {
  if (typeof window === "undefined") {
    return;
  }
  for (const region of Object.keys(sizes) as LayoutRegion[]) {
    window.localStorage.setItem(layoutKey(region), String(sizes[region]));
  }
}

/** Drops stored sizes so defaults apply again (View → Reset layout). */
export function clearLayout(): void {
  if (typeof window === "undefined") {
    return;
  }
  for (const region of Object.keys(DEFAULT_LAYOUT) as LayoutRegion[]) {
    window.localStorage.removeItem(layoutKey(region));
  }
}
