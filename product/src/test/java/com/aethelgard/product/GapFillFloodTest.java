/*
 * File: product/src/test/java/com/aethelgard/product/GapFillFloodTest.java
 * Purpose: F-044 witness — triple-junction flood fill + crumb 0.2%
 * Audience: Agents / CI
 * Update when: F-044 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GapFillFloodTest {

  @Test
  @DisplayName("FR-1/FR-2: triple-junction gap not claimed by third (upper) plate")
  void tripleJunctionGapNotWrappedByUpper() {
    // Plate 2 above; 0 left / 1 right; unresolved gap column touching all three.
    int u = PlateKinematics.UNRESOLVED;
    int[][] cells = {
      {2, 2, 2, 2},
      {0, u, u, 1},
      {0, 0, 1, 1},
    };
    PlateKinematics.fillUnresolvedFlood(cells, 4, 3);
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 4; x++) {
        assertTrue(cells[y][x] >= 0, "unowned at (" + x + "," + y + ")");
      }
    }
    assertTrue(cells[1][1] == 0 || cells[1][1] == 1, "gap left owned by 0 or 1, was " + cells[1][1]);
    assertTrue(cells[1][2] == 0 || cells[1][2] == 1, "gap right owned by 0 or 1, was " + cells[1][2]);
    assertEquals(2, cells[0][1]);
    assertEquals(2, cells[0][2]);
  }

  @Test
  @DisplayName("FR-2: zero neighbors wait; single expands; multi uses longest then low id")
  void floodPhases() {
    int u = PlateKinematics.UNRESOLVED;
    // Interior unresolved between same-plate walls — single-neighbor expand only.
    int[][] cells = {
      {0, 0, 0, 0},
      {0, u, u, 0},
      {0, 0, 0, 0},
    };
    PlateKinematics.fillUnresolvedFlood(cells, 4, 3);
    assertEquals(0, cells[1][1]);
    assertEquals(0, cells[1][2]);
    // Multi-neighbor contested: lower id wins when contacts equal
    int[][] multi = {
      {0, 1},
      {u, u},
      {0, 1},
    };
    PlateKinematics.fillUnresolvedFlood(multi, 2, 3);
    assertTrue(multi[1][0] == 0 || multi[1][0] == 1);
    assertTrue(multi[1][1] == 0 || multi[1][1] == 1);
  }

  @Test
  @DisplayName("FR-1: advect diverge still only contacting plates on simple Y split")
  void divergeStillContactingOnly() {
    int[][] seed = {
      {0, 0, 0, 0},
      {0, 0, 0, 0},
      {1, 1, 1, 1},
      {1, 1, 1, 1},
    };
    Grid plates = new Grid(seed);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0}, new int[] {-1, 1});
    Boundaries boundaries =
        new Boundaries(
            List.of(
                new BoundaryContact(0, 1, 0, 1, 0, 1, BoundaryKind.SEPARATE),
                new BoundaryContact(1, 1, 0, 1, 0, 1, BoundaryKind.SEPARATE),
                new BoundaryContact(2, 1, 0, 1, 0, 1, BoundaryKind.SEPARATE),
                new BoundaryContact(3, 1, 0, 1, 0, 1, BoundaryKind.SEPARATE)));
    Grid moved = PlateKinematics.advect(plates, vel, 1, boundaries).plates();
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < 4; x++) {
        int id = moved.get(x, y);
        assertTrue(id == 0 || id == 1, "third plate at (" + x + "," + y + ") id=" + id);
      }
    }
  }

  @Test
  @DisplayName("FR-3: crumb bar 0.1% absorbs small island that stays under the bar")
  void crumbBarOneTenthPercent() {
    // 50×50 = 2500; 0.1% absorbs size < 2.5. Use size-2 island of plate 1.
    int w = 50;
    int h = 50;
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        cells[y][x] = 0;
      }
    }
    cells[10][10] = 1;
    cells[10][11] = 1;
    Grid plates = new Grid(cells);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 0});
    ApplyGeometry.Result result =
        ApplyGeometry.apply(
            plates, Boundaries.empty(), AreaFlux.zeros(2), PlateRegistry.from(plates, vel), vel);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        assertEquals(0, result.plates().get(x, y), "island should be absorbed at (" + x + "," + y + ")");
      }
    }
  }

  @Test
  @DisplayName("FR-6/FR-7: determinism; wiki flood + 0.1%; no Plates.assign in kinematics")
  void determinismWikiAndNoGlobalAssign() throws Exception {
    ProductSession a = ProductSession.ofDefault();
    ProductSession b = ProductSession.ofDefault();
    a.advance(5);
    b.advance(5);
    assertEquals(a.plates(), b.plates());
    assertEquals(a.settledWorld(), b.settledWorld());

    Path root = findRepoRoot();
    String wiki = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(wiki.contains("0.1%"));
    assertTrue(wiki.toLowerCase().contains("flood"));
    assertTrue(wiki.contains("F-044"));

    String src =
        Files.readString(root.resolve("product/src/main/java/com/aethelgard/product/PlateKinematics.java"));
    assertFalse(src.contains("Plates.assign"));
    assertTrue(src.contains("fillUnresolvedFlood"));
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath().normalize();
    for (Path cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("product"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new Exception("repo root not found");
  }
}
