/*
 * File: engine/src/test/java/com/aethelgard/engine/pool/EngineRunTest.java
 * Purpose: Proves how a run starts and steps: creation at step 0, advance by n, and determinism
 * Audience: Agents / CI
 * Update when: The engine's step contract changes
 */

package com.aethelgard.engine.pool;

import static com.aethelgard.engine.TestSystems.writing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.system.EngineSystem;
import com.aethelgard.engine.user.InputKind;
import com.aethelgard.engine.user.UserInput;
import com.aethelgard.engine.user.UserView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EngineRunTest {

  /** Proves F-068 FR-1 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Creating a run from a caller's config settles step 0 with one update and an empty buffer")
  void createStartsAtStepZeroFromCallerConfig() {
    Engine engine =
        Engine.create(
            new EngineConfig(40L, List.of(), Map.of("depth", 3L)),
            new EngineSetup(CategoryTree.empty(), List.of(), List.of(),
                FieldSchema.of("depth", FieldType.STATIC), EngineDiagnostics.noop()));

    assertEquals(0, engine.stepIndex());
    assertEquals(1, engine.settled().updateCount());
    assertTrue(engine.eventBufferEmpty());
    // The caller's values are where step 0 started: heartbeat 40 plus one update, depth as given.
    assertEquals(41L, engine.settled().value());
    assertEquals(3L, engine.settled().field("depth"));
  }

  /** Proves F-068 FR-2 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("advance(n) runs exactly n steps, one update each, with a settled snapshot after each")
  void advanceRunsExactlyNSteps() {
    Engine engine = Engine.create(new EngineConfig(0L));
    List<PoolSnapshot> seen = new ArrayList<>();
    seen.add(engine.settled());

    engine.advance();
    seen.add(engine.settled());
    engine.advance(3);
    seen.add(engine.settled());

    assertEquals(4, engine.stepIndex());
    assertEquals(List.of(1, 2, 5), seen.stream().map(PoolSnapshot::updateCount).toList());
    // A snapshot taken earlier is not changed by later steps.
    assertEquals(1L, seen.get(0).value());
    assertEquals(2L, seen.get(1).value());
    assertEquals(5L, seen.get(2).value());

    engine.advance(0);
    assertEquals(4, engine.stepIndex());
    assertEquals(5, engine.settled().updateCount());
  }

  /** Proves F-068 FR-2 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A negative advance is refused and changes nothing")
  void negativeAdvanceIsRefused() {
    Engine engine = Engine.create(new EngineConfig(0L));
    engine.advance(2);
    PoolSnapshot before = engine.settled();

    assertThrows(IllegalArgumentException.class, () -> engine.advance(-1));

    assertEquals(2, engine.stepIndex());
    assertEquals(before, engine.settled());
  }

  /** Proves F-068 FR-3 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The same config, setup and inputs give an equal settled Pool after n steps")
  void sameInputsGiveSamePool() {
    PoolSnapshot a = runWithSystems(false, 5);
    PoolSnapshot b = runWithSystems(false, 5);

    assertEquals(a, b);
    // The run is not trivially constant: the systems and the input moved the fields.
    assertNotEquals(0L, a.field("total"));
  }

  /** Proves F-068 FR-3 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Registering the systems in another order gives the same settled Pool")
  void systemOrderDoesNotChangeThePool() {
    assertEquals(runWithSystems(false, 5), runWithSystems(true, 5));
  }

  /**
   * A run with three systems that all write one field (two of them the same field under two
   * rules), and a held input, advanced {@code steps} times.
   */
  private static PoolSnapshot runWithSystems(boolean reversed, int steps) {
    CategoryTree tree = CategoryTree.of("world");
    List<EngineSystem> systems =
        new ArrayList<>(
            List.of(
                writing(tree, "world", "alpha", "total", 2L),
                writing(tree, "world", "beta", "total", 5L),
                writing(tree, "world", "gamma", "owner", "gamma"),
                writing(tree, "world", "delta", "owner", "delta")));
    if (reversed) {
      java.util.Collections.reverse(systems);
    }
    UserInput input = new UserInput().register(SkeletonPoolCompute.NUDGE_ACTION, InputKind.NON_PERSISTENT);
    input.press(SkeletonPoolCompute.NUDGE_ACTION);
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("total", 0L, "owner", "none")),
            new EngineSetup(
                tree,
                List.of(),
                systems,
                FieldSchema.of(Map.of("total", FieldType.INCREMENT, "owner", FieldType.STATIC)),
                EngineDiagnostics.noop(),
                input,
                UserView.noop()));
    engine.advance(steps);
    return engine.settled();
  }
}
