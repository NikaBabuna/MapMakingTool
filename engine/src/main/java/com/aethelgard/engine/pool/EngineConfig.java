/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/EngineConfig.java
 * Purpose: Caller-supplied Step 0 seed and optional scripted event emissions
 * Audience: Agents / callers (tests, later CLI/UI)
 * Update when: Config shape for bootstrap changes
 */

package com.aethelgard.engine.pool;

import java.util.List;
import java.util.Objects;

/**
 * Starting configuration for a run. Sole seed for Step 0 — no hidden globals.
 *
 * @param initialValue trivial Pool seed (F-002 heartbeat)
 * @param emitCategoryPathsEachUpdate category paths emitted on every Pool {@code update} (F-003)
 */
public record EngineConfig(long initialValue, List<String> emitCategoryPathsEachUpdate) {

  public EngineConfig {
    emitCategoryPathsEachUpdate =
        List.copyOf(Objects.requireNonNullElse(emitCategoryPathsEachUpdate, List.of()));
  }

  /** Config with no scripted emissions. */
  public EngineConfig(long initialValue) {
    this(initialValue, List.of());
  }
}
