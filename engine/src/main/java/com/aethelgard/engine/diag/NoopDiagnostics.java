/*
 * File: engine/src/main/java/com/aethelgard/engine/diag/NoopDiagnostics.java
 * Purpose: Silent diagnostics (tests that ignore logging)
 * Audience: Agents / tests
 * Update when: Interface methods change
 */

package com.aethelgard.engine.diag;

import com.aethelgard.engine.event.EngineEvent;
import com.aethelgard.engine.event.EventClaimer;

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
