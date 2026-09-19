/*
 * File: product/src/test/java/com/aethelgard/product/EarthFissionTest.java
 * Purpose: F-044 witness — Earth-like fission + thin absorb
 * Audience: Agents / CI
 * Update when: F-044 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EarthFissionTest {

  @Test
  @DisplayName("FR-1/FR-2: substantial split — largest keeps parent id; child inherits velocity")
  void largestKeepsIdAndInherits() {
    // 20×10: plate 0 left/right blocks separated by plate 1; seal wrap so 0 cannot join via X.
    int w = 20;
    int h = 10;
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (x == 0 || x == w - 1 || (x >= 9 && x <= 10)) {
          cells[y][x] = 1;
        } else if (x < 9) {
          cells[y][x] = 0;
        } else {
          cells[y][x] = 0;
        }
      }
    }
    Grid plates = new Grid(cells);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1, 0}, new int[] {0, 0});
    ApplyGeometry.Result result =
        ApplyGeometry.apply(
            plates, Boundaries.empty(), AreaFlux.zeros(2), PlateRegistry.from(plates, vel), vel);
    assertTrue(result.registry().count() >= 3, "count=" + result.registry().count());
    assertTrue(result.registry().area(0) >= ApplyGeometry.minNewPlateArea(w, h));
    int withVx1 = 0;
    for (int i = 0; i < result.velocities().count(); i++) {
      if (result.velocities().vx(i) == 1 && result.registry().area(i) > 0) {
        withVx1++;
      }
    }
    assertTrue(withVx1 >= 2, "parent + child should inherit vx=1");
  }

  @Test
  @DisplayName("FR-3/FR-6: thin ribbon does not remain its own plate")
  void thinRibbonAbsorbed() {
    // Large world so min-new-plate (0.5%) exceeds ribbon area; thin scrap absorbs.
    int w = 100;
    int h = 40;
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (x < 48) {
          cells[y][x] = 0;
        } else if (x > 48) {
          cells[y][x] = 1;
        } else {
          cells[y][x] = (y >= 10 && y <= 25) ? 2 : 0;
        }
      }
    }
    Grid plates = new Grid(cells);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0, 0}, new int[] {0, 0, 0});
    ApplyGeometry.Result result =
        ApplyGeometry.apply(
            plates, Boundaries.empty(), AreaFlux.zeros(3), PlateRegistry.from(plates, vel), vel);
    Set<Integer> ids = new HashSet<>();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        ids.add(result.plates().get(x, y));
      }
    }
    assertTrue(ids.size() <= 2, "ribbon plate must be absorbed, ids=" + ids);
    assertEquals(w * h, result.registry().totalArea());
  }

  @Test
  @DisplayName("FR-7: wiki documents Earth-like fission thresholds")
  void wikiLocks() throws Exception {
    String wiki =
        Files.readString(findRepoRoot().resolve("docs/product/wiki/tectonics.md"));
    assertTrue(wiki.contains("0.5%") || wiki.contains("F-044"));
    assertTrue(wiki.toLowerCase().contains("largest") || wiki.contains("span"));
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath().normalize();
    for (Path cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new Exception("repo root not found");
  }
}
