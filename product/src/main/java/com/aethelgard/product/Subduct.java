/*
 * File: product/src/main/java/com/aethelgard/product/Subduct.java
 * Purpose: Occupancy consume at COLLIDE + unshare SEPARATE contact-locker copies (F-058)
 * Audience: PlateKinematics / tests
 * Update when: Subduction occupancy rule changes
 */

package com.aethelgard.product;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * After occupancy remap: oceanic collide losers take the surviving locker; extra copies of a
 * SEPARATE contact locker become {@link PlateKinematics#UNRESOLVED} so {@link RidgeCreate} mints
 * thin ocean instead of stretching the border.
 */
public final class Subduct {

  private Subduct() {}

  /**
   * Mutates {@code occDest} in standing-advection destination space using standing contacts and
   * the same post-flux plates / velocities {@link PlateKinematics} used to write occupancy.
   */
  public static void correct(
      int[][] occDest,
      Grid standingOccupancy,
      Boundaries standingBoundaries,
      Lockers lockers,
      PlateRegistry registry,
      Grid postFluxPlates,
      PlateVelocities velocities) {
    Objects.requireNonNull(occDest, "occDest");
    Objects.requireNonNull(standingOccupancy, "standingOccupancy");
    Objects.requireNonNull(standingBoundaries, "standingBoundaries");
    Objects.requireNonNull(lockers, "lockers");
    Objects.requireNonNull(registry, "registry");
    Objects.requireNonNull(postFluxPlates, "postFluxPlates");
    Objects.requireNonNull(velocities, "velocities");
    int width = standingOccupancy.width();
    int height = standingOccupancy.height();
    consumeCollide(
        occDest,
        standingOccupancy,
        standingBoundaries,
        lockers,
        registry,
        postFluxPlates,
        velocities,
        width,
        height);
    unshareSeparate(
        occDest, standingOccupancy, standingBoundaries, postFluxPlates, velocities, width, height);
  }

  private static void consumeCollide(
      int[][] occDest,
      Grid standingOccupancy,
      Boundaries standingBoundaries,
      Lockers lockers,
      PlateRegistry registry,
      Grid postFluxPlates,
      PlateVelocities velocities,
      int width,
      int height) {
    for (BoundaryContact c : standingBoundaries.contacts()) {
      if (c.kind() != BoundaryKind.COLLIDE) {
        continue;
      }
      int lose = CrustPrecedence.collideLoser(c, standingOccupancy, lockers, registry, width, height);
      if (lose == CrustPrecedence.NONE) {
        continue;
      }
      int[] b = SphereTopology.neighbor(c.x(), c.y(), c.nx(), c.ny(), width, height);
      int lx;
      int ly;
      int wx;
      int wy;
      if (lose == c.plateA()) {
        lx = c.x();
        ly = c.y();
        wx = b[0];
        wy = b[1];
      } else {
        lx = b[0];
        ly = b[1];
        wx = c.x();
        wy = c.y();
      }
      int destPlate = postFluxPlates.get(lx, ly);
      if (destPlate < 0 || destPlate >= velocities.count()) {
        continue;
      }
      int[] step =
          SphereTopology.advectCell(
              lx, ly, velocities.vx(destPlate), velocities.vy(destPlate), width, height);
      occDest[step[1]][step[0]] = standingOccupancy.get(wx, wy);
    }
  }

  private static void unshareSeparate(
      int[][] occDest,
      Grid standingOccupancy,
      Boundaries standingBoundaries,
      Grid postFluxPlates,
      PlateVelocities velocities,
      int width,
      int height) {
    Map<Integer, Long> keepDest = new HashMap<>();
    for (BoundaryContact c : standingBoundaries.contacts()) {
      if (c.kind() != BoundaryKind.SEPARATE) {
        continue;
      }
      int[] b = SphereTopology.neighbor(c.x(), c.y(), c.nx(), c.ny(), width, height);
      rememberKeep(keepDest, c.x(), c.y(), standingOccupancy, postFluxPlates, velocities, width, height);
      rememberKeep(keepDest, b[0], b[1], standingOccupancy, postFluxPlates, velocities, width, height);
    }
    if (keepDest.isEmpty()) {
      return;
    }
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int id = occDest[y][x];
        if (id < 0) {
          continue;
        }
        Long keep = keepDest.get(id);
        if (keep != null && keep != key(x, y)) {
          occDest[y][x] = PlateKinematics.UNRESOLVED;
        }
      }
    }
  }

  private static void rememberKeep(
      Map<Integer, Long> keepDest,
      int x,
      int y,
      Grid standingOccupancy,
      Grid postFluxPlates,
      PlateVelocities velocities,
      int width,
      int height) {
    int locker = standingOccupancy.get(x, y);
    int plate = postFluxPlates.get(x, y);
    if (plate < 0 || plate >= velocities.count()) {
      return;
    }
    int[] step =
        SphereTopology.advectCell(x, y, velocities.vx(plate), velocities.vy(plate), width, height);
    keepDest.putIfAbsent(locker, key(step[0], step[1]));
  }

  private static long key(int x, int y) {
    return (((long) x) << 32) | (y & 0xffffffffL);
  }
}
