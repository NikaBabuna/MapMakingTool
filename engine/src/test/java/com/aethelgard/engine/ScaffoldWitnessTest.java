/*
 * File: engine/src/test/java/com/aethelgard/engine/ScaffoldWitnessTest.java
 * Purpose: F-001 infrastructure witness — layout, coordinates, Java 21, isolation
 * Audience: Agents / CI
 * Update when: F-001 FRs or scaffold layout change
 */

package com.aethelgard.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Maps to F-001 FRs. Suite green via {@code mvnw.cmd test} from repo root (FR-1).
 */
class ScaffoldWitnessTest {

  private static Path repoRoot;
  private static String parentPom;
  private static String enginePom;

  @BeforeAll
  static void loadPoms() throws IOException {
    repoRoot = findRepoRoot();
    parentPom = Files.readString(repoRoot.resolve("pom.xml"));
    enginePom = Files.readString(repoRoot.resolve("engine/pom.xml"));
  }

  @Test
  @DisplayName("FR-2: parent is pom packaging with exactly one module: engine")
  void parentIsAggregatorWithEngineOnly() {
    assertTrue(parentPom.contains("<packaging>pom</packaging>"), "parent packaging must be pom");
    List<String> modules = captureAll(parentPom, "<module>([^<]+)</module>");
    assertEquals(List.of("engine"), modules, "F-001 must have exactly the engine module");
  }

  @Test
  @DisplayName("FR-3: Java release is 21")
  void javaReleaseIs21() {
    assertTrue(
        parentPom.contains("<maven.compiler.release>21</maven.compiler.release>"),
        "parent must set maven.compiler.release to 21");
  }

  @Test
  @DisplayName("FR-4: coordinates are com.aethelgard:aethelgard and com.aethelgard:engine")
  void mavenCoordinates() {
    assertTrue(
        parentPom.contains("<groupId>com.aethelgard</groupId>"),
        "parent groupId must be com.aethelgard");
    assertTrue(
        parentPom.contains("<artifactId>aethelgard</artifactId>"),
        "parent artifactId must be aethelgard");
    assertTrue(
        enginePom.contains("<artifactId>engine</artifactId>"),
        "engine artifactId must be engine");
    assertTrue(
        enginePom.contains("<groupId>com.aethelgard</groupId>")
            || parentPom.contains("<groupId>com.aethelgard</groupId>"),
        "engine inherits com.aethelgard groupId");
  }

  @Test
  @DisplayName("FR-5: production types live under com.aethelgard.engine")
  void packageRootIsComAethelgardEngine() {
    assertEquals("com.aethelgard.engine", ScaffoldWitnessTest.class.getPackageName());
    assertTrue(
        Files.isRegularFile(
            repoRoot.resolve(
                "engine/src/main/java/com/aethelgard/engine/package-info.java")),
        "package-info.java must exist under com.aethelgard.engine");
  }

  @Test
  @DisplayName("FR-6: engine has no compile-scope UI/CLI/product dependencies")
  void engineHasNoAdapterOrProductCompileDeps() {
    // Strip all test-scoped dependencies so we only judge compile classpath intent
    String withoutTestDeps =
        enginePom.replaceAll(
            "(?s)<dependency>\\s*(?:(?!</dependency>).)*?<scope>test</scope>\\s*</dependency>",
            "");

    assertFalse(
        withoutTestDeps.toLowerCase().contains("javafx"),
        "engine must not depend on JavaFX");
    assertFalse(
        withoutTestDeps.toLowerCase().contains("swing"),
        "engine must not declare Swing UI deps");
    assertFalse(
        withoutTestDeps.contains("picocli") || withoutTestDeps.contains("commons-cli"),
        "engine must not depend on a CLI library");
    assertFalse(
        withoutTestDeps.contains("<artifactId>product</artifactId>")
            || withoutTestDeps.contains("<artifactId>cli</artifactId>")
            || withoutTestDeps.contains("<artifactId>ui</artifactId>"),
        "engine must not depend on product/cli/ui modules");
    assertFalse(
        withoutTestDeps.toLowerCase().contains("logback"),
        "engine must not depend on Logback (SLF4J API only)");
  }

  @Test
  @DisplayName("FR-7: at least one JUnit test executes in engine")
  void suiteIsNotVacuouslyEmpty() {
    assertTrue(true, "this method running satisfies FR-7");
  }

  @Test
  @DisplayName("FR-8: engine architecture doc records layout contract")
  void architectureDocRecordsLayout() throws IOException {
    Path arch = repoRoot.resolve("docs/engine/architecture.md");
    assertTrue(Files.isRegularFile(arch), "docs/engine/architecture.md must exist");
    String text = Files.readString(arch);
    assertTrue(text.contains("com.aethelgard.engine"), "must record package root");
    assertTrue(text.contains("Java 21") || text.contains("release 21"), "must record Java 21");
    assertTrue(text.contains("engine"), "must record engine module");
    assertTrue(
        text.toLowerCase().contains("one-way") || text.contains("never the reverse"),
        "must record one-way dependency rule");
  }

  @Test
  @DisplayName("FR-9: .gitignore excludes target/ and IDE output")
  void gitignoreCoversBuildProducts() throws IOException {
    Path gitignore = repoRoot.resolve(".gitignore");
    assertTrue(Files.isRegularFile(gitignore), ".gitignore must exist");
    String text = Files.readString(gitignore);
    assertTrue(text.contains("target/"), "must ignore Maven target/");
    assertTrue(
        text.contains(".idea/") || text.contains("*.iml") || text.contains(".vscode/"),
        "must ignore typical IDE output");
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

  private static List<String> captureAll(String text, String regex) {
    Matcher m = Pattern.compile(regex).matcher(text);
    return m.results().map(r -> r.group(1)).collect(Collectors.toList());
  }
}
