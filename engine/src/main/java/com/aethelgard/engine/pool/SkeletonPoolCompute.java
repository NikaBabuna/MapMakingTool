/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/SkeletonPoolCompute.java
 * Purpose: Default G-001 Pool compute — heartbeat, nudge, scripted emissions
 * Audience: EngineSetup defaults / regression witnesses
 * Update when: Skeleton demo rules change
 */

package com.aethelgard.engine.pool;

/**
 * Default {@link PoolCompute}: {@code value += 1}; if Input View has {@link #NUDGE_ACTION} active,
 * also {@code value += 100}; then emit scripted categories.
 */
public final class SkeletonPoolCompute implements PoolCompute {

  /** Shared default instance. */
  public static final SkeletonPoolCompute INSTANCE = new SkeletonPoolCompute();

  /** Skeleton demo action: when active in Input View, adds 100 to {@code value}. */
  public static final String NUDGE_ACTION = "nudge";

  private SkeletonPoolCompute() {}

  @Override
  public void compute(PoolComputeContext context) {
    context.setValue(context.value() + 1);
    if (context.inputView().isActive(NUDGE_ACTION)) {
      context.setValue(context.value() + 100);
    }
    context.emitScripted();
  }
}
