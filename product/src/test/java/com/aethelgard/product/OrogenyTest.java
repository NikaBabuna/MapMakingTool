/*
 * File: product/src/test/java/com/aethelgard/product/OrogenyTest.java
 * Purpose: F-038 witness — elevation from standing classified boundaries (O(contacts))
 * Audience: Agents / CI
 * Update when: F-038 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrogenyTest {

  @Test
  @DisplayName("FR-2: COLLIDE winner +1 / loser -1; SEPARATE both -1; PASS_BY 0")
  void boundaryKindsRelief() {
    PlateRegistry reg =
        new PlateRegistry(
            0L, new int[] {10, 5, 8}, new int[] {1, 0, 0}, new int[] {0, 0, 0});
    Boundaries boundaries =
        new Boundaries(
            List.of(
                new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE),
                new BoundaryContact(2, 0, 1, 0, 0, 2, BoundaryKind.SEPARATE),
                new BoundaryContact(0, 1, 1, 0, 1, 2, BoundaryKind.PASS_BY)));
    Grid elev = Grid.zeros(4, 2);
    Grid next = Orogeny.apply(boundaries, reg, elev);
    // COLLIDE: plate0 (larger) at (0,0) wins +1; plate1 at (1,0) loses -1
    assertEquals(1, next.get(0, 0));
    assertEquals(-1, next.get(1, 0));
    // SEPARATE: both -1
    assertEquals(-1, next.get(2, 0));
    assertEquals(-1, next.get(3, 0));
    // PASS_BY: unchanged
    assertEquals(0, next.get(0, 1));
    assertEquals(0, next.get(1, 1));
  }

  @Test
  @DisplayName("FR-2: winner COLLIDE beats SEPARATE on the same cell")
  void combineLadder() {
    PlateRegistry reg =
        new PlateRegistry(0L, new int[] {10, 5, 5}, new int[] {0, 0, 0}, new int[] {0, 0, 0});
    // Cell (1,0) is loser of COLLIDE with 0 and also SEPARATE with 2 → still -1 (loser)
    // Cell (0,0) is winner +1
    Boundaries boundaries =
        new Boundaries(
            List.of(
                new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE),
                new BoundaryContact(1, 0, 1, 0, 1, 2, BoundaryKind.SEPARATE)));
    Grid next = Orogeny.apply(boundaries, reg, Grid.zeros(3, 1));
    assertEquals(1, next.get(0, 0));
    assertEquals(-1, next.get(1, 0));
    assertEquals(-1, next.get(2, 0));
  }

  @Test
  @DisplayName("FR-3: interior / PASS_BY unchanged; 1x1 stays 0; negatives accumulate")
  void interiorNegativeAndTiny() {
    Boundaries empty = Boundaries.empty();
    PlateRegistry one = new PlateRegistry(0L, new int[] {3}, new int[] {1}, new int[] {0});
    Grid zeros = Grid.zeros(3, 1);
    assertEquals(zeros, Orogeny.apply(empty, one, zeros));

    PlateRegistry reg =
        new PlateRegistry(0L, new int[] {10, 5}, new int[] {0, 1}, new int[] {0, 0});
    Boundaries collide =
        new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE)));
    Grid once = Orogeny.apply(collide, reg, Grid.zeros(2, 1));
    Grid twice = Orogeny.apply(collide, reg, once);
    assertEquals(2, twice.get(0, 0));
    assertEquals(-2, twice.get(1, 0));

    Engine tiny = ProductHost.create(new WorldSpec(1, 1, 0L));
    tiny.advance(5);
    assertEquals(0, ((Grid) tiny.settled().field(WorldFields.ELEVATION)).get(0, 0));
  }

  @Test
  @DisplayName("FR-4: apply walks contacts once — no nested cell×contact scan in source")
  void contactsWalkStructural() throws Exception {
    String src =
        Files.readString(
            findRepoRoot().resolve("product/src/main/java/com/aethelgard/product/Orogeny.java"));
    assertTrue(src.contains("boundaries.contacts()"));
    assertTrue(src.contains("HashMap") || src.contains("ranks"));
    // Forbid classic regression: for each cell, for each contact
    assertFalse(
        Pattern.compile(
                "for\\s*\\(\\s*int\\s+[xy]\\s*=.*?boundaries\\.contacts\\(\\)",
                Pattern.DOTALL)
            .matcher(src)
            .find());
    assertFalse(src.contains("cells × contacts") || src.contains("cells*contacts"));
  }

  @Test
  @DisplayName("FR-1/FR-6: engine matches ProductGeneration; golden + determinism")
  void standingOrogenyOnEngine() throws Exception {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(
            (Grid) engine.settled().field(WorldFields.PLATES),
            (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY),
            (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY),
            Grid.zeros(8, 8));
    engine.advance(3);
    for (int g = 1; g <= 3; g++) {
      state = ProductGeneration.advance(state, g);
    }
    assertEquals(state.elevation(), engine.settled().field(WorldFields.ELEVATION));
    assertEquals(state.plates(), engine.settled().field(WorldFields.PLATES));
    assertEquals(state.velocities(), engine.settled().field(WorldFields.PLATE_VELOCITY));

    WorldSpec def = WorldSpec.DEFAULT;
    Engine a = ProductHost.create(def);
    Engine b = ProductHost.create(def);
    a.advance(WorldDump.CANONICAL_STEPS);
    b.advance(WorldDump.CANONICAL_STEPS);
    assertEquals(WorldDump.of(a, def), WorldDump.of(b, def));
    String golden =
        Files.readString(
                findRepoRoot().resolve("product/src/test/resources/worlds/default-n3.txt"),
                StandardCharsets.UTF_8)
            .replace("\r\n", "\n");
    assertEquals(golden, WorldDump.of(a, def));

    ProductSession view = ProductSession.view();
    assertEquals(1920, view.spec().width());
    assertEquals(1080, view.spec().height());
  }

  @Test
  @DisplayName("FR-1/FR-7: wiki boundary orogeny; CollisionUplift gone; no Swing")
  void wikiAndRetiredUplift() throws Exception {
    Path root = findRepoRoot();
    String elev = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    String tect = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(elev.toLowerCase().contains("height"));
    assertTrue(tect.contains("boundaries") || elev.toLowerCase().contains("crust"));
    assertTrue(tect.contains("retired"));
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("F-038"));
    assertFalse(
        Files.exists(
            root.resolve("product/src/main/java/com/aethelgard/product/CollisionUplift.java")));
    assertFalse(
        Files.readString(root.resolve("product/src/main/java/com/aethelgard/product/Orogeny.java"))
            .contains("javax.swing"));
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
