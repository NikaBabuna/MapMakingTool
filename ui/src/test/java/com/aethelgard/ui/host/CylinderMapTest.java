/*
 * File: ui/src/test/java/com/aethelgard/ui/host/CylinderMapTest.java
 * Purpose: Structural witness — horizontal loop tiles; dark blank N/S
 * Audience: Agents / CI
 * Update when: Map tiling FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CylinderMapTest {

  @Test
  @DisplayName("MapCanvas horizontal tiles only; dark blank N/S")
  void blankNorthSouth() throws Exception {
    Path root = findRepoRoot();
    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("Horizontal loop") || canvas.contains("blank") || canvas.contains("dark"));
    assertTrue(canvas.contains("for (let i = -1"));
    assertFalse(canvas.contains("for (let j = -1"));
    assertTrue(canvas.contains("drawImage(source, i * dw, 0)"));

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
