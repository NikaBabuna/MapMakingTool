/*
 * File: product/src/main/java/com/aethelgard/product/Lockers.java
 * Purpose: Locker-id → crust thickness table (G-010)
 * Audience: ProductHost / Orogeny / ThicknessToElevation / tests
 * Update when: Locker payload or T_ocean changes
 */

package com.aethelgard.product;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable thickness table keyed by locker id. Step 0 is all {@link #T_OCEAN}.
 */
public final class Lockers {

  /** Provisional oceanic thickness (F-056). \(T_{land}\) is F-058. */
  public static final int T_OCEAN = 8;

  private final int[] thickness;

  public Lockers(int[] thickness) {
    Objects.requireNonNull(thickness, "thickness");
    if (thickness.length < 1) {
      throw new IllegalArgumentException("lockers must have at least one id");
    }
    this.thickness = Arrays.copyOf(thickness, thickness.length);
  }

  /** {@code count} lockers, each {@link #T_OCEAN}. */
  public static Lockers oceanic(int count) {
    if (count < 1) {
      throw new IllegalArgumentException("count must be >= 1, was " + count);
    }
    int[] t = new int[count];
    Arrays.fill(t, T_OCEAN);
    return new Lockers(t);
  }

  public int count() {
    return thickness.length;
  }

  public int thickness(int id) {
    if (id < 0 || id >= thickness.length) {
      throw new IllegalArgumentException("locker id " + id + " out of 0.." + (thickness.length - 1));
    }
    return thickness[id];
  }

  /** Copy of thicknesses in id order. */
  public int[] thicknesses() {
    return Arrays.copyOf(thickness, thickness.length);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Lockers other)) {
      return false;
    }
    return Arrays.equals(thickness, other.thickness);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(thickness);
  }
}
