/*
 * File: product/src/test/java/com/aethelgard/product/StepPathHotspotsTest.java
 * Purpose: F-046 witness — phase collectors, crumb 0.01%, advection hotspot
 * Audience: Agents / CI
 * Update when: F-046 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StepPathHotspotsTest {

  @Test
  @DisplayName("FR-1/FR-2: phase collectors record on advance; listReport includes them")
  void phaseCollectorsRecordAndList() {
    ProductSession session = ProductSession.ofDefault();
    DiagnosticsHub hub = session.diagnostics();
    assertTrue(hub.has(DiagnosticIds.PHASE_TRACE));
    assertTrue(hub.has(DiagnosticIds.PHASE_INTERACTION));
    assertTrue(hub.has(DiagnosticIds.PHASE_INTEGRATE));
    assertTrue(hub.has(DiagnosticIds.PHASE_APPLY));
    assertTrue(hub.has(DiagnosticIds.PHASE_OROGENY));
    assertTrue(hub.has(DiagnosticIds.PHASE_ISOSTASY));
    assertTrue(hub.has(DiagnosticIds.ADVANCE_WALL));

    session.advance(2);
    assertEquals(2, hub.get(DiagnosticIds.PHASE_TRACE).size());
    assertEquals(2, hub.get(DiagnosticIds.PHASE_INTERACTION).size());
    assertEquals(2, hub.get(DiagnosticIds.PHASE_INTEGRATE).size());
    assertEquals(2, hub.get(DiagnosticIds.PHASE_APPLY).size());
    assertEquals(2, hub.get(DiagnosticIds.PHASE_OROGENY).size());
    assertEquals(2, hub.get(DiagnosticIds.PHASE_ISOSTASY).size());
    assertTrue(hub.get(DiagnosticIds.PHASE_APPLY).latest() > 0);

    String list = hub.listReport();
    assertTrue(list.contains(DiagnosticIds.PHASE_TRACE));
    assertTrue(list.contains(DiagnosticIds.PHASE_OROGENY));
    assertTrue(list.contains(DiagnosticIds.ADVANCE_WALL));
  }

  @Test
  @DisplayName("FR-3: advection no longer allocates whoMin grid")
  void advectDropsWhoMinAllocation() throws Exception {
    Path root = findRepoRoot();
    String src =
        Files.readString(
            root.resolve("product/src/main/java/com/aethelgard/product/PlateKinematics.java"));
    assertFalse(src.contains("whoMin"), "whoMin grid removed as step-path hotspot");
    assertTrue(src.contains("claims[ny][nx] == 1") || src.contains("claims[ny][nx]==1"));
  }

  @Test
  @DisplayName("FR-4: crumb bar 0.01% absorbs under-bar island; over-bar survives")
  void crumbBarOneHundredthPercent() {
    // 200×200 = 40_000; 0.01% absorbs size < 4. Size-2 absorbs; size-5 survives.
    int w = 200;
    int h = 200;
    int[][] absorbCells = filled(w, h, 0);
    absorbCells[10][10] = 1;
    absorbCells[10][11] = 1;
    Grid absorbPlates = new Grid(absorbCells);
    PlateVelocities vel = new PlateVelocities(0L, new int[] {0, 0}, new int[] {0, 0});
    ApplyGeometry.Result absorbed =
        ApplyGeometry.apply(
            absorbPlates,
            Boundaries.empty(),
            AreaFlux.zeros(2),
            PlateRegistry.from(absorbPlates, vel),
            vel);
    assertEquals(0, absorbed.plates().get(10, 10));
    assertEquals(0, absorbed.plates().get(11, 10));

    int[][] keepCells = filled(w, h, 0);
    for (int i = 0; i < 5; i++) {
      keepCells[20][20 + i] = 1;
    }
    Grid keepPlates = new Grid(keepCells);
    ApplyGeometry.Result kept =
        ApplyGeometry.apply(
            keepPlates,
            Boundaries.empty(),
            AreaFlux.zeros(2),
            PlateRegistry.from(keepPlates, vel),
            vel);
    int survivors = 0;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (kept.plates().get(x, y) != 0) {
          survivors++;
        }
      }
    }
    assertEquals(5, survivors, "size-5 island should survive 0.01% crumb bar");
    assertEquals(ApplyGeometry.CRUMB_DENOMINATOR, 10_000);
  }

  @Test
  @DisplayName("FR-5: diag on/off does not change world dump")
  void phasesDoNotAffectWorld() {
    ProductSession a = ProductSession.ofDefault();
    a.advance(3);
    String dumpA = a.settledWorld();

    ProductSession b = ProductSession.ofDefault();
    for (String id :
        new String[] {
          DiagnosticIds.PHASE_TRACE,
          DiagnosticIds.PHASE_INTERACTION,
          DiagnosticIds.PHASE_INTEGRATE,
          DiagnosticIds.PHASE_APPLY,
          DiagnosticIds.PHASE_OROGENY,
          DiagnosticIds.PHASE_ISOSTASY,
          DiagnosticIds.ADVANCE_WALL,
          DiagnosticIds.HEAP_USED,
          DiagnosticIds.HEAP_MAX
        }) {
      b.diagnostics().setEnabled(id, false);
    }
    b.advance(3);
    assertEquals(dumpA, b.settledWorld());
  }

  @Test
  @DisplayName("FR-6: wiki 0.01% crumb + phase collectors; no engine production edits")
  void docsAndNoEngineEdits() throws Exception {
    Path root = findRepoRoot();
    String wiki = Files.readString(root.resolve("docs/product/wiki/tectonics.md"));
    assertTrue(wiki.contains("0.01%"));

    String arch = Files.readString(root.resolve("docs/architecture/studio/session.md"));
    assertTrue(arch.contains("phase.trace") || arch.contains("phase collectors"));

    // No unexpected engine source edits this Step: Engine.java still serial System loop.
    String engine =
        Files.readString(
            root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertTrue(engine.contains("for (EngineSystem system : systems)"));
  }

  private static int[][] filled(int w, int h, int value) {
    int[][] cells = new int[h][w];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        cells[y][x] = value;
      }
    }
    return cells;
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath().normalize();
    for (Path cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("product"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new Exception("repo root not found");
  }
}
