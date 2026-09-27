/*
 * File: product/src/main/java/com/aethelgard/product/world/ProductGeneration.java
 * Purpose: One-generation plate pipeline helper (integrate → apply → advect → subduct → locker stamp → mint → margin → suture → isostasy)
 * Audience: Tests / debugging
 * Update when: Generation Sub-System order changes
 */

package com.aethelgard.product.world;

import com.aethelgard.product.world.boundaries.Boundaries;
import com.aethelgard.product.world.crust.ContinentalCollision;
import com.aethelgard.product.world.crust.Isostasy;
import com.aethelgard.product.world.crust.MarginRelief;
import com.aethelgard.product.world.crust.Orogeny;
import com.aethelgard.product.world.crust.RidgeCreation;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.Occupancy;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.interaction.AreaFlux;
import com.aethelgard.product.world.interaction.MotionIntent;
import com.aethelgard.product.world.motion.GeometryApplication;
import com.aethelgard.product.world.motion.PlateKinematics;
import com.aethelgard.product.world.motion.VelocityIntegration;
import java.util.Objects;

/**
 * Mirrors the tectonics System order for independent witnesses: boundaries/flux/intent from
 * standing plates, VelocityIntegration, GeometryApplication, occupancy remap, Subduction consume/unshare,
 * locker stamps on standing occupancy, ridge mint in gaps, margin relief, continental collide,
 * isostasy of remapped keys.
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
    AreaFlux flux = AreaFlux.from(boundaries, standingReg, state.occupancy(), state.lockers());
    MotionIntent intent = MotionIntent.from(boundaries, standingReg, state.occupancy(), state.lockers());
    PlateVelocities integrated = VelocityIntegration.integrate(standingVel, intent);
    boolean[][] skipOcc = new boolean[standing.height()][standing.width()];
    GeometryApplication.Result geom =
        GeometryApplication.apply(
            standing,
            boundaries,
            flux,
            standingReg,
            integrated,
            state.occupancy(),
            state.lockers(),
            skipOcc);
    Lockers stamped =
        Orogeny.applyToLockers(boundaries, standingReg, state.occupancy(), state.lockers());
    Boundaries ridge = Boundaries.trace(geom.plates(), geom.velocities());
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(
            geom.plates(),
            state.occupancy(),
            geom.velocities(),
            generationIndex,
            ridge,
            boundaries,
            state.lockers(),
            standingReg,
            skipOcc);
    RidgeCreation.Result minted = RidgeCreation.apply(moved.occupancy(), stamped);
    Lockers relieved =
        MarginRelief.apply(minted.occupancy(), minted.lockers(), moved.plates(), moved.velocities());
    Lockers crust =
        ContinentalCollision.apply(
            minted.occupancy(), relieved, moved.plates(), moved.velocities());
    PlateRegistry after = PlateRegistry.from(moved.plates(), moved.velocities());
    Grid elevation = Isostasy.apply(minted.occupancy(), crust);
    return new Snapshot(
        moved.plates(),
        moved.velocities(),
        after,
        minted.occupancy(),
        crust,
        elevation);
  }
}
