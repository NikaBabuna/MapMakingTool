/*
 * File: ui/src/test/java/com/aethelgard/ui/host/MapHostTest.java
 * Purpose: Proves the studio's HTTP host: what status and raster answer, how actions answer, what is refused, and that repeated rasters reuse their bytes
 * Audience: Agents / CI
 * Update when: MapHost's routes, status, or raster body change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.world.fields.Plates;
import com.aethelgard.product.world.fields.WorldSpec;
import com.aethelgard.ui.ElevationRaster;
import com.aethelgard.ui.MapController;
import com.aethelgard.ui.MapLayer;
import com.aethelgard.ui.PlayScheduler;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MapHostTest {

  private static final WorldSpec SPEC = new WorldSpec(8, 8, 0L);

  private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

  /** Proves F-068 FR-60 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("/health answers ok, and /api/status describes the world, the view, and the diagnostics")
  void statusDescribesTheWorld() throws Exception {
    MapController controller = new MapController(SPEC, Runnable::run, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      HttpResponse<String> health = get(host, "/health");
      assertEquals(200, health.statusCode());
      assertEquals("ok", health.body());

      String status = get(host, "/api/status").body();
      for (String part : List.of("\"step\":0", "\"seed\":0", "\"width\":8", "\"height\":8", "\"layer\":\"Elevation\"",
          "\"speed\":\"1x\"", "\"playing\":false", "\"busy\":false", "\"statusText\":\"Step 0\"", "\"inspect\":null",
          "\"legend\":[", "\"diag\":{", "advance.wall")) {
        assertTrue(status.contains(part), "status has " + part + ": " + status);
      }
      controller.inspect(2, 3);
      assertTrue(get(host, "/api/status").body().contains("\"inspect\":{\"x\":2,\"y\":3"));
    }
  }

  /** Proves F-068 FR-60 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("/api/raster is the controller's picture for the status step and layer")
  void rasterMatchesTheStatus() throws Exception {
    MapController controller = new MapController(SPEC, Runnable::run, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      controller.advance();
      controller.setLayer(MapLayer.OVERLAY);

      HttpResponse<byte[]> raster = raster(host);
      assertEquals(200, raster.statusCode());
      assertEquals("8", raster.headers().firstValue("X-Width").orElseThrow());
      assertEquals("8", raster.headers().firstValue("X-Height").orElseThrow());
      ByteBuffer body = ByteBuffer.wrap(raster.body());
      assertEquals(8, body.getInt());
      assertEquals(8, body.getInt());
      int[] pixels = new int[64];
      for (int i = 0; i < 64; i++) {
        pixels[i] = body.getInt();
      }
      ElevationRaster expected =
          ElevationRaster.paint(controller.session().elevation(), controller.session().plates(), MapLayer.OVERLAY);
      assertArrayEquals(expected.pixels(), pixels);
      String status = get(host, "/api/status").body();
      assertTrue(status.contains("\"step\":1") && status.contains("\"layer\":\"Overlay\""));
    }
  }

  /** Proves F-068 FR-60 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Repeated raster requests between paints return the same bytes, and 20 cycles allocate no new body")
  void repeatedRastersReuseTheirBytes() throws Exception {
    MapController controller = new MapController(SPEC, Runnable::run, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      byte[] a = raster(host).body();
      byte[] b = raster(host).body();
      assertArrayEquals(a, b);

      for (int i = 0; i < 20; i++) {
        controller.advance();
        raster(host);
        raster(host);
      }
      assertEquals(1, host.packedBodyAllocations(), "one body for a fixed map size");
    }
  }

  /** Proves F-068 FR-61 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Every action answers with the status once accepted, and an advance shows busy until it is done")
  void actionsAnswerWithStatus() throws Exception {
    ArrayDeque<Runnable> background = new ArrayDeque<>();
    MapController controller = new MapController(SPEC, background::add, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      HttpResponse<String> advance = post(host, "/api/advance", "");
      assertEquals(200, advance.statusCode());
      assertTrue(advance.body().contains("\"busy\":true") && advance.body().contains("\"statusText\":\"Working...\""));
      assertTrue(get(host, "/api/status").body().contains("\"busy\":true"), "still busy");
      background.removeFirst().run();
      assertTrue(get(host, "/api/status").body().contains("\"step\":1"));
      assertTrue(get(host, "/api/status").body().contains("\"busy\":false"));

      assertTrue(post(host, "/api/layer?layer=Plates", "").body().contains("\"layer\":\"Plates\""));
      assertTrue(post(host, "/api/speed?speed=4x", "").body().contains("\"speed\":\"4x\""));
      assertTrue(post(host, "/api/play", "").body().contains("\"playing\":true"));
      assertTrue(post(host, "/api/pause", "").body().contains("\"playing\":false"));
      assertTrue(post(host, "/api/inspect?x=1&y=2", "").body().contains("\"inspect\":{\"x\":1,\"y\":2"));
      String reseeded = post(host, "/api/new-world?seed=7", "").body();
      assertTrue(reseeded.contains("\"seed\":7") && reseeded.contains("\"step\":0"));
      controller.advance();
      String restarted = post(host, "/api/restart-engine", "").body();
      assertTrue(restarted.contains("\"seed\":7") && restarted.contains("\"step\":0"));
    }
  }

  /** Proves F-068 FR-61 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Bad parameters are refused with 400 and a message, and change nothing")
  void badParametersAreRefused() throws Exception {
    MapController controller = new MapController(SPEC, Runnable::run, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      String before = get(host, "/api/status").body();
      for (String[] c : new String[][] {
          {"/api/layer?layer=Sky", "error: unknown layer"},
          {"/api/speed?speed=9x", "error: unknown speed"},
          {"/api/new-world", "error: seed required"},
          {"/api/new-world?seed=north", "error: bad seed"},
          {"/api/inspect?x=1", "error: x and y required"},
          {"/api/inspect?x=a&y=b", "error: bad coordinates"}}) {
        HttpResponse<String> response = post(host, c[0], "");
        assertEquals(400, response.statusCode(), c[0]);
        assertTrue(response.body().contains(c[1]), c[0] + " → " + response.body());
      }
      assertEquals(before, get(host, "/api/status").body());
    }
  }

  /** Proves F-068 FR-61 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("/api/command answers the exit code, ok, the output, and the status; a refused line gets 400")
  void commandRouteAnswers() throws Exception {
    MapController controller = new MapController(SPEC, Runnable::run, PlayScheduler.idle());
    try (MapHost host = MapHost.start(controller, 0)) {
      HttpResponse<String> ok = post(host, "/api/command", "advance 2");
      assertEquals(200, ok.statusCode());
      for (String part : List.of("\"exitCode\":0", "\"ok\":true", "\"output\":\"step=2\"", "\"status\":{", "\"step\":2")) {
        assertTrue(ok.body().contains(part), part + " in " + ok.body());
      }

      HttpResponse<String> refused = post(host, "/api/command", "nope");
      assertEquals(400, refused.statusCode());
      assertTrue(refused.body().contains("\"exitCode\":2") && refused.body().contains("\"ok\":false"), refused.body());
      assertEquals(2, controller.stepIndex());
    }
  }

  private HttpResponse<String> get(MapHost host, String path) throws Exception {
    return http.send(HttpRequest.newBuilder(URI.create(host.baseUrl() + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
  }

  private HttpResponse<String> post(MapHost host, String path, String body) throws Exception {
    return http.send(
        HttpRequest.newBuilder(URI.create(host.baseUrl() + path))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .header("Content-Type", "text/plain")
            .build(),
        HttpResponse.BodyHandlers.ofString());
  }

  private HttpResponse<byte[]> raster(MapHost host) throws Exception {
    return http.send(HttpRequest.newBuilder(URI.create(host.baseUrl() + "/api/raster")).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
  }
}
