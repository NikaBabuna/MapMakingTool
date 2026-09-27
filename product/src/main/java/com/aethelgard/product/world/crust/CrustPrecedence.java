/*
 * File: product/src/main/java/com/aethelgard/product/world/crust/CrustPrecedence.java
 * Purpose: COLLIDE loser from oceanic vs continental locker thickness
 * Audience: AreaFlux / GeometryApplication / Orogeny / MotionIntent / Subduction
 * Update when: Buoyancy precedence rule changes
 */

package com.aethelgard.product.world.crust;

import com.aethelgard.product.world.boundaries.BoundaryContact;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.interaction.AreaFlux;
import com.aethelgard.product.world.topology.SphereTopology;
import java.util.Objects;

/**
 * Per-contact-cell buoyancy. Thickness {@code >= T_land} is continental. Oceanic subducts at
 * COLLIDE; ocean–ocean keeps smaller-loses; continent–continent neither loses (a suture).
 */
public final class CrustPrecedence {

  /** Sentinel: continent–continent, no collide loser this Step. */
  public static final int NONE = -1;

  private CrustPrecedence() {}

  public static boolean isContinental(Lockers lockers, int lockerId) {
    Objects.requireNonNull(lockers, "lockers");
    return lockers.thickness(lockerId) >= Lockers.T_LAND;
  }

  public static boolean isContinentalCell(Grid occupancy, Lockers lockers, int x, int y) {
    Objects.requireNonNull(occupancy, "occupancy");
    return isContinental(lockers, occupancy.get(x, y));
  }

  /**
   * COLLIDE loser plate id, or {@link #NONE} when both contact cells are continental.
   *
   * @param occupancy standing occupancy (pre-move)
   */
  public static int collideLoser(
      BoundaryContact contact,
      Grid occupancy,
      Lockers lockers,
      PlateRegistry registry,
      int width,
      int height) {
    Objects.requireNonNull(contact, "contact");
    Objects.requireNonNull(occupancy, "occupancy");
    Objects.requireNonNull(lockers, "lockers");
    Objects.requireNonNull(registry, "registry");
    int[] b = SphereTopology.neighbor(contact.x(), contact.y(), contact.nx(), contact.ny(), width, height);
    boolean contA = isContinentalCell(occupancy, lockers, contact.x(), contact.y());
    boolean contB = isContinentalCell(occupancy, lockers, b[0], b[1]);
    if (contA && contB) {
      return NONE;
    }
    if (contA != contB) {
      return contA ? contact.plateB() : contact.plateA();
    }
    return AreaFlux.loser(contact.plateA(), contact.plateB(), registry);
  }
}
