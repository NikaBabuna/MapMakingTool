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
    assertTrue(tectonics.contains("superseded") || tectonics.contains("Superseded") || tectonics.contains("retired") || tectonics.contains("Retired"));
    assertTrue(
        tectonics.contains("Sphere-on-rectangle")
            || tectonics.contains("sphere-on-rectangle")
            || tectonics.contains("antipodal"));
    assertTrue(tectonics.contains("F-045") || tectonics.contains("until F-045"));
    assertTrue(tectonics.contains("F-043") || tectonics.contains("until F-043") || tectonics.contains("F-044"));

    String decisions = Files.readString(root.resolve("docs/project/decisions.md"));
    assertTrue(decisions.contains("ADR-012"));
    assertTrue(decisions.contains("G-009"));
    assertTrue(decisions.contains("ridge") || decisions.contains("contacting"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("G-009"));
    assertTrue(style.contains("planned") || style.contains("Planned"));

    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.contains("G-009"));
    assertTrue(flows.contains("F-041") || flows.contains("planned"));

    String goals = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goals.contains("G-009"));
    assertTrue(goals.contains("Simulation runner") || goals.contains("simulation-runner"));
    assertTrue(goals.contains("in progress") || goals.contains("Active Goal"));

    String agents = Files.readString(root.resolve("AGENTS.md"));
    assertTrue(agents.contains("G-009"));
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
