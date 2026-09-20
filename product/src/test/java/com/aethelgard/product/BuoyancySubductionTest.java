/*
 * File: product/src/test/java/com/aethelgard/product/BuoyancySubductionTest.java
 * Purpose: F-058 witness — buoyancy collide, consume, SEPARATE rift mint
 * Audience: Agents / CI
 * Update when: F-058 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BuoyancySubductionTest {

  @Test
  @DisplayName("FR-1: T_land=16; Step 0 all T_ocean; continental helper")
  void landThresholdAndStepZero() {
    assertEquals(16, Lockers.T_LAND);
    assertEquals(8, Lockers.T_OCEAN);
    Lockers mixed = new Lockers(new int[] {8, 15, 16, 20});
    assertFalse(CrustPrecedence.continental(mixed, 0));
    assertFalse(CrustPrecedence.continental(mixed, 1));
    assertTrue(CrustPrecedence.continental(mixed, 2));
    assertTrue(CrustPrecedence.continental(mixed, 3));

    Engine engine = ProductHost.create(new WorldSpec(3, 2, 0L));
    Lockers lockers = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    Grid occupancy = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    for (int y = 0; y < 2; y++) {
      for (int x = 0; x < 3; x++) {
        assertEquals(Lockers.T_OCEAN, lockers.thickness(occupancy.get(x, y)));
        assertEquals(0, ((Grid) engine.settled().field(WorldFields.ELEVATION)).get(x, y));
      }
    }
  }

  @Test
  @DisplayName("FR-2: O-C collide sinks ocean even when continent plate is smaller")
  void oceanSubductsDespiteArea() {
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {1, 10}, new int[] {1, -1}, new int[] {0, 0});
    Grid occupancy = Occupancy.seed(2, 1);
    Lockers lockers = new Lockers(new int[] {Lockers.T_LAND, Lockers.T_OCEAN});
    BoundaryContact contact = new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE);
    int lose = CrustPrecedence.collideLoser(contact, occupancy, lockers, registry, 2, 1);
    assertEquals(1, lose, "oceanic plate loses");

    AreaFlux flux = AreaFlux.from(new Boundaries(List.of(contact)), registry, occupancy, lockers);
    assertEquals(0, flux.deltaArea(0));
    assertEquals(-1, flux.deltaArea(1));
    assertEquals(1, flux.sinkDelta());

    Grid plates = new Grid(new int[][] {{0, 1}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1, -1}, new int[] {0, 0});
    boolean[][] skip = new boolean[1][2];
    ApplyGeometry.Result geom =
        ApplyGeometry.apply(
            plates,
            new Boundaries(List.of(contact)),
            flux,
            registry,
            vel,
            occupancy,
            lockers,
            skip);
    assertEquals(0, geom.plates().get(0, 0), "continent cell not sunk");
  }

  @Test
  @DisplayName("FR-3: O-O collide still smaller-loses; area tie → lower id")
  void oceanOceanSmallerLoses() {
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {10, 3, 10}, new int[] {0, 0, 0}, new int[] {0, 0, 0});
    Grid occupancy = Occupancy.seed(3, 1);
    Lockers oceanic = Lockers.oceanic(3);
    BoundaryContact mid = new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE);
    assertEquals(1, CrustPrecedence.collideLoser(mid, occupancy, oceanic, registry, 3, 1));
    BoundaryContact tie = new BoundaryContact(0, 0, 1, 0, 0, 2, BoundaryKind.COLLIDE);
    assertEquals(0, CrustPrecedence.collideLoser(tie, occupancy, oceanic, registry, 3, 1));
  }

  @Test
  @DisplayName("FR-4: C-C collide is 0 flux and 0 stamps; occupancy ids kept")
  void continentContinentFrozen() {
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {2, 8}, new int[] {1, -1}, new int[] {0, 0});
    Grid occupancy = Occupancy.seed(2, 1);
    Lockers lockers = new Lockers(new int[] {Lockers.T_LAND, Lockers.T_LAND + 2});
    BoundaryContact contact = new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE);
    assertEquals(
        CrustPrecedence.NONE,
        CrustPrecedence.collideLoser(contact, occupancy, lockers, registry, 2, 1));

    AreaFlux flux = AreaFlux.from(new Boundaries(List.of(contact)), registry, occupancy, lockers);
    assertEquals(0, flux.deltaArea(0));
    assertEquals(0, flux.deltaArea(1));
    assertEquals(0, flux.sinkDelta());

    Lockers stamped =
        Orogeny.applyToLockers(new Boundaries(List.of(contact)), registry, occupancy, lockers);
    assertEquals(Lockers.T_LAND, stamped.thickness(0));
    assertEquals(Lockers.T_LAND + 2, stamped.thickness(1));

    Grid plates = new Grid(new int[][] {{0, 1}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1, -1}, new int[] {0, 0});
    boolean[][] skip = new boolean[1][2];
    ApplyGeometry.Result geom =
        ApplyGeometry.apply(
            plates,
            new Boundaries(List.of(contact)),
            flux,
            registry,
            vel,
            occupancy,
            lockers,
            skip);
    assertEquals(0, geom.plates().get(0, 0));
    assertEquals(1, geom.plates().get(1, 0));
  }

  @Test
  @DisplayName("FR-5: consumed ocean dest points at surviving locker, not the oceanic id")
  void consumeWritesSurvivorLocker() {
    Grid occupancy = Occupancy.seed(2, 1);
    Lockers lockers = new Lockers(new int[] {Lockers.T_LAND, Lockers.T_OCEAN});
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {1, 10}, new int[] {0, 0}, new int[] {0, 0});
    Boundaries collide =
        new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE)));
    Grid plates = new Grid(new int[][] {{0, 1}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 0});
    int[][] occDest = {{occupancy.get(0, 0), occupancy.get(1, 0)}};
    Subduct.correct(occDest, occupancy, collide, lockers, registry, plates, vel);
    assertEquals(occupancy.get(0, 0), occDest[0][1], "ocean dest becomes continent locker");
    assertNotEquals(occupancy.get(1, 0), occDest[0][1]);
    assertEquals(occupancy.get(0, 0), occDest[0][0], "continent interior rides");
  }

  @Test
  @DisplayName("FR-6: SEPARATE copies of the contact locker remint as T_ocean")
  void separateDoesNotStretchBorderLocker() {
    Grid occupancy = Occupancy.seed(4, 1);
    Lockers lockers = new Lockers(new int[] {Lockers.T_OCEAN, 20, Lockers.T_OCEAN, Lockers.T_OCEAN});
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 0});
    Boundaries separate =
        new Boundaries(List.of(new BoundaryContact(1, 0, 1, 0, 0, 1, BoundaryKind.SEPARATE)));
    int[][] occDest = {{0, 1, 1, 1}};
    Subduct.correct(
        occDest, occupancy, separate, lockers, PlateRegistry.from(plates, vel), plates, vel);
    assertEquals(1, occDest[0][1], "contact locker may keep its ride dest");
    assertEquals(PlateKinematics.UNRESOLVED, occDest[0][2]);
    assertEquals(PlateKinematics.UNRESOLVED, occDest[0][3]);
    RidgeCreate.Result minted = RidgeCreate.apply(new Grid(occDest), lockers);
    assertNotEquals(1, minted.occupancy().get(2, 0));
    assertNotEquals(1, minted.occupancy().get(3, 0));
    assertEquals(Lockers.T_OCEAN, minted.lockers().thickness(minted.occupancy().get(2, 0)));
    assertEquals(Lockers.T_OCEAN, minted.lockers().thickness(minted.occupancy().get(3, 0)));
    Grid elevation = ThicknessToElevation.apply(minted.occupancy(), minted.lockers());
    assertEquals(0, elevation.get(2, 0));
    assertEquals(0, elevation.get(3, 0));
  }

  @Test
  @DisplayName("FR-7: determinism; ProductGeneration matches engine; no engine source edits")
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

    Grid occ = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    Lockers loc = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    assertEquals(ThicknessToElevation.apply(occ, loc), engine.settled().field(WorldFields.ELEVATION));

    Set<Integer> seen = new HashSet<>();
    for (int y = 0; y < spec.height(); y++) {
      for (int x = 0; x < spec.width(); x++) {
        seen.add(occ.get(x, y));
      }
    }
    assertFalse(seen.contains(PlateKinematics.UNRESOLVED));

    Path root = findRepoRoot();
    String engineJava =
        Files.readString(root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertFalse(engineJava.contains("Subduct"));
    assertFalse(engineJava.contains("CrustPrecedence"));
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
