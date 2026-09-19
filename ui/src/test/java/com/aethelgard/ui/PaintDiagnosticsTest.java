/*
 * File: ui/src/test/java/com/aethelgard/ui/PaintDiagnosticsTest.java
 * Purpose: F-042 witness — MapController paint timing into session hub
 * Audience: Agents / CI
 * Update when: F-042 FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.DiagnosticIds;
import com.aethelgard.product.WorldSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaintDiagnosticsTest {

  @Test
  @DisplayName("FR-3: capture records paint.wall on session hub")
  void recordsPaintOnHub() {
    MapController controller = new MapController(WorldSpec.DEFAULT);
    assertTrue(controller.lastPaintNanos() > 0);
    assertTrue(controller.session().diagnostics().get(DiagnosticIds.PAINT_WALL).size() >= 1);
    Long last = controller.session().diagnostics().get(DiagnosticIds.PAINT_WALL).latest();
    assertTrue(last != null && last > 0);
    controller.advance();
    assertTrue(controller.session().diagnostics().get(DiagnosticIds.PAINT_WALL).size() >= 2);
  }
}
