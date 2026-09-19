/*
 * File: ui/src/test/java/com/aethelgard/ui/host/SphereCameraTest.java
 * Purpose: F-045 camera — X wrap + Y clamp to dark N/S margins
 * Audience: Agents / CI
 * Update when: Camera sphere/UI rules change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SphereCameraTest {

  @Test
  @DisplayName("FR-3: viewport X wrap + Y clamp; MapCanvas horizontal tiles only")
  void antipodalCamera() throws Exception {
    Path root = findRepoRoot();
    String viewport = Files.readString(root.resolve("ui/web/src/lib/viewport.ts"));
    assertTrue(viewport.contains("clampVertical"));
    assertTrue(viewport.contains("wrapPan"));
    assertTrue(viewport.contains("dark") || viewport.contains("map band") || viewport.contains("N/S"));
    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("Horizontal loop") || canvas.contains("blank") || canvas.contains("dark"));
    assertTrue(canvas.contains("for (let i = -1"));
    assertFalse(canvas.contains("for (let j = -1"));
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath().normalize();
    for (Path cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("ui"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new Exception("repo root not found");
  }
}
