/*
 * File: engine/src/test/java/com/aethelgard/engine/CiWitnessTest.java
 * Purpose: F-009 CI pipeline witness — workflow file + doc contract
 * Audience: Agents / CI
 * Update when: F-009 FRs or CI workflow change
 */

package com.aethelgard.engine;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Maps to F-009 FRs. Prior Step F-001 tests remain in {@link ScaffoldWitnessTest}. */
class CiWitnessTest {

  private static Path repoRoot;
  private static String workflow;

  @BeforeAll
  static void loadWorkflow() throws IOException {
    repoRoot = findRepoRoot();
    Path ci = repoRoot.resolve(".github/workflows/ci.yml");
    assertTrue(Files.isRegularFile(ci), "CI workflow must exist at .github/workflows/ci.yml");
    workflow = Files.readString(ci);
  }

  @Test
  @DisplayName("FR-1: workflow under .github/workflows declares jobs")
  void workflowExistsAndDeclaresJobs() {
    assertTrue(!workflow.isBlank(), "workflow must be non-empty");
    assertTrue(workflow.contains("jobs:"), "workflow must declare jobs");
  }

  @Test
  @DisplayName("FR-2: runs on push and pull_request to main")
  void triggersPushAndPullRequestOnMain() {
    assertTrue(workflow.contains("push:"), "must trigger on push");
    assertTrue(workflow.contains("pull_request:"), "must trigger on pull_request");
    assertTrue(
        workflow.contains("branches: [main]") || workflow.contains("- main"),
        "must target main");
  }

  @Test
  @DisplayName("FR-3: job uses JDK 21 (Temurin or equivalent)")
  void usesJdk21() {
    assertTrue(
        workflow.contains("java-version: \"21\"") || workflow.contains("java-version: '21'")
            || workflow.contains("java-version: 21"),
        "must set java-version to 21");
    assertTrue(
        workflow.toLowerCase().contains("temurin")
            || workflow.toLowerCase().contains("zulu")
            || workflow.toLowerCase().contains("liberica")
            || workflow.toLowerCase().contains("microsoft"),
        "must name a JDK distribution (Temurin or equivalent)");
  }

  @Test
  @DisplayName("FR-4: invokes mvnw and runs tests")
  void runsMavenWrapperTests() {
    assertTrue(workflow.contains("mvnw"), "must invoke Maven Wrapper");
    assertTrue(workflow.contains("test"), "must run tests");
  }

  @Test
  @DisplayName("FR-5: docs record that CI runs the Maven witness")
  void docsRecordCiWitness() throws IOException {
    Path arch = repoRoot.resolve("docs/architecture/program.md");
    assertTrue(Files.isRegularFile(arch), "engine architecture must exist");
    String text = Files.readString(arch);
    assertTrue(
        text.contains("GitHub Actions") || text.contains("CI"),
        "architecture must mention CI / GitHub Actions");
    assertTrue(
        text.contains("mvnw") || text.contains("Maven witness") || text.contains("test"),
        "architecture must tie CI to the Maven witness");
  }

  private static Path findRepoRoot() {
    Path dir = Path.of("").toAbsolutePath().normalize();
    for (Path cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("engine"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    fail("Could not find repo root from " + dir);
    return dir;
  }
}
