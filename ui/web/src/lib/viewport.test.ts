/*
 * File: ui/web/src/lib/viewport.test.ts
 * Purpose: Proves how the map camera behaves: fit, zoom limits toward the pointer, east-west wrap without vertical gaps, and cell picking across the seam
 * Audience: Agents / CI
 * Update when: The viewport rules change
 */

import { describe, expect, it } from "vitest";
import { MAX_SCALE, fittedViewport, panBy, stageToCell, zoomAt, type Viewport } from "@/lib/viewport";

// A 1920 x 1080 map drawn at display size on a 1000 x 700 stage.
const W = 1920;
const H = 1080;
const STAGE_W = 1000;
const STAGE_H = 700;

// Proves F-068 FR-62 (docs/paperwork/steps/F-068.md).
describe("viewport", () => {
  it("fit shows the whole map, centred", () => {
    const vp = fittedViewport(STAGE_W, STAGE_H, W, H);

    expect(W * vp.scale).toBeLessThanOrEqual(STAGE_W + 1e-9);
    expect(H * vp.scale).toBeLessThanOrEqual(STAGE_H + 1e-9);
    // One side fills the stage exactly, and the map sits in the middle.
    expect(Math.min(STAGE_W - W * vp.scale, STAGE_H - H * vp.scale)).toBeCloseTo(0, 9);
    expect(vp.tx).toBeCloseTo((STAGE_W - W * vp.scale) / 2, 9);
    expect(vp.ty).toBeCloseTo((STAGE_H - H * vp.scale) / 2, 9);
  });

  it("zoom stays between fit and 16, and keeps the point under the pointer", () => {
    const fit = fittedViewport(STAGE_W, STAGE_H, W, H);
    const pointer = { x: 400, y: 350 };
    const worldX = (pointer.x - fit.tx) / fit.scale;
    const worldY = (pointer.y - fit.ty) / fit.scale;

    const closer = zoomAt(fit, pointer.x, pointer.y, 3, fit.scale, W, H, STAGE_H);
    expect(closer.scale).toBeCloseTo(fit.scale * 3, 9);
    // The map point under the pointer stays under it (modulo one map width, since x wraps).
    const period = W * closer.scale;
    const drawnX = (((pointer.x - closer.tx) % period) + period) % period;
    expect(drawnX / closer.scale).toBeCloseTo(worldX, 6);
    expect((pointer.y - closer.ty) / closer.scale).toBeCloseTo(worldY, 6);

    expect(zoomAt(fit, 500, 350, 1e6, fit.scale, W, H, STAGE_H).scale).toBe(MAX_SCALE);
    expect(zoomAt(closer, 500, 350, 1e-6, fit.scale, W, H, STAGE_H).scale).toBe(fit.scale);
  });

  it("pan wraps east-west and never shows a gap above or below the map", () => {
    const fit = fittedViewport(STAGE_W, STAGE_H, W, H);
    const zoomed = zoomAt(fit, 500, 350, 4, fit.scale, W, H, STAGE_H);

    for (const [dx, dy] of [[5000, 0], [-7777, 0], [123, 9999], [-50, -9999], [0, 40]]) {
      const vp = panBy(zoomed, dx, dy, W, H, STAGE_H);
      const mapH = H * vp.scale;
      expect(vp.tx).toBeGreaterThanOrEqual(0);
      expect(vp.tx).toBeLessThan(W * vp.scale);
      // Zoomed in, the map covers the stage from top to bottom.
      expect(vp.ty).toBeLessThanOrEqual(0);
      expect(vp.ty + mapH).toBeGreaterThanOrEqual(STAGE_H);
    }

    // Zoomed out, the map stays centred vertically whatever the pan.
    const out = panBy(fit, 300, 500, W, H, STAGE_H);
    expect(out.ty).toBeCloseTo((STAGE_H - H * out.scale) / 2, 9);
  });

  it("a click picks the cell drawn under it, on both sides of the seam", () => {
    const worldW = 64;
    const worldH = 36;
    const vp: Viewport = { scale: 2, tx: 1000, ty: -100 };
    const cellW = (W / worldW) * vp.scale;
    const cellH = (H / worldH) * vp.scale;
    const period = W * vp.scale;

    for (const [cx, cy] of [[0, 0], [5, 7], [worldW - 1, 20], [31, worldH - 1]]) {
      for (const copy of [-1, 0, 1]) {
        const stageX = vp.tx + (cx + 0.5) * cellW + copy * period;
        const stageY = vp.ty + (cy + 0.5) * cellH;
        expect(stageToCell(vp, stageX, stageY, worldW, worldH, W, H)).toEqual({ x: cx, y: cy });
      }
    }
    // Just left and right of the seam are the last and the first column.
    expect(stageToCell(vp, vp.tx - 1, 500, worldW, worldH, W, H)?.x).toBe(worldW - 1);
    expect(stageToCell(vp, vp.tx + 1, 500, worldW, worldH, W, H)?.x).toBe(0);
    // Above the map there is no cell.
    expect(stageToCell({ scale: 0.4, tx: 0, ty: 50 }, 10, 10, worldW, worldH, W, H)).toBeNull();
  });
});
