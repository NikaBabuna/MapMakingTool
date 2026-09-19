/*
 * File: product/src/main/java/com/aethelgard/product/Orogeny.java
 * Purpose: Tectonics Sub-System — elevation from standing classified boundaries
 * Audience: Product tectonics EngineSystem
 * Update when: Boundary orogeny relief rule changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import java.util.Objects;
import java.util.Set;

/**
 * Reads standing {@code plates}, {@code elevation}, {@code plate_registry}, and staged/pool {@code
 * boundaries}; writes elevation. COLLIDE: winner +1 / loser −1; SEPARATE: both −1; PASS_BY: 0.
 * Per-cell: any winner COLLIDE wins over loser/SEPARATE. Does not see this Step’s geometry write.
 * Wiki: {@code docs/product/wiki/tectonics.md}.
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
    PlateRegistry registry = requireRegistry(io.readPool(WorldFields.PLATE_REGISTRY));
    Boundaries boundaries = requireBoundaries(readBoundaries(io));
    io.write(WorldFields.ELEVATION, apply(plates, boundaries, registry, elevation));
  }

  /** One generation of boundary orogeny on standing plates. No floor: elevation may go negative. */
  public static Grid apply(
      Grid plates, Boundaries boundaries, PlateRegistry registry, Grid elevation) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(boundaries, "boundaries");
    Objects.requireNonNull(registry, "registry");
    Objects.requireNonNull(elevation, "elevation");
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
        next[y][x] = elevation.get(x, y) + delta(x, y, plates, boundaries, registry);
      }
    }
    return new Grid(next);
  }

  /**
   * Cell delta from standing contacts that touch {@code (x,y)}. Winner COLLIDE → {@code +1}; else
   * loser COLLIDE or SEPARATE → {@code -1}; else {@code 0}.
   */
  public static int delta(
      int x, int y, Grid plates, Boundaries boundaries, PlateRegistry registry) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(boundaries, "boundaries");
    Objects.requireNonNull(registry, "registry");
    int width = plates.width();
    int height = plates.height();
    boolean win = false;
    boolean lose = false;
    boolean separate = false;
    for (BoundaryContact c : boundaries.contacts()) {
      int ax = c.x();
      int ay = c.y();
      int bx = Math.floorMod(ax + c.nx(), width);
      int by = ay + c.ny();
      if (by < 0 || by >= height) {
        continue;
      }
      boolean onA = ax == x && ay == y;
      boolean onB = bx == x && by == y;
      if (!onA && !onB) {
        continue;
      }
      switch (c.kind()) {
        case COLLIDE -> {
          int loseId = AreaFlux.loser(c.plateA(), c.plateB(), registry);
          int myId = onA ? c.plateA() : c.plateB();
          if (myId == loseId) {
            lose = true;
          } else {
            win = true;
          }
        }
        case SEPARATE -> separate = true;
        case PASS_BY -> {
          // no relief
        }
      }
    }
    if (win) {
      return 1;
    }
    if (lose || separate) {
      return -1;
    }
    return 0;
  }

  /**
   * Closing used by boundary classification: {@code n · (vA − vB)}. Kept here so {@link Boundaries}
   * and tests share one definition.
   */
  static int closing(int plateA, int plateB, PlateVelocities velocities, int nx, int ny) {
    if (plateA == plateB) {
      return 0;
    }
    int dvx = velocities.vx(plateA) - velocities.vx(plateB);
    int dvy = velocities.vy(plateA) - velocities.vy(plateB);
    return nx * dvx + ny * dvy;
  }

  private static Object readBoundaries(SubSystemIo io) {
    Object staged = io.readStaging(WorldFields.BOUNDARIES);
    if (staged != null) {
      return staged;
    }
    return io.readPool(WorldFields.BOUNDARIES);
  }

  private static Grid requireGrid(Object value, String field) {
    if (value instanceof Grid grid) {
      return grid;
    }
    throw new IllegalStateException(
        "field '" + field + "' must be Grid, was " + value.getClass().getName());
  }

  private static PlateRegistry requireRegistry(Object value) {
    if (value instanceof PlateRegistry registry) {
      return registry;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.PLATE_REGISTRY
            + "' must be PlateRegistry, was "
            + (value == null ? "null" : value.getClass().getName()));
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
}
