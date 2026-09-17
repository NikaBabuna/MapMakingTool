/*
 * File: product/src/main/java/com/aethelgard/product/WorldSpec.java
 * Purpose: Step 0 world seed — geometry and recorded RNG seed
 * Audience: ProductHost / tests
 * Update when: World bootstrap parameters change
 */

package com.aethelgard.product;

/**
 * Initial conditions for a product run. Width and height are cell counts. {@code seed} places the
 * Step-0 Voronoi plate sites and per-plate velocities; elevation cells start at 0.
 *
 * @param width cell count east–west (≥ 1)
 * @param height cell count north–south (≥ 1)
 * @param seed recorded generation seed; places Step-0 Voronoi plate sites and velocities (F-020)
 */
public record WorldSpec(int width, int height, long seed) {

  /** Default dump fixture: 8×8, seed {@code 0}. */
  public static final WorldSpec DEFAULT = new WorldSpec(8, 8, 0L);

  /** Product map window: 512×512, seed {@code 0}. */
  public static final WorldSpec VIEW = new WorldSpec(512, 512, 0L);

  public WorldSpec {
    if (width < 1 || height < 1) {
      throw new IllegalArgumentException(
          "width and height must be >= 1, was " + width + "x" + height);
    }
  }
}
