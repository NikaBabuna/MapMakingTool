/*
 * File: product/src/test/java/com/aethelgard/product/DiagnosticsHubTest.java
 * Purpose: F-042 witness — controllable diagnostics hub
 * Audience: Agents / CI
 * Update when: F-042 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiagnosticsHubTest {

  @Test
  @DisplayName("FR-1/FR-2: defaults register; advance samples when enabled")
  void advanceRecordsWhenEnabled() {
    ProductSession session = ProductSession.ofDefault();
    DiagnosticsHub hub = session.diagnostics();
    assertTrue(hub.has(DiagnosticIds.ADVANCE_WALL));
    assertTrue(hub.has(DiagnosticIds.HEAP_USED));
    assertTrue(hub.has(DiagnosticIds.HEAP_MAX));
    assertTrue(hub.has(DiagnosticIds.PAINT_WALL));
    assertEquals(0, hub.get(DiagnosticIds.ADVANCE_WALL).size());

    session.advance(2);
    assertEquals(2, hub.get(DiagnosticIds.ADVANCE_WALL).size());
    assertEquals(2, hub.get(DiagnosticIds.HEAP_USED).size());
    assertEquals(2, hub.get(DiagnosticIds.HEAP_MAX).size());
    assertNotNull(hub.get(DiagnosticIds.ADVANCE_WALL).latest());
    assertTrue(hub.get(DiagnosticIds.ADVANCE_WALL).latest() > 0);
    assertTrue(hub.report().contains(DiagnosticIds.ADVANCE_WALL));
  }

  @Test
  @DisplayName("FR-5: disable stops samples; enable resumes; world dump unchanged by diag")
  void enableDisableDoesNotAffectWorld() {
    ProductSession a = ProductSession.ofDefault();
    a.advance(3);
    String dumpA = a.settledWorld();

    ProductSession b = ProductSession.ofDefault();
    b.diagnostics().setEnabled(DiagnosticIds.ADVANCE_WALL, false);
    b.diagnostics().setEnabled(DiagnosticIds.HEAP_USED, false);
    b.diagnostics().setEnabled(DiagnosticIds.HEAP_MAX, false);
    b.advance(2);
    assertEquals(0, b.diagnostics().get(DiagnosticIds.ADVANCE_WALL).size());
    b.diagnostics().setEnabled(DiagnosticIds.ADVANCE_WALL, true);
    b.advance(1);
    assertEquals(1, b.diagnostics().get(DiagnosticIds.ADVANCE_WALL).size());
    assertEquals(dumpA, b.settledWorld());
  }

  @Test
  @DisplayName("FR-1: clear and ring capacity")
  void clearAndCapacity() {
    DiagnosticsHub hub = DiagnosticsHub.withDefaults();
    hub.record(DiagnosticIds.ADVANCE_WALL, 10);
    hub.record(DiagnosticIds.ADVANCE_WALL, 20);
    assertEquals(2, hub.get(DiagnosticIds.ADVANCE_WALL).size());
    hub.clear(DiagnosticIds.ADVANCE_WALL);
    assertEquals(0, hub.get(DiagnosticIds.ADVANCE_WALL).size());
    assertNull(hub.get(DiagnosticIds.ADVANCE_WALL).latest());
    assertEquals(DiagnosticsHub.DEFAULT_CAPACITY, hub.get(DiagnosticIds.ADVANCE_WALL).capacity());
    assertFalse(hub.listReport().isBlank());
  }
}
