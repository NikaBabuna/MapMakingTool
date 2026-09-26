/*
 * File: product/src/test/java/com/aethelgard/product/WorldCreationTest.java
 * Purpose: Proves what a new world is at step 0: its size, its plates, flat ocean crust, and seeded velocities
 * Audience: Agents / CI
 * Update when: WorldSpec, the step-0 seed, or the plate partition rule changes
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WorldCreationTest {

  private static final List<Long> SEEDS = List.of(0L, 1L, 5L, 12L, 13L, 25L, -7L, 123_456_789L);

  /** Proves F-068 FR-18 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("VIEW is 1920 by 1080 with seed 0, and DEFAULT is 8 by 8 with seed 0")
  void specsFixSizeAndSeed() {
    assertEquals(new WorldSpec(1920, 1080, 0L), WorldSpec.VIEW);
    assertEquals(new WorldSpec(8, 8, 0L), WorldSpec.DEFAULT);

    ProductSession view = ProductSession.view();
    assertEquals(1920, view.plates().width());
    assertEquals(1080, view.plates().height());
    assertEquals(1920, view.elevation().width());
    assertEquals(1080, view.elevation().height());
    assertEquals(0, view.stepIndex());
  }

  /** Proves F-068 FR-18 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A world with a size that is not positive is refused")
  void invalidSizeIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> new WorldSpec(0, 8, 0L));
    assertThrows(IllegalArgumentException.class, () -> new WorldSpec(8, 0, 0L));
    assertThrows(IllegalArgumentException.class, () -> new WorldSpec(-4, 8, 0L));
  }

  /** Proves F-068 FR-19 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("At step 0 the map is divided among exactly 12 + (seed mod 13) plates, and every cell has one")
  void stepZeroDividesTheMapIntoPlates() {
    for (long seed : SEEDS) {
      ProductSession session = new ProductSession(new WorldSpec(64, 32, seed));
      int expected = 12 + (int) Math.floorMod(seed, 13L);
      Grid plates = session.plates();
      assertEquals(expected, session.plateRegistry().count(), "seed " + seed + ": plates in the registry");
      assertEquals(expected, session.plateVelocities().count(), "seed " + seed + ": plates with a velocity");
      int[] area = new int[expected];
      for (int y = 0; y < plates.height(); y++) {
        for (int x = 0; x < plates.width(); x++) {
          int id = plates.get(x, y);
          assertTrue(id >= 0 && id < expected, "seed " + seed + ": cell (" + x + "," + y + ") has plate " + id);
          area[id]++;
        }
      }
      for (int p = 0; p < expected; p++) {
        assertEquals(area[p], session.plateRegistry().area(p), "seed " + seed + ": area of plate " + p);
      }
    }
  }

  /** Proves F-068 FR-19 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Each cell belongs to its nearest site by latitude-weighted distance that wraps east to west; ties go to the lower index")
  void eachCellBelongsToItsNearestSite() {
    for (WorldSpec spec : List.of(new WorldSpec(64, 32, 0L), new WorldSpec(40, 20, 3L), new WorldSpec(30, 30, 17L))) {
      int n = 12 + (int) Math.floorMod(spec.seed(), 13L);
      int[] sx = new int[n];
      int[] sy = new int[n];
      for (int i = 0; i < n; i++) {
        sx[i] = Plates.siteX(spec.width(), spec.seed(), i);
        sy[i] = Plates.siteY(spec.height(), spec.seed(), i);
      }
      Grid plates = new ProductSession(spec).plates();
      for (int y = 0; y < spec.height(); y++) {
        for (int x = 0; x < spec.width(); x++) {
          assertEquals(nearestSite(x, y, sx, sy, spec.width(), spec.height()), plates.get(x, y),
              spec + " cell (" + x + "," + y + ")");
        }
      }
    }
  }

  /** Proves F-068 FR-20 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("At step 0 all crust is ocean of thickness 8, elevation is 0 everywhere, and no phase has run")
  void stepZeroIsFlatOcean() {
    WorldSpec spec = new WorldSpec(24, 12, 4L);
    Engine engine = ProductHost.create(spec);
    Grid occupancy = (Grid) engine.settled().field(WorldFields.OCCUPANCY);
    Lockers lockers = (Lockers) engine.settled().field(WorldFields.LOCKERS);
    Grid elevation = (Grid) engine.settled().field(WorldFields.ELEVATION);

    Set<Integer> lockerIds = new HashSet<>();
    for (int y = 0; y < spec.height(); y++) {
      for (int x = 0; x < spec.width(); x++) {
        assertEquals(8, lockers.thickness(occupancy.get(x, y)));
        assertEquals(0, elevation.get(x, y));
        lockerIds.add(occupancy.get(x, y));
      }
    }
    assertEquals(spec.width() * spec.height(), lockerIds.size(), "every cell has its own column of crust");
    assertTrue(engine.lastClaimFinish().finishedSystemIds().isEmpty(), "no phase ran in step 0");
  }

  /** Proves F-068 FR-21 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Each seeded velocity component is -1, 0 or 1, some plate moves, and the registry agrees with the plates")
  void seededVelocitiesAreUnitStepsAndSomePlateMoves() {
    for (long seed = 0; seed < 30; seed++) {
      ProductSession session = new ProductSession(new WorldSpec(32, 16, seed));
      PlateVelocities velocities = session.plateVelocities();
      PlateRegistry registry = session.plateRegistry();
      Grid plates = session.plates();
      boolean moving = false;
      int[] area = new int[registry.count()];
      for (int y = 0; y < plates.height(); y++) {
        for (int x = 0; x < plates.width(); x++) {
          area[plates.get(x, y)]++;
        }
      }
      for (int p = 0; p < velocities.count(); p++) {
        assertTrue(Math.abs(velocities.vx(p)) <= 1 && Math.abs(velocities.vy(p)) <= 1, "seed " + seed + " plate " + p);
        moving |= velocities.vx(p) != 0 || velocities.vy(p) != 0;
        assertEquals(velocities.vx(p), registry.vx(p));
        assertEquals(velocities.vy(p), registry.vy(p));
        assertEquals(area[p], registry.area(p), "seed " + seed + " plate " + p + " area");
      }
      assertTrue(moving, "seed " + seed + ": some plate moves");
      assertEquals(32 * 16, registry.totalArea());
    }
  }

  /** Proves F-068 FR-22 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The same spec builds the same world, and another seed builds another world")
  void sameSpecSameWorldAndOtherSeedOtherWorld() {
    WorldSpec spec = new WorldSpec(40, 20, 9L);
    ProductSession a = new ProductSession(spec);
    ProductSession b = new ProductSession(spec);
    assertEquals(a.settledWorld(), b.settledWorld());
    a.advance(4);
    b.advance(4);
    assertEquals(a.settledWorld(), b.settledWorld());

    ProductSession other = new ProductSession(new WorldSpec(40, 20, 10L));
    assertNotEquals(new ProductSession(spec).plates(), other.plates());
  }

  /** The seed page's rule: least latitude-weighted squared distance, wrapping in x, lowest index on a tie. */
  private static int nearestSite(int x, int y, int[] sx, int[] sy, int width, int height) {
    long q = Math.max(1, Math.round(1024 * Math.sin(Math.PI * (y + 0.5) / height)));
    int best = -1;
    long bestD = Long.MAX_VALUE;
    for (int i = 0; i < sx.length; i++) {
      int dx = Math.abs(x - sx[i]);
      dx = Math.min(dx, width - dx);
      long wx = (dx * q) / 1024;
      long dy = y - sy[i];
      long d = wx * wx + dy * dy;
      if (d < bestD) {
        bestD = d;
        best = i;
      }
    }
    return best;
  }
}
