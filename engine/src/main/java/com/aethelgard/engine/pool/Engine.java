/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/Engine.java
 * Purpose: Step loop driver — config bootstrap, events, Systems, merge, settled snapshots
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
import com.aethelgard.engine.merge.ProvenancedWrite;
import com.aethelgard.engine.merge.StepOutputBuffer;
import com.aethelgard.engine.merge.TypedMerge;
import com.aethelgard.engine.system.EngineSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Drives discrete Steps over a {@link Pool}.
 *
 * <p><b>Step index rule (F-002):</b> {@link #stepIndex()} is the 0-based index of the last
 * completed Step. {@link #create} completes Step 0 → index {@code 0}. Each {@link #advance()}
 * completes the next Step and increments the index by 1. After create + {@code advance(n)},
 * {@code stepIndex() == n}.
 *
 * <p><b>Step order (F-004):</b> update → claim → run claiming Systems → typed merge → apply →
 * clear event buffer → settle. Systems run synchronously (claim/finish barrier is F-005).
 */
public final class Engine {

  private final Pool pool;
  private final EventBuffer eventBuffer = new EventBuffer();
  private final List<Category> emissions;
  private final List<EventClaimer> claimers;
  private final List<EngineSystem> systems;
  private final EngineDiagnostics diagnostics;
  private int lastCompletedStepIndex = -1;
  private ClaimResult lastClaimResult = ClaimResult.empty();
  private StepOutputBuffer lastStepOutput = new StepOutputBuffer();

  private Engine(
      Pool pool,
      List<Category> emissions,
      List<EventClaimer> claimers,
      List<EngineSystem> systems,
      EngineDiagnostics diagnostics) {
    this.pool = pool;
    this.emissions = List.copyOf(emissions);
    this.claimers = List.copyOf(claimers);
    this.systems = List.copyOf(systems);
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
   * Creates a run with explicit category tree, claimers, Systems, schema, and diagnostics.
   *
   * <p>Emission paths in {@code config} are resolved against {@code setup.categoryTree()}.
   */
  public static Engine create(EngineConfig config, EngineSetup setup) {
    Objects.requireNonNull(config, "config");
    Objects.requireNonNull(setup, "setup");
    List<Category> emissions =
        setup.categoryTree().resolveAll(config.emitCategoryPathsEachUpdate());
    Engine engine =
        new Engine(
            new Pool(config, setup.fieldSchema()),
            emissions,
            setup.claimers(),
            setup.systems(),
            setup.diagnostics());
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

  /** Provenanced System writes from the last completed Step (before merge apply). */
  public StepOutputBuffer lastStepOutput() {
    return lastStepOutput;
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

    List<EventClaimer> allClaimers = new ArrayList<>(claimers);
    for (EngineSystem system : systems) {
      allClaimers.add(system.claimer());
    }

    ClaimResult result = EventClaiming.claim(eventBuffer.events(), allClaimers);
    for (var entry : result.claimedByClaimer().entrySet()) {
      EventClaimer claimer = entry.getKey();
      for (EngineEvent event : entry.getValue()) {
        diagnostics.eventClaimed(event, claimer);
      }
    }
    for (EngineEvent event : result.unmatched()) {
      diagnostics.unmatchedEvent(event);
    }

    // Same snapshot for every System — independence (FR-2).
    PoolSnapshot snapshotForSystems = pool.snapshot();
    StepOutputBuffer output = new StepOutputBuffer();
    for (EngineSystem system : systems) {
      List<EngineEvent> claimed = result.claimedByClaimer().get(system.claimer());
      if (claimed == null || claimed.isEmpty()) {
        continue;
      }
      Map<String, Long> outSys = system.run(snapshotForSystems);
      for (var fieldWrite : outSys.entrySet()) {
        output.add(fieldWrite.getKey(), new ProvenancedWrite(system.id(), fieldWrite.getValue()));
      }
    }

    Map<String, Long> merged =
        TypedMerge.merge(pool.fieldSchema(), pool.fieldValues(), output);
    pool.applyFields(merged);

    eventBuffer.clear();
    lastClaimResult = result;
    lastStepOutput = output;
    diagnostics.stepSettled(stepIndex);
    lastCompletedStepIndex = stepIndex;
  }
}
