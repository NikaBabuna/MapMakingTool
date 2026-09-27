/*
 * File: product/src/test/java/com/aethelgard/product/session/ProductSessionTest.java
 * Purpose: Proves what a session is: one world it owns, reports, and advances one caller at a time
 * Audience: Agents / CI
 * Update when: ProductSession changes
 */

package com.aethelgard.product.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.world.ProductHost;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.fields.WorldSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductSessionTest {

  /** Proves F-068 FR-43 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A session owns one world, reports its spec, step and fields, and refuses a negative advance")
  void sessionOwnsOneWorld() {
    WorldSpec spec = new WorldSpec(16, 8, 2L);
    ProductSession session = new ProductSession(spec);
    assertEquals(spec, session.spec());
    assertEquals(0, session.stepIndex());
    Grid platesAtZero = session.plates();
    String worldAtZero = session.settledWorld();

    session.advance(3);
    assertEquals(3, session.stepIndex());
    assertEquals(16, session.elevation().width());
    assertEquals(8, session.elevation().height());
    assertEquals(session.plates(), session.field(WorldFields.PLATES));
    assertTrue(session.fieldNames().containsAll(List.of(
        WorldFields.ELEVATION, WorldFields.PLATES, WorldFields.PLATE_VELOCITY, WorldFields.PLATE_REGISTRY,
        WorldFields.BOUNDARIES, WorldFields.AREA_FLUX, WorldFields.MOTION_INTENT, WorldFields.OCCUPANCY, WorldFields.LOCKERS)));
    assertEquals(List.of(ProductHost.TECTONICS_SYSTEM_ID), session.systemIds());
    assertEquals(new ProductSession(spec).plates(), platesAtZero, "a value read earlier does not change as the world moves");
    assertTrue(!worldAtZero.equals(session.settledWorld()), "the world moved");

    assertThrows(IllegalArgumentException.class, () -> session.advance(-1));
    assertEquals(3, session.stepIndex());
  }

  /** Proves F-068 FR-43 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Advances from several threads run one after another and give the same world as one caller")
  void concurrentAdvancesDoNotInterleave() throws Exception {
    WorldSpec spec = new WorldSpec(24, 12, 6L);
    ProductSession shared = new ProductSession(spec);
    ExecutorService pool = Executors.newFixedThreadPool(4);
    CountDownLatch start = new CountDownLatch(1);
    List<Future<?>> done = new ArrayList<>();
    for (int t = 0; t < 4; t++) {
      done.add(pool.submit(() -> {
        start.await();
        shared.advance(5);
        return null;
      }));
    }
    start.countDown();
    for (Future<?> f : done) {
      f.get();
    }
    pool.shutdown();

    ProductSession alone = new ProductSession(spec);
    alone.advance(20);
    assertEquals(20, shared.stepIndex());
    assertEquals(alone.settledWorld(), shared.settledWorld());
  }
}
