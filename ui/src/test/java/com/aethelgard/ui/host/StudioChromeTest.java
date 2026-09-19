/*
 * File: ui/src/test/java/com/aethelgard/ui/host/StudioChromeTest.java
 * Purpose: F-027 structural witness for studio cartography shell
 * Audience: Agents / CI
 * Update when: F-027 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudioChromeTest {

  @Test
  @DisplayName("FR-1..FR-5: style guide + studio map-first shell + dock/console")
  void styleGuideAndShell() throws Exception {
    Path root = findRepoRoot();

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("Studio cartography"));
    // F-053 renamed the dock flag to a rail key and moved it into lib/layout.ts
    assertTrue(style.contains("aethelgard.rail.right.open"));
    assertTrue(style.contains("--accent") || style.contains("gray") || style.contains("--ink"));

    String layoutLib = Files.readString(root.resolve("ui/web/src/lib/layout.ts"));
    assertTrue(layoutLib.contains("aethelgard.rail.right.open"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("studio"));
    assertTrue(tool.contains("studio-bar"));
    assertTrue(tool.contains("RAIL_OPEN_KEYS"));
    assertTrue(tool.contains("console-drawer") || tool.contains("terminalOpen") || tool.contains("Terminal"));
    assertTrue(tool.contains("side-rail") || tool.contains("studio-panel") || tool.contains("dockOpen"));
    assertTrue(tool.contains("postAdvance"));
    assertTrue(tool.contains("Terminal") || tool.contains("Console"));
    assertFalse(tool.contains("tool-brand"));
    assertFalse(tool.contains("JFrame"));

    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("studio"));
    assertTrue(css.contains("map-stage"));
    assertTrue(css.contains("--accent"));
    assertFalse(css.contains("Fraunces"));
    assertFalse(css.contains("--ember"));

    String layout = Files.readString(root.resolve("ui/web/src/app/layout.tsx"));
    assertTrue(layout.contains("IBM_Plex_Sans") || layout.contains("IBM Plex"));

    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.contains("studio") || flows.contains("G-007") || flows.contains("dock"));

    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("style-guide.md"));
    assertTrue(nav.toLowerCase().contains("studio") || nav.contains("G-007"));

    String readme = Files.readString(root.resolve("ui/web/README.md"));
    assertTrue(readme.contains("studio") || readme.contains("dock") || readme.contains("G-007"));
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
