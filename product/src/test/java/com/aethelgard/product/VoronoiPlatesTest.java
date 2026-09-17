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
    assertEquals(6, Plates.count(0L));
    assertEquals(independentSeed(8, 8, 0L), plates);
    assertIdsInRange(plates, 6);

    assertEquals(15, Plates.count(-1L));
    assertEquals(6, Plates.count(10L));
    Grid other = (Grid) ProductHost.create(new WorldSpec(5, 4, 7L)).settled().field(WorldFields.PLATES);
    assertEquals(independentSeed(5, 4, 7L), other);
  }

  @Test
  @DisplayName("FR-1: Euclidean nearest site; ties take lower index")
  void nearestSiteAndLowerIndexTies() {
    Grid tied = Plates.assign(3, 1, new int[] {0, 2}, new int[] {0, 0});
    assertEquals(new Grid(new int[][] {{0, 0, 1}}), tied);

    Grid coincident = Plates.assign(2, 2, new int[] {0, 0}, new int[] {0, 0});
    assertEquals(new Grid(new int[][] {{0, 0}, {0, 0}}), coincident);
  }

  @Test
  @DisplayName("FR-2: wiki and architecture document Voronoi mix; stripe is not current truth")
  void wikiAndArchitectureDocumentMix() throws Exception {
    Path root = findRepoRoot();
    String wiki = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    assertTrue(wiki.toLowerCase().contains("voronoi"));
    assertTrue(wiki.contains("6") && wiki.contains("15"));
    assertTrue(wiki.contains("floorMod"));
    assertTrue(wiki.contains("0x9E3779B97F4A7C15"));
    assertTrue(wiki.contains("0xBF58476D1CE4E5B9"));
    assertTrue(wiki.contains("0x94D049BB133111EB"));
    assertTrue(wiki.toLowerCase().contains("euclidean"));
    assertTrue(wiki.toLowerCase().contains("lower site index"));
    assertTrue(wiki.toLowerCase().contains("supersed"));
    assertTrue(!wiki.contains("xBoundary") && !wiki.contains("x < xBoundary"));

    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.toLowerCase().contains("voronoi"));
    assertTrue(!world.toLowerCase().contains("two-plate vertical"));

    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.toLowerCase().contains("voronoi"));
    assertTrue(arch.contains("plates"));
    assertTrue(arch.toLowerCase().contains("euclidean"));
    assertTrue(arch.toLowerCase().contains("lower site index"));
    assertTrue(arch.contains("6") && arch.contains("15"));
  }

  @Test
  @DisplayName("FR-3: collision +1 on standing plates; kinematics moves plates")
  void collisionUpliftOnVoronoiSutures() {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateVelocities vel = (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    Grid elevation = Grid.zeros(8, 8);
    engine.advance(3);
    Grid standing = plates;
    for (int g = 1; g <= 3; g++) {
      elevation = expectedUpliftOnce(standing, elevation);
      standing = PlateKinematics.advect(standing, vel, g);
    }
    assertEquals(elevation, engine.settled().field(WorldFields.ELEVATION));
    assertEquals(standing, engine.settled().field(WorldFields.PLATES));
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
    int n = 6 + (int) Math.floorMod(seed, 10L);
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
        long bestD2 = dist2(x, y, xs[0], ys[0]);
        for (int i = 1; i < n; i++) {
          long d2 = dist2(x, y, xs[i], ys[i]);
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

  private static long dist2(int x, int y, int sx, int sy) {
    long dx = (long) x - (long) sx;
    long dy = (long) y - (long) sy;
    return dx * dx + dy * dy;
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

  private static Grid expectedUpliftOnce(Grid plates, Grid elevation) {
    int[][] next = new int[plates.height()][plates.width()];
    for (int y = 0; y < plates.height(); y++) {
      for (int x = 0; x < plates.width(); x++) {
        int bump = Plates.hasForeignNeighbor(plates, x, y) ? 1 : 0;
        next[y][x] = elevation.get(x, y) + bump;
      }
    }
    return new Grid(next);
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
