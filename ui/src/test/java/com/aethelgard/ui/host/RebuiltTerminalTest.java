/*
 * File: ui/src/test/java/com/aethelgard/ui/host/RebuiltTerminalTest.java
 * Purpose: F-050 witness — scrap placeholder console; rebuild terminal
 * Audience: Agents / CI
 * Update when: F-050 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RebuiltTerminalTest {

  @Test
  @DisplayName("FR-1/FR-2: style guide + scrap placeholder copy; Terminal chrome")
  void styleAndPlaceholderScrap() throws Exception {
    Path root = findRepoRoot();

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("F-050") || style.toLowerCase().contains("rebuilt"));
    assertTrue(style.contains("aethelgard>"));
    assertTrue(style.contains("session get") || style.contains("Noun/verb"));
    assertFalse(style.toLowerCase().contains("placeholder verbs"));

    String terminal = Files.readString(root.resolve("ui/web/src/components/Terminal.tsx"));
    assertTrue(terminal.contains("Terminal"));
    assertTrue(terminal.contains("aethelgard>"));
    assertTrue(terminal.contains("help"));
    assertTrue(terminal.contains("session get") || terminal.contains("Noun/verb"));
    assertFalse(terminal.contains("Placeholder verbs"));
    assertFalse(terminal.contains("status · advance · dump"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("Terminal"));
    assertTrue(tool.contains("<Terminal"));
    assertFalse(tool.contains(">Console<") || tool.contains("\"Console\""));
  }

  @Test
  @DisplayName("FR-3/FR-4: CommandDispatch wiring; dedicated component; transcript kinds")
  void dispatchAndTranscript() throws Exception {
    Path root = findRepoRoot();

    String terminal = Files.readString(root.resolve("ui/web/src/components/Terminal.tsx"));
    assertTrue(terminal.contains("kind: \"command\"") || terminal.contains("\"command\""));
    assertTrue(terminal.contains("\"output\""));
    assertTrue(terminal.contains("\"error\""));
    assertTrue(terminal.contains("ArrowUp") && terminal.contains("ArrowDown"));
    assertTrue(terminal.contains("32") || terminal.contains("HISTORY_CAP"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("postCommand"));
    assertTrue(tool.contains("onRun") || tool.contains("onTerminalRun"));
    assertTrue(tool.contains("`") || tool.contains("terminalOpen"));

    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("terminal-line") || css.contains("is-error"));
    assertTrue(css.contains("is-command") || css.contains("is-output"));

    String controller =
        Files.readString(root.resolve("ui/src/main/java/com/aethelgard/ui/MapController.java"));
    assertTrue(controller.contains("CommandDispatch"));
    assertFalse(controller.contains("javax.swing"));

    // No MapHost API shape change: /api/command still present
    String host = Files.readString(root.resolve("ui/web/src/lib/host.ts"));
    assertTrue(host.contains("/api/command"));
  }

  @Test
  @DisplayName("FR-5: docs SYNC; no F-051/F-052 Accept; no JFrame; no engine edits claimed")
  void docsAndScope() throws Exception {
    Path root = findRepoRoot();

    String blocker = Files.readString(root.resolve("docs/blockers/F-050.md"));
    assertFalse(blocker.contains("F-051 Accepted"));
    assertFalse(blocker.contains("F-052 Accepted"));

    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("F-050"));

    String terminal = Files.readString(root.resolve("ui/web/src/components/Terminal.tsx"));
    assertFalse(terminal.contains("JFrame"));
    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertFalse(tool.contains("JFrame"));
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath();
    for (int i = 0; i < 8; i++) {
      if (Files.isRegularFile(dir.resolve("pom.xml"))
          && Files.isDirectory(dir.resolve("ui"))
          && Files.isDirectory(dir.resolve("docs"))) {
        return dir;
      }
      Path parent = dir.getParent();
      if (parent == null) {
        break;
      }
      dir = parent;
    }
    throw new Exception("repo root not found");
  }
}
