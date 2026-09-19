/*
 * File: product/src/test/java/com/aethelgard/product/OrogenyTest.java
 * Purpose: F-038 witness — elevation from standing classified boundaries
 * Audience: Agents / CI
 * Update when: F-038 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrogenyTest {

  @Test
  @DisplayName("FR-2: COLLIDE winner +1 / loser -1; SEPARATE both -1; PASS_BY 0")
  void boundaryKindsRelief() {
    // 1×2 stack — single south contact (no X-wrap double edge)
    Grid plates = new Grid(new int[][] {{0}, {1}});
    PlateVelocities collide =
        new PlateVelocities(0L, new int[] {0, 0}, new int[] {1, 0});
    PlateRegistry reg = PlateRegistry.from(plates, collide);
    Boundaries bounds = Boundaries.trace(plates, collide);
    assertEquals(1, bounds.contacts().size());
    assertEquals(BoundaryKind.COLLIDE, bounds.contacts().get(0).kind());
    assertEquals(0, AreaFlux.loser(0, 1, reg)); // lower id loses on equal area
    assertEquals(-1, Orogeny.delta(0, 0, plates, bounds, reg)); // loser (top)
    assertEquals(1, Orogeny.delta(0, 1, plates, bounds, reg)); // winner (bottom)
    assertEquals(
        new Grid(new int[][] {{-1}, {1}}), Orogeny.apply(plates, bounds, reg, Grid.zeros(1, 2)));

    PlateVelocities separate =
        new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 1});
    Boundaries sep = Boundaries.trace(plates, separate);
    assertEquals(BoundaryKind.SEPARATE, sep.contacts().get(0).kind());
    PlateRegistry sepReg = PlateRegistry.from(plates, separate);
    assertEquals(-1, Orogeny.delta(0, 0, plates, sep, sepReg));
    assertEquals(-1, Orogeny.delta(0, 1, plates, sep, sepReg));

    PlateVelocities shear =
        new PlateVelocities(0L, new int[] {1, -1}, new int[] {0, 0});
    Boundaries pass = Boundaries.trace(plates, shear);
    assertEquals(BoundaryKind.PASS_BY, pass.contacts().get(0).kind());
    PlateRegistry shearReg = PlateRegistry.from(plates, shear);
    assertEquals(0, Orogeny.delta(0, 0, plates, pass, shearReg));
    assertEquals(0, Orogeny.delta(0, 1, plates, pass, shearReg));
  }

  @Test
  @DisplayName("FR-3: interior 0; negatives accumulate; 1x1 stays 0")
  void interiorNegativeAndTiny() {
    Grid plates = new Grid(new int[][] {{0, 0, 0}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1}, new int[] {0});
    PlateRegistry reg = PlateRegistry.from(plates, vel);
    Boundaries empty = Boundaries.trace(plates, vel);
    assertEquals(0, empty.contacts().size());
    Grid zeros = Grid.zeros(3, 1);
    assertEquals(zeros, Orogeny.apply(plates, empty, reg, zeros));

    Grid rift = new Grid(new int[][] {{0}, {1}});
    PlateVelocities pull = new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 1});
    PlateRegistry riftReg = PlateRegistry.from(rift, pull);
    Boundaries sep = Boundaries.trace(rift, pull);
    Grid once = Orogeny.apply(rift, sep, riftReg, Grid.zeros(1, 2));
    Grid twice = Orogeny.apply(rift, sep, riftReg, once);
    assertEquals(-2, twice.get(0, 0));
    assertEquals(-2, twice.get(0, 1));

    Engine tiny = ProductHost.create(new WorldSpec(1, 1, 0L));
    tiny.advance(5);
    assertEquals(0, ((Grid) tiny.settled().field(WorldFields.ELEVATION)).get(0, 0));
  }

  @Test
  @DisplayName("FR-1/FR-4: engine uses standing boundary orogeny; matches ProductGeneration")
  void standingOrogenyOnEngine() {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateVelocities vel = (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    PlateRegistry reg = (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(plates, vel, reg, Grid.zeros(8, 8));
    engine.advance(3);
    for (int g = 1; g <= 3; g++) {
      state = ProductGeneration.advance(state, g);
    }
    assertEquals(state.elevation(), engine.settled().field(WorldFields.ELEVATION));
    assertEquals(state.plates(), engine.settled().field(WorldFields.PLATES));
  }

  @Test
  @DisplayName("FR-4/FR-5: determinism; golden; wiki boundary orogeny")
  void determinismDumpDocs() throws Exception {
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

    Path root = findRepoRoot();
    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(
        tectonics.contains("F-038")
            || tectonics.toLowerCase().contains("boundary")
                && tectonics.toLowerCase().contains("orogeny"));
    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    assertTrue(!enginePom.contains("<artifactId>product</artifactId>"));
  }

  private static Path findRepoRoot() {
    var dir = Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("product"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found from " + dir);
  }
}
