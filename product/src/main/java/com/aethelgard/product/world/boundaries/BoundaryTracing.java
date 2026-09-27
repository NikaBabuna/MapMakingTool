/*
 * File: product/src/main/java/com/aethelgard/product/world/boundaries/BoundaryTracing.java
 * Purpose: Sub-System — refresh classified boundaries each generation
 * Audience: Product tectonics EngineSystem
 * Update when: Boundary pipeline stage changes
 */

package com.aethelgard.product.world.boundaries;

import com.aethelgard.engine.systems.SubSystem;
import com.aethelgard.engine.systems.SubSystemIo;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.WorldFields;
import java.util.Set;

/**
 * Reads standing {@code plates} and {@code plate_velocity}; writes {@code boundaries}. Runs in the
 * tectonics System before {@link Orogeny} so both see the same standing plates.
 */
public final class BoundaryTracing implements SubSystem {

  @Override
  public String id() {
    return "trace-boundaries";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.BOUNDARIES);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid plates = requireGrid(io.readPool(WorldFields.PLATES), WorldFields.PLATES);
    PlateVelocities velocities = requireVelocities(io.readPool(WorldFields.PLATE_VELOCITY));
    io.write(WorldFields.BOUNDARIES, Boundaries.trace(plates, velocities));
  }

  private static Grid requireGrid(Object value, String field) {
    if (value instanceof Grid grid) {
      return grid;
    }
    throw new IllegalStateException(
        "field '" + field + "' must be Grid, was " + value.getClass().getName());
  }

  private static PlateVelocities requireVelocities(Object value) {
    if (value instanceof PlateVelocities velocities) {
      return velocities;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.PLATE_VELOCITY
            + "' must be PlateVelocities, was "
            + value.getClass().getName());
  }
}
