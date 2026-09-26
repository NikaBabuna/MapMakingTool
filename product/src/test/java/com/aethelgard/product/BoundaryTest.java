/*
 * File: product/src/test/java/com/aethelgard/product/BoundaryTest.java
 * Purpose: Proves how plates meet: every contact found once and classified, who loses a collision, and each contact's budget and intent
 * Audience: Agents / CI
 * Update when: Boundary tracing, crust precedence, AreaFlux, or MotionIntent changes
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoundaryTest {

  /** Proves F-068 FR-25 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Every contact is found once and classified from the two velocities alone, across the seam and the poles")
  void everyContactIsFoundOnceAndClassified() {
    // Two grown worlds, and east | west halves that also meet across the seam and across both poles.
    List<Object[]> worlds = new ArrayList<>();
    for (WorldSpec spec : List.of(new WorldSpec(32, 16, 7L), new WorldSpec(40, 20, 2L))) {
      ProductSession session = new ProductSession(spec);
      session.advance(2);
      worlds.add(new Object[] {spec.toString(), session.plates(), session.plateVelocities()});
    }
    int[][] halves = new int[6][8];
    for (int[] row : halves) {
      for (int x = 0; x < 8; x++) {
        row[x] = x < 4 ? 0 : 1;
      }
    }
    worlds.add(new Object[] {"halves 8x6", new Grid(halves), new PlateVelocities(0L, new int[] {1, -1}, new int[] {1, 0})});

    List<String> problems = new ArrayList<>();
    for (Object[] world : worlds) {
      String spec = (String) world[0];
      Grid plates = (Grid) world[1];
      PlateVelocities v = (PlateVelocities) world[2];
      Boundaries contacts = Boundaries.trace(plates, v);
      int w = plates.width();
      int h = plates.height();

      // 1. Every contact joins two different plates across one edge, and its kind is the sign of n·(va − vb).
      Map<String, Integer> seen = new HashMap<>();
      for (BoundaryContact c : contacts.contacts()) {
        int[] b = neighbour(c.x(), c.y(), c.nx(), c.ny(), w, h);
        assertEquals(c.plateA(), plates.get(c.x(), c.y()));
        assertEquals(c.plateB(), plates.get(b[0], b[1]));
        assertTrue(c.plateA() != c.plateB());
        int closing = c.nx() * (v.vx(c.plateA()) - v.vx(c.plateB())) + c.ny() * (v.vy(c.plateA()) - v.vy(c.plateB()));
        BoundaryKind expected = closing > 0 ? BoundaryKind.COLLIDE : closing < 0 ? BoundaryKind.SEPARATE : BoundaryKind.PASS_BY;
        assertEquals(expected, c.kind(), c.toString());
        seen.merge(edge(c.x(), c.y(), b[0], b[1], w), 1, Integer::sum);
      }

      // 2. Every edge between two plates is found once, including those across the seam and the poles.
      List<String> missing = new ArrayList<>();
      List<String> repeated = new ArrayList<>();
      Set<String> edges = new HashSet<>();
      int seamEdges = 0;
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          for (int[] n : new int[][] {{1, 0}, {0, 1}, {0, -1}}) {
            if (n[1] == -1 && y != 0) {
              continue; // north steps matter only across the north pole; the rest are south steps seen from above
            }
            int[] b = neighbour(x, y, n[0], n[1], w, h);
            String key = edge(x, y, b[0], b[1], w);
            if (plates.get(x, y) == plates.get(b[0], b[1]) || !edges.add(key)) {
              continue;
            }
            String where = "(" + x + "," + y + ")-(" + b[0] + "," + b[1] + ")";
            int found = seen.getOrDefault(key, 0);
            if (found == 0) {
              missing.add(where);
            } else if (found > 1) {
              repeated.add(where + " x" + found);
            }
            seamEdges += x == w - 1 && n[0] == 1 ? 1 : 0;
          }
        }
      }
      assertTrue(seamEdges > 0, spec + ": the scenario crosses the seam");
      if (!missing.isEmpty() || !repeated.isEmpty()) {
        problems.add(spec + ": never found " + missing + "; found more than once " + repeated);
      }
    }
    assertTrue(problems.isEmpty(), String.join(System.lineSeparator(), problems));
  }

  /** Proves F-068 FR-26 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A continent never loses a collision to ocean, even when its plate is smaller")
  void continentNeverLosesToOcean() {
    Grid occupancy = Occupancy.seed(2, 1);
    PlateRegistry registry = new PlateRegistry(0L, new int[] {1, 10}, new int[] {1, -1}, new int[] {0, 0});
    BoundaryContact contact = new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE);

    Lockers continentWest = new Lockers(new int[] {16, 8});
    assertEquals(1, CrustPrecedence.collideLoser(contact, occupancy, continentWest, registry, 2, 1), "the ocean (plate 1) loses");
    Lockers continentEast = new Lockers(new int[] {8, 20});
    assertEquals(0, CrustPrecedence.collideLoser(contact, occupancy, continentEast, registry, 2, 1), "the ocean (plate 0) loses");
  }

  /** Proves F-068 FR-26 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Of two ocean plates the smaller loses; on equal area the lower id loses")
  void smallerOceanPlateLoses() {
    Grid occupancy = Occupancy.seed(3, 1);
    Lockers ocean = Lockers.oceanic(3);
    PlateRegistry registry = new PlateRegistry(0L, new int[] {10, 3, 10}, new int[] {0, 0, 0}, new int[] {0, 0, 0});

    BoundaryContact smallerEast = new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE);
    assertEquals(1, CrustPrecedence.collideLoser(smallerEast, occupancy, ocean, registry, 3, 1));
    BoundaryContact smallerWest = new BoundaryContact(1, 0, 1, 0, 1, 2, BoundaryKind.COLLIDE);
    assertEquals(1, CrustPrecedence.collideLoser(smallerWest, occupancy, ocean, registry, 3, 1));
    BoundaryContact equal = new BoundaryContact(0, 0, 1, 0, 2, 0, BoundaryKind.COLLIDE);
    assertEquals(0, CrustPrecedence.collideLoser(equal, occupancy, ocean, registry, 3, 1));
  }

  /** Proves F-068 FR-26 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Every contact adds its budget and its intent, and the budgets conserve area")
  void everyContactGetsBudgetAndIntent() {
    Grid occupancy = Occupancy.seed(4, 4);
    Lockers ocean = Lockers.oceanic(16);
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {4, 4, 5, 9, 4}, new int[] {-1, 1, 0, 0, 0}, new int[] {0, 0, 1, -1, 0});
    Boundaries contacts =
        new Boundaries(List.of(
            new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.SEPARATE),
            new BoundaryContact(2, 1, 0, 1, 2, 3, BoundaryKind.COLLIDE),
            new BoundaryContact(3, 3, 1, 0, 4, 0, BoundaryKind.PASS_BY)));

    AreaFlux flux = AreaFlux.from(contacts, registry, occupancy, ocean);
    // Rift: both plates +1, the sink −2. Collision: the smaller plate 2 −1, the sink +1. Pass-by: nothing.
    assertEquals(List.of(1, 1, -1, 0, 0), List.of(flux.deltaArea(0), flux.deltaArea(1), flux.deltaArea(2), flux.deltaArea(3), flux.deltaArea(4)));
    assertEquals(-1, flux.sinkDelta());
    assertEquals(0, flux.netCells());

    MotionIntent intent = MotionIntent.from(contacts, registry, occupancy, ocean);
    // Rift pushes both apart along n = (1,0). Collision along n = (0,1): the winner 3 is pushed away; the loser 2 is left alone.
    assertEquals(List.of(-1, 0), List.of(intent.ix(0), intent.iy(0)));
    assertEquals(List.of(1, 0), List.of(intent.ix(1), intent.iy(1)));
    assertEquals(List.of(0, 0), List.of(intent.ix(2), intent.iy(2)));
    assertEquals(List.of(0, 1), List.of(intent.ix(3), intent.iy(3)));
    assertEquals(List.of(0, 0), List.of(intent.ix(4), intent.iy(4)));
  }

  /** The neighbour on the sphere: wrap in x; across a pole, the antipodal cell on the same row. */
  private static int[] neighbour(int x, int y, int dx, int dy, int w, int h) {
    int ny = y + dy;
    if (ny < 0 || ny >= h) {
      return new int[] {(x + w / 2) % w, y};
    }
    return new int[] {Math.floorMod(x + dx, w), ny};
  }

  /** One key per undirected edge. */
  private static String edge(int x, int y, int bx, int by, int w) {
    int a = y * w + x;
    int b = by * w + bx;
    return Math.min(a, b) + "-" + Math.max(a, b);
  }
}
