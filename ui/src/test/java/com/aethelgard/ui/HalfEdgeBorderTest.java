/*
 * File: ui/src/test/java/com/aethelgard/ui/HalfEdgeBorderTest.java
 * Purpose: F-044/F-045 witness — half-edge core + bold dilated Plates stroke
 * Audience: Agents / CI
 * Update when: Border paint FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.Grid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HalfEdgeBorderTest {

  @Test
  @DisplayName("FR-4: vertical contact — core on west; bold stroke ≥2 cells")
  void verticalContactBoldStroke() {
    // Height ≥2 so south polar wrap does not link the single row to itself.
    Grid plates =
        new Grid(
            new int[][] {
              {0, 0, 1, 1},
              {0, 0, 1, 1},
            });
    assertFalse(ElevationRaster.isPlateBoundaryCore(plates, 0, 0));
    assertTrue(ElevationRaster.isPlateBoundaryCore(plates, 1, 0));
    assertFalse(ElevationRaster.isPlateBoundaryCore(plates, 2, 0));
    assertTrue(ElevationRaster.isPlateBoundary(plates, 1, 0));
    assertTrue(ElevationRaster.isPlateBoundary(plates, 2, 0));
    assertEquals(ElevationRaster.PLATE_BOUNDARY_RGB, ElevationRaster.plateBoundaryCell(plates, 1, 0));
    assertEquals(ElevationRaster.PLATE_BOUNDARY_RGB, ElevationRaster.plateBoundaryCell(plates, 2, 0));
  }

  @Test
  @DisplayName("FR-4: horizontal contact — core on north; bold covers south")
  void horizontalContactBoldStroke() {
    Grid plates =
        new Grid(
            new int[][] {
              {0, 0},
              {1, 1},
            });
    assertTrue(ElevationRaster.isPlateBoundaryCore(plates, 0, 0));
    assertFalse(ElevationRaster.isPlateBoundaryCore(plates, 0, 1));
    assertTrue(ElevationRaster.isPlateBoundary(plates, 0, 0));
    assertTrue(ElevationRaster.isPlateBoundary(plates, 0, 1));
  }

  @Test
  @DisplayName("FR-5 overlay: Overlay darkens on bold stroke")
  void overlayMatchesBoldStroke() {
    Grid elev = Grid.zeros(4, 1);
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}});
    int suture = ElevationRaster.overlayCell(elev, plates, 1, 0);
    assertEquals(ElevationRaster.darken(ElevationRaster.elevationCell(elev, 1, 0)), suture);
  }
}
