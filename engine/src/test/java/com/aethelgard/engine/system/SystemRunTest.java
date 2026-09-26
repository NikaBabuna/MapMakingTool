/*
 * File: engine/src/test/java/com/aethelgard/engine/system/SystemRunTest.java
 * Purpose: Proves how systems run: only when they claim, on one shared snapshot, sub-systems in a defined order, counted by the barrier
 * Audience: Agents / CI
 * Update when: EngineSystem, sub-system ordering, or the claim/finish barrier changes
 */

package com.aethelgard.engine.system;

import static com.aethelgard.engine.TestSystems.sub;
import static com.aethelgard.engine.TestSystems.system;
import static com.aethelgard.engine.TestSystems.writing;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SystemRunTest {

  private static final CategoryTree TREE = CategoryTree.of("world", "world/quiet");

  /** Proves F-068 FR-9 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A claiming system runs its sub-systems and its output reaches merge")
  void claimingSystemRunsItsSubSystems() {
    EngineSystem counter =
        system(TREE, "world", "counter", null,
            sub("copy", Set.of("score"), io -> io.write("score", io.poolValue())),
            sub("flag", Set.of("seen"), io -> io.write("seen", "yes")));
    Engine engine = engine(List.of(counter), Map.of("score", 0L, "seen", "no"));

    // Step 0: the heartbeat became 11, and the sub-system copied it.
    assertEquals(11L, engine.settled().field("score"));
    assertEquals("yes", engine.settled().field("seen"));
    assertEquals("counter", engine.lastStepOutput().asMap().get("score").getFirst().systemId());
  }

  /** Proves F-068 FR-9 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A system with no claimed event does not run")
  void unclaimedSystemDoesNotRun() {
    EngineSystem idle = writing(TREE, "world/quiet", "idle", "score", 99L);
    Engine engine = engine(List.of(idle), Map.of("score", 0L));
    engine.advance(2);

    assertEquals(0L, engine.settled().field("score"));
    assertTrue(engine.lastClaimFinish().finishedSystemIds().isEmpty());
  }

  /** Proves F-068 FR-10 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Systems in one step read one shared snapshot and never another's output from that step")
  void systemsShareOneSnapshot() {
    EngineSystem writer = writing(TREE, "world", "writer", "mark", 99L);
    EngineSystem reader =
        system(TREE, "world", "reader", null,
            sub("read", Set.of("seen"), io -> io.write("seen", io.readPool("mark"))));
    Engine engine = engine(List.of(writer, reader), Map.of("mark", 0L, "seen", -1L));

    assertEquals(99L, engine.settled().field("mark"));
    assertEquals(0L, engine.settled().field("seen"), "the reader saw the mark from before this step");
    engine.advance();
    assertEquals(99L, engine.settled().field("seen"), "the next step's snapshot holds the merged mark");
  }

  /** Proves F-068 FR-11 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Sub-systems with disjoint write ranges give the same output in any order")
  void disjointSubSystemsRunInAnyOrder() {
    SubSystem left = sub("left", Set.of("x"), io -> io.write("x", io.poolValue() + 1));
    SubSystem right = sub("right", Set.of("y"), io -> io.write("y", io.poolValue() + 2));
    PoolSnapshot snapshot = new PoolSnapshot(5L, 1, Map.of("x", 0L, "y", 0L));

    Map<String, Object> forward = system(TREE, "world", "s", null, left, right).run(snapshot);
    Map<String, Object> reverse = system(TREE, "world", "s", null, right, left).run(snapshot);

    assertEquals(Map.of("x", 6L, "y", 7L), forward);
    assertEquals(forward, reverse);
  }

  /** Proves F-068 FR-11 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Overlapping sub-systems run in the order the resolver gives")
  void overlappingSubSystemsFollowTheResolver() {
    SubSystem first = sub("first", Set.of("slot"), io -> io.write("slot", 1L));
    SubSystem second =
        sub("second", Set.of("slot"), io -> {
          Object staged = io.readStaging("slot");
          io.write("slot", (staged == null ? 0L : (Long) staged) * 10 + 2);
        });
    PoolSnapshot snapshot = new PoolSnapshot(0L, 1, Map.of("slot", 0L));

    Map<String, Object> firstThenSecond =
        system(TREE, "world", "s", conflict -> List.of(first, second), first, second).run(snapshot);
    Map<String, Object> secondThenFirst =
        system(TREE, "world", "s", conflict -> List.of(second, first), first, second).run(snapshot);

    assertEquals(12L, firstThenSecond.get("slot"), "second read first's staged 1");
    assertEquals(1L, secondThenFirst.get("slot"), "first ran last");
  }

  /** Proves F-068 FR-11 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Overlapping sub-systems without a resolver are refused")
  void overlapWithoutResolverIsRefused() {
    EngineSystem clash =
        system(TREE, "world", "s", null,
            sub("a", Set.of("slot"), io -> io.write("slot", 1L)),
            sub("b", Set.of("slot"), io -> io.write("slot", 2L)));

    assertThrows(IllegalStateException.class, () -> clash.run(new PoolSnapshot(0L, 1, Map.of("slot", 0L))));
  }

  /** Proves F-068 FR-12 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The barrier counts each claiming system once, and stub claimers not at all")
  void barrierCountsEachClaimingSystemOnce() {
    EngineSystem three =
        system(TREE, "world", "three", null,
            sub("a", Set.of("a"), io -> io.write("a", 1L)),
            sub("b", Set.of("b"), io -> io.write("b", 1L)),
            sub("c", Set.of("c"), io -> io.write("c", 1L)));
    EngineSystem one = writing(TREE, "world", "one", "d", 1L);
    EventClaimer stub = new EventClaimer("stub", TREE.get("world"));
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("a", 0L, "b", 0L, "c", 0L, "d", 0L)),
            new EngineSetup(
                TREE, List.of(stub), List.of(three, one),
                FieldSchema.of(Map.of("a", FieldType.STATIC, "b", FieldType.STATIC,
                    "c", FieldType.STATIC, "d", FieldType.STATIC)),
                EngineDiagnostics.noop()));

    ClaimFinishSnapshot barrier = engine.lastClaimFinish();
    assertEquals(2, barrier.claimCount());
    assertEquals(2, barrier.finishCount());
    assertEquals(List.of("three", "one"), barrier.finishedSystemIds());
    assertTrue(engine.lastClaimResult().claimedByClaimer().containsKey(stub), "the stub did claim");
  }

  /** Proves F-068 FR-12 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("An unbalanced claim/finish count is rejected")
  void unbalancedBarrierIsRejected() {
    ClaimFinishBarrier barrier = new ClaimFinishBarrier();
    barrier.onClaimed("a");
    barrier.onClaimed("b");
    barrier.onFinished("a");

    assertThrows(IllegalStateException.class, barrier::requireBalanced);
    barrier.onFinished("b");
    assertDoesNotThrow(barrier::requireBalanced);
  }

  private static Engine engine(List<EngineSystem> systems, Map<String, Object> fields) {
    Map<String, FieldType> types = new java.util.HashMap<>();
    fields.keySet().forEach(k -> types.put(k, FieldType.STATIC));
    return Engine.create(
        new EngineConfig(10L, List.of("world"), fields),
        new EngineSetup(TREE, List.of(), systems, FieldSchema.of(types), EngineDiagnostics.noop()));
  }
}
