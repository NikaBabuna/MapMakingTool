/*
 * File: product/src/main/java/com/aethelgard/product/world/crust/MarginRelief.java
 * Purpose: Tectonics Sub-System — rift trough, collide slope, lip blend
 * Audience: Product tectonics EngineSystem
 * Update when: Margin thickness rule changes
 */

package com.aethelgard.product.world.crust;

import com.aethelgard.engine.systems.SubSystem;
import com.aethelgard.engine.systems.SubSystemIo;
import com.aethelgard.product.world.boundaries.Boundaries;
import com.aethelgard.product.world.boundaries.BoundaryContact;
import com.aethelgard.product.world.boundaries.BoundaryKind;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.topology.SphereTopology;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * After ridge mint, before isostasy. Oceanic lockers near a post-move split follow a trough.
 * Oceanic lockers near a collision gain a short bonus that stays below {@link Lockers#T_LAND}.
 * A seed hash blends some lip cells toward the farther neighbor. Thickness {@code >= T_land} is
 * left alone. Pass-by writes nothing.
 */
public final class MarginRelief implements SubSystem {

  /** Orthogonal steps from a split contact back to thickness {@link Lockers#T_OCEAN}. */
  public static final int DIVERGE_RADIUS = 8;

  /** Orthogonal steps from a collide contact. Bonus is 0 at this distance. */
  public static final int COLLIDE_RADIUS = 4;

  /** Lip blend applies at split distance 1..{@value} inclusive. */
  public static final int LIP = 4;

  @Override
  public String id() {
    return "margin-relief";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.LOCKERS);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid occupancy = requireGrid(readField(io, WorldFields.OCCUPANCY), WorldFields.OCCUPANCY);
    Lockers lockers = requireLockers(readField(io, WorldFields.LOCKERS));
    Grid plates = requireGrid(readField(io, WorldFields.PLATES), WorldFields.PLATES);
    PlateVelocities velocities = requireVelocities(readField(io, WorldFields.PLATE_VELOCITY));
    io.write(WorldFields.LOCKERS, apply(occupancy, lockers, plates, velocities));
  }

  /** Trough thickness at split distance {@code d} in 0..{@link #DIVERGE_RADIUS}. */
  public static int trough(int distance) {
    return 4 + distance / 2;
  }

  /** Collide bonus. 0 when {@code distance >= } {@link #COLLIDE_RADIUS}. */
  public static int collideBonus(int distance) {
    if (distance < 0 || distance >= COLLIDE_RADIUS) {
      return 0;
    }
    return COLLIDE_RADIUS - distance;
  }

  /**
   * Half of the cells, stable for {@code (seed, x, y)}. Used on the split lip.
   */
  public static boolean selectsLip(long seed, int x, int y) {
    long z = seed ^ (((long) x) * 0x9E3779B97F4A7C15L) ^ (((long) y) * 0xC2B2AE3D27D4EB4FL);
    z ^= z >>> 33;
    return (z & 1L) == 0L;
  }

  /** Traces post-move contacts, then applies the margin. */
  public static Lockers apply(
      Grid occupancy, Lockers lockers, Grid plates, PlateVelocities velocities) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(velocities, "velocities");
    return apply(occupancy, lockers, Boundaries.trace(plates, velocities), velocities.seed());
  }

  /** Margin from an already classified contact list. {@code seed} drives the lip hash. */
  public static Lockers apply(Grid occupancy, Lockers lockers, Boundaries boundaries, long seed) {
    Objects.requireNonNull(occupancy, "occupancy");
    Objects.requireNonNull(lockers, "lockers");
    Objects.requireNonNull(boundaries, "boundaries");
    int width = occupancy.width();
    int height = occupancy.height();
    int[][] separate = distances(boundaries, BoundaryKind.SEPARATE, DIVERGE_RADIUS, width, height);
    int[][] collide = distances(boundaries, BoundaryKind.COLLIDE, COLLIDE_RADIUS, width, height);
    int[] pre = lockers.thicknesses();
    int count = pre.length;
    int[] sepD = new int[count];
    int[] colD = new int[count];
    int[] repX = new int[count];
    int[] repY = new int[count];
    Arrays.fill(sepD, Integer.MAX_VALUE);
    Arrays.fill(colD, Integer.MAX_VALUE);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int id = occupancy.get(x, y);
        if (id < 0 || id >= count) {
          continue;
        }
        int sd = separate[y][x];
        if (sd >= 0 && sd < sepD[id]) {
          sepD[id] = sd;
          repX[id] = x;
          repY[id] = y;
        }
        int cd = collide[y][x];
        if (cd >= 0 && cd < colD[id]) {
          colD[id] = cd;
        }
      }
    }
    int[] next = Arrays.copyOf(pre, count);
    for (int id = 0; id < count; id++) {
      if (pre[id] >= Lockers.T_LAND) {
        continue;
      }
      int thickness = pre[id];
      boolean changed = false;
      if (sepD[id] <= DIVERGE_RADIUS) {
        int d = sepD[id];
        int curve = trough(d);
        if (d >= 1 && d <= LIP && selectsLip(seed, repX[id], repY[id])) {
          int neighbor = shoulderLocker(repX[id], repY[id], separate, occupancy, width, height);
          if (neighbor >= 0 && neighbor < count) {
            curve = (curve + pre[neighbor]) / 2;
            if (curve >= Lockers.T_LAND) {
              curve = Lockers.T_LAND - 1;
            }
          }
        }
        thickness = curve;
        changed = true;
      }
      if (colD[id] < COLLIDE_RADIUS) {
        thickness += collideBonus(colD[id]);
        if (thickness >= Lockers.T_LAND) {
          thickness = Lockers.T_LAND - 1;
        }
        changed = true;
      }
      if (changed) {
        next[id] = thickness;
      }
    }
    return new Lockers(next);
  }

  private static int shoulderLocker(
      int x, int y, int[][] separate, Grid occupancy, int width, int height) {
    int here = separate[y][x];
    int bestD = here;
    int bestId = -1;
    for (int[] dir : SphereTopology.ORTHO) {
      int[] n = SphereTopology.neighbor(x, y, dir[0], dir[1], width, height);
      int nd = separate[n[1]][n[0]];
      if (nd < 0) {
        nd = DIVERGE_RADIUS + 1;
      }
      if (nd <= here) {
        continue;
      }
      int nid = occupancy.get(n[0], n[1]);
      if (nd > bestD || bestId < 0 || (nd == bestD && nid < bestId)) {
        bestD = nd;
        bestId = nid;
      }
    }
    return bestId;
  }

  private static int[][] distances(
      Boundaries boundaries, BoundaryKind kind, int radius, int width, int height) {
    int[][] dist = new int[height][width];
    for (int y = 0; y < height; y++) {
      Arrays.fill(dist[y], -1);
    }
    ArrayDeque<Integer> queue = new ArrayDeque<>();
    for (BoundaryContact contact : boundaries.contacts()) {
      if (contact.kind() != kind) {
        continue;
      }
      seed(dist, queue, contact.x(), contact.y(), width);
      int[] neighbor =
          SphereTopology.neighbor(
              contact.x(), contact.y(), contact.nx(), contact.ny(), width, height);
      seed(dist, queue, neighbor[0], neighbor[1], width);
    }
    while (!queue.isEmpty()) {
      int packed = queue.removeFirst();
      int x = packed % width;
      int y = packed / width;
      int d = dist[y][x];
      if (d >= radius) {
        continue;
      }
      for (int[] dir : SphereTopology.ORTHO) {
        int[] n = SphereTopology.neighbor(x, y, dir[0], dir[1], width, height);
        if (dist[n[1]][n[0]] >= 0) {
          continue;
        }
        dist[n[1]][n[0]] = d + 1;
        queue.addLast(n[1] * width + n[0]);
      }
    }
    return dist;
  }

  private static void seed(int[][] dist, ArrayDeque<Integer> queue, int x, int y, int width) {
    if (dist[y][x] >= 0) {
      return;
    }
    dist[y][x] = 0;
    queue.addLast(y * width + x);
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
