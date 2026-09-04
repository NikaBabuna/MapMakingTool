/*
 * File: engine/src/main/java/com/aethelgard/engine/event/EventBuffer.java
 * Purpose: Shared per-Step event buffer
 * Audience: Agents implementing engine
 * Update when: Buffer lifecycle changes
 */

package com.aethelgard.engine.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Filled once during Pool compute; cleared when the Step settles. */
public final class EventBuffer {

  private final List<EngineEvent> events = new ArrayList<>();

  public void add(EngineEvent event) {
    events.add(event);
  }

  public List<EngineEvent> events() {
    return Collections.unmodifiableList(events);
  }

  public boolean isEmpty() {
    return events.isEmpty();
  }

  public void clear() {
    events.clear();
  }
}
