/*
 * File: product/src/main/java/com/aethelgard/product/ProductCategories.java
 * Purpose: Product-authored event category tree (ADR-009)
 * Audience: ProductHost / emission policy / Systems
 * Update when: Product event categories change
 */

package com.aethelgard.product;

import com.aethelgard.engine.event.CategoryTree;

/**
 * Category paths owned by the product. The engine does not author this tree (ADR-009).
 */
public final class ProductCategories {

  /** Generation tick claimed by kinematics and tectonics Systems. */
  public static final String TECTONICS = "world/tectonics";

  private ProductCategories() {}

  /** Product category tree for a run. */
  public static CategoryTree tree() {
    return CategoryTree.of(TECTONICS);
  }
}
