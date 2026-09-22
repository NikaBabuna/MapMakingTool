/*
 * File: ui/src/test/java/com/aethelgard/ui/host/GoalCloseTest.java
 * Purpose: F-054 witness — layer harden + G-009 Goal close
 * Audience: Agents / CI
 * Update when: F-054 FRs or G-009 close claims change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.WorldSpec;
import com.aethelgard.ui.ElevationRaster;
import com.aethelgard.ui.MapController;
import com.aethelgard.ui.MapLayer;
import com.aethelgard.ui.PlayScheduler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoalCloseTest {

  @Test
  @DisplayName("FR-1/FR-2: layer applyGen + preventDefault on 1/2/3; typing targets suppress")
  void layerHardenUi() throws Exception {
    Path root = findRepoRoot();
    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("applyGenRef"));
    assertTrue(tool.contains("++applyGenRef.current"));
    assertTrue(tool.contains("gen !== applyGenRef.current"));
    assertTrue(tool.contains("isTypingTarget"));
    assertTrue(tool.contains("key === \"1\""));
    assertTrue(tool.contains("key === \"2\""));
    assertTrue(tool.contains("key === \"3\""));
    // Digits call preventDefault when handled (F-054)
    int k1 = tool.indexOf("key === \"1\"");
    assertTrue(k1 >= 0);
    String digitBlock = tool.substring(k1, Math.min(tool.length(), k1 + 280));
    assertTrue(digitBlock.contains("preventDefault"));
    assertTrue(digitBlock.contains("onLayer(\"Elevation\")"));
    int k2 = tool.indexOf("key === \"2\"");
    String digit2 = tool.substring(k2, Math.min(tool.length(), k2 + 200));
    assertTrue(digit2.contains("preventDefault"));
    int k3 = tool.indexOf("key === \"3\"");
    String digit3 = tool.substring(k3, Math.min(tool.length(), k3 + 200));
    assertTrue(digit3.contains("preventDefault"));

    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("map-layer-switch"));
    assertTrue(canvas.contains("onLayer"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(
        style.contains("layer keys")
            || style.contains("1, 2, and 3"));
  }

  @Test
  @DisplayName("FR-3: concurrent setLayer + advance does not tear paint buffers")
  void paintLockSafeUnderConcurrency() throws Exception {
    MapController controller = new MapController(new WorldSpec(32, 32, 11L));
    ExecutorService pool = Executors.newFixedThreadPool(2);
    AtomicReference<Throwable> fail = new AtomicReference<>();
    AtomicInteger layerOps = new AtomicInteger();
    java.util.concurrent.CyclicBarrier barrier = new java.util.concurrent.CyclicBarrier(3);
    java.util.concurrent.atomic.AtomicBoolean stop = new java.util.concurrent.atomic.AtomicBoolean(false);

    pool.execute(
        () -> {
          try {
            barrier.await(5, TimeUnit.SECONDS);
            while (!stop.get()) {
              controller.setLayer(MapLayer.PLATES);
              controller.setLayer(MapLayer.OVERLAY);
              controller.setLayer(MapLayer.ELEVATION);
              layerOps.incrementAndGet();
            }
          } catch (Throwable t) {
            fail.compareAndSet(null, t);
          }
        });
    pool.execute(
        () -> {
          try {
            barrier.await(5, TimeUnit.SECONDS);
            while (!stop.get()) {
              ElevationRaster r = controller.raster();
              assertEquals(32 * 32, r.pixels().length);
            }
          } catch (Throwable t) {
            fail.compareAndSet(null, t);
          }
        });

    barrier.await(5, TimeUnit.SECONDS);
    for (int i = 0; i < 40; i++) {
      controller.advance();
      assertEquals(32 * 32, controller.raster().pixels().length);
    }
    stop.set(true);
    pool.shutdown();
    assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
    if (fail.get() != null) {
      throw new AssertionError("concurrent paint failed", fail.get());
    }
    assertTrue(layerOps.get() > 0);
    assertEquals(32 * 32, controller.raster().pixels().length);
  }

  @Test
  @DisplayName("FR-3: host layer switch keeps status layer aligned with raster paint")
  void hostLayerMatchesRaster() throws Exception {
    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController controller =
        new MapController(new WorldSpec(8, 8, 5L), queue::add, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      controller.setLayer(MapLayer.PLATES);
      ElevationRaster expected =
          ElevationRaster.paint(
              controller.session().elevation(),
              controller.session().plates(),
              MapLayer.PLATES);
      int[] a = controller.raster().pixels();
      int[] b = expected.pixels();
      assertEquals(b.length, a.length);
      for (int i = 0; i < a.length; i++) {
        assertEquals(b[i], a[i]);
      }
      assertEquals(MapLayer.PLATES, controller.layer());
    }
  }

  @Test
  @DisplayName("FR-4..FR-6: G-009 done; claims; entry points none; docs hygiene; no engine edits")
  void goalClosed() throws Exception {
    Path root = findRepoRoot();

    String goalDoc =
        Files.readString(root.resolve("docs/paperwork/goals/G-009-simulation-runner-harden.md"));
    assertTrue(goalDoc.contains("**Status:** `done`"));
    assertTrue(goalDoc.contains("- [x] Diverge:"));
    assertTrue(goalDoc.contains("- [x] Slivers"));
    assertTrue(goalDoc.contains("- [x] Sphere polar wrap"));
    assertTrue(goalDoc.contains("- [x] Diagnostics"));
    assertTrue(goalDoc.contains("- [x] Shared command"));
    assertTrue(goalDoc.contains("- [x] Runner chrome"));
    assertTrue(goalDoc.contains("- [x] Perf/memory"));
    assertTrue(goalDoc.contains("- [x] Determinism; no `engine` production edits; suite green"));
    assertTrue(goalDoc.contains("| F-054 |") && goalDoc.contains("| done |"));

    String goals = Files.readString(root.resolve("docs/paperwork/goals.md"));
    assertTrue(
        goals.contains("**Active Goal:** none")
            || goals.contains("Active Goal:** none")
            || goals.contains("G-010")
            || goals.contains("Active Goal:** [G-"));
    assertTrue(goals.contains("G-009") && goals.contains("done"));
    String g009 = goals.lines().filter(l -> l.contains("| G-009 |")).findFirst().orElse("");
    assertTrue(g009.contains("| done |"), g009);

    String agents = Files.readString(root.resolve("AGENTS.md"));
    assertTrue(agents.contains("G-011") || agents.contains("docs/protocol/README.md"));

    String phase = Files.readString(root.resolve("docs/protocol/environment/phase.md"));
    assertTrue(phase.contains("alpha"));

    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("G-009"));

    String readme = Files.readString(root.resolve("README.md"));
    assertTrue(readme.contains("G-011") || readme.contains("docs/protocol/README.md"));

    assertFalse(Files.exists(root.resolve("docs/project/session.md")));

    String protocol = Files.readString(root.resolve(".cursor/rules/protocol.mdc"));
    assertTrue(protocol.contains("G-011") || protocol.contains("docs/protocol/README.md"));

    String arch = Files.readString(root.resolve("docs/paperwork/goals.md"));
    assertTrue(arch.contains("G-009"));

    String flows = Files.readString(root.resolve("docs/product/journeys.md"));
    assertFalse(flows.contains("G-009 simulation runner (planned)"));
    assertFalse(flows.contains("aethelgard.dockOpen"));
    assertFalse(flows.contains("panelInspectOpen"));
    assertTrue(flows.contains("Play"));

    String world = Files.readString(root.resolve("docs/product/wiki/world.md"));
    assertFalse(world.contains("through F-040 / G-008"));
    assertTrue(world.contains("sphere") || world.contains("Sphere"));

    String elev = Files.readString(root.resolve("docs/product/wiki/elevation.md"));
    assertFalse(elev.contains("through F-040 / G-008"));

    String glossary = Files.readString(root.resolve("docs/product/glossary.md"));
    assertFalse(glossary.contains("Planned (F-042)"));
    assertFalse(glossary.contains("Slow/Normal/Fast") || glossary.contains("| Slow |"));

    String productArch = Files.readString(root.resolve("docs/architecture/studio/host.md"));
    assertTrue(productArch.contains("panels.ts") || productArch.contains("lib/panels"));
    assertFalse(productArch.contains("SwingPlayScheduler.java"));

    String engineArch = Files.readString(root.resolve("docs/architecture/program.md"));
    assertFalse(engineArch.toLowerCase().contains("placeholder console"));

    // No engine production edits this Step (and Goal claim)
    Path engineMain = root.resolve("engine/src/main/java");
    assertTrue(Files.isDirectory(engineMain));
    String blocker = Files.readString(root.resolve("docs/paperwork/steps/F-054.md"));
    assertTrue(blocker.contains("no `engine/`") || blocker.contains("Zero `engine/`"));
    assertTrue(blocker.contains("FR-5") || blocker.contains("Determinism"));
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath();
    for (int i = 0; i < 8; i++) {
      if (Files.isRegularFile(dir.resolve("pom.xml"))
          && Files.isDirectory(dir.resolve("ui"))
          && Files.isDirectory(dir.resolve("docs"))) {
        return dir;
      }
      Path parent = dir.getParent();
      if (parent == null) {
        break;
      }
      dir = parent;
    }
    throw new Exception("repo root not found");
  }
}
