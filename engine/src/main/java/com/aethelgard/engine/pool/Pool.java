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
import com.aethelgard.engine.merge.FieldSchema;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Engine Pool: shared state updated once at the start of each Step's computation.
 *
 * <p>May emit scripted events into the shared buffer during {@link #update}. Typed fields receive
 * merged System output after claim (F-004).
 */
public final class Pool {

  private long value;
  private int updateCount;
  private final FieldSchema fieldSchema;
  private final Map<String, Long> fields = new LinkedHashMap<>();

  Pool(EngineConfig config, FieldSchema fieldSchema) {
    this.value = config.initialValue();
    this.updateCount = 0;
    this.fieldSchema = fieldSchema == null ? FieldSchema.empty() : fieldSchema;
    for (String name : this.fieldSchema.asMap().keySet()) {
      fields.put(name, config.initialFields().getOrDefault(name, 0L));
    }
    for (String name : config.initialFields().keySet()) {
      if (!this.fieldSchema.has(name)) {
        throw new IllegalArgumentException(
            "initialFields contains undeclared field: " + name);
      }
    }
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

  /** Applies typed-merge result once per Step (after Systems finish). */
  void applyFields(Map<String, Long> merged) {
    fields.clear();
    fields.putAll(merged);
  }

  Map<String, Long> fieldValues() {
    return Map.copyOf(fields);
  }

  FieldSchema fieldSchema() {
    return fieldSchema;
  }

  PoolSnapshot snapshot() {
    return new PoolSnapshot(value, updateCount, fields);
  }

  int updateCount() {
    return updateCount;
  }
}
