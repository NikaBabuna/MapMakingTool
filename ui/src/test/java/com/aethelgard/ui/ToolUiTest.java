/*
 * File: ui/src/test/java/com/aethelgard/ui/ToolUiTest.java
 * Purpose: F-022 tool UI — ocean, hillshade, layers, play, seed, inspect, legend
 * Audience: Agents / CI
 * Update when: F-022 FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.Grid;
import com.aethelgard.product.WorldSpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ToolUiTest {

  @Test
  @DisplayName("FR-1: negatives are ocean; land ramp unchanged; same grid → same RGB")
  void oceanAndLandRamp() {
    assertEquals(pack(18, 56, 92), ElevationRaster.OCEAN_RGB);
    assertEquals(ElevationRaster.OCEAN_RGB, ElevationRaster.rgbOf(-1));
    assertEquals(ElevationRaster.OCEAN_RGB, ElevationRaster.rgbOf(-12));
    assertEquals(pack(12, 10, 18), ElevationRaster.landRamp(0));
    assertEquals(pack(255, 196, 96), ElevationRaster.landRamp(32));
    assertEquals(ElevationRaster.landRamp(32), ElevationRaster.rgbOf(32));

    Grid grid =
        new Grid(
            new int[][] {
              {0, -3},
              {8, 32}
            });
    ElevationRaster a = ElevationRaster.of(grid);
    ElevationRaster b = ElevationRaster.of(grid);
    assertEquals(a, b);
    assertEquals(ElevationRaster.elevationCell(grid, 0, 1), a.rgb(0, 1));
    assertEquals(ElevationRaster.OCEAN_RGB, ElevationRaster.elevationCell(grid, 1, 0));
  }

  @Test
  @DisplayName("FR-2: hillshade on land; flat matches ramp; ocean not shaded")
  void hillshadeFormula() {
    Grid flat = new Grid(new int[][] {{5, 5}, {5, 5}});
    assertEquals(ElevationRaster.landRamp(5), ElevationRaster.elevationCell(flat, 0, 0));
    assertEquals(ElevationRaster.HILLSHADE_FLAT, ElevationRaster.hillshadeLit(0, 0));

    Grid slope =
        new Grid(
            new int[][] {
              {0, 0, 0},
              {0, 4, 0},
              {0, 0, 0}
            });
    // (1,1): west=0, north=0, dw=4, dn=4 → lit = 12+8+8=28 → clamp 18
    int lit = ElevationRaster.hillshadeLit(4, 4);
    assertEquals(18, lit);
    int expected = ElevationRaster.applyHillshade(ElevationRaster.landRamp(4), 18);
    assertEquals(expected, ElevationRaster.elevationCell(slope, 1, 1));
    assertNotEquals(ElevationRaster.landRamp(4), expected);

    Grid ocean =
        new Grid(
            new int[][] {
              {10, 10},
              {10, -2}
            });
    assertEquals(ElevationRaster.OCEAN_RGB, ElevationRaster.elevationCell(ocean, 1, 1));
  }

  @Test
  @DisplayName("FR-3: layers paint differently; switch does not advance")
  void layersDoNotAdvance() {
    MapController map = new MapController(new WorldSpec(8, 8, 0L));
    map.advance();
    int step = map.stepIndex();
    ElevationRaster elevation = map.raster();
    assertEquals(MapLayer.ELEVATION, map.layer());

    map.setLayer(MapLayer.PLATES);
    assertEquals(step, map.stepIndex());
    assertEquals(MapLayer.PLATES, map.layer());
    assertNotEquals(elevation, map.raster());
    assertEquals(
        ElevationRaster.paint(map.session().elevation(), map.session().plates(), MapLayer.PLATES),
        map.raster());

    map.setLayer(MapLayer.OVERLAY);
    assertEquals(step, map.stepIndex());
    Grid plates = map.session().plates();
    Grid elev = map.session().elevation();
    assertEquals(ElevationRaster.overlayCell(elev, plates, 0, 0), map.raster().rgb(0, 0));

    map.setLayer(MapLayer.ELEVATION);
    assertEquals(elevation, map.raster());
  }

  @Test
  @DisplayName("FR-3: overlay darkens east/south plate contacts")
  void overlayDarkensSutures() {
    Grid elev = Grid.zeros(2, 2);
    Grid plates =
        new Grid(
            new int[][] {
              {0, 1},
              {0, 0}
            });
    int interior = ElevationRaster.elevationCell(elev, 1, 1);
    // (0,0) east is 1 → suture
    assertEquals(
        ElevationRaster.darken(ElevationRaster.elevationCell(elev, 0, 0)),
        ElevationRaster.overlayCell(elev, plates, 0, 0));
    // (1,1) east wraps to (0,1)=0, south wraps to (1,0)=1 → south differs
    assertNotEquals(interior, ElevationRaster.overlayCell(elev, plates, 1, 1));
  }

  @Test
  @DisplayName("FR-4: play ticks on injected scheduler; pause; speeds; busy ignored")
  void playPauseSpeed() {
    RecordingPlayScheduler scheduler = new RecordingPlayScheduler();
    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController map = new MapController(new WorldSpec(8, 8, 0L), queue::add, scheduler);

    assertFalse(map.playing());
    assertEquals(MapSpeed.NORMAL, map.speed());
    assertEquals(250, MapSpeed.NORMAL.periodMillis());
    assertEquals(1000, MapSpeed.SLOW.periodMillis());
    assertEquals(100, MapSpeed.FAST.periodMillis());

    map.playTick();
    assertEquals(0, map.stepIndex(), "paused tick is a no-op");

    map.play();
    assertTrue(map.playing());
    assertEquals(1, scheduler.starts);
    assertEquals(250, scheduler.lastPeriod);

    map.setSpeed(MapSpeed.FAST);
    assertEquals(MapSpeed.FAST, map.speed());
    assertEquals(100, scheduler.lastPeriod);
    assertEquals(2, scheduler.starts);

    map.playTick();
    assertTrue(map.busy());
    assertEquals(MapController.WORKING_STATUS, map.statusText());
    map.playTick();
    assertEquals(1, queue.size(), "busy play tick ignored");
    queue.removeFirst().run();
    assertFalse(map.busy());
    assertEquals(1, map.stepIndex());
    assertEquals("Step 1", map.statusText());

    map.pause();
    assertFalse(map.playing());
    assertEquals(2, scheduler.stops);
    int step = map.stepIndex();
    map.playTick();
    assertEquals(step, map.stepIndex());
  }

  @Test
  @DisplayName("FR-5: VIEW launch; newWorld reseeds; ignored while busy; DEFAULT unchanged")
  void newWorldAndView() {
    assertEquals(1920, WorldSpec.VIEW.width());
    assertEquals(1080, WorldSpec.VIEW.height());
    assertEquals(0L, WorldSpec.VIEW.seed());
    assertEquals(8, WorldSpec.DEFAULT.width());
    assertEquals(8, WorldSpec.DEFAULT.height());
    assertEquals(0L, WorldSpec.DEFAULT.seed());

    MapController view = MapController.view(Runnable::run);
    assertEquals(WorldSpec.VIEW, view.spec());
    assertEquals(0, view.stepIndex());
    assertEquals(1920, view.raster().width());
    assertEquals(1080, view.raster().height());

    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController map = new MapController(new WorldSpec(8, 8, 0L), queue::add);
    map.advance();
    assertEquals(1, map.stepIndex());

    map.advanceAsync();
    map.newWorld(7L);
    assertEquals(0L, map.spec().seed(), "ignored while busy");
    assertEquals(1, map.stepIndex());
    queue.removeFirst().run();

    map.play();
    map.newWorld(7L);
    assertFalse(map.playing());
    assertEquals(7L, map.spec().seed());
    assertEquals(8, map.spec().width());
    assertEquals(0, map.stepIndex());
    assertEquals(0, map.session().elevation().get(0, 0));
    assertEquals(ElevationRaster.rgbOf(0), map.raster().rgb(0, 0));
    assertNull(map.inspected());
    assertEquals(7L, map.session().spec().seed());
  }

  @Test
  @DisplayName("FR-6: inspect returns cell fields from captured grids")
  void inspectCell() {
    MapController map = new MapController(new WorldSpec(8, 8, 0L));
    CellInspect cell = map.inspect(2, 3);
    assertEquals(2, cell.x());
    assertEquals(3, cell.y());
    assertEquals(0, cell.elevation());
    int plate = map.session().plates().get(2, 3);
    assertEquals(plate, cell.plateId());
    assertEquals(map.session().plateVelocities().vx(plate), cell.vx());
    assertEquals(map.session().plateVelocities().vy(plate), cell.vy());
    assertEquals(cell, map.inspected());

    map.advance();
    CellInspect later = map.inspected();
    assertNotNull(later);
    assertEquals(2, later.x());
    assertEquals(3, later.y());
    assertEquals(map.session().elevation().get(2, 3), later.elevation());
  }

  @Test
  @DisplayName("FR-7: legend tracks layer; Next/Tauri chrome; no Swing in ui main; house holds")
  void legendShellAndHouse() throws Exception {
    MapController map = new MapController(new WorldSpec(8, 8, 0L));
    List<LegendEntry> elevation = map.legend();
    assertEquals("Ocean (e < 0)", elevation.get(0).label());
    assertEquals(ElevationRaster.OCEAN_RGB, elevation.get(0).rgb());
    assertEquals("Low (0)", elevation.get(1).label());
    assertEquals("High (32)", elevation.get(2).label());

    map.setLayer(MapLayer.PLATES);
    List<LegendEntry> plates = map.legend();
    assertFalse(plates.isEmpty());
    assertTrue(plates.get(0).label().startsWith("Plate "));
    assertEquals(ElevationRaster.plateRgb(idOf(plates.get(0).label())), plates.get(0).rgb());

    map.setLayer(MapLayer.OVERLAY);
    List<LegendEntry> overlay = map.legend();
    assertEquals("Suture", overlay.get(overlay.size() - 1).label());

    Path root = findRepoRoot();
    assertFalse(Files.exists(root.resolve("ui/src/main/java/com/aethelgard/ui/MapFrame.java")));
    assertFalse(Files.exists(root.resolve("ui/src/main/java/com/aethelgard/ui/ProductApp.java")));
    assertFalse(
        Files.exists(root.resolve("ui/src/main/java/com/aethelgard/ui/SwingPlayScheduler.java")));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("Advance"));
    assertTrue(tool.contains("Play"));
    assertTrue(tool.contains("Pause") || tool.contains("playing"));
    assertTrue(tool.contains("New world"));
    assertTrue(tool.contains("Seed") || tool.contains("seed"));
    assertTrue(tool.contains("Inspect") || tool.contains("inspect"));
    assertTrue(tool.contains("Legend") || tool.contains("legend"));
    assertTrue(tool.contains("postAdvance"));

    String layers = Files.readString(root.resolve("ui/src/main/java/com/aethelgard/ui/MapLayer.java"));
    assertTrue(layers.contains("Elevation"));
    assertTrue(layers.contains("Plates"));
    assertTrue(layers.contains("Overlay"));

    String controller =
        Files.readString(root.resolve("ui/src/main/java/com/aethelgard/ui/MapController.java"));
    assertFalse(controller.contains("javax.swing"));
    assertFalse(controller.contains("java.awt"));

    try (var walk = Files.walk(root.resolve("ui/src/main/java"))) {
      assertTrue(
          walk.filter(p -> p.toString().endsWith(".java"))
              .map(
                  p -> {
                    try {
                      return Files.readString(p);
                    } catch (Exception e) {
                      throw new RuntimeException(e);
                    }
                  })
              .noneMatch(s -> s.contains("javax.swing") || s.contains("JFrame")));
    }

    String productHost =
        Files.readString(
            root.resolve("product/src/main/java/com/aethelgard/product/ProductHost.java"));
    assertFalse(productHost.contains("javax.swing"));

    String engineLoop =
        Files.readString(
            root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertNotNull(engineLoop);
  }

  @Test
  @DisplayName("FR-7: plateRgb is deterministic")
  void plateRgbStable() {
    assertEquals(ElevationRaster.plateRgb(3), ElevationRaster.plateRgb(3));
    assertNotEquals(ElevationRaster.plateRgb(0), ElevationRaster.plateRgb(1));
  }

  private static int idOf(String plateLabel) {
    return Integer.parseInt(plateLabel.substring("Plate ".length()));
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

  private static final class RecordingPlayScheduler implements PlayScheduler {
    int starts;
    int stops;
    int lastPeriod = -1;

    @Override
    public void start(int periodMillis, Runnable tick) {
      starts++;
      lastPeriod = periodMillis;
    }

    @Override
    public void stop() {
      stops++;
    }
  }
}
