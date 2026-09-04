/*
 * File: cli/src/test/java/com/aethelgard/cli/CliRunnerTest.java
 * Purpose: F-007 witness — CLI module, N Steps, settled report, invalid args
 * Audience: Agents / CI
 * Update when: F-007 FRs change
 */

package com.aethelgard.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CliRunnerTest {

  @Test
  @DisplayName("FR-1: parent lists cli; cli depends on engine; engine does not depend on cli")
  void moduleWiringOneWay() throws Exception {
    Path root = findRepoRoot();
    String parent = Files.readString(root.resolve("pom.xml"));
    assertTrue(parent.contains("<module>cli</module>"));

    String cliPom = Files.readString(root.resolve("cli/pom.xml"));
    assertTrue(cliPom.contains("<artifactId>cli</artifactId>"));
    assertTrue(cliPom.contains("<artifactId>engine</artifactId>"));

    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    assertFalse(enginePom.contains("<artifactId>cli</artifactId>"));
  }

  @Test
  @DisplayName("FR-2: CLI advances N additional Steps after create")
  void runsNAdditionalSteps() {
    CliResult result = CliRunner.run(new String[] {"--steps", "3", "--initial", "0"});
    assertTrue(result.ok());
    assertTrue(result.output().contains("stepIndex=3"));
    assertTrue(result.output().contains("updateCount=4"));
  }

  @Test
  @DisplayName("FR-3: report includes stepIndex, value, updateCount")
  void reportsSettledState() {
    CliResult result = CliRunner.run(new String[] {"--steps", "0", "--initial", "10"});
    assertEquals(0, result.exitCode());
    String out = result.output();
    assertTrue(out.contains("stepIndex=0"));
    assertTrue(out.contains("value=11"));
    assertTrue(out.contains("updateCount=1"));
  }

  @Test
  @DisplayName("FR-4: negative steps and unknown flags fail non-zero")
  void invalidArgsFail() {
    CliResult negative = CliRunner.run(new String[] {"--steps", "-1"});
    assertEquals(2, negative.exitCode());
    assertTrue(negative.output().startsWith("error:"));

    CliResult unknown = CliRunner.run(new String[] {"--bogus"});
    assertEquals(2, unknown.exitCode());
    assertTrue(unknown.output().contains("unknown argument"));
  }

  @Test
  @DisplayName("FR-5: runner is headless (no stdin); defaults run Step 0 only")
  void headlessDefaults() {
    CliResult result = CliRunner.run(new String[0]);
    assertTrue(result.ok());
    assertTrue(result.output().contains("stepIndex=0"));
    assertTrue(result.output().contains("value=1"));
  }

  @Test
  @DisplayName("FR-6: cli README and navigation/architecture mention the module")
  void docsMentionCliModule() throws Exception {
    Path root = findRepoRoot();
    assertTrue(Files.isRegularFile(root.resolve("cli/README.md")));
    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("cli"));
    String arch = Files.readString(root.resolve("docs/engine/architecture.md"));
    assertTrue(arch.toLowerCase().contains("cli"));
  }

  private static Path findRepoRoot() {
    var dir = Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("engine"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found");
  }
}
