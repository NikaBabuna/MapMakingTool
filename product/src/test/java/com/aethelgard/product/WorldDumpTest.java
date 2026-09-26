/*
 * File: product/src/test/java/com/aethelgard/product/WorldDumpTest.java
 * Purpose: Proves that the world dump is a function of the settled fields, and that printing it changes nothing
 * Audience: Agents / CI
 * Update when: WorldDump changes
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WorldDumpTest {

  /** Proves F-068 FR-45 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Equal fields give equal text, and a change in one cell or one column changes it")
  void dumpIsAFunctionOfTheFields() {
    ProductSession session = new ProductSession(new WorldSpec(12, 6, 4L));
    session.advance(2);
    WorldSpec spec = session.spec();
    Grid elevation = session.elevation();
    Grid occupancy = (Grid) session.field(WorldFields.OCCUPANCY);
    Lockers lockers = (Lockers) session.field(WorldFields.LOCKERS);

    String text = dump(spec, elevation, occupancy, lockers, session);
    assertEquals(text, dump(spec, copy(elevation, -1, -1, 0), occupancy, lockers, session), "equal fields, equal text");
    assertEquals(session.settledWorld(), text);

    Grid oneCellHigher = copy(elevation, 3, 2, elevation.get(3, 2) + 1);
    assertNotEquals(text, dump(spec, oneCellHigher, occupancy, lockers, session));

    int[] thicker = lockers.thicknesses();
    thicker[occupancy.get(0, 0)] += 1;
    assertNotEquals(text, dump(spec, elevation, occupancy, new Lockers(thicker), session));
  }

  /** Proves F-068 FR-45 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Printing the dump changes no field and no diagnostic")
  void printingChangesNothing() {
    ProductSession session = new ProductSession(new WorldSpec(12, 6, 4L));
    session.advance(2);
    String report = session.diagnostics().listReport();
    String first = session.settledWorld();

    String second = session.settledWorld();

    assertEquals(first, second);
    assertEquals(2, session.stepIndex());
    assertEquals(report, session.diagnostics().listReport());
  }

  private static String dump(WorldSpec spec, Grid elevation, Grid occupancy, Lockers lockers, ProductSession s) {
    return WorldDump.format(spec, s.stepIndex(), elevation, s.plates(), occupancy, lockers,
        s.plateVelocities(), s.plateRegistry(), s.boundaries(), s.areaFlux(), s.motionIntent());
  }

  /** A copy of {@code grid}, with cell (x, y) set to {@code value} when x is not negative. */
  private static Grid copy(Grid grid, int x, int y, int value) {
    int[][] cells = new int[grid.height()][grid.width()];
    for (int j = 0; j < grid.height(); j++) {
      for (int i = 0; i < grid.width(); i++) {
        cells[j][i] = grid.get(i, j);
      }
    }
    if (x >= 0) {
      cells[y][x] = value;
    }
    return new Grid(cells);
  }
}
