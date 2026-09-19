/*
 * File: ui/src/main/java/com/aethelgard/ui/ExecutorPlayScheduler.java
 * Purpose: Play ticks via ScheduledExecutorService (HTTP host / non-Swing)
 * Audience: MapHost / tests
 * Update when: Play scheduling contract changes
 */

package com.aethelgard.ui;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** {@link PlayScheduler} backed by a daemon single-thread scheduler. */
public final class ExecutorPlayScheduler implements PlayScheduler, AutoCloseable {

  private final ScheduledExecutorService scheduler;
  private final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();

  public ExecutorPlayScheduler() {
    this.scheduler =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              Thread t = new Thread(r, "map-play");
              t.setDaemon(true);
              return t;
            });
  }

  @Override
  public void start(int periodMillis, Runnable tick) {
    Objects.requireNonNull(tick, "tick");
    stop();
    int period = Math.max(1, periodMillis);
    ScheduledFuture<?> started =
        scheduler.scheduleAtFixedRate(tick, period, period, TimeUnit.MILLISECONDS);
    future.set(started);
  }

  @Override
  public void stop() {
    ScheduledFuture<?> running = future.getAndSet(null);
    if (running != null) {
      running.cancel(false);
    }
  }

  @Override
  public void close() {
    stop();
    scheduler.shutdownNow();
  }
}
