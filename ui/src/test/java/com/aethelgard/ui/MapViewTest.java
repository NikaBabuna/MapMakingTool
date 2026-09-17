/*
 * File: ui/src/test/java/com/aethelgard/ui/MapViewTest.java
 * Purpose: F-018 raster/busy + F-019 house — map view in ui
 * Audience: Agents / CI
 * Update when: Map view FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.Grid;
import com.aethelgard.product.WorldSpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MapViewTest {

  @Test
  @DisplayName("FR-1: ui depends on product; product does not depend on ui")
  void moduleDependsOnProduct() throws Exception {
    Path root = findRepoRoot();
    String uiPom = Files.readString(root.resolve("ui/pom.xml"));
    assertTrue(uiPom.contains("<artifactId>product</artifactId>"));
    assertTrue(uiPom.contains("<artifactId>cli</artifactId>"));
    String productPom = Files.readString(root.resolve("product/pom.xml"));
    assertFalse(productPom.contains("<artifactId>ui</artifactId>"));
    String cliPom = Files.readString(root.resolve("cli/pom.xml"));
    assertFalse(cliPom.contains("<artifactId>ui</artifactId>"));
    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    assertFalse(enginePom.contains("<artifactId>product</artifactId>"));
    assertFalse(enginePom.contains("<artifactId>ui</artifactId>"));
  }

  @Test
  @DisplayName("F-018 FR-1: VIEW raster geometry; same grid → same RGB")
  void viewSpecAndRasterGeometry() {
    Grid grid = new Grid(new int[][] {{0, 1, 2}, {3, 4, 5}});
    ElevationRaster raster = ElevationRaster.of(grid);
    assertEquals(3, raster.width());
    assertEquals(2, raster.height());
    assertEquals(ElevationRaster.elevationCell(grid, 0, 0), raster.rgb(0, 0));
    assertEquals(ElevationRaster.elevationCell(grid, 2, 1), raster.rgb(2, 1));
    assertEquals(raster, ElevationRaster.of(grid));

    MapController view = MapController.view(Runnable::run);
    assertEquals(512, view.spec().width());
    assertEquals(512, view.raster().width());
    assertEquals(ElevationRaster.rgbOf(0), view.raster().rgb(0, 0));
  }

  @Test
  @DisplayName("F-018 FR-2 / F-022: absolute integer ramp; ocean for negatives")
  void absoluteRampFormula() {
    assertEquals(pack(12, 10, 18), ElevationRaster.rgbOf(0));
    assertEquals(pack(255, 196, 96), ElevationRaster.rgbOf(32));
    assertEquals(ElevationRaster.rgbOf(32), ElevationRaster.rgbOf(99));
    assertEquals(ElevationRaster.OCEAN_RGB, ElevationRaster.rgbOf(-4));
    assertEquals(pack(18, 56, 92), ElevationRaster.rgbOf(-1));
    assertNotEquals(ElevationRaster.rgbOf(0), ElevationRaster.rgbOf(-4));
    int e = 8;
    int r = 12 + (243 * e) / 32;
    int g = 10 + (186 * e) / 32;
    int b = 18 + (78 * e) / 32;
    assertEquals(pack(r, g, b), ElevationRaster.rgbOf(e));
  }

  @Test
  @DisplayName("F-018 FR-3 / F-019: controller advances via session; no Swing in controller")
  void controllerAdvancesHeadlessly() throws Exception {
    MapController map = new MapController(new WorldSpec(8, 8, 0L));
    assertEquals(0, map.stepIndex());
    assertEquals("Step 0", map.statusText());
    ElevationRaster before = map.raster();
    assertEquals(8, before.width());
    assertEquals(ElevationRaster.rgbOf(0), before.rgb(0, 0));

    map.advance();
    assertEquals(1, map.stepIndex());
    assertNotEquals(before, map.raster());
    assertEquals(ElevationRaster.of(map.session().elevation()), map.raster());

    String src =
        Files.readString(
            findRepoRoot().resolve("ui/src/main/java/com/aethelgard/ui/MapController.java"));
    assertFalse(src.contains("javax.swing"));
    assertFalse(src.contains("java.awt"));
  }

  @Test
  @DisplayName("F-018 FR-4: async busy Working…; ignored while busy")
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
  @DisplayName("FR-5: MapFrame/ProductApp live in ui; Advance uses advanceAsync")
  void swingShellInUi() throws Exception {
    Path root = findRepoRoot();
    assertTrue(
        Files.isRegularFile(root.resolve("ui/src/main/java/com/aethelgard/ui/ProductApp.java")));
    Path frame = root.resolve("ui/src/main/java/com/aethelgard/ui/MapFrame.java");
    assertTrue(Files.isRegularFile(frame));
    String text = Files.readString(frame);
    assertTrue(text.contains("JFrame"));
    assertTrue(text.contains("Advance"));
    assertTrue(text.contains("advanceAsync"));
    assertFalse(text.contains("controller.advance()"));
    String uiPom = Files.readString(root.resolve("ui/pom.xml"));
    assertTrue(uiPom.contains("com.aethelgard.ui.ProductApp"));
  }

  @Test
  @DisplayName("FR-5/FR-6: launch scripts and architecture point at ui; G-001 UI claim still done")
  void docsLaunchFromUi() throws Exception {
    Path root = findRepoRoot();
    String runProduct = Files.readString(root.resolve("run-product.cmd"));
    assertTrue(runProduct.contains("-pl ui"));
    assertFalse(runProduct.contains("-pl product exec"));
    String runUi = Files.readString(root.resolve("run-ui.cmd"));
    assertTrue(runUi.toLowerCase().contains("run-product") || runUi.contains("-pl ui"));

    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("ElevationRaster"));
    assertTrue(arch.contains("com.aethelgard.ui"));
    assertTrue(arch.toLowerCase().contains("working"));

    String goal =
        Files.readString(root.resolve("docs/project/goals/G-001-engine-skeleton.md"));
    assertTrue(goal.contains("[x] Basic UI can advance/view Steps"));
    assertTrue(goal.contains("**Status:** `done`") || goal.contains("**Status:** done"));
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
