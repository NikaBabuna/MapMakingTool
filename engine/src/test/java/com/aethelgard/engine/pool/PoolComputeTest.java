/*
 * File: engine/src/test/java/com/aethelgard/engine/pool/PoolComputeTest.java
 * Purpose: F-010 witnesses — pluggable Pool compute
 * Audience: Maven Surefire
 * Update when: F-010 FRs or compute API change
 */

package com.aethelgard.engine.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.merge.FieldSchema;
import com.aethelgard.engine.merge.FieldType;
import com.aethelgard.engine.user.InputKind;
import com.aethelgard.engine.user.UserInput;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PoolComputeTest {

  @Test
  @DisplayName("FR-1: EngineSetup accepts a custom PoolCompute")
  void setupAcceptsCustomCompute() {
    PoolCompute custom = ctx -> ctx.setValue(42L);
    EngineSetup setup =
        new EngineSetup(
            CategoryTree.empty(),
            List.of(),
            List.of(),
            FieldSchema.empty(),
            EngineDiagnostics.noop(),
            new UserInput(),
            null,
            custom);
    assertEquals(custom, setup.poolCompute());
  }

  @Test
  @DisplayName("FR-2: default compute preserves heartbeat and nudge")
  void defaultPreservesHeartbeatAndNudge() {
    assertEquals(SkeletonPoolCompute.INSTANCE, EngineSetup.defaults().poolCompute());

    Engine plain = Engine.create(new EngineConfig(10L));
    assertEquals(11L, plain.settled().value());
    plain.advance();
    assertEquals(12L, plain.settled().value());

    UserInput input = new UserInput().register(SkeletonPoolCompute.NUDGE_ACTION, InputKind.NON_PERSISTENT);
    input.press(SkeletonPoolCompute.NUDGE_ACTION);
    Engine nudged =
        Engine.create(
            new EngineConfig(0L),
            new EngineSetup(
                null, List.of(), List.of(), null, EngineDiagnostics.noop(), input, null));
    assertEquals(101L, nudged.settled().value());
  }

  @Test
  @DisplayName("FR-3: custom compute replaces heartbeat; can write declared fields")
  void customComputeReplacesHeartbeatAndWritesFields() {
    FieldSchema schema = FieldSchema.of("marker", FieldType.STATIC);
    PoolCompute custom =
        ctx -> {
          ctx.setValue(999L);
          ctx.setField("marker", 7L);
          // intentionally no emitScripted / no +1
        };

    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of(), Map.of("marker", 0L)),
            new EngineSetup(
                CategoryTree.empty(),
                List.of(),
                List.of(),
                schema,
                EngineDiagnostics.noop(),
                new UserInput(),
                null,
                custom));

    assertEquals(999L, engine.settled().value());
    assertEquals(7L, engine.settled().field("marker"));
    engine.advance();
    assertEquals(999L, engine.settled().value());
    assertEquals(7L, engine.settled().field("marker"));
  }

  @Test
  @DisplayName("FR-3: Pool source no longer hardcodes heartbeat/nudge rules")
  void poolSourceDelegatesCompute() throws Exception {
    Path poolSource =
        findRepoRoot()
            .resolve("engine/src/main/java/com/aethelgard/engine/pool/Pool.java");
    String src = Files.readString(poolSource);
    assertFalse(src.contains("value = value + 1"), "heartbeat must not be hardcoded in Pool");
    assertFalse(src.contains("value + 100"), "nudge must not be hardcoded in Pool");
    assertTrue(src.contains("compute.compute("), "Pool.update must delegate to PoolCompute");
  }

  @Test
  @DisplayName("FR-4: custom compute sees Input View and may emit scripted events")
  void computeSeesInputViewAndCanEmit() {
    CategoryTree tree = CategoryTree.of("pulse");
    boolean[] sawPulse = {false};
    PoolCompute custom =
        ctx -> {
          assertTrue(ctx.inputView().isActive("ping"));
          ctx.emitScripted();
          sawPulse[0] = !ctx.scriptedEmissions().isEmpty();
        };

    UserInput input = new UserInput().register("ping", InputKind.PERSISTENT);
    input.press("ping");

    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("pulse")),
            new EngineSetup(
                tree,
                List.of(),
                List.of(),
                FieldSchema.empty(),
                EngineDiagnostics.noop(),
                input,
                null,
                custom));

    assertTrue(sawPulse[0]);
    assertTrue(engine.lastInputView().isActive("ping"));
    // scripted pulse emitted with no claimers → unmatched
    assertEquals(1, engine.lastClaimResult().unmatched().size());
  }

  @Test
  @DisplayName("FR-5: engine has no UI/CLI/product compile deps")
  void noUiCliProductCompileDeps() throws Exception {
    String enginePom = Files.readString(findRepoRoot().resolve("engine/pom.xml"));
    String withoutTestDeps = enginePom.replaceAll("(?s)<scope>test</scope>.*?</dependency>", "");
    assertTrue(!withoutTestDeps.contains("<artifactId>cli</artifactId>"));
    assertTrue(!withoutTestDeps.contains("<artifactId>ui</artifactId>"));
    assertTrue(!withoutTestDeps.contains("<artifactId>product</artifactId>"));
  }

  @Test
  @DisplayName("FR-6: architecture docs mention PoolCompute")
  void architectureDocumentsExtensionPoint() throws Exception {
    String arch =
        Files.readString(findRepoRoot().resolve("docs/engine/architecture.md"));
    assertTrue(arch.contains("PoolCompute"), "architecture.md must record PoolCompute");
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath();
    for (int i = 0; i < 8; i++) {
      if (Files.exists(dir.resolve("pom.xml")) && Files.exists(dir.resolve("engine"))) {
        return dir;
      }
      dir = dir.getParent();
    }
    throw new IllegalStateException("repo root not found");
  }
}
