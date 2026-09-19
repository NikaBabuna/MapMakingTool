/*
 * File: product/src/main/java/com/aethelgard/product/BoundaryKind.java
 * Purpose: Classified plate-contact kinds for G-008 boundaries
 * Audience: Boundaries / tests
 * Update when: Boundary classification vocabulary changes
 */

package com.aethelgard.product;

/** Contact kind from relative motion across a plate edge. */
public enum BoundaryKind {
  SEPARATE,
  COLLIDE,
  PASS_BY
}
