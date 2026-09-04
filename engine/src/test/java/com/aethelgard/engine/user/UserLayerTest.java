/*
 * File: engine/src/test/java/com/aethelgard/engine/user/UserLayerTest.java
 * Purpose: F-006 witness — User Input, Input View, User View
 * Audience: Agents / CI
 * Update when: F-006 FRs change
 */

package com.aethelgard.engine.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.pool.Pool;
import com.aethelgard.engine.pool.PoolSnapshot;
import com.aethelgard.engine.system.EngineSystem;
import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import com.aethelgard.engine.system.SystemConfig;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserLayerTest {

  @Test
  @DisplayName("FR-1: Persistent latches until consume; Non-persistent tracks hold")
  void persistentAndNonPersistentRegister() {
    UserInput input = new UserInput();
    input.register("hold", InputKind.NON_PERSISTENT);
    input.register("once", InputKind.PERSISTENT);

    input.press("hold");
    assertTrue(input.isHeld("hold"));
    input.release("hold");
    assertFalse(input.isHeld("hold"));

    input.press("once");
    input.release("once");
    assertFalse(input.isHeld("once"));
    assertTrue(input.isLatched("once"));
    input.consume("once");
    assertFalse(input.isLatched("once"));
  }

  @Test
  @DisplayName("FR-2: Pool update samples staged Input View once per Step")
  void poolSamplesInputView() {
    UserInput input = new UserInput().register(Pool.NUDGE_ACTION, InputKind.NON_PERSISTENT);
    input.press(Pool.NUDGE_ACTION);

    Engine engine =
        Engine.create(
            new EngineConfig(0L),
            new EngineSetup(
                CategoryTree.empty(),
                List.of(),
                List.of(),
                FieldSchema.empty(),
                EngineDiagnostics.noop(),
                input,
                UserView.noop()));

    assertTrue(engine.lastInputView().isActive(Pool.NUDGE_ACTION));
    // Step 0: +1 heartbeat +100 nudge
    assertEquals(101L, engine.settled().value());
  }

  @Test
  @DisplayName("FR-3: Non-persistent needs hold at stage; Persistent survives release until consume")
  void persistenceSemanticsInInputView() {
    UserInput input = new UserInput();
    input.register("tap", InputKind.NON_PERSISTENT);
    input.register("pulse", InputKind.PERSISTENT);

    input.press("tap");
    input.release("tap");
    assertFalse(input.stage().isActive("tap"));

    input.press("pulse");
    input.release("pulse");
    InputView view = input.stage();
    assertTrue(view.isActive("pulse"));
    input.consumePersistentPresentIn(view);
    assertFalse(input.isLatched("pulse"));
    assertFalse(input.stage().isActive("pulse"));
  }

  @Test
  @DisplayName("FR-4: User View sees settled Pool only (after merge), once per Step")
  void userViewSeesSettledOnly() {
    CategoryTree tree = CategoryTree.of("world");
    EngineSystem writer =
        new EngineSystem(
            new SystemConfig(
                "sys",
                tree.get("world"),
                List.of(
                    new SubSystem() {
                      @Override
                      public String id() {
                        return "w";
                      }

                      @Override
                      public Set<String> writeRanges() {
                        return Set.of("mark");
                      }

                      @Override
                      public void execute(SubSystemIo io) {
                        io.write("mark", 42L);
                      }
                    }),
                null));

    RecordingUserView view = new RecordingUserView();
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("mark", 0L)),
            new EngineSetup(
                tree,
                List.of(),
                List.of(writer),
                FieldSchema.of("mark", FieldType.STATIC),
                EngineDiagnostics.noop(),
                new UserInput(),
                settled -> {
                  view.onSettled(settled);
                  // settled must already include merge
                  assertEquals(42L, settled.field("mark"));
                }));

    assertEquals(1, view.frames().size());
    assertEquals(engine.settled(), view.lastFrame());
    assertEquals(42L, view.lastFrame().field("mark"));
  }

  @Test
  @DisplayName("FR-5: defaults unchanged; recording view works without UI toolkits")
  void defaultsAndRecordingWithoutUi() {
    Engine plain = Engine.create(new EngineConfig(5L));
    assertEquals(6L, plain.settled().value());
    assertTrue(plain.lastInputView().active().isEmpty());

    RecordingUserView recording = new RecordingUserView();
    UserInput input = new UserInput().register(Pool.NUDGE_ACTION, InputKind.PERSISTENT);
    input.press(Pool.NUDGE_ACTION);
    input.release(Pool.NUDGE_ACTION);

    Engine engine =
        Engine.create(
            new EngineConfig(0L),
            new EngineSetup(
                CategoryTree.empty(),
                List.of(),
                List.of(),
                FieldSchema.empty(),
                EngineDiagnostics.noop(),
                input,
                recording));

    assertEquals(1, recording.frames().size());
    assertEquals(101L, recording.lastFrame().value());
    // persistent consumed after Step 0 sample
    engine.advance();
    assertEquals(2, recording.frames().size());
    assertEquals(102L, recording.lastFrame().value()); // no second nudge
  }

  @Test
  @DisplayName("FR-6: engine has no UI/CLI/product compile deps")
  void noUiCliProductCompileDeps() throws Exception {
    var root = findRepoRoot();
    String enginePom = java.nio.file.Files.readString(root.resolve("engine/pom.xml"));
    assertTrue(!enginePom.contains("<artifactId>ui</artifactId>"));
    assertTrue(!enginePom.contains("<artifactId>cli</artifactId>"));
    assertTrue(!enginePom.contains("<artifactId>product</artifactId>"));
    assertTrue(!enginePom.toLowerCase().contains("swing"));
  }

  private static java.nio.file.Path findRepoRoot() {
    var dir = java.nio.file.Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (java.nio.file.Files.isRegularFile(cursor.resolve("pom.xml"))
          && java.nio.file.Files.isDirectory(cursor.resolve("engine"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found");
  }
}
