/*
 * File: product/src/test/java/com/aethelgard/product/CrustTest.java
 * Purpose: Proves what happens to crust: it rides, rifts make fresh ocean, ocean sinks under continents, arcs and sutures thicken, margins shape the floor, and height is thickness minus 8
 * Audience: Agents / CI
 * Update when: Advection of crust, RidgeCreate, Subduct, Orogeny, MarginRelief, ContinentalCollide, or ThicknessToElevation changes
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CrustTest {

  /** Proves F-068 FR-30 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Crust rides with its plate and keeps its thickness")
  void crustRidesWithItsPlate() {
    Grid plates = new Grid(new int[][] {{0, 0, 0, 0}, {0, 0, 0, 0}});
    Grid occupancy = Occupancy.seed(4, 2);
    Lockers lockers = new Lockers(new int[] {8, 20, 12, 8, 30, 8, 16, 9});
    PlateVelocities east = new PlateVelocities(0L, new int[] {1}, new int[] {0});

    PlateKinematics.AdvectResult moved = PlateKinematics.advect(plates, occupancy, east, 1, Boundaries.empty());
    Grid elevation = ThicknessToElevation.apply(moved.occupancy(), lockers);

    for (int y = 0; y < 2; y++) {
      for (int x = 0; x < 4; x++) {
        int from = Occupancy.id(Math.floorMod(x - 1, 4), y, 4);
        assertEquals(from, moved.occupancy().get(x, y), "cell (" + x + "," + y + ") carries the crust from its west");
        assertEquals(lockers.thickness(from) - 8, elevation.get(x, y));
      }
    }
  }

  /** Proves F-068 FR-30 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A cell two plates reach keeps the crust of the lower plate")
  void contestedCellKeepsTheLowestPlatesCrust() {
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}});
    Grid occupancy = Occupancy.seed(4, 1);
    PlateVelocities apart = new PlateVelocities(0L, new int[] {-1, 1}, new int[] {0, 0});

    Grid moved = PlateKinematics.advect(plates, occupancy, apart, 1, Boundaries.empty()).occupancy();

    // x=0 is reached by plate 0 (from x=1) and plate 1 (from x=3, across the seam); x=3 by plate 0 (from x=0) and plate 1 (from x=2).
    assertEquals(1, moved.get(0, 0), "plate 0's crust from x=1");
    assertEquals(0, moved.get(3, 0), "plate 0's crust from x=0");
  }

  /** Proves F-068 FR-31 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A rift gap becomes fresh ocean of thickness 8, at height 0, one new column per cell")
  void riftGapBecomesFreshOcean() {
    Grid plates = new Grid(new int[][] {{0, 0, 1, 1}});
    Lockers lockers = new Lockers(new int[] {20, 8, 8, 8});
    PlateVelocities apart = new PlateVelocities(0L, new int[] {-1, 1}, new int[] {0, 0});
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(plates, Occupancy.seed(4, 1), apart, 1, Boundaries.empty());

    RidgeCreate.Result ridge = RidgeCreate.apply(moved.occupancy(), lockers);
    Grid elevation = ThicknessToElevation.apply(ridge.occupancy(), ridge.lockers());

    int left = ridge.occupancy().get(1, 0);
    int right = ridge.occupancy().get(2, 0);
    assertNotEquals(left, right, "each gap cell gets its own column");
    for (int fresh : new int[] {left, right}) {
      assertTrue(fresh >= lockers.count(), "a new column, not a neighbour's");
      assertEquals(8, ridge.lockers().thickness(fresh));
    }
    assertEquals(0, elevation.get(1, 0));
    assertEquals(0, elevation.get(2, 0));
    assertEquals(12, elevation.get(3, 0), "the thick crust rode to x=3; the ridge did not copy it");
  }

  /** Proves F-068 FR-32 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Where ocean meets continent the ocean sinks, even when its plate is larger, and the consumed cell shares the survivor's crust")
  void oceanSinksUnderContinentEvenWhenLarger() {
    Grid plates = new Grid(new int[][] {{0, 1}});
    Grid occupancy = Occupancy.seed(2, 1);
    Lockers lockers = new Lockers(new int[] {16, 8});
    PlateRegistry registry = new PlateRegistry(0L, new int[] {1, 10}, new int[] {1, -1}, new int[] {0, 0});
    PlateVelocities velocities = new PlateVelocities(0L, new int[] {1, -1}, new int[] {0, 0});
    Boundaries collide = new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE)));

    AreaFlux flux = AreaFlux.from(collide, registry, occupancy, lockers);
    assertEquals(0, flux.deltaArea(0), "the continent loses no area");
    assertEquals(-1, flux.deltaArea(1), "the ocean plate loses a cell");

    ApplyGeometry.Result geometry =
        ApplyGeometry.apply(plates, collide, flux, registry, velocities, occupancy, lockers, new boolean[1][2]);
    assertEquals(0, geometry.plates().get(0, 0), "the continent's cell is not sunk");

    // With both plates still, the loser's edge arrives where it stood: that cell takes the continent's column.
    PlateVelocities still = new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 0});
    int[][] destination = {{occupancy.get(0, 0), occupancy.get(1, 0)}};
    Subduct.correct(destination, occupancy, collide, lockers, registry, plates, still);
    assertEquals(occupancy.get(0, 0), destination[0][1], "the consumed cell now lies under the continent's crust");
    assertEquals(occupancy.get(0, 0), destination[0][0], "the continent's own cell keeps its column");
  }

  /** Proves F-068 FR-33 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Two colliding continents never merge into one plate")
  void continentsMeetWithoutMerging() {
    Grid occupancy = Occupancy.seed(2, 1);
    Lockers lockers = new Lockers(new int[] {16, 18});
    PlateRegistry registry = new PlateRegistry(0L, new int[] {2, 8}, new int[] {1, -1}, new int[] {0, 0});
    BoundaryContact contact = new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE);
    Boundaries collide = new Boundaries(List.of(contact));

    assertEquals(CrustPrecedence.NONE, CrustPrecedence.collideLoser(contact, occupancy, lockers, registry, 2, 1), "neither loses");
    AreaFlux flux = AreaFlux.from(collide, registry, occupancy, lockers);
    assertEquals(List.of(0, 0, 0), List.of(flux.deltaArea(0), flux.deltaArea(1), flux.sinkDelta()), "no cell is consumed");
    assertEquals(lockers, Orogeny.applyToLockers(collide, registry, occupancy, lockers), "no stamp on two continents");

    // A whole generation of two continents driving into each other keeps both plates.
    int w = 6;
    int h = 3;
    int[][] cells = new int[h][w];
    for (int[] row : cells) {
      for (int x = 0; x < w; x++) {
        row[x] = x < w / 2 ? 0 : 1;
      }
    }
    Grid plates = new Grid(cells);
    PlateVelocities toward = new PlateVelocities(1L, new int[] {1, -1}, new int[] {0, 0});
    int[] thickness = new int[w * h];
    Arrays.fill(thickness, 16);
    ProductGeneration.Snapshot after =
        ProductGeneration.advance(
            new ProductGeneration.Snapshot(plates, toward, PlateRegistry.from(plates, toward),
                Occupancy.seed(w, h), new Lockers(thickness), Grid.zeros(w, h)),
            1);

    Set<Integer> owners = new HashSet<>();
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        owners.add(after.plates().get(x, y));
      }
    }
    assertEquals(2, owners.size(), "both plates remain");
    assertEquals(2, after.registry().count());
  }

  /** Proves F-068 FR-34 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Where ocean meets ocean, the winner thickens by 8 once, to at least 16")
  void oceanCollisionRaisesAnArc() {
    // The winner's column (locker 0) touches the front at two cells; it is thickened once.
    Grid occupancy = new Grid(new int[][] {{0, 1}, {0, 1}});
    PlateRegistry registry = new PlateRegistry(0L, new int[] {10, 3}, new int[] {1, -1}, new int[] {0, 0});
    Boundaries front =
        new Boundaries(List.of(
            new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE),
            new BoundaryContact(0, 1, 1, 0, 0, 1, BoundaryKind.COLLIDE)));

    assertEquals(List.of(16, 8), thicknesses(ContinentalCollide.apply(occupancy, new Lockers(new int[] {8, 8}), front, registry)));
    assertEquals(List.of(23, 8), thicknesses(ContinentalCollide.apply(occupancy, new Lockers(new int[] {15, 8}), front, registry)));
    assertEquals(List.of(16, 5), thicknesses(ContinentalCollide.apply(occupancy, new Lockers(new int[] {6, 5}), front, registry)),
        "a thin winner is raised to 16; the loser is untouched");
  }

  /** Proves F-068 FR-35 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A suture thickens both continents by 4, never past 32; other contacts add no collision thickening")
  void sutureThickensBothSidesUpToTheCap() {
    Grid occupancy = Occupancy.seed(2, 1);
    PlateRegistry registry = new PlateRegistry(0L, new int[] {4, 4}, new int[] {1, -1}, new int[] {0, 0});
    Boundaries collide = contact(BoundaryKind.COLLIDE);

    assertEquals(List.of(20, 24), thicknesses(ContinentalCollide.apply(occupancy, new Lockers(new int[] {16, 20}), collide, registry)));
    assertEquals(List.of(32, 32), thicknesses(ContinentalCollide.apply(occupancy, new Lockers(new int[] {30, 32}), collide, registry)));

    Lockers oceanContinent = new Lockers(new int[] {16, 8});
    assertEquals(oceanContinent, ContinentalCollide.apply(occupancy, oceanContinent, collide, registry));
    Lockers continents = new Lockers(new int[] {16, 20});
    assertEquals(continents, ContinentalCollide.apply(occupancy, continents, contact(BoundaryKind.PASS_BY), registry));
    assertEquals(continents, ContinentalCollide.apply(occupancy, continents, contact(BoundaryKind.SEPARATE), registry));
  }

  /** Proves F-068 FR-36 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Orogeny stamps the contact cells' crust (collide +1/-1, separate -1 each, pass-by 0), and the stamps ride with it")
  void orogenyStampsRideWithTheCrust() {
    Grid occupancy = Occupancy.seed(2, 1);
    PlateRegistry registry = new PlateRegistry(0L, new int[] {10, 5}, new int[] {1, 0}, new int[] {0, 0});
    Lockers ocean = Lockers.oceanic(2);

    Lockers collided = Orogeny.applyToLockers(contact(BoundaryKind.COLLIDE), registry, occupancy, ocean);
    assertEquals(List.of(9, 7), thicknesses(collided), "winner (larger plate 0) +1, loser -1");
    assertEquals(List.of(7, 7), thicknesses(Orogeny.applyToLockers(contact(BoundaryKind.SEPARATE), registry, occupancy, ocean)));
    assertEquals(List.of(8, 8), thicknesses(Orogeny.applyToLockers(contact(BoundaryKind.PASS_BY), registry, occupancy, ocean)));

    Grid plates = new Grid(new int[][] {{0, 1}});
    PlateVelocities east = new PlateVelocities(0L, new int[] {1, 1}, new int[] {0, 0});
    Grid moved = PlateKinematics.advect(plates, occupancy, east, 1, Boundaries.empty()).occupancy();
    Grid elevation = ThicknessToElevation.apply(moved, collided);
    assertEquals(1, elevation.get(1, 0), "the +1 stamp moved east with its crust");
    assertEquals(-1, elevation.get(0, 0), "the -1 stamp moved east across the seam");
  }

  /** Proves F-068 FR-37 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Near a rift the ocean floor is 4 thick on the contact and back to 8 at distance 8")
  void riftTroughDeepensTheFloor() {
    Lockers result = MarginRelief.apply(Occupancy.seed(MW, MH), Lockers.oceanic(MW * MH), margin(BoundaryKind.SEPARATE), 0L);

    assertEquals(4, thickness(result, EDGE, ROW), "on the contact");
    assertEquals(4, thickness(result, EDGE + 1, ROW), "the other side of the contact");
    // Beyond the lip (distance 5 and more) the trough is 4 + floor(d/2).
    for (int d = 5; d <= 8; d++) {
      assertEquals(4 + d / 2, thickness(result, EDGE - d, ROW), "distance " + d);
    }
    assertEquals(8, thickness(result, EDGE - 9, ROW), "distance 9 is untouched");
  }

  /** Proves F-068 FR-37 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Near a collision the ocean floor rises 4 on the line, 1 less per cell, and stays below 16")
  void collisionSlopeStaysOcean() {
    int[] start = Lockers.oceanic(MW * MH).thicknesses();
    start[Occupancy.id(EDGE, ROW - 1, MW)] = 14;
    Lockers result = MarginRelief.apply(Occupancy.seed(MW, MH), new Lockers(start), margin(BoundaryKind.COLLIDE), 1L);

    for (int d = 0; d <= 4; d++) {
      assertEquals(8 + Math.max(0, 4 - d), thickness(result, EDGE - d, ROW), "distance " + d);
    }
    assertEquals(15, thickness(result, EDGE, ROW - 1), "14 + 4 is held below land at 15");
  }

  /** Proves F-068 FR-37 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Margins never make land or change continental crust, and the blend repeats for a seed")
  void marginsLeaveContinentsAlone() {
    int[] start = Lockers.oceanic(MW * MH).thicknesses();
    int continent = Occupancy.id(EDGE, ROW, MW);
    start[continent] = 20;
    start[Occupancy.id(EDGE - 1, ROW, MW)] = 15;
    Grid occupancy = Occupancy.seed(MW, MH);

    for (BoundaryKind kind : List.of(BoundaryKind.SEPARATE, BoundaryKind.COLLIDE)) {
      Lockers once = MarginRelief.apply(occupancy, new Lockers(start), margin(kind), 99L);
      Lockers twice = MarginRelief.apply(occupancy, new Lockers(start), margin(kind), 99L);
      assertEquals(once, twice, kind + ": the same seed shapes the same margin");
      assertEquals(20, once.thickness(continent), kind + ": continental crust is unchanged");
      for (int id = 0; id < once.count(); id++) {
        if (id != continent) {
          assertTrue(once.thickness(id) < 16, kind + ": column " + id + " stayed ocean");
        }
      }
    }
  }

  /** Proves F-068 FR-38 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Every cell's elevation is its crust's thickness minus 8, so cells that share crust stand equally high")
  void elevationIsThicknessMinusEight() {
    Grid occupancy = new Grid(new int[][] {{0, 1, 1}, {2, 3, 0}});
    Lockers lockers = new Lockers(new int[] {32, 4, 16, 8});

    Grid elevation = ThicknessToElevation.apply(occupancy, lockers);

    assertEquals(List.of(24, -4, -4, 8, 0, 24),
        List.of(elevation.get(0, 0), elevation.get(1, 0), elevation.get(2, 0),
            elevation.get(0, 1), elevation.get(1, 1), elevation.get(2, 1)));
  }

  // --- a straight margin: contacts along column EDGE of a 40 x 8 map ---

  private static final int MW = 40;
  private static final int MH = 8;
  private static final int EDGE = 19;
  private static final int ROW = 4;

  private static Boundaries margin(BoundaryKind kind) {
    List<BoundaryContact> contacts = new ArrayList<>();
    for (int y = 0; y < MH; y++) {
      contacts.add(new BoundaryContact(EDGE, y, 1, 0, 0, 1, kind));
    }
    return new Boundaries(contacts);
  }

  private static int thickness(Lockers lockers, int x, int y) {
    return lockers.thickness(Occupancy.id(x, y, MW));
  }

  private static Boundaries contact(BoundaryKind kind) {
    return new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, kind)));
  }

  private static List<Integer> thicknesses(Lockers lockers) {
    List<Integer> out = new ArrayList<>();
    for (int id = 0; id < lockers.count(); id++) {
      out.add(lockers.thickness(id));
    }
    return out;
  }
}
