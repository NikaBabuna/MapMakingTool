/*
 * File: product/src/test/java/com/aethelgard/product/PlatePartitionTest.java
 * Purpose: F-033 witness — toroidal partition N=12–24 + plate_registry
 * Audience: Agents / CI
 * Update when: F-033 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlatePartitionTest {

  private static final long GOLDEN = 0x9E3779B97F4A7C15L;
  private static final long SILVER = 0xBF58476D1CE4E5B9L;
  private static final long BRONZE = 0x94D049BB133111EBL;

  @Test
  @DisplayName("FR-1: count 12–24; cylindrical nearest-site; ties lower index")
  void toroidalPartitionRule() {
    assertEquals(12, Plates.count(0L));
    assertEquals(24, Plates.count(-1L));
    assertEquals(22, Plates.count(10L));
    assertEquals(12, Plates.count(13L));

    // Flat would give site 1 at x=7; cylinder wraps X toward site 0
    Grid wrap = Plates.assign(8, 1, new int[] {0, 1}, new int[] {0, 0});
    assertEquals(0, wrap.get(7, 0));

    Grid tied = Plates.assign(3, 1, new int[] {0, 2}, new int[] {0, 0});
    assertEquals(new Grid(new int[][] {{0, 0, 1}}), tied);
  }

  @Test
  @DisplayName("FR-2: Step 0 full cover; ids in 0..N-1; elevation zero")
  void stepZeroFullCover() {
    WorldSpec spec = new WorldSpec(8, 8, 0L);
    Engine engine = ProductHost.create(spec);
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    int n = Plates.count(0L);
    assertEquals(independentSeed(8, 8, 0L), plates);
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        assertEquals(0, elevation.get(x, y));
        int id = plates.get(x, y);
        assertTrue(id >= 0 && id < n);
      }
    }
  }

  @Test
  @DisplayName("FR-3: plate_registry areas + velocities lockstep with plates / plate_velocity")
  void registrySkeleton() {
    WorldSpec spec = new WorldSpec(8, 8, 7L);
    ProductSession session = new ProductSession(spec);
    PlateRegistry registry = session.plateRegistry();
    PlateVelocities velocities = session.plateVelocities();
    Grid plates = session.plates();
    int n = Plates.count(7L);
    assertEquals(n, registry.count());
    assertEquals(n, velocities.count());
    assertEquals(8 * 8, registry.totalArea());
    for (int i = 0; i < n; i++) {
      assertEquals(velocities.vx(i), registry.vx(i));
      assertEquals(velocities.vy(i), registry.vy(i));
    }
    int[] counted = new int[n];
    for (int y = 0; y < plates.height(); y++) {
      for (int x = 0; x < plates.width(); x++) {
        counted[plates.get(x, y)]++;
      }
    }
    for (int i = 0; i < n; i++) {
      assertEquals(counted[i], registry.area(i));
    }
    Engine engine = ProductHost.create(spec);
    assertEquals(registry, engine.settled().field(WorldFields.PLATE_REGISTRY));
  }

  @Test
  @DisplayName("FR-5: docs + dump mention registry; tectonics code status live for partition")
  void docsAndDump() throws Exception {
    Path root = findRepoRoot();
    String tectonics = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(tectonics.contains("12"));
    String archPartition = Files.readString(root.resolve("docs/architecture/world/README.md"));
    assertTrue(archPartition.contains("plate_registry"));
    String arch = Files.readString(root.resolve("docs/architecture/world/README.md"));
    assertTrue(arch.contains("plate_registry") || arch.contains("12"));
    String dump = WorldDump.of(ProductHost.create(WorldSpec.DEFAULT), WorldSpec.DEFAULT);
    assertTrue(dump.contains("plate_registry:\n"));
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
