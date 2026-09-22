/*
 * File: product/src/test/java/com/aethelgard/product/BoundaryInteractionTest.java
 * Purpose: F-035 witness — area_flux + motion_intent + precedence
 * Audience: Agents / CI
 * Update when: F-035 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoundaryInteractionTest {

  @Test
  @DisplayName("FR-1: area_flux and motion_intent seeded and refreshed on advance")
  void seedAndAdvance() {
    WorldSpec spec = new WorldSpec(4, 3, 0L);
    Engine engine = ProductHost.create(spec);
    Boundaries boundaries = (Boundaries) engine.settled().field(WorldFields.BOUNDARIES);
    PlateRegistry registry = (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    AreaFlux flux0 = (AreaFlux) engine.settled().field(WorldFields.AREA_FLUX);
    MotionIntent intent0 = (MotionIntent) engine.settled().field(WorldFields.MOTION_INTENT);
    assertEquals(AreaFlux.from(boundaries, registry), flux0);
    assertEquals(MotionIntent.from(boundaries, registry), intent0);
    assertEquals(0, flux0.netCells());

    Grid standingPlates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateVelocities standingVel =
        (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    PlateRegistry standingReg =
        (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    Boundaries standingBoundaries = Boundaries.trace(standingPlates, standingVel);

    engine.advance(1);
    // Flux/intent are from the standing snapshot (same Step as TraceBoundaries), not remapped ids.
    assertEquals(
        AreaFlux.from(standingBoundaries, standingReg),
        engine.settled().field(WorldFields.AREA_FLUX));
    assertEquals(
        MotionIntent.from(standingBoundaries, standingReg),
        engine.settled().field(WorldFields.MOTION_INTENT));
  }

  @Test
  @DisplayName("FR-2..FR-4: precedence, flux kinds, motion kinds")
  void precedenceAndBudgets() {
    PlateRegistry registry =
        new PlateRegistry(0L, new int[] {10, 3, 10}, new int[] {0, 0, 0}, new int[] {0, 0, 0});
    assertEquals(1, AreaFlux.loser(0, 1, registry));
    assertEquals(0, AreaFlux.winner(0, 1, registry));
    assertEquals(0, AreaFlux.loser(0, 2, registry)); // tie → lower id

    Boundaries boundaries =
        new Boundaries(
            java.util.List.of(
                new BoundaryContact(0, 0, 1, 0, 0, 1, BoundaryKind.SEPARATE),
                new BoundaryContact(1, 0, 1, 0, 0, 1, BoundaryKind.COLLIDE),
                new BoundaryContact(2, 0, 1, 0, 0, 1, BoundaryKind.PASS_BY),
                new BoundaryContact(0, 1, 0, 1, 1, 2, BoundaryKind.COLLIDE)));

    AreaFlux flux = AreaFlux.from(boundaries, registry);
    // SEPARATE: +1/+1; COLLIDE 0-1: loser 1 −1; COLLIDE 1-2: loser 1 −1; PASS_BY 0
    assertEquals(1, flux.deltaArea(0));
    assertEquals(1 - 1 - 1, flux.deltaArea(1));
    assertEquals(0, flux.deltaArea(2));
    assertEquals(-2 + 1 + 1, flux.sinkDelta());
    assertEquals(0, flux.netCells());

    MotionIntent intent = MotionIntent.from(boundaries, registry);
    // SEPARATE (0,1) east: A0 (−1,0) B1 (+1,0)
    // COLLIDE (0,1) east: damp A0 (−1,0) B1 (+1,0); loser1 slab (−1,0) → B1 net 0 from this contact
    // PASS_BY: 0
    // COLLIDE (1,2) south: damp A1 (0,−1) B2 (0,+1); loser1 slab (0,+1) → A1 net 0; B2 (0,+1)
    assertEquals(-1 - 1, intent.ix(0));
    assertEquals(0, intent.iy(0));
    assertEquals(1, intent.ix(1)); // SEPARATE +1; both COLLIDE nets 0 on loser
    assertEquals(0, intent.iy(1));
    assertEquals(0, intent.ix(2));
    assertEquals(1, intent.iy(2));
  }

  @Test
  @DisplayName("FR-5: Step 0 plates match seed; interaction budgets present before apply")
  void noGeometryApply() {
    WorldSpec spec = new WorldSpec(6, 4, 3L);
    Engine engine = ProductHost.create(spec);
    Grid plates0 = (Grid) engine.settled().field(WorldFields.PLATES);
    assertEquals(Plates.seed(spec.width(), spec.height(), spec.seed()), plates0);
    assertEquals(
        AreaFlux.from(
            (Boundaries) engine.settled().field(WorldFields.BOUNDARIES),
            (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY)),
        engine.settled().field(WorldFields.AREA_FLUX));
  }

  @Test
  @DisplayName("FR-6: determinism; dump lists flux+intent; wiki banners; no engine edits needed")
  void determinismDumpDocs() throws Exception {
    WorldSpec spec = new WorldSpec(8, 8, 7L);
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(2);
    b.advance(2);
    assertEquals(a.settled().field(WorldFields.AREA_FLUX), b.settled().field(WorldFields.AREA_FLUX));
    assertEquals(
        a.settled().field(WorldFields.MOTION_INTENT), b.settled().field(WorldFields.MOTION_INTENT));
    String dump = WorldDump.of(a, spec);
    assertEquals(dump, WorldDump.of(b, spec));
    assertTrue(dump.contains("area_flux:\n"));
    assertTrue(dump.contains("motion_intent:\n"));

    Path root = findRepoRoot();
    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.toLowerCase().contains("cylinder"));
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("area_flux"));
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
    throw new IllegalStateException("repo root not found");
  }
}
