/*
 * File: product/src/main/java/com/aethelgard/product/ProductGeneration.java
 * Purpose: One-generation plate pipeline helper (integrate → apply → advect → locker stamp → isostasy)
 * Audience: Tests / debugging
 * Update when: Generation Sub-System order changes
 */

package com.aethelgard.product;

import java.util.Objects;

/**
 * Mirrors the tectonics System order for independent witnesses: boundaries/flux/intent from
 * standing plates, IntegrateVelocity, ApplyGeometry, occupancy remap, locker stamps on standing
 * occupancy, isostasy of remapped keys.
 */
public final class ProductGeneration {

  private ProductGeneration() {}

  public record Snapshot(
      Grid plates,
      PlateVelocities velocities,
      PlateRegistry registry,
      Grid occupancy,
      Lockers lockers,
      Grid elevation) {
    public Snapshot {
      Objects.requireNonNull(plates, "plates");
      Objects.requireNonNull(velocities, "velocities");
      Objects.requireNonNull(registry, "registry");
      Objects.requireNonNull(occupancy, "occupancy");
      Objects.requireNonNull(lockers, "lockers");
      Objects.requireNonNull(elevation, "elevation");
    }

    /** Step-0 occupancy + oceanic lockers; {@code elevation} is typically zeros. */
    public Snapshot(
        Grid plates, PlateVelocities velocities, PlateRegistry registry, Grid elevation) {
      this(
          plates,
          velocities,
          registry,
          Occupancy.seed(plates.width(), plates.height()),
          Lockers.oceanic(Occupancy.count(plates.width(), plates.height())),
          elevation);
    }
  }

  /** One generation Step with {@code generationIndex} ≥ 1 (same as kinematics). */
  public static Snapshot advance(Snapshot state, int generationIndex) {
    Objects.requireNonNull(state, "state");
    if (generationIndex < 1) {
      throw new IllegalArgumentException("generationIndex must be >= 1, was " + generationIndex);
    }
    Grid standing = state.plates();
    PlateVelocities standingVel = state.velocities();
    PlateRegistry standingReg = PlateRegistry.from(standing, standingVel);
    Boundaries boundaries = Boundaries.trace(standing, standingVel);
    AreaFlux flux = AreaFlux.from(boundaries, standingReg);
    MotionIntent intent = MotionIntent.from(boundaries, standingReg);
    PlateVelocities integrated = IntegrateVelocity.integrate(standingVel, intent);
    ApplyGeometry.Result geom =
        ApplyGeometry.apply(standing, boundaries, flux, standingReg, integrated);
    Lockers stamped =
        Orogeny.applyToLockers(boundaries, standingReg, state.occupancy(), state.lockers());
    Boundaries ridge = Boundaries.trace(geom.plates(), geom.velocities());
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(
            geom.plates(), state.occupancy(), geom.velocities(), generationIndex, ridge);
    PlateRegistry after = PlateRegistry.from(moved.plates(), moved.velocities());
    Grid elevation = ThicknessToElevation.apply(moved.occupancy(), stamped);
    return new Snapshot(
        moved.plates(), moved.velocities(), after, moved.occupancy(), stamped, elevation);
  }
}
