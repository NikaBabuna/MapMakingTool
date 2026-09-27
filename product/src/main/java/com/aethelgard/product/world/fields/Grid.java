/*
 * File: product/src/main/java/com/aethelgard/product/world/fields/Grid.java
 * Purpose: Immutable 2D int layer stored in the Pool
 * Audience: Product host / Systems / tests
 * Update when: Grid cell type or copy rules change
 */

package com.aethelgard.product.world.fields;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable rectangular grid of {@code int} cells. {@code x} is column {@code [0, width)}, {@code y}
 * is row {@code [0, height)}.
 */
public final class Grid {

  private final int width;
  private final int height;
  private final int[][] cells;

  /**
   * Copies {@code cells}. Every row must have the same length; width and height must be ≥ 1.
   *
   * @param cells row-major values (must not be {@code null})
   */
  public Grid(int[][] cells) {
    Objects.requireNonNull(cells, "cells");
    if (cells.length < 1) {
      throw new IllegalArgumentException("height must be >= 1");
    }
    int w = cells[0].length;
    if (w < 1) {
      throw new IllegalArgumentException("width must be >= 1");
    }
    int[][] copy = new int[cells.length][w];
    for (int y = 0; y < cells.length; y++) {
      if (cells[y] == null || cells[y].length != w) {
        throw new IllegalArgumentException("all rows must have width " + w);
      }
      System.arraycopy(cells[y], 0, copy[y], 0, w);
    }
    this.width = w;
    this.height = cells.length;
    this.cells = copy;
  }

  /** Grid of {@code width} × {@code height} filled with {@code 0}. */
  public static Grid zeros(int width, int height) {
    if (width < 1 || height < 1) {
      throw new IllegalArgumentException(
          "width and height must be >= 1, was " + width + "x" + height);
    }
    return new Grid(new int[height][width]);
  }

  public int width() {
    return width;
  }

  public int height() {
    return height;
  }

  public int get(int x, int y) {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      throw new IllegalArgumentException(
          "cell (" + x + "," + y + ") out of " + width + "x" + height);
    }
    return cells[y][x];
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Grid other)) {
      return false;
    }
    return width == other.width && height == other.height && Arrays.deepEquals(cells, other.cells);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(cells);
  }
}
