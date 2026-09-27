/*
 * File: engine/src/main/java/com/aethelgard/engine/events/EventClaimer.java
 * Purpose: Stub claimer — category ancestry only
 * Audience: Agents / tests
 * Update when: Claimer contract expands toward Systems
 */

package com.aethelgard.engine.events;

import java.util.Objects;

/**
 * Minimal claimer: assigned one category; claims that category and all descendants.
 *
 * <p>Full Systems wrap a claimer via {@link com.aethelgard.engine.systems.EngineSystem#claimer()}.
 */
public final class EventClaimer {

  private final String id;
  private final Category assigned;

  public EventClaimer(String id, Category assigned) {
    this.id = Objects.requireNonNull(id, "id");
    this.assigned = Objects.requireNonNull(assigned, "assigned");
  }

  public String id() {
    return id;
  }

  public Category assigned() {
    return assigned;
  }

  public boolean claims(EngineEvent event) {
    return event.category().isSelfOrDescendantOf(assigned);
  }

  @Override
  public String toString() {
    return id + "@" + assigned.path();
  }
}
