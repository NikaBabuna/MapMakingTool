/*
 * File: product/src/test/java/com/aethelgard/product/CrustGoalCloseTest.java
 * Purpose: F-061 witness — isostasy at occupancy, G-010 close, doc hygiene
 * Audience: Agents / CI
 * Update when: F-061 FRs or G-010 close claims change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CrustGoalCloseTest {

  @Test
  @DisplayName("FR-1: elevation is thickness minus 8 at occupancy; same seed matches")
  void elevationIsIsostasy() {
    assertIsostasy(WorldSpec.DEFAULT, 3);
    assertIsostasy(new WorldSpec(8, 8, 7L), 3);

    Engine a = ProductHost.create(WorldSpec.DEFAULT);
    Engine b = ProductHost.create(WorldSpec.DEFAULT);
    a.advance(3);
    b.advance(3);
    assertEquals(a.settled().field(WorldFields.ELEVATION), b.settled().field(WorldFields.ELEVATION));
    assertEquals(a.settled().field(WorldFields.OCCUPANCY), b.settled().field(WorldFields.OCCUPANCY));
    assertEquals(a.settled().field(WorldFields.LOCKERS), b.settled().field(WorldFields.LOCKERS));
  }

  @Test
  @DisplayName("FR-3..FR-5: G-010 done; claims; entry points none; docs hygiene")
  void goalClosed() throws Exception {
    Path root = findRepoRoot();

    String goalDoc = Files.readString(root.resolve("docs/project/goals/G-010-crust-topology.md"));
    assertTrue(goalDoc.contains("**Status:** `done`"));
    assertFalse(goalDoc.contains("- [ ]"));
    assertTrue(goalDoc.contains("- [x] `elevation` matches isostasy"));
    assertTrue(goalDoc.contains("| F-061 |") && goalDoc.contains("| done |"));

    String goals = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goals.contains("G-010"));
    String g010 = goals.lines().filter(l -> l.contains("| G-010 |")).findFirst().orElse("");
    assertTrue(g010.contains("| done |"), g010);
    assertTrue(goals.contains("G-011"));
    assertTrue(goals.contains("**Active Goal:**"));

    String agents = Files.readString(root.resolve("AGENTS.md"));
    assertTrue(agents.contains("G-011"));
    assertTrue(agents.contains("docs/protocol/README.md"));
    String phase = Files.readString(root.resolve("docs/protocol/environment/phase.md"));
    assertTrue(phase.contains("alpha"));
    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("G-010"));
    assertTrue(nav.contains("G-011"));
    String readme = Files.readString(root.resolve("README.md"));
    assertTrue(readme.contains("G-010"));
    assertTrue(readme.contains("G-011"));
    String protocol = Files.readString(root.resolve(".cursor/rules/protocol.mdc"));
    assertTrue(protocol.contains("G-011"));
    String arch = Files.readString(root.resolve("docs/architecture.md"));
    assertTrue(arch.contains("G-010"));
    assertTrue(arch.contains("G-011"));

    String flows = Files.readString(root.resolve("docs/product/journeys.md"));
    assertFalse(flows.contains("crust topology (planned)"));
    assertFalse(flows.contains("**F-058** buoyancy live"));
    assertTrue(flows.contains("Play"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertFalse(style.contains("HUD/crust layer"));
    assertTrue(style.contains("Boundary"));

    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertFalse(tectonics.contains("G-010 remaining"));
    assertTrue(tectonics.contains("subduct"));

    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertFalse(world.contains("Goal close remains"));
    assertTrue(world.contains("sphere"));

    String blockers = Files.readString(root.resolve("docs/blockers/README.md"));
    assertTrue(blockers.contains("F-060.md") && blockers.contains("F-061.md"));

    String engineArch = Files.readString(root.resolve("docs/engine/architecture.md"));
    assertFalse(engineArch.contains("Placeholder verbs"));

    String blocker = Files.readString(root.resolve("docs/blockers/F-061.md"));
    assertTrue(blocker.contains("no `engine/`") || blocker.contains("Zero `engine/`"));
    assertTrue(Files.isDirectory(root.resolve("engine/src/main/java")));
  }

  private static void assertIsostasy(WorldSpec spec, int steps) {
    Engine engine = ProductHost.create(spec);
    engine.advance(steps);
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);
    Grid occupancy = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    Lockers lockers = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    for (int y = 0; y < elevation.height(); y++) {
      for (int x = 0; x < elevation.width(); x++) {
        assertEquals(
            lockers.thickness(occupancy.get(x, y)) - Lockers.T_OCEAN,
            elevation.get(x, y),
            "seed=" + spec.seed() + " x=" + x + " y=" + y);
      }
    }
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
