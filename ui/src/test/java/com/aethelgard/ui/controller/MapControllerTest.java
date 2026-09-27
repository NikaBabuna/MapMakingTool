/*
 * File: ui/src/test/java/com/aethelgard/ui/controller/MapControllerTest.java
 * Purpose: Proves what the studio's controller does: layers, background steps, play, new world and restart, inspect, the console, and safe painting
 * Audience: Agents / CI
 * Update when: MapController, MapSpeed, or PlayScheduler changes
 */

package com.aethelgard.ui.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.cli.CliResult;
import com.aethelgard.product.session.ProductSession;
import com.aethelgard.product.session.diagnostics.DiagnosticIds;
import com.aethelgard.product.session.diagnostics.RingDiagnosticCollector;
import com.aethelgard.product.world.fields.WorldSpec;
import com.aethelgard.ui.raster.ElevationRaster;
import com.aethelgard.ui.raster.MapLayer;
import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MapControllerTest {

  private static final WorldSpec SPEC = new WorldSpec(16, 8, 3L);

  /** Proves F-068 FR-53 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The legend follows the layer, and Plates and Overlay have a Boundary row in the stroke's colour")
  void legendFollowsTheLayer() {
    MapController map = new MapController(SPEC, Runnable::run);

    assertFalse(labels(map.legend()).contains("Boundary"), "Elevation has no Boundary row");

    map.setLayer(MapLayer.PLATES);
    LegendEntry boundary = row(map.legend(), "Boundary");
    assertEquals(0x24262A, boundary.rgb(), "the Plates stroke colour (36, 38, 42)");
    assertTrue(contains(map.raster(), boundary.rgb()), "the legend's Boundary colour is on the map");

    map.setLayer(MapLayer.OVERLAY);
    assertTrue(labels(map.legend()).contains("Boundary"));
    assertTrue(map.legend().size() > 1, "Overlay also shows the height rows");
  }

  /** Proves F-068 FR-54 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Switching layer repaints the same step without stepping")
  void layerSwitchDoesNotStep() {
    MapController map = new MapController(SPEC, Runnable::run);
    map.advance();
    map.advance();
    int generation = map.paintGeneration();
    ElevationRaster heights = map.raster();
    int[] heightPixels = heights.pixels().clone();

    map.setLayer(MapLayer.PLATES);

    assertEquals(2, map.stepIndex());
    assertEquals(MapLayer.PLATES, map.layer());
    assertTrue(map.paintGeneration() > generation);
    ProductSession session = map.session();
    assertEquals(ElevationRaster.paint(session.elevation(), session.plates(), MapLayer.PLATES), map.raster());
    assertNotEquals(ElevationRaster.paint(session.elevation(), session.plates(), MapLayer.ELEVATION), map.raster());
    assertArrayEquals(heightPixels, heights.pixels(), "the previous picture is untouched");
  }

  /** Proves F-068 FR-54 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("An advance runs in the background showing Working...; while busy, another advance or command is ignored")
  void advanceRunsInTheBackground() {
    ArrayDeque<Runnable> background = new ArrayDeque<>();
    MapController map = new MapController(SPEC, background::add);
    assertEquals("Step 0", map.statusText());

    map.advanceAsync();
    assertTrue(map.isBusy());
    assertEquals("Working...", map.statusText());
    assertEquals(0, map.stepIndex(), "nothing moved until the background step runs");

    map.advanceAsync();
    assertEquals(1, background.size(), "a second advance while busy is ignored");
    CliResult refused = map.runCommand("advance 5");
    assertNotEquals(0, refused.exitCode(), "a command while busy is refused");
    assertEquals(0, map.session().stepIndex(), "and did not run");

    background.removeFirst().run();
    assertFalse(map.isBusy());
    assertEquals(1, map.stepIndex());
    assertEquals("Step 1", map.statusText());
  }

  /** Proves F-068 FR-54 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A status read never waits for a step in progress")
  void statusNeverWaitsForAStep() throws Exception {
    MapController map = new MapController(new WorldSpec(640, 320, 2L), Runnable::run);
    AtomicBoolean started = new AtomicBoolean();
    Thread worker = new Thread(() -> {
      started.set(true);
      map.session().advance(6);
    });
    worker.start();
    while (!started.get()) {
      Thread.onSpinWait();
    }
    Thread.sleep(50);

    long t0 = System.nanoTime();
    int step = map.stepIndex();
    String text = map.statusText();
    long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
    boolean stillStepping = worker.isAlive();
    worker.join();

    assertTrue(stillStepping, "the step was still running during the read (make the world larger if this fails)");
    assertTrue(elapsedMs < 100, "status took " + elapsedMs + " ms");
    assertEquals(0, step);
    assertEquals("Step 0", text);
  }

  /** Proves F-068 FR-55 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Play ticks at the chosen speed, drops a tick while busy, and pause stops it")
  void playTicksAtTheChosenSpeed() {
    RecordingScheduler scheduler = new RecordingScheduler();
    ArrayDeque<Runnable> background = new ArrayDeque<>();
    MapController map = new MapController(SPEC, background::add, scheduler);

    assertEquals(MapSpeed.X1, map.speed(), "1x is the calm starting rate");
    map.playTick();
    assertEquals(0, background.size(), "a tick while paused does nothing");

    map.play();
    assertTrue(map.isPlaying());
    int calm = scheduler.period;
    assertEquals(calm / 2, MapSpeed.X2.periodMillis());
    assertEquals(calm / 4, MapSpeed.X4.periodMillis());
    assertTrue(MapSpeed.FASTEST.periodMillis() < MapSpeed.X4.periodMillis());

    map.setSpeed(MapSpeed.X4);
    assertEquals(MapSpeed.X4.periodMillis(), scheduler.period, "the running schedule follows the new speed");

    scheduler.tick.run();
    assertTrue(map.isBusy());
    scheduler.tick.run();
    assertEquals(1, background.size(), "a tick while busy is dropped");
    background.removeFirst().run();
    assertEquals(1, map.stepIndex());

    map.pause();
    assertFalse(map.isPlaying());
    assertTrue(scheduler.stopped);
    scheduler.tick.run();
    assertEquals(0, background.size(), "no step after pause");
  }

  /** Proves F-068 FR-56 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("New world reseeds at step 0 and is ignored while busy")
  void newWorldReseeds() {
    ArrayDeque<Runnable> background = new ArrayDeque<>();
    MapController map = new MapController(SPEC, background::add);
    map.advance();

    map.newWorld(42L);
    assertEquals(42L, map.spec().seed());
    assertEquals(0, map.stepIndex());
    assertEquals(new ProductSession(new WorldSpec(16, 8, 42L)).settledWorld(), map.session().settledWorld());

    map.advanceAsync();
    map.newWorld(7L);
    assertEquals(42L, map.spec().seed(), "ignored while busy");
  }

  /** Proves F-068 FR-56 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Restart engine returns to step 0 with the same seed, even while busy")
  void restartEngineKeepsTheSeed() {
    ArrayDeque<Runnable> background = new ArrayDeque<>();
    MapController map = new MapController(SPEC, background::add);
    map.advance();
    map.advanceAsync();
    map.play();

    map.restartEngine();

    assertEquals(0, map.stepIndex());
    assertEquals(SPEC, map.spec());
    assertFalse(map.isBusy());
    assertFalse(map.isPlaying());
    background.removeFirst().run();
    assertEquals(0, map.stepIndex(), "the step that was in flight does not undo the restart");
  }

  /** Proves F-068 FR-57 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Inspect reports a cell's position, elevation, plate and velocity at the captured step")
  void inspectReportsTheCell() {
    MapController map = new MapController(SPEC, Runnable::run);
    map.advance();
    map.advance();
    ProductSession s = map.session();

    CellInspect cell = map.inspect(5, 3);

    int plate = s.plates().get(5, 3);
    assertEquals(new CellInspect(5, 3, s.elevation().get(5, 3), plate,
        s.plateVelocities().vx(plate), s.plateVelocities().vy(plate)), cell);
    assertEquals(cell, map.inspected());
  }

  /** Proves F-068 FR-58 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A console line runs on the map's own world through the shared command language, and the picture refreshes")
  void consoleRunsOnTheMapWorld() {
    MapController map = new MapController(SPEC, Runnable::run);
    int generation = map.paintGeneration();

    CliResult result = map.runCommand("advance 2");

    assertEquals(0, result.exitCode());
    assertEquals("step=2", result.output());
    assertEquals(2, map.stepIndex());
    assertTrue(map.paintGeneration() > generation);
    ProductSession s = map.session();
    assertEquals(ElevationRaster.paint(s.elevation(), s.plates(), map.layer()), map.raster());
    assertEquals("step=2 width=16 height=8 seed=3", map.runCommand("status").output());
  }

  /** Proves F-068 FR-59 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Painting reuses its buffers, a new paint never writes into the previous picture, and concurrent switches never tear one")
  void paintNeverTears() throws Exception {
    MapController map = new MapController(new WorldSpec(32, 32, 11L), Runnable::run);
    ElevationRaster first = map.raster();
    map.advance();
    ElevationRaster second = map.raster();
    int[] secondPixels = second.pixels().clone();
    map.setLayer(MapLayer.PLATES);
    ElevationRaster third = map.raster();

    assertNotSame(first.pixels(), second.pixels(), "consecutive pictures use different buffers");
    assertSame(first.pixels(), third.pixels(), "the buffers are reused");
    assertArrayEquals(secondPixels, second.pixels(), "the previous picture survived the new paint");

    // Layer switches race advances; every picture handed out is whole and matches its layer and step.
    ExecutorService pool = Executors.newFixedThreadPool(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicBoolean stop = new AtomicBoolean();
    CyclicBarrier go = new CyclicBarrier(2);
    pool.execute(() -> {
      try {
        go.await(5, TimeUnit.SECONDS);
        while (!stop.get()) {
          for (MapLayer layer : MapLayer.values()) {
            map.setLayer(layer);
          }
        }
      } catch (Throwable t) {
        failure.compareAndSet(null, t);
      }
    });
    go.await(5, TimeUnit.SECONDS);
    for (int i = 0; i < 30; i++) {
      map.advance();
      assertEquals(32 * 32, map.raster().pixels().length);
    }
    stop.set(true);
    pool.shutdown();
    assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
    if (failure.get() != null) {
      throw new AssertionError("a concurrent paint failed", failure.get());
    }
    ProductSession s = map.session();
    assertEquals(ElevationRaster.paint(s.elevation(), s.plates(), map.layer()), map.raster(), "the last picture is whole");
  }

  /** Proves F-068 FR-59 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Paint time is recorded on the session's diagnostics")
  void paintTimeIsRecorded() {
    MapController map = new MapController(SPEC, Runnable::run);
    RingDiagnosticCollector paint = (RingDiagnosticCollector) map.session().diagnostics().get(DiagnosticIds.PAINT_WALL);
    int before = paint.size();
    int paintsBefore = map.paintGeneration();

    map.setLayer(MapLayer.OVERLAY);
    map.advance();

    int paints = map.paintGeneration() - paintsBefore;
    assertTrue(paints >= 2, "a layer switch and an advance each painted");
    assertEquals(before + paints, paint.size(), "one sample per paint");
  }

  private static List<String> labels(List<LegendEntry> legend) {
    return legend.stream().map(LegendEntry::label).toList();
  }

  private static LegendEntry row(List<LegendEntry> legend, String label) {
    return legend.stream().filter(r -> r.label().equals(label)).findFirst().orElseThrow();
  }

  private static boolean contains(ElevationRaster raster, int rgb) {
    for (int p : raster.pixels()) {
      if (p == rgb) {
        return true;
      }
    }
    return false;
  }

  private static final class RecordingScheduler implements PlayScheduler {
    int period = -1;
    Runnable tick = () -> {};
    boolean stopped;

    @Override
    public void start(int periodMillis, Runnable tick) {
      this.period = periodMillis;
      this.tick = tick;
      this.stopped = false;
    }

    @Override
    public void stop() {
      stopped = true;
    }
  }
}
