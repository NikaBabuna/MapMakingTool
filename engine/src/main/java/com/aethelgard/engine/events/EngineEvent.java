/*
 * File: engine/src/main/java/com/aethelgard/engine/events/EngineEvent.java
 * Purpose: Notification written to the shared event buffer
 * Audience: Agents implementing engine
 * Update when: Event payload shape changes
 */

package com.aethelgard.engine.events;

import java.util.Objects;

/** Pool-emitted notification; not addressed to a specific claimer. */
public record EngineEvent(Category category) {

  public EngineEvent {
    Objects.requireNonNull(category, "category");
  }
}
