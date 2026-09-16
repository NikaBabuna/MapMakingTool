/*
 * File: product/src/main/java/com/aethelgard/product/ProductHost.java
 * Purpose: Product factory — construct Engine via EngineSetup
 * Audience: Product callers / tests
 * Update when: Product EngineSetup wiring changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import java.util.Objects;

/**
 * Product entry for constructing an {@link Engine}. Setup is owned here so later Steps can add
 * schema and Systems without callers talking to engine defaults directly.
 *
 * <p>F-013: {@link #setup()} is {@link EngineSetup#defaults()} (skeleton compute / emission).
 */
public final class ProductHost {

  private ProductHost() {}

  /** Product {@link EngineSetup}. F-013: engine defaults. */
  public static EngineSetup setup() {
    return EngineSetup.defaults();
  }

  /** Creates a run with a zero heartbeat seed and product setup. Completes Step 0. */
  public static Engine create() {
    return create(new EngineConfig(0L));
  }

  /**
   * Creates a run from {@code config} and {@link #setup()}. Completes Step 0.
   *
   * @param config Step 0 seed (must not be {@code null})
   */
  public static Engine create(EngineConfig config) {
    return Engine.create(Objects.requireNonNull(config, "config"), setup());
  }
}
