/*
 * File: product/src/test/java/com/aethelgard/product/IntegrateVelocityTest.java
 * Purpose: F-037 witness — edge-driven velocity integrate
 * Audience: Agents / CI
 * Update when: F-037 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.system.SubSystem;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IntegrateVelocityTest {

  @Test
  @DisplayName("FR-1: clamp(v + sgn(intent)); zero intent leaves axis; all-stop forces plate 0")
  void integrateClampAndGuard() {
    PlateVelocities standing = new PlateVelocities(1L, new int[] {0, 1, -1}, new int[] {0, 0, 1});
    MotionIntent intent = new MotionIntent(new int[] {5, 0, -3}, new int[] {-2, 0, 0});
    PlateVelocities out = IntegrateVelocity.integrate(standing, intent);
    assertEquals(1, out.vx(0));
    assertEquals(-1, out.vy(0));
    assertEquals(1, out.vx(1));
    assertEquals(0, out.vy(1));
    assertEquals(-1, out.vx(2));
    assertEquals(1, out.vy(2));

    PlateVelocities stopped =
        IntegrateVelocity.integrate(
            new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 0}),
            MotionIntent.zeros(2));
    assertEquals(1, stopped.vx(0));
    assertEquals(0, stopped.vy(0));
    assertEquals(0, stopped.vx(1));
    assertEquals(0, stopped.vy(1));

    assertEquals(-1, IntegrateVelocity.clampUnit(-9));
    assertEquals(1, IntegrateVelocity.clampUnit(9));
    assertEquals(0, IntegrateVelocity.clampUnit(0));
  }

  @Test
  @DisplayName("FR-2: Sub-System order Trace→Interaction→Integrate→Apply→Orogeny; fission inherits")
  void pipelineOrderAndFissionInherit() {
    List<? extends SubSystem> subs = ProductHost.setup().systems().get(0).config().subSystems();
    assertEquals(
        List.of(
            "trace-boundaries",
            "boundary-interaction",
            "integrate-velocity",
            "apply-geometry",
            "orogeny"),
        subs.stream().map(SubSystem::id).toList());

    // Two tall blobs of plate 0 (span Y ≥ 3) separated by plate 1
    int[][] cells = new int[7][4];
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 4; x++) {
        if (y == 3) {
          cells[y][x] = 1;
        } else {
          cells[y][x] = 0;
        }
      }
    }
    Grid plates = new Grid(cells);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 0});
    PlateRegistry reg = PlateRegistry.from(plates, vel);
    MotionIntent push = new MotionIntent(new int[] {2, 0}, new int[] {0, 0});
    PlateVelocities integrated = IntegrateVelocity.integrate(vel, push);
    assertEquals(1, integrated.vx(0));
    ApplyGeometry.Result result =
        ApplyGeometry.apply(plates, Boundaries.empty(), AreaFlux.zeros(2), reg, integrated);
    assertTrue(result.registry().count() >= 3, "plate 0 should fission into two + plate 1");
    int inherited = 0;
    for (int i = 0; i < result.velocities().count(); i++) {
      if (result.velocities().vx(i) == 1 && result.registry().area(i) > 0) {
        inherited++;
      }
    }
    assertTrue(inherited >= 2, "fission children should inherit integrated vx");
  }

  @Test
  @DisplayName("FR-3: Step 0 keeps seeded velocities; generation 1+ nudges away from seed")
  void step0SeedThenIntegrate() {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine engine = ProductHost.create(spec);
    PlateVelocities seed = PlateVelocities.seed(spec.seed());
    PlateVelocities step0 = (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    assertEquals(seed, step0);

    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateRegistry reg = (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    Boundaries boundaries = Boundaries.trace(plates, step0);
    MotionIntent intent = MotionIntent.from(boundaries, reg);
    PlateVelocities expectedFirst = IntegrateVelocity.integrate(step0, intent);

    engine.advance(1);
    // After apply/fission, count may change; at least one generation must not freeze seed forever
    // when intent is non-zero on surviving plates — compare ProductGeneration path.
    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(plates, step0, reg, Grid.zeros(spec.width(), spec.height()));
    state = ProductGeneration.advance(state, 1);
    assertEquals(state.velocities(), engine.settled().field(WorldFields.PLATE_VELOCITY));
    assertEquals(expectedFirst.count(), IntegrateVelocity.integrate(step0, intent).count());
    if (!intentEqualsZero(intent)) {
      // Seed forever would keep step0; integrated standing may differ before remap
      assertNotEquals(step0, expectedFirst);
    }
  }

  @Test
  @DisplayName("FR-4/FR-5: determinism; golden; docs mention F-037 integrate")
  void determinismDumpDocs() throws Exception {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(WorldDump.CANONICAL_STEPS);
    b.advance(WorldDump.CANONICAL_STEPS);
    assertEquals(WorldDump.of(a, spec), WorldDump.of(b, spec));
    assertEquals(
        a.settled().field(WorldFields.PLATE_VELOCITY), b.settled().field(WorldFields.PLATE_VELOCITY));

    String dump = WorldDump.of(a, spec);
    String golden =
        Files.readString(
                findRepoRoot().resolve("product/src/test/resources/worlds/default-n3.txt"),
                StandardCharsets.UTF_8)
            .replace("\r\n", "\n");
    assertEquals(golden, dump);

    Path root = findRepoRoot();
    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(tectonics.contains("F-037") || tectonics.toLowerCase().contains("integrate"));
    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    assertTrue(!enginePom.contains("<artifactId>product</artifactId>"));
  }

  private static boolean intentEqualsZero(MotionIntent intent) {
    for (int i = 0; i < intent.plateCount(); i++) {
      if (intent.ix(i) != 0 || intent.iy(i) != 0) {
        return false;
      }
    }
    return true;
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
    throw new IllegalStateException("repo root not found from " + dir);
  }
}
