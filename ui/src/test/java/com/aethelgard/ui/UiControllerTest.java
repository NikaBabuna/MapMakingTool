/*
 * File: ui/src/test/java/com/aethelgard/ui/UiControllerTest.java
 * Purpose: F-008 witness — headless UI controller (no JFrame)
 * Audience: Agents / CI
 * Update when: F-008 FRs change
 */

package com.aethelgard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UiControllerTest {

  @Test
  @DisplayName("FR-1: parent lists ui; ui depends on engine; engine has no ui/Swing deps")
  void moduleWiringOneWay() throws Exception {
    Path root = findRepoRoot();
    String parent = Files.readString(root.resolve("pom.xml"));
    assertTrue(parent.contains("<module>ui</module>"));

    String uiPom = Files.readString(root.resolve("ui/pom.xml"));
    assertTrue(uiPom.contains("<artifactId>ui</artifactId>"));
    assertTrue(uiPom.contains("<artifactId>engine</artifactId>"));

    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    assertFalse(enginePom.contains("<artifactId>ui</artifactId>"));
    assertFalse(enginePom.toLowerCase().contains("swing"));
  }

  @Test
  @DisplayName("FR-2: controller creates run, advances, exposes settled state")
  void advanceAndExposeSettled() {
    UiController ui = new UiController(5L);
    assertEquals(0, ui.stepIndex());
    assertEquals(6L, ui.settled().value());
    assertTrue(ui.settledText().contains("stepIndex=0"));
    assertTrue(ui.settledText().contains("value=6"));

    ui.advance();
    assertEquals(1, ui.stepIndex());
    assertEquals(7L, ui.settled().value());
    assertTrue(ui.settledText().contains("stepIndex=1"));

    ui.advance(2);
    assertEquals(3, ui.stepIndex());
    assertEquals(9L, ui.settled().value());
  }

  @Test
  @DisplayName("FR-3: settled text updates via User View on each completed Step")
  void settledViaUserView() {
    List<String> updates = new ArrayList<>();
    UiController ui = new UiController(0L);
    ui.onSettledTextChanged(updates::add);
    // listener gets current text immediately
    assertFalse(updates.isEmpty());
    int afterWire = updates.size();

    ui.advance();
    assertTrue(updates.size() > afterWire);
    assertTrue(updates.getLast().contains("stepIndex=1"));
    assertTrue(updates.getLast().contains("updateCount=2"));
  }

  @Test
  @DisplayName("FR-4: UiController has no Swing/AWT; tests exercise it headlessly")
  void controllerHasNoSwing() throws Exception {
    Path root = findRepoRoot();
    String controller =
        Files.readString(root.resolve("ui/src/main/java/com/aethelgard/ui/UiController.java"));
    assertFalse(controller.contains("javax.swing"));
    assertFalse(controller.contains("java.awt"));
    UiController ui = new UiController(0L);
    ui.advance(1);
    assertEquals(1, ui.stepIndex());
  }

  @Test
  @DisplayName("FR-5: Swing shell source exists (SkeletonApp / SkeletonFrame)")
  void swingShellExists() throws Exception {
    Path root = findRepoRoot();
    assertTrue(Files.isRegularFile(root.resolve("ui/src/main/java/com/aethelgard/ui/SkeletonApp.java")));
    assertTrue(
        Files.isRegularFile(root.resolve("ui/src/main/java/com/aethelgard/ui/SkeletonFrame.java")));
    String frame =
        Files.readString(root.resolve("ui/src/main/java/com/aethelgard/ui/SkeletonFrame.java"));
    assertTrue(frame.contains("JFrame"));
    assertTrue(frame.contains("Advance"));
  }

  @Test
  @DisplayName("FR-6: ui README and docs mention the module; G-001 claim closed in goals file")
  void docsAndGoalClosure() throws Exception {
    Path root = findRepoRoot();
    assertTrue(Files.isRegularFile(root.resolve("ui/README.md")));
    String nav = Files.readString(root.resolve("docs/navigation.md"));
    assertTrue(nav.contains("../ui/") || nav.contains("ui/README"));
    String goal =
        Files.readString(root.resolve("docs/project/goals/G-001-engine-skeleton.md"));
    assertTrue(goal.contains("[x] Basic UI can advance/view Steps"));
    assertTrue(goal.contains("**Status:** done") || goal.contains("**Status:** `done`"));
  }

  private static Path findRepoRoot() {
    var dir = Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("engine"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found");
  }
}
