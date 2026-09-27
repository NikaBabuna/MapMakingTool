/*
 * File: engine/src/main/java/com/aethelgard/engine/diagnostics/EngineDiagnostics.java
 * Purpose: Observability port — Step/event/claim/unmatched
 * Audience: Agents / callers / tests
 * Update when: Diagnostic channels change
 */

package com.aethelgard.engine.diagnostics;

import com.aethelgard.engine.events.EngineEvent;
import com.aethelgard.engine.events.EventClaimer;

/**
 * Side-channel view into the engine. Must not affect Pool determinism.
 *
 * <p>Default production bridge: {@link #slf4j()}. Tests: {@link RecordingDiagnostics}.
 */
public interface EngineDiagnostics {

  void stepStarted(int stepIndex);

  void stepSettled(int stepIndex);

  void eventEmitted(EngineEvent event);

  void eventClaimed(EngineEvent event, EventClaimer claimer);

  /** Unmatched events must be reported — never silently dropped. */
  void unmatchedEvent(EngineEvent event);

  static EngineDiagnostics slf4j() {
    return new Slf4jDiagnostics();
  }

  static EngineDiagnostics noop() {
    return NoopDiagnostics.INSTANCE;
  }

  static EngineDiagnostics compose(EngineDiagnostics first, EngineDiagnostics second) {
    return new CompositeDiagnostics(first, second);
  }
}
