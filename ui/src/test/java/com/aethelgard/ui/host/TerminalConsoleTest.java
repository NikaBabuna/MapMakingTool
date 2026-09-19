/*
 * File: ui/src/test/java/com/aethelgard/ui/host/TerminalConsoleTest.java
 * Purpose: F-040 structural witness — traditional console + G-008 close
 * Audience: Agents / CI
 * Update when: F-040 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TerminalConsoleTest {

  @Test
  @DisplayName("FR-1..FR-4/FR-7: terminal console styling, prompt, history; no JFrame")
  void traditionalTerminal() throws Exception {
    Path root = findRepoRoot();

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.toLowerCase().contains("terminal") || style.contains("phosphor"));
    assertTrue(style.contains("aethelgard>"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("aethelgard>"));
    assertTrue(tool.contains("terminal"));
    assertTrue(tool.contains("ArrowUp"));
    assertTrue(tool.contains("ArrowDown"));
    assertTrue(tool.contains("consoleHistory") || tool.contains("historyIndex"));
    assertTrue(tool.contains("postCommand"));
    assertFalse(tool.contains("JFrame"));

    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("console-drawer.terminal") || css.contains(".terminal"));
    assertTrue(css.contains("terminal-prompt") || css.contains("#7dff9a"));
  }

  @Test
  @DisplayName("FR-5/FR-6: G-008 done; Active Goal none; entry points agree")
  void goalClosed() throws Exception {
    Path root = findRepoRoot();

    String goals = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goals.contains("**Active Goal:** none") || goals.contains("Active Goal:** none"));
    assertTrue(goals.contains("G-008") && goals.contains("done"));
    String table =
        goals.lines().filter(l -> l.contains("| G-008 |")).findFirst().orElse("");
    assertTrue(table.contains("| done |"), table);

    String goalDoc =
        Files.readString(root.resolve("docs/project/goals/G-008-boundary-tectonics-studio.md"));
    assertTrue(goalDoc.contains("**Status:** `done`"));
    assertTrue(goalDoc.contains("- [x] Studio panels + mappy style + traditional console"));
    assertTrue(goalDoc.contains("- [x] Determinism; no `engine` production edits; suite green"));

    String agents = Files.readString(root.resolve("AGENTS.md"));
    assertTrue(agents.contains("Active Goal:** none") || agents.contains("**Active Goal:** none"));
    assertTrue(agents.contains("G-008"));

    String phase = Files.readString(root.resolve("docs/PHASE.md"));
    assertTrue(phase.contains("none") || phase.contains("G-008"));
    assertTrue(phase.contains("G-008"));

    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("none") || nav.toLowerCase().contains("active goal"));
    assertTrue(nav.contains("G-008"));

    String readme = Files.readString(root.resolve("README.md"));
    assertTrue(readme.contains("G-008") || readme.toLowerCase().contains("no active"));

    String session = Files.readString(root.resolve("docs/project/session.md"));
    assertTrue(session.contains("none") || session.contains("G-008"));

    String protocol = Files.readString(root.resolve(".cursor/rules/protocol.mdc"));
    assertTrue(protocol.contains("none") || protocol.contains("G-008"));

    String arch = Files.readString(root.resolve("docs/architecture.md"));
    assertTrue(arch.contains("G-008"));
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
