/*
 * File: ui/src/test/java/com/aethelgard/ui/host/StatusCacheTest.java
 * Purpose: F-038 FR-5 — status/step must not block on ProductSession lock
 * Audience: Agents / CI
 * Update when: Status caching rules change
 */

package com.aethelgard.ui.host;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.ProductSession;
import com.aethelgard.product.WorldSpec;
import com.aethelgard.ui.MapController;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusCacheTest {

  @Test
  @DisplayName("FR-5: stepIndex/statusText return while session lock is held")
  void statusDoesNotWaitOnPhysicsLock() throws Exception {
    MapController map = new MapController(new WorldSpec(4, 4, 0L));
    assertEquals(0, map.stepIndex());
    Object lock = sessionLock(map.session());
    synchronized (lock) {
      long start = System.nanoTime();
      assertEquals(0, map.stepIndex());
      assertEquals("Step 0", map.statusText());
      long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
      assertTrue(elapsedMs < 200, "status blocked on physics lock: " + elapsedMs + "ms");
    }
  }

  private static Object sessionLock(ProductSession session) throws Exception {
    Field field = ProductSession.class.getDeclaredField("lock");
    field.setAccessible(true);
    return field.get(session);
  }
}
