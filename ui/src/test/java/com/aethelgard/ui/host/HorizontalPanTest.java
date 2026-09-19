/*
 * File: ui/src/test/java/com/aethelgard/ui/host/HorizontalPanTest.java
 * Purpose: F-033 structural witness — horizontal-only studio pan
 * Audience: Agents / CI
 * Update when: F-033 pan FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HorizontalPanTest {

  @Test
  @DisplayName("FR-4: viewport pan ignores dy; lockVertical; docs say horizontal-only")
  void horizontalOnlyPan() throws Exception {
    Path root = findRepoRoot();
    String viewport = Files.readString(root.resolve("ui/web/src/lib/viewport.ts"));
    assertTrue(viewport.contains("lockVertical"));
    assertTrue(viewport.contains("_dy") || viewport.contains("dy is ignored"));
    assertTrue(viewport.contains("Horizontal-only") || viewport.contains("horizontal-only"));

    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("panBy(viewportRef.current, dx, 0"));
    assertTrue(canvas.contains("stageSizeRef.current.h"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(
        style.toLowerCase().contains("horizontal")
            || style.toLowerCase().contains("left/right")
            || style.toLowerCase().contains("left–right"));
    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.toLowerCase().contains("horizontal") || flows.contains("F-033"));
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
