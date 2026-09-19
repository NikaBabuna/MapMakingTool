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
 * Deterministic RGB image of a map layer. One packed {@code 0xRRGGBB} per cell in a flat {@code
 * int[]} (row-major). Paint may reuse a caller buffer when dimensions match (F-047).
 *
 * <p>Physical atlas ramp (F-052): brighter land stops clamp 64; bathymetry for negatives; hillshade
 * on land for Elevation and Overlay. Formulas: product architecture + this class.
 */
public final class ElevationRaster {

  public static final int CLAMP = 64;
  /** Deepest bathymetry stop (e ≤ −64). */
  public static final int OCEAN_FLOOR = -64;
  /**
   * Shallow ocean swatch (e = −1) — legacy {@code OCEAN_RGB} name kept for tests/legend that want a
   * single sea chip.
   */
  public static final int OCEAN_RGB = pack(72, 128, 168);
  /** Bathymetry stops (e = −64, −32, −16, −8, −1). */
  public static final int[] OCEAN_STOP_E = {-64, -32, -16, -8, -1};

  public static final int[] OCEAN_STOP_R = {18, 32, 48, 60, 72};
  public static final int[] OCEAN_STOP_G = {48, 72, 96, 112, 128};
  public static final int[] OCEAN_STOP_B = {78, 108, 132, 152, 168};
  /** Landstops for piecewise atlas ramp (e = 0, 12, 24, 40, 64). */
  public static final int[] LAND_STOP_E = {0, 12, 24, 40, 64};

  public static final int[] LAND_STOP_R = {142, 168, 196, 214, 248};
  public static final int[] LAND_STOP_G = {168, 178, 168, 176, 236};
  public static final int[] LAND_STOP_B = {118, 128, 118, 128, 210};
  public static final int HILLSHADE_FLAT = 12;
  public static final int HILLSHADE_MIN = 6;
  public static final int HILLSHADE_MAX = 18;
  public static final long PLATE_GOLDEN = 0x9E3779B97F4A7C15L;
  /** Plates-layer interior fill (muted gray). */
  public static final int PLATE_INTERIOR_RGB = pack(88, 92, 96);
  /** Plates-layer boundary stroke. */
  public static final int PLATE_BOUNDARY_RGB = pack(36, 38, 42);

  private final int width;
  private final int height;
  private final int[] rgb;

  private ElevationRaster(int width, int height, int[] rgb) {
    this.width = width;
    this.height = height;
    this.rgb = rgb;
  }

  /** Elevation layer (ocean + hillshaded land). */
  public static ElevationRaster of(Grid elevation) {
    return paint(elevation, null, MapLayer.ELEVATION);
  }

  /** Raster of {@code elevation} and {@code plates} for {@code layer} (allocates a new buffer). */
  public static ElevationRaster paint(Grid elevation, Grid plates, MapLayer layer) {
    return paint(elevation, plates, layer, null);
  }

  /**
   * Raster into {@code reuse} when non-null and {@code length == width * height}; otherwise
   * allocates. Same formulas as {@link #paint(Grid, Grid, MapLayer)}.
   */
  public static ElevationRaster paint(Grid elevation, Grid plates, MapLayer layer, int[] reuse) {
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
    int need = w * h;
    int[] cells = (reuse != null && reuse.length == need) ? reuse : new int[need];
    for (int y = 0; y < h; y++) {
      int row = y * w;
      for (int x = 0; x < w; x++) {
        cells[row + x] =
            switch (layer) {
              case ELEVATION -> elevationCell(elevation, x, y);
              case PLATES -> plateBoundaryCell(plates, x, y);
              case OVERLAY -> overlayCell(elevation, plates, x, y);
            };
      }
    }
    return new ElevationRaster(w, h, cells);
  }

  /**
   * Unshaded cell color: bathymetry if {@code elevation < 0}, else physical land ramp (clamp 64).
   *
   * <p>Ocean interpolates stops at e = −64…−1. Land interpolates atlas stops at e = 0…64 (F-052).
   */
  public static int rgbOf(int elevation) {
    if (elevation < 0) {
      return oceanRamp(elevation);
    }
    return landRamp(elevation);
  }

  /** Bathymetry ramp; {@code elevation} is treated as {@code max(OCEAN_FLOOR, min(e, -1))}. */
  public static int oceanRamp(int elevation) {
    int e = elevation;
    if (e > -1) {
      e = -1;
    } else if (e < OCEAN_FLOOR) {
      e = OCEAN_FLOOR;
    }
    for (int i = 0; i < OCEAN_STOP_E.length - 1; i++) {
      int e0 = OCEAN_STOP_E[i];
      int e1 = OCEAN_STOP_E[i + 1];
      if (e <= e1) {
        int span = e1 - e0;
        int t = span == 0 ? 0 : e - e0;
        int r = OCEAN_STOP_R[i] + ((OCEAN_STOP_R[i + 1] - OCEAN_STOP_R[i]) * t) / span;
        int g = OCEAN_STOP_G[i] + ((OCEAN_STOP_G[i + 1] - OCEAN_STOP_G[i]) * t) / span;
        int b = OCEAN_STOP_B[i] + ((OCEAN_STOP_B[i + 1] - OCEAN_STOP_B[i]) * t) / span;
        return pack(r, g, b);
      }
    }
    return pack(
        OCEAN_STOP_R[OCEAN_STOP_R.length - 1],
        OCEAN_STOP_G[OCEAN_STOP_G.length - 1],
        OCEAN_STOP_B[OCEAN_STOP_B.length - 1]);
  }

  /** Physical atlas land ramp; {@code elevation} is treated as {@code max(0, min(e, 64))}. */
  public static int landRamp(int elevation) {
    int e = elevation;
    if (e < 0) {
      e = 0;
    } else if (e > CLAMP) {
      e = CLAMP;
    }
    for (int i = 0; i < LAND_STOP_E.length - 1; i++) {
      int e0 = LAND_STOP_E[i];
      int e1 = LAND_STOP_E[i + 1];
      if (e <= e1) {
        int span = e1 - e0;
        int t = span == 0 ? 0 : e - e0;
        int r = LAND_STOP_R[i] + ((LAND_STOP_R[i + 1] - LAND_STOP_R[i]) * t) / span;
        int g = LAND_STOP_G[i] + ((LAND_STOP_G[i + 1] - LAND_STOP_G[i]) * t) / span;
        int b = LAND_STOP_B[i] + ((LAND_STOP_B[i + 1] - LAND_STOP_B[i]) * t) / span;
        return pack(r, g, b);
      }
    }
    return pack(LAND_STOP_R[LAND_STOP_R.length - 1], LAND_STOP_G[LAND_STOP_G.length - 1], LAND_STOP_B[LAND_STOP_B.length - 1]);
  }

  /**
   * Plate-id color (kept for tests / legacy). Plates layer uses {@link #plateBoundaryCell}.
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

  /**
   * Half-edge core (F-044): true when plate id differs from east or south neighbor under
   * {@link com.aethelgard.product.SphereTopology}.
   */
  public static boolean isPlateBoundaryCore(Grid plates, int x, int y) {
    Objects.requireNonNull(plates, "plates");
    int id = plates.get(x, y);
    int width = plates.width();
    int height = plates.height();
    int[] east = com.aethelgard.product.SphereTopology.neighbor(x, y, 1, 0, width, height);
    if (plates.get(east[0], east[1]) != id) {
      return true;
    }
    int[] south = com.aethelgard.product.SphereTopology.neighbor(x, y, 0, 1, width, height);
    if (plates.get(south[0], south[1]) != id) {
      return true;
    }
    return false;
  }

  /**
   * Bold border (F-045 P1): core half-edge plus orthogonal dilation so stroke is ≥2 cells — stays
   * visible when the canvas nearest-neighbor scales down.
   */
  public static boolean isPlateBoundary(Grid plates, int x, int y) {
    if (isPlateBoundaryCore(plates, x, y)) {
      return true;
    }
    int width = plates.width();
    int height = plates.height();
    for (int[] d : com.aethelgard.product.SphereTopology.ORTHO) {
      int[] n = com.aethelgard.product.SphereTopology.neighbor(x, y, d[0], d[1], width, height);
      if (isPlateBoundaryCore(plates, n[0], n[1])) {
        return true;
      }
    }
    return false;
  }

  /** Plates-layer pixel: dark boundary stroke on gray interior. */
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
   * Elevation-layer pixel: ocean or hillshaded land. West/north use {@link
   * com.aethelgard.product.SphereTopology}.
   */
  public static int elevationCell(Grid elevation, int x, int y) {
    int e = elevation.get(x, y);
    if (e < 0) {
      return oceanRamp(e);
    }
    int rgb = landRamp(e);
    int width = elevation.width();
    int height = elevation.height();
    int[] west = com.aethelgard.product.SphereTopology.neighbor(x, y, -1, 0, width, height);
    int[] north = com.aethelgard.product.SphereTopology.neighbor(x, y, 0, -1, width, height);
    int dw = e - elevation.get(west[0], west[1]);
    int dn = e - elevation.get(north[0], north[1]);
    return applyHillshade(rgb, hillshadeLit(dw, dn));
  }

  /**
   * Overlay pixel: elevation paint, then darken on bold sphere half-edge (same as Plates stroke
   * core+dilate).
   */
  public static int overlayCell(Grid elevation, Grid plates, int x, int y) {
    int rgb = elevationCell(elevation, x, y);
    if (isPlateBoundary(plates, x, y)) {
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
    return rgb[y * width + x];
  }

  /** True when this raster's backing store is exactly {@code buffer} (reuse witness). */
  public boolean usesBuffer(int[] buffer) {
    return rgb == buffer;
  }

  /** Row-major packed RGB (length {@code width * height}). */
  public int[] pixels() {
    return rgb;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ElevationRaster other)) {
      return false;
    }
    return width == other.width && height == other.height && Arrays.equals(rgb, other.rgb);
  }

  @Override
  public int hashCode() {
    int result = width;
    result = 31 * result + height;
    result = 31 * result + Arrays.hashCode(rgb);
    return result;
  }

  static int pack(int r, int g, int b) {
    return (r << 16) | (g << 8) | b;
  }
}
