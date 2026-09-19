/*
 * File: product/src/main/java/com/aethelgard/product/PlateVelocities.java
 * Purpose: STATIC per-plate integer velocities (seed + edge-driven integrate)
 * Audience: ProductHost / PlateKinematics / IntegrateVelocity / tests
 * Update when: Velocity seed or unit clamp rule changes
 */

package com.aethelgard.product;

import java.util.Arrays;
import java.util.Objects;

/**
 * Per-plate {@code (vx, vy)} in {@code {-1, 0, 1}}. Seeded at Step 0 from {@code seed}; nudged each
 * generation by {@link IntegrateVelocity}. Wiki: {@code docs/product/wiki/elevation.md}.
 *
 * <p>Axis {@code 2} is {@code vx}; axis {@code 3} is {@code vy}. If every plate would be {@code
 * (0, 0)}, plate {@code 0} is forced to {@code (1, 0)}.
 */
public final class PlateVelocities {

  /** Mix axis for east–west velocity. */
  public static final int AXIS_VX = 2;

  /** Mix axis for north–south velocity. */
  public static final int AXIS_VY = 3;

  private final long seed;
  private final int[] vx;
  private final int[] vy;

  public PlateVelocities(long seed, int[] vx, int[] vy) {
    Objects.requireNonNull(vx, "vx");
    Objects.requireNonNull(vy, "vy");
    if (vx.length < 1 || vx.length != vy.length) {
      throw new IllegalArgumentException(
          "vx and vy must be the same length >= 1, was " + vx.length + " / " + vy.length);
    }
    for (int i = 0; i < vx.length; i++) {
      requireUnit(vx[i], "vx[" + i + "]");
      requireUnit(vy[i], "vy[" + i + "]");
    }
    this.seed = seed;
    this.vx = Arrays.copyOf(vx, vx.length);
    this.vy = Arrays.copyOf(vy, vy.length);
  }

  /** Velocities for {@code N = Plates.count(seed)} plates. */
  public static PlateVelocities seed(long seed) {
    int n = Plates.count(seed);
    int[] vx = new int[n];
    int[] vy = new int[n];
    boolean anyMove = false;
    for (int i = 0; i < n; i++) {
      vx[i] = unit(Plates.mix(seed, i, AXIS_VX));
      vy[i] = unit(Plates.mix(seed, i, AXIS_VY));
      if (vx[i] != 0 || vy[i] != 0) {
        anyMove = true;
      }
    }
    if (!anyMove) {
      vx[0] = 1;
    }
    return new PlateVelocities(seed, vx, vy);
  }

  /** {@code mix} remainder in {@code {-1, 0, 1}}. */
  public static int unit(long mixed) {
    return (int) Math.floorMod(mixed, 3L) - 1;
  }

  public long seed() {
    return seed;
  }

  public int count() {
    return vx.length;
  }

  public int vx(int plateIndex) {
    return vx[requirePlate(plateIndex)];
  }

  public int vy(int plateIndex) {
    return vy[requirePlate(plateIndex)];
  }

  /** True when at least one plate has a non-zero component. */
  public boolean anyMoving() {
    for (int i = 0; i < vx.length; i++) {
      if (vx[i] != 0 || vy[i] != 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Site {@code plateIndex} column after {@code generationIndex} generation Steps (wrap X).
   * {@code generationIndex == 0} is the Step-0 site.
   */
  public int movedSiteX(int width, int plateIndex, int generationIndex) {
    requireGeneration(generationIndex);
    int orig = Plates.siteX(width, seed, plateIndex);
    return wrapX(orig, vx(plateIndex), generationIndex, width);
  }

  /**
   * Site {@code plateIndex} row after {@code generationIndex} generation Steps (Y clipped to
   * {@code [0, height)} — polar edge, no wrap).
   */
  public int movedSiteY(int height, int plateIndex, int generationIndex) {
    requireGeneration(generationIndex);
    int orig = Plates.siteY(height, seed, plateIndex);
    return clampY(orig, vy(plateIndex), generationIndex, height);
  }

  static int wrapX(int origin, int velocity, int generationIndex, int period) {
    return (int) Math.floorMod(origin + (long) generationIndex * velocity, (long) period);
  }

  static int clampY(int origin, int velocity, int generationIndex, int height) {
    long y = (long) origin + (long) generationIndex * velocity;
    if (y < 0L) {
      return 0;
    }
    if (y >= height) {
      return height - 1;
    }
    return (int) y;
  }

  /** @deprecated use {@link #wrapX} */
  static int wrap(int origin, int velocity, int generationIndex, int period) {
    return wrapX(origin, velocity, generationIndex, period);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PlateVelocities other)) {
      return false;
    }
    return seed == other.seed && Arrays.equals(vx, other.vx) && Arrays.equals(vy, other.vy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(seed, Arrays.hashCode(vx), Arrays.hashCode(vy));
  }

  private int requirePlate(int plateIndex) {
    if (plateIndex < 0 || plateIndex >= vx.length) {
      throw new IllegalArgumentException(
          "plateIndex " + plateIndex + " out of 0.." + (vx.length - 1));
    }
    return plateIndex;
  }

  private static void requireUnit(int component, String name) {
    if (component < -1 || component > 1) {
      throw new IllegalArgumentException(name + " must be in {-1,0,1}, was " + component);
    }
  }

  private static void requireGeneration(int generationIndex) {
    if (generationIndex < 0) {
      throw new IllegalArgumentException("generationIndex must be >= 0, was " + generationIndex);
    }
  }
}
