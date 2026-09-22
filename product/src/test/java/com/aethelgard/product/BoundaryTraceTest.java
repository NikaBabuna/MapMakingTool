/*
 * File: product/src/test/java/com/aethelgard/product/BoundaryTraceTest.java
 * Purpose: F-034 witness — boundaries field + classify + cylinder contacts
 * Audience: Agents / CI
 * Update when: F-034 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoundaryTraceTest {

  @Test
  @DisplayName("FR-1..FR-3: boundaries seeded and classified; east+south unique; sphere south")
  void boundariesSeedAndClassify() {
    WorldSpec spec = new WorldSpec(4, 3, 0L);
    Engine engine = ProductHost.create(spec);
    Boundaries at0 = (Boundaries) engine.settled().field(WorldFields.BOUNDARIES);
    assertTrue(at0.size() >= 1);
    Grid plates = (Grid) engine.settled().field(WorldFields.PLATES);
    PlateVelocities vel = (PlateVelocities) engine.settled().field(WorldFields.PLATE_VELOCITY);
    assertEquals(Boundaries.trace(plates, vel), at0);

    Set<String> keys = new HashSet<>();
    for (BoundaryContact c : at0.contacts()) {
      assertTrue(c.nx() == 1 && c.ny() == 0 || c.nx() == 0 && c.ny() == 1);
      assertTrue(c.plateA() != c.plateB());
      int[] b = SphereTopology.neighbor(c.x(), c.y(), c.nx(), c.ny(), plates.width(), plates.height());
      assertEquals(c.plateB(), plates.get(b[0], b[1]));
      assertEquals(Boundaries.classify(c.plateA(), c.plateB(), vel, c.nx(), c.ny()), c.kind());
      String key = c.x() + "," + c.y() + "," + c.nx() + "," + c.ny();
      assertTrue(keys.add(key), "duplicate edge " + key);
    }

    engine.advance(1);
    Boundaries after = (Boundaries) engine.settled().field(WorldFields.BOUNDARIES);
    // Same-Step snapshot: TraceBoundaries reads standing plates from before kinematics merge.
    assertEquals(Boundaries.trace(plates, vel), after);
  }

  @Test
  @DisplayName("FR-4/FR-6: cylinder docs; determinism; dump lists boundaries")
  void docsDeterminismDump() throws Exception {
    WorldSpec spec = new WorldSpec(8, 8, 7L);
    Engine a = ProductHost.create(spec);
    Engine b = ProductHost.create(spec);
    a.advance(2);
    b.advance(2);
    assertEquals(a.settled().field(WorldFields.BOUNDARIES), b.settled().field(WorldFields.BOUNDARIES));
    assertEquals(WorldDump.of(a, spec), WorldDump.of(b, spec));
    assertTrue(WorldDump.of(a, spec).contains("boundaries:\n"));

    Path root = findRepoRoot();
    String tectonics = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertTrue(
        tectonics.toLowerCase().contains("cylinder")
            || tectonics.toLowerCase().contains("sphere"));
    assertFalse(tectonics.contains("Code status:** not implemented"));
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
