/*
 * File: ui/src/main/java/com/aethelgard/ui/SkeletonFrame.java
 * Purpose: Minimal Swing shell — Advance button + settled text (not used in tests)
 * Audience: Interactive operators
 * Update when: Skeleton window layout changes
 */

package com.aethelgard.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/** Interactive window. Must not be constructed from automated tests (headless rule). */
public final class SkeletonFrame extends JFrame {

  public SkeletonFrame(UiController controller) {
    super("Aethelgard — Engine Skeleton");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(420, 280);
    setLocationByPlatform(true);

    JTextArea settled = new JTextArea();
    settled.setEditable(false);
    settled.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    settled.setText(controller.settledText());

    controller.onSettledTextChanged(
        text -> SwingUtilities.invokeLater(() -> settled.setText(text)));

    JButton advance = new JButton("Advance");
    advance.addActionListener(e -> controller.advance());

    add(new JScrollPane(settled), BorderLayout.CENTER);
    add(advance, BorderLayout.SOUTH);
  }
}
