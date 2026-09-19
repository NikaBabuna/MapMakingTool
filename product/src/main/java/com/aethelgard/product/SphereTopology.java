/*
 * File: product/src/main/java/com/aethelgard/product/SphereTopology.java
 * Purpose: Sphere-on-rectangle polar wrap — neighbors and steps (F-045)
 * Audience: Product tectonics / UI paint / tests
 * Update when: Polar wrap rule changes
 */

package com.aethelgard.product;

/**
 * Sphere-on-rectangle topology (G-009 / ADR-012): X wraps with {@code floorMod}; crossing north
 * re-enters at antipodal longitude ({@code x + width/2}) with heading flip of both {@code vx} and
 * {@code vy}; same for south. Not a 3D globe mesh.
 */
public final class SphereTopology {

  private SphereTopology() {}

  /** Antipodal longitude. */
  public static int antipodeX(int x, int width) {
    return Math.floorMod(x + width / 2, width);
  }

  /**
   * Step from {@code (x,y)} by one cell in direction {@code (dx,dy)} where each component is in
   * {@code {-1,0,1}}. Returns packed {@code nx, ny} — never off-map.
   */
  public static int[] neighbor(int x, int y, int dx, int dy, int width, int height) {
    if (dx == 0 && dy == 0) {
      return new int[] {x, y};
    }
    int nx = x;
    int ny = y;
    if (dx != 0) {
      nx = Math.floorMod(x + dx, width);
    }
    if (dy < 0) {
      if (y + dy >= 0) {
        ny = y + dy;
      } else {
        // Cross north pole → antipodal x, stay on north edge row.
        nx = antipodeX(nx, width);
        ny = 0;
      }
    } else if (dy > 0) {
      if (y + dy < height) {
        ny = y + dy;
      } else {
        nx = antipodeX(nx, width);
        ny = height - 1;
      }
    }
    return new int[] {nx, ny};
  }

  /**
   * Translate {@code (x,y)} by plate velocity {@code (vx,vy)} for one generation. If the step
   * crosses a pole, apply antipodal X and flip both velocity components (B2). Returns {@code
   * [nx, ny, vxOut, vyOut]}.
   */
  public static int[] advectCell(int x, int y, int vx, int vy, int width, int height) {
    int nx = Math.floorMod(x + vx, width);
    int ny = y + vy;
    int ovx = vx;
    int ovy = vy;
    if (ny < 0) {
      nx = antipodeX(x + vx, width);
      ny = 0;
      ovx = -vx;
      ovy = -vy;
    } else if (ny >= height) {
      nx = antipodeX(x + vx, width);
      ny = height - 1;
      ovx = -vx;
      ovy = -vy;
    }
    return new int[] {nx, ny, ovx, ovy};
  }

  /** Four orthogonal directions: E, W, S, N as {@code {dx,dy}}. */
  public static final int[][] ORTHO = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
}
