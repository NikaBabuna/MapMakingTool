/*
 * File: product/src/test/java/com/aethelgard/product/world/motion/PlateMotionTest.java
 * Purpose: Proves how plates move: velocities nudged by intent, rifts filled by their own plates, split plates renumbered, crumbs absorbed
 * Audience: Agents / CI
 * Update when: VelocityIntegration, advection and flood fill, or fission changes
 */

package com.aethelgard.product.world.motion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.session.ProductSession;
import com.aethelgard.product.world.boundaries.Boundaries;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.WorldSpec;
import com.aethelgard.product.world.interaction.AreaFlux;
import com.aethelgard.product.world.interaction.MotionIntent;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlateMotionTest {

  /** Proves F-068 FR-27 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Each velocity component moves at most 1 toward its intent and stays in -1, 0, 1; a zero intent leaves it alone")
  void velocityStepsTowardIntent() {
    PlateVelocities standing = new PlateVelocities(1L, new int[] {0, 1, -1, 1}, new int[] {0, 0, 1, -1});
    MotionIntent intent = new MotionIntent(new int[] {5, 0, -3, -2}, new int[] {-2, 0, 0, 4});

    PlateVelocities next = VelocityIntegration.integrate(standing, intent);

    assertEquals(1, next.vx(0));
    assertEquals(-1, next.vy(0), "one step toward a strong intent, not five");
    assertEquals(1, next.vx(1));
    assertEquals(0, next.vy(1), "zero intent leaves the component");
    assertEquals(-1, next.vx(2), "already at -1: stays in range");
    assertEquals(1, next.vy(2));
    assertEquals(0, next.vx(3), "from 1 toward a negative intent: one step to 0");
    assertEquals(0, next.vy(3), "from -1 toward a positive intent: one step to 0");
  }

  /** Proves F-068 FR-27 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("If every plate would stop, plate 0 keeps moving")
  void allStoppedKeepsOnePlateMoving() {
    PlateVelocities stopped = new PlateVelocities(0L, new int[] {0, 0, 0}, new int[] {0, 0, 0});

    PlateVelocities next = VelocityIntegration.integrate(stopped, MotionIntent.zeros(3));

    assertTrue(next.hasMovingPlate());
    assertTrue(next.vx(0) != 0 || next.vy(0) != 0, "plate 0 moves");
    assertEquals(0, next.vx(1));
    assertEquals(0, next.vy(1));
    assertEquals(0, next.vx(2));
    assertEquals(0, next.vy(2));
  }

  /** Proves F-068 FR-28 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A rift is filled only by the two plates that opened it, and after a generation every cell has a plate")
  void riftIsFilledOnlyByItsTwoPlates() {
    int w = 8;
    int h = 4;
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        cells[y][x] = x < 4 ? 0 : 1;
      }
    }
    Grid plates = new Grid(cells);
    PlateVelocities apart = new PlateVelocities(0L, new int[] {-1, 1}, new int[] {0, 0});

    Grid moved = PlateKinematics.advect(plates, apart, 1, Boundaries.trace(plates, apart)).plates();

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int id = moved.get(x, y);
        assertTrue(id == 0 || id == 1, "cell (" + x + "," + y + ") owned by " + id);
      }
    }

    // A whole generation leaves no cell without a plate.
    ProductSession session = new ProductSession(new WorldSpec(48, 24, 21L));
    for (int step = 1; step <= 8; step++) {
      session.advance(1);
      Grid p = session.plates();
      int count = session.plateRegistry().count();
      for (int y = 0; y < p.height(); y++) {
        for (int x = 0; x < p.width(); x++) {
          assertTrue(p.get(x, y) >= 0 && p.get(x, y) < count, "step " + step + " cell (" + x + "," + y + ")");
        }
      }
    }
  }

  /** Proves F-068 FR-28 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A gap that also touches a third plate is filled only by the two plates that opened it")
  void tripleJunctionGapIsNotTakenByAThirdPlate() {
    int u = PlateKinematics.UNRESOLVED;
    // Plate 2 above; plates 0 and 1 parted beneath it and left a gap that touches all three.
    int[][] cells = {
      {2, 2, 2, 2},
      {0, u, u, 1},
      {0, 0, 1, 1},
    };

    PlateKinematics.fillUnresolvedFlood(cells, 4, 3);

    for (int x = 1; x <= 2; x++) {
      assertTrue(cells[1][x] == 0 || cells[1][x] == 1, "gap cell " + x + " went to " + cells[1][x]);
    }
    assertEquals(2, cells[0][1]);
    assertEquals(2, cells[0][2]);
  }

  /** Proves F-068 FR-29 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A plate in two pieces becomes two plates that keep its velocity; ids run without gaps and the registry matches")
  void splitPlateBecomesOnePlatePerPiece() {
    int w = 8;
    int h = 5;
    // Plate 1 holds both polar rows and three columns; plate 0 is two pieces between them.
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        boolean inner = y > 0 && y < h - 1 && (x == 1 || x == 2 || x == 5 || x == 6);
        cells[y][x] = inner ? 0 : 1;
      }
    }
    Grid plates = new Grid(cells);
    PlateVelocities velocities = new PlateVelocities(0L, new int[] {1, 0}, new int[] {-1, 0});

    GeometryApplication.Result result =
        GeometryApplication.apply(plates, Boundaries.empty(), AreaFlux.zeros(2), PlateRegistry.from(plates, velocities), velocities);

    Grid after = result.plates();
    int west = after.get(1, 2);
    int east = after.get(5, 2);
    int rest = after.get(0, 0);
    assertEquals(3, result.registry().count());
    assertEquals(Set.of(0, 1, 2), Set.of(west, east, rest), "ids are 0..2 with no gap");
    assertNotEquals(west, east, "the two pieces are two plates");
    for (int piece : new int[] {west, east}) {
      assertEquals(1, result.velocities().vx(piece), "a piece keeps its parent's velocity");
      assertEquals(-1, result.velocities().vy(piece));
    }
    assertRegistryMatches(after, result.registry());
  }

  /** Proves F-068 FR-29 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A piece under 0.01% of the map joins the neighbour it shares the longest edge with; a piece at the bar stays a plate")
  void crumbJoinsItsLongestNeighbour() {
    int w = 200;
    int h = 200; // 40,000 cells: the bar is 4 cells
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        cells[y][x] = x < 100 ? 0 : 2;
      }
    }
    // Crumb A: two cells of plate 1 against the border, mostly inside plate 0.
    cells[50][98] = 1;
    cells[50][99] = 1;
    // Crumb B: two cells of plate 1 against the border, mostly inside plate 2.
    cells[80][100] = 1;
    cells[80][101] = 1;
    // Island C: four cells of plate 1 inside plate 0.
    cells[150][40] = 1;
    cells[150][41] = 1;
    cells[151][40] = 1;
    cells[151][41] = 1;
    Grid plates = new Grid(cells);
    PlateVelocities still = new PlateVelocities(0L, new int[] {0, 0, 0}, new int[] {0, 0, 0});

    GeometryApplication.Result result =
        GeometryApplication.apply(plates, Boundaries.empty(), AreaFlux.zeros(3), PlateRegistry.from(plates, still), still);

    Grid after = result.plates();
    assertEquals(after.get(97, 50), after.get(98, 50));
    assertEquals(after.get(97, 50), after.get(99, 50), "crumb A joined the plate around it");
    assertEquals(after.get(102, 80), after.get(100, 80));
    assertEquals(after.get(102, 80), after.get(101, 80), "crumb B joined the plate around it");
    assertNotEquals(after.get(39, 150), after.get(40, 150), "island C, at the bar, is still its own plate");
    assertEquals(3, result.registry().count());
    assertRegistryMatches(after, result.registry());
  }

  private static void assertRegistryMatches(Grid plates, PlateRegistry registry) {
    int[] area = new int[registry.count()];
    Set<Integer> ids = new HashSet<>();
    for (int y = 0; y < plates.height(); y++) {
      for (int x = 0; x < plates.width(); x++) {
        area[plates.get(x, y)]++;
        ids.add(plates.get(x, y));
      }
    }
    assertEquals(registry.count(), ids.size(), "every id owns a cell");
    for (int p = 0; p < registry.count(); p++) {
      assertEquals(area[p], registry.area(p), "area of plate " + p);
    }
  }
}
