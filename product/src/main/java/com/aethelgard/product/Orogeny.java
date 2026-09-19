/*
 * File: product/src/main/java/com/aethelgard/product/Orogeny.java
 * Purpose: Tectonics Sub-System — elevation from standing classified boundaries
 * Audience: Product tectonics EngineSystem
 * Update when: Boundary orogeny relief rule changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Reads standing {@code elevation}, staged/pool {@code boundaries}, and standing {@code
 * plate_registry}. Writes a new elevation grid. COLLIDE winner {@code +1} / loser {@code -1};
 * SEPARATE both {@code -1}; PASS_BY {@code 0}. Walks contacts once (O(contacts)); no cell×contact
 * nest.
 */
public final class Orogeny implements SubSystem {

  /** Per-cell priority: higher wins the combine ladder. */
  private static final byte RANK_NONE = 0;

  private static final byte RANK_SEPARATE = 1;
  private static final byte RANK_LOSE = 2;
  private static final byte RANK_WIN = 3;

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
    Grid elevation = requireGrid(io.readPool(WorldFields.ELEVATION), WorldFields.ELEVATION);
    Boundaries boundaries = requireBoundaries(readField(io, WorldFields.BOUNDARIES));
    PlateRegistry registry = requireRegistry(io.readPool(WorldFields.PLATE_REGISTRY));
    io.write(WorldFields.ELEVATION, apply(boundaries, registry, elevation));
  }

  /**
   * One generation of boundary orogeny. No floor: elevation may go negative. Complexity:
   * O(contacts) stamps + one elevation copy.
   */
  public static Grid apply(Boundaries boundaries, PlateRegistry registry, Grid elevation) {
    Objects.requireNonNull(boundaries, "boundaries");
    Objects.requireNonNull(registry, "registry");
    Objects.requireNonNull(elevation, "elevation");
    int width = elevation.width();
    int height = elevation.height();
    Map<Long, Byte> ranks = new HashMap<>(Math.max(16, boundaries.size() * 2));
    for (BoundaryContact c : boundaries.contacts()) {
      int ax = c.x();
      int ay = c.y();
      int[] nb = SphereTopology.neighbor(ax, ay, c.nx(), c.ny(), width, height);
      int bx = nb[0];
      int by = nb[1];
      switch (c.kind()) {
        case PASS_BY -> {
          /* no relief */
        }
        case SEPARATE -> {
          bump(ranks, key(ax, ay), RANK_SEPARATE);
          bump(ranks, key(bx, by), RANK_SEPARATE);
        }
        case COLLIDE -> {
          int lose = AreaFlux.loser(c.plateA(), c.plateB(), registry);
          if (c.plateA() == lose) {
            bump(ranks, key(ax, ay), RANK_LOSE);
            bump(ranks, key(bx, by), RANK_WIN);
          } else {
            bump(ranks, key(ax, ay), RANK_WIN);
            bump(ranks, key(bx, by), RANK_LOSE);
          }
        }
      }
    }
    int[][] next = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        next[y][x] = elevation.get(x, y);
      }
    }
    for (Map.Entry<Long, Byte> e : ranks.entrySet()) {
      long k = e.getKey();
      int x = (int) (k >>> 32);
      int y = (int) k;
      next[y][x] += deltaFromRank(e.getValue());
    }
    return new Grid(next);
  }

  /**
   * Convenience for tests: trace standing plates then apply. Prefer {@link #apply(Boundaries,
   * PlateRegistry, Grid)} when boundaries are already known.
   */
  public static Grid apply(Grid plates, PlateVelocities velocities, Grid elevation) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(velocities, "velocities");
    PlateRegistry registry = PlateRegistry.from(plates, velocities);
    return apply(Boundaries.trace(plates, velocities), registry, elevation);
  }

  /** {@code n · (vA − vB)} for a foreign neighbor; {@code 0} when the plates match. */
  public static int closing(int plateA, int plateB, PlateVelocities velocities, int nx, int ny) {
    if (plateA == plateB) {
      return 0;
    }
    int dvx = velocities.vx(plateA) - velocities.vx(plateB);
    int dvy = velocities.vy(plateA) - velocities.vy(plateB);
    return nx * dvx + ny * dvy;
  }

  static int deltaFromRank(byte rank) {
    return switch (rank) {
      case RANK_WIN -> 1;
      case RANK_LOSE, RANK_SEPARATE -> -1;
      default -> 0;
    };
  }

  private static void bump(Map<Long, Byte> ranks, long key, byte candidate) {
    Byte prior = ranks.get(key);
    if (prior == null || candidate > prior) {
      ranks.put(key, candidate);
    }
  }

  private static long key(int x, int y) {
    return (((long) x) << 32) | (y & 0xffffffffL);
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

  private static Boundaries requireBoundaries(Object value) {
    if (value instanceof Boundaries boundaries) {
      return boundaries;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.BOUNDARIES
            + "' must be Boundaries, was "
            + value.getClass().getName());
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
}
