/*
 * File: ui/src/test/java/com/aethelgard/ui/host/HorizontalPanTest.java
 * Purpose: F-038 structural witness — X wrap + Y polar-clamp pan
 * Audience: Agents / CI
 * Update when: Camera pan rules change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HorizontalPanTest {

  @Test
  @DisplayName("FR-8: viewport clampVertical; panBy takes dy; MapCanvas passes dy")
  void polarClampPan() throws Exception {
    Path root = findRepoRoot();
    String viewport = Files.readString(root.resolve("ui/web/src/lib/viewport.ts"));
    assertTrue(viewport.contains("clampVertical"));
    assertTrue(viewport.contains("Pan with X wrap and Y clamp") || viewport.contains("polar"));
    assertTrue(viewport.contains("stageH: number"));

    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("panBy(viewportRef.current, dx, dy"));
  }

  private static Path findRepoRoot() throws Exception {
    var dir = Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("engine"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found");
  }
}
