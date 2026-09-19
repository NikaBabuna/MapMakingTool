/*
 * File: product/src/main/java/com/aethelgard/product/ProductSession.java
 * Purpose: In-process owner of one product Engine run
 * Audience: UI / CLI / tests
 * Update when: Session lifecycle or serialized advance changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.pool.Engine;
import java.util.Objects;

/**
 * One Aethelgard run. Owns the {@link Engine}. {@link #advance(int)} is serialized so UI and CLI
 * cannot interleave Steps. No Swing. Not a command parser.
 */
public final class ProductSession {

  private final WorldSpec spec;
  private final Engine engine;
  private final Object lock = new Object();
  private final DiagnosticsHub diagnostics = DiagnosticsHub.withDefaults();

  public ProductSession(WorldSpec spec) {
    this.spec = Objects.requireNonNull(spec, "spec");
    this.engine = ProductHost.create(spec);
  }

  /** Session for {@link WorldSpec#DEFAULT} (dump fixture). */
  public static ProductSession ofDefault() {
    return new ProductSession(WorldSpec.DEFAULT);
  }

  /** Session for {@link WorldSpec#VIEW} (map window). */
  public static ProductSession view() {
    return new ProductSession(WorldSpec.VIEW);
  }

  public WorldSpec spec() {
    return spec;
  }

  /** Controllable diagnostics hub (not Pool state). */
  public DiagnosticsHub diagnostics() {
    return diagnostics;
  }

  public int stepIndex() {
    synchronized (lock) {
      return engine.stepIndex();
    }
  }

  /** One generation Step. */
  public void advance() {
    advance(1);
  }

  /**
   * {@code n} additional generation Steps. {@code n == 0} is a no-op. Negative is illegal.
   */
  public void advance(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be >= 0, was " + n);
    }
    synchronized (lock) {
      for (int i = 0; i < n; i++) {
        long t0 = System.nanoTime();
        PhaseTiming.withHub(
            diagnostics,
            () -> {
              engine.advance(1);
              return null;
            });
        long dt = System.nanoTime() - t0;
        diagnostics.record(DiagnosticIds.ADVANCE_WALL, dt);
        Runtime rt = Runtime.getRuntime();
        diagnostics.record(DiagnosticIds.HEAP_USED, rt.totalMemory() - rt.freeMemory());
        diagnostics.record(DiagnosticIds.HEAP_MAX, rt.maxMemory());
      }
    }
  }

  public Grid elevation() {
    synchronized (lock) {
      return (Grid) engine.settled().field(WorldFields.ELEVATION);
    }
  }

  public Grid plates() {
    synchronized (lock) {
      return (Grid) engine.settled().field(WorldFields.PLATES);
    }
  }

  public PlateVelocities plateVelocities() {
    synchronized (lock) {
      return (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    }
  }

  public PlateRegistry plateRegistry() {
    synchronized (lock) {
      return (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    }
  }

  public Boundaries boundaries() {
    synchronized (lock) {
      return (Boundaries) engine.settled().field(WorldFields.BOUNDARIES);
    }
  }

  public AreaFlux areaFlux() {
    synchronized (lock) {
      return (AreaFlux) engine.settled().field(WorldFields.AREA_FLUX);
    }
  }

  public MotionIntent motionIntent() {
    synchronized (lock) {
      return (MotionIntent) engine.settled().field(WorldFields.MOTION_INTENT);
    }
  }

  /** Headless snapshot ({@link WorldDump}); same lock as {@link #advance(int)}. */
  public String settledWorld() {
    synchronized (lock) {
      return WorldDump.of(engine, spec);
    }
  }
}
