/*
 * File: product/src/test/java/com/aethelgard/product/ProtocolShelfTest.java
 * Purpose: F-062 witness — protocol shelf, no session, doors point at the goal index
 * Audience: Agents / CI
 * Update when: F-062 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProtocolShelfTest {

  @Test
  @DisplayName("FR-1: four rooms exist; old conduct paths are gone")
  void fr1_roomsAndOldConductGone() throws Exception {
    Path root = findRepoRoot();
    for (String room : new String[] {"environment", "navigation", "blueprints", "flows"}) {
      assertTrue(Files.isRegularFile(root.resolve("docs/protocol/" + room + "/README.md")), room);
    }
    assertFalse(Files.exists(root.resolve("docs/process")));
    assertFalse(Files.exists(root.resolve("docs/PHASE.md")));
    assertFalse(Files.exists(root.resolve("docs/doc-contract.md")));
  }

  @Test
  @DisplayName("FR-2: dispatch names every flow and is the only write map")
  void fr2_dispatchIsTheWriteMap() throws Exception {
    Path root = findRepoRoot();
    String dispatch = Files.readString(root.resolve("docs/protocol/environment/dispatch.md"));
    for (String flow :
        new String[] {
          "Reconcile",
          "Rollback",
          "Open goal",
          "Amend goal",
          "Close goal",
          "Store step",
          "Implement",
          "Sync",
          "Close step",
          "Amend step",
          "Restructure",
          "Scope",
          "Decide",
          "Record source"
        }) {
      assertTrue(dispatch.contains(flow), flow);
    }
    String door = Files.readString(root.resolve("docs/protocol/environment/README.md"));
    assertTrue(door.contains("dispatch.md"));
    assertTrue(door.toLowerCase().contains("may not"));
  }

  @Test
  @DisplayName("FR-3: Goal and Step only; session file and write-a-session instruction are gone")
  void fr3_goalAndStepOnly() throws Exception {
    Path root = findRepoRoot();
    assertFalse(Files.exists(root.resolve("docs/project/session.md")));
    String territory = Files.readString(root.resolve("docs/protocol/environment/territory.md"));
    assertTrue(territory.contains("Goal"));
    assertTrue(territory.contains("Step"));
    assertTrue(territory.contains("There is no session"));
    try (Stream<Path> walk = Files.walk(root.resolve("docs/protocol"))) {
      walk.filter(Files::isRegularFile).forEach(path -> {
        try {
          String text = Files.readString(path).toLowerCase();
          assertFalse(text.contains("write a session"), path.toString());
          assertFalse(text.contains("create session.md"), path.toString());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  @Test
  @DisplayName("FR-4: dictionary has protocol words and not product words")
  void fr4_dictionary() throws Exception {
    String dictionary =
        Files.readString(findRepoRoot().resolve("docs/protocol/environment/dictionary.md"));
    String lowerDictionary = dictionary.toLowerCase();
    for (String word : new String[] {"goal", "step", "shelf", "entrance", "blueprint", "flow", "accept", "witness", "torn"}) {
      assertTrue(lowerDictionary.contains(word), word);
    }
    assertFalse(lowerDictionary.contains("plate"));
    assertFalse(lowerDictionary.contains("java"));
    assertFalse(lowerDictionary.contains("locker"));
  }

  @Test
  @DisplayName("FR-5: each blueprint names a real document")
  void fr5_blueprintsNameRealDocs() throws Exception {
    Path dir = findRepoRoot().resolve("docs/protocol/blueprints");
    String[][] kinds = {
      {"readme.md", "README.md"},
      {"docs-index.md", "docs/README.md"},
      {"navigation.md", "docs/navigation.md"},
      {"agents.md", "AGENTS.md"},
      {"cursor-rule.md", "protocol.mdc"},
      {"environment.md", "territory.md"},
      {"dictionary.md", "dictionary.md"},
      {"dispatch.md", "dispatch.md"},
      {"nav-page.md", "pointers.md"},
      {"blueprint.md", "blueprints/"},
      {"flow.md", "flows/goals.md"},
      {"goal-index.md", "goals.md"},
      {"goal.md", "G-011-docs-restructuring.md"},
      {"step-registry.md", "features.md"},
      {"step.md", "F-062.md"},
      {"roadmap.md", "roadmap.md"},
      {"backlog.md", "backlog.md"},
      {"changelog.md", "changelog.md"},
      {"decisions.md", "decisions.md"},
      {"adr.md", "ADR-0"},
      {"scope.md", "project.md"},
      {"concept.md", "concept.md"},
      {"glossary.md", "docs/product/glossary.md"},
      {"style-guide.md", "style-guide.md"},
      {"journeys.md", "docs/product/flows.md"},
      {"wiki.md", "tectonics.md"},
      {"architecture.md", "docs/engine/architecture.md"},
      {"engine-glossary.md", "docs/engine/glossary.md"},
      {"spec.md", "open-questions.md"},
      {"source.md", "File"}
    };
    for (String[] kind : kinds) {
      Path file = dir.resolve(kind[0]);
      assertTrue(Files.isRegularFile(file), kind[0]);
      assertTrue(Files.readString(file).contains(kind[1]), kind[0] + " names " + kind[1]);
    }
  }

  @Test
  @DisplayName("FR-6: flow files hold the named algorithms as ordered steps")
  void fr6_flowAlgorithms() throws Exception {
    Path dir = findRepoRoot().resolve("docs/protocol/flows");
    assertHasSteps(dir.resolve("goals.md"), "Open goal", "Amend goal", "Close goal");
    assertHasSteps(dir.resolve("steps.md"), "Store step", "Implement", "Sync", "Close step", "Amend step");
    assertHasSteps(dir.resolve("structure.md"), "Restructure", "Scope", "Decide");
    assertHasSteps(dir.resolve("source.md"), "Record source");
    assertHasSteps(dir.resolve("judgment.md"), "Reconcile", "Rollback");
  }

  @Test
  @DisplayName("FR-7: protocol pages do not name the language, build tool, or witness command")
  void fr7_noLanguageOrBuildCommand() throws Exception {
    try (Stream<Path> walk = Files.walk(findRepoRoot().resolve("docs/protocol"))) {
      walk.filter(Files::isRegularFile).forEach(path -> {
        try {
          String text = Files.readString(path);
          String lower = text.toLowerCase();
          assertFalse(lower.contains("java"), path.toString());
          assertFalse(lower.contains("maven"), path.toString());
          assertFalse(lower.contains("mvnw"), path.toString());
          assertFalse(lower.contains("junit"), path.toString());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  @Test
  @DisplayName("FR-8: Active Goal line only on the goal index; doors point at protocol and name G-011")
  void fr8_doorsPointAtIndex() throws Exception {
    Path root = findRepoRoot();
    String goals = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goals.contains("**Active Goal:**"));
    assertTrue(goals.contains("G-011"));
    for (String door : new String[] {"AGENTS.md", ".cursor/rules/protocol.mdc", "docs/README.md", "docs/navigation.md"}) {
      String text = Files.readString(root.resolve(door));
      assertFalse(text.contains("**Active Goal:**"), door);
      assertTrue(text.contains("G-011"), door);
      assertTrue(text.contains("docs/protocol/README.md") || text.contains("protocol/README.md"), door);
      assertTrue(text.contains("goals.md"), door);
    }
  }

  @Test
  @DisplayName("FR-9: four docs levels only under the architecture shelf")
  void fr9_architectureDepth() throws Exception {
    String phase = Files.readString(findRepoRoot().resolve("docs/protocol/environment/phase.md"));
    assertTrue(phase.contains("four"));
    assertTrue(phase.contains("architecture"));
    assertTrue(phase.contains("No other shelf"));
  }

  private static void assertHasSteps(Path file, String... names) throws IOException {
    String text = Files.readString(file);
    assertTrue(text.contains("1."), file.toString());
    for (String name : names) {
      assertTrue(text.contains(name), file + " missing " + name);
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
