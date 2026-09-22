/*
 * File: ui/src/test/java/com/aethelgard/ui/host/MultiPanelStudioTest.java
 * Purpose: F-039 structural witness — multi-panel studio + mappy stage
 * Audience: Agents / CI
 * Update when: F-039 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultiPanelStudioTest {

  @Test
  @DisplayName("FR-1..FR-6: multi-panel Inspect/Legend + mappy neatline + docs")
  void multiPanelAndMappy() throws Exception {
    Path root = findRepoRoot();

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("Inspect") && style.contains("Legend"));
    assertTrue(style.toLowerCase().contains("map"));

    String panels = Files.readString(root.resolve("ui/web/src/lib/panels.ts"));
    assertTrue(panels.contains("id: \"inspect\""));
    assertTrue(panels.contains("id: \"legend\""));
    assertTrue(panels.contains("aethelgard.panel."));

    String panel = Files.readString(root.resolve("ui/web/src/components/Panel.tsx"));
    assertTrue(panel.contains("studio-panel"));
    assertTrue(panel.contains("data-panel"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("panelsFor"));
    assertTrue(tool.contains("panelOpenKey"));
    assertTrue(tool.contains("side-rail"));
    assertTrue(tool.contains("postAdvance"));
    assertTrue(tool.contains("Terminal") || tool.contains("Console"));
    assertFalse(tool.contains("JFrame"));

    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("map-neatline"));
    assertTrue(canvas.contains("map-graticule") || canvas.contains("map-ticks"));
    assertTrue(canvas.contains("map-hud"));

    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("studio-panel"));
    assertTrue(css.contains("map-neatline"));
    assertTrue(css.contains("map-graticule") || css.contains("map-ticks"));
    assertTrue(css.contains("map-hud"));

    String flows = Files.readString(root.resolve("docs/product/journeys.md"));
    assertTrue(flows.contains("Inspect"));

    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("F-039") || nav.contains("style-guide"));
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
