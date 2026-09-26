/*
 * File: ui/web/src/lib/layout.test.ts
 * Purpose: Proves that rail and terminal sizes stay within their limits, survive a reload, and reset
 * Audience: Agents / CI
 * Update when: The layout limits or their storage change
 */

import { describe, expect, it } from "vitest";
import { DEFAULT_LAYOUT, LAYOUT_LIMITS, clampSize, clearLayout, readLayout, writeLayout, type LayoutRegion } from "@/lib/layout";

const REGIONS: LayoutRegion[] = ["leftRail", "rightRail", "terminal"];

// Proves F-068 FR-63 (docs/paperwork/steps/F-068.md).
describe("layout", () => {
  it("keeps every size within its limits", () => {
    for (const region of REGIONS) {
      const { min, max } = LAYOUT_LIMITS[region];
      expect(clampSize(region, min - 500)).toBe(min);
      expect(clampSize(region, max + 500)).toBe(max);
      expect(clampSize(region, (min + max) / 2)).toBe(Math.round((min + max) / 2));
      expect(clampSize(region, Number.NaN)).toBe(DEFAULT_LAYOUT[region]);
      expect(DEFAULT_LAYOUT[region]).toBeGreaterThanOrEqual(min);
      expect(DEFAULT_LAYOUT[region]).toBeLessThanOrEqual(max);
    }
  });

  it("survives a reload, and a stored size out of range comes back clamped", () => {
    writeLayout({ leftRail: 300, rightRail: 400, terminal: 200 });
    expect(readLayout()).toEqual({ leftRail: 300, rightRail: 400, terminal: 200 });

    window.localStorage.setItem("aethelgard.layout.terminal", "99999");
    expect(readLayout().terminal).toBe(LAYOUT_LIMITS.terminal.max);
  });

  it("resets to the first sizes", () => {
    writeLayout({ leftRail: 300, rightRail: 400, terminal: 200 });
    clearLayout();
    expect(readLayout()).toEqual(DEFAULT_LAYOUT);
  });
});
