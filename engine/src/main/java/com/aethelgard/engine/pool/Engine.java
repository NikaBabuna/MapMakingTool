/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/Engine.java
 * Purpose: Step loop driver — config bootstrap, events, claiming, settled snapshots
 * Audience: Agents / callers (tests, later CLI/UI)
 * Update when: Step orchestration API changes
 */

package com.aethelgard.engine.pool;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.Category;
import com.aethelgard.engine.event.ClaimResult;
import com.aethelgard.engine.event.EngineEvent;
import com.aethelgard.engine.event.EventBuffer;
import com.aethelgard.engine.event.EventClaimer;
import com.aethelgard.engine.event.EventClaiming;
import java.util.List;
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
  private final EventBuffer eventBuffer = new EventBuffer();
  private final List<Category> emissions;
  private final List<EventClaimer> claimers;
  private final EngineDiagnostics diagnostics;
  private int lastCompletedStepIndex = -1;
  private ClaimResult lastClaimResult = ClaimResult.empty();

  private Engine(
      Pool pool,
      List<Category> emissions,
      List<EventClaimer> claimers,
      EngineDiagnostics diagnostics) {
    this.pool = pool;
    this.emissions = List.copyOf(emissions);
    this.claimers = List.copyOf(claimers);
    this.diagnostics = diagnostics;
  }

  /**
   * Creates a run from {@code config}, runs Step 0, returns settled engine at {@code stepIndex()
   * == 0}. Uses {@link EngineSetup#defaults()}.
   */
  public static Engine create(EngineConfig config) {
    return create(config, EngineSetup.defaults());
  }

  /**
   * Creates a run with explicit category tree, claimers, and diagnostics.
   *
   * <p>Emission paths in {@code config} are resolved against {@code setup.categoryTree()}.
   */
  public static Engine create(EngineConfig config, EngineSetup setup) {
    Objects.requireNonNull(config, "config");
    Objects.requireNonNull(setup, "setup");
    List<Category> emissions =
        setup.categoryTree().resolveAll(config.emitCategoryPathsEachUpdate());
    Engine engine =
        new Engine(new Pool(config), emissions, setup.claimers(), setup.diagnostics());
    engine.runStep();
    return engine;
  }

  /** Completes one more Step. */
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

  /** Claim / unmatched outcomes from the last completed Step. */
  public ClaimResult lastClaimResult() {
    return lastClaimResult;
  }

  /** True when the shared event buffer is empty (expected after settle). */
  public boolean eventBufferEmpty() {
    return eventBuffer.isEmpty();
  }

  private void runStep() {
    int stepIndex = lastCompletedStepIndex + 1;
    diagnostics.stepStarted(stepIndex);

    if (!eventBuffer.isEmpty()) {
      throw new IllegalStateException("event buffer not empty at Step start");
    }

    pool.update(eventBuffer, emissions, diagnostics);

    ClaimResult result = EventClaiming.claim(eventBuffer.events(), claimers);
    for (var entry : result.claimedByClaimer().entrySet()) {
      EventClaimer claimer = entry.getKey();
      for (EngineEvent event : entry.getValue()) {
        diagnostics.eventClaimed(event, claimer);
      }
    }
    for (EngineEvent event : result.unmatched()) {
      diagnostics.unmatchedEvent(event);
    }

    eventBuffer.clear();
    lastClaimResult = result;
    diagnostics.stepSettled(stepIndex);
    lastCompletedStepIndex = stepIndex;
  }
}
