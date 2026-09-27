/*
 * File: engine/src/main/java/com/aethelgard/engine/systems/ClaimFinishSnapshot.java
 * Purpose: Observable claim/finish outcome after a completed Step
 * Audience: Agents / callers / tests
 * Update when: Barrier observation shape changes
 */

package com.aethelgard.engine.systems;

import java.util.List;
import java.util.Objects;

/**
 * Settled claim/finish counters for one Step.
 *
 * @param claimCount Systems that claimed ≥1 event
 * @param finishCount Systems that reported finished
 * @param finishedSystemIds System ids that finished (order = finish order)
 */
public record ClaimFinishSnapshot(
    int claimCount, int finishCount, List<String> finishedSystemIds) {

  public ClaimFinishSnapshot {
    finishedSystemIds = List.copyOf(Objects.requireNonNull(finishedSystemIds, "finishedSystemIds"));
  }

  public static ClaimFinishSnapshot empty() {
    return new ClaimFinishSnapshot(0, 0, List.of());
  }

  public boolean isBalanced() {
    return claimCount == finishCount;
  }
}
