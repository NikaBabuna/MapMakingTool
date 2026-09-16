/*
 * File: product/src/main/java/com/aethelgard/product/MapController.java
 * Purpose: Headless map logic — create run, advance, raster, busy flag
 * Audience: Tests / Swing shell
 * Update when: Map window behavior changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.pool.Engine;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Headless controller for the product map. No Swing types — safe for CI tests.
 *
 * <p>{@link #advance()} runs one generation Step on the caller. {@link #advanceAsync()} runs it on
 * the injected {@link Executor} and reports {@link #busy()} / {@link #WORKING_STATUS} while in
 * flight.
 */
public final class MapController {

  public static final String WORKING_STATUS = "Working...";

  private final WorldSpec spec;
  private final Executor executor;
  private final Engine engine;
  private final AtomicBoolean busy = new AtomicBoolean(false);
  private final List<Runnable> listeners = new CopyOnWriteArrayList<>();
  private ElevationRaster raster;

  public MapController(WorldSpec spec) {
    this(spec, Runnable::run);
  }

  public MapController(WorldSpec spec, Executor executor) {
    this.spec = Objects.requireNonNull(spec, "spec");
    this.executor = Objects.requireNonNull(executor, "executor");
    this.engine = ProductHost.create(spec);
    this.raster = snapshot();
  }

  /** Launch controller: {@link WorldSpec#VIEW}. */
  public static MapController view(Executor executor) {
    return new MapController(WorldSpec.VIEW, executor);
  }

  public WorldSpec spec() {
    return spec;
  }

  public int stepIndex() {
    return engine.stepIndex();
  }

  public ElevationRaster raster() {
    return raster;
  }

  public boolean busy() {
    return busy.get();
  }

  /** {@link #WORKING_STATUS} while busy; otherwise {@code Step n}. */
  public String statusText() {
    if (busy.get()) {
      return WORKING_STATUS;
    }
    return "Step " + engine.stepIndex();
  }

  public void onChanged(Runnable listener) {
    Objects.requireNonNull(listener, "listener");
    listeners.add(listener);
    listener.run();
  }

  /** One generation Step on the caller thread. */
  public void advance() {
    engine.advance(1);
    raster = snapshot();
    fire();
  }

  /**
   * One generation Step on {@code executor}. Ignored while {@link #busy()}. Sets busy and status
   * before submit.
   */
  public void advanceAsync() {
    if (!busy.compareAndSet(false, true)) {
      return;
    }
    fire();
    executor.execute(
        () -> {
          try {
            engine.advance(1);
            raster = snapshot();
          } finally {
            busy.set(false);
            fire();
          }
        });
  }

  public Engine engine() {
    return engine;
  }

  private ElevationRaster snapshot() {
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);
    return ElevationRaster.of(elevation);
  }

  private void fire() {
    for (Runnable listener : listeners) {
      listener.run();
    }
  }
}
