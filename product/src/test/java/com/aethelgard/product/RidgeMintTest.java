/*
 * File: product/src/test/java/com/aethelgard/product/RidgeMintTest.java
 * Purpose: F-057 witness — ridge mint thin oceanic lockers in advection gaps
 * Audience: Agents / CI
 * Update when: F-057 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RidgeMintTest {

  @Test
  @DisplayName("FR-1: gap cells mint new T_ocean lockers; plate flood still owns the cell")
  void gapCellsMintThinOcean() {
    int width = 4;
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}});
    int[] thick = {20, Lockers.T_OCEAN, Lockers.T_OCEAN, Lockers.T_OCEAN};
    Lockers lockers = new Lockers(thick);
    Grid occupancy = Occupancy.seed(width, 1);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {-1, 1}, new int[] {0, 0});
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(plates, occupancy, vel, 1, Boundaries.empty());

    assertEquals(PlateKinematics.UNRESOLVED, moved.occupancy().get(1, 0));
    assertEquals(PlateKinematics.UNRESOLVED, moved.occupancy().get(2, 0));
    assertTrue(moved.plates().get(1, 0) >= 0, "plate flood owns gap");
    assertTrue(moved.plates().get(2, 0) >= 0, "plate flood owns gap");

    RidgeCreate.Result minted = RidgeCreate.apply(moved.occupancy(), lockers);
    int gapLeft = minted.occupancy().get(1, 0);
    int gapRight = minted.occupancy().get(2, 0);
    assertEquals(4, gapLeft);
    assertEquals(5, gapRight);
    assertEquals(Lockers.T_OCEAN, minted.lockers().thickness(gapLeft));
    assertEquals(Lockers.T_OCEAN, minted.lockers().thickness(gapRight));
    assertEquals(6, minted.lockers().count());
  }

  @Test
  @DisplayName("FR-2: unique dest rides; contested keeps lowest-plate locker")
  void rideAndContestedKeepSourceLockers() {
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}});
    Grid occupancy = Occupancy.seed(4, 1);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {-1, 1}, new int[] {0, 0});
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(plates, occupancy, vel, 1, Boundaries.empty());
    RidgeCreate.Result minted = RidgeCreate.apply(moved.occupancy(), Lockers.oceanic(4));
    // dest 0 contested: plate 0 locker 1 vs plate 1 locker 3 → locker 1
    assertEquals(1, minted.occupancy().get(0, 0));
    // dest 3 contested: plate 0 locker 0 vs plate 1 locker 2 → locker 0
    assertEquals(0, minted.occupancy().get(3, 0));
  }

  @Test
  @DisplayName("FR-3: gap elevation is isostasy 0, not a neighbor mountain")
  void gapElevationIsOceanNotMountain() {
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}});
    int[] thick = {20, Lockers.T_OCEAN, Lockers.T_OCEAN, Lockers.T_OCEAN};
    Lockers lockers = new Lockers(thick);
    Grid occupancy = Occupancy.seed(4, 1);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {-1, 1}, new int[] {0, 0});
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(plates, occupancy, vel, 1, Boundaries.empty());
    RidgeCreate.Result minted = RidgeCreate.apply(moved.occupancy(), lockers);
    Grid elevation = ThicknessToElevation.apply(minted.occupancy(), minted.lockers());
    assertEquals(0, elevation.get(1, 0));
    assertEquals(0, elevation.get(2, 0));
    // locker 0 (thickness 20) rode to dest 3
    assertEquals(20 - Lockers.T_OCEAN, elevation.get(3, 0));

    java.util.List<String> ids =
        ProductHost.setup().systems().getFirst().config().subSystems().stream()
            .map(s -> s.id())
            .toList();
    assertEquals("isostasy", ids.get(ids.size() - 1));
    assertTrue(ids.contains("ridge-create"));
  }

  @Test
  @DisplayName("FR-4: determinism; ProductGeneration matches engine; no engine source edits")
  void determinismAndNoEngineEdits() throws Exception {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(3);
    b.advance(3);
    assertEquals(a.settled().field(WorldFields.OCCUPANCY), b.settled().field(WorldFields.OCCUPANCY));
    assertEquals(a.settled().field(WorldFields.LOCKERS), b.settled().field(WorldFields.LOCKERS));
    assertEquals(a.settled().field(WorldFields.ELEVATION), b.settled().field(WorldFields.ELEVATION));

    Engine engine = ProductHost.create(spec);
    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(
            (Grid) engine.settled().field(WorldFields.PLATES),
            (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY),
            (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY),
            (Grid) engine.settled().field(WorldFields.OCCUPANCY),
            (Lockers) engine.settled().field(WorldFields.LOCKERS),
            (Grid) engine.settled().field(WorldFields.ELEVATION));
    engine.advance(3);
    for (int g = 1; g <= 3; g++) {
      state = ProductGeneration.advance(state, g);
    }
    assertEquals(state.occupancy(), engine.settled().field(WorldFields.OCCUPANCY));
    assertEquals(state.lockers(), engine.settled().field(WorldFields.LOCKERS));
    assertEquals(state.elevation(), engine.settled().field(WorldFields.ELEVATION));

    Set<Integer> startIds = new HashSet<>();
    Grid step0 = Occupancy.seed(spec.width(), spec.height());
    for (int y = 0; y < spec.height(); y++) {
      for (int x = 0; x < spec.width(); x++) {
        startIds.add(step0.get(x, y));
      }
    }
    Grid occ = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    boolean minted = false;
    for (int y = 0; y < spec.height(); y++) {
      for (int x = 0; x < spec.width(); x++) {
        int id = occ.get(x, y);
        assertTrue(id >= 0);
        if (!startIds.contains(id)) {
          minted = true;
        }
      }
    }
    assertTrue(minted, "DEFAULT seed after 3 Steps should mint at least one gap locker");

    Path root = findRepoRoot();
    String engineJava =
        Files.readString(root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertFalse(engineJava.contains("RidgeCreate"), "no engine production edits for ridge mint");
    assertEquals(1, ProductHost.setup().systems().size());
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
