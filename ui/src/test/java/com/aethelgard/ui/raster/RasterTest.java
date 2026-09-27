/*
 * File: ui/src/test/java/com/aethelgard/ui/raster/RasterTest.java
 * Purpose: Proves the colours the studio paints: water and land ramps, hill shading, and the plate stroke in the Plates and Overlay views
 * Audience: Agents / CI
 * Update when: ElevationRaster or the map's colour rules change
 */

package com.aethelgard.ui.raster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.world.fields.Grid;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RasterTest {

  // The ramps of the style guide, as the raster page gives them: heights and (R, G, B).
  private static final int[] OCEAN_E = {-64, -32, -16, -8, -1};
  private static final int[][] OCEAN_C = {{24, 64, 104}, {44, 98, 146}, {66, 132, 178}, {86, 162, 206}, {110, 190, 226}};
  private static final int[] LAND_E = {0, 12, 24, 40, 64};
  private static final int[][] LAND_C = {{150, 196, 120}, {186, 208, 132}, {214, 190, 124}, {232, 198, 140}, {255, 250, 236}};

  /** Proves F-068 FR-51 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Water is darker when deeper, and clamped at -64")
  void waterDarkensWithDepth() {
    int previous = Integer.MAX_VALUE;
    for (int depth : new int[] {-1, -4, -8, -16, -32, -48, -64}) {
      int rgb = flat(depth, MapLayer.ELEVATION);
      assertEquals(ramp(depth, OCEAN_E, OCEAN_C), rgb, "depth " + depth);
      assertTrue(brightness(rgb) < previous, "depth " + depth + " is darker");
      previous = brightness(rgb);
    }
    assertEquals(flat(-64, MapLayer.ELEVATION), flat(-200, MapLayer.ELEVATION), "below -64 looks like -64");
  }

  /** Proves F-068 FR-51 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("From 0 up, flat land takes the land ramp, clamped at 64")
  void landFollowsTheRamp() {
    for (int height : new int[] {0, 5, 12, 20, 24, 33, 40, 64}) {
      assertEquals(ramp(height, LAND_E, LAND_C), flat(height, MapLayer.ELEVATION), "height " + height);
    }
    assertEquals(pack(150, 196, 120), flat(0, MapLayer.ELEVATION), "sea level is the lowest green");
    assertEquals(flat(64, MapLayer.ELEVATION), flat(90, MapLayer.ELEVATION), "above 64 looks like 64");
  }

  /** Proves F-068 FR-51 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Land is hill-shaded by its rise over its west and north neighbours; water is never shaded")
  void onlyLandIsShaded() {
    // Centre cell (1,1) at height h; its west and north neighbours at h - 3 (lit) or h + 3 (shadowed).
    for (int[] c : new int[][] {{20, -3}, {20, 3}, {-20, -3}, {-20, 3}}) {
      int h = c[0];
      int n = h + c[1];
      Grid grid = new Grid(new int[][] {{n, n, n}, {n, h, h}, {n, h, h}});
      int painted = ElevationRaster.paint(grid, zeros(3, 3), MapLayer.ELEVATION).rgb(1, 1);
      if (h < 0) {
        assertEquals(ramp(h, OCEAN_E, OCEAN_C), painted, "water at " + h + " is not shaded");
      } else {
        int lit = Math.max(6, Math.min(18, 12 + 2 * (h - n) + 2 * (h - n)));
        assertEquals(shade(ramp(h, LAND_E, LAND_C), lit), painted, "land at " + h + " beside " + n);
      }
    }
  }

  /** Proves F-068 FR-51 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The same grids give the same colours in every view")
  void sameGridsSameColours() {
    Grid elevation = new Grid(new int[][] {{-30, -2, 0, 7}, {12, 40, 70, -64}, {3, 3, -9, 25}});
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}, {0, 2, 2, 1}, {0, 2, 1, 1}});
    for (MapLayer layer : MapLayer.values()) {
      assertEquals(ElevationRaster.paint(elevation, plates, layer), ElevationRaster.paint(elevation, plates, layer), layer.label());
    }
  }

  /** Proves F-068 FR-52 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The Plates view is muted gray, with a dark stroke on both sides of every contact, across the seam and the poles")
  void platesDrawABoldStrokeAlongEveryContact() {
    int gray = pack(88, 92, 96);
    int stroke = pack(36, 38, 42);
    // East | west halves meet at x=3|4, across the seam at x=7|0, and across both poles (each polar cell faces its antipode).
    // North | south halves meet between rows 2 and 3 only: across a pole each cell faces its own plate.
    for (Grid plates : List.of(grid(8, 6, (x, y) -> x < 4 ? 0 : 1), grid(8, 6, (x, y) -> y < 3 ? 0 : 1))) {
      ElevationRaster view = ElevationRaster.paint(zeros(8, 6), plates, MapLayer.PLATES);
      List<String> unstroked = new java.util.ArrayList<>();
      for (int y = 0; y < 6; y++) {
        for (int x = 0; x < 8; x++) {
          for (int[] d : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int[] n = neighbour(x, y, d[0], d[1], 8, 6);
            if (plates.get(x, y) != plates.get(n[0], n[1]) && view.rgb(x, y) != stroke) {
              unstroked.add("(" + x + "," + y + ") facing (" + n[0] + "," + n[1] + ")");
            }
          }
          if (distanceToOtherPlate(plates, x, y) >= 3) {
            assertEquals(gray, view.rgb(x, y), "interior (" + x + "," + y + ") is muted gray");
          }
        }
      }
      assertTrue(unstroked.isEmpty(), "contact cells without a stroke: " + unstroked);
    }
  }

  /** Proves F-068 FR-52 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The Overlay view draws the same stroke over height, a third as bright, and height elsewhere")
  void overlayDrawsTheStrokeOverHeight() {
    Grid elevation = grid(8, 6, (x, y) -> (x * 7 + y * 5) % 50 - 20);
    Grid plates = grid(8, 6, (x, y) -> x < 4 ? 0 : 1);
    ElevationRaster height = ElevationRaster.paint(elevation, plates, MapLayer.ELEVATION);
    ElevationRaster overlay = ElevationRaster.paint(elevation, plates, MapLayer.OVERLAY);
    ElevationRaster platesView = ElevationRaster.paint(elevation, plates, MapLayer.PLATES);
    int stroke = pack(36, 38, 42);

    for (int y = 0; y < 6; y++) {
      for (int x = 0; x < 8; x++) {
        int base = height.rgb(x, y);
        int expected = platesView.rgb(x, y) == stroke ? third(base) : base;
        assertEquals(expected, overlay.rgb(x, y), "(" + x + "," + y + ")");
      }
    }
  }

  // --- the colour rules of the raster page, written out ---

  private static int ramp(int e, int[] stops, int[][] colours) {
    e = Math.max(stops[0], Math.min(stops[stops.length - 1], e));
    int i = 0;
    while (i < stops.length - 2 && e > stops[i + 1]) {
      i++;
    }
    int[] rgb = new int[3];
    for (int ch = 0; ch < 3; ch++) {
      int from = colours[i][ch];
      int to = colours[i + 1][ch];
      rgb[ch] = from + (to - from) * (e - stops[i]) / (stops[i + 1] - stops[i]);
    }
    return pack(rgb[0], rgb[1], rgb[2]);
  }

  private static int shade(int rgb, int lit) {
    return pack(Math.min(255, r(rgb) * lit / 12), Math.min(255, g(rgb) * lit / 12), Math.min(255, b(rgb) * lit / 12));
  }

  private static int third(int rgb) {
    return pack(r(rgb) / 3, g(rgb) / 3, b(rgb) / 3);
  }

  private static int flat(int height, MapLayer layer) {
    Grid grid = grid(3, 3, (x, y) -> height);
    return ElevationRaster.paint(grid, zeros(3, 3), layer).rgb(1, 1);
  }

  private static int brightness(int rgb) {
    return r(rgb) + g(rgb) + b(rgb);
  }

  private static int pack(int r, int g, int b) {
    return (r << 16) | (g << 8) | b;
  }

  private static int r(int rgb) {
    return (rgb >> 16) & 0xFF;
  }

  private static int g(int rgb) {
    return (rgb >> 8) & 0xFF;
  }

  private static int b(int rgb) {
    return rgb & 0xFF;
  }

  private static Grid zeros(int w, int h) {
    return Grid.zeros(w, h);
  }

  /** The neighbour on the sphere: wrap in x; across a pole, the antipodal cell on the same row. */
  private static int[] neighbour(int x, int y, int dx, int dy, int w, int h) {
    int ny = y + dy;
    if (ny < 0 || ny >= h) {
      return new int[] {(x + w / 2) % w, y};
    }
    return new int[] {Math.floorMod(x + dx, w), ny};
  }

  /** Steps on the sphere from (x, y) to the nearest cell of another plate (breadth-first, capped at 4). */
  private static int distanceToOtherPlate(Grid plates, int x, int y) {
    int w = plates.width();
    int h = plates.height();
    int[][] dist = new int[h][w];
    for (int[] row : dist) {
      java.util.Arrays.fill(row, -1);
    }
    java.util.ArrayDeque<int[]> queue = new java.util.ArrayDeque<>();
    dist[y][x] = 0;
    queue.add(new int[] {x, y});
    while (!queue.isEmpty()) {
      int[] c = queue.poll();
      if (plates.get(c[0], c[1]) != plates.get(x, y)) {
        return dist[c[1]][c[0]];
      }
      if (dist[c[1]][c[0]] == 4) {
        continue;
      }
      for (int[] d : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
        int[] n = neighbour(c[0], c[1], d[0], d[1], w, h);
        if (dist[n[1]][n[0]] < 0) {
          dist[n[1]][n[0]] = dist[c[1]][c[0]] + 1;
          queue.add(n);
        }
      }
    }
    return Integer.MAX_VALUE;
  }

  private interface Cell {
    int at(int x, int y);
  }

  private static Grid grid(int w, int h, Cell cell) {
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        cells[y][x] = cell.at(x, y);
      }
    }
    return new Grid(cells);
  }
}
