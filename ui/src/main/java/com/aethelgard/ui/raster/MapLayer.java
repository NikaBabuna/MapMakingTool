/*
 * File: ui/src/main/java/com/aethelgard/ui/raster/MapLayer.java
 * Purpose: Map view layer switch (elevation / plates / overlay)
 * Audience: MapController / tests
 * Update when: Visible layers change
 */

package com.aethelgard.ui.raster;

/** Which grid the map paints. Switching layer does not advance the world. */
public enum MapLayer {
  ELEVATION("Elevation"),
  PLATES("Plates"),
  OVERLAY("Overlay");

  private final String label;

  MapLayer(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }
}
