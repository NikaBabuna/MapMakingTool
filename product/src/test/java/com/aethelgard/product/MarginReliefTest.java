/*
 * File: product/src/test/java/com/aethelgard/product/MarginReliefTest.java
 * Purpose: F-059 witness — rift trough, collide slope, lip blend
 * Audience: Agents / CI
 * Update when: F-059 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MarginReliefTest {

  private static final int WIDTH = 40;
  private static final int HEIGHT = 8;
  private static final int BOUNDARY_X = 19;
  private static final int ROW = 4;

  @Test
  @DisplayName("FR-1: split trough is thinnest on the contact and 8 at distance 8; continent stays")
  void divergeTroughSparesContinent() {
    Grid occupancy = Occupancy.seed(WIDTH, HEIGHT);
    int[] thickness = Lockers.oceanic(WIDTH * HEIGHT).thicknesses();
    int contact = Occupancy.id(BOUNDARY_X, ROW, WIDTH);
    int beside = Occupancy.id(BOUNDARY_X - 1, ROW, WIDTH);
    int at8 = Occupancy.id(BOUNDARY_X - 8, ROW, WIDTH);
    int beyond = Occupancy.id(BOUNDARY_X - 9, ROW, WIDTH);
    thickness[contact] = 20;
    thickness[beyond] = 3;
    Lockers result = MarginRelief.apply(occupancy, new Lockers(thickness), separate(), 0L);

    assertEquals(20, result.thickness(contact), "continental contact locker unchanged");
    assertEquals(MarginRelief.trough(1), result.thickness(beside));
    assertTrue(result.thickness(beside) < Lockers.T_OCEAN);
    assertEquals(Lockers.T_OCEAN, result.thickness(at8));
    assertEquals(3, result.thickness(beyond));
    int oceanContact = Occupancy.id(BOUNDARY_X, ROW - 1, WIDTH);
    assertEquals(MarginRelief.trough(0), result.thickness(oceanContact));
    assertTrue(result.thickness(oceanContact) < result.thickness(at8));
  }

  @Test
  @DisplayName("FR-2: collide bonus falls off to 0 at distance 4 and stays below 16")
  void collideSlopeStaysOceanic() {
    Grid occupancy = Occupancy.seed(WIDTH, HEIGHT);
    int[] thickness = Lockers.oceanic(WIDTH * HEIGHT).thicknesses();
    int deep = Occupancy.id(BOUNDARY_X, ROW, WIDTH);
    int nearLand = Occupancy.id(BOUNDARY_X, ROW - 1, WIDTH);
    int at3 = Occupancy.id(BOUNDARY_X - 3, ROW, WIDTH);
    int at4 = Occupancy.id(BOUNDARY_X - 4, ROW, WIDTH);
    thickness[deep] = 8;
    thickness[nearLand] = 14;
    thickness[at4] = 5;
    Lockers result = MarginRelief.apply(occupancy, new Lockers(thickness), collide(), 1L);

    assertEquals(8 + MarginRelief.collideBonus(0), result.thickness(deep));
    assertEquals(15, result.thickness(nearLand));
    assertTrue(result.thickness(nearLand) < Lockers.T_LAND);
    assertEquals(8 + MarginRelief.collideBonus(3), result.thickness(at3));
    assertEquals(0, MarginRelief.collideBonus(4));
    assertEquals(5, result.thickness(at4));
  }

  @Test
  @DisplayName("FR-3: lip blend is not a straight band of 8s and repeats for the same seed")
  void lipBlendIsDeterministic() {
    long seed = mixedLipSeed();
    Grid occupancy = Occupancy.seed(WIDTH, HEIGHT);
    Lockers once =
        MarginRelief.apply(occupancy, Lockers.oceanic(WIDTH * HEIGHT), separate(), seed);
    Lockers twice =
        MarginRelief.apply(occupancy, Lockers.oceanic(WIDTH * HEIGHT), separate(), seed);
    assertEquals(once, twice);

    int blended = 0;
    int plain = 0;
    for (int y = 0; y < HEIGHT; y++) {
      int id = Occupancy.id(BOUNDARY_X - 4, y, WIDTH);
      int thickness = once.thickness(id);
      assertNotEquals(Lockers.T_OCEAN, thickness);
      if (MarginRelief.selectsLip(seed, BOUNDARY_X - 4, y)) {
        assertEquals((MarginRelief.trough(4) + Lockers.T_OCEAN) / 2, thickness);
        blended++;
      } else {
        assertEquals(MarginRelief.trough(4), thickness);
        plain++;
      }
    }
    assertTrue(blended > 0 && plain > 0);
  }

  @Test
  @DisplayName("FR-4: isostasy, determinism, ProductGeneration matches engine, no engine edits")
  void determinismAndNoEngineEdits() throws Exception {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(2);
    b.advance(2);
    assertEquals(a.settled().field(WorldFields.OCCUPANCY), b.settled().field(WorldFields.OCCUPANCY));
    assertEquals(a.settled().field(WorldFields.LOCKERS), b.settled().field(WorldFields.LOCKERS));
    assertEquals(a.settled().field(WorldFields.ELEVATION), b.settled().field(WorldFields.ELEVATION));

    Grid occ = (Grid) a.settled().field(WorldFields.OCCUPANCY);
    Lockers loc = (Lockers) a.settled().field(WorldFields.LOCKERS);
    assertEquals(ThicknessToElevation.apply(occ, loc), a.settled().field(WorldFields.ELEVATION));

    Engine engine = ProductHost.create(spec);
    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(
            (Grid) engine.settled().field(WorldFields.PLATES),
            (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY),
            (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY),
            (Grid) engine.settled().field(WorldFields.OCCUPANCY),
            (Lockers) engine.settled().field(WorldFields.LOCKERS),
            (Grid) engine.settled().field(WorldFields.ELEVATION));
    engine.advance(2);
    for (int g = 1; g <= 2; g++) {
      state = ProductGeneration.advance(state, g);
    }
    assertEquals(state.occupancy(), engine.settled().field(WorldFields.OCCUPANCY));
    assertEquals(state.lockers(), engine.settled().field(WorldFields.LOCKERS));
    assertEquals(state.elevation(), engine.settled().field(WorldFields.ELEVATION));

    Path root = findRepoRoot();
    String engineJava =
        Files.readString(root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertFalse(engineJava.contains("MarginRelief"));
  }

  private static long mixedLipSeed() {
    for (long seed = 0; seed < 64; seed++) {
      int selected = 0;
      for (int y = 0; y < HEIGHT; y++) {
        if (MarginRelief.selectsLip(seed, BOUNDARY_X - 4, y)) {
          selected++;
        }
      }
      if (selected > 0 && selected < HEIGHT) {
        return seed;
      }
    }
    throw new IllegalStateException("no mixed lip seed");
  }

  private static Boundaries separate() {
    return contacts(BoundaryKind.SEPARATE);
  }

  private static Boundaries collide() {
    return contacts(BoundaryKind.COLLIDE);
  }

  private static Boundaries contacts(BoundaryKind kind) {
    List<BoundaryContact> contacts = new ArrayList<>();
    for (int y = 0; y < HEIGHT; y++) {
      contacts.add(new BoundaryContact(BOUNDARY_X, y, 1, 0, 0, 1, kind));
    }
    return new Boundaries(contacts);
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
