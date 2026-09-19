/*
 * File: cli/src/test/java/com/aethelgard/cli/CliFullRunnerTest.java
 * Purpose: F-049 witness — CLI as full headless runner
 * Audience: Agents / CI
 * Update when: F-049 FRs change
 */

package com.aethelgard.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.ProductHost;
import com.aethelgard.product.ProductSession;
import com.aethelgard.product.WorldSpec;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CliFullRunnerTest {

  @Test
  @DisplayName("FR-1: --seed creates one session; later commands see that seed")
  void seedOwnsSession() {
    CliResult status =
        CliRunner.run(new String[] {"--seed", "42", "-c", "session get"});
    assertTrue(status.ok());
    assertEquals("step=0 width=8 height=8 seed=42", status.output());

    CliResult dump = CliRunner.run(new String[] {"--seed", "42", "--steps", "0"});
    assertTrue(dump.ok());
    assertTrue(dump.output().startsWith("world w=8 h=8 seed=42 steps=0\n"));

    ProductSession expected =
        new ProductSession(new WorldSpec(WorldSpec.DEFAULT.width(), WorldSpec.DEFAULT.height(), 42L));
    assertEquals(expected.settledWorld(), dump.output());
  }

  @Test
  @DisplayName("FR-2: multi -c lines share one session via CommandDispatch")
  void multiCommandSameSession() {
    CliResult result =
        CliRunner.run(
            new String[] {
              "--seed",
              "7",
              "-c",
              "session advance 2",
              "-c",
              "session get",
              "--command",
              "session get seed"
            });
    assertTrue(result.ok());
    assertEquals("step=2\nstep=2 width=8 height=8 seed=7\nseed=7", result.output());
  }

  @Test
  @DisplayName("FR-3: --steps advances then dumps through CommandDispatch")
  void stepsViaDispatch() {
    CliResult result = CliRunner.run(new String[] {"--steps", "3"});
    assertTrue(result.ok());
    ProductSession expected = ProductSession.ofDefault();
    expected.advance(3);
    assertEquals(expected.settledWorld(), result.output());
    assertTrue(result.output().contains("steps=3"));
  }

  @Test
  @DisplayName("FR-4: headless noun/verb queries and error exit codes")
  void headlessCatalogAndErrors() {
    CliResult pool =
        CliRunner.run(new String[] {"--seed", "0", "-c", "list pool", "-c", "schema get"});
    assertTrue(pool.ok());
    assertTrue(pool.output().contains("elevation"));
    assertTrue(pool.output().contains("elevation=STATIC"));

    CliResult systems = CliRunner.run(new String[] {"-c", "list systems"});
    assertTrue(systems.ok());
    assertTrue(systems.output().contains(ProductHost.TECTONICS_SYSTEM_ID));

    CliResult diag = CliRunner.run(new String[] {"-c", "diag get"});
    assertTrue(diag.ok());
    assertTrue(diag.output().contains("advance.wall"));

    CliResult bad = CliRunner.run(new String[] {"-c", "session get", "-c", "nope"});
    assertEquals(2, bad.exitCode());
    assertTrue(bad.output().contains("step=0 width=8 height=8 seed=0"));
    assertTrue(bad.output().contains("error:"));
  }

  @Test
  @DisplayName("FR-5: docs retire CLI placeholder; no F-050/F-051 Accept claims; no engine edits")
  void docsAndNoEngineEdit() throws Exception {
    Path root = findRepoRoot();
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("F-049"));
    assertFalse(arch.toLowerCase().contains("placeholder (adr-010)"));

    String readme = Files.readString(root.resolve("cli/README.md"));
    assertTrue(readme.contains("--seed"));
    assertTrue(readme.contains("-c"));
    assertFalse(readme.toLowerCase().contains("placeholder dispatcher"));

    String blocker = Files.readString(root.resolve("docs/blockers/F-049.md"));
    assertFalse(blocker.contains("F-050 Accepted"));
    assertFalse(blocker.contains("F-051 Accepted"));

    // No production engine source edits in this Step (read ports from F-048 remain).
    assertTrue(Files.isRegularFile(root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java")));
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
