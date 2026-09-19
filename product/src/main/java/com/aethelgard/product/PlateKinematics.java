/*
 * File: product/src/main/java/com/aethelgard/product/PlateKinematics.java
 * Purpose: Kinematics Sub-System — advect plate ownership each generation Step
 * Audience: Product kinematics EngineSystem
 * Update when: Advection / leftover-fill rule changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import java.util.Set;

/**
 * Reads standing {@code plates} and CONSTANT {@code plate_velocity}; writes a new plates grid.
 * Cells translate by their plate's {@code (vx, vy)} with wrap on X only; Y off-map claims are
 * dropped (polar edge). Cells with zero or more than one claimant are filled by nearest moved site
 * (cylindrical distance; lower index on ties).
 *
 * <p>Does not write elevation. Collision uplift in the same Step reads the same standing plates.
 */
public final class PlateKinematics implements SubSystem {

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
    Grid plates = requireGrid(io.readPool(WorldFields.PLATES), WorldFields.PLATES);
    PlateVelocities velocities = requireVelocities(io.readPool(WorldFields.PLATE_VELOCITY));
    int generationIndex = Math.toIntExact(io.poolValue()) - 1;
    io.write(WorldFields.PLATES, advect(plates, velocities, generationIndex));
  }

  /**
   * One generation of advection. {@code generationIndex} is {@code 1} on the first tectonics tick
   * (moved sites = original + {@code G * velocity}, wrapped).
   */
  public static Grid advect(Grid plates, PlateVelocities velocities, int generationIndex) {
    if (plates == null || velocities == null) {
      throw new NullPointerException("plates and velocities");
    }
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
    int[] siteX = new int[n];
    int[] siteY = new int[n];
    for (int i = 0; i < n; i++) {
      siteX[i] = velocities.movedSiteX(width, i, generationIndex);
      siteY[i] = velocities.movedSiteY(height, i, generationIndex);
    }
    Grid fill = Plates.assign(width, height, siteX, siteY);
    int[][] next = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        next[y][x] = claims[y][x] == 1 ? who[y][x] : fill.get(x, y);
      }
    }
    return new Grid(next);
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
