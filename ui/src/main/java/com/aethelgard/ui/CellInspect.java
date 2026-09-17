/*
 * File: ui/src/main/java/com/aethelgard/ui/CellInspect.java
 * Purpose: Click-inspect snapshot of one map cell
 * Audience: MapController / sidebar / tests
 * Update when: Inspect fields change
 */

package com.aethelgard.ui;

/**
 * Values at one cell. Causal history is out of scope (later Goal).
 *
 * @param x column
 * @param y row
 * @param elevation cell height (may be negative)
 * @param plateId owning plate
 * @param vx plate east–west velocity in {-1,0,1}
 * @param vy plate north–south velocity in {-1,0,1}
 */
public record CellInspect(int x, int y, int elevation, int plateId, int vx, int vy) {}
