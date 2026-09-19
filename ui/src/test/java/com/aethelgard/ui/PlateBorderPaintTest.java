/*
 * File: ui/src/test/java/com/aethelgard/ui/PlateBorderPaintTest.java
 * Purpose: F-044 witness — east/south plate boundary stroke
 * Audience: Agents / CI
 * Update when: F-044 paint FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.Grid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlateBorderPaintTest {

  @Test
  @DisplayName("FR-5: boundary only on east/south foreign edge")
  void eastSouthStrokeOnly() {
    Grid row = new Grid(new int[][] {{0, 0, 1, 1}});
    assertTrue(ElevationRaster.isPlateBoundary(row, 1, 0), "east foreign");
    assertFalse(ElevationRaster.isPlateBoundary(row, 2, 0), "same east; no south");
    assertEquals(ElevationRaster.PLATE_BOUNDARY_RGB, ElevationRaster.plateBoundaryCell(row, 1, 0));
    assertEquals(ElevationRaster.PLATE_INTERIOR_RGB, ElevationRaster.plateBoundaryCell(row, 2, 0));
  }
}
