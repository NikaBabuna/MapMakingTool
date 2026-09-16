/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/EngineSetup.java
 * Purpose: Optional claimers, Systems, schema, user layer, compute, diagnostics for a run
 * Audience: Agents / callers / tests
 * Update when: Run wiring options change
 */

package com.aethelgard.engine.pool;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.event.EventClaimer;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.system.EngineSystem;
import com.aethelgard.engine.user.UserInput;
import com.aethelgard.engine.user.UserView;
import java.util.List;
import java.util.Objects;

/**
 * Wiring for events/claiming/Systems/user-layer/compute/diagnostics. {@link
 * Engine#create(EngineConfig)} uses {@link #defaults()}.
 */
public record EngineSetup(
    CategoryTree categoryTree,
    List<EventClaimer> claimers,
    List<EngineSystem> systems,
    FieldSchema fieldSchema,
    EngineDiagnostics diagnostics,
    UserInput userInput,
    UserView userView,
    PoolCompute poolCompute) {

  public EngineSetup {
    categoryTree = categoryTree == null ? CategoryTree.empty() : categoryTree;
    claimers = List.copyOf(Objects.requireNonNullElse(claimers, List.of()));
    systems = List.copyOf(Objects.requireNonNullElse(systems, List.of()));
    fieldSchema = fieldSchema == null ? FieldSchema.empty() : fieldSchema;
    diagnostics = diagnostics == null ? EngineDiagnostics.slf4j() : diagnostics;
    userInput = userInput == null ? new UserInput() : userInput;
    userView = userView == null ? UserView.noop() : userView;
    poolCompute = poolCompute == null ? SkeletonPoolCompute.INSTANCE : poolCompute;
  }

  /** F-003-compatible wiring (no Systems / schema / custom user layer / custom compute). */
  public EngineSetup(
      CategoryTree categoryTree, List<EventClaimer> claimers, EngineDiagnostics diagnostics) {
    this(
        categoryTree,
        claimers,
        List.of(),
        FieldSchema.empty(),
        diagnostics,
        new UserInput(),
        UserView.noop(),
        null);
  }

  /** F-004/F-005-compatible wiring without custom user layer or compute. */
  public EngineSetup(
      CategoryTree categoryTree,
      List<EventClaimer> claimers,
      List<EngineSystem> systems,
      FieldSchema fieldSchema,
      EngineDiagnostics diagnostics) {
    this(
        categoryTree,
        claimers,
        systems,
        fieldSchema,
        diagnostics,
        new UserInput(),
        UserView.noop(),
        null);
  }

  /** F-006-compatible wiring without custom compute (default {@link SkeletonPoolCompute}). */
  public EngineSetup(
      CategoryTree categoryTree,
      List<EventClaimer> claimers,
      List<EngineSystem> systems,
      FieldSchema fieldSchema,
      EngineDiagnostics diagnostics,
      UserInput userInput,
      UserView userView) {
    this(categoryTree, claimers, systems, fieldSchema, diagnostics, userInput, userView, null);
  }

  public static EngineSetup defaults() {
    return new EngineSetup(
        CategoryTree.empty(),
        List.of(),
        List.of(),
        FieldSchema.empty(),
        EngineDiagnostics.slf4j(),
        new UserInput(),
        UserView.noop(),
        null);
  }
}
