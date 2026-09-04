/*
 * File: ui/src/main/java/com/aethelgard/ui/SkeletonApp.java
 * Purpose: Swing entry point for the skeleton UI
 * Audience: Operators / agents
 * Update when: Launch wiring changes
 */

package com.aethelgard.ui;

import javax.swing.SwingUtilities;

/** Launches {@link SkeletonFrame} on the EDT. */
public final class SkeletonApp {

  private SkeletonApp() {}

  public static void main(String[] args) {
    long initial = 0L;
    if (args != null && args.length >= 2 && "--initial".equals(args[0])) {
      initial = Long.parseLong(args[1]);
    }
    long seed = initial;
    SwingUtilities.invokeLater(
        () -> {
          UiController controller = new UiController(seed);
          SkeletonFrame frame = new SkeletonFrame(controller);
          frame.setVisible(true);
        });
  }
}
