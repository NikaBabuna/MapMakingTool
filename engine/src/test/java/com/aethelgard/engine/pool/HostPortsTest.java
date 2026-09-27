/*
 * File: engine/src/test/java/com/aethelgard/engine/pool/HostPortsTest.java
 * Purpose: Proves the host ports: the default and a custom Pool compute, and the default and a custom emission policy
 * Audience: Agents / CI
 * Update when: PoolCompute, EventEmissionPolicy, or their defaults change
 */

package com.aethelgard.engine.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diagnostics.EngineDiagnostics;
import com.aethelgard.engine.events.CategoryTree;
import com.aethelgard.engine.events.EngineEvent;
import com.aethelgard.engine.events.EventClaimer;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.user.InputKind;
import com.aethelgard.engine.user.UserInput;
import com.aethelgard.engine.user.UserView;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HostPortsTest {

  private static final CategoryTree TREE =
      CategoryTree.of("world", "world/go", "world/rain", "world/wind", "world/heat", "world/poke");

  /** Proves F-068 FR-4 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The default compute adds 1 to the heartbeat each step, and 100 more while nudge is active")
  void defaultComputeKeepsHeartbeatAndNudge() {
    UserInput input = new UserInput().register(Pool.NUDGE_ACTION, InputKind.NON_PERSISTENT);
    Engine engine = Engine.create(new EngineConfig(10L), setup(input, null, null, List.of()));
    assertEquals(11L, engine.settled().value());

    input.press(Pool.NUDGE_ACTION);
    engine.advance();
    assertEquals(112L, engine.settled().value());

    input.release(Pool.NUDGE_ACTION);
    engine.advance();
    assertEquals(113L, engine.settled().value());
  }

  /** Proves F-068 FR-4 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A custom compute replaces the heartbeat, writes declared fields, reads input, and emits")
  void customComputeReplacesTheDefault() {
    PoolCompute compute =
        context -> {
          context.setValue(7L);
          context.setField("count", context.fieldOrZero("count") + 1);
          if (context.inputView().isActive("go")) {
            context.emitPath("world/go");
          }
        };
    UserInput input = new UserInput().register("go", InputKind.NON_PERSISTENT);
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of(), Map.of("count", 0L)),
            setup(input, compute, null, List.of(new EventClaimer("listener", TREE.get("world")))));

    assertEquals(7L, engine.settled().value(), "the custom compute, not the heartbeat, set the value");
    assertEquals(1L, engine.settled().field("count"));
    assertTrue(engine.lastClaimResult().unmatched().isEmpty());
    assertTrue(claimedPaths(engine).isEmpty(), "no input, no event");

    input.press("go");
    engine.advance();
    assertEquals(7L, engine.settled().value());
    assertEquals(2L, engine.settled().field("count"));
    assertEquals(List.of("world/go"), claimedPaths(engine));
  }

  /** Proves F-068 FR-5 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The default emission policy emits the scripted paths every step")
  void defaultPolicyEmitsScriptedPaths() {
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world/rain", "world/wind")),
            setup(new UserInput(), null, null, List.of(new EventClaimer("all", TREE.get("world")))));

    assertEquals(List.of("world/rain", "world/wind"), claimedPaths(engine));
    engine.advance();
    assertEquals(List.of("world/rain", "world/wind"), claimedPaths(engine));
  }

  /** Proves F-068 FR-5 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A custom emission policy emits from field state and from the Input View")
  void customPolicyEmitsFromFieldsAndInput() {
    EventEmissionPolicy policy =
        context -> {
          if (context.fieldOrZero("temp") > 5) {
            context.emitPath("world/heat");
          }
          if (context.inputView().isActive("poke")) {
            context.emitPath("world/poke");
          }
        };
    UserInput input = new UserInput().register("poke", InputKind.NON_PERSISTENT);
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world/rain"), Map.of("temp", 9L)),
            setup(input, null, policy, List.of(new EventClaimer("all", TREE.get("world")))));

    // The scripted path (world/rain) is not emitted: the custom policy alone decides.
    assertEquals(List.of("world/heat"), claimedPaths(engine));

    input.press("poke");
    engine.advance();
    assertEquals(List.of("world/heat", "world/poke"), claimedPaths(engine));
  }

  private static EngineSetup setup(
      UserInput input, PoolCompute compute, EventEmissionPolicy policy, List<EventClaimer> claimers) {
    return new EngineSetup(
        TREE,
        claimers,
        List.of(),
        FieldSchema.of(Map.of("count", FieldType.STATIC, "temp", FieldType.STATIC)),
        EngineDiagnostics.noop(),
        input,
        UserView.noop(),
        compute,
        policy);
  }

  private static List<String> claimedPaths(Engine engine) {
    return engine.lastClaimResult().claimedByClaimer().values().stream()
        .flatMap(List::stream)
        .map(EngineEvent::category)
        .map(c -> c.path())
        .sorted()
        .toList();
  }
}
