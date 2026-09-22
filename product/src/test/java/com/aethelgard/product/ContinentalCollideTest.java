/*
 * File: product/src/test/java/com/aethelgard/product/ContinentalCollideTest.java
 * Purpose: F-060 witness — arc, suture, cap, ride
 * Audience: Agents / CI
 * Update when: F-060 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContinentalCollideTest {

  @Test
  @DisplayName("FR-1: ocean–ocean winner +8 once and at least 16; loser stays its own locker")
  void arcThickensWinnerOnce() {
    Grid occupancy = new Grid(new int[][] {{0, 1}, {0, 1}});
    Grid plates = new Grid(new int[][] {{0, 1}, {0, 1}});
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {10, 3}, new int[] {1, -1}, new int[] {0, 0});
    Boundaries front =
        new Boundaries(
            List.of(
                new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE),
                new BoundaryContact(0, 1, 1, 0, 0, 1, BoundaryKind.COLLIDE)));

    Lockers fromMargin = ContinentalCollide.apply(occupancy, new Lockers(new int[] {15, 8}), front, registry);
    assertEquals(23, fromMargin.thickness(0));
    assertEquals(8, fromMargin.thickness(1));
    assertTrue(fromMargin.thickness(0) >= Lockers.T_LAND);
    assertTrue(fromMargin.thickness(0) <= ContinentalCollide.CAP);

    Lockers fromOcean = ContinentalCollide.apply(occupancy, new Lockers(new int[] {8, 8}), front, registry);
    assertEquals(16, fromOcean.thickness(0));
    assertEquals(8, fromOcean.thickness(1));

    Lockers fromThin = ContinentalCollide.apply(occupancy, new Lockers(new int[] {7, 4}), front, registry);
    assertEquals(Lockers.T_LAND, fromThin.thickness(0));
    assertEquals(4, fromThin.thickness(1));

    assertEquals(0, plates.get(0, 0));
    assertEquals(1, plates.get(1, 0));
    assertEquals(0, occupancy.get(0, 0));
    assertEquals(1, occupancy.get(1, 0));
  }

  @Test
  @DisplayName("FR-2: both continental sides +4; plates and occupancy ids remain")
  void sutureThickensBothSides() {
    Grid occupancy = Occupancy.seed(2, 1);
    Grid plates = new Grid(new int[][] {{0, 1}});
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {2, 8}, new int[] {1, -1}, new int[] {0, 0});
    Boundaries collide =
        new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE)));
    Lockers lockers = new Lockers(new int[] {Lockers.T_LAND, 20});
    Lockers sutured = ContinentalCollide.apply(occupancy, lockers, collide, registry);

    assertEquals(Lockers.T_LAND + ContinentalCollide.SUTURE, sutured.thickness(0));
    assertEquals(24, sutured.thickness(1));
    assertEquals(0, occupancy.get(0, 0));
    assertEquals(1, occupancy.get(1, 0));
    assertEquals(0, plates.get(0, 0));
    assertEquals(1, plates.get(1, 0));

    int width = 6;
    int height = 3;
    int[][] cells = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        cells[y][x] = x < width / 2 ? 0 : 1;
      }
    }
    Grid wide = new Grid(cells);
    PlateVelocities velocities = new PlateVelocities(1L, new int[] {1, -1}, new int[] {0, 0});
    int[] thickness = new int[width * height];
    java.util.Arrays.fill(thickness, Lockers.T_LAND);
    ProductGeneration.Snapshot after =
        ProductGeneration.advance(
            new ProductGeneration.Snapshot(
                wide,
                velocities,
                PlateRegistry.from(wide, velocities),
                Occupancy.seed(width, height),
                new Lockers(thickness),
                Grid.zeros(width, height)),
            1);
    boolean plate0 = false;
    boolean plate1 = false;
    for (int y = 0; y < after.plates().height(); y++) {
      for (int x = 0; x < after.plates().width(); x++) {
        plate0 |= after.plates().get(x, y) == 0;
        plate1 |= after.plates().get(x, y) == 1;
      }
    }
    assertTrue(plate0 && plate1, "suture does not remove either plate");
    boolean continentRemains = false;
    for (int id = 0; id < after.lockers().count(); id++) {
      if (after.lockers().thickness(id) >= Lockers.T_LAND) {
        continentRemains = true;
      }
    }
    assertTrue(continentRemains, "continental thickness is not consumed");
    assertEquals(
        after.lockers().thickness(after.occupancy().get(0, 0)) - Lockers.T_OCEAN,
        after.elevation().get(0, 0));
  }

  @Test
  @DisplayName("FR-3: cap holds at 32; ocean–continent, pass-by, and diverge add 0")
  void capAndIgnoredKinds() {
    Grid occupancy = Occupancy.seed(2, 1);
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {4, 4}, new int[] {1, -1}, new int[] {0, 0});
    Boundaries collide =
        new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE)));
    Lockers capped =
        ContinentalCollide.apply(occupancy, new Lockers(new int[] {30, ContinentalCollide.CAP}), collide, registry);
    assertEquals(ContinentalCollide.CAP, capped.thickness(0));
    assertEquals(ContinentalCollide.CAP, capped.thickness(1));

    Lockers oceanContinent = new Lockers(new int[] {Lockers.T_LAND, Lockers.T_OCEAN});
    assertEquals(oceanContinent, ContinentalCollide.apply(occupancy, oceanContinent, collide, registry));

    Boundaries pass =
        new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.PASS_BY)));
    Lockers oceanic = Lockers.oceanic(2);
    assertEquals(oceanic, ContinentalCollide.apply(occupancy, oceanic, pass, registry));

    Boundaries separate =
        new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.SEPARATE)));
    assertEquals(oceanic, ContinentalCollide.apply(occupancy, oceanic, separate, registry));
  }

  @Test
  @DisplayName("FR-4: thickened crust rides; isostasy; determinism; one system; no engine edits")
  void ridesAndMatchesEngine() throws Exception {
    Grid parked = new Grid(new int[][] {{2, 0}});
    Lockers lockers = new Lockers(new int[] {23, 8, Lockers.T_OCEAN});
    Grid elevation = ThicknessToElevation.apply(parked, lockers);
    assertEquals(23 - Lockers.T_OCEAN, elevation.get(1, 0));
    assertEquals(0, elevation.get(0, 0));
    assertTrue(lockers.thickness(parked.get(1, 0)) >= Lockers.T_LAND);

    Boundaries quiet =
        new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.PASS_BY)));
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {1, 1}, new int[] {0, 0}, new int[] {0, 0});
    Lockers later = ContinentalCollide.apply(parked, lockers, quiet, registry);
    assertEquals(23, later.thickness(0));
    assertTrue(later.thickness(parked.get(1, 0)) >= Lockers.T_LAND);

    var systems = ProductHost.setup().systems();
    assertEquals(1, systems.size());
    assertEquals(ProductHost.TECTONICS_SYSTEM_ID, systems.get(0).config().id());

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

    Grid occ = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    Lockers loc = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    assertEquals(ThicknessToElevation.apply(occ, loc), engine.settled().field(WorldFields.ELEVATION));

    boolean continental = false;
    int riding = -1;
    for (int y = 0; y < spec.height(); y++) {
      for (int x = 0; x < spec.width(); x++) {
        int id = occ.get(x, y);
        if (id >= 0 && loc.thickness(id) >= Lockers.T_LAND) {
          continental = true;
          riding = id;
        }
      }
    }
    assertTrue(continental, "a collide-friendly run grows crust to T_land");
    engine.advance(1);
    Lockers ridden = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    Grid riddenOcc = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    assertTrue(ridden.thickness(riding) >= Lockers.T_LAND);
    Grid riddenElev = (Grid) engine.settled().field(WorldFields.ELEVATION);
    for (int y = 0; y < spec.height(); y++) {
      for (int x = 0; x < spec.width(); x++) {
        if (riddenOcc.get(x, y) == riding) {
          assertEquals(ridden.thickness(riding) - Lockers.T_OCEAN, riddenElev.get(x, y));
        }
      }
    }

    Path root = findRepoRoot();
    String engineJava =
        Files.readString(root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertFalse(engineJava.contains("ContinentalCollide"));
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
