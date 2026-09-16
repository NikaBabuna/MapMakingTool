/*
 * File: product/src/main/java/com/aethelgard/product/ProductHost.java
 * Purpose: Product factory — construct Engine via EngineSetup
 * Audience: Product callers / tests
 * Update when: Product EngineSetup wiring changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Product entry for constructing an {@link Engine}. Setup is owned here so later Steps can add
 * schema and Systems without callers talking to engine defaults directly.
 *
 * <p>F-014: schema includes {@link WorldFields#ELEVATION}; {@link #create()} seeds a zero {@link
 * Grid}.
 */
public final class ProductHost {

  private ProductHost() {}

  /** Product {@link EngineSetup} with the world field schema. */
  public static EngineSetup setup() {
    return new EngineSetup(
        null,
        List.of(),
        List.of(),
        FieldSchema.of(WorldFields.ELEVATION, FieldType.STATIC),
        null);
  }

  /** Creates a run with {@link WorldSpec#DEFAULT} (8×8, seed 0). Completes Step 0. */
  public static Engine create() {
    return create(WorldSpec.DEFAULT);
  }

  /**
   * Creates a run from {@code spec}: seeds {@code elevation} as a zero grid of that geometry.
   * Completes Step 0.
   *
   * @param spec Step 0 world seed (must not be {@code null})
   */
  public static Engine create(WorldSpec spec) {
    Objects.requireNonNull(spec, "spec");
    Grid elevation = Grid.zeros(spec.width(), spec.height());
    EngineConfig config =
        new EngineConfig(0L, List.of(), Map.of(WorldFields.ELEVATION, elevation));
    return Engine.create(config, setup());
  }
}
