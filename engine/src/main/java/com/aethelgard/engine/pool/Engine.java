/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/Engine.java
 * Purpose: Step loop driver — input staging, events, Systems, barrier, merge, User View
 * Audience: Agents / callers (tests, later CLI/UI)
 * Update when: Step orchestration API changes
 */

package com.aethelgard.engine.pool;

import com.aethelgard.engine.diagnostics.EngineDiagnostics;
import com.aethelgard.engine.events.Category;
import com.aethelgard.engine.events.ClaimResult;
import com.aethelgard.engine.events.EngineEvent;
import com.aethelgard.engine.events.EventBuffer;
import com.aethelgard.engine.events.EventClaimer;
import com.aethelgard.engine.events.EventClaiming;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.ProvenancedWrite;
import com.aethelgard.engine.merge.StepOutputBuffer;
import com.aethelgard.engine.merge.TypedMerge;
import com.aethelgard.engine.systems.ClaimFinishBarrier;
import com.aethelgard.engine.systems.ClaimFinishSnapshot;
import com.aethelgard.engine.systems.EngineSystem;
import com.aethelgard.engine.user.InputView;
import com.aethelgard.engine.user.UserInput;
import com.aethelgard.engine.user.UserView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Drives discrete Steps over a {@link Pool}.
 *
 * <p><b>Step index rule:</b> {@link #stepIndex()} is the 0-based index of the last
 * completed Step. {@link #create} completes Step 0 → index {@code 0}. Each {@link #advance()}
 * completes the next Step and increments the index by 1. After create + {@code advance(n)},
 * {@code stepIndex() == n}.
 *
 * <p><b>Step order:</b> stage Input View → update (reads Input View) → claim → Systems →
 * barrier → typed merge → apply → clear → User View(settled) → settle.
 */
public final class Engine {

  private final Pool pool;
  private final EventBuffer eventBuffer = new EventBuffer();
  private final List<Category> emissions;
  private final List<EventClaimer> claimers;
  private final List<EngineSystem> systems;
  private final EngineDiagnostics diagnostics;
  private final UserInput userInput;
  private final UserView userView;
  private int lastCompletedStepIndex = -1;
  private ClaimResult lastClaimResult = ClaimResult.empty();
  private StepOutputBuffer lastStepOutput = new StepOutputBuffer();
  private ClaimFinishSnapshot lastClaimFinish = ClaimFinishSnapshot.empty();
  private InputView lastInputView = InputView.empty();

  private Engine(
      Pool pool,
      List<Category> emissions,
      List<EventClaimer> claimers,
      List<EngineSystem> systems,
      EngineDiagnostics diagnostics,
      UserInput userInput,
      UserView userView) {
    this.pool = pool;
    this.emissions = List.copyOf(emissions);
    this.claimers = List.copyOf(claimers);
    this.systems = List.copyOf(systems);
    this.diagnostics = diagnostics;
    this.userInput = userInput;
    this.userView = userView;
  }

  /**
   * Creates a run from {@code config}, runs Step 0, returns settled engine at {@code stepIndex()
   * == 0}. Uses {@link EngineSetup#defaults()}.
   */
  public static Engine create(EngineConfig config) {
    return create(config, EngineSetup.defaults());
  }

  /**
   * Creates a run with explicit category tree, claimers, Systems, schema, user layer, and
   * diagnostics.
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
            new Pool(
                config,
                setup.fieldSchema(),
                setup.poolCompute(),
                setup.categoryTree(),
                setup.eventEmissionPolicy()),
            emissions,
            setup.claimers(),
            setup.systems(),
            setup.diagnostics(),
            setup.userInput(),
            setup.userView());
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

  /** Registered Systems for this run (construction introspection). */
  public List<EngineSystem> systems() {
    return systems;
  }

  /** Field schema for this run (construction introspection). */
  public FieldSchema fieldSchema() {
    return pool.fieldSchema();
  }

  /** Claim / unmatched outcomes from the last completed Step. */
  public ClaimResult lastClaimResult() {
    return lastClaimResult;
  }

  /** Provenanced System writes from the last completed Step (before merge apply). */
  public StepOutputBuffer lastStepOutput() {
    return lastStepOutput;
  }

  /** Claim/finish barrier snapshot from the last completed Step. */
  public ClaimFinishSnapshot lastClaimFinish() {
    return lastClaimFinish;
  }

  /** Input View sampled at the start of the last completed Step. */
  public InputView lastInputView() {
    return lastInputView;
  }

  /** Shared User Input register for this run (press/release between Steps). */
  public UserInput userInput() {
    return userInput;
  }

  /** True when the shared event buffer is empty (expected after settle). */
  public boolean isEventBufferEmpty() {
    return eventBuffer.isEmpty();
  }

  private void runStep() {
    int stepIndex = lastCompletedStepIndex + 1;
    diagnostics.stepStarted(stepIndex);

    if (!eventBuffer.isEmpty()) {
      throw new IllegalStateException("event buffer not empty at Step start");
    }

    InputView inputView = userInput.stage();
    pool.update(eventBuffer, emissions, diagnostics, inputView);
    userInput.consumePersistentPresentIn(inputView);
    lastInputView = inputView;

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

    // Same snapshot for every System — independence.
    // Note: this snapshot is post-update / pre-merge — not exposed to User View.
    PoolSnapshot snapshotForSystems = pool.snapshot();
    StepOutputBuffer output = new StepOutputBuffer();
    ClaimFinishBarrier barrier = new ClaimFinishBarrier();

    for (EngineSystem system : systems) {
      List<EngineEvent> claimed = result.claimedByClaimer().get(system.claimer());
      if (claimed == null || claimed.isEmpty()) {
        continue;
      }
      barrier.onClaimed(system.id());
      Map<String, Object> outSys = system.run(snapshotForSystems);
      for (var fieldWrite : outSys.entrySet()) {
        output.add(fieldWrite.getKey(), new ProvenancedWrite(system.id(), fieldWrite.getValue()));
      }
      barrier.onFinished(system.id());
    }

    barrier.requireBalanced();

    Map<String, Object> merged =
        TypedMerge.merge(pool.fieldSchema(), pool.fieldValues(), output);
    pool.applyFields(merged);

    eventBuffer.clear();
    lastClaimResult = result;
    lastStepOutput = output;
    lastClaimFinish = barrier.snapshot();
    lastCompletedStepIndex = stepIndex;

    PoolSnapshot settled = pool.snapshot();
    userView.onSettled(settled);

    diagnostics.stepSettled(stepIndex);
  }
}
