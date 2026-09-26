/*
 * File: engine/src/test/java/com/aethelgard/engine/event/EventClaimingTest.java
 * Purpose: Proves how events are claimed: by category ancestry, unmatched ones reported, and never refilled within a step
 * Audience: Agents / CI
 * Update when: Claiming, the event buffer, or unmatched reporting changes
 */

package com.aethelgard.engine.event;

import static com.aethelgard.engine.TestSystems.writing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.diag.RecordingDiagnostics;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.pool.PoolCompute;
import com.aethelgard.engine.user.UserInput;
import com.aethelgard.engine.user.UserView;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventClaimingTest {

  private static final CategoryTree TREE =
      CategoryTree.of("world", "world/sea", "world/sea/tide", "world/land");

  private static final List<String> ALL = List.of("world", "world/sea", "world/sea/tide", "world/land");

  /** Proves F-068 FR-6 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A claimer of a category takes that category and every descendant")
  void claimerTakesCategoryAndDescendants() {
    EventClaimer sea = new EventClaimer("sea", TREE.get("world/sea"));
    Engine engine = run(List.of(sea), EngineDiagnostics.noop());

    assertEquals(List.of("world/sea", "world/sea/tide"), paths(engine.lastClaimResult().claimedByClaimer().get(sea)));
  }

  /** Proves F-068 FR-6 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A claimer never takes a sibling's or a parent's event")
  void claimerLeavesSiblingsAndParents() {
    EventClaimer tide = new EventClaimer("tide", TREE.get("world/sea/tide"));
    EventClaimer land = new EventClaimer("land", TREE.get("world/land"));
    Engine engine = run(List.of(tide, land), EngineDiagnostics.noop());

    Map<EventClaimer, List<EngineEvent>> claimed = engine.lastClaimResult().claimedByClaimer();
    assertEquals(List.of("world/sea/tide"), paths(claimed.get(tide)));
    assertEquals(List.of("world/land"), paths(claimed.get(land)));
    assertEquals(List.of("world", "world/sea"), paths(engine.lastClaimResult().unmatched()));
  }

  /** Proves F-068 FR-7 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("An event no claimer takes is reported as unmatched, not dropped")
  void unclaimedEventIsReportedUnmatched() {
    RecordingDiagnostics recording = new RecordingDiagnostics();
    EventClaimer sea = new EventClaimer("sea", TREE.get("world/sea"));
    Engine engine = run(List.of(sea), recording);

    List<String> unmatched = paths(engine.lastClaimResult().unmatched());
    assertEquals(List.of("world", "world/land"), unmatched);
    assertEquals(unmatched, paths(recording.unmatchedEvents()));
    // Every emitted event is either claimed or unmatched.
    int claimed = engine.lastClaimResult().claimedByClaimer().values().stream().mapToInt(List::size).sum();
    assertEquals(ALL.size(), claimed + unmatched.size());
  }

  /** Proves F-068 FR-8 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The buffer empties every step, and a system's output becomes an event only in the next step")
  void bufferEmptiesEveryStepAndOutputFiresNextStep() {
    CategoryTree tree = CategoryTree.of("world", "world/start", "world/echo");
    // The compute emits start on the first update (step 0), and echo whenever the settled pulse is 1.
    PoolCompute compute =
        context -> {
          if (context.updateCount() == 1) {
            context.emitPath("world/start");
          }
          if (context.fieldOrZero("pulse") == 1L) {
            context.emitPath("world/echo");
          }
        };
    EventClaimer echo = new EventClaimer("echo", tree.get("world/echo"));
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of(), Map.of("pulse", 0L)),
            new EngineSetup(
                tree,
                List.of(echo),
                List.of(writing(tree, "world/start", "starter", "pulse", 1L)),
                FieldSchema.of("pulse", FieldType.STATIC),
                EngineDiagnostics.noop(),
                new UserInput(),
                UserView.noop(),
                compute,
                null));

    assertTrue(engine.eventBufferEmpty());
    assertEquals(1L, engine.settled().field("pulse"), "the system wrote the pulse in step 0");
    assertTrue(engine.lastClaimResult().claimedByClaimer().getOrDefault(echo, List.of()).isEmpty(),
        "the pulse written in step 0 does not fire an event in step 0");

    engine.advance();
    assertTrue(engine.eventBufferEmpty());
    assertEquals(List.of("world/echo"), paths(engine.lastClaimResult().claimedByClaimer().get(echo)));
  }

  /** A run whose compute emits one event on each of the four categories, every step. */
  private static Engine run(List<EventClaimer> claimers, EngineDiagnostics diagnostics) {
    return Engine.create(
        new EngineConfig(0L, ALL),
        new EngineSetup(TREE, claimers, diagnostics));
  }

  private static List<String> paths(List<EngineEvent> events) {
    return events == null ? List.of() : events.stream().map(e -> e.category().path()).sorted().toList();
  }
}
