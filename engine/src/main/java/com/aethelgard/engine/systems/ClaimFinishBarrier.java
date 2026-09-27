/*
 * File: engine/src/main/java/com/aethelgard/engine/systems/ClaimFinishBarrier.java
 * Purpose: Per-Step claim/finish counters; gate merge until balanced
 * Audience: Engine loop / tests
 * Update when: Barrier semantics change
 */

package com.aethelgard.engine.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Tracks System claim/finish for one Step. Merge may proceed only when {@link #requireBalanced()}
 * succeeds ({@code claimCount == finishCount}).
 *
 * <p>Non-finishing Systems are not supported — claiming Systems always finish
 * synchronously in the engine loop.
 */
public final class ClaimFinishBarrier {

  private int claimCount;
  private int finishCount;
  private final List<String> finishedSystemIds = new ArrayList<>();

  /** Records that a System claimed ≥1 event this Step. */
  public void onClaimed(String systemId) {
    Objects.requireNonNull(systemId, "systemId");
    claimCount++;
  }

  /** Records that a claiming System finished (exactly once per claim expected). */
  public void onFinished(String systemId) {
    Objects.requireNonNull(systemId, "systemId");
    finishCount++;
    finishedSystemIds.add(systemId);
  }

  /**
   * Gate before typed merge / Pool apply.
   *
   * @throws IllegalStateException if claim and finish counts differ
   */
  public void requireBalanced() {
    if (claimCount != finishCount) {
      throw new IllegalStateException(
          "claim/finish barrier unbalanced: claimCount="
              + claimCount
              + " finishCount="
              + finishCount
              + " finished="
              + finishedSystemIds);
    }
  }

  public int claimCount() {
    return claimCount;
  }

  public int finishCount() {
    return finishCount;
  }

  public ClaimFinishSnapshot snapshot() {
    return new ClaimFinishSnapshot(claimCount, finishCount, List.copyOf(finishedSystemIds));
  }
}
