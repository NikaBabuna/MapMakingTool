/*
 * File: ui/src/test/java/com/aethelgard/ui/LargeViewHostTest.java
 * Purpose: F-031 FR-3 MapController / MapHost on VIEW 1920×1080
 * Audience: Agents / CI
 * Update when: F-031 FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.WorldSpec;
import com.aethelgard.ui.host.MapHost;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LargeViewHostTest {

  @Test
  @DisplayName("FR-3: MapController raster + MapHost status/raster match VIEW 1920×1080")
  void viewRasterAndHostStatus() throws Exception {
    MapController map = MapController.view(Runnable::run);
    assertEquals(1920, map.spec().width());
    assertEquals(1080, map.spec().height());
    ElevationRaster raster = map.raster();
    assertEquals(1920, raster.width());
    assertEquals(1080, raster.height());
    assertEquals(ElevationRaster.rgbOf(0), raster.rgb(0, 0));

    MapHost host = MapHost.start(WorldSpec.VIEW, 0);
    try {
      HttpClient client = HttpClient.newHttpClient();
      String status =
          client
              .send(
                  HttpRequest.newBuilder(URI.create(host.baseUrl() + "/api/status")).GET().build(),
                  HttpResponse.BodyHandlers.ofString())
              .body();
      assertTrue(status.contains("\"width\":1920"));
      assertTrue(status.contains("\"height\":1080"));

      HttpResponse<byte[]> rasterRes =
          client.send(
              HttpRequest.newBuilder(URI.create(host.baseUrl() + "/api/raster")).GET().build(),
              HttpResponse.BodyHandlers.ofByteArray());
      assertEquals("1920", rasterRes.headers().firstValue("X-Width").orElse(""));
      assertEquals("1080", rasterRes.headers().firstValue("X-Height").orElse(""));
      assertEquals(8 + 1920L * 1080L * 4L, rasterRes.body().length);
    } finally {
      host.close();
    }
  }
}
