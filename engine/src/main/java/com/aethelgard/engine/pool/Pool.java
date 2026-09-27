/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/Pool.java
 * Purpose: Shared simulation state; update() once per Step computation
 * Audience: Agents implementing the engine
 * Update when: Pool lifecycle or state shape changes
 */

package com.aethelgard.engine.pool;

import com.aethelgard.engine.diagnostics.EngineDiagnostics;
import com.aethelgard.engine.events.Category;
import com.aethelgard.engine.events.CategoryTree;
import com.aethelgard.engine.events.EventBuffer;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.user.InputView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Engine Pool: shared state updated once at the start of each Step's computation.
 *
 * <p>Update rules live in {@link PoolCompute} (default {@link SkeletonPoolCompute}). Emission rules
 * live in {@link EventEmissionPolicy} (default {@link ScriptedEventEmissionPolicy}). Typed fields
 * also receive merged System output after claim. Samples {@link InputView} once per
 * update.
 */
public final class Pool {

  /** Skeleton demo action name — alias of {@link SkeletonPoolCompute#NUDGE_ACTION}. */
  public static final String NUDGE_ACTION = SkeletonPoolCompute.NUDGE_ACTION;

  private long value;
  private int updateCount;
  private final FieldSchema fieldSchema;
  private final Map<String, Object> fields = new LinkedHashMap<>();
  private final PoolCompute compute;
  private final CategoryTree categoryTree;
  private final EventEmissionPolicy emissionPolicy;
  private InputView lastInputView = InputView.empty();

  Pool(
      EngineConfig config,
      FieldSchema fieldSchema,
      PoolCompute compute,
      CategoryTree categoryTree,
      EventEmissionPolicy emissionPolicy) {
    this.value = config.initialValue();
    this.updateCount = 0;
    this.fieldSchema = fieldSchema == null ? FieldSchema.empty() : fieldSchema;
    this.compute = Objects.requireNonNull(compute, "compute");
    this.categoryTree = Objects.requireNonNull(categoryTree, "categoryTree");
    this.emissionPolicy = Objects.requireNonNull(emissionPolicy, "emissionPolicy");
    for (String name : this.fieldSchema.asMap().keySet()) {
      Object seed = config.initialFields().get(name);
      fields.put(name, seed != null ? seed : 0L);
    }
    for (String name : config.initialFields().keySet()) {
      if (!this.fieldSchema.has(name)) {
        throw new IllegalArgumentException(
            "initialFields contains undeclared field: " + name);
      }
    }
  }

  /**
   * Invoked exactly once at the start of each Step's computation. Delegates rules to {@link
   * PoolCompute}.
   */
  void update(
      EventBuffer buffer,
      List<Category> emissions,
      EngineDiagnostics diagnostics,
      InputView inputView) {
    this.lastInputView = inputView == null ? InputView.empty() : inputView;
    updateCount++;
    compute.compute(
        new PoolComputeContext(
            this,
            this.lastInputView,
            buffer,
            emissions,
            diagnostics,
            categoryTree,
            emissionPolicy));
  }

  /** Applies typed-merge result once per Step (after Systems finish). */
  void applyFields(Map<String, Object> merged) {
    fields.clear();
    fields.putAll(merged);
  }

  Map<String, Object> fieldValues() {
    return Map.copyOf(fields);
  }

  FieldSchema fieldSchema() {
    return fieldSchema;
  }

  InputView lastInputView() {
    return lastInputView;
  }

  PoolSnapshot snapshot() {
    return new PoolSnapshot(value, updateCount, fields);
  }

  int updateCount() {
    return updateCount;
  }

  long valueForCompute() {
    return value;
  }

  void setValueForCompute(long value) {
    this.value = value;
  }

  void putFieldForCompute(String name, Object value) {
    fields.put(name, Objects.requireNonNull(value, "value"));
  }
}
