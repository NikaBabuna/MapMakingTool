/*
 * File: product/src/main/java/com/aethelgard/product/world/crust/ContinentalCollision.java
 * Purpose: Tectonics Sub-System — arc on ocean–ocean, suture on continent–continent
 * Audience: Product tectonics EngineSystem
 * Update when: Arc, suture, or thickness cap changes
 */

package com.aethelgard.product.world.crust;

import com.aethelgard.engine.systems.SubSystem;
import com.aethelgard.engine.systems.SubSystemIo;
import com.aethelgard.product.world.boundaries.Boundaries;
import com.aethelgard.product.world.boundaries.BoundaryContact;
import com.aethelgard.product.world.boundaries.BoundaryKind;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.interaction.AreaFlux;
import com.aethelgard.product.world.topology.SphereTopology;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * After margin relief, before isostasy. An ocean–ocean collision adds {@link #ARC} once to the
 * winner’s locker, and that result is at least {@link Lockers#T_LAND}. A continent–continent
 * collision adds {@link #SUTURE} once to each side. Writes stop at {@link #CAP}. A locker already
 * at the cap is left alone. Ocean–continent, pass-by, and splits add nothing. Plate ids are not
 * merged.
 */
public final class ContinentalCollision implements SubSystem {

  /** Ocean–ocean winner bump. One collision crosses {@link Lockers#T_LAND} from oceanic crust. */
  public static final int ARC = 8;

  /** Continent–continent bump on each side. */
  public static final int SUTURE = 4;

  /** Thickness writes do not pass this value. */
  public static final int CAP = 32;

  @Override
  public String id() {
    return "continental-collide";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(WorldFields.LOCKERS);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid occupancy = requireGrid(readField(io, WorldFields.OCCUPANCY), WorldFields.OCCUPANCY);
    Lockers lockers = requireLockers(readField(io, WorldFields.LOCKERS));
    Grid plates = requireGrid(readField(io, WorldFields.PLATES), WorldFields.PLATES);
    PlateVelocities velocities = requireVelocities(readField(io, WorldFields.PLATE_VELOCITY));
    io.write(WorldFields.LOCKERS, apply(occupancy, lockers, plates, velocities));
  }

  /** Traces post-move contacts, then thickens arc and suture lockers. */
  public static Lockers apply(
      Grid occupancy, Lockers lockers, Grid plates, PlateVelocities velocities) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(velocities, "velocities");
    return apply(
        occupancy,
        lockers,
        Boundaries.trace(plates, velocities),
        PlateRegistry.from(plates, velocities));
  }

  /**
   * One write per locker. Ocean–ocean uses smaller-loses (area tie → lower plate id). Both sides
   * already at {@link Lockers#T_LAND} suture. A locker already at {@link #CAP} is left alone.
   */
  public static Lockers apply(
      Grid occupancy, Lockers lockers, Boundaries boundaries, PlateRegistry registry) {
    Objects.requireNonNull(occupancy, "occupancy");
    Objects.requireNonNull(lockers, "lockers");
    Objects.requireNonNull(boundaries, "boundaries");
    Objects.requireNonNull(registry, "registry");
    int width = occupancy.width();
    int height = occupancy.height();
    int[] pre = lockers.thicknesses();
    int count = pre.length;
    boolean[] arc = new boolean[count];
    boolean[] suture = new boolean[count];
    for (BoundaryContact contact : boundaries.contacts()) {
      if (contact.kind() != BoundaryKind.COLLIDE) {
        continue;
      }
      int[] neighbor =
          SphereTopology.neighbor(
              contact.x(), contact.y(), contact.nx(), contact.ny(), width, height);
      int idA = occupancy.get(contact.x(), contact.y());
      int idB = occupancy.get(neighbor[0], neighbor[1]);
      if (idA < 0 || idB < 0 || idA >= count || idB >= count) {
        continue;
      }
      boolean continentA = pre[idA] >= Lockers.T_LAND;
      boolean continentB = pre[idB] >= Lockers.T_LAND;
      if (continentA && continentB) {
        suture[idA] = true;
        suture[idB] = true;
      } else if (!continentA && !continentB) {
        int winner = AreaFlux.winner(contact.plateA(), contact.plateB(), registry);
        int winnerId = winner == contact.plateA() ? idA : idB;
        arc[winnerId] = true;
      }
    }
    int[] next = Arrays.copyOf(pre, count);
    for (int id = 0; id < count; id++) {
      if (pre[id] >= CAP) {
        continue;
      }
      int delta = suture[id] ? SUTURE : arc[id] ? ARC : 0;
      if (delta == 0) {
        continue;
      }
      int thickened = pre[id] + delta;
      if (thickened > CAP) {
        thickened = CAP;
      }
      if (arc[id] && thickened < Lockers.T_LAND) {
        thickened = Lockers.T_LAND;
      }
      next[id] = thickened;
    }
    return new Lockers(next);
  }

  private static Object readField(SubSystemIo io, String field) {
    Object staged = io.readStaging(field);
    return staged != null ? staged : io.readPool(field);
  }

  private static Grid requireGrid(Object value, String field) {
    if (value instanceof Grid grid) {
      return grid;
    }
    throw new IllegalStateException(
        "field '" + field + "' must be Grid, was " + value.getClass().getName());
  }

  private static Lockers requireLockers(Object value) {
    if (value instanceof Lockers lockers) {
      return lockers;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.LOCKERS
            + "' must be Lockers, was "
            + value.getClass().getName());
  }

  private static PlateVelocities requireVelocities(Object value) {
    if (value instanceof PlateVelocities velocities) {
      return velocities;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.PLATE_VELOCITY
            + "' must be PlateVelocities, was "
            + value.getClass().getName());
  }
}
