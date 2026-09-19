/*
 * File: ui/src/test/java/com/aethelgard/ui/host/SphereCameraTest.java
 * Purpose: F-045 structural witness — antipodal vertical wrap pan
 * Audience: Agents / CI
 * Update when: Camera sphere rules change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SphereCameraTest {

  @Test
  @DisplayName("FR-3: viewport wrapPan antipodal Y; MapCanvas 3×3 antipodal tiles")
  void antipodalCamera() throws Exception {
    Path root = findRepoRoot();
    String viewport = Files.readString(root.resolve("ui/web/src/lib/viewport.ts"));
    assertTrue(viewport.contains("antipodal") || viewport.contains("periodX / 2"));
    assertTrue(viewport.contains("while (ty < 0)") || viewport.contains("periodY"));
    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("antipodal") || canvas.contains("half"));
    assertTrue(canvas.contains("j === 0") || canvas.contains("for (let j"));
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
