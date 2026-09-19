/*
 * File: product/src/test/java/com/aethelgard/product/RidgeAccretionTest.java
 * Purpose: F-043 witness — SEPARATE gaps filled only by contacting plates
 * Audience: Agents / CI
 * Update when: F-043 FRs change
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

class RidgeAccretionTest {

  @Test
  @DisplayName("FR-1/FR-4: two plates diverge — gap owned only by those two; full cover")
  void divergeGapOnlyContactingPlates() {
    // Columns of plate 0 (top) and plate 1 (bottom); velocities pull apart on Y.
    int[][] cells = {
      {0, 0, 0, 0},
      {0, 0, 0, 0},
      {1, 1, 1, 1},
      {1, 1, 1, 1},
    };
    Grid plates = new Grid(cells);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0}, new int[] {-1, 1});
    Boundaries boundaries =
        new Boundaries(
            List.of(
                new BoundaryContact(0, 1, 0, 1, 0, 1, BoundaryKind.SEPARATE),
                new BoundaryContact(1, 1, 0, 1, 0, 1, BoundaryKind.SEPARATE),
                new BoundaryContact(2, 1, 0, 1, 0, 1, BoundaryKind.SEPARATE),
                new BoundaryContact(3, 1, 0, 1, 0, 1, BoundaryKind.SEPARATE)));

    Grid moved = PlateKinematics.advect(plates, vel, 1, boundaries).plates();

    assertEquals(4, moved.height());
    assertEquals(4, moved.width());
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < 4; x++) {
        int id = moved.get(x, y);
        assertTrue(id == 0 || id == 1, "third plate at (" + x + "," + y + ") id=" + id);
      }
    }
  }

  @Test
  @DisplayName("FR-2: advect source does not call Plates.assign for leftovers")
  void noGlobalAssignInAdvect() throws Exception {
    String src =
        Files.readString(
            findRepoRoot().resolve("product/src/main/java/com/aethelgard/product/PlateKinematics.java"));
    assertFalse(src.contains("Plates.assign"));
    assertTrue(src.contains("fillUnresolvedFlood") || src.toLowerCase().contains("flood"));
  }

  @Test
  @DisplayName("FR-3: ApplyGeometry has no nearestOwner global backstop")
  void noNearestOwnerInApply() throws Exception {
    String src =
        Files.readString(
            findRepoRoot().resolve("product/src/main/java/com/aethelgard/product/ApplyGeometry.java"));
    assertFalse(src.contains("nearestOwner"));
  }

  @Test
  @DisplayName("FR-5/FR-6: determinism + wiki ridge live")
  void determinismAndWiki() throws Exception {
    ProductSession a = ProductSession.ofDefault();
    ProductSession b = ProductSession.ofDefault();
    a.advance(5);
    b.advance(5);
    assertEquals(a.plates(), b.plates());
    assertEquals(a.settledWorld(), b.settledWorld());

    String wiki =
        Files.readString(findRepoRoot().resolve("docs/product/wiki/tectonics.md"));
    assertTrue(wiki.toLowerCase().contains("ridge"));
    assertTrue(wiki.contains("F-043") || wiki.contains("live"));
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
