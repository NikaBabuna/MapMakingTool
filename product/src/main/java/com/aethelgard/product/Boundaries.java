/*
 * File: product/src/main/java/com/aethelgard/product/Boundaries.java
 * Purpose: STATIC classified plate-contact list
 * Audience: ProductHost / TraceBoundaries / tests
 * Update when: Boundary field shape or trace rule changes
 */

package com.aethelgard.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classified plate contacts. Wiki: {@code docs/product/wiki/tectonics.md}. Geometry is a
 * cylinder: wrap X; Y does not wrap (polar edges).
 */
public final class Boundaries {

  private final List<BoundaryContact> contacts;

  public Boundaries(List<BoundaryContact> contacts) {
    Objects.requireNonNull(contacts, "contacts");
    this.contacts = List.copyOf(contacts);
  }

  public static Boundaries empty() {
    return new Boundaries(List.of());
  }

  /**
   * Trace undirected foreign contacts (east + south only) and classify from standing velocities.
   * North/west are omitted to avoid doubles. Y neighbors off-map are skipped (no vertical wrap).
   */
  public static Boundaries trace(Grid plates, PlateVelocities velocities) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(velocities, "velocities");
    int width = plates.width();
    int height = plates.height();
    List<BoundaryContact> out = new ArrayList<>();
    int[][] dirs = {{1, 0}, {0, 1}};
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int plateA = plates.get(x, y);
        for (int[] d : dirs) {
          int nx = d[0];
          int ny = d[1];
          int bx = Math.floorMod(x + nx, width);
          int by = y + ny;
          if (by < 0 || by >= height) {
            continue;
          }
          int plateB = plates.get(bx, by);
          if (plateA == plateB) {
            continue;
          }
          BoundaryKind kind = classify(plateA, plateB, velocities, nx, ny);
          out.add(new BoundaryContact(x, y, nx, ny, plateA, plateB, kind));
        }
      }
    }
    return new Boundaries(out);
  }

  /** Same rule as {@link Orogeny#closing}: {@code n · (vA − vB)}. */
  public static BoundaryKind classify(
      int plateA, int plateB, PlateVelocities velocities, int nx, int ny) {
    int closing = Orogeny.closing(plateA, plateB, velocities, nx, ny);
    if (closing > 0) {
      return BoundaryKind.COLLIDE;
    }
    if (closing < 0) {
      return BoundaryKind.SEPARATE;
    }
    return BoundaryKind.PASS_BY;
  }

  public List<BoundaryContact> contacts() {
    return Collections.unmodifiableList(contacts);
  }

  public int size() {
    return contacts.size();
  }

  public int count(BoundaryKind kind) {
    Objects.requireNonNull(kind, "kind");
    int n = 0;
    for (BoundaryContact c : contacts) {
      if (c.kind() == kind) {
        n++;
      }
    }
    return n;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Boundaries other)) {
      return false;
    }
    return contacts.equals(other.contacts);
  }

  @Override
  public int hashCode() {
    return contacts.hashCode();
  }
}
