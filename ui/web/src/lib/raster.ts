/*
 * File: ui/web/src/lib/raster.ts
 * Purpose: Decode MapHost packed RGB raster into ImageData
 * Audience: MapCanvas
 * Update when: Raster wire format changes
 */

/** Big-endian width, height, then row-major packed 0xRRGGBB ints (matches MapHost.packRaster). */
export function decodePackedRaster(buffer: ArrayBuffer): {
  width: number;
  height: number;
  image: ImageData;
} {
  const view = new DataView(buffer);
  if (buffer.byteLength < 8) {
    throw new Error("raster too short");
  }
  const width = view.getInt32(0, false);
  const height = view.getInt32(4, false);
  const expected = 8 + width * height * 4;
  if (buffer.byteLength < expected || width <= 0 || height <= 0) {
    throw new Error(`bad raster ${width}x${height} len=${buffer.byteLength}`);
  }
  const image = new ImageData(width, height);
  let o = 8;
  let p = 0;
  for (let i = 0; i < width * height; i++) {
    const rgb = view.getInt32(o, false);
    o += 4;
    image.data[p++] = (rgb >> 16) & 0xff;
    image.data[p++] = (rgb >> 8) & 0xff;
    image.data[p++] = rgb & 0xff;
    image.data[p++] = 255;
  }
  return { width, height, image };
}

export function rgbCss(packed: number): string {
  const r = (packed >> 16) & 0xff;
  const g = (packed >> 8) & 0xff;
  const b = packed & 0xff;
  return `rgb(${r}, ${g}, ${b})`;
}
