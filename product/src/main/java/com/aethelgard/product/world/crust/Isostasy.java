/*
 * File: product/src/main/java/com/aethelgard/product/world/crust/Isostasy.java
 * Purpose: Tectonics Sub-System — elevation from locker thickness at occupancy keys
 * Audience: Product tectonics EngineSystem
 * Update when: Integer isostasy formula changes
 */

package com.aethelgard.product.world.crust;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.WorldFields;
import java.util.Objects;
import java.util.Set;

/**
 * Sole writer of {@code elevation}. {@code elevation = thickness(occupancy) − T_ocean}.
 */
public final class Isostasy implements SubSystem {

  @Override
  public String id() {
    return "isostasy";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.ELEVATION);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid occupancy = requireGrid(readField(io, WorldFields.OCCUPANCY), WorldFields.OCCUPANCY);
    Lockers lockers = requireLockers(readField(io, WorldFields.LOCKERS));
    io.write(WorldFields.ELEVATION, apply(occupancy, lockers));
  }

  /**
   * Integer isostasy: height is locker thickness minus {@link Lockers#T_OCEAN} so Step 0 stays
   * elevation 0.
   */
  public static Grid apply(Grid occupancy, Lockers lockers) {
    Objects.requireNonNull(occupancy, "occupancy");
    Objects.requireNonNull(lockers, "lockers");
    int width = occupancy.width();
    int height = occupancy.height();
    int[][] next = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        next[y][x] = lockers.thickness(occupancy.get(x, y)) - Lockers.T_OCEAN;
      }
    }
    return new Grid(next);
  }

  private static Object readField(SubSystemIo io, String field) {
    Object staged = io.readStaging(field);
    return staged != null ? staged : io.readPool(field);
  }

  private static Grid requireGrid(Object value, String field) {
    if (value instanceof Grid grid) {
      return grid;
    }
    throw new IllegalStateException(
        "field '" + field + "' must be Grid, was " + value.getClass().getName());
  }

  private static Lockers requireLockers(Object value) {
    if (value instanceof Lockers lockers) {
      return lockers;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.LOCKERS
            + "' must be Lockers, was "
            + value.getClass().getName());
  }
}
