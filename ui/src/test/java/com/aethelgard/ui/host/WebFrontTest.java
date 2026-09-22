/*
 * File: ui/src/test/java/com/aethelgard/ui/host/WebFrontTest.java
 * Purpose: F-025 structural witness for Next tool under ui/web
 * Audience: Agents / CI
 * Update when: F-025 FRs change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WebFrontTest {

  @Test
  @DisplayName("FR-1/FR-4/FR-5: ui/web Next app; host client; client play; docs; no JFrame")
  void nextFrontUnderUi() throws Exception {
    Path root = findRepoRoot();
    Path web = root.resolve("ui/web");
    assertTrue(Files.isDirectory(web));
    assertFalse(Files.exists(root.resolve("web")));
    assertFalse(Files.exists(root.resolve("apps")));

    String pkg = Files.readString(web.resolve("package.json"));
    assertTrue(pkg.contains("\"next\""));
    assertTrue(pkg.contains("\"react\""));

    String hostClient = Files.readString(web.resolve("src/lib/host.ts"));
    assertTrue(hostClient.contains("DEFAULT_HOST"));
    assertTrue(hostClient.contains("127.0.0.1:7420"));
    assertTrue(hostClient.contains("NEXT_PUBLIC_MAP_HOST"));
    assertTrue(hostClient.contains("/api/advance"));
    assertTrue(hostClient.contains("/api/status"));
    assertTrue(hostClient.contains("/api/raster"));
    assertTrue(hostClient.contains("/api/command"));

    String tool = Files.readString(web.resolve("src/components/MapTool.tsx"));
    assertTrue(tool.contains("postAdvance"));
    assertTrue(tool.contains("SPEED_MS"));
    assertTrue(tool.contains("setInterval"));
    assertTrue(tool.contains("Elevation"));
    assertTrue(tool.contains("Plates"));
    assertTrue(tool.contains("Overlay"));
    assertTrue(tool.contains("Terminal") || tool.contains("Console"));
    assertTrue(tool.contains("Working...") || tool.contains("statusText"));
    assertFalse(tool.contains("JFrame"));
    assertFalse(tool.contains("javax.swing"));

    String readme = Files.readString(web.resolve("README.md"));
    assertTrue(readme.contains("npm run dev"));
    assertTrue(readme.contains("MapHostApp"));

    String uiReadme = Files.readString(root.resolve("ui/README.md"));
    assertTrue(uiReadme.contains("ui/web"));
    String flows = Files.readString(root.resolve("docs/product/journeys.md"));
    assertTrue(flows.contains("Aethelgard"));
    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("ui/web") || arch.contains("Next.js"));
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
