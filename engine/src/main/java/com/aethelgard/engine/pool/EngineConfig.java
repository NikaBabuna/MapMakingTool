/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/EngineConfig.java
 * Purpose: Caller-supplied Step 0 seed and optional scripted event emissions
 * Audience: Agents / callers (tests, later CLI/UI)
 * Update when: Config shape for bootstrap changes
 */

package com.aethelgard.engine.pool;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Starting configuration for a run. Sole seed for Step 0 — no hidden globals.
 *
 * @param initialValue trivial Pool seed (F-002 heartbeat)
 * @param emitCategoryPathsEachUpdate category paths emitted on every Pool {@code update} (F-003)
 * @param initialFields typed field seeds (F-004/F-011); must match {@link
 *     com.aethelgard.engine.merge.FieldSchema} keys when used; values are {@link Object}
 */
public record EngineConfig(
    long initialValue, List<String> emitCategoryPathsEachUpdate, Map<String, Object> initialFields) {

  public EngineConfig {
    emitCategoryPathsEachUpdate =
        List.copyOf(Objects.requireNonNullElse(emitCategoryPathsEachUpdate, List.of()));
    initialFields = Map.copyOf(Objects.requireNonNullElse(initialFields, Map.of()));
  }

  /** Config with no scripted emissions and no typed fields. */
  public EngineConfig(long initialValue) {
    this(initialValue, List.of(), Map.of());
  }

  /** Config with scripted emissions and no typed fields (F-003). */
  public EngineConfig(long initialValue, List<String> emitCategoryPathsEachUpdate) {
    this(initialValue, emitCategoryPathsEachUpdate, Map.of());
  }
}
