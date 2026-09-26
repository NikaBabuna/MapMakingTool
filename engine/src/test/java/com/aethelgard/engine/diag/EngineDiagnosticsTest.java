/*
 * File: engine/src/test/java/com/aethelgard/engine/diag/EngineDiagnosticsTest.java
 * Purpose: Proves what the engine's diagnostics report of a step, and that they never change the Pool
 * Audience: Agents / CI
 * Update when: EngineDiagnostics or its recording binding changes
 */

package com.aethelgard.engine.diag;

import static com.aethelgard.engine.TestSystems.writing;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aethelgard.engine.diag.RecordingDiagnostics.Kind;
import com.aethelgard.engine.diag.RecordingDiagnostics.Record;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.event.EventClaimer;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EngineDiagnosticsTest {

  private static final CategoryTree TREE = CategoryTree.of("world", "world/sea", "world/land");

  /** Proves F-068 FR-17 and FR-7 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A recording binding sees each step start, every event, its claim or its miss, and the settle")
  void recordingSinkSeesTheStepLifecycle() {
    RecordingDiagnostics recording = new RecordingDiagnostics();
    EventClaimer sea = new EventClaimer("sea", TREE.get("world/sea"));
    Engine engine = run(recording, List.of(sea));
    recording.clear();

    engine.advance();

    List<Kind> kinds = recording.records().stream().map(Record::kind).toList();
    assertEquals(Kind.STEP_STARTED, kinds.getFirst());
    assertEquals(Kind.STEP_SETTLED, kinds.getLast());
    assertEquals(1, recording.records().getFirst().stepIndex());
    assertEquals(1, recording.records().getLast().stepIndex());
    assertEquals(2, count(kinds, Kind.EVENT_EMITTED));
    // world/sea is claimed by the claimer and by the system; world/land by nobody.
    assertEquals(List.of("world/sea", "world/sea"),
        recording.records().stream().filter(r -> r.kind() == Kind.EVENT_CLAIMED)
            .map(r -> r.event().category().path()).toList());
    assertEquals(List.of("world/land"),
        recording.unmatchedEvents().stream().map(e -> e.category().path()).toList());
  }

  /** Proves F-068 FR-17 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("No diagnostics binding changes the Pool")
  void diagnosticsDoNotChangeThePool() {
    Engine silent = run(EngineDiagnostics.noop(), List.of());
    Engine recorded = run(new RecordingDiagnostics(), List.of());
    Engine logged = run(EngineDiagnostics.slf4j(), List.of());
    for (Engine engine : List.of(silent, recorded, logged)) {
      engine.advance(3);
    }

    assertEquals(silent.settled(), recorded.settled());
    assertEquals(silent.settled(), logged.settled());
  }

  private static Engine run(EngineDiagnostics diagnostics, List<EventClaimer> claimers) {
    return Engine.create(
        new EngineConfig(0L, List.of("world/sea", "world/land"), Map.of("n", 0L)),
        new EngineSetup(
            TREE, claimers, List.of(writing(TREE, "world/sea", "tides", "n", 1L)),
            FieldSchema.of("n", FieldType.INCREMENT), diagnostics));
  }

  private static long count(List<Kind> kinds, Kind kind) {
    return kinds.stream().filter(k -> k == kind).count();
  }
}
