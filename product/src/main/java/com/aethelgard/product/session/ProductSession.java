/*
 * File: product/src/main/java/com/aethelgard/product/session/ProductSession.java
 * Purpose: In-process owner of one product Engine run
 * Audience: UI / CLI / tests
 * Update when: Session lifecycle or serialized advance changes
 */

package com.aethelgard.product.session;

import com.aethelgard.engine.merge.FieldMergeType;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.system.EngineSystem;
import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.product.session.diagnostics.DiagnosticIds;
import com.aethelgard.product.session.diagnostics.DiagnosticsHub;
import com.aethelgard.product.session.diagnostics.PhaseTiming;
import com.aethelgard.product.world.ProductHost;
import com.aethelgard.product.world.boundaries.Boundaries;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.fields.WorldSpec;
import com.aethelgard.product.world.interaction.AreaFlux;
import com.aethelgard.product.world.interaction.MotionIntent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * One Aethelgard run. Owns the {@link Engine}. {@link #advance(int)} is serialized so UI and CLI
 * cannot interleave Steps. No Swing. Not a command parser — command language lives in {@code cli}.
 */
public final class ProductSession {

  private final WorldSpec spec;
  private final Engine engine;
  private final Object lock = new Object();
  private final DiagnosticsHub diagnostics = DiagnosticsHub.withDefaults();

  public ProductSession(WorldSpec spec) {
    this.spec = Objects.requireNonNull(spec, "spec");
    this.engine = ProductHost.create(spec);
  }

  /** Session for {@link WorldSpec#DEFAULT} (dump fixture). */
  public static ProductSession ofDefault() {
    return new ProductSession(WorldSpec.DEFAULT);
  }

  /** Session for {@link WorldSpec#VIEW} (map window). */
  public static ProductSession view() {
    return new ProductSession(WorldSpec.VIEW);
  }

  public WorldSpec spec() {
    return spec;
  }

  /** Controllable diagnostics hub (not Pool state). */
  public DiagnosticsHub diagnostics() {
    return diagnostics;
  }

  public int stepIndex() {
    synchronized (lock) {
      return engine.stepIndex();
    }
  }

  /** One generation Step. */
  public void advance() {
    advance(1);
  }

  /**
   * {@code n} additional generation Steps. {@code n == 0} is a no-op. Negative is illegal.
   */
  public void advance(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be >= 0, was " + n);
    }
    synchronized (lock) {
      for (int i = 0; i < n; i++) {
        long t0 = System.nanoTime();
        PhaseTiming.withHub(
            diagnostics,
            () -> {
              engine.advance(1);
              return null;
            });
        long dt = System.nanoTime() - t0;
        diagnostics.record(DiagnosticIds.ADVANCE_WALL, dt);
        Runtime rt = Runtime.getRuntime();
        diagnostics.record(DiagnosticIds.HEAP_USED, rt.totalMemory() - rt.freeMemory());
        diagnostics.record(DiagnosticIds.HEAP_MAX, rt.maxMemory());
      }
    }
  }

  public Grid elevation() {
    synchronized (lock) {
      return (Grid) engine.settled().field(WorldFields.ELEVATION);
    }
  }

  public Grid plates() {
    synchronized (lock) {
      return (Grid) engine.settled().field(WorldFields.PLATES);
    }
  }

  public PlateVelocities plateVelocities() {
    synchronized (lock) {
      return (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    }
  }

  public PlateRegistry plateRegistry() {
    synchronized (lock) {
      return (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    }
  }

  public Boundaries boundaries() {
    synchronized (lock) {
      return (Boundaries) engine.settled().field(WorldFields.BOUNDARIES);
    }
  }

  public AreaFlux areaFlux() {
    synchronized (lock) {
      return (AreaFlux) engine.settled().field(WorldFields.AREA_FLUX);
    }
  }

  public MotionIntent motionIntent() {
    synchronized (lock) {
      return (MotionIntent) engine.settled().field(WorldFields.MOTION_INTENT);
    }
  }

  /** Headless snapshot ({@link WorldDump}); same lock as {@link #advance(int)}. */
  public String settledWorld() {
    synchronized (lock) {
      return WorldDump.of(engine, spec);
    }
  }

  /** Declared Pool field names in schema order (construction). */
  public List<String> fieldNames() {
    synchronized (lock) {
      return List.copyOf(engine.fieldSchema().asMap().keySet());
    }
  }

  /** Field name → merge-type label (construction). */
  public Map<String, String> schemaTypes() {
    synchronized (lock) {
      FieldSchema schema = engine.fieldSchema();
      Map<String, String> out = new LinkedHashMap<>();
      for (var e : schema.asMap().entrySet()) {
        out.put(e.getKey(), mergeTypeName(e.getValue()));
      }
      return Map.copyOf(out);
    }
  }

  /** Registered System ids in registration order (construction). */
  public List<String> systemIds() {
    synchronized (lock) {
      List<String> ids = new ArrayList<>();
      for (EngineSystem system : engine.systems()) {
        ids.add(system.id());
      }
      return List.copyOf(ids);
    }
  }

  /**
   * Construction detail for one System: id, category path, sub-system ids.
   *
   * @throws IllegalArgumentException if id unknown
   */
  public String systemDetail(String systemId) {
    Objects.requireNonNull(systemId, "systemId");
    synchronized (lock) {
      for (EngineSystem system : engine.systems()) {
        if (system.id().equals(systemId)) {
          StringBuilder sb = new StringBuilder();
          sb.append("id=")
              .append(system.id())
              .append(" category=")
              .append(system.config().assignedCategory().path())
              .append('\n');
          sb.append("subsystems=");
          List<SubSystem> subs = system.config().subSystems();
          for (int i = 0; i < subs.size(); i++) {
            if (i > 0) {
              sb.append(',');
            }
            sb.append(subs.get(i).id());
          }
          return sb.toString();
        }
      }
      throw new IllegalArgumentException("unknown system: " + systemId);
    }
  }

  /** Settled Pool field value (information). */
  public Object field(String name) {
    Objects.requireNonNull(name, "name");
    synchronized (lock) {
      return engine.settled().field(name);
    }
  }

  private static String mergeTypeName(FieldMergeType type) {
    if (type instanceof FieldType ft) {
      return ft.name();
    }
    return type.getClass().getSimpleName();
  }
}
