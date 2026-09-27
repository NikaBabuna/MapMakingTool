/*
 * File: product/src/main/java/com/aethelgard/product/world/fields/Occupancy.java
 * Purpose: Cell → locker-id grid helpers
 * Audience: ProductHost / GeometryApplication / tests
 * Update when: Occupancy seed or key rule changes
 */

package com.aethelgard.product.world.fields;

/**
 * Occupancy keys: each cell stores a locker id. Distinct from {@code plates} (who owns the cell).
 */
public final class Occupancy {

  private Occupancy() {}

  /**
   * Step-0 keys: one locker per cell, id {@code y * width + x}.
   */
  public static Grid seed(int width, int height) {
    if (width < 1 || height < 1) {
      throw new IllegalArgumentException(
          "width and height must be >= 1, was " + width + "x" + height);
    }
    int[][] cells = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        cells[y][x] = id(x, y, width);
      }
    }
    return new Grid(cells);
  }

  public static int id(int x, int y, int width) {
    return y * width + x;
  }

  public static int count(int width, int height) {
    return width * height;
  }
}
