/*
 * File: product/src/test/java/com/aethelgard/product/VoronoiPlatesTest.java
 * Purpose: F-017 witness — Voronoi plate count, mix, assignment, collision
 * Audience: Agents / CI
 * Update when: F-017 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VoronoiPlatesTest {

  private static final long GOLDEN = 0x9E3779B97F4A7C15L;
  private static final long SILVER = 0xBF58476D1CE4E5B9L;
  private static final long BRONZE = 0x94D049BB133111EBL;

  @Test
  @DisplayName("FR-1: create leaves elevation zero; plates are Voronoi of N sites")
  void createLeavesElevationZeroAndVoronoiPlates() {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    assertZero(elevation);
    assertEquals(independentMix(0L, 3, 1), Plates.mix(0L, 3, 1));
    assertEquals(12, Plates.count(0L));
    assertEquals(independentSeed(8, 8, 0L), plates);
    assertIdsInRange(plates, 12);

    assertEquals(24, Plates.count(-1L));
    assertEquals(22, Plates.count(10L));
    Grid other = (Grid) ProductHost.create(new WorldSpec(5, 4, 7L)).settled().field(WorldFields.PLATES);
    assertEquals(independentSeed(5, 4, 7L), other);
  }

  @Test
  @DisplayName("FR-1: toroidal nearest site; ties take lower index")
  void nearestSiteAndLowerIndexTies() {
    Grid tied = Plates.assign(3, 1, new int[] {0, 2}, new int[] {0, 0});
    assertEquals(new Grid(new int[][] {{0, 0, 1}}), tied);

    Grid coincident = Plates.assign(2, 2, new int[] {0, 0}, new int[] {0, 0});
    assertEquals(new Grid(new int[][] {{0, 0}, {0, 0}}), coincident);
  }

  @Test
  @DisplayName("FR-2: wiki and architecture document toroidal nearest-site mix")
  void wikiAndArchitectureDocumentMix() throws Exception {
    Path root = findRepoRoot();
    String wiki = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(wiki.toLowerCase().contains("nearest"));
    assertTrue(wiki.contains("12") && wiki.contains("24"));
    assertTrue(wiki.contains("retired"));
    assertTrue(!wiki.contains("xBoundary") && !wiki.contains("x < xBoundary"));

    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.toLowerCase().contains("torus") || world.toLowerCase().contains("cylinder"));
    assertTrue(!world.toLowerCase().contains("two-plate vertical"));

    String plates = Files.readString(root.resolve("product/src/main/java/com/aethelgard/product/Plates.java"));
    assertTrue(plates.contains("0x9E3779B97F4A7C15"));
    assertTrue(plates.contains("0xBF58476D1CE4E5B9"));
    assertTrue(plates.contains("0x94D049BB133111EB"));
    assertTrue(plates.contains("floorMod"));

    String arch = Files.readString(root.resolve("docs/architecture/world/plates.md"));
    assertTrue(arch.contains("plates"));
    assertTrue(arch.toLowerCase().contains("toroid") || arch.toLowerCase().contains("cylinder") || arch.contains("12"));
    assertTrue(arch.toLowerCase().contains("lower site index"));
    assertTrue(arch.contains("12") && arch.contains("24"));
  }

  @Test
  @DisplayName("FR-3: orogeny on standing plates; ProductGeneration moves plates")
  void collisionUpliftOnVoronoiSutures() {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(
            plates,
            (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY),
            (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY),
            Grid.zeros(8, 8));
    engine.advance(3);
    for (int g = 1; g <= 3; g++) {
      state = ProductGeneration.advance(state, g);
    }
    assertEquals(state.elevation(), engine.settled().field(WorldFields.ELEVATION));
    assertEquals(state.plates(), engine.settled().field(WorldFields.PLATES));
    assertNotEquals(plates, engine.settled().field(WorldFields.PLATES));

    Engine tiny = ProductHost.create(new WorldSpec(1, 1, 0L));
    tiny.advance(5);
    assertZero((Grid) tiny.settled().field(WorldFields.ELEVATION));
  }

  @Test
  @DisplayName("FR-4: same seed + geometry + N Steps match; dump included")
  void sameSeedMatches() {
    WorldSpec spec = new WorldSpec(8, 8, 7L);
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(4);
    b.advance(4);
    assertEquals(a.settled().field(WorldFields.PLATES), b.settled().field(WorldFields.PLATES));
    assertEquals(a.settled().field(WorldFields.ELEVATION), b.settled().field(WorldFields.ELEVATION));
    assertEquals(WorldDump.of(a, spec), WorldDump.of(b, spec));

    WorldSpec other = new WorldSpec(8, 8, 8L);
    Engine c = ProductHost.create(other);
    c.advance(4);
    assertNotEquals(WorldDump.of(a, spec), WorldDump.of(c, other));
  }

  @Test
  @DisplayName("FR-6: DEFAULT remains 8x8 seed 0")
  void defaultGeometryUnchanged() {
    assertEquals(8, WorldSpec.DEFAULT.width());
    assertEquals(8, WorldSpec.DEFAULT.height());
    assertEquals(0L, WorldSpec.DEFAULT.seed());
    Engine engine = ProductHost.create();
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    assertEquals(8, plates.width());
    assertEquals(8, plates.height());
  }

  private static Grid independentSeed(int width, int height, long seed) {
    int n = 12 + (int) Math.floorMod(seed, 13L);
    int[] xs = new int[n];
    int[] ys = new int[n];
    for (int i = 0; i < n; i++) {
      xs[i] = (int) Math.floorMod(independentMix(seed, i, 0), (long) width);
      ys[i] = (int) Math.floorMod(independentMix(seed, i, 1), (long) height);
    }
    int[][] cells = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int best = 0;
        long bestD2 = dist2B1(x, y, xs[0], ys[0], width, height);
        for (int i = 1; i < n; i++) {
          long d2 = dist2B1(x, y, xs[i], ys[i], width, height);
          if (d2 < bestD2) {
            bestD2 = d2;
            best = i;
          }
        }
        cells[y][x] = best;
      }
    }
    return new Grid(cells);
  }

  private static long dist2B1(int x, int y, int sx, int sy, int width, int height) {
    long dx = Math.min(Math.abs((long) x - sx), (long) width - Math.abs((long) x - sx));
    long dy = (long) y - (long) sy;
    double s = Math.sin(Math.PI * (y + 0.5) / height);
    int cosQ = Math.max(1, (int) Math.round(1024.0 * s));
    long dxw = (dx * cosQ) / 1024L;
    return dxw * dxw + dy * dy;
  }

  private static long independentMix(long seed, int siteIndex, int axis) {
    long z = seed;
    z ^= (long) siteIndex * GOLDEN;
    z ^= (long) axis * SILVER;
    z += GOLDEN;
    z = (z ^ (z >>> 30)) * SILVER;
    z = (z ^ (z >>> 27)) * BRONZE;
    return z ^ (z >>> 31);
  }

  private static void assertIdsInRange(Grid plates, int n) {
    for (int y = 0; y < plates.height(); y++) {
      for (int x = 0; x < plates.width(); x++) {
        int id = plates.get(x, y);
        assertTrue(id >= 0 && id < n, "cell (" + x + "," + y + ") id " + id);
      }
    }
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
