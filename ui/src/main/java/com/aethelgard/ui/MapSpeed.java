/*
 * File: ui/src/main/java/com/aethelgard/ui/MapSpeed.java
 * Purpose: Play-tick periods for the map window
 * Audience: MapController / tests
 * Update when: Speed table changes
 */

package com.aethelgard.ui;

/** Play speeds. Periods are milliseconds between ticks. Default is {@link #NORMAL}. */
public enum MapSpeed {
  SLOW("Slow", 1000),
  NORMAL("Normal", 250),
  FAST("Fast", 100);

  private final String label;
  private final int periodMillis;

  MapSpeed(String label, int periodMillis) {
    this.label = label;
    this.periodMillis = periodMillis;
  }

  public String label() {
    return label;
  }

  public int periodMillis() {
    return periodMillis;
  }

  @Override
  public String toString() {
    return label;
  }
}
