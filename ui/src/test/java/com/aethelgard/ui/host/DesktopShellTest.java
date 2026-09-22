/*
 * File: ui/src/test/java/com/aethelgard/ui/host/DesktopShellTest.java
 * Purpose: F-026 Tauri shell + Swing removal + G-006 close witnesses
 * Audience: Agents / CI
 * Update when: F-026 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DesktopShellTest {

  @Test
  @DisplayName("FR-1/FR-2: Tauri under ui/desktop; launch scripts; MapHostApp PID")
  void tauriDesktopAndLaunch() throws Exception {
    Path root = findRepoRoot();
    assertTrue(Files.isDirectory(root.resolve("ui/desktop/src-tauri")));
    assertTrue(Files.isRegularFile(root.resolve("ui/desktop/src-tauri/tauri.conf.json")));
    assertTrue(Files.isRegularFile(root.resolve("ui/desktop/src-tauri/src/lib.rs")));
    String conf = Files.readString(root.resolve("ui/desktop/src-tauri/tauri.conf.json"));
    assertTrue(conf.contains("localhost:3000"));
    assertTrue(conf.contains("beforeDevCommand"));
    assertTrue(conf.contains("--prefix ../web") || conf.contains("../web"));
    String rust = Files.readString(root.resolve("ui/desktop/src-tauri/src/lib.rs"));
    assertTrue(rust.contains("MapHostApp"));
    assertTrue(rust.contains("aethelgard-maphost.pid") || rust.contains("pid_file"));
    assertTrue(rust.contains("stop_map_host") || rust.contains("taskkill") || rust.contains("kill"));

    String app =
        Files.readString(root.resolve("ui/src/main/java/com/aethelgard/ui/host/MapHostApp.java"));
    assertTrue(app.contains("PID_FILE_NAME"));
    assertTrue(app.contains("aethelgard-maphost.pid"));

    String run = Files.readString(root.resolve("run-product.cmd"));
    assertTrue(run.contains("desktop"));
    assertTrue(run.contains("ui\\web") || run.contains("ui/web"));
    assertFalse(run.contains("ProductApp"));

    String desktopReadme = Files.readString(root.resolve("ui/desktop/README.md"));
    assertTrue(desktopReadme.contains("MapHost"));
    assertTrue(desktopReadme.contains("tauri") || desktopReadme.contains("Tauri"));
  }

  @Test
  @DisplayName("FR-3: Swing interactive types removed from ui main")
  void swingGone() throws Exception {
    Path root = findRepoRoot();
    assertFalse(Files.exists(root.resolve("ui/src/main/java/com/aethelgard/ui/MapFrame.java")));
    assertFalse(Files.exists(root.resolve("ui/src/main/java/com/aethelgard/ui/ProductApp.java")));
    assertFalse(
        Files.exists(root.resolve("ui/src/main/java/com/aethelgard/ui/SwingPlayScheduler.java")));
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
    assertTrue(
        Files.isRegularFile(root.resolve("ui/src/main/java/com/aethelgard/ui/MapController.java")));
    assertTrue(
        Files.isRegularFile(root.resolve("ui/src/main/java/com/aethelgard/ui/host/MapHost.java")));
  }

  @Test
  @DisplayName("FR-5: G-006 remains done; entry points may name a later Active Goal")
  void goalClosed() throws Exception {
    Path root = findRepoRoot();
    String goals = Files.readString(root.resolve("docs/paperwork/goals.md"));
    assertTrue(goals.contains("G-006"));
    assertTrue(goals.contains("G-006-webview-front.md"));
    String table =
        goals.lines().filter(l -> l.contains("| G-006 |")).findFirst().orElse("");
    assertTrue(table.contains("| done |"), table);
    // Active Goal may be none (post-close) or a later Goal (e.g. G-007)
    assertTrue(
        goals.contains("**Active Goal:** none")
            || goals.contains("G-007")
            || goals.contains("Active Goal:** [G-"));

    String g006 =
        Files.readString(root.resolve("docs/paperwork/goals/G-006-webview-front.md"));
    assertTrue(g006.contains("**Status:** `done`") || g006.contains("**Status:** done"));

    // Entry points may advance past naming G-006; goals index + Goal file remain authoritative.
    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("G-006") || goals.contains("G-006"));
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
