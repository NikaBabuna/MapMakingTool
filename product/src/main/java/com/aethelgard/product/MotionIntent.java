/*
 * File: product/src/main/java/com/aethelgard/product/MotionIntent.java
 * Purpose: STATIC per-plate preferred Δv from classified boundaries
 * Audience: ProductHost / BoundaryInteraction / tests
 * Update when: Motion intent rules change
 */

package com.aethelgard.product;

import java.util.Arrays;
import java.util.Objects;

/**
 * Preferred edge-driven velocity nudge per plate (integrated by {@link IntegrateVelocity}). Wiki:
 * {@code docs/product/wiki/tectonics.md}.
 */
public final class MotionIntent {

  private final int[] ix;
  private final int[] iy;

  public MotionIntent(int[] ix, int[] iy) {
    Objects.requireNonNull(ix, "ix");
    Objects.requireNonNull(iy, "iy");
    if (ix.length < 1 || ix.length != iy.length) {
      throw new IllegalArgumentException(
          "ix/iy must be the same length >= 1, was " + ix.length + " / " + iy.length);
    }
    this.ix = Arrays.copyOf(ix, ix.length);
    this.iy = Arrays.copyOf(iy, iy.length);
  }

  public static MotionIntent zeros(int plateCount) {
    if (plateCount < 1) {
      throw new IllegalArgumentException("plateCount must be >= 1, was " + plateCount);
    }
    return new MotionIntent(new int[plateCount], new int[plateCount]);
  }

  /** Intent from standing boundaries + registry (F-035 locks). */
  public static MotionIntent from(Boundaries boundaries, PlateRegistry registry) {
    Objects.requireNonNull(boundaries, "boundaries");
    Objects.requireNonNull(registry, "registry");
    int n = registry.count();
    int[] ix = new int[n];
    int[] iy = new int[n];
    for (BoundaryContact c : boundaries.contacts()) {
      int nx = c.nx();
      int ny = c.ny();
      switch (c.kind()) {
        case SEPARATE -> {
          // Ridge push away from the contact
          ix[c.plateA()] -= nx;
          iy[c.plateA()] -= ny;
          ix[c.plateB()] += nx;
          iy[c.plateB()] += ny;
        }
        case COLLIDE -> {
          // Dampen closing (apart)
          ix[c.plateA()] -= nx;
          iy[c.plateA()] -= ny;
          ix[c.plateB()] += nx;
          iy[c.plateB()] += ny;
          // Slab pull on loser into the boundary
          int lose = AreaFlux.loser(c.plateA(), c.plateB(), registry);
          if (lose == c.plateA()) {
            ix[lose] += nx;
            iy[lose] += ny;
          } else {
            ix[lose] -= nx;
            iy[lose] -= ny;
          }
        }
        case PASS_BY -> {
          // no normal intent
        }
      }
    }
    return new MotionIntent(ix, iy);
  }

  public int plateCount() {
    return ix.length;
  }

  public int ix(int plateIndex) {
    return ix[requirePlate(plateIndex)];
  }

  public int iy(int plateIndex) {
    return iy[requirePlate(plateIndex)];
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MotionIntent other)) {
      return false;
    }
    return Arrays.equals(ix, other.ix) && Arrays.equals(iy, other.iy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Arrays.hashCode(ix), Arrays.hashCode(iy));
  }

  private int requirePlate(int plateIndex) {
    if (plateIndex < 0 || plateIndex >= ix.length) {
      throw new IllegalArgumentException(
          "plateIndex " + plateIndex + " out of 0.." + (ix.length - 1));
    }
    return plateIndex;
  }
}
