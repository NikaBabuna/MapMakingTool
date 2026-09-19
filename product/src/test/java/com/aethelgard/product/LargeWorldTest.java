/*
 * File: product/src/test/java/com/aethelgard/product/LargeWorldTest.java
 * Purpose: F-031 witness for WorldSpec.VIEW 1920×1080 (product side)
 * Audience: Agents / CI
 * Update when: F-031 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LargeWorldTest {

  @Test
  @DisplayName("FR-1: VIEW is 1920×1080 seed 0; DEFAULT stays 8×8")
  void viewSpecGeometry() {
    assertEquals(1920, WorldSpec.VIEW.width());
    assertEquals(1080, WorldSpec.VIEW.height());
    assertEquals(0L, WorldSpec.VIEW.seed());
    assertEquals(8, WorldSpec.DEFAULT.width());
    assertEquals(8, WorldSpec.DEFAULT.height());
    assertEquals(0L, WorldSpec.DEFAULT.seed());
  }

  @Test
  @DisplayName("FR-2: ProductHost / ProductSession VIEW Step 0 geometry; elevation zeros")
  void viewCreatesZeroElevation() {
    Engine engine = ProductHost.create(WorldSpec.VIEW);
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    assertEquals(1920, elevation.width());
    assertEquals(1080, elevation.height());
    assertEquals(1920, plates.width());
    assertEquals(1080, plates.height());
    assertEquals(0, elevation.get(0, 0));
    assertEquals(0, elevation.get(1919, 1079));

    ProductSession session = ProductSession.view();
    assertEquals(WorldSpec.VIEW, session.spec());
    assertEquals(0, session.stepIndex());
    assertEquals(1920, session.elevation().width());
    assertEquals(1080, session.plates().height());
    assertEquals(0, session.elevation().get(100, 100));
  }

  @Test
  @DisplayName("FR-5: docs say VIEW is live 1920×1080")
  void docsSayViewLive() throws Exception {
    Path root = findRepoRoot();
    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.contains("1920"));
    String glossary = Files.readString(root.resolve("docs/product/glossary.md"));
    assertTrue(glossary.contains("1920"));
    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(tectonics.contains("1920"));
    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.contains("1920"));
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
