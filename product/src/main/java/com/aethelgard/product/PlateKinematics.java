/*
 * File: product/src/main/java/com/aethelgard/product/PlateKinematics.java
 * Purpose: Kinematics — advect plate ownership; ridge accretion for leftovers (F-043)
 * Audience: ApplyGeometry / tests
 * Update when: Advection / ridge-fill rule changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import java.util.Objects;
import java.util.Set;

/**
 * Cells translate by plate velocity (wrap X; Y off-map dropped). Unique claimants keep ownership.
 * Empty/contested cells are filled by <strong>ridge accretion</strong>: SEPARATE contacts extend
 * plates A/B into gaps whose owned neighbors are only from {A,B}; remaining holes use neighbor
 * flood only. No global nearest-site refill.
 */
public final class PlateKinematics implements SubSystem {

  /** Temporary unresolved ownership after advection (must not remain). */
  public static final int UNRESOLVED = -1;

  private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

  @Override
  public String id() {
    return "plate-kinematics";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.PLATES);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid plates = requireGrid(readPlates(io), WorldFields.PLATES);
    PlateVelocities velocities = requireVelocities(readVelocities(io));
    Boundaries boundaries = requireBoundaries(readBoundaries(io));
    int generationIndex = Math.toIntExact(io.poolValue()) - 1;
    io.write(WorldFields.PLATES, advect(plates, velocities, generationIndex, boundaries));
  }

  private static Object readPlates(SubSystemIo io) {
    Object staged = io.readStaging(WorldFields.PLATES);
    return staged != null ? staged : io.readPool(WorldFields.PLATES);
  }

  private static Object readVelocities(SubSystemIo io) {
    Object staged = io.readStaging(WorldFields.PLATE_VELOCITY);
    return staged != null ? staged : io.readPool(WorldFields.PLATE_VELOCITY);
  }

  private static Object readBoundaries(SubSystemIo io) {
    Object staged = io.readStaging(WorldFields.BOUNDARIES);
    return staged != null ? staged : io.readPool(WorldFields.BOUNDARIES);
  }

  /**
   * One generation of advection + ridge/neighbor fill. {@code generationIndex} ≥ 1 on the first
   * tectonics tick.
   */
  public static Grid advect(
      Grid plates, PlateVelocities velocities, int generationIndex, Boundaries boundaries) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(velocities, "velocities");
    Objects.requireNonNull(boundaries, "boundaries");
    if (generationIndex < 1) {
      throw new IllegalArgumentException("generationIndex must be >= 1, was " + generationIndex);
    }
    int width = plates.width();
    int height = plates.height();
    int n = velocities.count();
    int[][] claims = new int[height][width];
    int[][] who = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int plate = plates.get(x, y);
        if (plate < 0 || plate >= n) {
          throw new IllegalStateException(
              "plate id " + plate + " out of 0.." + (n - 1) + " at (" + x + "," + y + ")");
        }
        int nx = PlateVelocities.wrapX(x, velocities.vx(plate), 1, width);
        int ny = y + velocities.vy(plate);
        if (ny < 0 || ny >= height) {
          continue;
        }
        claims[ny][nx]++;
        who[ny][nx] = plate;
      }
    }
    int[][] next = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        next[y][x] = claims[y][x] == 1 ? who[y][x] : UNRESOLVED;
      }
    }
    fillRidgeThenNeighbors(next, width, height, boundaries);
    return new Grid(next);
  }

  /**
   * Ridge accretion then neighbor flood until full cover. Pure SEPARATE gaps: neighbor set ⊆ {A,B}
   * → lower id among neighbors (plate extends).
   */
  static void fillRidgeThenNeighbors(
      int[][] cells, int width, int height, Boundaries boundaries) {
    boolean progress = true;
    while (progress) {
      progress = false;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (cells[y][x] != UNRESOLVED) {
            continue;
          }
          int ridge = ridgeOwner(cells, width, height, x, y, boundaries);
          if (ridge >= 0) {
            cells[y][x] = ridge;
            progress = true;
          }
        }
      }
    }
    progress = true;
    while (progress) {
      progress = false;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (cells[y][x] != UNRESOLVED) {
            continue;
          }
          int owner = neighborOwner(cells, width, height, x, y);
          if (owner >= 0) {
            cells[y][x] = owner;
            progress = true;
          }
        }
      }
    }
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (cells[y][x] < 0) {
          throw new IllegalStateException("unowned cell after ridge fill at (" + x + "," + y + ")");
        }
      }
    }
  }

  /**
   * If owned neighbors are a non-empty subset of some SEPARATE pair {A,B}, extend that ridge
   * (lower id when both touch).
   */
  private static int ridgeOwner(
      int[][] cells, int width, int height, int x, int y, Boundaries boundaries) {
    int n0 = -1;
    int n1 = -1;
    int distinct = 0;
    for (int[] d : DIRS) {
      int nx = Math.floorMod(x + d[0], width);
      int ny = y + d[1];
      if (ny < 0 || ny >= height) {
        continue;
      }
      int id = cells[ny][nx];
      if (id < 0) {
        continue;
      }
      if (distinct == 0) {
        n0 = id;
        distinct = 1;
      } else if (id != n0 && distinct == 1) {
        n1 = id;
        distinct = 2;
      } else if (id != n0 && id != n1) {
        return -1;
      }
    }
    if (distinct == 0) {
      return -1;
    }
    for (BoundaryContact c : boundaries.contacts()) {
      if (c.kind() != BoundaryKind.SEPARATE) {
        continue;
      }
      int a = c.plateA();
      int b = c.plateB();
      if (distinct == 1) {
        if (n0 == a || n0 == b) {
          return n0;
        }
      } else {
        boolean match =
            (n0 == a && n1 == b) || (n0 == b && n1 == a);
        if (match) {
          return Math.min(n0, n1);
        }
      }
    }
    return -1;
  }

  /** Longest orthogonal contact among owned neighbors; lower id on ties. */
  private static int neighborOwner(int[][] cells, int width, int height, int x, int y) {
    int bestId = -1;
    int bestContact = -1;
    for (int[] d : DIRS) {
      int nx = Math.floorMod(x + d[0], width);
      int ny = y + d[1];
      if (ny < 0 || ny >= height) {
        continue;
      }
      int id = cells[ny][nx];
      if (id < 0) {
        continue;
      }
      int contact = 0;
      for (int[] d2 : DIRS) {
        int sx = Math.floorMod(x + d2[0], width);
        int sy = y + d2[1];
        if (sy < 0 || sy >= height) {
          continue;
        }
        if (cells[sy][sx] == id) {
          contact++;
        }
      }
      if (contact > bestContact || (contact == bestContact && (bestId < 0 || id < bestId))) {
        bestContact = contact;
        bestId = id;
      }
    }
    return bestId;
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
