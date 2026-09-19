/*
 * File: product/src/main/java/com/aethelgard/product/ProductGeneration.java
 * Purpose: One-generation plate pipeline helper (integrate → apply → advect → standing orogeny)
 * Audience: Tests / debugging
 * Update when: Generation Sub-System order changes
 */

package com.aethelgard.product;

import java.util.Objects;

/**
 * Mirrors the tectonics System order for independent witnesses: boundaries/flux/intent from
 * standing plates, IntegrateVelocity, ApplyGeometry, advection, orogeny on standing plates.
 */
public final class ProductGeneration {

  private ProductGeneration() {}

  public record Snapshot(
      Grid plates, PlateVelocities velocities, PlateRegistry registry, Grid elevation) {
    public Snapshot {
      Objects.requireNonNull(plates, "plates");
      Objects.requireNonNull(velocities, "velocities");
      Objects.requireNonNull(registry, "registry");
      Objects.requireNonNull(elevation, "elevation");
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
    Grid moved = PlateKinematics.advect(geom.plates(), geom.velocities(), generationIndex);
    PlateRegistry after = PlateRegistry.from(moved, geom.velocities());
    Grid elevation = Orogeny.apply(standing, boundaries, standingReg, state.elevation());
    return new Snapshot(moved, geom.velocities(), after, elevation);
  }
}
