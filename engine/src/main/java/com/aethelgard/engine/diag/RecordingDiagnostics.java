/*
 * File: engine/src/main/java/com/aethelgard/engine/diag/RecordingDiagnostics.java
 * Purpose: Capturable diagnostics sink for tests (ADR-006 witness)
 * Audience: Tests
 * Update when: Recorded event kinds change
 */

package com.aethelgard.engine.diag;

import com.aethelgard.engine.event.EngineEvent;
import com.aethelgard.engine.event.EventClaimer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** In-memory diagnostics for assertions without scraping stdout. */
public final class RecordingDiagnostics implements EngineDiagnostics {

  public enum Kind {
    STEP_STARTED,
    STEP_SETTLED,
    EVENT_EMITTED,
    EVENT_CLAIMED,
    UNMATCHED
  }

  public record Record(Kind kind, int stepIndex, EngineEvent event, EventClaimer claimer) {}

  private final List<Record> records = new ArrayList<>();

  @Override
  public void stepStarted(int stepIndex) {
    records.add(new Record(Kind.STEP_STARTED, stepIndex, null, null));
  }

  @Override
  public void stepSettled(int stepIndex) {
    records.add(new Record(Kind.STEP_SETTLED, stepIndex, null, null));
  }

  @Override
  public void eventEmitted(EngineEvent event) {
    records.add(new Record(Kind.EVENT_EMITTED, -1, event, null));
  }

  @Override
  public void eventClaimed(EngineEvent event, EventClaimer claimer) {
    records.add(new Record(Kind.EVENT_CLAIMED, -1, event, claimer));
  }

  @Override
  public void unmatchedEvent(EngineEvent event) {
    records.add(new Record(Kind.UNMATCHED, -1, event, null));
  }

  public List<Record> records() {
    return Collections.unmodifiableList(records);
  }

  public List<EngineEvent> unmatchedEvents() {
    return records.stream()
        .filter(r -> r.kind() == Kind.UNMATCHED)
        .map(Record::event)
        .toList();
  }

  public void clear() {
    records.clear();
  }
}
