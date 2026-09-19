/*
 * File: product/src/main/java/com/aethelgard/product/PlateRegistry.java
 * Purpose: STATIC per-plate actor registry (area + initial velocity)
 * Audience: ProductHost / session / tests
 * Update when: Registry fields or seed rule changes
 */

package com.aethelgard.product;

import java.util.Arrays;
import java.util.Objects;

/**
 * Per-plate actor data at Step 0: cell {@code area} and initial {@code (vx, vy)}. Wiki: {@code
 * docs/product/wiki/tectonics.md}. Edge-driven velocity integrate is F-037; until then Constant
 * {@link PlateVelocities} remains the motion source and is seeded in lockstep.
 */
public final class PlateRegistry {

  private final long seed;
  private final int[] area;
  private final int[] vx;
  private final int[] vy;

  public PlateRegistry(long seed, int[] area, int[] vx, int[] vy) {
    Objects.requireNonNull(area, "area");
    Objects.requireNonNull(vx, "vx");
    Objects.requireNonNull(vy, "vy");
    if (area.length < 1 || area.length != vx.length || area.length != vy.length) {
      throw new IllegalArgumentException(
          "area/vx/vy must be the same length >= 1, was "
              + area.length
              + " / "
              + vx.length
              + " / "
              + vy.length);
    }
    for (int i = 0; i < area.length; i++) {
      if (area[i] < 0) {
        throw new IllegalArgumentException("area[" + i + "] must be >= 0, was " + area[i]);
      }
      requireUnit(vx[i], "vx[" + i + "]");
      requireUnit(vy[i], "vy[" + i + "]");
    }
    this.seed = seed;
    this.area = Arrays.copyOf(area, area.length);
    this.vx = Arrays.copyOf(vx, vx.length);
    this.vy = Arrays.copyOf(vy, vy.length);
  }

  /** Build registry from a Step-0 plates grid and velocities (same N). */
  public static PlateRegistry from(Grid plates, PlateVelocities velocities) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(velocities, "velocities");
    int n = velocities.count();
    int[] area = new int[n];
    for (int y = 0; y < plates.height(); y++) {
      for (int x = 0; x < plates.width(); x++) {
        int id = plates.get(x, y);
        if (id < 0 || id >= n) {
          throw new IllegalStateException(
              "plate id " + id + " out of 0.." + (n - 1) + " at (" + x + "," + y + ")");
        }
        area[id]++;
      }
    }
    int[] vx = new int[n];
    int[] vy = new int[n];
    for (int i = 0; i < n; i++) {
      vx[i] = velocities.vx(i);
      vy[i] = velocities.vy(i);
    }
    return new PlateRegistry(velocities.seed(), area, vx, vy);
  }

  public long seed() {
    return seed;
  }

  public int count() {
    return area.length;
  }

  public int area(int plateIndex) {
    return area[requirePlate(plateIndex)];
  }

  public int vx(int plateIndex) {
    return vx[requirePlate(plateIndex)];
  }

  public int vy(int plateIndex) {
    return vy[requirePlate(plateIndex)];
  }

  /** Total cells accounted for (should equal width×height when fully covered). */
  public int totalArea() {
    int sum = 0;
    for (int a : area) {
      sum += a;
    }
    return sum;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PlateRegistry other)) {
      return false;
    }
    return seed == other.seed
        && Arrays.equals(area, other.area)
        && Arrays.equals(vx, other.vx)
        && Arrays.equals(vy, other.vy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(seed, Arrays.hashCode(area), Arrays.hashCode(vx), Arrays.hashCode(vy));
  }

  private int requirePlate(int plateIndex) {
    if (plateIndex < 0 || plateIndex >= area.length) {
      throw new IllegalArgumentException(
          "plateIndex " + plateIndex + " out of 0.." + (area.length - 1));
    }
    return plateIndex;
  }

  private static void requireUnit(int component, String name) {
    if (component < -1 || component > 1) {
      throw new IllegalArgumentException(name + " must be in {-1,0,1}, was " + component);
    }
  }
}
