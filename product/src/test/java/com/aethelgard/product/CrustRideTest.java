/*
 * File: product/src/test/java/com/aethelgard/product/CrustRideTest.java
 * Purpose: F-056 witness — occupancy keys, locker ride, isostasy elevation
 * Audience: Agents / CI
 * Update when: F-056 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineSetup;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CrustRideTest {

  @Test
  @DisplayName("FR-1: occupancy + lockers at Step 0; elevation 0; plates still ownership")
  void stepZeroOceanicCover() {
    WorldSpec spec = new WorldSpec(3, 2, 0L);
    Engine engine = ProductHost.create(spec);
    EngineSetup setup = ProductHost.setup();
    assertTrue(setup.fieldSchema().has(WorldFields.OCCUPANCY));
    assertTrue(setup.fieldSchema().has(WorldFields.LOCKERS));
    assertEquals(FieldType.STATIC, setup.fieldSchema().typeOf(WorldFields.OCCUPANCY));
    assertEquals(FieldType.STATIC, setup.fieldSchema().typeOf(WorldFields.LOCKERS));

    Grid occupancy = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    Lockers lockers = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    assertEquals(3, occupancy.width());
    assertEquals(2, occupancy.height());
    assertEquals(6, lockers.count());
    for (int y = 0; y < 2; y++) {
      for (int x = 0; x < 3; x++) {
        assertEquals(Occupancy.id(x, y, 3), occupancy.get(x, y));
        assertEquals(Lockers.T_OCEAN, lockers.thickness(occupancy.get(x, y)));
        assertEquals(0, elevation.get(x, y));
      }
    }
    assertInstanceOf(Grid.class, plates);
    assertNotEquals(occupancy, plates, "occupancy is not plate ownership");
  }

  @Test
  @DisplayName("FR-2: unique-dest interiors keep the same locker ids after advection")
  void occupancyRidesWithPlateVelocity() {
    int width = 4;
    int height = 1;
    Grid plates = new Grid(new int[][] {{0, 0, 0, 0}});
    Grid occupancy = Occupancy.seed(width, height);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1}, new int[] {0});
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(plates, occupancy, vel, 1, Boundaries.empty());
    // Each cell x goes to x+1 (wrap). Occupancy at dest is the source locker.
    assertEquals(3, moved.occupancy().get(0, 0));
    assertEquals(0, moved.occupancy().get(1, 0));
    assertEquals(1, moved.occupancy().get(2, 0));
    assertEquals(2, moved.occupancy().get(3, 0));
    assertEquals(0, moved.plates().get(0, 0));
  }

  @Test
  @DisplayName("FR-3: elevation is isostasy; Orogeny does not write elevation")
  void elevationIsIsostasyOnly() {
    Grid occupancy = Occupancy.seed(2, 1);
    int[] t = {Lockers.T_OCEAN, Lockers.T_OCEAN + 3};
    Lockers lockers = new Lockers(t);
    Grid elevation = ThicknessToElevation.apply(occupancy, lockers);
    assertEquals(0, elevation.get(0, 0));
    assertEquals(3, elevation.get(1, 0));

    List<String> ids =
        ProductHost.setup().systems().getFirst().config().subSystems().stream()
            .map(s -> s.id())
            .toList();
    assertEquals("isostasy", ids.get(ids.size() - 1));
    assertTrue(ids.contains("orogeny"));
    assertTrue(ids.contains("ridge-create"));

    Engine engine = ProductHost.create(new WorldSpec(2, 1, 0L));
    engine.advance(1);
    Grid occ = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    Lockers loc = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    Grid elev = (Grid) engine.settled().field(WorldFields.ELEVATION);
    assertEquals(ThicknessToElevation.apply(occ, loc), elev);
  }

  @Test
  @DisplayName("FR-4: contact stamps thicken lockers and ride with occupancy")
  void stampsRideOnLockers() {
    PlateRegistry reg =
        new PlateRegistry(0L, new int[] {10, 5}, new int[] {1, 0}, new int[] {0, 0});
    Boundaries collide =
        new Boundaries(List.of(new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE)));
    Grid occupancy = Occupancy.seed(2, 1);
    Lockers oceanic = Lockers.oceanic(2);
    Lockers stamped = Orogeny.applyToLockers(collide, reg, occupancy, oceanic);
    assertEquals(Lockers.T_OCEAN + 1, stamped.thickness(0));
    assertEquals(Lockers.T_OCEAN - 1, stamped.thickness(1));

    Grid plates = new Grid(new int[][] {{0, 1}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1, 1}, new int[] {0, 0});
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(plates, occupancy, vel, 1, Boundaries.empty());
    Grid elev = ThicknessToElevation.apply(moved.occupancy(), stamped);
    // Both cells move +1 x (wrap): locker 0 (winner +1) is now at x=1; locker 1 at x=0.
    assertEquals(-1, elev.get(0, 0));
    assertEquals(1, elev.get(1, 0));
    assertNotEquals(1, elev.get(0, 0), "stamp must not stay on standing coordinates");
  }

  @Test
  @DisplayName("FR-5: determinism; ProductGeneration matches engine; no engine source edits")
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

    Path root = findRepoRoot();
    String engineJava =
        Files.readString(root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertFalse(engineJava.contains("occupancy"), "no engine production edits for occupancy");
    assertTrue(
        ProductHost.setup().systems().size() == 1,
        "one tectonics System");
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
