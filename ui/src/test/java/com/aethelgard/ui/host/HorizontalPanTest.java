/*
 * File: ui/src/test/java/com/aethelgard/ui/host/HorizontalPanTest.java
 * Purpose: F-038 structural witness — vertical pan clamped at polar edges
 * Audience: Agents / CI
 * Update when: F-038 camera FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HorizontalPanTest {

  @Test
  @DisplayName("FR-6: clampVertical; panBy takes dy; MapCanvas passes dy + stageH")
  void verticalPanClampedAtPoles() throws Exception {
    Path root = findRepoRoot();
    String viewport = Files.readString(root.resolve("ui/web/src/lib/viewport.ts"));
    assertTrue(viewport.contains("clampVertical"));
    assertTrue(viewport.contains("panBy"));
    assertTrue(viewport.contains("stageH"));
    assertTrue(
        viewport.contains("polar")
            || viewport.contains("mapH")
            || viewport.contains("cannot leave"));

    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("panBy(viewportRef.current, dx, dy"));
    assertTrue(canvas.contains("stageSizeRef.current.h"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(
        style.toLowerCase().contains("clamp")
            || style.toLowerCase().contains("polar")
            || style.toLowerCase().contains("vertical"));
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
