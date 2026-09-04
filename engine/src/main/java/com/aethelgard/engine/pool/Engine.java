/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/Engine.java
 * Purpose: Step loop driver — config bootstrap, advance N Steps, settled snapshots
 * Audience: Agents / callers (tests, later CLI/UI)
 * Update when: Step orchestration API changes
 */

package com.aethelgard.engine.pool;

import java.util.Objects;

/**
 * Drives discrete Steps over a {@link Pool}.
 *
 * <p><b>Step index rule (F-002):</b> {@link #stepIndex()} is the 0-based index of the last
 * completed Step. {@link #create} completes Step 0 → index {@code 0}. Each {@link #advance()}
 * completes the next Step and increments the index by 1. After create + {@code advance(n)},
 * {@code stepIndex() == n}.
 */
public final class Engine {

  private final Pool pool;
  private int lastCompletedStepIndex = -1;

  private Engine(Pool pool) {
    this.pool = pool;
  }

  /**
   * Creates a run from {@code config}, runs Step 0 (Pool {@code update} once), and returns a
   * settled engine at {@code stepIndex() == 0}.
   */
  public static Engine create(EngineConfig config) {
    Objects.requireNonNull(config, "config");
    Engine engine = new Engine(new Pool(config));
    engine.runStep();
    return engine;
  }

  /** Completes one more Step (Pool {@code update} once, then settle). */
  public void advance() {
    runStep();
  }

  /**
   * Completes {@code n} additional Steps.
   *
   * @param n number of Steps to advance; must be {@code >= 0}
   */
  public void advance(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be >= 0, was " + n);
    }
    for (int i = 0; i < n; i++) {
      runStep();
    }
  }

  /** Index of the last completed Step (0-based). */
  public int stepIndex() {
    return lastCompletedStepIndex;
  }

  /** Settled Pool state after the last completed Step. */
  public PoolSnapshot settled() {
    return pool.snapshot();
  }

  private void runStep() {
    pool.update();
    // F-003+: event buffer, Systems, merge, View
    lastCompletedStepIndex++;
  }
}
