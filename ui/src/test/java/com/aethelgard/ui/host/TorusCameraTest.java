/*
 * File: ui/src/test/java/com/aethelgard/ui/host/TorusCameraTest.java
 * Purpose: F-032 structural witness for loopback pan + zoom clamp
 * Audience: Agents / CI
 * Update when: F-032 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TorusCameraTest {

  @Test
  @DisplayName("FR-1..FR-5: viewport fit/wrap + MapCanvas torus camera + docs")
  void docsAndViewport() throws Exception {
    Path root = findRepoRoot();

    String viewport = Files.readString(root.resolve("ui/web/src/lib/viewport.ts"));
    assertTrue(viewport.contains("fitScale"));
    assertTrue(viewport.contains("wrapPan") || viewport.contains("floorMod"));
    assertTrue(viewport.contains("fittedViewport"));
    assertTrue(viewport.contains("MAX_SCALE"));
    assertTrue(viewport.contains("stageToCell"));
    assertTrue(viewport.contains("floorMod((stageX") || viewport.contains("floorMod((stageX -"));

    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("fitScale"));
    assertTrue(canvas.contains("drawImage"));
    assertTrue(canvas.contains("for (let i = -1"));
    assertTrue(canvas.contains("stageToCell"));
    assertTrue(canvas.contains("fittedViewport") || canvas.contains("resetViewport"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("fitView") || tool.contains("fittedViewport"));
    assertTrue(tool.contains("Reset view"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.toLowerCase().contains("loopback") || style.toLowerCase().contains("fit"));
    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.toLowerCase().contains("loopback") || flows.contains("F-032"));
    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(tectonics.toLowerCase().contains("torus"));
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
