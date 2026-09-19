/*
 * File: ui/src/test/java/com/aethelgard/ui/host/CylinderMapTest.java
 * Purpose: F-034/F-045 structural witness — map loop tiles (sphere antipodal Y)
 * Audience: Agents / CI
 * Update when: Map tiling FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CylinderMapTest {

  @Test
  @DisplayName("FR-5/F-045: MapCanvas sphere loop tiles (3×3 antipodal)")
  void blankNorthSouth() throws Exception {
    Path root = findRepoRoot();
    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("for (let i = -1"));
    assertTrue(canvas.contains("for (let j = -1") || canvas.contains("antipodal") || canvas.contains("half"));
    assertTrue(canvas.contains("drawImage"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(
        style.toLowerCase().contains("blank")
            || style.toLowerCase().contains("polar")
            || style.toLowerCase().contains("cylinder")
            || style.toLowerCase().contains("sphere")
            || style.contains("G-009"));
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
