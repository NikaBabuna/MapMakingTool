/*
 * File: product/src/test/java/com/aethelgard/product/SessionDiagnosticsTest.java
 * Purpose: Proves what the session's diagnostics record, how much they keep, and that they never change the world
 * Audience: Agents / CI
 * Update when: DiagnosticsHub, its collectors, or the phase timings change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionDiagnosticsTest {

  private static final WorldSpec SPEC = new WorldSpec(16, 8, 3L);

  /** Proves F-068 FR-44 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Advance and phase timings are recorded only while enabled, and clear empties them")
  void collectorsRecordOnlyWhileEnabled() {
    ProductSession session = new ProductSession(SPEC);
    DiagnosticsHub hub = session.diagnostics();
    session.advance(2);
    assertEquals(2, size(hub, DiagnosticIds.ADVANCE_WALL));
    for (String phase : new String[] {
        DiagnosticIds.PHASE_TRACE, DiagnosticIds.PHASE_INTERACTION, DiagnosticIds.PHASE_INTEGRATE,
        DiagnosticIds.PHASE_APPLY, DiagnosticIds.PHASE_OROGENY, DiagnosticIds.PHASE_ISOSTASY}) {
      assertEquals(2, size(hub, phase), phase);
    }

    hub.setEnabled(DiagnosticIds.PHASE_TRACE, false);
    session.advance(1);
    assertEquals(2, size(hub, DiagnosticIds.PHASE_TRACE), "disabled: no new sample");
    assertEquals(3, size(hub, DiagnosticIds.ADVANCE_WALL), "the others kept recording");

    hub.clear(DiagnosticIds.ADVANCE_WALL);
    assertEquals(0, size(hub, DiagnosticIds.ADVANCE_WALL));
    hub.setEnabled(DiagnosticIds.PHASE_TRACE, true);
    session.advance(1);
    assertEquals(3, size(hub, DiagnosticIds.PHASE_TRACE));
    assertEquals(1, size(hub, DiagnosticIds.ADVANCE_WALL));
  }

  /** Proves F-068 FR-44 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Each ring keeps at most 64 samples, the newest")
  void ringsKeepAtMost64Samples() {
    RingDiagnosticCollector ring = new RingDiagnosticCollector("test.ring", 64);
    for (long v = 1; v <= 100; v++) {
      ring.record(v);
    }
    assertEquals(64, ring.size());
    assertEquals(100L, ring.latest());

    ProductSession session = new ProductSession(new WorldSpec(4, 4, 0L));
    session.advance(70);
    assertEquals(64, size(session.diagnostics(), DiagnosticIds.ADVANCE_WALL));
  }

  /** Proves F-068 FR-44 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The world is the same with diagnostics enabled, disabled, or cleared")
  void diagnosticsNeverChangeTheWorld() {
    ProductSession recorded = new ProductSession(SPEC);
    ProductSession silent = new ProductSession(SPEC);
    for (String id : silent.diagnostics().ids()) {
      silent.diagnostics().setEnabled(id, false);
    }
    recorded.advance(3);
    recorded.diagnostics().clearAll();
    recorded.advance(3);
    silent.advance(6);

    assertEquals(silent.settledWorld(), recorded.settledWorld());
  }

  private static int size(DiagnosticsHub hub, String id) {
    return ((RingDiagnosticCollector) hub.get(id)).size();
  }
}
