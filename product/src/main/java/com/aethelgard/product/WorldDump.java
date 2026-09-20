/*
 * File: product/src/main/java/com/aethelgard/product/WorldDump.java
 * Purpose: Headless snapshot of settled world grids
 * Audience: Tests / later observers
 * Update when: Snapshot format changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.pool.Engine;
import java.util.Objects;

/**
 * Formats settled world state as a stable text snapshot (header, elevation, plates, occupancy,
 * lockers, velocities). Same fields always produce the same string.
 */
public final class WorldDump {

  /** Canonical generation Steps for the DEFAULT-world golden (F-016). */
  public static final int CANONICAL_STEPS = 3;

  private WorldDump() {}

  /**
   * Snapshot of {@code engine} after {@link ProductHost#create(WorldSpec)} (and optional advances).
   * {@code spec} must match the run's geometry.
   */
  public static String of(Engine engine, WorldSpec spec) {
    Objects.requireNonNull(engine, "engine");
    Objects.requireNonNull(spec, "spec");
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    Grid occupancy = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    Lockers lockers = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    PlateVelocities velocities =
        (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    PlateRegistry registry =
        (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    Boundaries boundaries = (Boundaries) engine.settled().field(WorldFields.BOUNDARIES);
    AreaFlux areaFlux = (AreaFlux) engine.settled().field(WorldFields.AREA_FLUX);
    MotionIntent motionIntent = (MotionIntent) engine.settled().field(WorldFields.MOTION_INTENT);
    return format(
        spec,
        engine.stepIndex(),
        elevation,
        plates,
        occupancy,
        lockers,
        velocities,
        registry,
        boundaries,
        areaFlux,
        motionIntent);
  }

  /**
   * Stable snapshot. Lines use {@code \n}. Ends with a trailing newline.
   *
   * @param steps last completed Step index (0 after create)
   */
  public static String format(WorldSpec spec, int steps, Grid elevation, Grid plates) {
    PlateVelocities velocities = PlateVelocities.seed(spec.seed());
    PlateRegistry registry = PlateRegistry.from(plates, velocities);
    Boundaries boundaries = Boundaries.trace(plates, velocities);
    return format(
        spec,
        steps,
        elevation,
        plates,
        velocities,
        registry,
        boundaries,
        AreaFlux.from(boundaries, registry),
        MotionIntent.from(boundaries, registry));
  }

  /**
   * Stable snapshot. Lines use {@code \n}. Ends with a trailing newline.
   *
   * @param steps last completed Step index (0 after create)
   */
  public static String format(
      WorldSpec spec, int steps, Grid elevation, Grid plates, PlateVelocities velocities) {
    PlateRegistry registry = PlateRegistry.from(plates, velocities);
    Boundaries boundaries = Boundaries.trace(plates, velocities);
    return format(
        spec,
        steps,
        elevation,
        plates,
        velocities,
        registry,
        boundaries,
        AreaFlux.from(boundaries, registry),
        MotionIntent.from(boundaries, registry));
  }

  /**
   * Stable snapshot. Lines use {@code \n}. Ends with a trailing newline.
   *
   * @param steps last completed Step index (0 after create)
   */
  public static String format(
      WorldSpec spec,
      int steps,
      Grid elevation,
      Grid plates,
      PlateVelocities velocities,
      PlateRegistry registry) {
    Boundaries boundaries = Boundaries.trace(plates, velocities);
    return format(
        spec,
        steps,
        elevation,
        plates,
        velocities,
        registry,
        boundaries,
        AreaFlux.from(boundaries, registry),
        MotionIntent.from(boundaries, registry));
  }

  /**
   * Stable snapshot. Lines use {@code \n}. Ends with a trailing newline.
   *
   * @param steps last completed Step index (0 after create)
   */
  public static String format(
      WorldSpec spec,
      int steps,
      Grid elevation,
      Grid plates,
      PlateVelocities velocities,
      PlateRegistry registry,
      Boundaries boundaries) {
    return format(
        spec,
        steps,
        elevation,
        plates,
        Occupancy.seed(spec.width(), spec.height()),
        Lockers.oceanic(Occupancy.count(spec.width(), spec.height())),
        velocities,
        registry,
        boundaries,
        AreaFlux.from(boundaries, registry),
        MotionIntent.from(boundaries, registry));
  }

  /**
   * Stable snapshot. Lines use {@code \n}. Ends with a trailing newline.
   *
   * @param steps last completed Step index (0 after create)
   */
  public static String format(
      WorldSpec spec,
      int steps,
      Grid elevation,
      Grid plates,
      PlateVelocities velocities,
      PlateRegistry registry,
      Boundaries boundaries,
      AreaFlux areaFlux,
      MotionIntent motionIntent) {
    return format(
        spec,
        steps,
        elevation,
        plates,
        Occupancy.seed(spec.width(), spec.height()),
        Lockers.oceanic(Occupancy.count(spec.width(), spec.height())),
        velocities,
        registry,
        boundaries,
        areaFlux,
        motionIntent);
  }

  /**
   * Stable snapshot including occupancy keys and locker thicknesses.
   *
   * @param steps last completed Step index (0 after create)
   */
  public static String format(
      WorldSpec spec,
      int steps,
      Grid elevation,
      Grid plates,
      Grid occupancy,
      Lockers lockers,
      PlateVelocities velocities,
      PlateRegistry registry,
      Boundaries boundaries,
      AreaFlux areaFlux,
      MotionIntent motionIntent) {
    Objects.requireNonNull(spec, "spec");
    Objects.requireNonNull(elevation, "elevation");
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(occupancy, "occupancy");
    Objects.requireNonNull(lockers, "lockers");
    Objects.requireNonNull(velocities, "velocities");
    Objects.requireNonNull(registry, "registry");
    Objects.requireNonNull(boundaries, "boundaries");
    Objects.requireNonNull(areaFlux, "areaFlux");
    Objects.requireNonNull(motionIntent, "motionIntent");
    if (steps < 0) {
      throw new IllegalArgumentException("steps must be >= 0, was " + steps);
    }
    requireGeometry(spec, elevation, "elevation");
    requireGeometry(spec, plates, "plates");
    requireGeometry(spec, occupancy, "occupancy");
    StringBuilder out = new StringBuilder();
    out.append("world w=")
        .append(spec.width())
        .append(" h=")
        .append(spec.height())
        .append(" seed=")
        .append(spec.seed())
        .append(" steps=")
        .append(steps)
        .append('\n');
    out.append("elevation:\n");
    appendGrid(out, elevation);
    out.append("plates:\n");
    appendGrid(out, plates);
    out.append("occupancy:\n");
    appendGrid(out, occupancy);
    out.append("lockers:\n");
    for (int i = 0; i < lockers.count(); i++) {
      out.append(lockers.thickness(i)).append('\n');
    }
    out.append("plate_velocity:\n");
    for (int i = 0; i < velocities.count(); i++) {
      out.append(velocities.vx(i)).append(' ').append(velocities.vy(i)).append('\n');
    }
    out.append("plate_registry:\n");
    for (int i = 0; i < registry.count(); i++) {
      out.append(registry.area(i))
          .append(' ')
          .append(registry.vx(i))
          .append(' ')
          .append(registry.vy(i))
          .append('\n');
    }
    out.append("boundaries:\n");
    for (BoundaryContact c : boundaries.contacts()) {
      out.append(c.x())
          .append(' ')
          .append(c.y())
          .append(' ')
          .append(c.nx())
          .append(' ')
          .append(c.ny())
          .append(' ')
          .append(c.plateA())
          .append(' ')
          .append(c.plateB())
          .append(' ')
          .append(c.kind().name())
          .append('\n');
    }
    out.append("area_flux:\n");
    out.append(areaFlux.sinkDelta()).append('\n');
    for (int i = 0; i < areaFlux.plateCount(); i++) {
      out.append(areaFlux.deltaArea(i)).append('\n');
    }
    out.append("motion_intent:\n");
    for (int i = 0; i < motionIntent.plateCount(); i++) {
      out.append(motionIntent.ix(i)).append(' ').append(motionIntent.iy(i)).append('\n');
    }
    return out.toString();
  }

  private static void requireGeometry(WorldSpec spec, Grid grid, String label) {
    if (grid.width() != spec.width() || grid.height() != spec.height()) {
      throw new IllegalArgumentException(
          label
              + " "
              + grid.width()
              + "x"
              + grid.height()
              + " != spec "
              + spec.width()
              + "x"
              + spec.height());
    }
  }

  private static void appendGrid(StringBuilder out, Grid grid) {
    for (int y = 0; y < grid.height(); y++) {
      for (int x = 0; x < grid.width(); x++) {
        if (x > 0) {
          out.append(' ');
        }
        out.append(grid.get(x, y));
      }
      out.append('\n');
    }
  }
}
