/*
 * File: ui/src/test/java/com/aethelgard/ui/host/RunnerShellTest.java
 * Purpose: F-051 witness — runner shell, gray chrome, physical map, speeds
 * Audience: Agents / CI
 * Update when: F-051 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.ui.ElevationRaster;
import com.aethelgard.ui.MapSpeed;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RunnerShellTest {

  @Test
  @DisplayName("FR-1: style guide documents runner shell slots + gray + map palette")
  void styleGuideShell() throws Exception {
    Path root = findRepoRoot();
    String style = Files.readString(root.resolve("docs/product/style-guide.md"));
    assertTrue(style.contains("F-051") || style.toLowerCase().contains("runner shell"));
    assertTrue(style.contains("1x") && style.contains("Fastest"));
    assertTrue(style.contains("Transport") || style.contains("runner") || style.contains("World rail"));
    assertTrue(style.contains("--ink") || style.toLowerCase().contains("gray"));
  }

  @Test
  @DisplayName("FR-2/FR-3: top bar transport + world rail; host dot")
  void barAndWorldRail() throws Exception {
    Path root = findRepoRoot();
    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("runner-identity") || tool.contains("host-dot"));
    assertTrue(tool.contains("Play") && tool.contains("Pause"));
    assertTrue(tool.contains("1x") && tool.contains("Fastest"));
    assertTrue(tool.contains("Reset view"));
    assertTrue(tool.contains("data-panel=\"world\"") || tool.contains("World"));
    assertTrue(tool.contains("Reset world") || tool.contains("seed"));
    assertFalse(tool.contains("Host linked"));
  }

  @Test
  @DisplayName("FR-4: map layer HUD + physical atlas paint constants")
  void mapLayerAndPalette() throws Exception {
    Path root = findRepoRoot();
    String canvas = Files.readString(root.resolve("ui/web/src/components/MapCanvas.tsx"));
    assertTrue(canvas.contains("map-layer-switch") || canvas.contains("onLayer"));

    assertEquals(0x6ebee2, ElevationRaster.OCEAN_RGB);
    assertEquals(MapSpeed.X1.periodMillis(), 250);
    assertEquals(MapSpeed.FASTEST.label(), "Fastest");

    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("map-layer-switch"));
    assertTrue(css.contains("--terminal-fg"));
  }

  @Test
  @DisplayName("FR-5: terminal continuous surface tokens (not phosphor green)")
  void terminalRestyle() throws Exception {
    Path root = findRepoRoot();
    String css = Files.readString(root.resolve("ui/web/src/app/globals.css"));
    assertTrue(css.contains("--terminal-bg"));
    assertFalse(css.contains("#7dff9a"));
    assertFalse(css.contains("#8dffaa"));
    String terminal = Files.readString(root.resolve("ui/web/src/components/Terminal.tsx"));
    assertTrue(terminal.contains("aethelgard>"));
    assertTrue(terminal.contains("postCommand") || Files.readString(root.resolve("ui/web/src/components/MapTool.tsx")).contains("postCommand"));
  }

  @Test
  @DisplayName("FR-6: docs mention F-051; shell preserved under F-052; no JFrame")
  void docsAndScope() throws Exception {
    Path root = findRepoRoot();
    String blocker = Files.readString(root.resolve("docs/blockers/F-051.md"));
    assertTrue(blocker.contains("FR-1"));
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("F-051") || arch.contains("F-052"));
    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertFalse(tool.contains("JFrame"));
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
