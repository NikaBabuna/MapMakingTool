/*
 * File: product/src/main/java/com/aethelgard/product/AreaFlux.java
 * Purpose: STATIC per-plate area budgets + sink from classified boundaries
 * Audience: ProductHost / BoundaryInteraction / tests
 * Update when: Flux budget rules change
 */

package com.aethelgard.product;

import java.util.Arrays;
import java.util.Objects;

/**
 * Per-generation area create/destroy budgets before geometry apply (F-036). Wiki: {@code
 * docs/product/wiki/tectonics.md}.
 */
public final class AreaFlux {

  private final int[] deltaArea;
  private final int sinkDelta;

  public AreaFlux(int[] deltaArea, int sinkDelta) {
    Objects.requireNonNull(deltaArea, "deltaArea");
    if (deltaArea.length < 1) {
      throw new IllegalArgumentException("deltaArea length must be >= 1");
    }
    this.deltaArea = Arrays.copyOf(deltaArea, deltaArea.length);
    this.sinkDelta = sinkDelta;
  }

  public static AreaFlux zeros(int plateCount) {
    if (plateCount < 1) {
      throw new IllegalArgumentException("plateCount must be >= 1, was " + plateCount);
    }
    return new AreaFlux(new int[plateCount], 0);
  }

  /**
   * Collide loser: smaller registry area; area ties → lower plate id. Only meaningful for {@link
   * BoundaryKind#COLLIDE}.
   */
  public static int loser(int plateA, int plateB, PlateRegistry registry) {
    Objects.requireNonNull(registry, "registry");
    int a = registry.area(plateA);
    int b = registry.area(plateB);
    if (a < b) {
      return plateA;
    }
    if (b < a) {
      return plateB;
    }
    return Math.min(plateA, plateB);
  }

  public static int winner(int plateA, int plateB, PlateRegistry registry) {
    int lose = loser(plateA, plateB, registry);
    return lose == plateA ? plateB : plateA;
  }

  /** Budgets from standing boundaries + registry (F-035 locks). */
  public static AreaFlux from(Boundaries boundaries, PlateRegistry registry) {
    Objects.requireNonNull(boundaries, "boundaries");
    Objects.requireNonNull(registry, "registry");
    int n = registry.count();
    int[] delta = new int[n];
    int sink = 0;
    for (BoundaryContact c : boundaries.contacts()) {
      switch (c.kind()) {
        case SEPARATE -> {
          delta[c.plateA()] += 1;
          delta[c.plateB()] += 1;
          sink -= 2;
        }
        case COLLIDE -> {
          int lose = loser(c.plateA(), c.plateB(), registry);
          delta[lose] -= 1;
          sink += 1;
        }
        case PASS_BY -> {
          // no flux
        }
      }
    }
    return new AreaFlux(delta, sink);
  }

  public int plateCount() {
    return deltaArea.length;
  }

  public int deltaArea(int plateIndex) {
    return deltaArea[requirePlate(plateIndex)];
  }

  public int sinkDelta() {
    return sinkDelta;
  }

  /** Conservation: {@code sum(deltaArea) + sinkDelta == 0}. */
  public int netCells() {
    int sum = sinkDelta;
    for (int d : deltaArea) {
      sum += d;
    }
    return sum;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AreaFlux other)) {
      return false;
    }
    return sinkDelta == other.sinkDelta && Arrays.equals(deltaArea, other.deltaArea);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Arrays.hashCode(deltaArea), sinkDelta);
  }

  private int requirePlate(int plateIndex) {
    if (plateIndex < 0 || plateIndex >= deltaArea.length) {
      throw new IllegalArgumentException(
          "plateIndex " + plateIndex + " out of 0.." + (deltaArea.length - 1));
    }
    return plateIndex;
  }
}
