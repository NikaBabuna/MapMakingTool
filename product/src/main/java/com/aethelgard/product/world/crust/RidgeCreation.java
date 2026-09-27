/*
 * File: product/src/main/java/com/aethelgard/product/world/crust/RidgeCreation.java
 * Purpose: Tectonics Sub-System — mint thin oceanic lockers in advection gaps
 * Audience: Product tectonics EngineSystem
 * Update when: Ridge mint rule changes
 */

package com.aethelgard.product.world.crust;

import com.aethelgard.engine.systems.SubSystem;
import com.aethelgard.engine.systems.SubSystemIo;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.motion.PlateKinematics;
import java.util.Objects;
import java.util.Set;

/**
 * Fills occupancy cells left {@link PlateKinematics#UNRESOLVED} after advection with new locker
 * ids at {@link Lockers#T_OCEAN}. Plate flood is unchanged. Unique/contested occupancy is left
 * alone.
 */
public final class RidgeCreation implements SubSystem {

  @Override
  public String id() {
    return "ridge-create";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.OCCUPANCY, WorldFields.LOCKERS);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid occupancy = requireGrid(readField(io, WorldFields.OCCUPANCY), WorldFields.OCCUPANCY);
    Lockers lockers = requireLockers(readField(io, WorldFields.LOCKERS));
    Result minted = apply(occupancy, lockers);
    io.write(WorldFields.OCCUPANCY, minted.occupancy());
    io.write(WorldFields.LOCKERS, minted.lockers());
  }

  /** Occupancy after mint plus the extended locker table. */
  public record Result(Grid occupancy, Lockers lockers) {
    public Result {
      Objects.requireNonNull(occupancy, "occupancy");
      Objects.requireNonNull(lockers, "lockers");
    }
  }

  /**
   * Raster-order mint: each {@link PlateKinematics#UNRESOLVED} cell gets the next locker id,
   * thickness {@link Lockers#T_OCEAN}. Resolved cells keep their ids.
   */
  public static Result apply(Grid occupancy, Lockers lockers) {
    Objects.requireNonNull(occupancy, "occupancy");
    Objects.requireNonNull(lockers, "lockers");
    int width = occupancy.width();
    int height = occupancy.height();
    int gaps = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (occupancy.get(x, y) == PlateKinematics.UNRESOLVED) {
          gaps++;
        }
      }
    }
    if (gaps == 0) {
      return new Result(occupancy, lockers);
    }
    int[][] cells = new int[height][width];
    int nextId = lockers.count();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int id = occupancy.get(x, y);
        if (id == PlateKinematics.UNRESOLVED) {
          cells[y][x] = nextId++;
        } else {
          cells[y][x] = id;
        }
      }
    }
    if (nextId != lockers.count() + gaps) {
      throw new IllegalStateException("mint id mismatch: next=" + nextId + " gaps=" + gaps);
    }
    return new Result(new Grid(cells), lockers.appendOceanic(gaps));
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
