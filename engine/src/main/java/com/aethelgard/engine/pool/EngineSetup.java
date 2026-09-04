/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/EngineSetup.java
 * Purpose: Optional claimers, category tree, and diagnostics for a run
 * Audience: Agents / callers / tests
 * Update when: Run wiring options change
 */

package com.aethelgard.engine.pool;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.event.EventClaimer;
import java.util.List;
import java.util.Objects;

/**
 * Wiring for events/claiming/diagnostics. {@link Engine#create(EngineConfig)} uses {@link
 * #defaults()}.
 */
public record EngineSetup(
    CategoryTree categoryTree,
    List<EventClaimer> claimers,
    EngineDiagnostics diagnostics) {

  public EngineSetup {
    categoryTree = categoryTree == null ? CategoryTree.empty() : categoryTree;
    claimers = List.copyOf(Objects.requireNonNullElse(claimers, List.of()));
    diagnostics = diagnostics == null ? EngineDiagnostics.slf4j() : diagnostics;
  }

  public static EngineSetup defaults() {
    return new EngineSetup(CategoryTree.empty(), List.of(), EngineDiagnostics.slf4j());
  }
}
