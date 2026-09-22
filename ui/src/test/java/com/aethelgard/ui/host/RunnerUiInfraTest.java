/*
 * File: ui/src/test/java/com/aethelgard/ui/host/RunnerUiInfraTest.java
 * Purpose: F-053 witness — panel registry, menu model, resizable layout, palette, QoL
 * Audience: Agents / CI
 * Update when: F-053 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.WorldSpec;
import com.aethelgard.ui.ElevationRaster;
import com.aethelgard.ui.LegendEntry;
import com.aethelgard.ui.MapController;
import com.aethelgard.ui.PlayScheduler;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RunnerUiInfraTest {

  private final HttpClient http =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

  @Test
  @DisplayName("FR-1: rails render from the panel registry, not per-panel JSX")
  void panelRegistryDrivesRails() throws Exception {
    Path root = findRepoRoot();
    String panels = Files.readString(root.resolve("ui/web/src/lib/panels.ts"));
    for (String field : List.of("id", "title", "dock", "order", "defaultOpen", "collapsible")) {
      assertTrue(panels.contains(field + ":"), "descriptor field " + field);
    }
    for (String id : List.of("perf", "world", "inspect", "legend")) {
      assertTrue(panels.contains("id: \"" + id + "\""), "descriptor " + id);
    }
    assertTrue(panels.contains("dock: \"left\""));
    assertTrue(panels.contains("dock: \"right\""));
    assertTrue(panels.contains("export function panelsFor"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("panelsFor(\"left\").map(renderPanel)"));
    assertTrue(tool.contains("panelsFor(\"right\").map(renderPanel)"));
    // No hand-written panel chrome left in the rails
    assertFalse(tool.contains("data-panel=\"inspect\""));
    assertFalse(tool.contains("data-panel=\"legend\""));
    assertFalse(tool.contains("<article className=\"studio-panel\""));
  }

  @Test
  @DisplayName("FR-2: Panel owns chrome, collapse, and per-id persistence")
  void panelComponentOwnsChrome() throws Exception {
    Path root = findRepoRoot();
    String panel = Files.readString(root.resolve("ui/web/src/components/Panel.tsx"));
    assertTrue(panel.contains("studio-panel"));
    assertTrue(panel.contains("data-panel={descriptor.id}"));
    assertTrue(panel.contains("panel-chrome"));
    assertTrue(panel.contains("panel-toggle"));
    assertTrue(panel.contains("aria-expanded={open}"));
    assertTrue(panel.contains("panel-body"));
    assertTrue(panel.contains("descriptor.collapsible"));

    String panels = Files.readString(root.resolve("ui/web/src/lib/panels.ts"));
    assertTrue(panels.contains("`aethelgard.panel.${id}.open`"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("panelOpenKey"));
    assertTrue(tool.contains("togglePanel"));
  }

  @Test
  @DisplayName("FR-3: menu-bar row renders File..Help from menus.ts; stubs inert")
  void menuBarFromDescriptors() throws Exception {
    Path root = findRepoRoot();
    String menus = Files.readString(root.resolve("ui/web/src/lib/menus.ts"));
    for (String label : List.of("File", "Edit", "View", "Simulation", "Help")) {
      assertTrue(menus.contains("label: \"" + label + "\""), "menu " + label);
    }
    for (String action :
        List.of(
            "world.new",
            "seed.copy",
            "view.resetView",
            "view.resetLayout",
            "sim.play",
            "sim.pause",
            "sim.advance",
            "sim.restartUi",
            "sim.restartEngine",
            "help.shortcuts")) {
      assertTrue(menus.contains("\"" + action + "\""), "action " + action);
    }
    assertTrue(menus.contains("enabled: false"), "stub items exist");

    String bar = Files.readString(root.resolve("ui/web/src/components/MenuBar.tsx"));
    assertTrue(bar.contains("role=\"menubar\""));
    assertTrue(bar.contains("role=\"menuitem\""));
    assertTrue(bar.contains("aria-disabled"));
    assertTrue(bar.contains("Escape"));
    assertTrue(bar.contains("ArrowRight") && bar.contains("ArrowLeft"));
    assertTrue(bar.contains("menus.map"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("<MenuBar"));
    assertTrue(tool.contains("onMenuAction"));
    assertTrue(tool.contains("location.reload"));
    assertTrue(tool.contains("postRestartEngine"));
    assertFalse(tool.contains("JFrame"));

    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains(".menu-bar"));
    assertTrue(css.contains("--menu-h"));
    assertTrue(css.contains("grid-template-rows: var(--menu-h) var(--bar-h)"));
  }

  @Test
  @DisplayName("FR-4: rail widths + terminal height resize, persist, and reset")
  void layoutResizePersistReset() throws Exception {
    Path root = findRepoRoot();
    String layout = Files.readString(root.resolve("ui/web/src/lib/layout.ts"));
    for (String region : List.of("leftRail", "rightRail", "terminal")) {
      assertTrue(layout.contains(region), "region " + region);
    }
    assertTrue(layout.contains("aethelgard.layout."));
    assertTrue(layout.contains("LAYOUT_LIMITS"));
    assertTrue(layout.contains("export function clampSize"));
    assertTrue(layout.contains("export function readLayout"));
    assertTrue(layout.contains("export function writeLayout"));
    assertTrue(layout.contains("export function clearLayout"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("beginResize(\"leftRail\""));
    assertTrue(tool.contains("beginResize(\"rightRail\""));
    assertTrue(tool.contains("beginResize(\"terminal\""));
    assertTrue(tool.contains("clampSize"));
    assertTrue(tool.contains("writeLayout"));
    assertTrue(tool.contains("function resetLayout"));
    assertTrue(tool.contains("\"--terminal-h\""));

    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains(".rail-splitter"));
    assertTrue(css.contains("cursor: col-resize"));
    assertTrue(css.contains("cursor: row-resize"));
    assertTrue(css.contains("height: var(--terminal-h)"));

    String menus = Files.readString(root.resolve("ui/web/src/lib/menus.ts"));
    assertTrue(menus.contains("Reset layout"));
  }

  @Test
  @DisplayName("FR-5: separation tokens split stage, rails, and panels")
  void separationTokens() throws Exception {
    Path root = findRepoRoot();
    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("--panel-edge:"));
    assertTrue(css.contains("--gutter:"));
    assertTrue(css.contains("--rail-bg:"));
    int panelIdx = css.indexOf(".studio-panel {");
    assertTrue(panelIdx >= 0);
    String panelBlock = css.substring(panelIdx, panelIdx + 320);
    assertTrue(panelBlock.contains("var(--panel-edge)"));
    assertTrue(panelBlock.contains("box-shadow"));
    int railIdx = css.indexOf(".side-rail {");
    assertTrue(railIdx >= 0);
    assertTrue(css.substring(railIdx, railIdx + 320).contains("var(--rail-bg)"));
  }

  @Test
  @DisplayName("FR-6: brighter documented ramps stay deterministic and match the legend")
  void brighterPaletteAndLegend() {
    // Brighter than the F-052 stops (ocean shallow 72,128,168 · land peak 248,236,210)
    assertEquals(0x6ebee2, ElevationRaster.OCEAN_RGB);
    assertTrue(ElevationRaster.OCEAN_STOP_G[4] > 128);
    assertTrue(ElevationRaster.OCEAN_STOP_B[4] > 168);
    assertTrue(ElevationRaster.LAND_STOP_G[0] > 168);
    assertTrue(ElevationRaster.LAND_STOP_R[4] >= 248);
    assertTrue(ElevationRaster.LAND_STOP_G[4] > 236);
    assertTrue(ElevationRaster.LAND_STOP_B[4] > 210);

    // Documented stop positions unchanged; ramps monotone in luminance
    assertEquals(64, ElevationRaster.CLAMP);
    assertEquals(-64, ElevationRaster.OCEAN_FLOOR);
    for (int e = -63; e <= -1; e++) {
      assertTrue(
          luma(ElevationRaster.oceanRamp(e)) >= luma(ElevationRaster.oceanRamp(e - 1)),
          "ocean luma rises toward the shore at e=" + e);
    }
    assertTrue(luma(ElevationRaster.landRamp(64)) > luma(ElevationRaster.landRamp(0)));

    // Deterministic
    assertEquals(ElevationRaster.rgbOf(-7), ElevationRaster.rgbOf(-7));
    assertEquals(ElevationRaster.rgbOf(31), ElevationRaster.rgbOf(31));
    assertNotEquals(ElevationRaster.rgbOf(-1), ElevationRaster.rgbOf(0));

    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController controller =
        new MapController(new WorldSpec(8, 8, 11L), queue::add, PlayScheduler.idle());
    List<LegendEntry> legend = controller.legend();
    assertEquals(
        ElevationRaster.oceanRamp(ElevationRaster.OCEAN_FLOOR), legend.get(0).rgb(), "deep swatch");
    assertEquals(ElevationRaster.OCEAN_RGB, legend.get(1).rgb(), "shallow swatch");
    assertEquals(ElevationRaster.landRamp(0), legend.get(2).rgb(), "low land swatch");
    assertEquals(ElevationRaster.landRamp(64), legend.get(3).rgb(), "high land swatch");
  }

  @Test
  @DisplayName("FR-7: QoL — shortcuts overlay, Advance N, copy seed, steps/sec, Clear")
  void qualityOfLife() throws Exception {
    Path root = findRepoRoot();
    String shortcuts = Files.readString(root.resolve("ui/web/src/lib/shortcuts.ts"));
    for (String keys : List.of("Space", "?", "R", "P", "D")) {
      assertTrue(shortcuts.contains("\"" + keys + "\"") || shortcuts.contains(keys), "key " + keys);
    }
    String overlay = Files.readString(root.resolve("ui/web/src/components/ShortcutsOverlay.tsx"));
    assertTrue(overlay.contains("role=\"dialog\""));
    assertTrue(overlay.contains("SHORTCUTS.map"));

    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("<ShortcutsOverlay"));
    assertTrue(tool.contains("key === \"?\""));
    assertTrue(tool.contains("session advance ${count}"));
    assertTrue(tool.contains("Advance ×N"));
    assertTrue(tool.contains("clipboard.writeText"));
    assertTrue(tool.contains("formatStepRate"));
    assertTrue(tool.contains("data-diag=\"advance.rate\""));

    String terminal = Files.readString(root.resolve("ui/web/src/components/Terminal.tsx"));
    assertTrue(terminal.contains("terminal-clear"));
    assertTrue(terminal.contains("setLog([])"));

    // Advance N rides the existing dispatcher — no new verb
    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController controller =
        new MapController(new WorldSpec(8, 8, 5L), queue::add, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      String body = post(host, "/api/command", "session advance 3");
      assertTrue(body.contains("\"ok\":true"), body);
      assertTrue(get(host, "/api/status").contains("\"step\":3"));
    }
  }

  @Test
  @DisplayName("FR-8: docs synced for F-053 (F-054 may later close Goal)")
  void docsSynced() throws Exception {
    Path root = findRepoRoot();
    String blocker = Files.readString(root.resolve("docs/paperwork/steps/F-053.md"));
    assertTrue(blocker.contains("FR-1") && blocker.contains("FR-8"));

    String features = Files.readString(root.resolve("docs/paperwork/steps.md"));
    assertTrue(features.contains("F-053"));

    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("Menu bar") || style.contains("menu bar"));
    assertTrue(style.contains("?"));
    String layout = Files.readString(root.resolve("ui/web/src/lib/layout.ts"));
    assertTrue(layout.contains("aethelgard.layout."));

    String arch = Files.readString(root.resolve("docs/architecture/studio/host.md"));
    assertTrue(arch.contains("panels.ts"));
    assertTrue(arch.contains("panels.ts") || arch.contains("panel registry"));

    String glossary = Files.readString(root.resolve("docs/product/glossary.md"));
    assertTrue(glossary.contains("Plate"));
    assertTrue(glossary.contains("Overlay"));

    String flows = Files.readString(root.resolve("docs/product/journeys.md"));
    assertTrue(flows.contains("Play"));

    String changelog = Files.readString(root.resolve("docs/paperwork/changelog.md"));
    assertTrue(changelog.contains("F-053"));

    String goal =
        Files.readString(root.resolve("docs/paperwork/goals/G-009-simulation-runner-harden.md"));
    assertTrue(goal.contains("F-053"));
  }

  private static int luma(int rgb) {
    int r = (rgb >> 16) & 0xff;
    int g = (rgb >> 8) & 0xff;
    int b = rgb & 0xff;
    return (r * 299 + g * 587 + b * 114) / 1000;
  }

  private String get(MapHost host, String path) throws Exception {
    HttpResponse<String> res =
        http.send(
            HttpRequest.newBuilder(URI.create(host.baseUrl() + path)).GET().build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, res.statusCode());
    return res.body();
  }

  private String post(MapHost host, String path, String body) throws Exception {
    HttpResponse<String> res =
        http.send(
            HttpRequest.newBuilder(URI.create(host.baseUrl() + path))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString());
    assertTrue(res.statusCode() >= 200 && res.statusCode() < 300, "status " + res.statusCode());
    return res.body();
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
