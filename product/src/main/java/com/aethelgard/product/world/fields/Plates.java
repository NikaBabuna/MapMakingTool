/*
 * File: product/src/main/java/com/aethelgard/product/world/fields/Plates.java
 * Purpose: Step-0 nearest-site plate partition from WorldSpec.seed
 * Audience: ProductHost / Orogeny / tests
 * Update when: Plate-seed geometry rule changes
 */

package com.aethelgard.product.world.fields;

/**
 * Cylinder nearest-site plate seed from {@code seed} with B1 latitude-weighted distance. Wiki:
 * {@code docs/product/wiki/tectonics.md}.
 *
 * <p>Site count is {@code 12 + floorMod(seed, 13)} (12–24). Each cell takes the nearest site under
 * wrap-X distance scaled by {@link #cosQ(int, int)}; ties take the lower site index.
 */
public final class Plates {

  private static final long MIX_GOLDEN = 0x9E3779B97F4A7C15L;
  private static final long MIX_SILVER = 0xBF58476D1CE4E5B9L;
  private static final long MIX_BRONZE = 0x94D049BB133111EBL;

  /** Fixed-point scale for {@link #cosQ(int, int)}. */
  public static final int COS_SCALE = 1024;

  private Plates() {}

  /** Site count \(N\) in {@code 12..24} from {@code seed}. */
  public static int count(long seed) {
    return 12 + (int) Math.floorMod(seed, 13L);
  }

  /**
   * SplitMix mix of {@code (seed, siteIndex, axis)} as documented in the wiki. Axis {@code 0} is
   * \(x\); axis {@code 1} is \(y\).
   */
  public static long mix(long seed, int siteIndex, int axis) {
    long z = seed;
    z ^= (long) siteIndex * MIX_GOLDEN;
    z ^= (long) axis * MIX_SILVER;
    return splitmix64(z);
  }

  /** Site \(i\) column in {@code [0, width)}. */
  public static int siteX(int width, long seed, int siteIndex) {
    requirePositive(width, "width");
    return (int) Math.floorMod(mix(seed, siteIndex, 0), (long) width);
  }

  /** Site \(i\) row in {@code [0, height)}. */
  public static int siteY(int height, long seed, int siteIndex) {
    requirePositive(height, "height");
    return (int) Math.floorMod(mix(seed, siteIndex, 1), (long) height);
  }

  /**
   * Equirectangular cosine weight at row {@code y}: {@code max(1, round(1024 *
   * sin(π*(y+0.5)/height)))}.
   */
  public static int cosQ(int y, int height) {
    requirePositive(height, "height");
    if (y < 0 || y >= height) {
      throw new IllegalArgumentException("y " + y + " out of 0.." + (height - 1));
    }
    double s = Math.sin(Math.PI * (y + 0.5) / height);
    int q = (int) Math.round(COS_SCALE * s);
    return Math.max(1, q);
  }

  /**
   * Assign each cell the nearest site index under B1 latitude-weighted cylindrical distance. {@code
   * siteX} and {@code siteY} must be the same length \(N \ge 1\). Ties take the lower index.
   */
  public static Grid assign(int width, int height, int[] siteX, int[] siteY) {
    requirePositive(width, "width");
    requirePositive(height, "height");
    if (siteX == null || siteY == null) {
      throw new NullPointerException("siteX and siteY");
    }
    if (siteX.length < 1 || siteX.length != siteY.length) {
      throw new IllegalArgumentException(
          "site arrays must be the same length >= 1, was "
              + siteX.length
              + " / "
              + siteY.length);
    }
    int n = siteX.length;
    int[][] cells = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int best = 0;
        long bestD2 = dist2(x, y, siteX[0], siteY[0], width, height);
        for (int i = 1; i < n; i++) {
          long d2 = dist2(x, y, siteX[i], siteY[i], width, height);
          if (d2 < bestD2) {
            bestD2 = d2;
            best = i;
          }
        }
        cells[y][x] = best;
      }
    }
    return new Grid(cells);
  }

  /** Plate-id grid for {@code width} × {@code height} from {@code seed}. */
  public static Grid seed(int width, int height, long seed) {
    requirePositive(height, "height");
    int n = count(seed);
    int[] xs = new int[n];
    int[] ys = new int[n];
    for (int i = 0; i < n; i++) {
      xs[i] = siteX(width, seed, i);
      ys[i] = siteY(height, seed, i);
    }
    return assign(width, height, xs, ys);
  }

  /** True when a 4-neighbor has a different plate id (clipped edges; orogeny uses its own wrap). */
  public static boolean hasForeignNeighbor(Grid plates, int x, int y) {
    int id = plates.get(x, y);
    return isDifferent(plates, x - 1, y, id)
        || isDifferent(plates, x + 1, y, id)
        || isDifferent(plates, x, y - 1, id)
        || isDifferent(plates, x, y + 1, id);
  }

  /**
   * B1 squared distance: wrap X, flat Y, east–west scaled by {@link #cosQ(int, int)} at the query
   * row.
   */
  public static long dist2(int x, int y, int sx, int sy, int width, int height) {
    long dx = toroidalDelta(x, sx, width);
    long dy = (long) y - (long) sy;
    long dxw = (dx * cosQ(y, height)) / COS_SCALE;
    return dxw * dxw + dy * dy;
  }

  /** @deprecated use {@link #dist2(int, int, int, int, int, int)} */
  public static long dist2Cylinder(int x, int y, int sx, int sy, int width) {
    // Height-agnostic fallback treats cosQ as COS_SCALE (no latitude squash).
    long dx = toroidalDelta(x, sx, width);
    long dy = (long) y - (long) sy;
    return dx * dx + dy * dy;
  }

  private static long toroidalDelta(int a, int b, int period) {
    long d = Math.abs((long) a - (long) b);
    long wrap = (long) period - d;
    return Math.min(d, wrap);
  }

  private static long splitmix64(long z) {
    z += MIX_GOLDEN;
    z = (z ^ (z >>> 30)) * MIX_SILVER;
    z = (z ^ (z >>> 27)) * MIX_BRONZE;
    return z ^ (z >>> 31);
  }

  private static boolean isDifferent(Grid plates, int x, int y, int id) {
    if (x < 0 || y < 0 || x >= plates.width() || y >= plates.height()) {
      return false;
    }
    return plates.get(x, y) != id;
  }

  private static void requirePositive(int value, String name) {
    if (value < 1) {
      throw new IllegalArgumentException(name + " must be >= 1, was " + value);
    }
  }
}
