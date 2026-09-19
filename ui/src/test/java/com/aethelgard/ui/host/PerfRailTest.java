/*
 * File: ui/src/test/java/com/aethelgard/ui/host/PerfRailTest.java
 * Purpose: F-052 witness — perf rail, diag status, layer HUD, paint, no overlay
 * Audience: Agents / CI
 * Update when: F-052 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.DiagnosticIds;
import com.aethelgard.product.DiagnosticsHub;
import com.aethelgard.product.WorldSpec;
import com.aethelgard.ui.ElevationRaster;
import com.aethelgard.ui.MapController;
import com.aethelgard.ui.MapLayer;
import com.aethelgard.ui.PlayScheduler;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayDeque;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PerfRailTest {

  private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

  @Test
  @DisplayName("FR-1/FR-3: left Perf rail + always-on terminal panel")
  void perfRailAndTerminalPanel() throws Exception {
    Path root = findRepoRoot();
    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    // F-053 moved panel identity + persistence into the registry (lib/panels.ts)
    String panels = Files.readString(root.resolve("ui/web/src/lib/panels.ts"));
    assertTrue(panels.contains("id: \"perf\""));
    assertTrue(panels.contains("aethelgard.panel."));
    assertTrue(tool.contains("PERF_ROWS"));
    assertTrue(tool.contains("panelsFor"));
    assertFalse(tool.contains("setTerminalOpen"));
    String terminal = Files.readString(root.resolve("ui/web/src/components/Terminal.tsx"));
    assertTrue(terminal.contains("terminal-panel"));
    assertFalse(terminal.contains("hidden={!open}"));
    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("--perf-w"));
    assertTrue(css.contains("terminal-panel"));
  }

  @Test
  @DisplayName("FR-2: /api/status includes diag last/mean/n")
  void statusIncludesDiag() throws Exception {
    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController controller =
        new MapController(new WorldSpec(8, 8, 7L), queue::add, PlayScheduler.idle());
    controller.session().diagnostics().record(DiagnosticIds.ADVANCE_WALL, 100);
    controller.session().diagnostics().record(DiagnosticIds.ADVANCE_WALL, 300);
    try (MapHost host = MapHost.start(controller, 0)) {
      String status = get(host, "/api/status");
      assertTrue(status.contains("\"diag\":{"));
      assertTrue(status.contains("\"advance.wall\":{"));
      assertTrue(status.contains("\"mean\":200"));
      assertTrue(status.contains("\"n\":2"));
      assertTrue(status.contains("\"paint.wall\""));
      assertTrue(status.contains("\"phase.orogeny\""));
    }
  }

  @Test
  @DisplayName("FR-4: layer HUD top-left + pointer isolation; host layer switches")
  void layerHudAndHost() throws Exception {
    Path root = findRepoRoot();
    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("map-layer-switch"));
    assertTrue(canvas.contains("closest(\".map-layer-switch\")"));
    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    int switchIdx = css.indexOf(".map-layer-switch");
    assertTrue(switchIdx >= 0);
    String block = css.substring(switchIdx, switchIdx + 280);
    assertTrue(block.contains("top: 0.65rem"));
    assertFalse(block.contains("bottom: 0.65rem"));

    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController controller =
        new MapController(new WorldSpec(8, 8, 3L), queue::add, PlayScheduler.idle());
    assertEquals(MapLayer.ELEVATION, controller.layer());
    try (MapHost host = MapHost.start(controller, 0)) {
      post(host, "/api/layer?layer=Plates");
      assertTrue(get(host, "/api/status").contains("\"layer\":\"Plates\""));
      post(host, "/api/layer?layer=Overlay");
      assertTrue(get(host, "/api/status").contains("\"layer\":\"Overlay\""));
    }
  }

  @Test
  @DisplayName("FR-5: no Working… map overlay; busy still reported")
  void noWorkingOverlay() throws Exception {
    Path root = findRepoRoot();
    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertFalse(canvas.contains("Working…"));
    assertFalse(canvas.contains("Working..."));
    assertFalse(canvas.contains("map-busy"));
    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertFalse(css.contains(".map-busy"));

    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController controller =
        new MapController(new WorldSpec(8, 8, 1L), queue::add, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      assertTrue(get(host, "/api/status").contains("\"busy\":false"));
    }
  }

  @Test
  @DisplayName("FR-6: brighter bathymetry + land clamp 64")
  void brighterDeeperPaint() {
    assertEquals(64, ElevationRaster.CLAMP);
    assertEquals(0x6ebee2, ElevationRaster.OCEAN_RGB);
    assertEquals(ElevationRaster.OCEAN_RGB, ElevationRaster.oceanRamp(-1));
    assertNotEquals(ElevationRaster.oceanRamp(-1), ElevationRaster.oceanRamp(-64));
    assertEquals(ElevationRaster.landRamp(64), ElevationRaster.rgbOf(64));
    assertEquals(ElevationRaster.landRamp(64), ElevationRaster.rgbOf(99));
    assertNotEquals(ElevationRaster.landRamp(0), ElevationRaster.landRamp(64));
  }

  @Test
  @DisplayName("FR-7: docs F-052; collector mean; no JFrame")
  void docsAndMean() throws Exception {
    Path root = findRepoRoot();
    String blocker = Files.readString(root.resolve("docs/blockers/F-052.md"));
    assertTrue(blocker.contains("FR-1"));
    assertTrue(blocker.contains("Perf"));
    String features = Files.readString(root.resolve("docs/project/features.md"));
    assertTrue(features.contains("F-052"));

    DiagnosticsHub hub = DiagnosticsHub.withDefaults();
    hub.record(DiagnosticIds.PAINT_WALL, 10);
    hub.record(DiagnosticIds.PAINT_WALL, 30);
    assertEquals(20L, hub.get(DiagnosticIds.PAINT_WALL).mean());
    assertNull(hub.get(DiagnosticIds.HEAP_USED).mean());

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertFalse(tool.contains("JFrame"));
  }

  private String get(MapHost host, String path) throws Exception {
    HttpResponse<String> res =
        http.send(
            HttpRequest.newBuilder(URI.create(host.baseUrl() + path)).GET().build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, res.statusCode());
    return res.body();
  }

  private void post(MapHost host, String path) throws Exception {
    HttpResponse<String> res =
        http.send(
            HttpRequest.newBuilder(URI.create(host.baseUrl() + path))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build(),
            HttpResponse.BodyHandlers.ofString());
    assertTrue(res.statusCode() >= 200 && res.statusCode() < 300);
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
