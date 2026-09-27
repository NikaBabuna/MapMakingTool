/*
 * File: engine/src/test/java/com/aethelgard/engine/user/UserLayerTest.java
 * Purpose: Proves how a person's input reaches a step and how a view sees the result
 * Audience: Agents / CI
 * Update when: UserInput, InputView, or UserView changes
 */

package com.aethelgard.engine.user;

import static com.aethelgard.engine.TestSystems.writing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diagnostics.EngineDiagnostics;
import com.aethelgard.engine.events.CategoryTree;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.pool.Pool;
import com.aethelgard.engine.pool.PoolCompute;
import com.aethelgard.engine.pool.PoolSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserLayerTest {

  /** Proves F-068 FR-15 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A persistent press stays latched after release until one step consumes it")
  void persistentInputLatchesUntilConsumed() {
    UserInput input = new UserInput().register(Pool.NUDGE_ACTION, InputKind.PERSISTENT);
    Engine engine = Engine.create(new EngineConfig(0L), setup(input, UserView.noop()));
    assertEquals(1L, engine.settled().value());

    input.press(Pool.NUDGE_ACTION);
    input.release(Pool.NUDGE_ACTION);
    assertTrue(input.isLatched(Pool.NUDGE_ACTION));

    engine.advance();
    assertEquals(102L, engine.settled().value(), "the latched press nudged this step");
    assertFalse(input.isLatched(Pool.NUDGE_ACTION), "the step consumed it");

    engine.advance();
    assertEquals(103L, engine.settled().value(), "a consumed press nudges only once");
  }

  /** Proves F-068 FR-15 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A held input counts only while it is held when the step starts")
  void heldInputCountsOnlyWhileHeld() {
    UserInput input = new UserInput().register(Pool.NUDGE_ACTION, InputKind.NON_PERSISTENT);
    Engine engine = Engine.create(new EngineConfig(0L), setup(input, UserView.noop()));

    input.press(Pool.NUDGE_ACTION);
    input.release(Pool.NUDGE_ACTION);
    engine.advance();
    assertEquals(2L, engine.settled().value(), "released before the step: no nudge");

    input.press(Pool.NUDGE_ACTION);
    engine.advance(2);
    assertEquals(204L, engine.settled().value(), "held through two steps: nudged twice");
  }

  /** Proves F-068 FR-15 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The Input View is sampled once per step, before the compute")
  void inputViewIsSampledOncePerStep() {
    UserInput input = new UserInput().register("go", InputKind.NON_PERSISTENT);
    List<Boolean> seenByCompute = new ArrayList<>();
    PoolCompute compute = context -> seenByCompute.add(context.inputView().isActive("go"));
    Engine engine =
        Engine.create(
            new EngineConfig(0L),
            new EngineSetup(CategoryTree.empty(), List.of(), List.of(), FieldSchema.empty(),
                EngineDiagnostics.noop(), input, UserView.noop(), compute, null));

    input.press("go");
    engine.advance();
    input.release("go");
    engine.advance();

    assertEquals(List.of(false, true, false), seenByCompute);
    assertFalse(engine.lastInputView().isActive("go"));
  }

  /** Proves F-068 FR-16 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The User View sees only settled snapshots, once per step")
  void userViewSeesOnlySettledSnapshots() {
    CategoryTree tree = CategoryTree.of("world");
    List<PoolSnapshot> frames = new ArrayList<>();
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("mark", 0L)),
            new EngineSetup(
                tree, List.of(), List.of(writing(tree, "world", "sys", "mark", 42L)),
                FieldSchema.of("mark", FieldType.STATIC), EngineDiagnostics.noop(),
                new UserInput(), frames::add));
    engine.advance(2);

    assertEquals(3, frames.size());
    for (PoolSnapshot frame : frames) {
      assertEquals(42L, frame.field("mark"), "every frame is after merge");
    }
    assertEquals(engine.settled(), frames.getLast());
  }

  private static EngineSetup setup(UserInput input, UserView view) {
    return new EngineSetup(
        CategoryTree.empty(), List.of(), List.of(), FieldSchema.empty(), EngineDiagnostics.noop(), input, view);
  }
}
