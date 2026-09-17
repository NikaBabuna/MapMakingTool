/*
 * File: ui/src/main/java/com/aethelgard/ui/SwingPlayScheduler.java
 * Purpose: EDT timer for map Play ticks (interactive only)
 * Audience: ProductApp
 * Update when: Play timer wiring changes
 */

package com.aethelgard.ui;

import javax.swing.Timer;

/** {@link javax.swing.Timer} play loop. Must not be constructed from automated tests. */
public final class SwingPlayScheduler implements PlayScheduler {

  private Timer timer;

  @Override
  public void start(int periodMillis, Runnable tick) {
    stop();
    timer = new Timer(periodMillis, e -> tick.run());
    timer.start();
  }

  @Override
  public void stop() {
    if (timer != null) {
      timer.stop();
      timer = null;
    }
  }
}
