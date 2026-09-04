/*
 * File: engine/src/test/java/com/aethelgard/engine/system/ClaimFinishDeterminismTest.java
 * Purpose: F-005 witness — claim/finish barrier + determinism
 * Audience: Agents / CI
 * Update when: F-005 FRs change
 */

package com.aethelgard.engine.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.event.EventClaimer;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.pool.PoolSnapshot;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClaimFinishDeterminismTest {

  @Test
  @DisplayName("FR-1: claiming Systems increment claim and finish once each before merge")
  void claimingSystemsFinishOnce() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem a = systemWriting(tree, "sysA", "x", 1L);
    EngineSystem b = systemWriting(tree, "sysB", "y", 2L);
    FieldSchema schema =
        FieldSchema.of(Map.of("x", FieldType.STATIC, "y", FieldType.STATIC));

    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("x", 0L, "y", 0L)),
            new EngineSetup(
                tree, List.of(), List.of(a, b), schema, EngineDiagnostics.noop()));

    ClaimFinishSnapshot barrier = engine.lastClaimFinish();
    assertEquals(2, barrier.claimCount());
    assertEquals(2, barrier.finishCount());
    assertEquals(List.of("sysA", "sysB"), barrier.finishedSystemIds());
    assertTrue(barrier.isBalanced());
  }

  @Test
  @DisplayName("FR-2: barrier rejects unbalanced claim/finish; Engine exposes balanced snapshot")
  void barrierGatesMerge() {
    ClaimFinishBarrier barrier = new ClaimFinishBarrier();
    barrier.onClaimed("lonely");
    assertThrows(IllegalStateException.class, barrier::requireBalanced);

    barrier.onFinished("lonely");
    barrier.requireBalanced(); // no throw
    assertTrue(barrier.snapshot().isBalanced());

    CategoryTree tree = CategoryTree.of("world");
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("n", 0L)),
            new EngineSetup(
                tree,
                List.of(),
                List.of(systemWriting(tree, "sys", "n", 5L)),
                FieldSchema.of("n", FieldType.STATIC),
                EngineDiagnostics.noop()));
    assertTrue(engine.lastClaimFinish().isBalanced());
    assertEquals(5L, engine.settled().field("n"));
  }

  @Test
  @DisplayName("FR-3: stub claimers do not inflate System claim/finish counters")
  void stubClaimersDoNotCount() {
    CategoryTree tree = CategoryTree.of("world");
    EventClaimer stub = new EventClaimer("stub", tree.get("world"));

    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world")),
            new EngineSetup(tree, List.of(stub), EngineDiagnostics.noop()));

    assertEquals(0, engine.lastClaimFinish().claimCount());
    assertEquals(0, engine.lastClaimFinish().finishCount());
    assertTrue(engine.lastClaimResult().unmatched().isEmpty());
    assertEquals(1, engine.lastClaimResult().claimedByClaimer().get(stub).size());
  }

  @Test
  @DisplayName("FR-4: same config + setup + N advances → equal settled Pool")
  void identicalRunsDeterministic() {
    EngineConfig config =
        new EngineConfig(3L, List.of("world"), Map.of("acc", 0L, "flag", 0L));
    EngineSetup setup = multiSystemSetup();

    Engine a = Engine.create(config, setup);
    a.advance(4);
    Engine b = Engine.create(config, setup);
    b.advance(4);

    assertEquals(a.settled(), b.settled());
    assertEquals(a.stepIndex(), b.stepIndex());
  }

  @Test
  @DisplayName("FR-5: permuting System list order yields same settled Pool")
  void systemOrderIndependent() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem left = systemWriting(tree, "left", "x", 10L);
    EngineSystem right = systemWriting(tree, "right", "y", 20L);
    FieldSchema schema =
        FieldSchema.of(Map.of("x", FieldType.INCREMENT, "y", FieldType.INCREMENT));
    EngineConfig config =
        new EngineConfig(0L, List.of("world"), Map.of("x", 0L, "y", 0L));

    Engine forward =
        Engine.create(
            config,
            new EngineSetup(
                tree, List.of(), List.of(left, right), schema, EngineDiagnostics.noop()));
    forward.advance(2);

    Engine reverse =
        Engine.create(
            config,
            new EngineSetup(
                tree, List.of(), List.of(right, left), schema, EngineDiagnostics.noop()));
    reverse.advance(2);

    PoolSnapshot a = forward.settled();
    PoolSnapshot b = reverse.settled();
    assertEquals(a.value(), b.value());
    assertEquals(a.updateCount(), b.updateCount());
    assertEquals(a.fields(), b.fields());
  }

  @Test
  @DisplayName("FR-6: engine has no UI/CLI/product compile deps")
  void noUiCliProductCompileDeps() throws Exception {
    var root = findRepoRoot();
    String enginePom = java.nio.file.Files.readString(root.resolve("engine/pom.xml"));
    assertTrue(!enginePom.contains("<artifactId>ui</artifactId>"));
    assertTrue(!enginePom.contains("<artifactId>cli</artifactId>"));
    assertTrue(!enginePom.contains("<artifactId>product</artifactId>"));
  }

  private static EngineSetup multiSystemSetup() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem acc =
        new EngineSystem(
            new SystemConfig(
                "accSys",
                tree.get("world"),
                List.of(writeSub("w", Set.of("acc"), io -> io.write("acc", 1L))),
                null));
    EngineSystem flag =
        new EngineSystem(
            new SystemConfig(
                "flagSys",
                tree.get("world"),
                List.of(writeSub("w", Set.of("flag"), io -> io.write("flag", io.poolValue()))),
                null));
    FieldSchema schema =
        FieldSchema.of(Map.of("acc", FieldType.INCREMENT, "flag", FieldType.STATIC));
    return new EngineSetup(
        tree, List.of(), List.of(acc, flag), schema, EngineDiagnostics.noop());
  }

  private static EngineSystem systemWriting(
      CategoryTree tree, String systemId, String field, long value) {
    return new EngineSystem(
        new SystemConfig(
            systemId,
            tree.get("world"),
            List.of(writeSub("w", Set.of(field), io -> io.write(field, value))),
            null));
  }

  private static SubSystem writeSub(
      String id, Set<String> ranges, Consumer<SubSystemIo> body) {
    return new SubSystem() {
      @Override
      public String id() {
        return id;
      }

      @Override
      public Set<String> writeRanges() {
        return ranges;
      }

      @Override
      public void execute(SubSystemIo io) {
        body.accept(io);
      }
    };
  }

  private static java.nio.file.Path findRepoRoot() {
    var dir = java.nio.file.Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (java.nio.file.Files.isRegularFile(cursor.resolve("pom.xml"))
          && java.nio.file.Files.isDirectory(cursor.resolve("engine"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found");
  }
}
