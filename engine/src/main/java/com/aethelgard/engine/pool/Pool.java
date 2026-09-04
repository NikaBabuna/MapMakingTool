/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/Pool.java
 * Purpose: Shared simulation state; update() once per Step computation
 * Audience: Agents implementing the engine
 * Update when: Pool lifecycle or state shape changes
 */

package com.aethelgard.engine.pool;

/**
 * Engine Pool: shared state updated once at the start of each Step's computation.
 *
 * <p>F-002 uses a trivial value so the Step spine is testable. Events, Systems, and merge arrive
 * in later Steps.
 */
public final class Pool {

  private long value;
  private int updateCount;

  Pool(EngineConfig config) {
    this.value = config.initialValue();
    this.updateCount = 0;
  }

  /**
   * Invoked exactly once at the start of each Step's computation.
   *
   * <p>Trivial rule for F-002: {@code value = value + 1}.
   */
  void update() {
    updateCount++;
    value = value + 1;
  }

  PoolSnapshot snapshot() {
    return new PoolSnapshot(value, updateCount);
  }

  int updateCount() {
    return updateCount;
  }
}
