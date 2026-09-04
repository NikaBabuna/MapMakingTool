/*
 * File: engine/src/main/java/com/aethelgard/engine/diag/Slf4jDiagnostics.java
 * Purpose: Default EngineDiagnostics bridge to SLF4J
 * Audience: Agents / runtime
 * Update when: Log messages or levels change
 */

package com.aethelgard.engine.diag;

import com.aethelgard.engine.event.EngineEvent;
import com.aethelgard.engine.event.EventClaimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Slf4jDiagnostics implements EngineDiagnostics {

  private static final Logger LOG = LoggerFactory.getLogger("com.aethelgard.engine");

  @Override
  public void stepStarted(int stepIndex) {
    LOG.debug("step started index={}", stepIndex);
  }

  @Override
  public void stepSettled(int stepIndex) {
    LOG.debug("step settled index={}", stepIndex);
  }

  @Override
  public void eventEmitted(EngineEvent event) {
    LOG.debug("event emitted category={}", event.category().path());
  }

  @Override
  public void eventClaimed(EngineEvent event, EventClaimer claimer) {
    LOG.debug(
        "event claimed category={} claimer={}", event.category().path(), claimer.id());
  }

  @Override
  public void unmatchedEvent(EngineEvent event) {
    LOG.warn("unmatched event category={}", event.category().path());
  }
}
