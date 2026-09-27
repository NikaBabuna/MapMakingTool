/*
 * File: product/src/main/java/com/aethelgard/product/world/ProductHost.java
 * Purpose: Product factory — construct Engine via EngineSetup
 * Audience: Product callers / tests
 * Update when: Product EngineSetup wiring changes
 */

package com.aethelgard.product.world;

import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.system.EngineSystem;
import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SystemConfig;
import com.aethelgard.product.session.diagnostics.DiagnosticIds;
import com.aethelgard.product.session.diagnostics.TimingSubSystem;
import com.aethelgard.product.world.boundaries.Boundaries;
import com.aethelgard.product.world.boundaries.BoundaryTracing;
import com.aethelgard.product.world.crust.ContinentalCollision;
import com.aethelgard.product.world.crust.Isostasy;
import com.aethelgard.product.world.crust.MarginRelief;
import com.aethelgard.product.world.crust.Orogeny;
import com.aethelgard.product.world.crust.RidgeCreation;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.Occupancy;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.Plates;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.fields.WorldSpec;
import com.aethelgard.product.world.interaction.AreaFlux;
import com.aethelgard.product.world.interaction.BoundaryInteraction;
import com.aethelgard.product.world.interaction.MotionIntent;
import com.aethelgard.product.world.motion.GeometryApplication;
import com.aethelgard.product.world.motion.VelocityIntegration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Product entry for constructing an {@link Engine}. Setup is owned here so later Steps can add
 * schema and Systems without callers talking to engine defaults directly.
 *
 * <p>Schema: elevation / plates / plate_registry / boundaries / area_flux / motion_intent /
 * plate_velocity / occupancy / lockers are STATIC. One tectonics System claims {@code
 * world/tectonics} after Step 0 and runs BoundaryTracing → BoundaryInteraction →
 * VelocityIntegration → GeometryApplication → Orogeny (locker stamps) → RidgeCreation →
 * MarginRelief → ContinentalCollision → Isostasy. GeometryApplication remaps occupancy
 * and runs Subduction corrections.
 */
public final class ProductHost {

  /** Provenance / claimer id for the tectonics System (owns the full plate pipeline). */
  public static final String TECTONICS_SYSTEM_ID = "tectonics";

  /**
   * Legacy id kept for tests/docs that still mention kinematics; advection runs inside {@link
   * GeometryApplication}.
   */
  public static final String KINEMATICS_SYSTEM_ID = "kinematics";

  private ProductHost() {}

  /**
   * Product {@link EngineSetup}: world fields, category tree, generation tick, tectonics System.
   */
  public static EngineSetup setup() {
    CategoryTree tree = ProductCategories.tree();
    VelocityIntegration velocityIntegration = new VelocityIntegration();
    GeometryApplication geometryApplication = new GeometryApplication();
    SubSystem timedIntegrate =
        new TimingSubSystem(velocityIntegration, DiagnosticIds.PHASE_INTEGRATE);
    SubSystem timedApply = new TimingSubSystem(geometryApplication, DiagnosticIds.PHASE_APPLY);
    SubSystem timedOrogeny = new TimingSubSystem(new Orogeny(), DiagnosticIds.PHASE_OROGENY);
    RidgeCreation ridgeCreation = new RidgeCreation();
    MarginRelief marginRelief = new MarginRelief();
    ContinentalCollision continentalCollision = new ContinentalCollision();
    SubSystem timedIsostasy =
        new TimingSubSystem(new Isostasy(), DiagnosticIds.PHASE_ISOSTASY);
    EngineSystem tectonics =
        new EngineSystem(
            new SystemConfig(
                TECTONICS_SYSTEM_ID,
                tree.get(ProductCategories.TECTONICS),
                List.of(
                    new TimingSubSystem(new BoundaryTracing(), DiagnosticIds.PHASE_TRACE),
                    new TimingSubSystem(
                        new BoundaryInteraction(), DiagnosticIds.PHASE_INTERACTION),
                    timedIntegrate,
                    timedApply,
                    timedOrogeny,
                    ridgeCreation,
                    marginRelief,
                    continentalCollision,
                    timedIsostasy),
                conflict -> {
                  List<SubSystem> preferred =
                      List.of(
                          timedIntegrate,
                          timedApply,
                          timedOrogeny,
                          ridgeCreation,
                          marginRelief,
                          continentalCollision);
                  List<SubSystem> ordered = new ArrayList<>();
                  for (SubSystem sub : preferred) {
                    if (conflict.contains(sub)) {
                      ordered.add(sub);
                    }
                  }
                  return ordered;
                }));
    FieldSchema schema =
        FieldSchema.of(
            Map.of(
                WorldFields.ELEVATION, FieldType.STATIC,
                WorldFields.PLATES, FieldType.STATIC,
                WorldFields.PLATE_REGISTRY, FieldType.STATIC,
                WorldFields.BOUNDARIES, FieldType.STATIC,
                WorldFields.AREA_FLUX, FieldType.STATIC,
                WorldFields.MOTION_INTENT, FieldType.STATIC,
                WorldFields.PLATE_VELOCITY, FieldType.STATIC,
                WorldFields.OCCUPANCY, FieldType.STATIC,
                WorldFields.LOCKERS, FieldType.STATIC));
    return new EngineSetup(
        tree,
        List.of(),
        List.of(tectonics),
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
   * Creates a run from {@code spec}: seeds zero elevation, B1 nearest-site plates (N=12–24),
   * occupancy keys + oceanic lockers, plate_registry, boundaries, area_flux, motion_intent, and
   * STATIC plate_velocity. Completes Step 0.
   *
   * @param spec Step 0 world seed (must not be {@code null})
   */
  public static Engine create(WorldSpec spec) {
    Objects.requireNonNull(spec, "spec");
    Grid elevation = Grid.zeros(spec.width(), spec.height());
    Grid plates = Plates.seed(spec.width(), spec.height(), spec.seed());
    Grid occupancy = Occupancy.seed(spec.width(), spec.height());
    Lockers lockers = Lockers.oceanic(Occupancy.count(spec.width(), spec.height()));
    PlateVelocities velocities = PlateVelocities.seed(spec.seed());
    PlateRegistry registry = PlateRegistry.from(plates, velocities);
    Boundaries boundaries = Boundaries.trace(plates, velocities);
    AreaFlux areaFlux = AreaFlux.from(boundaries, registry);
    MotionIntent motionIntent = MotionIntent.from(boundaries, registry);
    EngineConfig config =
        new EngineConfig(
            0L,
            List.of(),
            Map.of(
                WorldFields.ELEVATION, elevation,
                WorldFields.PLATES, plates,
                WorldFields.PLATE_REGISTRY, registry,
                WorldFields.BOUNDARIES, boundaries,
                WorldFields.AREA_FLUX, areaFlux,
                WorldFields.MOTION_INTENT, motionIntent,
                WorldFields.PLATE_VELOCITY, velocities,
                WorldFields.OCCUPANCY, occupancy,
                WorldFields.LOCKERS, lockers));
    return Engine.create(config, setup());
  }
}
