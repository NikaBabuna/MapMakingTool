/*
 * File: product/src/main/java/com/aethelgard/product/Plates.java
 * Purpose: Step-0 two-plate partition from WorldSpec.seed
 * Audience: ProductHost / CollisionUplift / tests
 * Update when: Plate-seed geometry rule changes
 */

package com.aethelgard.product;

/**
 * Two-plate seed: a vertical suture from {@code seed}. Wiki: {@code docs/product/wiki/elevation.md}.
 */
public final class Plates {

  private Plates() {}

  /**
   * Exclusive east edge of plate 0: cells with {@code x < boundary} are plate 0; {@code x >=
   * boundary} are plate 1. When {@code width == 1} the boundary is {@code 1} (every cell plate 0,
   * no suture).
   */
  public static int boundaryX(int width, long seed) {
    if (width < 1) {
      throw new IllegalArgumentException("width must be >= 1, was " + width);
    }
    if (width == 1) {
      return 1;
    }
    return 1 + (int) Math.floorMod(seed, (long) (width - 1));
  }

  /** Plate-id grid for {@code width} × {@code height} from {@code seed}. */
  public static Grid seed(int width, int height, long seed) {
    if (height < 1) {
      throw new IllegalArgumentException("height must be >= 1, was " + height);
    }
    int boundary = boundaryX(width, seed);
    int[][] cells = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        cells[y][x] = x < boundary ? 0 : 1;
      }
    }
    return new Grid(cells);
  }

  /** True when a 4-neighbor has a different plate id. */
  public static boolean hasForeignNeighbor(Grid plates, int x, int y) {
    int id = plates.get(x, y);
    return different(plates, x - 1, y, id)
        || different(plates, x + 1, y, id)
        || different(plates, x, y - 1, id)
        || different(plates, x, y + 1, id);
  }

  private static boolean different(Grid plates, int x, int y, int id) {
    if (x < 0 || y < 0 || x >= plates.width() || y >= plates.height()) {
      return false;
    }
    return plates.get(x, y) != id;
  }
}
