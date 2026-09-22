/*
 * File: ui/src/test/java/com/aethelgard/ui/ConsoleUiTest.java
 * Purpose: F-023 in-UI console uses cli dispatcher on the map session
 * Audience: Agents / CI
 * Update when: F-023 FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.cli.CliResult;
import com.aethelgard.product.WorldSpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsoleUiTest {

  @Test
  @DisplayName("FR-3: ui depends on cli; runCommand advances and refreshes raster; no JFrame")
  void consoleSharesSession() throws Exception {
    Path root = findRepoRoot();
    String uiPom = Files.readString(root.resolve("ui/pom.xml"));
    assertTrue(uiPom.contains("<artifactId>cli</artifactId>"));
    String cliPom = Files.readString(root.resolve("cli/pom.xml"));
    assertFalse(cliPom.contains("<artifactId>ui</artifactId>"));

    MapController map = new MapController(new WorldSpec(8, 8, 0L));
    ElevationRaster before = map.raster();
    CliResult result = map.runCommand("advance");
    assertTrue(result.ok());
    assertEquals("step=1", result.output());
    assertEquals(1, map.stepIndex());
    assertNotEquals(before, map.raster());
    assertEquals(
        ElevationRaster.paint(map.session().elevation(), map.session().plates(), map.layer()),
        map.raster());

    CliResult status = map.runCommand("status");
    assertEquals("step=1 width=8 height=8 seed=0", status.output());

    assertFalse(Files.exists(root.resolve("ui/src/main/java/com/aethelgard/ui/MapFrame.java")));
    String tool = Files.readString(root.resolve("ui/web/src/components/MapTool.tsx"));
    assertTrue(tool.contains("Terminal") || tool.contains("console"));
    assertTrue(tool.contains("postCommand") || tool.contains("/api/command"));
    assertTrue(Files.isRegularFile(root.resolve("ui/web/src/components/Terminal.tsx")));

    String controller =
        Files.readString(root.resolve("ui/src/main/java/com/aethelgard/ui/MapController.java"));
    assertTrue(controller.contains("CommandDispatch"));
    assertFalse(controller.contains("javax.swing"));
    assertFalse(controller.contains("java.awt"));
  }

  @Test
  @DisplayName("FR-3/FR-4: console ignored while busy; map Advance still uses the session")
  void busyAndMapAdvance() {
    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController map = new MapController(new WorldSpec(8, 8, 0L), queue::add);
    map.advanceAsync();
    CliResult busy = map.runCommand("advance");
    assertEquals(2, busy.exitCode());
    assertTrue(busy.output().contains("busy"));
    assertEquals(0, map.stepIndex());
    queue.removeFirst().run();
    assertEquals(1, map.stepIndex());

    map.advance();
    assertEquals(2, map.stepIndex());
    CliResult again = map.runCommand("advance 2");
    assertTrue(again.ok());
    assertEquals(4, map.stepIndex());
    assertEquals("step=4", again.output());
  }

  @Test
  @DisplayName("FR-5: G-005 remains done in goals registry")
  void goalClosedEntryPoints() throws Exception {
    Path root = findRepoRoot();
    String goals = Files.readString(root.resolve("docs/project/goals.md"));
    assertTrue(goals.contains("G-005-living-map.md"));
    String g005 =
        Files.readString(root.resolve("docs/project/goals/G-005-living-map.md"));
    assertTrue(g005.contains("**Status:** `done`") || g005.contains("**Status:** done"));
    String table =
        goals
            .lines()
            .filter(l -> l.contains("| G-005 |"))
            .findFirst()
            .orElse("");
    assertTrue(table.contains("| done |"), table);
    // Entry points may name a later Goal. The Active Goal line lives on the goal index.
    String agents = Files.readString(root.resolve("AGENTS.md"));
    String goalsActive = goals.lines().filter(l -> l.contains("Active Goal")).findFirst().orElse("");
    assertFalse(goalsActive.isBlank());
    assertTrue(agents.contains("goals.md"));
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
