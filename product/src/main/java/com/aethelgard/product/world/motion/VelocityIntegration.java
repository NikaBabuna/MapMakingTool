/*
 * File: product/src/main/java/com/aethelgard/product/world/motion/VelocityIntegration.java
 * Purpose: Sub-System — nudge plate velocities from standing motion_intent
 * Audience: Product tectonics EngineSystem
 * Update when: Edge-driven integrate rule changes
 */

package com.aethelgard.product.world.motion;

import com.aethelgard.engine.systems.SubSystem;
import com.aethelgard.engine.systems.SubSystemIo;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.interaction.MotionIntent;
import java.util.Objects;
import java.util.Set;

/**
 * Reads standing {@code plate_velocity}, {@code plate_registry}, and staged/pool {@code
 * motion_intent}; writes nudged STATIC {@code plate_velocity} + matching registry velocities.
 * GeometryApplication then inherits those velocities through fission. Wiki: {@code
 * docs/product/wiki/tectonics.md}.
 */
public final class VelocityIntegration implements SubSystem {

  @Override
  public String id() {
    return "integrate-velocity";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.PLATE_VELOCITY, WorldFields.PLATE_REGISTRY);
  }

  @Override
  public void execute(SubSystemIo io) {
    PlateVelocities standing = requireVelocities(io.readPool(WorldFields.PLATE_VELOCITY));
    PlateRegistry registry = requireRegistry(io.readPool(WorldFields.PLATE_REGISTRY));
    MotionIntent intent = requireIntent(readIntent(io));
    PlateVelocities nudged = integrate(standing, intent);
    io.write(WorldFields.PLATE_VELOCITY, nudged);
    io.write(WorldFields.PLATE_REGISTRY, PlateRegistry.withVelocities(registry, nudged));
  }

  /**
   * Per-axis {@code v' = clamp(v + sgn(intent), -1, 1)}; {@code sgn(0)=0}. If every plate would be
   * {@code (0,0)}, plate {@code 0} is forced to {@code (1,0)}.
   */
  public static PlateVelocities integrate(PlateVelocities standing, MotionIntent intent) {
    Objects.requireNonNull(standing, "standing");
    Objects.requireNonNull(intent, "intent");
    if (standing.count() != intent.plateCount()) {
      throw new IllegalArgumentException(
          "velocity count "
              + standing.count()
              + " != intent count "
              + intent.plateCount());
    }
    int n = standing.count();
    int[] vx = new int[n];
    int[] vy = new int[n];
    boolean anyMove = false;
    for (int i = 0; i < n; i++) {
      vx[i] = clampUnit(standing.vx(i) + Integer.signum(intent.ix(i)));
      vy[i] = clampUnit(standing.vy(i) + Integer.signum(intent.iy(i)));
      if (vx[i] != 0 || vy[i] != 0) {
        anyMove = true;
      }
    }
    if (!anyMove) {
      vx[0] = 1;
    }
    return new PlateVelocities(standing.seed(), vx, vy);
  }

  static int clampUnit(int value) {
    if (value < -1) {
      return -1;
    }
    if (value > 1) {
      return 1;
    }
    return value;
  }

  private static Object readIntent(SubSystemIo io) {
    Object staged = io.readStaging(WorldFields.MOTION_INTENT);
    if (staged != null) {
      return staged;
    }
    return io.readPool(WorldFields.MOTION_INTENT);
  }

  private static PlateVelocities requireVelocities(Object value) {
    if (value instanceof PlateVelocities velocities) {
      return velocities;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.PLATE_VELOCITY
            + "' must be PlateVelocities, was "
            + (value == null ? "null" : value.getClass().getName()));
  }

  private static PlateRegistry requireRegistry(Object value) {
    if (value instanceof PlateRegistry registry) {
      return registry;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.PLATE_REGISTRY
            + "' must be PlateRegistry, was "
            + (value == null ? "null" : value.getClass().getName()));
  }

  private static MotionIntent requireIntent(Object value) {
    if (value instanceof MotionIntent intent) {
      return intent;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.MOTION_INTENT
            + "' must be MotionIntent, was "
            + (value == null ? "null" : value.getClass().getName()));
  }
}
