/*
 * File: ui/src/main/java/com/aethelgard/ui/MapSpeed.java
 * Purpose: Play-tick periods for the map window
 * Audience: MapController / MapHost / tests
 * Update when: Speed table changes
 */

package com.aethelgard.ui;

/** Play speeds. Periods are milliseconds between ticks. Default is {@link #X1}. */
public enum MapSpeed {
  X1("1x", 250),
  X2("2x", 125),
  X4("4x", 62),
  /** As fast as the scheduler allows (1 ms tick; UI may treat 0 as synonym). */
  FASTEST("Fastest", 1);

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
}
