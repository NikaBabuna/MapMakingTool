/*
 * File: product/src/test/java/com/aethelgard/product/SimulationRunnerDocsTest.java
 * Purpose: F-041 structural witness for G-009 wiki / ADR locks
 * Audience: Agents / CI
 * Update when: F-041 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimulationRunnerDocsTest {

  @Test
  @DisplayName("FR-1..FR-5: G-009 runner docs locks; no behavior change this Step")
  void f041WikiLocks() throws Exception {
    Path root = findRepoRoot();

    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(tectonics.contains("ridge") || tectonics.contains("Ridge"));
    assertTrue(tectonics.contains("third") || tectonics.contains("nearest"));
    assertTrue(tectonics.contains("retired") || tectonics.contains("Retired"));
    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.toLowerCase().contains("sphere"));
    String productArch = Files.readString(root.resolve("docs/architecture/world/motion.md"));
    assertTrue(productArch.contains("SphereTopology"));
    assertTrue(productArch.contains("flood"));

    String decisions = Files.readString(root.resolve("docs/paperwork/decisions/ADR-012-simulation-runner.md"));
    assertTrue(decisions.contains("ADR-012"));
    assertTrue(decisions.contains("G-009"));
    assertTrue(decisions.contains("ridge") || decisions.contains("contacting"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("Play"));
    assertTrue(style.contains("Fastest"));

    String flows = Files.readString(root.resolve("docs/product/journeys.md"));
    assertTrue(flows.contains("Play"));
    assertTrue(flows.contains("Pause"));

    String goals = Files.readString(root.resolve("docs/paperwork/goals.md"));
    assertTrue(goals.contains("G-009"));
    assertTrue(goals.contains("Simulation runner") || goals.contains("simulation-runner"));
    assertTrue(
        goals.contains("in progress")
            || goals.contains("Active Goal")
            || goals.contains("| done |"));

    String agents = Files.readString(root.resolve("AGENTS.md"));
    assertTrue(agents.contains("G-011") || agents.contains("docs/protocol/README.md"));
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
