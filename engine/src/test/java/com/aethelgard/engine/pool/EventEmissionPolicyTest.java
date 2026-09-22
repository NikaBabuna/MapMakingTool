/*
 * File: engine/src/test/java/com/aethelgard/engine/pool/EventEmissionPolicyTest.java
 * Purpose: F-012 witnesses — pluggable EventEmissionPolicy + host closure docs
 * Audience: Maven Surefire
 * Update when: F-012 FRs change
 */

package com.aethelgard.engine.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventEmissionPolicyTest {

  @Test
  @DisplayName("FR-1: EngineSetup accepts a custom EventEmissionPolicy")
  void setupAcceptsCustomPolicy() {
    EventEmissionPolicy custom = ctx -> {};
    EngineSetup setup =
        new EngineSetup(
            CategoryTree.empty(),
            List.of(),
            List.of(),
            FieldSchema.empty(),
            EngineDiagnostics.noop(),
            new UserInput(),
            null,
            null,
            custom);
    assertEquals(custom, setup.eventEmissionPolicy());
  }

  @Test
  @DisplayName("FR-2: default policy is scripted paths; skeleton still emits them")
  void defaultScriptedEmissionPreserved() {
    assertEquals(
        ScriptedEventEmissionPolicy.INSTANCE, EngineSetup.defaults().eventEmissionPolicy());

    CategoryTree tree = CategoryTree.of("world");
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world")),
            new EngineSetup(tree, List.of(), EngineDiagnostics.noop()));
    assertEquals(1, engine.lastClaimResult().unmatched().size());
    assertEquals("world", engine.lastClaimResult().unmatched().get(0).category().path());
  }

  @Test
  @DisplayName("FR-3: custom policy emits from Pool field state")
  void customPolicyUsesFieldState() {
    CategoryTree tree = CategoryTree.of("alert");
    EventEmissionPolicy armedOnly =
        ctx -> {
          if (ctx.fieldOrZero("armed") > 0) {
            ctx.emitPath("alert");
          }
        };

    Engine armed =
        Engine.create(
            new EngineConfig(0L, List.of(), Map.of("armed", 1L)),
            new EngineSetup(
                tree,
                List.of(),
                List.of(),
                FieldSchema.of("armed", FieldType.STATIC),
                EngineDiagnostics.noop(),
                new UserInput(),
                null,
                null,
                armedOnly));
    assertEquals(1, armed.lastClaimResult().unmatched().size());
    assertEquals("alert", armed.lastClaimResult().unmatched().get(0).category().path());

    Engine quiet =
        Engine.create(
            new EngineConfig(0L, List.of(), Map.of("armed", 0L)),
            new EngineSetup(
                tree,
                List.of(),
                List.of(),
                FieldSchema.of("armed", FieldType.STATIC),
                EngineDiagnostics.noop(),
                new UserInput(),
                null,
                null,
                armedOnly));
    assertEquals(0, quiet.lastClaimResult().unmatched().size());
  }

  @Test
  @DisplayName("FR-3: custom policy can read Input View")
  void customPolicyUsesInputView() {
    CategoryTree tree = CategoryTree.of("ping");
    EventEmissionPolicy onPing =
        ctx -> {
          if (ctx.inputView().isActive("ping")) {
            ctx.emitPath("ping");
          }
        };

    UserInput input = new UserInput().register("ping", InputKind.NON_PERSISTENT);
    input.press("ping");

    Engine engine =
        Engine.create(
            new EngineConfig(0L),
            new EngineSetup(
                tree,
                List.of(),
                List.of(),
                FieldSchema.empty(),
                EngineDiagnostics.noop(),
                input,
                null,
                null,
                onPing));
    assertEquals(1, engine.lastClaimResult().unmatched().size());
  }

  @Test
  @DisplayName("FR-4: SkeletonPoolCompute uses wired policy; custom compute may ignore it")
  void skeletonUsesPolicyCustomComputeMayIgnore() {
    AtomicBoolean policyCalled = new AtomicBoolean(false);
    EventEmissionPolicy tracking =
        ctx -> {
          policyCalled.set(true);
          ctx.emitScripted();
        };

    CategoryTree tree = CategoryTree.of("world");
    Engine withSkeleton =
        Engine.create(
            new EngineConfig(0L, List.of("world")),
            new EngineSetup(
                tree,
                List.of(),
                List.of(),
                FieldSchema.empty(),
                EngineDiagnostics.noop(),
                new UserInput(),
                null,
                null,
                tracking));
    assertTrue(policyCalled.get());
    assertEquals(1, withSkeleton.lastClaimResult().unmatched().size());

    policyCalled.set(false);
    PoolCompute ignorePolicy = ctx -> ctx.setValue(42L); // no applyEmissionPolicy
    Engine custom =
        Engine.create(
            new EngineConfig(0L, List.of("world")),
            new EngineSetup(
                tree,
                List.of(),
                List.of(),
                FieldSchema.empty(),
                EngineDiagnostics.noop(),
                new UserInput(),
                null,
                ignorePolicy,
                tracking));
    assertEquals(false, policyCalled.get());
    assertEquals(42L, custom.settled().value());
    assertEquals(0, custom.lastClaimResult().unmatched().size());
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
  @DisplayName("FR-6: docs record emission port and G-002 host readiness")
  void docsRecordHostPortsAndGoalDone() throws Exception {
    Path root = findRepoRoot();
    String arch = Files.readString(root.resolve("docs/architecture/host/README.md"));
    String goal =
        Files.readString(root.resolve("docs/paperwork/goals/G-002-engine-host-readiness.md"));
    String events = Files.readString(root.resolve("docs/architecture/host/events.md"));
    assertTrue(arch.contains("EventEmissionPolicy"));
    assertTrue(arch.contains("PoolCompute"));
    assertTrue(arch.contains("FieldMergeType"));
    assertTrue(events.contains("EventEmissionPolicy") || events.contains("emission policy"));
    assertTrue(goal.contains("**Status:** `done`") || goal.contains("**Status:** done"));
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
