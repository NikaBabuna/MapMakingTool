/*
 * File: engine/src/main/java/com/aethelgard/engine/events/EventClaiming.java
 * Purpose: Ancestry dispatch over the event buffer
 * Audience: Agents implementing engine
 * Update when: Claiming algorithm changes
 */

package com.aethelgard.engine.events;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Ancestry dispatch over the event buffer. */
public final class EventClaiming {

  private EventClaiming() {}

  public static ClaimResult claim(List<EngineEvent> events, List<EventClaimer> claimers) {
    Map<EventClaimer, List<EngineEvent>> claimed = new LinkedHashMap<>();
    for (EventClaimer claimer : claimers) {
      claimed.put(claimer, new ArrayList<>());
    }
    List<EngineEvent> unmatched = new ArrayList<>();

    for (EngineEvent event : events) {
      boolean any = false;
      for (EventClaimer claimer : claimers) {
        if (claimer.claims(event)) {
          claimed.get(claimer).add(event);
          any = true;
        }
      }
      if (!any) {
        unmatched.add(event);
      }
    }

    return new ClaimResult(claimed, unmatched);
  }
}
