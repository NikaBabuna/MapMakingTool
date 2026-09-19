/*
 * File: product/src/main/java/com/aethelgard/product/Plates.java
 * Purpose: Step-0 toroidal nearest-site plate partition from WorldSpec.seed
 * Audience: ProductHost / Orogeny / tests
 * Update when: Plate-seed geometry rule changes
 */

package com.aethelgard.product;

/**
 * Toroidal-X / clipped-Y nearest-site plate seed from {@code seed}. Wiki: {@code
 * docs/product/wiki/tectonics.md}.
 *
 * <p>Site count is {@code 12 + floorMod(seed, 13)} (12–24). Each cell takes the nearest site under
 * cylindrical distance (wrap X, flat Y); ties take the lower site index.
 */
public final class Plates {

  private static final long MIX_GOLDEN = 0x9E3779B97F4A7C15L;
  private static final long MIX_SILVER = 0xBF58476D1CE4E5B9L;
  private static final long MIX_BRONZE = 0x94D049BB133111EBL;

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
   * Assign each cell the nearest site index under cylindrical distance (wrap X, flat Y). {@code
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
        long bestD2 = dist2Cylinder(x, y, siteX[0], siteY[0], width);
        for (int i = 1; i < n; i++) {
          long d2 = dist2Cylinder(x, y, siteX[i], siteY[i], width);
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
    return different(plates, x - 1, y, id)
        || different(plates, x + 1, y, id)
        || different(plates, x, y - 1, id)
        || different(plates, x, y + 1, id);
  }

  /** Cylindrical squared Euclidean distance (wrap X; flat Y). */
  public static long dist2Cylinder(int x, int y, int sx, int sy, int width) {
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

  private static boolean different(Grid plates, int x, int y, int id) {
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
