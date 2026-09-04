/*
 * File: engine/src/main/java/com/aethelgard/engine/event/ClaimResult.java
 * Purpose: Observable per-Step claim / unmatched outcomes
 * Audience: Agents / tests
 * Update when: Claim observation shape changes
 */

package com.aethelgard.engine.event;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Result of ancestry claiming for one Step.
 *
 * @param claimedByClaimer events each claimer claimed (an event may appear under multiple claimers)
 * @param unmatched events no claimer claimed
 */
public record ClaimResult(
    Map<EventClaimer, List<EngineEvent>> claimedByClaimer, List<EngineEvent> unmatched) {

  public ClaimResult {
    Objects.requireNonNull(claimedByClaimer, "claimedByClaimer");
    Objects.requireNonNull(unmatched, "unmatched");
    claimedByClaimer = Map.copyOf(claimedByClaimer);
    unmatched = List.copyOf(unmatched);
  }

  public static ClaimResult empty() {
    return new ClaimResult(Map.of(), List.of());
  }
}
