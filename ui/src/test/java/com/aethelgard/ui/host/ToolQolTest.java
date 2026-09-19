/*
 * File: ui/src/test/java/com/aethelgard/ui/host/ToolQolTest.java
 * Purpose: F-029 structural witness for shortcuts, seed QoL, feedback, a11y; G-007 close
 * Audience: Agents / CI
 * Update when: F-029 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ToolQolTest {

  @Test
  @DisplayName("FR-1..FR-5: shortcuts, seed QoL, busy/offline, a11y; G-007 closed")
  void shortcutsSeedFeedbackA11yGoalClosed() throws Exception {
    Path root = findRepoRoot();

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("keydown"));
    assertTrue(tool.contains("\" \"") || tool.contains("Spacebar"));
    assertTrue(tool.contains("Random"));
    assertTrue(tool.contains("confirmNew") || tool.contains("alertdialog"));
    assertTrue(tool.contains("Retry"));
    assertTrue(tool.contains("aria-pressed") || tool.contains("aria-label"));
    assertTrue(tool.contains("role=\"toolbar\""));

    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("prefers-reduced-motion"));
    assertTrue(css.contains("focus-visible"));
    assertTrue(css.contains("map-busy"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("Space"));
    assertTrue(style.contains("New world"));

    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.contains("shortcut") || flows.contains("Shortcuts") || flows.contains("G-007"));

    String goals = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goals.contains("G-007-studio-cartography"));
    String table =
        goals.lines().filter(l -> l.contains("| G-007 |")).findFirst().orElse("");
    assertTrue(table.contains("| done |"), table);
    // Active Goal may be none (post-close) or a later Goal (e.g. G-008)
    assertTrue(
        goals.contains("**Active Goal:** none")
            || goals.contains("G-008")
            || goals.contains("Active Goal:** [G-"));

    String goalDoc = Files.readString(root.resolve("docs/project/goals/G-007-studio-cartography.md"));
    assertTrue(goalDoc.contains("**Status:** `done`"));

    String agents = Files.readString(root.resolve("AGENTS.md"));
    assertTrue(agents.contains("G-007") || agents.contains("G-008") || agents.contains("Active Goal:** none"));

    String phase = Files.readString(root.resolve("docs/PHASE.md"));
    assertTrue(phase.contains("G-007") || phase.contains("G-008"));

    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("G-007"));

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
