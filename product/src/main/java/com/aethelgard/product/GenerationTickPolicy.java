/*
 * File: product/src/main/java/com/aethelgard/product/GenerationTickPolicy.java
 * Purpose: Emit the tectonics tick after Step 0
 * Audience: ProductHost EngineSetup
 * Update when: When generation events fire changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.pool.EventEmissionPolicy;
import com.aethelgard.engine.pool.PoolComputeContext;

/**
 * Product emission policy: fire {@link ProductCategories#TECTONICS} on every Pool update after Step
 * 0 (kinematics + collision uplift). Step 0 is {@code updateCount == 1}; generation starts at {@code
 * 2}.
 */
public final class GenerationTickPolicy implements EventEmissionPolicy {

  public static final GenerationTickPolicy INSTANCE = new GenerationTickPolicy();

  /** First updateCount that emits a generation tick (create is 1). */
  static final int FIRST_GENERATION_UPDATE = 2;

  private GenerationTickPolicy() {}

  @Override
  public void emitEvents(PoolComputeContext context) {
    if (context.updateCount() >= FIRST_GENERATION_UPDATE) {
      context.emitPath(ProductCategories.TECTONICS);
    }
  }
}
