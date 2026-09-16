/*
 * File: product/src/test/java/com/aethelgard/product/WorldDumpTest.java
 * Purpose: F-016 witness — headless dump, golden fixture, G-003 close docs
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
  @DisplayName("FR-3: Step 0 dump is zero elevation; after N, suture height is N")
  void stepZeroAndSutureRule() {
    WorldSpec spec = new WorldSpec(4, 2, 0L);
    Engine engine = ProductHost.create(spec);
    String zero = WorldDump.of(engine, spec);
    assertTrue(zero.startsWith("world w=4 h=2 seed=0 steps=0\n"));
    assertTrue(zero.contains("elevation:\n0 0 0 0\n0 0 0 0\n"));

    engine.advance(2);
    String risen = WorldDump.of(engine, spec);
    assertTrue(risen.contains("elevation:\n2 2 0 0\n2 2 0 0\n"));
    assertTrue(risen.contains("plates:\n0 1 1 1\n0 1 1 1\n"));
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
  @DisplayName("FR-5: dump recorded; G-003 done; Active Goal none on entry points")
  void docsRecordDumpAndGoalDone() throws Exception {
    Path root = findRepoRoot();
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("WorldDump"));
    String readme = Files.readString(root.resolve("product/README.md"));
    assertTrue(readme.contains("WorldDump"));

    String goal = Files.readString(root.resolve("docs/project/goals/G-003-first-product-world.md"));
    assertTrue(goal.contains("**Status:** `done`"));
    assertTrue(goal.contains("- [x] Headless product observer dumps the settled grid"));
    assertTrue(
        goal.contains("- [x] Incremental suite: all G-001 and G-002 Step tests remain green"));

    String goalsIndex = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goalsIndex.toLowerCase().contains("active goal:** none")
        || goalsIndex.contains("**Active Goal:** none"));
    assertTrue(goalsIndex.contains("G-003") && goalsIndex.contains("done"));

    String agents = Files.readString(root.resolve("AGENTS.md"));
    String readmeRoot = Files.readString(root.resolve("README.md"));
    String phase = Files.readString(root.resolve("docs/PHASE.md"));
    String nav = Files.readString(root.resolve("docs/navigation.md"));
    String session = Files.readString(root.resolve("docs/project/session.md"));
    String protocol = Files.readString(root.resolve(".cursor/rules/protocol.mdc"));
    String rollup = Files.readString(root.resolve("docs/architecture.md"));
    for (String text : new String[] {agents, readmeRoot, phase, nav, session, protocol, rollup}) {
      assertTrue(
          text.toLowerCase().contains("active goal:** none")
              || text.contains("**Active Goal:** none")
              || text.contains("Active Goal: none")
              || text.contains("**Current Goal:** none"),
          "entry point must say Active Goal none");
      assertFalse(
          text.contains("`in progress`) · Last completed: [G-002")
              || text.contains("G-003 First product world](docs/project/goals/G-003-first-product-world.md) (`in progress`)"),
          "must not still name G-003 as in progress");
    }
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
