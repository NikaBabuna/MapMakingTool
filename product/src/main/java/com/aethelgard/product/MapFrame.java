/*
 * File: product/src/main/java/com/aethelgard/product/MapFrame.java
 * Purpose: Product Swing shell — colored elevation map + Advance (not used in tests)
 * Audience: Interactive operators
 * Update when: Map window layout changes
 */

package com.aethelgard.product;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/** Interactive window. Must not be constructed from automated tests (headless rule). */
public final class MapFrame extends JFrame {

  public MapFrame(MapController controller) {
    super("Aethelgard");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setResizable(false);
    setLocationByPlatform(true);

    JLabel map = new JLabel();
    map.setHorizontalAlignment(SwingConstants.CENTER);
    JLabel status = new JLabel(controller.statusText(), SwingConstants.CENTER);
    JButton advance = new JButton("Advance");
    advance.addActionListener(e -> controller.advanceAsync());

    Runnable apply =
        () -> {
          map.setIcon(new ImageIcon(toImage(controller.raster())));
          status.setText(controller.statusText());
          advance.setEnabled(!controller.busy());
        };
    controller.onChanged(
        () -> {
          if (SwingUtilities.isEventDispatchThread()) {
            apply.run();
          } else {
            SwingUtilities.invokeLater(apply);
          }
        });

    add(status, BorderLayout.NORTH);
    add(map, BorderLayout.CENTER);
    add(advance, BorderLayout.SOUTH);
    pack();
  }

  static BufferedImage toImage(ElevationRaster raster) {
    BufferedImage image =
        new BufferedImage(raster.width(), raster.height(), BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < raster.height(); y++) {
      for (int x = 0; x < raster.width(); x++) {
        image.setRGB(x, y, raster.rgb(x, y));
      }
    }
    return image;
  }
}
