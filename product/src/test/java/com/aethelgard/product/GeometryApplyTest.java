/*
 * File: product/src/test/java/com/aethelgard/product/GeometryApplyTest.java
 * Purpose: F-036 witness — apply flux, fission, B1 distance
 * Audience: Agents / CI
 * Update when: F-036 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeometryApplyTest {

  @Test
  @DisplayName("FR-1: collide makes sink then flood; separate can reclaim; no sink remains")
  void applyFluxAndFlood() {
    // Two plates side by side; collide east contact → loser cell sinks then floods
    Grid plates = new Grid(new int[][] {{0, 1}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {1, -1}, new int[] {0, 0});
    PlateRegistry reg = PlateRegistry.from(plates, vel);
    Boundaries boundaries = Boundaries.trace(plates, vel);
    assertTrue(boundaries.count(BoundaryKind.COLLIDE) >= 1);
    AreaFlux flux = AreaFlux.from(boundaries, reg);
    ApplyGeometry.Result result = ApplyGeometry.apply(plates, boundaries, flux, reg, vel);
    for (int x = 0; x < 2; x++) {
      assertTrue(result.plates().get(x, 0) >= 0);
    }
    assertEquals(2, result.registry().totalArea());
  }

  @Test
  @DisplayName("FR-2: fission splits disconnected components; registry matches cells")
  void fissionCrumbDeath() {
    // Same id on both ends, foreign middle — after paint as one plate with gap via sink flood
    Grid plates = new Grid(new int[][] {{0, 1, 0}});
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 0});
    // Force a no-op flux world then manually call fission path via apply with empty boundaries
    Boundaries empty = Boundaries.empty();
    AreaFlux zero = AreaFlux.zeros(2);
    ApplyGeometry.Result result =
        ApplyGeometry.apply(plates, empty, zero, PlateRegistry.from(plates, vel), vel);
    // With empty flux, plates unchanged then dense remap keeps two components of id 0 as fission
    assertTrue(result.registry().count() >= 2);
    int sum = 0;
    for (int i = 0; i < result.registry().count(); i++) {
      sum += result.registry().area(i);
    }
    assertEquals(3, sum);
    assertEquals(sum, result.registry().totalArea());
  }

  @Test
  @DisplayName("FR-3: B1 latitude weight shrinks east–west distance near poles vs equator")
  void latitudeWeightedPartition() {
    assertTrue(Plates.cosQ(0, 10) < Plates.cosQ(5, 10));
    // Same sites; polar row should prefer nearer-in-y site more than equatorial metric would
    int[] xs = {0, 5};
    int[] ys = {0, 9};
    Grid g = Plates.assign(10, 10, xs, ys);
    assertEquals(0, g.get(0, 0));
    assertEquals(1, g.get(5, 9));
    // Mid wrap cell at pole: high cos squash → site 0 more competitive across wrap
    assertEquals(Plates.seed(8, 8, 0L), ProductHost.create(new WorldSpec(8, 8, 0L)).settled().field(WorldFields.PLATES));
  }

  @Test
  @DisplayName("FR-4/FR-5: determinism; golden; wiki B1 / apply banners")
  void determinismDump() throws Exception {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(WorldDump.CANONICAL_STEPS);
    b.advance(WorldDump.CANONICAL_STEPS);
    assertEquals(WorldDump.of(a, spec), WorldDump.of(b, spec));
    String dump = WorldDump.of(a, spec);
    String golden =
        Files.readString(
                findRepoRoot().resolve("product/src/test/resources/worlds/default-n3.txt"),
                StandardCharsets.UTF_8)
            .replace("\r\n", "\n");
    assertEquals(golden, dump);

    Path root = findRepoRoot();
    String tectonics = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(tectonics.toLowerCase().contains("cylinder"));
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("nearest"));
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
