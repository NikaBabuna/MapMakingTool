/*
 * File: ui/src/test/java/com/aethelgard/ui/RasterHostMemoryTest.java
 * Purpose: F-047 witness — paint buffer reuse + MapHost packed-body cache
 * Audience: Agents / CI
 * Update when: F-047 FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.DiagnosticIds;
import com.aethelgard.product.WorldSpec;
import com.aethelgard.ui.host.MapHost;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RasterHostMemoryTest {

  private final HttpClient http =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

  @Test
  @DisplayName("FR-1: paint double-buffer reuses slots; prior snapshot not aliased; paint.wall records")
  void paintReusesBuffer() {
    MapController controller = new MapController(new WorldSpec(8, 8, 0L));
    int[] slotA = controller.paintBufferForTest();
    ElevationRaster first = controller.raster();
    assertTrue(first.usesBuffer(slotA));
    int[] firstPixels = first.pixels().clone();

    controller.advance();
    ElevationRaster second = controller.raster();
    assertSame(slotA, controller.paintBufferForTest());
    assertTrue(!second.usesBuffer(slotA), "second paint uses the other slot");
    // Prior snapshot still has original pixels (not overwritten).
    for (int i = 0; i < firstPixels.length; i++) {
      assertEquals(firstPixels[i], first.pixels()[i]);
    }

    controller.setLayer(MapLayer.PLATES);
    assertSame(slotA, controller.paintBufferForTest());
    assertTrue(controller.session().diagnostics().get(DiagnosticIds.PAINT_WALL).size() >= 3);
  }

  @Test
  @DisplayName("FR-2/FR-3: raster GET caches body; bytes match packRaster")
  void hostCachesPackedBody() throws Exception {
    MapController controller = new MapController(new WorldSpec(8, 8, 0L));
    controller.advance();
    try (MapHost host = MapHost.start(controller, 0)) {
      byte[] a = getRaster(host);
      byte[] b = getRaster(host);
      assertEquals(1, host.packedBodyAllocations());
      assertSame(host.cachedPackedBody(), host.cachedPackedBody());
      byte[] expected = MapHost.packRaster(controller.raster());
      assertEquals(expected.length, a.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], a[i]);
        assertEquals(expected[i], b[i]);
      }

      controller.setLayer(MapLayer.OVERLAY);
      byte[] c = getRaster(host);
      assertEquals(1, host.packedBodyAllocations(), "same size reuses allocation");
      byte[] overlayPacked = MapHost.packRaster(controller.raster());
      for (int i = 0; i < overlayPacked.length; i++) {
        assertEquals(overlayPacked[i], c[i]);
      }
    }
  }

  @Test
  @DisplayName("FR-4: 20 advance+raster cycles keep O(1) packed allocations")
  void soakO1PackedAllocations() throws Exception {
    MapController controller = new MapController(new WorldSpec(8, 8, 0L));
    try (MapHost host = MapHost.start(controller, 0)) {
      for (int i = 0; i < 20; i++) {
        controller.advance();
        getRaster(host);
        getRaster(host);
        getRaster(host);
      }
      assertEquals(
          1,
          host.packedBodyAllocations(),
          "fixed geometry must not allocate a new packed body per cycle");
    }
  }

  @Test
  @DisplayName("FR-5: docs mention raster cache/reuse; no engine production edits")
  void docsAndNoEngineEdits() throws Exception {
    Path root = findRepoRoot();
    String arch = Files.readString(root.resolve("docs/architecture/studio/host.md"));
    assertTrue(arch.contains("F-047") || arch.toLowerCase().contains("packed") && arch.contains("cache"));
    assertTrue(arch.toLowerCase().contains("reuse") || arch.contains("flat"));

    String engine =
        Files.readString(
            root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertTrue(engine.contains("for (EngineSystem system : systems)"));
  }

  private byte[] getRaster(MapHost host) throws Exception {
    HttpResponse<byte[]> response =
        http.send(
            HttpRequest.newBuilder(URI.create(host.baseUrl() + "/api/raster")).GET().build(),
            HttpResponse.BodyHandlers.ofByteArray());
    assertEquals(200, response.statusCode());
    return response.body();
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath().normalize();
    for (Path cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("ui"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new Exception("repo root not found");
  }
}
