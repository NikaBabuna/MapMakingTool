/*
 * File: engine/src/main/java/com/aethelgard/engine/diagnostics/NoopDiagnostics.java
 * Purpose: Silent diagnostics (tests that ignore logging)
 * Audience: Agents / tests
 * Update when: Interface methods change
 */

package com.aethelgard.engine.diagnostics;

import com.aethelgard.engine.events.EngineEvent;
import com.aethelgard.engine.events.EventClaimer;

enum NoopDiagnostics implements EngineDiagnostics {
  INSTANCE;

  @Override
  public void stepStarted(int stepIndex) {}

  @Override
  public void stepSettled(int stepIndex) {}

  @Override
  public void eventEmitted(EngineEvent event) {}

  @Override
  public void eventClaimed(EngineEvent event, EventClaimer claimer) {}

  @Override
  public void unmatchedEvent(EngineEvent event) {}
}
