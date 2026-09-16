/*
 * File: product/src/test/java/com/aethelgard/product/MapViewTest.java
 * Purpose: F-018 witness — raster, MapController, busy, G-004 close docs
 * Audience: Agents / CI
 * Update when: F-018 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MapViewTest {

  @Test
  @DisplayName("FR-1: VIEW is 512x512 seed 0; DEFAULT stays 8x8; raster matches geometry")
  void viewSpecAndRasterGeometry() {
    assertEquals(512, WorldSpec.VIEW.width());
    assertEquals(512, WorldSpec.VIEW.height());
    assertEquals(0L, WorldSpec.VIEW.seed());
    assertEquals(8, WorldSpec.DEFAULT.width());
    assertEquals(8, WorldSpec.DEFAULT.height());
    assertEquals(0L, WorldSpec.DEFAULT.seed());

    Grid grid = new Grid(new int[][] {{0, 1, 2}, {3, 4, 5}});
    ElevationRaster raster = ElevationRaster.of(grid);
    assertEquals(3, raster.width());
    assertEquals(2, raster.height());
    assertEquals(ElevationRaster.rgbOf(0), raster.rgb(0, 0));
    assertEquals(ElevationRaster.rgbOf(5), raster.rgb(2, 1));
    assertEquals(raster, ElevationRaster.of(grid));

    var view = ProductHost.create(WorldSpec.VIEW);
    Grid elevation = (Grid) view.settled().field(WorldFields.ELEVATION);
    assertEquals(512, elevation.width());
    assertEquals(512, elevation.height());
    ElevationRaster viewRaster = ElevationRaster.of(elevation);
    assertEquals(512, viewRaster.width());
    assertEquals(ElevationRaster.rgbOf(0), viewRaster.rgb(0, 0));
  }

  @Test
  @DisplayName("FR-2: absolute integer ramp; clamp 32")
  void absoluteRampFormula() {
    assertEquals(pack(12, 10, 18), ElevationRaster.rgbOf(0));
    assertEquals(pack(255, 196, 96), ElevationRaster.rgbOf(32));
    assertEquals(ElevationRaster.rgbOf(32), ElevationRaster.rgbOf(99));
    assertEquals(ElevationRaster.rgbOf(0), ElevationRaster.rgbOf(-4));
    int e = 8;
    int r = 12 + (243 * e) / 32;
    int g = 10 + (186 * e) / 32;
    int b = 18 + (78 * e) / 32;
    assertEquals(pack(r, g, b), ElevationRaster.rgbOf(e));
  }

  @Test
  @DisplayName("FR-3: controller creates, advances one Step, no Swing in source")
  void controllerAdvancesHeadlessly() throws Exception {
    MapController map = new MapController(new WorldSpec(8, 8, 0L));
    assertEquals(0, map.stepIndex());
    assertEquals("Step 0", map.statusText());
    ElevationRaster before = map.raster();
    assertEquals(8, before.width());
    assertEquals(8, before.height());
    assertEquals(ElevationRaster.rgbOf(0), before.rgb(0, 0));

    map.advance();
    assertEquals(1, map.stepIndex());
    assertNotEquals(before, map.raster());
    Grid elevation = (Grid) map.engine().settled().field(WorldFields.ELEVATION);
    assertEquals(ElevationRaster.of(elevation), map.raster());

    String src =
        Files.readString(
            findRepoRoot()
                .resolve("product/src/main/java/com/aethelgard/product/MapController.java"));
    assertFalse(src.contains("javax.swing"));
    assertFalse(src.contains("java.awt"));
  }

  @Test
  @DisplayName("FR-4: async busy Working…; ignored while busy; compute on executor")
  void asyncBusyOnExecutor() {
    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController map = new MapController(new WorldSpec(8, 8, 0L), queue::add);
    AtomicInteger fires = new AtomicInteger();
    map.onChanged(fires::incrementAndGet);
    int afterWire = fires.get();

    map.advanceAsync();
    assertTrue(map.busy());
    assertEquals(MapController.WORKING_STATUS, map.statusText());
    assertEquals(1, queue.size());
    assertTrue(fires.get() > afterWire);
    int stepWhenBusy = map.stepIndex();

    map.advanceAsync();
    assertEquals(1, queue.size(), "second click ignored");

    queue.removeFirst().run();
    assertFalse(map.busy());
    assertEquals(stepWhenBusy + 1, map.stepIndex());
    assertEquals("Step " + map.stepIndex(), map.statusText());
    assertTrue(queue.isEmpty());
  }

  @Test
  @DisplayName("FR-5: ProductApp/MapFrame exist; ui/cli do not depend on product")
  void swingShellAndOneWayDeps() throws Exception {
    Path root = findRepoRoot();
    assertTrue(
        Files.isRegularFile(
            root.resolve("product/src/main/java/com/aethelgard/product/ProductApp.java")));
    Path frame = root.resolve("product/src/main/java/com/aethelgard/product/MapFrame.java");
    assertTrue(Files.isRegularFile(frame));
    String text = Files.readString(frame);
    assertTrue(text.contains("JFrame"));
    assertTrue(text.contains("Advance"));
    assertTrue(text.contains("advanceAsync"));
    assertFalse(text.contains("controller.advance()"));

    String cliPom = Files.readString(root.resolve("cli/pom.xml"));
    String uiPom = Files.readString(root.resolve("ui/pom.xml"));
    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    assertFalse(cliPom.contains("<artifactId>product</artifactId>"));
    assertFalse(uiPom.contains("<artifactId>product</artifactId>"));
    assertFalse(enginePom.contains("<artifactId>product</artifactId>"));
  }

  @Test
  @DisplayName("FR-2/FR-6: architecture records ramp; flows and G-004 close; Active Goal none")
  void docsRecordViewAndGoalDone() throws Exception {
    Path root = findRepoRoot();
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("ElevationRaster"));
    assertTrue(arch.contains("WorldSpec.VIEW"));
    assertTrue(arch.contains("12") && arch.contains("255"));
    assertTrue(arch.contains("32"));
    assertTrue(arch.toLowerCase().contains("working"));

    String flows = Files.readString(root.resolve("docs/product/flows.md"));
    assertTrue(flows.toLowerCase().contains("advance"));
    assertTrue(flows.toLowerCase().contains("working") || flows.contains("busy"));

    String goal = Files.readString(root.resolve("docs/project/goals/G-004-see-the-world.md"));
    assertTrue(goal.contains("**Status:** `done`"));
    assertFalse(goal.contains("**Status:** `in progress`"));
    assertTrue(goal.contains("- [x] 512×512 view spec; headless raster is deterministic RGB per cell"));
    assertTrue(
        goal.contains(
            "- [x] Product window paints that raster; Advance; loading while compute runs; no `JFrame` in tests"));

    String goalsIndex = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(
        goalsIndex.toLowerCase().contains("active goal:** none")
            || goalsIndex.contains("**Active Goal:** none"));
    assertTrue(goalsIndex.contains("G-004") && goalsIndex.contains("done"));

    String agents = Files.readString(root.resolve("AGENTS.md"));
    String readmeRoot = Files.readString(root.resolve("README.md"));
    String phase = Files.readString(root.resolve("docs/PHASE.md"));
    String nav = Files.readString(root.resolve("docs/navigation.md"));
    String session = Files.readString(root.resolve("docs/project/session.md"));
    String protocol = Files.readString(root.resolve(".cursor/rules/protocol.mdc"));
    String rollup = Files.readString(root.resolve("docs/architecture.md"));
    for (String text : new String[] {agents, readmeRoot, phase, nav, session, protocol, rollup}) {
      assertTrue(
          text.toLowerCase().contains("active goal:** none")
              || text.contains("**Active Goal:** none")
              || text.contains("Active Goal: none")
              || text.contains("**Current Goal:** none"),
          "entry point must say Active Goal none");
    }
  }

  private static int pack(int r, int g, int b) {
    return (r << 16) | (g << 8) | b;
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
