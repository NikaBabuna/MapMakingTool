/*
 * File: product/src/test/java/com/aethelgard/product/CrustTopologyDocsTest.java
 * Purpose: F-055 structural witness for G-010 wiki / ADR locks
 * Audience: Agents / CI
 * Update when: F-055 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CrustTopologyDocsTest {

  @Test
  @DisplayName("FR-1..FR-5: G-010 crust docs locks; no behavior change this Step")
  void f055WikiLocks() throws Exception {
    Path root = findRepoRoot();

    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(tectonics.contains("ride") || tectonics.contains("rides"));
    assertTrue(tectonics.contains("derived") || tectonics.contains("isostasy"));
    assertTrue(tectonics.contains("superseded for G-010") || tectonics.contains("superseded"));
    assertTrue(tectonics.contains("F-056") || tectonics.contains("until F-056"));
    assertTrue(tectonics.contains("Ridge mint") || tectonics.contains("ridge mint"));
    assertTrue(tectonics.contains("oceanic"));
    assertTrue(tectonics.contains("subduct"));
    assertTrue(tectonics.contains("suture") || tectonics.contains("Suture"));
    assertTrue(tectonics.contains("all oceanic") || tectonics.contains("All oceanic"));
    assertTrue(tectonics.contains("No plate-id weld") || tectonics.contains("no weld") || tectonics.contains("No weld"));
    assertTrue(tectonics.contains("occupancy") || tectonics.contains("Occupancy"));
    assertTrue(tectonics.contains("lockers") || tectonics.contains("`lockers`"));
    assertTrue(tectonics.contains("RidgeCreate"));
    assertTrue(tectonics.contains("Subduct"));
    assertTrue(tectonics.contains("ThicknessToElevation"));
    assertTrue(tectonics.contains("world/tectonics"));
    assertTrue(tectonics.contains("crust/ridge") || tectonics.contains("crust/isostasy"));

    String elevation = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    assertTrue(elevation.contains("G-010"));
    assertTrue(elevation.contains("superseded") || elevation.contains("isostasy"));
    assertTrue(elevation.contains("F-056"));

    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.contains("G-010"));
    assertTrue(world.contains("lockers") || world.contains("occupancy"));

    String decisions = Files.readString(root.resolve("docs/project/decisions.md"));
    assertTrue(decisions.contains("ADR-013"));
    assertTrue(decisions.contains("G-010"));
    assertTrue(decisions.contains("locker") || decisions.contains("Occupancy"));

    String goalDoc = Files.readString(root.resolve("docs/project/goals/G-010-crust-topology.md"));
    assertTrue(goalDoc.contains("**Status:** `done`"));
    assertTrue(goalDoc.contains("| F-055 |") && goalDoc.contains("| F-061 |"));

    String goals = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goals.contains("G-010"));
    assertTrue(goals.contains("Crust topology") || goals.contains("crust-topology"));
    String g010 = goals.lines().filter(l -> l.contains("| G-010 |")).findFirst().orElse("");
    assertTrue(g010.contains("| done |"), g010);
    assertTrue(goals.contains("**Active Goal:**"));
    assertTrue(goals.contains("G-011"));

    String goalsReadme = Files.readString(root.resolve("docs/project/goals/README.md"));
    String g009row =
        goalsReadme.lines().filter(l -> l.contains("G-009")).findFirst().orElse("");
    assertTrue(g009row.contains("| done |"), g009row);

    String features = Files.readString(root.resolve("docs/project/features.md"));
    assertTrue(features.contains("F-055") && features.contains("F-060"));

    String roadmap = Files.readString(root.resolve("docs/project/roadmap.md"));
    assertTrue(roadmap.contains("G-010"));

    String backlog = Files.readString(root.resolve("docs/project/backlog.md"));
    assertTrue(backlog.contains("G-010"));

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

    assertFalse(Files.exists(root.resolve("docs/project/session.md")));

    String protocol = Files.readString(root.resolve(".cursor/rules/protocol.mdc"));
    assertTrue(protocol.contains("G-011"));

    String arch = Files.readString(root.resolve("docs/architecture.md"));
    assertTrue(arch.contains("G-010"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("G-010"));
    assertTrue(style.contains("done") || style.contains("Boundary"));

    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.contains("G-010"));
    assertTrue(flows.contains("F-055"));

    String glossary = Files.readString(root.resolve("docs/product/glossary.md"));
    assertTrue(glossary.contains("Locker") || glossary.contains("locker"));
    assertTrue(glossary.contains("Occupancy") || glossary.contains("occupancy"));
    assertTrue(glossary.contains("Isostasy") || glossary.contains("isostasy"));

    assertTrue(Files.isRegularFile(root.resolve("docs/blockers/F-056.md")));

    String orogeny =
        Files.readString(root.resolve("product/src/main/java/com/aethelgard/product/Orogeny.java"));
    assertTrue(orogeny.contains("standing"));
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
