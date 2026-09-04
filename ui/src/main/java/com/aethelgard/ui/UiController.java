/*
 * File: ui/src/main/java/com/aethelgard/ui/UiController.java
 * Purpose: Headless UI logic — create run, advance Steps, settled text via User View
 * Audience: Tests / Swing shell
 * Update when: Skeleton UI behavior changes
 */

package com.aethelgard.ui;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.pool.PoolSnapshot;
import com.aethelgard.engine.user.UserInput;
import com.aethelgard.engine.user.UserView;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Headless controller for the skeleton UI. No Swing types — safe for CI tests.
 *
 * <p>Settled display is driven by {@link UserView} callbacks (F-006 contract).
 */
public final class UiController {

  private Engine engine;
  private String settledText = "";
  private Consumer<String> settledListener = text -> {};

  public UiController(long initialValue) {
    this(initialValue, EngineDiagnostics.noop());
  }

  public UiController(long initialValue, EngineDiagnostics diagnostics) {
    Objects.requireNonNull(diagnostics, "diagnostics");
    UserView bridging = this::onSettledSnapshot;
    this.engine =
        Engine.create(
            new EngineConfig(initialValue),
            new EngineSetup(
                null, List.of(), List.of(), null, diagnostics, new UserInput(), bridging));
    // Ensure text matches post-create stepIndex (field assigned after first callback).
    settledText = formatSettled(engine.stepIndex(), engine.settled());
  }

  private void onSettledSnapshot(PoolSnapshot snapshot) {
    int step =
        engine == null ? Math.max(0, snapshot.updateCount() - 1) : engine.stepIndex();
    settledText = formatSettled(step, snapshot);
    settledListener.accept(settledText);
  }

  /** Registers a listener invoked whenever settled text updates (e.g. Swing label). */
  public void onSettledTextChanged(Consumer<String> listener) {
    this.settledListener = listener == null ? text -> {} : listener;
    settledListener.accept(settledText);
  }

  public void advance() {
    engine.advance();
  }

  public void advance(int n) {
    engine.advance(n);
  }

  public int stepIndex() {
    return engine.stepIndex();
  }

  public PoolSnapshot settled() {
    return engine.settled();
  }

  public String settledText() {
    return settledText;
  }

  public Engine engine() {
    return engine;
  }

  static String formatSettled(int stepIndex, PoolSnapshot snap) {
    StringBuilder sb = new StringBuilder();
    sb.append("stepIndex=").append(stepIndex).append('\n');
    sb.append("value=").append(snap.value()).append('\n');
    sb.append("updateCount=").append(snap.updateCount()).append('\n');
    if (!snap.fields().isEmpty()) {
      sb.append("fields=").append(snap.fields()).append('\n');
    }
    return sb.toString();
  }
}
