/*
 * File: product/src/main/java/com/aethelgard/product/ElevationRaster.java
 * Purpose: Headless elevation → RGB raster (no Swing)
 * Audience: MapController / tests
 * Update when: Height color ramp changes
 */

package com.aethelgard.product;

import java.util.Arrays;
import java.util.Objects;

/**
 * Deterministic RGB image of an elevation {@link Grid}. One packed {@code 0xRRGGBB} per cell.
 * Absolute ramp: height 0 is dark; each +1 steps toward warm light; clamp at {@link #CLAMP}.
 */
public final class ElevationRaster {

  public static final int CLAMP = 32;
  public static final int DARK_R = 12;
  public static final int DARK_G = 10;
  public static final int DARK_B = 18;
  public static final int LIGHT_R = 255;
  public static final int LIGHT_G = 196;
  public static final int LIGHT_B = 96;

  private final int width;
  private final int height;
  private final int[][] rgb;

  private ElevationRaster(int[][] rgb) {
    this.height = rgb.length;
    this.width = rgb[0].length;
    this.rgb = rgb;
  }

  /** Raster of {@code elevation}; same geometry. */
  public static ElevationRaster of(Grid elevation) {
    Objects.requireNonNull(elevation, "elevation");
    int w = elevation.width();
    int h = elevation.height();
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        cells[y][x] = rgbOf(elevation.get(x, y));
      }
    }
    return new ElevationRaster(cells);
  }

  /**
   * Packed RGB for one height. Integer formula (truncating division):
   *
   * <pre>
   *   e = clamp(elevation, 0, 32)
   *   R = 12 + (243 * e) / 32
   *   G = 10 + (186 * e) / 32
   *   B = 18 + (78 * e) / 32
   * </pre>
   */
  public static int rgbOf(int elevation) {
    int e = elevation;
    if (e < 0) {
      e = 0;
    } else if (e > CLAMP) {
      e = CLAMP;
    }
    int r = DARK_R + ((LIGHT_R - DARK_R) * e) / CLAMP;
    int g = DARK_G + ((LIGHT_G - DARK_G) * e) / CLAMP;
    int b = DARK_B + ((LIGHT_B - DARK_B) * e) / CLAMP;
    return (r << 16) | (g << 8) | b;
  }

  public int width() {
    return width;
  }

  public int height() {
    return height;
  }

  public int rgb(int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      throw new IllegalArgumentException(
          "cell (" + x + "," + y + ") out of " + width + "x" + height);
    }
    return rgb[y][x];
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ElevationRaster other)) {
      return false;
    }
    return width == other.width && height == other.height && Arrays.deepEquals(rgb, other.rgb);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(rgb);
  }
}
