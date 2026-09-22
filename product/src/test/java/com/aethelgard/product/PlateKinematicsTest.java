/*
 * File: product/src/test/java/com/aethelgard/product/PlateKinematicsTest.java
 * Purpose: F-020 witness — velocities, advection, standing-plate uplift, dump
 * Audience: Agents / CI
 * Update when: F-020 FRs change
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlateKinematicsTest {

  private static final long GOLDEN = 0x9E3779B97F4A7C15L;
  private static final long SILVER = 0xBF58476D1CE4E5B9L;
  private static final long BRONZE = 0x94D049BB133111EBL;

  @Test
  @DisplayName("FR-1: plates STATIC, plate_velocity STATIC; one tectonics System; no Swing in new types")
  void schemaAndHostWiring() throws Exception {
    EngineSetup setup = ProductHost.setup();
    assertEquals(FieldType.STATIC, setup.fieldSchema().typeOf(WorldFields.PLATES));
    assertEquals(FieldType.STATIC, setup.fieldSchema().typeOf(WorldFields.PLATE_VELOCITY));
    assertEquals(1, setup.systems().size());
    assertEquals(ProductHost.TECTONICS_SYSTEM_ID, setup.systems().get(0).id());

    Engine engine = ProductHost.create(WorldSpec.DEFAULT);
    assertInstanceOf(PlateVelocities.class, engine.settled().field(WorldFields.PLATE_VELOCITY));

    Path root = findRepoRoot();
    assertFalse(
        Files.readString(root.resolve("product/src/main/java/com/aethelgard/product/PlateKinematics.java"))
            .contains("javax.swing"));
    assertFalse(
        Files.readString(root.resolve("product/src/main/java/com/aethelgard/product/PlateVelocities.java"))
            .contains("JFrame"));
    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    assertFalse(enginePom.contains("<artifactId>product</artifactId>"));
  }

  @Test
  @DisplayName("FR-2: mix axes 2/3 yield {-1,0,1}; not every plate is (0,0)")
  void velocitiesFromSeed() {
    for (long seed : new long[] {0L, 1L, 7L, 10L, -1L}) {
      PlateVelocities expected = independentVelocities(seed);
      PlateVelocities actual = PlateVelocities.seed(seed);
      assertEquals(expected, actual);
      assertEquals(Plates.count(seed), actual.count());
      assertTrue(actual.anyMoving(), "seed " + seed);
      for (int i = 0; i < actual.count(); i++) {
        assertTrue(actual.vx(i) >= -1 && actual.vx(i) <= 1);
        assertTrue(actual.vy(i) >= -1 && actual.vy(i) <= 1);
      }
    }
    assertEquals(-1, PlateVelocities.unit(0L));
    assertEquals(0, PlateVelocities.unit(1L));
    assertEquals(1, PlateVelocities.unit(2L));
  }

  @Test
  @DisplayName("FR-3: apply+advection pipeline matches ProductGeneration helper")
  void advectionMatchesIndependentRule() {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateVelocities vel = (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    PlateRegistry reg = (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    assertEquals(Plates.seed(8, 8, 0L), plates);

    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(plates, vel, reg, Grid.zeros(8, 8));
    engine.advance(1);
    state = ProductGeneration.advance(state, 1);
    assertEquals(state.plates(), engine.settled().field(WorldFields.PLATES));
    assertNotEquals(plates, engine.settled().field(WorldFields.PLATES));

    engine.advance(2);
    for (int g = 2; g <= 3; g++) {
      state = ProductGeneration.advance(state, g);
    }
    assertEquals(state.plates(), engine.settled().field(WorldFields.PLATES));

    Grid tiedFill = Plates.assign(3, 1, new int[] {0, 2}, new int[] {0, 0});
    assertEquals(new Grid(new int[][] {{0, 0, 1}}), tiedFill);
  }

  @Test
  @DisplayName("FR-4: elevation is standing-plate orogeny; plates follow ProductGeneration")
  void standingPlateUpliftUnchanged() {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateVelocities vel = (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    PlateRegistry reg = (PlateRegistry) engine.settled().field(WorldFields.PLATE_REGISTRY);
    ProductGeneration.Snapshot state =
        new ProductGeneration.Snapshot(plates, vel, reg, Grid.zeros(8, 8));
    engine.advance(3);
    for (int g = 1; g <= 3; g++) {
      state = ProductGeneration.advance(state, g);
    }
    assertEquals(state.elevation(), engine.settled().field(WorldFields.ELEVATION));
    assertEquals(state.plates(), engine.settled().field(WorldFields.PLATES));

    engine.advance(0);
    Engine tiny = ProductHost.create(new WorldSpec(1, 1, 0L));
    tiny.advance(5);
    assertZero((Grid) tiny.settled().field(WorldFields.ELEVATION));
  }

  @Test
  @DisplayName("FR-5: same seed matches; DEFAULT golden; VIEW 1920×1080 seed 0 (no VIEW advance)")
  void determinismDumpAndView() throws Exception {
    WorldSpec spec = WorldSpec.DEFAULT;
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(WorldDump.CANONICAL_STEPS);
    b.advance(WorldDump.CANONICAL_STEPS);
    assertEquals(WorldDump.of(a, spec), WorldDump.of(b, spec));
    assertEquals(a.settled().field(WorldFields.PLATES), b.settled().field(WorldFields.PLATES));
    assertEquals(a.settled().field(WorldFields.ELEVATION), b.settled().field(WorldFields.ELEVATION));
    assertEquals(
        a.settled().field(WorldFields.PLATE_VELOCITY), b.settled().field(WorldFields.PLATE_VELOCITY));

    String dump = WorldDump.of(a, spec);
    String golden =
        Files.readString(
                findRepoRoot().resolve("product/src/test/resources/worlds/default-n3.txt"),
                StandardCharsets.UTF_8)
            .replace("\r\n", "\n");
    assertEquals(golden, dump);
    assertTrue(dump.contains("plate_velocity:\n"));

    ProductSession view = ProductSession.view();
    assertEquals(1920, view.spec().width());
    assertEquals(1080, view.spec().height());
    assertEquals(0L, view.spec().seed());
    assertEquals(0, view.stepIndex());
    assertEquals(1920, view.plates().width());
    assertEquals(1080, view.plates().height());
  }

  @Test
  @DisplayName("FR-1/FR-3: generation claims tectonics; wiki records motion")
  void claimsAndWiki() throws Exception {
    Engine engine = ProductHost.create(WorldSpec.DEFAULT);
    engine.advance(1);
    assertEquals(1, engine.lastClaimFinish().claimCount());
    assertEquals(
        List.of(ProductHost.TECTONICS_SYSTEM_ID), engine.lastClaimFinish().finishedSystemIds());
    assertTrue(engine.lastStepOutput().asMap().containsKey(WorldFields.PLATES));
    assertEquals(
        ProductHost.TECTONICS_SYSTEM_ID,
        engine.lastStepOutput().asMap().get(WorldFields.PLATES).getFirst().systemId());
    assertEquals(
        ProductHost.TECTONICS_SYSTEM_ID,
        engine.lastStepOutput().asMap().get(WorldFields.ELEVATION).getFirst().systemId());

    Path root = findRepoRoot();
    String wiki = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    assertTrue(wiki.toLowerCase().contains("crust"));
    assertFalse(wiki.contains("Plates do not move."));
    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(world.toLowerCase().contains("sphere"));
    assertFalse(
        world.contains("| `plates` | Voronoi nearest-site ids from seed (6–15 sites) | unchanged (Constant) |"));
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("plate_velocity"));
    assertTrue(arch.contains("{-1,0,1}"));
  }

  private static PlateVelocities independentVelocities(long seed) {
    int n = 12 + (int) Math.floorMod(seed, 13L);
    int[] vx = new int[n];
    int[] vy = new int[n];
    boolean any = false;
    for (int i = 0; i < n; i++) {
      vx[i] = (int) Math.floorMod(independentMix(seed, i, 2), 3L) - 1;
      vy[i] = (int) Math.floorMod(independentMix(seed, i, 3), 3L) - 1;
      if (vx[i] != 0 || vy[i] != 0) {
        any = true;
      }
    }
    if (!any) {
      vx[0] = 1;
    }
    return new PlateVelocities(seed, vx, vy);
  }

  private static Grid independentAdvect(Grid plates, PlateVelocities vel, int generationIndex) {
    int width = plates.width();
    int height = plates.height();
    int n = vel.count();
    int[][] claims = new int[height][width];
    int[][] who = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int plate = plates.get(x, y);
        int nx = (int) Math.floorMod(x + (long) vel.vx(plate), (long) width);
        int ny = y + vel.vy(plate);
        if (ny < 0 || ny >= height) {
          continue;
        }
        claims[ny][nx]++;
        who[ny][nx] = plate;
      }
    }
    int[] siteX = new int[n];
    int[] siteY = new int[n];
    for (int i = 0; i < n; i++) {
      int origX = (int) Math.floorMod(independentMix(vel.seed(), i, 0), (long) width);
      int origY = (int) Math.floorMod(independentMix(vel.seed(), i, 1), (long) height);
      siteX[i] = (int) Math.floorMod(origX + (long) generationIndex * vel.vx(i), (long) width);
      long sy = (long) origY + (long) generationIndex * vel.vy(i);
      if (sy < 0L) {
        siteY[i] = 0;
      } else if (sy >= height) {
        siteY[i] = height - 1;
      } else {
        siteY[i] = (int) sy;
      }
    }
    Grid fill = independentAssign(width, height, siteX, siteY);
    int[][] next = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        next[y][x] = claims[y][x] == 1 ? who[y][x] : fill.get(x, y);
      }
    }
    return new Grid(next);
  }

  private static Grid independentSeed(int width, int height, long seed) {
    int n = 12 + (int) Math.floorMod(seed, 13L);
    int[] xs = new int[n];
    int[] ys = new int[n];
    for (int i = 0; i < n; i++) {
      xs[i] = (int) Math.floorMod(independentMix(seed, i, 0), (long) width);
      ys[i] = (int) Math.floorMod(independentMix(seed, i, 1), (long) height);
    }
    return independentAssign(width, height, xs, ys);
  }

  private static Grid independentAssign(int width, int height, int[] siteX, int[] siteY) {
    int n = siteX.length;
    int[][] cells = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int best = 0;
        long bestD2 = dist2Cylinder(x, y, siteX[0], siteY[0], width);
        for (int i = 1; i < n; i++) {
          long d2 = dist2Cylinder(x, y, siteX[i], siteY[i], width);
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

  private static long dist2Cylinder(int x, int y, int sx, int sy, int width) {
    long dx = Math.min(Math.abs((long) x - sx), (long) width - Math.abs((long) x - sx));
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
