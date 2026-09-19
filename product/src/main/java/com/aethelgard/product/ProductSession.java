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
      engine.advance(n);
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

  /** Headless snapshot ({@link WorldDump}); same lock as {@link #advance(int)}. */
  public String settledWorld() {
    synchronized (lock) {
      return WorldDump.of(engine, spec);
    }
  }
}
