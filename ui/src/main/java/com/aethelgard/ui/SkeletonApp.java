/*
 * File: ui/src/main/java/com/aethelgard/ui/SkeletonApp.java
 * Purpose: Swing entry point for the skeleton UI
 * Audience: Operators / agents
 * Update when: Launch wiring changes
 */

package com.aethelgard.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Launches {@link SkeletonFrame} on the EDT and blocks until the window closes.
 *
 * <p>Blocking is required for {@code mvn exec:java}, which otherwise returns from {@code main}
 * and tears down the JVM before the frame stays open.
 */
public final class SkeletonApp {

  private SkeletonApp() {}

  public static void main(String[] args) throws Exception {
    long initial = 0L;
    if (args != null && args.length >= 2 && "--initial".equals(args[0])) {
      initial = Long.parseLong(args[1]);
    }
    long seed = initial;
    CountDownLatch closed = new CountDownLatch(1);

    SwingUtilities.invokeLater(
        () -> {
          UiController controller = new UiController(seed);
          SkeletonFrame frame = new SkeletonFrame(controller);
          frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          frame.addWindowListener(
              new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                  closed.countDown();
                }
              });
          frame.setVisible(true);
          frame.toFront();
        });

    closed.await();
  }
}
