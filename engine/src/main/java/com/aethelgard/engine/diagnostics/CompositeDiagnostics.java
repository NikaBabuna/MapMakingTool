/*
 * File: engine/src/main/java/com/aethelgard/engine/diagnostics/CompositeDiagnostics.java
 * Purpose: Fan-out diagnostics (e.g. SLF4J + recording)
 * Audience: Agents / tests
 * Update when: Composition needs change
 */

package com.aethelgard.engine.diagnostics;

import com.aethelgard.engine.events.EngineEvent;
import com.aethelgard.engine.events.EventClaimer;

final class CompositeDiagnostics implements EngineDiagnostics {

  private final EngineDiagnostics first;
  private final EngineDiagnostics second;

  CompositeDiagnostics(EngineDiagnostics first, EngineDiagnostics second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public void stepStarted(int stepIndex) {
    first.stepStarted(stepIndex);
    second.stepStarted(stepIndex);
  }

  @Override
  public void stepSettled(int stepIndex) {
    first.stepSettled(stepIndex);
    second.stepSettled(stepIndex);
  }

  @Override
  public void eventEmitted(EngineEvent event) {
    first.eventEmitted(event);
    second.eventEmitted(event);
  }

  @Override
  public void eventClaimed(EngineEvent event, EventClaimer claimer) {
    first.eventClaimed(event, claimer);
    second.eventClaimed(event, claimer);
  }

  @Override
  public void unmatchedEvent(EngineEvent event) {
    first.unmatchedEvent(event);
    second.unmatchedEvent(event);
  }
}
