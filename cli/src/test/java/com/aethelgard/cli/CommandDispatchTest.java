/*
 * File: cli/src/test/java/com/aethelgard/cli/CommandDispatchTest.java
 * Purpose: F-023 placeholder dispatcher + headless verbs
 * Audience: Agents / CI
 * Update when: F-023 FRs change
 */

package com.aethelgard.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.ProductSession;
import com.aethelgard.product.WorldDump;
import com.aethelgard.product.WorldSpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommandDispatchTest {

  @Test
  @DisplayName("FR-1: verbs on a session; unknown/empty error; no verb table in product")
  void verbsAndProductHasNoTable() throws Exception {
    ProductSession session = ProductSession.ofDefault();
    CliResult status = CommandDispatch.execute(session, "status");
    assertTrue(status.ok());
    assertEquals("step=0 width=8 height=8 seed=0", status.output());

    assertEquals("step=1", CommandDispatch.execute(session, "advance").output());
    assertEquals("step=3", CommandDispatch.execute(session, "advance 2").output());

    CliResult at = CommandDispatch.execute(session, "at 0 0");
    assertTrue(at.ok());
    assertTrue(at.output().startsWith("x=0 y=0 elevation="));
    assertTrue(at.output().contains(" plate="));
    assertTrue(at.output().contains(" vx="));

    assertEquals("elevation\nplates\nplate_velocity", CommandDispatch.execute(session, "layers").output());

    CliResult dump = CommandDispatch.execute(session, "dump");
    assertTrue(dump.ok());
    assertTrue(dump.output().startsWith("world w=8 h=8 seed=0 steps=3\n"));

    CliResult unknown = CommandDispatch.execute(session, "teleport");
    assertEquals(2, unknown.exitCode());
    assertTrue(unknown.output().startsWith("error: unknown command:"));

    assertEquals(2, CommandDispatch.execute(session, "  ").exitCode());

    Path root = findRepoRoot();
    try (Stream<Path> files = Files.walk(root.resolve("product/src/main/java"))) {
      files
          .filter(p -> p.toString().endsWith(".java"))
          .forEach(
              path -> {
                try {
                  String src = Files.readString(path);
                  assertFalse(src.contains("CommandDispatch"), path.toString());
                  assertFalse(src.contains("unknown command:"), path.toString());
                  assertFalse(src.contains("case \"status\""), path.toString());
                  assertFalse(src.contains("case \"layers\""), path.toString());
                } catch (Exception ex) {
                  throw new RuntimeException(ex);
                }
              });
    }
    String productPom = Files.readString(root.resolve("product/pom.xml"));
    assertFalse(productPom.contains("<artifactId>cli</artifactId>"));
  }

  @Test
  @DisplayName("FR-2: --steps dump unchanged; argv verbs; cli does not depend on ui")
  void headlessFlagsAndVerbs() throws Exception {
    CliResult batch = CliRunner.run(new String[] {"--steps", "3"});
    assertTrue(batch.ok());
    ProductSession expected = ProductSession.ofDefault();
    expected.advance(WorldDump.CANONICAL_STEPS);
    assertEquals(expected.settledWorld(), batch.output());

    CliResult verb = CliRunner.run(new String[] {"status"});
    assertTrue(verb.ok());
    assertEquals("step=0 width=8 height=8 seed=0", verb.output());

    CliResult at = CliRunner.run(new String[] {"at", "1", "1"});
    assertTrue(at.ok());
    assertTrue(at.output().startsWith("x=1 y=1 "));

    Path root = findRepoRoot();
    String cliPom = Files.readString(root.resolve("cli/pom.xml"));
    assertFalse(cliPom.contains("<artifactId>ui</artifactId>"));
    String dispatch =
        Files.readString(root.resolve("cli/src/main/java/com/aethelgard/cli/CommandDispatch.java"));
    assertFalse(dispatch.contains("javax.swing"));
  }

  @Test
  @DisplayName("FR-4: concurrent dispatcher advances serialize on one session")
  void serializedDispatch() throws Exception {
    ProductSession session = ProductSession.ofDefault();
    ExecutorService pool = Executors.newFixedThreadPool(2);
    try {
      Future<CliResult> a = pool.submit(() -> CommandDispatch.execute(session, "advance 20"));
      Future<CliResult> b = pool.submit(() -> CommandDispatch.execute(session, "advance 20"));
      assertTrue(a.get().ok());
      assertTrue(b.get().ok());
      assertEquals(40, session.stepIndex());
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  @DisplayName("FR-5: same seed + N Steps identical; no engine production edits this Step")
  void determinismAndEngineUntouched() throws Exception {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    ProductSession a = new ProductSession(spec);
    ProductSession b = new ProductSession(spec);
    CommandDispatch.execute(a, "advance 5");
    CommandDispatch.execute(b, "advance 5");
    assertEquals(a.settledWorld(), b.settledWorld());

    Path root = findRepoRoot();
    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    assertFalse(enginePom.contains("<artifactId>cli</artifactId>"));
    assertFalse(enginePom.contains("<artifactId>ui</artifactId>"));
    assertFalse(enginePom.contains("<artifactId>product</artifactId>"));
  }

  private static Path findRepoRoot() {
    var dir = Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("engine"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found");
  }
}
