/*
 * File: product/src/main/java/com/aethelgard/product/BoundaryInteraction.java
 * Purpose: Sub-System — precedence / area_flux / motion_intent from boundaries
 * Audience: Product tectonics EngineSystem
 * Update when: Interaction pipeline stage changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import java.util.Set;

/**
 * Reads boundaries (staging from {@link TraceBoundaries} when present) and standing {@code
 * plate_registry}; writes {@code area_flux} and {@code motion_intent}. Does not change plates or
 * velocities.
 */
public final class BoundaryInteraction implements SubSystem {

  @Override
  public String id() {
    return "boundary-interaction";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.AREA_FLUX, WorldFields.MOTION_INTENT);
  }

  @Override
  public void execute(SubSystemIo io) {
    Boundaries boundaries = requireBoundaries(readBoundaries(io));
    PlateRegistry registry = requireRegistry(io.readPool(WorldFields.PLATE_REGISTRY));
    Grid occupancy = requireGrid(io.readPool(WorldFields.OCCUPANCY), WorldFields.OCCUPANCY);
    Lockers lockers = requireLockers(io.readPool(WorldFields.LOCKERS));
    io.write(WorldFields.AREA_FLUX, AreaFlux.from(boundaries, registry, occupancy, lockers));
    io.write(WorldFields.MOTION_INTENT, MotionIntent.from(boundaries, registry, occupancy, lockers));
  }

  private static Object readBoundaries(SubSystemIo io) {
    Object staged = io.readStaging(WorldFields.BOUNDARIES);
    if (staged != null) {
      return staged;
    }
    return io.readPool(WorldFields.BOUNDARIES);
  }

  private static Boundaries requireBoundaries(Object value) {
    if (value instanceof Boundaries boundaries) {
      return boundaries;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.BOUNDARIES
            + "' must be Boundaries, was "
            + (value == null ? "null" : value.getClass().getName()));
  }

  private static PlateRegistry requireRegistry(Object value) {
    if (value instanceof PlateRegistry registry) {
      return registry;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.PLATE_REGISTRY
            + "' must be PlateRegistry, was "
            + value.getClass().getName());
  }

  private static Grid requireGrid(Object value, String field) {
    if (value instanceof Grid grid) {
      return grid;
    }
    throw new IllegalStateException(
        "field '" + field + "' must be Grid, was " + (value == null ? "null" : value.getClass().getName()));
  }

  private static Lockers requireLockers(Object value) {
    if (value instanceof Lockers lockers) {
      return lockers;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.LOCKERS
            + "' must be Lockers, was "
            + (value == null ? "null" : value.getClass().getName()));
  }
}
