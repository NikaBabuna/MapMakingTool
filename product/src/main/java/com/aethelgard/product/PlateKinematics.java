/*
 * File: product/src/main/java/com/aethelgard/product/PlateKinematics.java
 * Purpose: Kinematics — advect plate ownership with sphere polar wrap (F-045)
 * Audience: ApplyGeometry / tests
 * Update when: Advection / gap-fill rule changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import java.util.Objects;
import java.util.Set;

/**
 * Cells translate by plate velocity under {@link SphereTopology}. Unique claimants keep ownership.
 * Empty/contested cells fill by iterative flood (F-044). Crossing a pole flips that plate's
 * heading ({@code vx},{@code vy}) for the next generation (F-045 B2).
 */
public final class PlateKinematics implements SubSystem {

  /** Temporary unresolved ownership after advection (must not remain). */
  public static final int UNRESOLVED = -1;

  @Override
  public String id() {
    return "plate-kinematics";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.PLATES, WorldFields.PLATE_VELOCITY, WorldFields.PLATE_REGISTRY);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid plates = requireGrid(readPlates(io), WorldFields.PLATES);
    PlateVelocities velocities = requireVelocities(readVelocities(io));
    Boundaries boundaries = requireBoundaries(readBoundaries(io));
    int generationIndex = Math.toIntExact(io.poolValue()) - 1;
    AdvectResult moved = advect(plates, velocities, generationIndex, boundaries);
    io.write(WorldFields.PLATES, moved.plates());
    io.write(WorldFields.PLATE_VELOCITY, moved.velocities());
    io.write(WorldFields.PLATE_REGISTRY, PlateRegistry.from(moved.plates(), moved.velocities()));
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

  /** Result of one advection generation (plates + heading-flipped velocities + occupancy keys). */
  public record AdvectResult(Grid plates, PlateVelocities velocities, Grid occupancy) {
    public AdvectResult(Grid plates, PlateVelocities velocities) {
      this(plates, velocities, Occupancy.seed(plates.width(), plates.height()));
    }
  }

  /**
   * One generation of advection + flood fill. {@code generationIndex} ≥ 1 on the first tectonics
   * tick. Plates that cross a pole have {@code vx,vy} flipped in the returned velocities.
   */
  public static AdvectResult advect(
      Grid plates, PlateVelocities velocities, int generationIndex, Boundaries boundaries) {
    return advect(
        plates,
        Occupancy.seed(plates.width(), plates.height()),
        velocities,
        generationIndex,
        boundaries);
  }

  /**
   * One generation of advection + flood fill, remapping occupancy keys with the same motion.
   * Unique destinations keep the source locker id. Contested cells keep the lowest-plate source
   * locker. Gap cells ({@code claims == 0}) stay {@link #UNRESOLVED} for {@link RidgeCreate}.
   */
  public static AdvectResult advect(
      Grid plates,
      Grid occupancy,
      PlateVelocities velocities,
      int generationIndex,
      Boundaries boundaries) {
    return advect(plates, occupancy, velocities, generationIndex, boundaries, null, null, null, null);
  }

  /**
   * Advection plus F-058 occupancy corrections: skip export from SEPARATE/collide-claimed cells,
   * consume oceanic collide lockers, unshare copied SEPARATE contact lockers.
   */
  public static AdvectResult advect(
      Grid plates,
      Grid occupancy,
      PlateVelocities velocities,
      int generationIndex,
      Boundaries boundaries,
      Boundaries standingBoundaries,
      Lockers lockers,
      PlateRegistry registry,
      boolean[][] skipOccupancyExport) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(occupancy, "occupancy");
    Objects.requireNonNull(velocities, "velocities");
    Objects.requireNonNull(boundaries, "boundaries");
    if (generationIndex < 1) {
      throw new IllegalArgumentException("generationIndex must be >= 1, was " + generationIndex);
    }
    if (occupancy.width() != plates.width() || occupancy.height() != plates.height()) {
      throw new IllegalArgumentException(
          "occupancy "
              + occupancy.width()
              + "x"
              + occupancy.height()
              + " != plates "
              + plates.width()
              + "x"
              + plates.height());
    }
    int width = plates.width();
    int height = plates.height();
    int n = velocities.count();
    int[][] claims = new int[height][width];
    int[][] who = new int[height][width];
    int[][] occDest = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        occDest[y][x] = UNRESOLVED;
      }
    }
    boolean[] crossed = new boolean[n];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int plate = plates.get(x, y);
        if (plate < 0 || plate >= n) {
          throw new IllegalStateException(
              "plate id " + plate + " out of 0.." + (n - 1) + " at (" + x + "," + y + ")");
        }
        int vx = velocities.vx(plate);
        int vy = velocities.vy(plate);
        int[] step = SphereTopology.advectCell(x, y, vx, vy, width, height);
        int nx = step[0];
        int ny = step[1];
        if (step[2] != vx || step[3] != vy) {
          crossed[plate] = true;
        }
        claims[ny][nx]++;
        if (claims[ny][nx] == 1) {
          who[ny][nx] = plate;
        } else if (plate < who[ny][nx]) {
          who[ny][nx] = plate;
        }
        if (skipOccupancyExport != null && skipOccupancyExport[y][x]) {
          continue;
        }
        if (plate == who[ny][nx]) {
          occDest[ny][nx] = occupancy.get(x, y);
        }
      }
    }
    int[][] next = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (claims[y][x] == 0) {
          next[y][x] = UNRESOLVED;
        } else {
          next[y][x] = who[y][x];
        }
      }
    }
    fillUnresolvedFlood(next, width, height);
    if (standingBoundaries != null && lockers != null && registry != null) {
      Subduct.correct(
          occDest, occupancy, standingBoundaries, lockers, registry, plates, velocities);
    }
    // Occupancy gaps stay UNRESOLVED — RidgeCreate mints thin ocean (F-057/F-058).
    int[] ovx = new int[n];
    int[] ovy = new int[n];
    for (int i = 0; i < n; i++) {
      if (crossed[i]) {
        ovx[i] = -velocities.vx(i);
        ovy[i] = -velocities.vy(i);
      } else {
        ovx[i] = velocities.vx(i);
        ovy[i] = velocities.vy(i);
      }
    }
    return new AdvectResult(
        new Grid(next), new PlateVelocities(velocities.seed(), ovx, ovy), new Grid(occDest));
  }

  /**
   * Iterative flood until full cover. Phase A: single distinct owned neighbor. Phase B: longest
   * orthogonal contact then lower plate id. Sphere neighbors (F-045).
   */
  static void fillUnresolvedFlood(int[][] cells, int width, int height) {
    boolean progress = true;
    while (progress) {
      progress = false;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (cells[y][x] != UNRESOLVED) {
            continue;
          }
          int only = singleOwnedNeighbor(cells, width, height, x, y);
          if (only >= 0) {
            cells[y][x] = only;
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
          throw new IllegalStateException("unowned cell after flood fill at (" + x + "," + y + ")");
        }
      }
    }
  }

  private static int singleOwnedNeighbor(int[][] cells, int width, int height, int x, int y) {
    int only = -1;
    for (int[] d : SphereTopology.ORTHO) {
      int[] n = SphereTopology.neighbor(x, y, d[0], d[1], width, height);
      int id = cells[n[1]][n[0]];
      if (id < 0) {
        continue;
      }
      if (only < 0) {
        only = id;
      } else if (id != only) {
        return -1;
      }
    }
    return only;
  }

  private static int neighborOwner(int[][] cells, int width, int height, int x, int y) {
    int bestId = -1;
    int bestContact = -1;
    for (int[] d : SphereTopology.ORTHO) {
      int[] n = SphereTopology.neighbor(x, y, d[0], d[1], width, height);
      int id = cells[n[1]][n[0]];
      if (id < 0) {
        continue;
      }
      int contact = 0;
      for (int[] d2 : SphereTopology.ORTHO) {
        int[] s = SphereTopology.neighbor(x, y, d2[0], d2[1], width, height);
        if (cells[s[1]][s[0]] == id) {
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
