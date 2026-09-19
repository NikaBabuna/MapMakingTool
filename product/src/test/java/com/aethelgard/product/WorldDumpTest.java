/*
 * File: product/src/test/java/com/aethelgard/product/WorldDumpTest.java
 * Purpose: F-016 witness — headless dump, golden fixture, G-003 close docs (F-017 golden)
 * Audience: Agents / CI
 * Update when: F-016 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WorldDumpTest {

  @Test
  @DisplayName("FR-1: same settled fields produce the same snapshot string")
  void dumpIsStable() {
    WorldSpec spec = new WorldSpec(4, 2, 0L);
    Engine engine = ProductHost.create(spec);
    engine.advance(2);
    String a = WorldDump.of(engine, spec);
    String b = WorldDump.of(engine, spec);
    assertEquals(a, b);
    assertTrue(a.startsWith("world w=4 h=2 seed=0 steps=2\n"));
    assertTrue(a.contains("elevation:\n"));
    assertTrue(a.contains("plates:\n"));
    assertTrue(a.contains("plate_velocity:\n"));
    assertTrue(a.contains("plate_registry:\n"));
    assertTrue(a.endsWith("\n"));
  }

  @Test
  @DisplayName("FR-2: DEFAULT + 3 Steps dump equals stored golden")
  void canonicalFixtureMatchesGolden() throws Exception {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine engine = ProductHost.create(spec);
    engine.advance(WorldDump.CANONICAL_STEPS);
    String dump = WorldDump.of(engine, spec);
    String golden =
        Files.readString(
                findRepoRoot().resolve("product/src/test/resources/worlds/default-n3.txt"),
                StandardCharsets.UTF_8)
            .replace("\r\n", "\n");
    assertEquals(golden, dump);
  }

  @Test
  @DisplayName("FR-3: Step 0 dump is zero elevation; after N, height matches standing orogeny")
  void stepZeroAndSutureRule() {
    WorldSpec spec = new WorldSpec(4, 2, 0L);
    Engine engine = ProductHost.create(spec);
    String zero = WorldDump.of(engine, spec);
    assertTrue(zero.startsWith("world w=4 h=2 seed=0 steps=0\n"));
    assertTrue(zero.contains("elevation:\n0 0 0 0\n0 0 0 0\n"));
    Grid plates = Plates.seed(4, 2, 0L);
    assertTrue(zero.contains("plates:\n" + gridBlock(plates)));
    assertTrue(zero.contains("plate_velocity:\n"));

    engine.advance(2);
    String risen = WorldDump.of(engine, spec);
    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(
            plates,
            PlateVelocities.seed(0L),
            PlateRegistry.from(plates, PlateVelocities.seed(0L)),
            Grid.zeros(4, 2));
    state = ProductGeneration.advance(state, 1);
    state = ProductGeneration.advance(state, 2);
    assertTrue(risen.contains("elevation:\n" + gridBlock(state.elevation())));
    assertTrue(risen.contains("plates:\n" + gridBlock(state.plates())));
  }

  @Test
  @DisplayName("FR-4: independent runs match; different seed differs when width > 1")
  void independentRunsAndDifferentSeed() {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(WorldDump.CANONICAL_STEPS);
    b.advance(WorldDump.CANONICAL_STEPS);
    assertEquals(WorldDump.of(a, spec), WorldDump.of(b, spec));

    WorldSpec other = new WorldSpec(spec.width(), spec.height(), spec.seed() + 1);
    Engine c = ProductHost.create(other);
    c.advance(WorldDump.CANONICAL_STEPS);
    assertNotEquals(WorldDump.of(a, spec), WorldDump.of(c, other));
  }

  @Test
  @DisplayName("FR-5: dump recorded; G-003 stays done")
  void docsRecordDumpAndGoalDone() throws Exception {
    Path root = findRepoRoot();
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("WorldDump"));
    String readme = Files.readString(root.resolve("product/README.md"));
    assertTrue(readme.contains("WorldDump"));

    String goal = Files.readString(root.resolve("docs/project/goals/G-003-first-product-world.md"));
    assertTrue(goal.contains("**Status:** `done`"));
    assertFalse(goal.contains("**Status:** `in progress`"));
    assertTrue(goal.contains("- [x] Headless product observer dumps the settled grid"));
    assertTrue(
        goal.contains("- [x] Incremental suite: all G-001 and G-002 Step tests remain green"));

    String goalsIndex = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goalsIndex.contains("G-003"));
    assertTrue(goalsIndex.contains("First product world"));
    assertTrue(goalsIndex.contains("done"));
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

  private static String gridBlock(Grid grid) {
    StringBuilder out = new StringBuilder();
    for (int y = 0; y < grid.height(); y++) {
      for (int x = 0; x < grid.width(); x++) {
        if (x > 0) {
          out.append(' ');
        }
        out.append(grid.get(x, y));
      }
      out.append('\n');
    }
    return out.toString();
  }
}
