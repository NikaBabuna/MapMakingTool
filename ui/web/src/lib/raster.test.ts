/*
 * File: ui/web/src/lib/raster.test.ts
 * Purpose: Proves that a decoded raster's pixels equal the host's packed colours
 * Audience: Agents / CI
 * Update when: The raster body format changes
 */

import { describe, expect, it } from "vitest";
import { decodePackedRaster, rgbCss } from "@/lib/raster";

/** The host's body: big-endian width, height, then one 0xRRGGBB int per cell, row by row. */
function pack(width: number, height: number, pixels: number[]): ArrayBuffer {
  const view = new DataView(new ArrayBuffer(8 + pixels.length * 4));
  view.setInt32(0, width, false);
  view.setInt32(4, height, false);
  pixels.forEach((rgb, i) => view.setInt32(8 + i * 4, rgb, false));
  return view.buffer;
}

// Proves F-068 FR-64 (docs/paperwork/steps/F-068.md).
describe("raster", () => {
  it("decodes the host's packing into equal pixels", () => {
    const pixels = [0x18_40_68, 0x96_c4_78, 0x24_26_2a, 0xff_fa_ec, 0x00_00_00, 0x6e_be_e2];

    const { width, height, image } = decodePackedRaster(pack(3, 2, pixels));

    expect([width, height]).toEqual([3, 2]);
    pixels.forEach((rgb, i) => {
      expect(Array.from(image.data.slice(i * 4, i * 4 + 4))).toEqual([(rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, 255]);
    });
    expect(rgbCss(0x24_26_2a)).toBe("rgb(36, 38, 42)");
  });

  it("refuses a body that is too short for its size", () => {
    expect(() => decodePackedRaster(new ArrayBuffer(4))).toThrow();
    expect(() => decodePackedRaster(pack(3, 2, [1, 2, 3]))).toThrow();
  });
});
