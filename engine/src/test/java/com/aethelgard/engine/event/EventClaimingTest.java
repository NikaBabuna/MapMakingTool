/*
 * File: engine/src/test/java/com/aethelgard/engine/event/EventClaimingTest.java
 * Purpose: F-003 witness — buffer, ancestry claim, unmatched diagnostics, no refill
 * Audience: Agents / CI
 * Update when: F-003 FRs change
 */

package com.aethelgard.engine.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.diag.RecordingDiagnostics;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventClaimingTest {

  @Test
  @DisplayName("FR-1: Pool emits events into shared buffer during Step")
  void poolEmitsEventsIntoBuffer() {
    RecordingDiagnostics diag = new RecordingDiagnostics();
    CategoryTree tree = CategoryTree.of("world/combat");
    EngineConfig config = new EngineConfig(0L, List.of("world/combat"));
    Engine engine =
        Engine.create(config, new EngineSetup(tree, List.of(), diag));

    long emitted =
        diag.records().stream()
            .filter(r -> r.kind() == RecordingDiagnostics.Kind.EVENT_EMITTED)
            .count();
    assertEquals(1, emitted);
    assertEquals(1, engine.lastClaimResult().unmatched().size());
  }

  @Test
  @DisplayName("FR-2: claimer assigned parent claims descendant categories")
  void ancestryClaiming() {
    CategoryTree tree = CategoryTree.of("world", "world/combat", "social/trade");
    EventClaimer world = new EventClaimer("worldSys", tree.get("world"));
    EventClaimer trade = new EventClaimer("tradeSys", tree.get("social/trade"));

    RecordingDiagnostics diag = new RecordingDiagnostics();
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world/combat", "social/trade")),
            new EngineSetup(tree, List.of(world, trade), diag));

    ClaimResult result = engine.lastClaimResult();
    assertEquals(1, result.claimedByClaimer().get(world).size());
    assertEquals("world/combat", result.claimedByClaimer().get(world).getFirst().category().path());
    assertEquals(1, result.claimedByClaimer().get(trade).size());
    assertTrue(result.unmatched().isEmpty());
  }

  @Test
  @DisplayName("FR-2: child claimer does not claim sibling or parent-only events")
  void childDoesNotClaimSibling() {
    CategoryTree tree = CategoryTree.of("world/combat", "world/environment");
    EventClaimer combat = new EventClaimer("combat", tree.get("world/combat"));

    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world/environment")),
            new EngineSetup(tree, List.of(combat), EngineDiagnostics.noop()));

    assertEquals(1, engine.lastClaimResult().unmatched().size());
    assertTrue(engine.lastClaimResult().claimedByClaimer().get(combat).isEmpty());
  }

  @Test
  @DisplayName("FR-3: unmatched events reported via EngineDiagnostics")
  void unmatchedReported() {
    RecordingDiagnostics diag = new RecordingDiagnostics();
    CategoryTree tree = CategoryTree.of("orphan");
    Engine.create(
        new EngineConfig(0L, List.of("orphan")),
        new EngineSetup(tree, List.of(), diag));

    assertEquals(1, diag.unmatchedEvents().size());
    assertEquals("orphan", diag.unmatchedEvents().getFirst().category().path());
  }

  @Test
  @DisplayName("FR-4: event buffer empty after settle; not carried into next Step start")
  void bufferClearedAfterSettle() {
    CategoryTree tree = CategoryTree.of("world");
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world")),
            new EngineSetup(tree, List.of(), EngineDiagnostics.noop()));

    assertTrue(engine.eventBufferEmpty());
    engine.advance();
    assertTrue(engine.eventBufferEmpty());
    // each Step still emitted once (scripted every update)
    assertEquals(1, engine.lastClaimResult().unmatched().size());
  }

  @Test
  @DisplayName("FR-5: claim outcomes observable without full Systems")
  void claimOutcomesObservable() {
    CategoryTree tree = CategoryTree.of("world/combat");
    EventClaimer combat = new EventClaimer("combat", tree.get("world/combat"));
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world/combat")),
            new EngineSetup(tree, List.of(combat), EngineDiagnostics.noop()));

    ClaimResult result = engine.lastClaimResult();
    assertEquals(1, result.claimedByClaimer().get(combat).size());
    assertTrue(result.unmatched().isEmpty());
  }

  @Test
  @DisplayName("FR-6/FR-7: recording sink captures step and claim lifecycle")
  void recordingCapturesLifecycle() {
    RecordingDiagnostics diag = new RecordingDiagnostics();
    CategoryTree tree = CategoryTree.of("world");
    EventClaimer world = new EventClaimer("w", tree.get("world"));
    Engine.create(
        new EngineConfig(0L, List.of("world")),
        new EngineSetup(tree, List.of(world), diag));

    assertTrue(
        diag.records().stream().anyMatch(r -> r.kind() == RecordingDiagnostics.Kind.STEP_STARTED));
    assertTrue(
        diag.records().stream().anyMatch(r -> r.kind() == RecordingDiagnostics.Kind.STEP_SETTLED));
    assertTrue(
        diag.records().stream().anyMatch(r -> r.kind() == RecordingDiagnostics.Kind.EVENT_EMITTED));
    assertTrue(
        diag.records().stream().anyMatch(r -> r.kind() == RecordingDiagnostics.Kind.EVENT_CLAIMED));
  }

  @Test
  @DisplayName("FR-6: engine pom uses slf4j-api compile and not logback")
  void slf4jApiOnlyInEngineCompile() throws Exception {
    var root = findRepoRoot();
    String enginePom = java.nio.file.Files.readString(root.resolve("engine/pom.xml"));
    assertTrue(enginePom.contains("slf4j-api"));
    assertTrue(
        !enginePom.toLowerCase().contains("logback"),
        "engine must not depend on Logback");
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
