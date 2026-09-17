/*
 * File: product/src/main/java/com/aethelgard/product/ProductHost.java
 * Purpose: Product factory — construct Engine via EngineSetup
 * Audience: Product callers / tests
 * Update when: Product EngineSetup wiring changes
 */

package com.aethelgard.product;

import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.system.EngineSystem;
import com.aethelgard.engine.system.SystemConfig;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Product entry for constructing an {@link Engine}. Setup is owned here so later Steps can add
 * schema and Systems without callers talking to engine defaults directly.
 *
 * <p>F-020: schema includes {@link WorldFields#ELEVATION} (STATIC), {@link WorldFields#PLATES}
 * (STATIC), and {@link WorldFields#PLATE_VELOCITY} (CONSTANT). Kinematics and tectonics Systems
 * both claim {@code world/tectonics} after Step 0 via {@link GenerationTickPolicy}.
 */
public final class ProductHost {

  /** Provenance / claimer id for the kinematics System. */
  public static final String KINEMATICS_SYSTEM_ID = "kinematics";

  /** Provenance / claimer id for the tectonics (collision-uplift) System. */
  public static final String TECTONICS_SYSTEM_ID = "tectonics";

  private ProductHost() {}

  /**
   * Product {@link EngineSetup}: world fields, category tree, generation tick, kinematics +
   * tectonics Systems.
   */
  public static EngineSetup setup() {
    CategoryTree tree = ProductCategories.tree();
    EngineSystem kinematics =
        new EngineSystem(
            new SystemConfig(
                KINEMATICS_SYSTEM_ID,
                tree.get(ProductCategories.TECTONICS),
                List.of(new PlateKinematics()),
                null));
    EngineSystem tectonics =
        new EngineSystem(
            new SystemConfig(
                TECTONICS_SYSTEM_ID,
                tree.get(ProductCategories.TECTONICS),
                List.of(new CollisionUplift()),
                null));
    FieldSchema schema =
        FieldSchema.of(
            Map.of(
                WorldFields.ELEVATION, FieldType.STATIC,
                WorldFields.PLATES, FieldType.STATIC,
                WorldFields.PLATE_VELOCITY, FieldType.CONSTANT));
    return new EngineSetup(
        tree,
        List.of(),
        List.of(kinematics, tectonics),
        schema,
        null,
        null,
        null,
        null,
        GenerationTickPolicy.INSTANCE);
  }

  /** Creates a run with {@link WorldSpec#DEFAULT} (8×8, seed 0). Completes Step 0. */
  public static Engine create() {
    return create(WorldSpec.DEFAULT);
  }

  /**
   * Creates a run from {@code spec}: seeds {@code elevation} as a zero grid, Voronoi {@code plates}
   * from {@code spec.seed()}, and CONSTANT {@code plate_velocity}. Completes Step 0 (no generation
   * tick).
   *
   * @param spec Step 0 world seed (must not be {@code null})
   */
  public static Engine create(WorldSpec spec) {
    Objects.requireNonNull(spec, "spec");
    Grid elevation = Grid.zeros(spec.width(), spec.height());
    Grid plates = Plates.seed(spec.width(), spec.height(), spec.seed());
    PlateVelocities velocities = PlateVelocities.seed(spec.seed());
    EngineConfig config =
        new EngineConfig(
            0L,
            List.of(),
            Map.of(
                WorldFields.ELEVATION, elevation,
                WorldFields.PLATES, plates,
                WorldFields.PLATE_VELOCITY, velocities));
    return Engine.create(config, setup());
  }
}
