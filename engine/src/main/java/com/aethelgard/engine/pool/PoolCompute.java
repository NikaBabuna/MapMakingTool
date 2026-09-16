/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/PoolCompute.java
 * Purpose: Pluggable Pool update strategy (host extension point)
 * Audience: Engine callers / product (later)
 * Update when: Compute port contract changes
 */

package com.aethelgard.engine.pool;

/**
 * Runs once per Step during Pool update. Product supplies a custom implementation; the engine
 * default is {@link SkeletonPoolCompute}.
 */
@FunctionalInterface
public interface PoolCompute {

  /** Applies this Step's Pool computation using {@code context}. */
  void compute(PoolComputeContext context);
}
