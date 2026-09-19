/*
 * File: ui/src/main/java/com/aethelgard/ui/ElevationRaster.java
 * Purpose: Headless map RGB raster — elevation, plates, overlay (no Swing)
 * Audience: MapController / tests
 * Update when: Paint formulas change
 */

package com.aethelgard.ui;

import com.aethelgard.product.Grid;
import java.util.Arrays;
import java.util.Objects;

/**
 * Deterministic RGB image of a map layer. One packed {@code 0xRRGGBB} per cell.
 *
 * <p>Land ramp (F-018) for {@code e >= 0}; ocean for negatives; hillshade on land for Elevation and
 * Overlay. Formulas: {@code docs/blockers/F-022.md} and product architecture.
 */
public final class ElevationRaster {

  public static final int CLAMP = 32;
  public static final int OCEAN_RGB = pack(18, 56, 92);
  public static final int DARK_R = 12;
  public static final int DARK_G = 10;
  public static final int DARK_B = 18;
  public static final int LIGHT_R = 255;
  public static final int LIGHT_G = 196;
  public static final int LIGHT_B = 96;
  public static final int HILLSHADE_FLAT = 12;
  public static final int HILLSHADE_MIN = 6;
  public static final int HILLSHADE_MAX = 18;
  public static final long PLATE_GOLDEN = 0x9E3779B97F4A7C15L;
  /** Plates layer: non-boundary fill (no per-id rainbow). */
  public static final int PLATE_INTERIOR_RGB = pack(200, 200, 200);
  /** Plates layer: cell that touches a foreign plate (cylinder 4-neighbor). */
  public static final int PLATE_BOUNDARY_RGB = pack(32, 32, 32);

  private final int width;
  private final int height;
  private final int[][] rgb;

  private ElevationRaster(int[][] rgb) {
    this.height = rgb.length;
    this.width = rgb[0].length;
    this.rgb = rgb;
  }

  /** Elevation layer (ocean + hillshaded land). */
  public static ElevationRaster of(Grid elevation) {
    return paint(elevation, null, MapLayer.ELEVATION);
  }

  /** Raster of {@code elevation} and {@code plates} for {@code layer}. */
  public static ElevationRaster paint(Grid elevation, Grid plates, MapLayer layer) {
    Objects.requireNonNull(elevation, "elevation");
    Objects.requireNonNull(layer, "layer");
    if (layer != MapLayer.ELEVATION) {
      Objects.requireNonNull(plates, "plates");
      if (plates.width() != elevation.width() || plates.height() != elevation.height()) {
        throw new IllegalArgumentException(
            "plates "
                + plates.width()
                + "x"
                + plates.height()
                + " != elevation "
                + elevation.width()
                + "x"
                + elevation.height());
      }
    }
    int w = elevation.width();
    int h = elevation.height();
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        cells[y][x] =
            switch (layer) {
              case ELEVATION -> elevationCell(elevation, x, y);
              case PLATES -> plateBoundaryCell(plates, x, y);
              case OVERLAY -> overlayCell(elevation, plates, x, y);
            };
      }
    }
    return new ElevationRaster(cells);
  }

  /**
   * Unshaded cell color: ocean if {@code elevation < 0}, else land ramp (clamp 32).
   *
   * <pre>
   *   e = min(elevation, 32)   // when elevation &gt;= 0
   *   R = 12 + (243 * e) / 32
   *   G = 10 + (186 * e) / 32
   *   B = 18 + (78 * e) / 32
   *   ocean = (18, 56, 92)
   * </pre>
   */
  public static int rgbOf(int elevation) {
    if (elevation < 0) {
      return OCEAN_RGB;
    }
    return landRamp(elevation);
  }

  /** F-018 land ramp; {@code elevation} is treated as {@code max(0, min(e, 32))}. */
  public static int landRamp(int elevation) {
    int e = elevation;
    if (e < 0) {
      e = 0;
    } else if (e > CLAMP) {
      e = CLAMP;
    }
    int r = DARK_R + ((LIGHT_R - DARK_R) * e) / CLAMP;
    int g = DARK_G + ((LIGHT_G - DARK_G) * e) / CLAMP;
    int b = DARK_B + ((LIGHT_B - DARK_B) * e) / CLAMP;
    return pack(r, g, b);
  }

  /**
   * Plate-id color (kept for tests / legacy; Plates layer uses {@link #plateBoundaryCell}).
   *
   * <pre>
   *   z = plateId * 0x9E3779B97F4A7C15
   *   z = z XOR (z &gt;&gt;&gt; 30)
   *   R = 48 + (z AND 0x7F)
   *   G = 48 + ((z &gt;&gt;&gt; 8) AND 0x7F)
   *   B = 48 + ((z &gt;&gt;&gt; 16) AND 0x7F)
   * </pre>
   */
  public static int plateRgb(int plateId) {
    long z = plateId * PLATE_GOLDEN;
    z ^= (z >>> 30);
    int r = 48 + (int) (z & 0x7F);
    int g = 48 + (int) ((z >>> 8) & 0x7F);
    int b = 48 + (int) ((z >>> 16) & 0x7F);
    return pack(r, g, b);
  }

  /** True when any cylinder 4-neighbor has a different plate id. */
  public static boolean isPlateBoundary(Grid plates, int x, int y) {
    int id = plates.get(x, y);
    int width = plates.width();
    int height = plates.height();
    if (id != plates.get(Math.floorMod(x + 1, width), y)) {
      return true;
    }
    if (id != plates.get(Math.floorMod(x - 1, width), y)) {
      return true;
    }
    if (y + 1 < height && id != plates.get(x, y + 1)) {
      return true;
    }
    if (y > 0 && id != plates.get(x, y - 1)) {
      return true;
    }
    return false;
  }

  /** Plates-layer pixel: gray interior, dark boundary. */
  public static int plateBoundaryCell(Grid plates, int x, int y) {
    return isPlateBoundary(plates, x, y) ? PLATE_BOUNDARY_RGB : PLATE_INTERIOR_RGB;
  }

  public static int hillshadeLit(int dw, int dn) {
    int lit = HILLSHADE_FLAT + 2 * dw + 2 * dn;
    if (lit < HILLSHADE_MIN) {
      return HILLSHADE_MIN;
    }
    if (lit > HILLSHADE_MAX) {
      return HILLSHADE_MAX;
    }
    return lit;
  }

  public static int applyHillshade(int rgb, int lit) {
    int r = Math.min(255, (((rgb >> 16) & 0xFF) * lit) / HILLSHADE_FLAT);
    int g = Math.min(255, (((rgb >> 8) & 0xFF) * lit) / HILLSHADE_FLAT);
    int b = Math.min(255, ((rgb & 0xFF) * lit) / HILLSHADE_FLAT);
    return pack(r, g, b);
  }

  /** Overlay suture: each channel {@code c / 3}. */
  public static int darken(int rgb) {
    int r = ((rgb >> 16) & 0xFF) / 3;
    int g = ((rgb >> 8) & 0xFF) / 3;
    int b = (rgb & 0xFF) / 3;
    return pack(r, g, b);
  }

  /**
   * Elevation-layer pixel: ocean or hillshaded land. West wraps on X; north is clipped (polar
   * edge — no Y wrap).
   */
  public static int elevationCell(Grid elevation, int x, int y) {
    int e = elevation.get(x, y);
    if (e < 0) {
      return OCEAN_RGB;
    }
    int rgb = landRamp(e);
    int west = Math.floorMod(x - 1, elevation.width());
    int dw = e - elevation.get(west, y);
    int dn = 0;
    if (y > 0) {
      dn = e - elevation.get(x, y - 1);
    }
    return applyHillshade(rgb, hillshadeLit(dw, dn));
  }

  /**
   * Overlay pixel: elevation paint, then darken if plate differs from east (wrap X) or south
   * (clipped Y) neighbor.
   */
  public static int overlayCell(Grid elevation, Grid plates, int x, int y) {
    int rgb = elevationCell(elevation, x, y);
    int id = plates.get(x, y);
    int east = Math.floorMod(x + 1, plates.width());
    boolean foreign = id != plates.get(east, y);
    if (y + 1 < plates.height() && id != plates.get(x, y + 1)) {
      foreign = true;
    }
    if (foreign) {
      return darken(rgb);
    }
    return rgb;
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

  static int pack(int r, int g, int b) {
    return (r << 16) | (g << 8) | b;
  }
}
