/*
 * File: product/src/test/java/com/aethelgard/product/OrogenyTest.java
 * Purpose: F-021 witness — converge / diverge / transform, torus, standing plates
 * Audience: Agents / CI
 * Update when: F-021 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrogenyTest {

  @Test
  @DisplayName("FR-2: 4x1 torus — leading converge +1, trailing diverge -1")
  void torusLeadingAndTrailing() {
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}});
    PlateVelocities vel =
        new PlateVelocities(0L, new int[] {0, 1}, new int[] {0, 0});
    assertEquals(1, independentDelta(plates, vel, 0, 0));
    assertEquals(-1, independentDelta(plates, vel, 1, 0));
    assertEquals(-1, independentDelta(plates, vel, 2, 0));
    assertEquals(1, independentDelta(plates, vel, 3, 0));
    assertEquals(
        new Grid(new int[][] {{1, -1, -1, 1}}),
        Orogeny.apply(plates, vel, Grid.zeros(4, 1)));
  }

  @Test
  @DisplayName("FR-2: shear is transform 0; same velocity is 0; converge wins over diverge")
  void transformAndConvergeWins() {
    Grid plates = new Grid(new int[][] {{0, 1}});
    PlateVelocities shear =
        new PlateVelocities(0L, new int[] {0, 0}, new int[] {1, -1});
    assertEquals(0, independentDelta(plates, shear, 0, 0));
    assertEquals(0, independentDelta(plates, shear, 1, 0));

    PlateVelocities locked =
        new PlateVelocities(0L, new int[] {1, 1}, new int[] {0, 0});
    assertEquals(0, independentDelta(plates, locked, 0, 0));

    // 2-wide torus: east diverge and west converge on the same neighbor pair → +1
    PlateVelocities split =
        new PlateVelocities(0L, new int[] {0, 1}, new int[] {0, 0});
    assertEquals(1, independentDelta(plates, split, 0, 0));
    assertEquals(1, Orogeny.delta(plates, split, 0, 0));
  }

  @Test
  @DisplayName("FR-3: interior 0; negatives accumulate; 1x1 stays 0")
  void interiorNegativeAndTiny() {
    Grid plates = new Grid(new int[][] {{0, 0, 0}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1}, new int[] {0});
    Grid zeros = Grid.zeros(3, 1);
    assertEquals(zeros, Orogeny.apply(plates, vel, zeros));

    Grid rift = new Grid(new int[][] {{0, 0, 1, 1}});
    PlateVelocities pull =
        new PlateVelocities(0L, new int[] {0, 1}, new int[] {0, 0});
    Grid once = Orogeny.apply(rift, pull, Grid.zeros(4, 1));
    Grid twice = Orogeny.apply(rift, pull, once);
    assertEquals(-2, twice.get(1, 0));
    assertEquals(2, twice.get(0, 0));

    Engine tiny = ProductHost.create(new WorldSpec(1, 1, 0L));
    tiny.advance(5);
    assertEquals(0, ((Grid) tiny.settled().field(WorldFields.ELEVATION)).get(0, 0));
  }

  @Test
  @DisplayName("FR-1/FR-4: engine uses standing orogeny; kinematics unused this Step")
  void standingOrogenyOnEngine() {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateVelocities vel = (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    Grid elevation = Grid.zeros(8, 8);
    engine.advance(3);
    for (int g = 1; g <= 3; g++) {
      elevation = Orogeny.apply(plates, vel, elevation);
      plates = PlateKinematics.advect(plates, vel, g);
    }
    assertEquals(elevation, engine.settled().field(WorldFields.ELEVATION));
    assertEquals(plates, engine.settled().field(WorldFields.PLATES));
    assertEquals(vel, engine.settled().field(WorldFields.PLATE_VELOCITY));
  }

  @Test
  @DisplayName("FR-5: same seed matches; DEFAULT golden; VIEW 1920×1080 seed 0 (no VIEW advance)")
  void determinismDumpAndView() throws Exception {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(WorldDump.CANONICAL_STEPS);
    b.advance(WorldDump.CANONICAL_STEPS);
    assertEquals(WorldDump.of(a, spec), WorldDump.of(b, spec));
    String dump = WorldDump.of(a, spec);
    String golden =
        Files.readString(
                findRepoRoot().resolve("product/src/test/resources/worlds/default-n3.txt"),
                StandardCharsets.UTF_8)
            .replace("\r\n", "\n");
    assertEquals(golden, dump);

    ProductSession view = ProductSession.view();
    assertEquals(1920, view.spec().width());
    assertEquals(1080, view.spec().height());
    assertEquals(0L, view.spec().seed());
    assertEquals(0, view.stepIndex());
    assertEquals(1920, view.elevation().width());
    assertEquals(1080, view.elevation().height());
  }

  @Test
  @DisplayName("FR-1: wiki records orogeny; CollisionUplift is gone")
  void wikiAndRetiredUplift() throws Exception {
    Path root = findRepoRoot();
    String wiki = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    assertTrue(wiki.toLowerCase().contains("orogeny") || wiki.toLowerCase().contains("converge"));
    assertTrue(wiki.contains("n ·") || wiki.contains("n · (v") || wiki.toLowerCase().contains("closing"));
    assertTrue(wiki.toLowerCase().contains("toroid"));
    assertTrue(wiki.contains("−1") || wiki.contains("-1"));
    assertFalse(wiki.contains("if any **4-neighbor** (north, east, south, west) has a **different plate id**, that cell’s elevation increases by **1**."));
    assertFalse(
        Files.exists(
            root.resolve("product/src/main/java/com/aethelgard/product/CollisionUplift.java")));
    assertFalse(
        Files.readString(root.resolve("product/src/main/java/com/aethelgard/product/Orogeny.java"))
            .contains("javax.swing"));
  }

  private static int independentDelta(Grid plates, PlateVelocities velocities, int x, int y) {
    int a = plates.get(x, y);
    boolean converge = false;
    boolean diverge = false;
    int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    for (int[] d : dirs) {
      int nx = d[0];
      int ny = d[1];
      int bx = Math.floorMod(x + nx, plates.width());
      int by = Math.floorMod(y + ny, plates.height());
      int b = plates.get(bx, by);
      if (a == b) {
        continue;
      }
      int closing =
          nx * (velocities.vx(a) - velocities.vx(b)) + ny * (velocities.vy(a) - velocities.vy(b));
      if (closing > 0) {
        converge = true;
      } else if (closing < 0) {
        diverge = true;
      }
    }
    if (converge) {
      return 1;
    }
    if (diverge) {
      return -1;
    }
    return 0;
  }

  private static Path findRepoRoot() {
    var dir = Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("engine"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found");
  }
}
