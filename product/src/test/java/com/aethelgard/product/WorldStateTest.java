/*
 * File: product/src/test/java/com/aethelgard/product/WorldStateTest.java
 * Purpose: F-014 witness — elevation Grid in the Pool at Step 0
 * Audience: Agents / CI
 * Update when: F-014 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.pool.PoolSnapshot;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WorldStateTest {

  @Test
  @DisplayName("FR-1: setup declares elevation Static; settled value is Grid not Long")
  void setupDeclaresElevationGrid() {
    EngineSetup setup = ProductHost.setup();
    assertTrue(setup.fieldSchema().has(WorldFields.ELEVATION));
    assertEquals(FieldType.STATIC, setup.fieldSchema().typeOf(WorldFields.ELEVATION));

    Object elevation = ProductHost.create(new WorldSpec(2, 3, 1L)).settled().field(WorldFields.ELEVATION);
    assertInstanceOf(Grid.class, elevation);
    assertTrue(!(elevation instanceof Long));
  }

  @Test
  @DisplayName("FR-2: create(WorldSpec) seeds zero elevation matching geometry")
  void createSeedsZeroGridMatchingSpec() {
    WorldSpec spec = new WorldSpec(3, 2, 99L);
    Engine engine = ProductHost.create(spec);
    assertEquals(0, engine.stepIndex());
    Grid grid = (Grid) engine.settled().field(WorldFields.ELEVATION);
    assertEquals(3, grid.width());
    assertEquals(2, grid.height());
    for (int y = 0; y < grid.height(); y++) {
      for (int x = 0; x < grid.width(); x++) {
        assertEquals(0, grid.get(x, y), "cell (" + x + "," + y + ")");
      }
    }
  }

  @Test
  @DisplayName("FR-3: create() is 8x8 seed 0; invalid geometry fails fast")
  void defaultSpecAndInvalidGeometry() {
    Engine engine = ProductHost.create();
    Grid grid = (Grid) engine.settled().field(WorldFields.ELEVATION);
    assertEquals(8, grid.width());
    assertEquals(8, grid.height());
    assertEquals(0L, WorldSpec.DEFAULT.seed());
    assertEquals(0, grid.get(0, 0));
    assertEquals(0, grid.get(7, 7));

    assertThrows(IllegalArgumentException.class, () -> new WorldSpec(0, 8, 0L));
    assertThrows(IllegalArgumentException.class, () -> new WorldSpec(8, 0, 0L));
    assertThrows(IllegalArgumentException.class, () -> new WorldSpec(-1, 4, 0L));
  }

  @Test
  @DisplayName("FR-4: world is elevation Grid, not heartbeat value")
  void worldIsElevationNotHeartbeat() {
    PoolSnapshot settled = ProductHost.create(new WorldSpec(4, 4, 0L)).settled();
    assertEquals(1L, settled.value(), "skeleton heartbeat still runs");
    Grid grid = (Grid) settled.field(WorldFields.ELEVATION);
    assertEquals(4, grid.width());
    assertNotEquals(settled.value(), (long) grid.get(0, 0));
  }

  @Test
  @DisplayName("FR-5: wiki world page exists and is indexed")
  void wikiDefinesWorldGridLayer() throws Exception {
    Path root = findRepoRoot();
    Path wiki = root.resolve("docs/product/wiki/world.md");
    assertTrue(Files.isRegularFile(wiki));
    String text = Files.readString(wiki);
    assertTrue(text.toLowerCase().contains("world"));
    assertTrue(text.toLowerCase().contains("grid"));
    assertTrue(text.toLowerCase().contains("layer"));
    assertTrue(text.toLowerCase().contains("elevation"));

    String index = Files.readString(root.resolve("docs/product/wiki/README.md"));
    assertTrue(index.contains("world.md"));
    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("wiki/world.md"));
  }

  @Test
  @DisplayName("FR-6: product architecture records elevation Grid wiring")
  void architectureRecordsWorldFields() throws Exception {
    String text = Files.readString(findRepoRoot().resolve("docs/product/architecture.md"));
    assertTrue(text.contains("elevation"));
    assertTrue(text.contains("Grid"));
    assertTrue(text.contains("ProductHost"));
  }

  private static Path findRepoRoot() {
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
