/*
 * File: product/src/main/java/com/aethelgard/product/DiagnosticIds.java
 * Purpose: Well-known session diagnostic collector ids (G-009)
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

  /** Tectonics Sub-System phase timings (F-046). */
  public static final String PHASE_TRACE = "phase.trace";

  public static final String PHASE_INTERACTION = "phase.interaction";
  public static final String PHASE_INTEGRATE = "phase.integrate";
  public static final String PHASE_APPLY = "phase.apply";
  public static final String PHASE_OROGENY = "phase.orogeny";

  private DiagnosticIds() {}
}
