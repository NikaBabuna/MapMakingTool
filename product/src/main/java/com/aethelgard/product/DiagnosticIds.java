/*
 * File: product/src/main/java/com/aethelgard/product/DiagnosticIds.java
 * Purpose: Well-known session diagnostic collector ids (G-009 / F-042)
 * Audience: ProductSession / MapController / CLI
 * Update when: Built-in collector ids change
 */

package com.aethelgard.product;

/** Built-in collector identifiers for {@link DiagnosticsHub}. */
public final class DiagnosticIds {

  public static final String ADVANCE_WALL = "advance.wall";
  public static final String HEAP_USED = "heap.used";
  public static final String HEAP_MAX = "heap.max";
  public static final String PAINT_WALL = "paint.wall";

  private DiagnosticIds() {}
}
