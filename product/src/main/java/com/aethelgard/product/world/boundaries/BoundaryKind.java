/*
 * File: product/src/main/java/com/aethelgard/product/world/boundaries/BoundaryKind.java
 * Purpose: Classified plate-contact kinds of the boundaries
 * Audience: Boundaries / tests
 * Update when: Boundary classification vocabulary changes
 */

package com.aethelgard.product.world.boundaries;

/** Contact kind from relative motion across a plate edge. */
public enum BoundaryKind {
  SEPARATE,
  COLLIDE,
  PASS_BY
}
