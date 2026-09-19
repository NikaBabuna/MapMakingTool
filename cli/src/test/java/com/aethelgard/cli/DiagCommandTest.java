/*
 * File: cli/src/test/java/com/aethelgard/cli/DiagCommandTest.java
 * Purpose: F-042 witness — stats / diag control verbs
 * Audience: Agents / CI
 * Update when: F-042 FRs change
 */

package com.aethelgard.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.DiagnosticIds;
import com.aethelgard.product.ProductSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiagCommandTest {

  @Test
  @DisplayName("FR-4: stats and diag list/on/off/clear")
  void statsAndDiagControl() {
    ProductSession session = ProductSession.ofDefault();
    session.advance();

    CliResult stats = CommandDispatch.execute(session, "stats");
    assertEquals(0, stats.exitCode());
    assertTrue(stats.output().contains(DiagnosticIds.ADVANCE_WALL));

    CliResult list = CommandDispatch.execute(session, "diag list");
    assertEquals(0, list.exitCode());
    assertTrue(list.output().contains(DiagnosticIds.HEAP_USED));
    assertTrue(list.output().contains("enabled=true"));

    CliResult off = CommandDispatch.execute(session, "diag off " + DiagnosticIds.ADVANCE_WALL);
    assertEquals(0, off.exitCode());
    session.advance();
    assertEquals(1, session.diagnostics().get(DiagnosticIds.ADVANCE_WALL).size());

    CliResult on = CommandDispatch.execute(session, "diag on " + DiagnosticIds.ADVANCE_WALL);
    assertEquals(0, on.exitCode());
    session.advance();
    assertEquals(2, session.diagnostics().get(DiagnosticIds.ADVANCE_WALL).size());

    CliResult clearOne =
        CommandDispatch.execute(session, "diag clear " + DiagnosticIds.ADVANCE_WALL);
    assertEquals(0, clearOne.exitCode());
    assertEquals(0, session.diagnostics().get(DiagnosticIds.ADVANCE_WALL).size());

    session.advance();
    CliResult clearAll = CommandDispatch.execute(session, "diag clear");
    assertEquals(0, clearAll.exitCode());
    assertEquals(0, session.diagnostics().get(DiagnosticIds.HEAP_USED).size());
  }
}
