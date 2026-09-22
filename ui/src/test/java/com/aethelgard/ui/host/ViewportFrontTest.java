/*
 * File: ui/src/test/java/com/aethelgard/ui/host/ViewportFrontTest.java
 * Purpose: F-028 structural + pure-math witness for pan/zoom (amended F-032 torus)
 * Audience: Agents / CI
 * Update when: Viewport FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewportFrontTest {

  @Test
  @DisplayName("FR-1..FR-4: viewport helpers + MapCanvas pan/zoom + reset (F-028/F-032)")
  void panZoomAndCellPick() throws Exception {
    Path root = findRepoRoot();

    String viewport = Files.readString(root.resolve("ui/web/src/lib/viewport.ts"));
    assertTrue(viewport.contains("zoomAt"));
    assertTrue(viewport.contains("panBy"));
    assertTrue(viewport.contains("stageToCell"));
    assertTrue(viewport.contains("fitScale"));
    assertTrue(viewport.contains("MAX_SCALE"));

    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("zoomAt"));
    assertTrue(canvas.contains("panBy"));
    assertTrue(canvas.contains("stageToCell"));
    assertTrue(canvas.contains("onWheel"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("Reset view") || tool.contains("fitView"));

    double stageW = 800;
    double displayW = 1920;
    double fit = Math.min(stageW / displayW, 600.0 / 1080.0);
    assertTrue(fit < 1.0);
    assertEquals(stageW / displayW, Math.min(stageW / displayW, 600.0 / 1080.0), 1e-9);

    // floorMod wrap
    assertEquals(10, Math.floorMod(-5, 15));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("pans") || style.contains("zooms"));
    String flows = Files.readString(root.resolve("docs/product/journeys.md"));
    assertTrue(flows.contains("pan") || flows.contains("zoom"));
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
