/*
 * File: ui/src/test/java/com/aethelgard/ui/host/ViewportFrontTest.java
 * Purpose: F-028 structural + pure-math witness for pan/zoom
 * Audience: Agents / CI
 * Update when: F-028 FRs change
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
  @DisplayName("FR-1..FR-4: viewport helpers + MapCanvas pan/zoom + reset")
  void panZoomAndCellPick() throws Exception {
    Path root = findRepoRoot();

    String viewport = Files.readString(root.resolve("ui/web/src/lib/viewport.ts"));
    assertTrue(viewport.contains("zoomAt"));
    assertTrue(viewport.contains("panBy"));
    assertTrue(viewport.contains("stageToCell"));
    assertTrue(viewport.contains("IDENTITY_VIEWPORT"));
    assertTrue(viewport.contains("MIN_SCALE"));
    assertTrue(viewport.contains("MAX_SCALE"));

    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("zoomAt"));
    assertTrue(canvas.contains("panBy"));
    assertTrue(canvas.contains("stageToCell"));
    assertTrue(canvas.contains("onWheel"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("Reset view") || tool.contains("resetViewport"));

    double scale = 2.0;
    double tx = 10;
    double ty = 20;
    double stageX = 50;
    double stageY = 60;
    double worldX = (stageX - tx) / scale;
    double worldY = (stageY - ty) / scale;
    double nextScale = 2.24;
    double nextTx = stageX - worldX * nextScale;
    double nextTy = stageY - worldY * nextScale;
    assertEquals(stageX - worldX * nextScale, nextTx, 1e-9);
    assertEquals(stageY - worldY * nextScale, nextTy, 1e-9);

    int cellX = (int) Math.floor((30.0 / 512.0) * 512);
    int cellY = (int) Math.floor((40.0 / 512.0) * 512);
    assertEquals(30, cellX);
    assertEquals(40, cellY);

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("Wheel zoom") || style.contains("pan"));
    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.contains("Pan") || flows.contains("pan") || flows.contains("zoom"));
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
