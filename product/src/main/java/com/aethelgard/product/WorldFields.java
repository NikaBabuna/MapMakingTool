/*
 * File: product/src/main/java/com/aethelgard/product/WorldFields.java
 * Purpose: Pool field names for the Aethelgard world
 * Audience: Product host / Systems / tests
 * Update when: Named world layers are added
 */

package com.aethelgard.product;

/** Named Pool fields for world layers. Later climate adds more constants of the same grid shape. */
public final class WorldFields {

  /** Elevation layer — {@link Grid} of {@code int} cells. */
  public static final String ELEVATION = "elevation";

  /** Plate-id layer — {@link Grid} of {@code int} cells; Step-0 seed for tectonics. */
  public static final String PLATES = "plates";

  private WorldFields() {}
}
