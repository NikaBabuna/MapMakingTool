/*
 * File: ui/src/main/java/com/aethelgard/ui/MapFrame.java
 * Purpose: Dark tool window — layers, play, seed, inspect, legend
 * Audience: Interactive operators
 * Update when: Map window layout changes
 */

package com.aethelgard.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/** Interactive window. Must not be constructed from automated tests (headless rule). */
public final class MapFrame extends JFrame {

  static final int BACKGROUND_RGB = 0x12141A;

  private static final Color BG = new Color(BACKGROUND_RGB);
  private static final Color FG = new Color(0xE8E6E3);
  private static final Color PANEL = new Color(0x1A1C24);
  private static final Color MUTED = new Color(0x8A8680);

  public MapFrame(MapController controller) {
    super("Aethelgard");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setResizable(false);
    setLocationByPlatform(true);
    getContentPane().setBackground(BG);

    JLabel map = new JLabel();
    map.setHorizontalAlignment(SwingConstants.CENTER);
    map.setOpaque(true);
    map.setBackground(BG);

    JLabel status = new JLabel(controller.statusText(), SwingConstants.CENTER);
    styleLabel(status);
    status.setBorder(new EmptyBorder(8, 8, 4, 8));

    JComboBox<MapLayer> layerBox = new JComboBox<>(MapLayer.values());
    styleCombo(layerBox);

    JButton advance = new JButton("Advance");
    JButton play = new JButton("Play");
    JButton pause = new JButton("Pause");
    styleButton(advance);
    styleButton(play);
    styleButton(pause);

    JComboBox<MapSpeed> speedBox = new JComboBox<>(MapSpeed.values());
    speedBox.setModel(new DefaultComboBoxModel<>(MapSpeed.values()));
    styleCombo(speedBox);

    JTextField seedField = new JTextField(String.valueOf(controller.spec().seed()), 8);
    styleField(seedField);
    JButton newWorld = new JButton("New world");
    styleButton(newWorld);

    JLabel inspectTitle = new JLabel("Inspect");
    styleLabel(inspectTitle);
    inspectTitle.setFont(inspectTitle.getFont().deriveFont(Font.BOLD));
    JLabel inspectBody = new JLabel("Click the map");
    styleLabel(inspectBody);
    inspectBody.setForeground(MUTED);

    JLabel legendTitle = new JLabel("Legend");
    styleLabel(legendTitle);
    legendTitle.setFont(legendTitle.getFont().deriveFont(Font.BOLD));
    JPanel legendRows = new JPanel();
    legendRows.setLayout(new BoxLayout(legendRows, BoxLayout.Y_AXIS));
    legendRows.setBackground(PANEL);

    JLabel consoleTitle = new JLabel("Console");
    styleLabel(consoleTitle);
    consoleTitle.setFont(consoleTitle.getFont().deriveFont(Font.BOLD));
    JTextArea consoleOut = new JTextArea(6, 16);
    consoleOut.setEditable(false);
    consoleOut.setLineWrap(true);
    consoleOut.setWrapStyleWord(true);
    consoleOut.setBackground(BG);
    consoleOut.setForeground(FG);
    consoleOut.setCaretColor(FG);
    consoleOut.setBorder(new EmptyBorder(4, 4, 4, 4));
    JScrollPane consoleScroll = new JScrollPane(consoleOut);
    consoleScroll.setBorder(BorderFactory.createLineBorder(new Color(0x2A2D38)));
    consoleScroll.setAlignmentX(LEFT_ALIGNMENT);
    JTextField consoleIn = new JTextField();
    styleField(consoleIn);
    consoleIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    JButton consoleRun = new JButton("Run");
    styleButton(consoleRun);
    consoleRun.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

    final boolean[] syncing = {false};

    Runnable apply =
        () -> {
          syncing[0] = true;
          try {
            map.setIcon(new ImageIcon(toImage(controller.raster())));
            status.setText(controller.statusText());
            boolean idle = !controller.busy();
            advance.setEnabled(idle);
            newWorld.setEnabled(idle);
            seedField.setEnabled(idle);
            consoleRun.setEnabled(idle);
            consoleIn.setEnabled(idle);
            layerBox.setSelectedItem(controller.layer());
            speedBox.setSelectedItem(controller.speed());
            seedField.setText(String.valueOf(controller.spec().seed()));
            CellInspect cell = controller.inspected();
            if (cell == null) {
              inspectBody.setText("Click the map");
              inspectBody.setForeground(MUTED);
            } else {
              inspectBody.setText(
                  "<html>("
                      + cell.x()
                      + ", "
                      + cell.y()
                      + ")<br>elevation "
                      + cell.elevation()
                      + "<br>plate "
                      + cell.plateId()
                      + "<br>v=("
                      + cell.vx()
                      + ", "
                      + cell.vy()
                      + ")</html>");
              inspectBody.setForeground(FG);
            }
            legendRows.removeAll();
            for (LegendEntry entry : controller.legend()) {
              legendRows.add(legendRow(entry));
              legendRows.add(Box.createVerticalStrut(4));
            }
            legendRows.revalidate();
            legendRows.repaint();
          } finally {
            syncing[0] = false;
          }
        };

    controller.onChanged(
        () -> {
          if (SwingUtilities.isEventDispatchThread()) {
            apply.run();
          } else {
            SwingUtilities.invokeLater(apply);
          }
        });

    layerBox.addActionListener(
        e -> {
          if (!syncing[0]) {
            MapLayer selected = (MapLayer) layerBox.getSelectedItem();
            if (selected != null) {
              controller.setLayer(selected);
            }
          }
        });
    speedBox.addActionListener(
        e -> {
          if (!syncing[0]) {
            MapSpeed selected = (MapSpeed) speedBox.getSelectedItem();
            if (selected != null) {
              controller.setSpeed(selected);
            }
          }
        });
    advance.addActionListener(e -> controller.advanceAsync());
    play.addActionListener(e -> controller.play());
    pause.addActionListener(e -> controller.pause());
    newWorld.addActionListener(
        e -> {
          if (!syncing[0]) {
            controller.newWorld(parseSeed(seedField.getText(), controller.spec().seed()));
          }
        });

    Runnable submitConsole =
        () -> {
          if (syncing[0] || controller.busy()) {
            return;
          }
          String line = consoleIn.getText();
          var result = controller.runCommand(line);
          consoleOut.append("> " + line + "\n" + result.output());
          if (!result.output().endsWith("\n")) {
            consoleOut.append("\n");
          }
          consoleIn.setText("");
        };
    consoleRun.addActionListener(e -> submitConsole.run());
    consoleIn.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              submitConsole.run();
            }
          }
        });

    map.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (controller.busy() || map.getIcon() == null) {
              return;
            }
            int imgW = map.getIcon().getIconWidth();
            int imgH = map.getIcon().getIconHeight();
            int originX = (map.getWidth() - imgW) / 2;
            int originY = (map.getHeight() - imgH) / 2;
            int x = e.getX() - originX;
            int y = e.getY() - originY;
            if (x >= 0 && y >= 0 && x < imgW && y < imgH) {
              controller.inspect(x, y);
            }
          }
        });

    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    toolbar.setBackground(PANEL);
    toolbar.add(label("Layer"));
    toolbar.add(layerBox);
    toolbar.add(advance);
    toolbar.add(play);
    toolbar.add(pause);
    toolbar.add(label("Speed"));
    toolbar.add(speedBox);
    toolbar.add(label("Seed"));
    toolbar.add(seedField);
    toolbar.add(newWorld);

    JPanel north = new JPanel(new BorderLayout());
    north.setBackground(BG);
    north.add(status, BorderLayout.NORTH);
    north.add(toolbar, BorderLayout.CENTER);

    JPanel sidebar = new JPanel();
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
    sidebar.setBackground(PANEL);
    sidebar.setBorder(new EmptyBorder(12, 12, 12, 12));
    sidebar.setPreferredSize(new Dimension(220, 512));
    inspectTitle.setAlignmentX(LEFT_ALIGNMENT);
    inspectBody.setAlignmentX(LEFT_ALIGNMENT);
    legendTitle.setAlignmentX(LEFT_ALIGNMENT);
    legendRows.setAlignmentX(LEFT_ALIGNMENT);
    consoleTitle.setAlignmentX(LEFT_ALIGNMENT);
    consoleIn.setAlignmentX(LEFT_ALIGNMENT);
    consoleRun.setAlignmentX(LEFT_ALIGNMENT);
    sidebar.add(inspectTitle);
    sidebar.add(Box.createVerticalStrut(6));
    sidebar.add(inspectBody);
    sidebar.add(Box.createVerticalStrut(16));
    sidebar.add(legendTitle);
    sidebar.add(Box.createVerticalStrut(6));
    sidebar.add(legendRows);
    sidebar.add(Box.createVerticalStrut(16));
    sidebar.add(consoleTitle);
    sidebar.add(Box.createVerticalStrut(6));
    sidebar.add(consoleScroll);
    sidebar.add(Box.createVerticalStrut(6));
    sidebar.add(consoleIn);
    sidebar.add(Box.createVerticalStrut(4));
    sidebar.add(consoleRun);

    add(north, BorderLayout.NORTH);
    add(map, BorderLayout.CENTER);
    add(sidebar, BorderLayout.EAST);
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

  private static JPanel legendRow(LegendEntry entry) {
    JPanel row = new JPanel(new BorderLayout(8, 0));
    row.setBackground(PANEL);
    row.setMaximumSize(new Dimension(200, 18));
    JPanel swatch =
        new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(entry.rgb()));
            g.fillRect(0, 0, getWidth(), getHeight());
          }
        };
    swatch.setPreferredSize(new Dimension(16, 16));
    swatch.setOpaque(false);
    JLabel text = new JLabel(entry.label());
    styleLabel(text);
    text.setFont(text.getFont().deriveFont(11f));
    row.add(swatch, BorderLayout.WEST);
    row.add(text, BorderLayout.CENTER);
    return row;
  }

  private static JLabel label(String text) {
    JLabel label = new JLabel(text);
    styleLabel(label);
    return label;
  }

  private static void styleLabel(JLabel label) {
    label.setForeground(FG);
    label.setBackground(BG);
  }

  private static void styleButton(JButton button) {
    button.setBackground(PANEL);
    button.setForeground(FG);
    button.setOpaque(true);
    button.setFocusPainted(false);
    button.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x2A2D38)), new EmptyBorder(4, 10, 4, 10)));
  }

  private static void styleCombo(JComboBox<?> combo) {
    combo.setBackground(PANEL);
    combo.setForeground(FG);
  }

  private static void styleField(JTextField field) {
    field.setBackground(PANEL);
    field.setForeground(FG);
    field.setCaretColor(FG);
    field.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x2A2D38)), new EmptyBorder(4, 6, 4, 6)));
  }

  private static long parseSeed(String text, long fallback) {
    try {
      return Long.parseLong(text.trim());
    } catch (NumberFormatException ignored) {
      return fallback;
    }
  }
}
