/*
 * File: ui/src/main/java/com/aethelgard/ui/ProductApp.java
 * Purpose: Swing entry point for the Aethelgard map window
 * Audience: Operators / agents
 * Update when: Launch wiring changes
 */

package com.aethelgard.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Launches {@link MapFrame} on the EDT and blocks until the window closes.
 *
 * <p>Generation runs on a single worker thread (not the EDT). Blocking is required for {@code mvn
 * exec:java}.
 */
public final class ProductApp {

  private ProductApp() {}

  public static void main(String[] args) throws Exception {
    CountDownLatch closed = new CountDownLatch(1);
    ExecutorService compute =
        Executors.newSingleThreadExecutor(
            r -> {
              Thread t = new Thread(r, "aethelgard-map");
              t.setDaemon(true);
              return t;
            });

    SwingUtilities.invokeLater(
        () -> {
          MapController controller = MapController.view(compute, new SwingPlayScheduler());
          MapFrame frame = new MapFrame(controller);
          frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          frame.addWindowListener(
              new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                  compute.shutdownNow();
                  closed.countDown();
                }
              });
          frame.setVisible(true);
          frame.toFront();
        });

    closed.await();
  }
}
