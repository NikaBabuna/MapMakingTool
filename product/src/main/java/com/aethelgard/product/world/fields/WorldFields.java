/*
 * File: product/src/main/java/com/aethelgard/product/world/fields/WorldFields.java
 * Purpose: Pool field names for the Aethelgard world
 * Audience: Product host / Systems / tests
 * Update when: Named world layers are added
 */

package com.aethelgard.product.world.fields;

/** Named Pool fields for world layers. Later climate adds more constants of the same grid shape. */
public final class WorldFields {

  /** Elevation layer — {@link Grid} of {@code int} cells. */
  public static final String ELEVATION = "elevation";

  /** Plate-id layer — {@link Grid} of {@code int} cells; STATIC (kinematics writes it). */
  public static final String PLATES = "plates";

  /** Per-plate integer velocities — {@link PlateVelocities}; STATIC. */
  public static final String PLATE_VELOCITY = "plate_velocity";

  /** Per-plate actors — {@link PlateRegistry}; STATIC (area + initial velocity). */
  public static final String PLATE_REGISTRY = "plate_registry";

  /** Classified plate contacts — {@link Boundaries}; STATIC. */
  public static final String BOUNDARIES = "boundaries";

  /** Per-plate area create/destroy budgets — {@link AreaFlux}; STATIC. */
  public static final String AREA_FLUX = "area_flux";

  /** Per-plate preferred Δv from edges — {@link MotionIntent}; STATIC. */
  public static final String MOTION_INTENT = "motion_intent";

  /** Cell → locker id — {@link Grid}; STATIC (GeometryApplication remaps keys). */
  public static final String OCCUPANCY = "occupancy";

  /** Locker id → thickness — {@link Lockers}; STATIC (Orogeny stamps). */
  public static final String LOCKERS = "lockers";

  private WorldFields() {}
}
