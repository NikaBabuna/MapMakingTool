/*
 * File: ui/src/main/java/com/aethelgard/ui/LegendEntry.java
 * Purpose: One legend swatch + label for the current map layer
 * Audience: MapController / MapFrame / tests
 * Update when: Legend model changes
 */

package com.aethelgard.ui;

/**
 * Headless legend row.
 *
 * @param rgb packed {@code 0xRRGGBB}
 * @param label display text
 */
public record LegendEntry(int rgb, String label) {}
