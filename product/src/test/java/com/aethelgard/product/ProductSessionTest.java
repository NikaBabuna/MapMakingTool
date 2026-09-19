/*
 * File: product/src/test/java/com/aethelgard/product/ProductSessionTest.java
 * Purpose: F-019 witness — ProductSession, serialized advance, unchanged world rules
 * Audience: Agents / CI
 * Update when: F-019 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductSessionTest {

  @Test
  @DisplayName("FR-3: session creates, reads grids, advances; no CLI verbs in type")
  void sessionOwnsRun() throws Exception {
    ProductSession session = ProductSession.ofDefault();
    assertEquals(WorldSpec.DEFAULT, session.spec());
    assertEquals(0, session.stepIndex());
    Grid elevation = session.elevation();
    Grid plates = session.plates();
    assertEquals(8, elevation.width());
    assertEquals(8, plates.height());
    for (int y = 0; y < elevation.height(); y++) {
      for (int x = 0; x < elevation.width(); x++) {
        assertEquals(0, elevation.get(x, y));
      }
    }

    session.advance();
    assertEquals(1, session.stepIndex());
    assertNotEquals(elevation, session.elevation());

    String src =
        Files.readString(
            findRepoRoot()
                .resolve("product/src/main/java/com/aethelgard/product/ProductSession.java"));
    assertFalse(src.contains("javax.swing"));
    assertFalse(src.contains("--steps"));
    assertFalse(src.contains("parse("));
  }

  @Test
  @DisplayName("FR-3: advance(n) is serialized; negative n fails")
  void serializedAdvance() throws Exception {
    ProductSession session = ProductSession.ofDefault();
    Thread a = new Thread(session::advance);
    Thread b = new Thread(session::advance);
    a.start();
    b.start();
    a.join();
    b.join();
    assertEquals(2, session.stepIndex());

    assertThrows(IllegalArgumentException.class, () -> session.advance(-1));
    session.advance(0);
    assertEquals(2, session.stepIndex());
  }

  @Test
  @DisplayName("FR-4: same seed + N Steps as ProductHost; VIEW 1920×1080; dump golden path")
  void worldRulesUnchanged() {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine engine = ProductHost.create(spec);
    engine.advance(WorldDump.CANONICAL_STEPS);
    ProductSession session = ProductSession.ofDefault();
    session.advance(WorldDump.CANONICAL_STEPS);
    assertEquals(WorldDump.of(engine, spec), session.settledWorld());
    assertEquals(WorldDump.CANONICAL_STEPS, session.stepIndex());

    ProductSession view = ProductSession.view();
    assertEquals(1920, view.spec().width());
    assertEquals(1080, view.spec().height());
    assertEquals(0L, view.spec().seed());
    assertEquals(1920, view.elevation().width());
    assertEquals(1080, view.plates().height());
    assertEquals(0, view.stepIndex());
  }

  @Test
  @DisplayName("FR-2: product main sources have no Swing / JFrame / MapFrame")
  void productMainHasNoSwing() throws Exception {
    Path main = findRepoRoot().resolve("product/src/main/java");
    try (Stream<Path> walk = Files.walk(main)) {
      walk.filter(p -> p.toString().endsWith(".java"))
          .forEach(
              path -> {
                try {
                  String text = Files.readString(path);
                  assertFalse(text.contains("javax.swing"), path.toString());
                  assertFalse(text.contains("JFrame"), path.toString());
                  assertFalse(text.contains("java.awt"), path.toString());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              });
    }
    assertFalse(
        Files.exists(
            findRepoRoot()
                .resolve("product/src/main/java/com/aethelgard/product/MapFrame.java")));
    assertFalse(
        Files.exists(
            findRepoRoot()
                .resolve("product/src/main/java/com/aethelgard/product/ProductApp.java")));
    String goal = Files.readString(findRepoRoot().resolve("docs/project/goals/G-004-see-the-world.md"));
    assertTrue(goal.contains("**Status:** `done`"));
    assertFalse(goal.contains("**Status:** `in progress`"));
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
