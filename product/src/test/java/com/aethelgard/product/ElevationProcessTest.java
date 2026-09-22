/*
 * File: product/src/test/java/com/aethelgard/product/ElevationProcessTest.java
 * Purpose: F-015 witness — plates seed, generation tick, collision uplift (F-017 Voronoi)
 * Audience: Agents / CI
 * Update when: F-015 FRs change
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
import java.util.List;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ElevationProcessTest {

  @Test
  @DisplayName("FR-1: setup wires category tree, generation policy, and tectonics System")
  void setupWiresTreePolicyAndSystem() {
    EngineSetup setup = ProductHost.setup();
    assertTrue(setup.categoryTree().contains(ProductCategories.TECTONICS));
    assertInstanceOf(GenerationTickPolicy.class, setup.eventEmissionPolicy());
    assertEquals(1, setup.systems().size());
    assertEquals(ProductHost.TECTONICS_SYSTEM_ID, setup.systems().get(0).id());
    assertEquals(
        ProductCategories.TECTONICS,
        setup.systems().getFirst().config().assignedCategory().path());
    assertTrue(setup.fieldSchema().has(WorldFields.PLATES));
    assertEquals(FieldType.STATIC, setup.fieldSchema().typeOf(WorldFields.PLATES));
    assertEquals(FieldType.STATIC, setup.fieldSchema().typeOf(WorldFields.PLATE_VELOCITY));
  }

  @Test
  @DisplayName("FR-2: create leaves elevation zero and does not claim generation")
  void createDoesNotRunGeneration() {
    Engine engine = ProductHost.create(new WorldSpec(4, 2, 0L));
    assertEquals(0, engine.stepIndex());
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);
    assertZero(elevation);
    assertEquals(0, engine.lastClaimFinish().claimCount());
    assertTrue(
        engine.lastClaimResult().claimedByClaimer().values().stream().allMatch(List::isEmpty),
        "no events claimed on Step 0");
    assertTrue(engine.lastClaimResult().unmatched().isEmpty());
    assertTrue(engine.lastStepOutput().isEmpty());
  }

  @Test
  @DisplayName("FR-3: Step 0 seeds Voronoi plates from seed")
  void createSeedsPlatesFromSpec() {
    WorldSpec spec = new WorldSpec(5, 3, 2L);
    Engine engine = ProductHost.create(spec);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    assertEquals(5, plates.width());
    assertEquals(3, plates.height());
    assertEquals(Plates.seed(5, 3, 2L), plates);
    assertEquals(14, Plates.count(2L));

    Grid oneWide =
        (Grid) ProductHost.create(new WorldSpec(1, 4, 99L)).settled().field(WorldFields.PLATES);
    assertEquals(1, oneWide.width());
    assertEquals(Plates.seed(1, 4, 99L), oneWide);
  }

  @Test
  @DisplayName("FR-4: after advance, tectonics System writes elevation; not heartbeat")
  void advanceElevationIsSystemProduced() {
    Engine engine = ProductHost.create(new WorldSpec(8, 8, 0L));
    Grid before = (Grid) engine.settled().field(WorldFields.ELEVATION);
    assertZero(before);

    engine.advance(1);
    assertEquals(1, engine.stepIndex());
    assertEquals(1, engine.lastClaimFinish().claimCount());
    assertEquals(
        List.of(ProductHost.TECTONICS_SYSTEM_ID), engine.lastClaimFinish().finishedSystemIds());
    assertTrue(engine.lastStepOutput().asMap().containsKey(WorldFields.ELEVATION));
    assertEquals(
        ProductHost.TECTONICS_SYSTEM_ID,
        engine.lastStepOutput().asMap().get(WorldFields.ELEVATION).getFirst().systemId());

    Grid after = (Grid) engine.settled().field(WorldFields.ELEVATION);
    assertNotEquals(before, after);
    assertEquals(2L, engine.settled().value(), "skeleton heartbeat still ticks");
    assertNotEquals(engine.settled().value(), (long) after.get(0, 0));

    Grid platesAfter = (Grid) engine.settled().field(WorldFields.PLATES);
    Grid platesBefore = Plates.seed(8, 8, 0L);
    assertNotEquals(platesBefore, platesAfter, "kinematics must move plates");
  }

  @Test
  @DisplayName("FR-5: orogeny on standing plates; same seed matches")
  void collisionUpliftMatchesRule() {
    WorldSpec spec = new WorldSpec(4, 2, 0L);
    Engine engine = ProductHost.create(spec);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateVelocities vel = (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    PlateRegistry reg = (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    engine.advance(1);
    ProductGeneration.Snapshot afterOne =
        ProductGeneration.advance(
            new ProductGeneration.Snapshot(plates, vel, reg, Grid.zeros(4, 2)), 1);
    assertEquals(afterOne.elevation(), engine.settled().field(WorldFields.ELEVATION));

    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(plates, vel, reg, Grid.zeros(4, 2));
    state = ProductGeneration.advance(state, 1);
    engine.advance(1);
    state = ProductGeneration.advance(state, 2);
    assertEquals(state.elevation(), engine.settled().field(WorldFields.ELEVATION));

    Engine a = ProductHost.create(new WorldSpec(8, 8, 7L));
    Engine b = ProductHost.create(new WorldSpec(8, 8, 7L));
    a.advance(3);
    b.advance(3);
    assertEquals(
        a.settled().field(WorldFields.ELEVATION), b.settled().field(WorldFields.ELEVATION));
  }

  @Test
  @DisplayName("FR-6: wiki and architecture record the elevation process")
  void wikiAndArchitectureRecordProcess() throws Exception {
    Path root = findRepoRoot();
    String wiki = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    assertTrue(wiki.toLowerCase().contains("height"));
    assertTrue(wiki.toLowerCase().contains("tectonic"));
    assertTrue(wiki.toLowerCase().contains("crust"));

    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.toLowerCase().contains("elevation"));

    String index = Files.readString(root.resolve("docs/product/wiki/README.md"));
    assertTrue(index.contains("elevation.md"));
    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("wiki/elevation.md"));

    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("world/tectonics"));
    assertTrue(arch.contains("GenerationTickPolicy") || arch.toLowerCase().contains("emission"));
    assertTrue(arch.contains("plates"));
    assertTrue(arch.contains("EngineSystem") || arch.toLowerCase().contains("system"));
  }

  private static void assertZero(Grid grid) {
    for (int y = 0; y < grid.height(); y++) {
      for (int x = 0; x < grid.width(); x++) {
        assertEquals(0, grid.get(x, y), "cell (" + x + "," + y + ")");
      }
    }
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
