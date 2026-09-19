/*
 * File: product/src/main/java/com/aethelgard/product/Orogeny.java
 * Purpose: Tectonics Sub-System — elevation from standing-plate relative motion
 * Audience: Product tectonics EngineSystem
 * Update when: Orogeny contact rule changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import java.util.Set;

/**
 * Reads standing {@code plates}, CONSTANT {@code plate_velocity}, and {@code elevation}. Writes a
 * new elevation grid. Cylinder 4-neighbors (wrap X; Y clipped): converge {@code +1}, diverge {@code
 * -1}, transform or interior {@code 0}. Any converge wins over diverge. Does not see kinematics
 * output this Step.
 */
public final class Orogeny implements SubSystem {

  @Override
  public String id() {
    return "orogeny";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.ELEVATION);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid plates = requireGrid(io.readPool(WorldFields.PLATES), WorldFields.PLATES);
    Grid elevation = requireGrid(io.readPool(WorldFields.ELEVATION), WorldFields.ELEVATION);
    PlateVelocities velocities = requireVelocities(io.readPool(WorldFields.PLATE_VELOCITY));
    io.write(WorldFields.ELEVATION, apply(plates, velocities, elevation));
  }

  /**
   * One generation of orogeny on standing plates. No floor: elevation may go negative.
   */
  public static Grid apply(Grid plates, PlateVelocities velocities, Grid elevation) {
    if (plates == null || velocities == null || elevation == null) {
      throw new NullPointerException("plates, velocities, elevation");
    }
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
        next[y][x] = elevation.get(x, y) + delta(plates, velocities, x, y);
      }
    }
    return new Grid(next);
  }

  /**
   * Cell delta: {@code +1} if any cylinder 4-neighbor is a converging foreign plate; else {@code
   * -1} if any is diverging; else {@code 0}. Y off-map neighbors are ignored (polar edge).
   */
  public static int delta(Grid plates, PlateVelocities velocities, int x, int y) {
    int a = plates.get(x, y);
    boolean converge = false;
    boolean diverge = false;
    int width = plates.width();
    int height = plates.height();
    int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    for (int[] d : dirs) {
      int nx = d[0];
      int ny = d[1];
      int bx = Math.floorMod(x + nx, width);
      int by = y + ny;
      if (by < 0 || by >= height) {
        continue;
      }
      int closing = closing(a, plates.get(bx, by), velocities, nx, ny);
      if (closing > 0) {
        converge = true;
      } else if (closing < 0) {
        diverge = true;
      }
    }
    if (converge) {
      return 1;
    }
    if (diverge) {
      return -1;
    }
    return 0;
  }

  /** {@code n · (vA − vB)} for a foreign neighbor; {@code 0} when the plates match. */
  static int closing(int plateA, int plateB, PlateVelocities velocities, int nx, int ny) {
    if (plateA == plateB) {
      return 0;
    }
    int dvx = velocities.vx(plateA) - velocities.vx(plateB);
    int dvy = velocities.vy(plateA) - velocities.vy(plateB);
    return nx * dvx + ny * dvy;
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
