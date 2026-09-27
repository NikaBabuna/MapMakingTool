/*
 * File: product/src/test/java/com/aethelgard/product/world/GenerationTest.java
 * Purpose: Proves what a whole generation does: nine phases in order, the same result as the reference pipeline, isostasy, determinism, and land that forms and rides
 * Audience: Agents / CI
 * Update when: The phase list, ProductGeneration, or the canonical dump changes
 */

package com.aethelgard.product.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.PoolSnapshot;
import com.aethelgard.engine.system.EngineSystem;
import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.product.session.ProductSession;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.fields.WorldSpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GenerationTest {

  private static final List<String> PHASES =
      List.of(
          "trace-boundaries",
          "boundary-interaction",
          "integrate-velocity",
          "apply-geometry",
          "orogeny",
          "ridge-create",
          "margin-relief",
          "continental-collide",
          "isostasy");

  /** Proves F-068 FR-39 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Step 0 runs no phase; every later step runs the nine phases in order, as one system")
  void laterStepsRunTheNinePhasesInOrder() {
    Engine engine = ProductHost.create(new WorldSpec(16, 8, 1L));
    assertTrue(engine.lastClaimFinish().finishedSystemIds().isEmpty(), "step 0 runs no phase");

    engine.advance();
    assertEquals(List.of(ProductHost.TECTONICS_SYSTEM_ID), engine.lastClaimFinish().finishedSystemIds());
    engine.advance();
    assertEquals(List.of(ProductHost.TECTONICS_SYSTEM_ID), engine.lastClaimFinish().finishedSystemIds());

    assertEquals(1, engine.systems().size());
    EngineSystem tectonics = engine.systems().getFirst();
    List<SubSystem> subs = tectonics.config().subSystems();
    assertEquals(PHASES, subs.stream().map(SubSystem::id).toList(), "registration order");
    List<SubSystem> shuffled = new ArrayList<>(subs);
    Collections.reverse(shuffled);
    List<String> resolved = tectonics.config().conflictResolver().resolveOrder(shuffled).stream().map(SubSystem::id).toList();
    assertEquals(PHASES.stream().filter(resolved::contains).toList(), resolved, "overlapping phases run in pipeline order");
  }

  /** Proves F-068 FR-40 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The engine run equals the reference pipeline, generation by generation")
  void engineMatchesTheReferencePipeline() {
    for (WorldSpec spec : List.of(new WorldSpec(32, 16, 5L), new WorldSpec(24, 24, 40L))) {
      Engine engine = ProductHost.create(spec);
      for (int g = 1; g <= 6; g++) {
        ProductGeneration.Snapshot expected = ProductGeneration.advance(snapshot(engine.settled()), g);
        engine.advance();
        ProductGeneration.Snapshot actual = snapshot(engine.settled());
        assertEquals(expected.plates(), actual.plates(), spec + " generation " + g + " plates");
        assertEquals(expected.velocities(), actual.velocities(), spec + " generation " + g + " velocities");
        assertEquals(expected.registry(), actual.registry(), spec + " generation " + g + " registry");
        assertEquals(expected.occupancy(), actual.occupancy(), spec + " generation " + g + " occupancy");
        assertEquals(expected.lockers(), actual.lockers(), spec + " generation " + g + " lockers");
        assertEquals(expected.elevation(), actual.elevation(), spec + " generation " + g + " elevation");
      }
    }
  }

  /** Proves F-068 FR-38 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("After every generation each cell's elevation is its crust's thickness minus 8")
  void everyGenerationSettlesIsostasy() {
    Engine engine = ProductHost.create(new WorldSpec(48, 24, 3L));
    for (int g = 1; g <= 10; g++) {
      engine.advance();
      PoolSnapshot s = engine.settled();
      Grid occupancy = (Grid) s.field(WorldFields.OCCUPANCY);
      Lockers lockers = (Lockers) s.field(WorldFields.LOCKERS);
      Grid elevation = (Grid) s.field(WorldFields.ELEVATION);
      for (int y = 0; y < occupancy.height(); y++) {
        for (int x = 0; x < occupancy.width(); x++) {
          assertEquals(lockers.thickness(occupancy.get(x, y)) - 8, elevation.get(x, y), "generation " + g + " (" + x + "," + y + ")");
        }
      }
    }
  }

  /** Proves F-068 FR-41 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The same seed and step count give the same fields")
  void sameSeedSameWorld() {
    WorldSpec spec = new WorldSpec(48, 24, 11L);
    ProductSession a = new ProductSession(spec);
    ProductSession b = new ProductSession(spec);
    a.advance(8);
    b.advance(3);
    b.advance(5);

    assertEquals(a.settledWorld(), b.settledWorld());
    assertEquals(a.elevation(), b.elevation());
    assertEquals(a.plates(), b.plates());
  }

  /** Proves F-068 FR-41 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The DEFAULT world after 3 steps equals the stored dump")
  void defaultWorldMatchesTheGoldenDump() throws IOException {
    ProductSession session = ProductSession.ofDefault();
    session.advance(3);

    String golden;
    try (InputStream in = GenerationTest.class.getResourceAsStream("/worlds/default-n3.txt")) {
      assertNotNull(in, "the stored dump is on the test class path");
      golden = new String(in.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
    assertEquals(golden, session.settledWorld());
  }

  /** Proves F-068 FR-42 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Where plates collide, land (thickness 16 or more) forms and rides with its plate")
  void continentsFormAndRide() {
    Engine engine = ProductHost.create(new WorldSpec(64, 32, 0L));
    engine.advance();
    assertTrue(landCells(engine.settled()) > 0, "land formed in the first generation");

    // Land rides: a land cell at step g+1 holds the column that stood one velocity step behind it at step g.
    int rode = 0;
    for (int g = 0; g < 5; g++) {
      PoolSnapshot before = engine.settled();
      engine.advance();
      PoolSnapshot after = engine.settled();
      Grid occBefore = (Grid) before.field(WorldFields.OCCUPANCY);
      Grid occAfter = (Grid) after.field(WorldFields.OCCUPANCY);
      Grid plates = (Grid) after.field(WorldFields.PLATES);
      PlateVelocities v = (PlateVelocities) after.field(WorldFields.PLATE_VELOCITY);
      Lockers lockers = (Lockers) after.field(WorldFields.LOCKERS);
      int w = plates.width();
      int h = plates.height();
      for (int y = 1; y < h - 1; y++) {
        for (int x = 0; x < w; x++) {
          int p = plates.get(x, y);
          int column = occAfter.get(x, y);
          int sy = y - v.vy(p);
          if (lockers.thickness(column) < 16 || (v.vx(p) == 0 && v.vy(p) == 0) || sy < 0 || sy >= h) {
            continue;
          }
          if (occBefore.get(Math.floorMod(x - v.vx(p), w), sy) == column) {
            rode++;
          }
        }
      }
      assertTrue(landCells(after) > 0, "land persists at step " + engine.stepIndex());
    }
    assertTrue(rode > 0, "land columns moved with their plates");
  }

  private static int landCells(PoolSnapshot s) {
    Grid occupancy = (Grid) s.field(WorldFields.OCCUPANCY);
    Lockers lockers = (Lockers) s.field(WorldFields.LOCKERS);
    int n = 0;
    for (int y = 0; y < occupancy.height(); y++) {
      for (int x = 0; x < occupancy.width(); x++) {
        n += lockers.thickness(occupancy.get(x, y)) >= 16 ? 1 : 0;
      }
    }
    return n;
  }

  private static ProductGeneration.Snapshot snapshot(PoolSnapshot s) {
    return new ProductGeneration.Snapshot(
        (Grid) s.field(WorldFields.PLATES),
        (PlateVelocities) s.field(WorldFields.PLATE_VELOCITY),
        (PlateRegistry) s.field(WorldFields.PLATE_REGISTRY),
        (Grid) s.field(WorldFields.OCCUPANCY),
        (Lockers) s.field(WorldFields.LOCKERS),
        (Grid) s.field(WorldFields.ELEVATION));
  }
}
