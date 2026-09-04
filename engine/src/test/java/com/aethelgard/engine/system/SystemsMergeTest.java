/*
 * File: engine/src/test/java/com/aethelgard/engine/system/SystemsMergeTest.java
 * Purpose: F-004 witness — Systems, Sub-Systems, typed merge, provenance
 * Audience: Agents / CI
 * Update when: F-004 FRs change
 */

package com.aethelgard.engine.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.merge.ProvenancedWrite;
import com.aethelgard.engine.merge.TypedMerge;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.pool.PoolSnapshot;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SystemsMergeTest {

  @Test
  @DisplayName("FR-1: claiming System runs Sub-Systems and produces OUT_SYS into Step output")
  void systemProducesOutSys() {
    CategoryTree tree = CategoryTree.of("world");
    SubSystem bump =
        writeSub("bump", Set.of("score"), io -> io.write("score", io.poolValue()));
    EngineSystem system =
        new EngineSystem(new SystemConfig("sysA", tree.get("world"), List.of(bump), null));

    FieldSchema schema = FieldSchema.of("score", FieldType.STATIC);
    Engine engine =
        Engine.create(
            new EngineConfig(10L, List.of("world"), Map.of("score", 0L)),
            new EngineSetup(
                tree, List.of(), List.of(system), schema, EngineDiagnostics.noop()));

    assertEquals(1, engine.lastStepOutput().asMap().get("score").size());
    assertEquals("sysA", engine.lastStepOutput().asMap().get("score").getFirst().systemId());
    // Step 0: value became 11; Sub-System wrote poolValue 11
    assertEquals(11L, engine.settled().field("score"));
  }

  @Test
  @DisplayName("FR-2: Systems share one Pool snapshot; neither sees the other's OUT_SYS same Step")
  void systemIndependence() {
    CategoryTree tree = CategoryTree.of("world");
    SubSystem writer =
        writeSub("writer", Set.of("mark"), io -> io.write("mark", 99L));
    SubSystem reader =
        writeSub(
            "reader",
            Set.of("seen"),
            io -> io.write("seen", io.readPool("mark")));

    EngineSystem sysA =
        new EngineSystem(new SystemConfig("sysA", tree.get("world"), List.of(writer), null));
    EngineSystem sysB =
        new EngineSystem(new SystemConfig("sysB", tree.get("world"), List.of(reader), null));

    FieldSchema schema =
        FieldSchema.of(
            Map.of("mark", FieldType.STATIC, "seen", FieldType.STATIC));
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("mark", 0L, "seen", -1L)),
            new EngineSetup(
                tree, List.of(), List.of(sysA, sysB), schema, EngineDiagnostics.noop()));

    // sysB read pre-merge mark (0), not sysA's 99
    assertEquals(99L, engine.settled().field("mark"));
    assertEquals(0L, engine.settled().field("seen"));
  }

  @Test
  @DisplayName("FR-3: disjoint write-ranges — Sub-System order does not change OUT_SYS")
  void disjointOrderIndependent() {
    CategoryTree tree = CategoryTree.of("world");
    SubSystem left =
        writeSub("left", Set.of("x"), io -> io.write("x", io.poolValue() + 1));
    SubSystem right =
        writeSub("right", Set.of("y"), io -> io.write("y", io.poolValue() + 2));

    PoolSnapshot snap = new PoolSnapshot(5L, 1, Map.of("x", 0L, "y", 0L));
    Map<String, Long> forward =
        new EngineSystem(new SystemConfig("s", tree.get("world"), List.of(left, right), null))
            .run(snap);
    Map<String, Long> reverse =
        new EngineSystem(new SystemConfig("s", tree.get("world"), List.of(right, left), null))
            .run(snap);

    assertEquals(forward, reverse);
    assertEquals(6L, forward.get("x"));
    assertEquals(7L, forward.get("y"));
  }

  @Test
  @DisplayName("FR-3: overlapping write-ranges use conflict-resolution order")
  void overlappingUsesResolver() {
    CategoryTree tree = CategoryTree.of("world");
    SubSystem first = writeSub("first", Set.of("slot"), io -> io.write("slot", 1L));
    SubSystem second = writeSub("second", Set.of("slot"), io -> io.write("slot", 2L));

    ConflictResolutionSubSystem firstThenSecond = conflict -> List.of(first, second);
    ConflictResolutionSubSystem secondThenFirst = conflict -> List.of(second, first);

    PoolSnapshot snap = new PoolSnapshot(0L, 1, Map.of("slot", 0L));
    Map<String, Long> a =
        new EngineSystem(
                new SystemConfig(
                    "s", tree.get("world"), List.of(first, second), firstThenSecond))
            .run(snap);
    Map<String, Long> b =
        new EngineSystem(
                new SystemConfig(
                    "s", tree.get("world"), List.of(first, second), secondThenFirst))
            .run(snap);

    assertEquals(2L, a.get("slot")); // last writer wins within System
    assertEquals(1L, b.get("slot"));
  }

  @Test
  @DisplayName("FR-3: overlapping ranges without resolver fail")
  void overlappingWithoutResolverFails() {
    CategoryTree tree = CategoryTree.of("world");
    SubSystem a = writeSub("a", Set.of("slot"), io -> io.write("slot", 1L));
    SubSystem b = writeSub("b", Set.of("slot"), io -> io.write("slot", 2L));
    EngineSystem system =
        new EngineSystem(new SystemConfig("s", tree.get("world"), List.of(a, b), null));

    assertThrows(
        IllegalStateException.class,
        () -> system.run(new PoolSnapshot(0L, 1, Map.of("slot", 0L))));
  }

  @Test
  @DisplayName("FR-4: Step output carries provenance; Static pick uses systemId pairs")
  void provenanceOnConflictingWrites() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem alpha =
        new EngineSystem(
            new SystemConfig(
                "alpha",
                tree.get("world"),
                List.of(writeSub("w", Set.of("flag"), io -> io.write("flag", 10L))),
                null));
    EngineSystem beta =
        new EngineSystem(
            new SystemConfig(
                "beta",
                tree.get("world"),
                List.of(writeSub("w", Set.of("flag"), io -> io.write("flag", 20L))),
                null));

    FieldSchema schema = FieldSchema.of("flag", FieldType.STATIC);
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("flag", 0L)),
            new EngineSetup(
                tree, List.of(), List.of(alpha, beta), schema, EngineDiagnostics.noop()));

    List<ProvenancedWrite> writes = engine.lastStepOutput().asMap().get("flag");
    assertEquals(2, writes.size());
    assertTrue(writes.stream().anyMatch(w -> w.systemId().equals("alpha") && w.value() == 10L));
    assertTrue(writes.stream().anyMatch(w -> w.systemId().equals("beta") && w.value() == 20L));
    // lexicographically smallest systemId wins
    assertEquals(10L, engine.settled().field("flag"));
    assertEquals("alpha", TypedMerge.pickOne(writes).systemId());
  }

  @Test
  @DisplayName("FR-5: Increment sums conflicting writes onto standing value")
  void incrementSums() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem a =
        systemWriting(tree, "a", "n", 3L);
    EngineSystem b =
        systemWriting(tree, "b", "n", 5L);

    FieldSchema schema = FieldSchema.of("n", FieldType.INCREMENT);
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("n", 10L)),
            new EngineSetup(
                tree, List.of(), List.of(a, b), schema, EngineDiagnostics.noop()));

    assertEquals(18L, engine.settled().field("n"));
  }

  @Test
  @DisplayName("FR-5: Constant ignores writes and keeps standing Pool value")
  void constantKeepsStanding() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem a = systemWriting(tree, "a", "locked", 999L);

    FieldSchema schema = FieldSchema.of("locked", FieldType.CONSTANT);
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("locked", 7L)),
            new EngineSetup(
                tree, List.of(), List.of(a), schema, EngineDiagnostics.noop()));

    assertEquals(7L, engine.settled().field("locked"));
  }

  @Test
  @DisplayName("FR-5: Destructive pick-one by systemId (one write valid per Step)")
  void destructivePickOne() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem zebra = systemWriting(tree, "zebra", "token", 1L);
    EngineSystem aardvark = systemWriting(tree, "aardvark", "token", 2L);

    FieldSchema schema = FieldSchema.of("token", FieldType.DESTRUCTIVE);
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("token", 0L)),
            new EngineSetup(
                tree,
                List.of(),
                List.of(zebra, aardvark),
                schema,
                EngineDiagnostics.noop()));

    assertEquals(2L, engine.settled().field("token")); // aardvark < zebra
  }

  @Test
  @DisplayName("FR-6: merge applies once; event buffer cleared; no same-Step refill")
  void applyOnceAndClearBuffer() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem sys = systemWriting(tree, "sys", "n", 1L);
    FieldSchema schema = FieldSchema.of("n", FieldType.INCREMENT);

    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("n", 0L)),
            new EngineSetup(
                tree, List.of(), List.of(sys), schema, EngineDiagnostics.noop()));

    assertTrue(engine.eventBufferEmpty());
    assertEquals(1L, engine.settled().field("n"));

    engine.advance();
    assertTrue(engine.eventBufferEmpty());
    assertEquals(2L, engine.settled().field("n"));
  }

  @Test
  @DisplayName("FR-7: engine has no UI/CLI/product compile deps")
  void noUiCliProductCompileDeps() throws Exception {
    var root = findRepoRoot();
    String enginePom = java.nio.file.Files.readString(root.resolve("engine/pom.xml"));
    assertTrue(!enginePom.contains("<artifactId>ui</artifactId>"));
    assertTrue(!enginePom.contains("<artifactId>cli</artifactId>"));
    assertTrue(!enginePom.contains("<artifactId>product</artifactId>"));
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

  private static SubSystem writeSub(String id, Set<String> ranges, java.util.function.Consumer<SubSystemIo> body) {
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
