/*
 * File: ui/src/main/java/com/aethelgard/ui/PlayScheduler.java
 * Purpose: Injected repeating ticks for Play (no Swing in MapController)
 * Audience: MapController / ProductApp / tests
 * Update when: Play scheduling contract changes
 */

package com.aethelgard.ui;

/**
 * Starts and stops a repeating play tick. Production uses a Swing timer; tests inject a recorder
 * or {@link #idle()}.
 */
public interface PlayScheduler {

  void start(int periodMillis, Runnable tick);

  void stop();

  /** No-op scheduler — tests drive {@link MapController#playTick()} themselves. */
  static PlayScheduler idle() {
    return new PlayScheduler() {
      @Override
      public void start(int periodMillis, Runnable tick) {}

      @Override
      public void stop() {}
    };
  }
}
