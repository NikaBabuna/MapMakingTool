/*
 * File: product/src/main/java/com/aethelgard/product/world/boundaries/BoundaryContact.java
 * Purpose: One undirected classified plate contact edge
 * Audience: Boundaries / tests
 * Update when: Contact record shape changes
 */

package com.aethelgard.product.world.boundaries;

import java.util.Objects;

/**
 * Undirected contact between cell {@code (x,y)} (plate A) and neighbor {@code (x+nx, y+ny)} (plate
 * B). Stored once for east ({@code nx=1,ny=0}) or south ({@code nx=0,ny=1}) only.
 */
public final class BoundaryContact {

  private final int x;
  private final int y;
  private final int nx;
  private final int ny;
  private final int plateA;
  private final int plateB;
  private final BoundaryKind kind;

  public BoundaryContact(int x, int y, int nx, int ny, int plateA, int plateB, BoundaryKind kind) {
    if (nx == 0 && ny == 0) {
      throw new IllegalArgumentException("neighbor offset must be non-zero");
    }
    this.x = x;
    this.y = y;
    this.nx = nx;
    this.ny = ny;
    this.plateA = plateA;
    this.plateB = plateB;
    this.kind = Objects.requireNonNull(kind, "kind");
  }

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public int nx() {
    return nx;
  }

  public int ny() {
    return ny;
  }

  public int plateA() {
    return plateA;
  }

  public int plateB() {
    return plateB;
  }

  public BoundaryKind kind() {
    return kind;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof BoundaryContact other)) {
      return false;
    }
    return x == other.x
        && y == other.y
        && nx == other.nx
        && ny == other.ny
        && plateA == other.plateA
        && plateB == other.plateB
        && kind == other.kind;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, nx, ny, plateA, plateB, kind);
  }

  @Override
  public String toString() {
    return "BoundaryContact{"
        + x
        + ","
        + y
        + "->"
        + nx
        + ","
        + ny
        + " "
        + plateA
        + "/"
        + plateB
        + " "
        + kind
        + "}";
  }
}
