/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/PoolComputeContext.java
 * Purpose: API surface for PoolCompute during one Step update
 * Audience: PoolCompute implementations
 * Update when: Compute context capabilities change
 */

package com.aethelgard.engine.pool;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.Category;
import com.aethelgard.engine.event.EngineEvent;
import com.aethelgard.engine.event.EventBuffer;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.user.InputView;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Mutable view of Pool state for one {@link PoolCompute#compute} call. Created by {@link Pool};
 * not retained across Steps.
 */
public final class PoolComputeContext {

  private final Pool pool;
  private final InputView inputView;
  private final EventBuffer buffer;
  private final List<Category> scriptedEmissions;
  private final EngineDiagnostics diagnostics;

  PoolComputeContext(
      Pool pool,
      InputView inputView,
      EventBuffer buffer,
      List<Category> scriptedEmissions,
      EngineDiagnostics diagnostics) {
    this.pool = Objects.requireNonNull(pool, "pool");
    this.inputView = inputView == null ? InputView.empty() : inputView;
    this.buffer = Objects.requireNonNull(buffer, "buffer");
    this.scriptedEmissions = List.copyOf(Objects.requireNonNull(scriptedEmissions, "scriptedEmissions"));
    this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
  }

  /** Staged Input View for this Step. */
  public InputView inputView() {
    return inputView;
  }

  /** Current trivial heartbeat value. */
  public long value() {
    return pool.valueForCompute();
  }

  /** Sets the trivial heartbeat value. */
  public void setValue(long value) {
    pool.setValueForCompute(value);
  }

  /** How many Pool updates have run including this one. */
  public int updateCount() {
    return pool.updateCount();
  }

  /** Declared field schema (may be empty). */
  public FieldSchema fieldSchema() {
    return pool.fieldSchema();
  }

  /** Current field value, or {@code 0} if unset. */
  public long fieldOrZero(String name) {
    return pool.fieldValues().getOrDefault(name, 0L);
  }

  /**
   * Writes a declared typed field during compute (before System merge apply).
   *
   * @throws IllegalArgumentException if {@code name} is not in the schema
   */
  public void setField(String name, long value) {
    Objects.requireNonNull(name, "name");
    if (!pool.fieldSchema().has(name)) {
      throw new IllegalArgumentException("undeclared field: " + name);
    }
    pool.putFieldForCompute(name, value);
  }

  /** Resolved scripted emission categories from config (may be empty). */
  public List<Category> scriptedEmissions() {
    return scriptedEmissions;
  }

  /** Emits one event into the shared Step buffer and records diagnostics. */
  public void emit(Category category) {
    Objects.requireNonNull(category, "category");
    EngineEvent event = new EngineEvent(category);
    buffer.add(event);
    diagnostics.eventEmitted(event);
  }

  /** Emits every scripted emission category (default skeleton behavior). */
  public void emitScripted() {
    for (Category category : scriptedEmissions) {
      emit(category);
    }
  }

  public EngineDiagnostics diagnostics() {
    return diagnostics;
  }

  /** Snapshot of current fields (unmodifiable copy). */
  public Map<String, Long> fields() {
    return pool.fieldValues();
  }
}
