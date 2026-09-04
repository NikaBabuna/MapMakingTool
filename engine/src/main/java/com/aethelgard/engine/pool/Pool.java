/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/Pool.java
 * Purpose: Shared simulation state; update() once per Step computation
 * Audience: Agents implementing the engine
 * Update when: Pool lifecycle or state shape changes
 */

package com.aethelgard.engine.pool;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.Category;
import com.aethelgard.engine.event.EngineEvent;
import com.aethelgard.engine.event.EventBuffer;
import java.util.List;

/**
 * Engine Pool: shared state updated once at the start of each Step's computation.
 *
 * <p>May emit scripted events into the shared buffer during {@link #update}.
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
   * <p>Trivial rule for F-002: {@code value = value + 1}. Then emits configured events (F-003).
   */
  void update(
      EventBuffer buffer, List<Category> emissions, EngineDiagnostics diagnostics) {
    updateCount++;
    value = value + 1;
    for (Category category : emissions) {
      EngineEvent event = new EngineEvent(category);
      buffer.add(event);
      diagnostics.eventEmitted(event);
    }
  }

  PoolSnapshot snapshot() {
    return new PoolSnapshot(value, updateCount);
  }

  int updateCount() {
    return updateCount;
  }
}
