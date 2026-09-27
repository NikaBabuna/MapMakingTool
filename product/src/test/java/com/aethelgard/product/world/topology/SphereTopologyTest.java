/*
 * File: product/src/test/java/com/aethelgard/product/world/topology/SphereTopologyTest.java
 * Purpose: Proves how the map joins as a sphere: columns wrap east to west, and a pole joins each cell to its antipode
 * Audience: Agents / CI
 * Update when: SphereTopology or polar advection changes
 */

package com.aethelgard.product.world.topology;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aethelgard.product.world.boundaries.Boundaries;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Occupancy;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.motion.PlateKinematics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SphereTopologyTest {

  private static final int W = 8;
  private static final int H = 6;

  /** Proves F-068 FR-23 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Columns wrap east to west")
  void columnsWrapEastToWest() {
    assertArrayEquals(new int[] {W - 1, 3}, SphereTopology.neighbor(0, 3, -1, 0, W, H));
    assertArrayEquals(new int[] {0, 3}, SphereTopology.neighbor(W - 1, 3, 1, 0, W, H));
    assertArrayEquals(new int[] {4, 3}, SphereTopology.neighbor(3, 3, 1, 0, W, H));
  }

  /** Proves F-068 FR-23 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Across a pole, a cell's neighbour is the antipodal cell on the same row")
  void polarNeighbourIsTheAntipode() {
    for (int x = 0; x < W; x++) {
      int antipode = (x + W / 2) % W;
      assertArrayEquals(new int[] {antipode, 0}, SphereTopology.neighbor(x, 0, 0, -1, W, H), "north of " + x);
      assertArrayEquals(new int[] {antipode, H - 1}, SphereTopology.neighbor(x, H - 1, 0, 1, W, H), "south of " + x);
    }
    assertArrayEquals(new int[] {3, 1}, SphereTopology.neighbor(3, 2, 0, -1, W, H), "an interior cell's north is plain");
  }

  /** Proves F-068 FR-24 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A plate that crosses a pole arrives at the antipodal column and its heading reverses")
  void plateCrossingAPoleTurnsBack() {
    // Plate 0 is the top row, heading north; plate 1 fills the rest and stands still.
    int[][] cells = new int[H][W];
    for (int y = 1; y < H; y++) {
      java.util.Arrays.fill(cells[y], 1);
    }
    Grid plates = new Grid(cells);
    Grid occupancy = Occupancy.seed(W, H);
    PlateVelocities velocities = new PlateVelocities(0L, new int[] {0, 0}, new int[] {-1, 0});

    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(plates, occupancy, velocities, 1, Boundaries.trace(plates, velocities));

    for (int x = 0; x < W; x++) {
      int antipode = (x + W / 2) % W;
      assertEquals(0, moved.plates().get(antipode, 0));
      assertEquals(Occupancy.id(x, 0, W), moved.occupancy().get(antipode, 0),
          "the crust of column " + x + " arrived at column " + antipode);
    }
    assertEquals(0, moved.velocities().vx(0));
    assertEquals(1, moved.velocities().vy(0), "plate 0 now heads south");
    assertEquals(0, moved.velocities().vy(1), "plate 1 did not touch a pole");
  }
}
