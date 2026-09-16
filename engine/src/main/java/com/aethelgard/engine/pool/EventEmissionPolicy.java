/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/EventEmissionPolicy.java
 * Purpose: Pluggable which-events-fire-this-Step strategy
 * Audience: EngineSetup / product (later)
 * Update when: Emission policy port changes
 */

package com.aethelgard.engine.pool;

/**
 * Decides which events enter the shared buffer during Pool compute. Default: {@link
 * ScriptedEventEmissionPolicy}. Custom {@link PoolCompute} may ignore the policy and emit manually.
 */
@FunctionalInterface
public interface EventEmissionPolicy {

  /** Emit zero or more events for this Step via {@code context}. */
  void emitEvents(PoolComputeContext context);
}
