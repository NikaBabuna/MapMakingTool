/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/ScriptedEventEmissionPolicy.java
 * Purpose: Default emission — EngineConfig scripted category paths
 * Audience: EngineSetup defaults / regression witnesses
 * Update when: Default emission rules change
 */

package com.aethelgard.engine.pool;

/**
 * Default {@link EventEmissionPolicy}: emit every category in {@link
 * PoolComputeContext#scriptedEmissions()} (from {@code EngineConfig.emitCategoryPathsEachUpdate}).
 */
public final class ScriptedEventEmissionPolicy implements EventEmissionPolicy {

  public static final ScriptedEventEmissionPolicy INSTANCE = new ScriptedEventEmissionPolicy();

  private ScriptedEventEmissionPolicy() {}

  @Override
  public void emitEvents(PoolComputeContext context) {
    context.emitScripted();
  }
}
