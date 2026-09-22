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
    assertTrue(tectonics.contains("retired"));
    assertTrue(tectonics.contains("oceanic"));
    assertTrue(tectonics.contains("subduct"));
    assertTrue(tectonics.contains("suture") || tectonics.contains("Suture"));
    assertTrue(tectonics.contains("all oceanic") || tectonics.contains("All oceanic"));
    assertTrue(tectonics.toLowerCase().contains("weld"));

    String elevation = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    assertTrue(elevation.contains("isostasy"));
    assertTrue(elevation.contains("crust"));

    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.contains("1920"));
    assertTrue(world.toLowerCase().contains("sphere"));

    String productArch = Files.readString(root.resolve("docs/architecture/world/README.md"));
    assertTrue(productArch.contains("Lockers"));
    assertTrue(productArch.contains("occupancy") || productArch.contains("lockers"));
    assertTrue(productArch.contains("RidgeCreate"));
    assertTrue(productArch.contains("Subduct"));
    assertTrue(productArch.contains("ThicknessToElevation"));
    assertTrue(productArch.contains("world/tectonics"));

    String decisions = Files.readString(root.resolve("docs/paperwork/decisions/ADR-013-crust-topology.md"));
    assertTrue(decisions.contains("ADR-013"));
    assertTrue(decisions.contains("G-010"));
    assertTrue(decisions.contains("locker") || decisions.contains("Occupancy"));

    String goalDoc = Files.readString(root.resolve("docs/paperwork/goals/G-010-crust-topology.md"));
    assertTrue(goalDoc.contains("**Status:** `done`"));
    assertTrue(goalDoc.contains("| F-055 |") && goalDoc.contains("| F-061 |"));

    String goals = Files.readString(root.resolve("docs/paperwork/goals.md"));
    assertTrue(goals.contains("G-010"));
    assertTrue(goals.contains("Crust topology") || goals.contains("crust-topology"));
    String g010 = goals.lines().filter(l -> l.contains("| G-010 |")).findFirst().orElse("");
    assertTrue(g010.contains("| done |"), g010);
    assertTrue(goals.contains("**Active Goal:**"));
    assertTrue(goals.contains("G-011"));

    String goalsReadme = Files.readString(root.resolve("docs/paperwork/goals.md"));
    String g009row =
        goalsReadme.lines().filter(l -> l.contains("G-009")).findFirst().orElse("");
    assertTrue(g009row.contains("| done |"), g009row);

    String features = Files.readString(root.resolve("docs/paperwork/steps.md"));
    assertTrue(features.contains("F-055") && features.contains("F-060"));

    String roadmap = Files.readString(root.resolve("docs/paperwork/roadmap.md"));
    assertTrue(roadmap.contains("G-010"));

    String backlog = Files.readString(root.resolve("docs/paperwork/backlog.md"));
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

    String arch = Files.readString(root.resolve("docs/paperwork/goals.md"));
    assertTrue(arch.contains("G-010"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("Boundary"));

    String journeys = Files.readString(root.resolve("docs/product/journeys.md"));
    assertTrue(journeys.contains("Play"));
    assertTrue(journeys.contains("Not built"));

    String glossary = Files.readString(root.resolve("docs/product/glossary.md"));
    assertTrue(glossary.contains("Crust"));
    assertTrue(glossary.contains("Ocean"));
    assertTrue(glossary.contains("Elevation"));

    assertTrue(Files.isRegularFile(root.resolve("docs/paperwork/steps/F-056.md")));

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
