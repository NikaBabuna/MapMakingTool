/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/EngineSetup.java
 * Purpose: Optional claimers, Systems, schema, user layer, compute, emission, diagnostics
 * Audience: Agents / callers / tests
 * Update when: Run wiring options change
 */

package com.aethelgard.engine.pool;

import com.aethelgard.engine.diagnostics.EngineDiagnostics;
import com.aethelgard.engine.events.CategoryTree;
import com.aethelgard.engine.events.EventClaimer;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.systems.EngineSystem;
import com.aethelgard.engine.user.UserInput;
import com.aethelgard.engine.user.UserView;
import java.util.List;
import java.util.Objects;

/**
 * Wiring for events/claiming/Systems/user-layer/compute/emission/diagnostics. {@link
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
    PoolCompute poolCompute,
    EventEmissionPolicy eventEmissionPolicy) {

  public EngineSetup {
    categoryTree = categoryTree == null ? CategoryTree.empty() : categoryTree;
    claimers = List.copyOf(Objects.requireNonNullElse(claimers, List.of()));
    systems = List.copyOf(Objects.requireNonNullElse(systems, List.of()));
    fieldSchema = fieldSchema == null ? FieldSchema.empty() : fieldSchema;
    diagnostics = diagnostics == null ? EngineDiagnostics.slf4j() : diagnostics;
    userInput = userInput == null ? new UserInput() : userInput;
    userView = userView == null ? UserView.noop() : userView;
    poolCompute = poolCompute == null ? SkeletonPoolCompute.INSTANCE : poolCompute;
    eventEmissionPolicy =
        eventEmissionPolicy == null ? ScriptedEventEmissionPolicy.INSTANCE : eventEmissionPolicy;
  }

  /** Wiring with claimers only: no systems, and the defaults for user layer, compute, and emission. */
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
        null,
        null);
  }

  /** Wiring with systems and a field schema, without a custom user layer. */
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
        null,
        null);
  }

  /** Wiring with a user layer, without custom compute / emission. */
  public EngineSetup(
      CategoryTree categoryTree,
      List<EventClaimer> claimers,
      List<EngineSystem> systems,
      FieldSchema fieldSchema,
      EngineDiagnostics diagnostics,
      UserInput userInput,
      UserView userView) {
    this(
        categoryTree,
        claimers,
        systems,
        fieldSchema,
        diagnostics,
        userInput,
        userView,
        null,
        null);
  }

  /** Wiring with a custom compute, without a custom emission policy. */
  public EngineSetup(
      CategoryTree categoryTree,
      List<EventClaimer> claimers,
      List<EngineSystem> systems,
      FieldSchema fieldSchema,
      EngineDiagnostics diagnostics,
      UserInput userInput,
      UserView userView,
      PoolCompute poolCompute) {
    this(
        categoryTree,
        claimers,
        systems,
        fieldSchema,
        diagnostics,
        userInput,
        userView,
        poolCompute,
        null);
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
        null,
        null);
  }
}
