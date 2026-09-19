/*
 * File: product/src/test/java/com/aethelgard/product/BoundaryTectonicsDocsTest.java
 * Purpose: F-030 structural witness for G-008 wiki locks
 * Audience: Agents / CI
 * Update when: F-030 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoundaryTectonicsDocsTest {

  @Test
  @DisplayName("FR-1..FR-5: G-008 tectonics wiki locks; no behavior change this Step")
  void f030WikiLocks() throws Exception {
    Path root = findRepoRoot();

    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(tectonics.contains("1920×1080") || tectonics.contains("1920x1080"));
    assertTrue(tectonics.contains("Torus") || tectonics.contains("torus"));
    assertTrue(tectonics.contains("12 +") || tectonics.contains("12–24") || tectonics.contains("12-24"));
    assertTrue(tectonics.contains("Fission") || tectonics.contains("fission"));
    assertTrue(tectonics.contains("0.01%"));
    assertTrue(tectonics.contains("plate_registry"));
    assertTrue(tectonics.contains("boundaries"));
    assertTrue(tectonics.contains("world/tectonics"));
    assertTrue(tectonics.contains("superseded") || tectonics.contains("retired") || tectonics.contains("Constant-forever"));
    assertTrue(tectonics.contains("not implemented") || tectonics.contains("Code status"));

    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.contains("1920×1080") || world.contains("1920x1080"));
    assertTrue(world.contains("F-031"));

    String elevation = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    assertTrue(elevation.contains("tectonics.md") || elevation.contains("G-008"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("G-008"));
    assertTrue(
        style.contains("planned")
            || style.contains("Planned")
            || style.contains("shipped")
            || style.contains("F-040")
            || style.contains("terminal"));
    assertTrue(style.contains("loopback") || style.contains("Loopback"));

    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.contains("G-008"));
    assertTrue(flows.contains("1920") || flows.contains("planned") || flows.contains("shipped"));

    String goals = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goals.contains("G-008"));
    assertTrue(goals.contains("boundary-tectonics") || goals.contains("Boundary tectonics"));

    // F-031: VIEW is live at 1920×1080
    String spec = Files.readString(root.resolve("product/src/main/java/com/aethelgard/product/WorldSpec.java"));
    assertTrue(spec.contains("1920"));
    assertTrue(spec.contains("1080"));
    assertFalse(spec.contains("new WorldSpec(512, 512"));
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath();
    for (int i = 0; i < 8; i++) {
      if (Files.isRegularFile(dir.resolve("pom.xml"))
          && Files.isDirectory(dir.resolve("product"))
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
