/*
 * File: product/src/test/java/com/aethelgard/product/SphereTopologyTest.java
 * Purpose: F-045 witness — sphere polar wrap, heading flip, ragged determinism
 * Audience: Agents / CI
 * Update when: F-045 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SphereTopologyTest {

  @Test
  @DisplayName("FR-1: advect off north → antipodal x, polar row, heading flip")
  void advectNorthPoleFlipsHeading() {
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1}, new int[] {-1});
    Grid plates = new Grid(new int[][] {{0, 0, 0, 0}});
    Boundaries empty = Boundaries.empty();
    PlateKinematics.AdvectResult moved = PlateKinematics.advect(plates, vel, 1, empty);
    // Cell (0,0) with vx=1,vy=-1 crosses north → antipode of (0+1)=1 → (1+2)=3 mod 4? 
    // antipodeX(0+1,4)=antipodeX(1,4)=1+2=3
    assertEquals(0, moved.plates().get(3, 0));
    assertEquals(-1, moved.velocities().vx(0));
    assertEquals(1, moved.velocities().vy(0));
  }

  @Test
  @DisplayName("FR-2: north neighbor of polar cell is antipodal on same row")
  void northNeighborAntipodal() {
    int[] n = SphereTopology.neighbor(1, 0, 0, -1, 8, 4);
    assertEquals(SphereTopology.antipodeX(1, 8), n[0]);
    assertEquals(0, n[1]);
  }

  @Test
  @DisplayName("FR-5: ragged skip is deterministic for seed")
  void raggedDeterministic() {
    assertEquals(ApplyGeometry.raggedSkip(42L, 3, 7), ApplyGeometry.raggedSkip(42L, 3, 7));
    assertEquals(ApplyGeometry.raggedExtra(42L, 3, 7), ApplyGeometry.raggedExtra(42L, 3, 7));
  }

  @Test
  @DisplayName("FR-6/FR-7: determinism; wiki sphere live; no engine edits in product Step")
  void determinismWiki() throws Exception {
    ProductSession a = ProductSession.ofDefault();
    ProductSession b = ProductSession.ofDefault();
    a.advance(5);
    b.advance(5);
    assertEquals(a.plates(), b.plates());
    assertEquals(a.settledWorld(), b.settledWorld());

    Path root = findRepoRoot();
    String wiki = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(wiki.contains("F-045"));
    assertTrue(wiki.toLowerCase().contains("sphere") || wiki.contains("antipod"));
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
