/*
 * File: ui/src/test/java/com/aethelgard/ui/host/MapHostTest.java
 * Purpose: F-024 localhost HTTP host over MapController
 * Audience: Agents / CI
 * Update when: F-024 FRs change
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
import java.io.IOException;
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

class MapHostTest {

  private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

  @Test
  @DisplayName("FR-1: host under ui; product/engine have no HTTP host types")
  void hostLivesInUi() throws Exception {
    Path root = findRepoRoot();
    assertTrue(Files.isRegularFile(root.resolve("ui/src/main/java/com/aethelgard/ui/host/MapHost.java")));
    assertFalse(Files.exists(root.resolve("host")));
    assertFalse(
        Files.walk(root.resolve("product/src/main/java"))
            .anyMatch(p -> p.toString().contains("MapHost") || p.toString().contains("httpserver")));
    assertFalse(
        Files.walk(root.resolve("engine/src/main/java"))
            .anyMatch(p -> p.toString().contains("MapHost") || p.toString().contains("httpserver")));

    try (MapHost host = MapHost.start(new WorldSpec(8, 8, 0L), 0)) {
      HttpResponse<String> health =
          http.send(
              HttpRequest.newBuilder(URI.create(host.baseUrl() + "/health")).GET().build(),
              HttpResponse.BodyHandlers.ofString());
      assertEquals(200, health.statusCode());
      assertEquals("ok", health.body());
    }
  }

  @Test
  @DisplayName("FR-2/FR-3: status JSON; mutations; busy Working...; console dispatch")
  void statusMutationsBusyCommand() throws Exception {
    ArrayDeque<Runnable> queue = new ArrayDeque<>();
    MapController controller =
        new MapController(new WorldSpec(8, 8, 0L), queue::add, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      String status = get(host, "/api/status");
      assertTrue(status.contains("\"step\":0"));
      assertTrue(status.contains("\"seed\":0"));
      assertTrue(status.contains("\"width\":8"));
      assertTrue(status.contains("\"height\":8"));
      assertTrue(status.contains("\"layer\":\"Elevation\""));
      assertTrue(status.contains("\"speed\":\"1x\""));
      assertTrue(status.contains("\"playing\":false"));
      assertTrue(status.contains("\"busy\":false"));
      assertTrue(status.contains("\"statusText\":\"Step 0\""));

      post(host, "/api/advance");
      String busyStatus = get(host, "/api/status");
      assertTrue(busyStatus.contains("\"busy\":true"));
      assertTrue(busyStatus.contains("\"statusText\":\"Working...\""));

      post(host, "/api/advance");
      assertEquals(1, queue.size(), "second advance ignored while busy");

      queue.removeFirst().run();
      awaitNotBusy(host);
      String after = get(host, "/api/status");
      assertTrue(after.contains("\"step\":1"));
      assertTrue(after.contains("\"busy\":false"));

      post(host, "/api/layer?layer=Plates");
      assertTrue(get(host, "/api/status").contains("\"layer\":\"Plates\""));
      post(host, "/api/speed?speed=4x");
      assertTrue(get(host, "/api/status").contains("\"speed\":\"4x\""));

      post(host, "/api/play");
      assertTrue(get(host, "/api/status").contains("\"playing\":true"));
      post(host, "/api/pause");
      assertTrue(get(host, "/api/status").contains("\"playing\":false"));

      post(host, "/api/inspect?x=1&y=2");
      String inspected = get(host, "/api/status");
      assertTrue(inspected.contains("\"inspect\":{"));
      assertTrue(inspected.contains("\"x\":1"));
      assertTrue(inspected.contains("\"y\":2"));

      post(host, "/api/new-world?seed=7");
      String reseeded = get(host, "/api/status");
      assertTrue(reseeded.contains("\"seed\":7"));
      assertTrue(reseeded.contains("\"step\":0"));

      HttpResponse<String> cmd =
          http.send(
              HttpRequest.newBuilder(URI.create(host.baseUrl() + "/api/command"))
                  .POST(HttpRequest.BodyPublishers.ofString("status"))
                  .header("Content-Type", "text/plain")
                  .build(),
              HttpResponse.BodyHandlers.ofString());
      assertEquals(200, cmd.statusCode());
      assertTrue(cmd.body().contains("\"ok\":true"));
      assertTrue(cmd.body().contains("step=0 width=8 height=8 seed=7"));
    }
  }

  @Test
  @DisplayName("FR-4: raster bytes match ElevationRaster for same grids + layer")
  void rasterMatchesController() throws Exception {
    MapController controller = new MapController(new WorldSpec(8, 8, 0L));
    controller.advance();
    controller.setLayer(MapLayer.OVERLAY);
    try (MapHost host = MapHost.start(controller, 0)) {
      HttpResponse<byte[]> response =
          http.send(
              HttpRequest.newBuilder(URI.create(host.baseUrl() + "/api/raster")).GET().build(),
              HttpResponse.BodyHandlers.ofByteArray());
      assertEquals(200, response.statusCode());
      byte[] body = response.body();
      int w = MapHost.readInt(body, 0);
      int h = MapHost.readInt(body, 4);
      ElevationRaster expected = controller.raster();
      assertEquals(expected.width(), w);
      assertEquals(expected.height(), h);
      assertEquals(8 + w * h * 4, body.length);
      int o = 8;
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          assertEquals(expected.rgb(x, y), MapHost.readInt(body, o));
          o += 4;
        }
      }
      byte[] packed = MapHost.packRaster(expected);
      assertEquals(packed.length, body.length);
      for (int i = 0; i < packed.length; i++) {
        assertEquals(packed[i], body[i]);
      }
    }
  }

  @Test
  @DisplayName("FR-5: no JFrame; determinism; docs mention MapHost")
  void houseDocsDeterminism() throws Exception {
    Path root = findRepoRoot();
    String hostSrc =
        Files.readString(root.resolve("ui/src/main/java/com/aethelgard/ui/host/MapHost.java"));
    assertFalse(hostSrc.contains("javax.swing"));
    assertFalse(hostSrc.contains("JFrame"));

    WorldSpec spec = new WorldSpec(8, 8, 0L);
    MapController a = new MapController(spec);
    MapController b = new MapController(spec);
    a.advance();
    a.advance();
    a.advance();
    b.advance();
    b.advance();
    b.advance();
    assertEquals(a.session().settledWorld(), b.session().settledWorld());

    String arch = Files.readString(root.resolve("docs/product/architecture.md"));
    assertTrue(arch.contains("MapHost"));
    String glossary = Files.readString(root.resolve("docs/product/glossary.md"));
    assertTrue(glossary.contains("MapHost"));
    String uiReadme = Files.readString(root.resolve("ui/README.md"));
    assertTrue(uiReadme.contains("MapHost"));
  }

  private String get(MapHost host, String path) throws IOException, InterruptedException {
    HttpResponse<String> response =
        http.send(
            HttpRequest.newBuilder(URI.create(host.baseUrl() + path)).GET().build(),
            HttpResponse.BodyHandlers.ofString());
    assertEquals(200, response.statusCode(), response.body());
    return response.body();
  }

  private void post(MapHost host, String path) throws IOException, InterruptedException {
    HttpResponse<String> response =
        http.send(
            HttpRequest.newBuilder(URI.create(host.baseUrl() + path))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build(),
            HttpResponse.BodyHandlers.ofString());
    assertTrue(response.statusCode() == 200 || response.statusCode() == 400, response.body());
  }

  private void awaitNotBusy(MapHost host) throws IOException, InterruptedException {
    long deadline = System.nanoTime() + Duration.ofSeconds(2).toNanos();
    while (System.nanoTime() < deadline) {
      if (get(host, "/api/status").contains("\"busy\":false")) {
        return;
      }
      Thread.sleep(5);
    }
    throw new AssertionError("still busy");
  }

  private static Path findRepoRoot() throws IOException {
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
    throw new IOException("repo root not found from " + Path.of("").toAbsolutePath());
  }
}
