/*
 * File: product/src/main/java/com/aethelgard/product/CollisionUplift.java
 * Purpose: Tectonics Sub-System — +1 elevation at plate contacts
 * Audience: Product tectonics EngineSystem
 * Update when: Collision-uplift rule changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import java.util.Set;

/**
 * Reads standing {@code plates} and {@code elevation}; writes a new elevation grid. Cells with a
 * 4-neighbor on a different plate gain {@code +1}. Does not see kinematics output in the same Step.
 */
public final class CollisionUplift implements SubSystem {

  @Override
  public String id() {
    return "collision-uplift";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.ELEVATION);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid plates = requireGrid(io.readPool(WorldFields.PLATES), WorldFields.PLATES);
    Grid elevation = requireGrid(io.readPool(WorldFields.ELEVATION), WorldFields.ELEVATION);
    if (plates.width() != elevation.width() || plates.height() != elevation.height()) {
      throw new IllegalStateException(
          "plates "
              + plates.width()
              + "x"
              + plates.height()
              + " != elevation "
              + elevation.width()
              + "x"
              + elevation.height());
    }
    int[][] next = new int[elevation.height()][elevation.width()];
    for (int y = 0; y < elevation.height(); y++) {
      for (int x = 0; x < elevation.width(); x++) {
        int bump = Plates.hasForeignNeighbor(plates, x, y) ? 1 : 0;
        next[y][x] = elevation.get(x, y) + bump;
      }
    }
    io.write(WorldFields.ELEVATION, new Grid(next));
  }

  private static Grid requireGrid(Object value, String field) {
    if (value instanceof Grid grid) {
      return grid;
    }
    throw new IllegalStateException(
        "field '" + field + "' must be Grid, was " + value.getClass().getName());
  }
}
