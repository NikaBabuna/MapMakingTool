/*
 * File: engine/src/test/java/com/aethelgard/engine/merge/FieldMergeTypeTest.java
 * Purpose: F-011 witnesses — Object carrier + pluggable FieldMergeType
 * Audience: Maven Surefire
 * Update when: F-011 FRs change
 */

package com.aethelgard.engine.merge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.system.EngineSystem;
import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SystemConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FieldMergeTypeTest {

  @Test
  @DisplayName("FR-1/FR-2: FieldSchema stores FieldMergeType; defaults are FieldType")
  void schemaHoldsMergeTypes() {
    FieldSchema schema = FieldSchema.of("n", FieldType.INCREMENT);
    assertInstanceOf(FieldMergeType.class, schema.typeOf("n"));
    assertEquals(FieldType.INCREMENT, schema.typeOf("n"));
  }

  @Test
  @DisplayName("FR-3: default Static/Increment/Constant/Destructive unchanged for Long")
  void defaultTypesPreserveLongRules() {
    CategoryTree tree = CategoryTree.of("world");
    Engine increment =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("n", 10L)),
            new EngineSetup(
                tree,
                List.of(),
                List.of(sys("aardvark", tree, "n", 3L), sys("zebra", tree, "n", 5L)),
                FieldSchema.of("n", FieldType.INCREMENT),
                EngineDiagnostics.noop()));
    assertEquals(18L, increment.settled().field("n"));

    Engine constant =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("locked", 7L)),
            new EngineSetup(
                tree,
                List.of(),
                List.of(sys("w", tree, "locked", 99L)),
                FieldSchema.of("locked", FieldType.CONSTANT),
                EngineDiagnostics.noop()));
    assertEquals(7L, constant.settled().field("locked"));
  }

  @Test
  @DisplayName("FR-4: custom FieldMergeType applied without enum change")
  void customMergeTypeWins() {
    CategoryTree tree = CategoryTree.of("world");
    FieldMergeType preferZebra =
        (standing, writers) ->
            writers.stream()
                .filter(w -> w.systemId().equals("zebra"))
                .findFirst()
                .orElseGet(() -> TypedMerge.pickOne(writers))
                .value();

    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("token", 0L)),
            new EngineSetup(
                tree,
                List.of(),
                List.of(sys("aardvark", tree, "token", 1L), sys("zebra", tree, "token", 2L)),
                FieldSchema.of("token", preferZebra),
                EngineDiagnostics.noop()));

    // Default Static would pick aardvark (1); custom prefers zebra (2)
    assertEquals(2L, engine.settled().field("token"));
  }

  @Test
  @DisplayName("FR-5: non-Long Object values survive Static merge")
  void nonLongStaticValue() {
    CategoryTree tree = CategoryTree.of("world");
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("label", "seed")),
            new EngineSetup(
                tree,
                List.of(),
                List.of(sys("w", tree, "label", "formed")),
                FieldSchema.of("label", FieldType.STATIC),
                EngineDiagnostics.noop()));

    assertEquals("formed", engine.settled().field("label"));
  }

  @Test
  @DisplayName("FR-6: Increment rejects non-Long writes")
  void incrementRejectsNonLong() {
    CategoryTree tree = CategoryTree.of("world");
    assertThrows(
        IllegalArgumentException.class,
        () ->
            Engine.create(
                new EngineConfig(0L, List.of("world"), Map.of("n", 0L)),
                new EngineSetup(
                    tree,
                    List.of(),
                    List.of(sys("w", tree, "n", "nope")),
                    FieldSchema.of("n", FieldType.INCREMENT),
                    EngineDiagnostics.noop())));
  }

  @Test
  @DisplayName("FR-7: engine has no UI/CLI/product compile deps")
  void noUiCliProductCompileDeps() throws Exception {
    String enginePom = Files.readString(findRepoRoot().resolve("engine/pom.xml"));
    String withoutTestDeps = enginePom.replaceAll("(?s)<scope>test</scope>.*?</dependency>", "");
    assertTrue(!withoutTestDeps.contains("<artifactId>cli</artifactId>"));
    assertTrue(!withoutTestDeps.contains("<artifactId>ui</artifactId>"));
    assertTrue(!withoutTestDeps.contains("<artifactId>product</artifactId>"));
  }

  @Test
  @DisplayName("FR-8: docs record FieldMergeType and Object carrier")
  void docsRecordExtensionPoints() throws Exception {
    Path root = findRepoRoot();
    String arch = Files.readString(root.resolve("docs/engine/architecture.md"));
    String merge = Files.readString(root.resolve("docs/engine/specs/merge-types.md"));
    assertTrue(arch.contains("FieldMergeType"));
    assertTrue(merge.contains("FieldMergeType"));
    assertTrue(merge.contains("Object") || merge.contains("custom"));
  }

  private static EngineSystem sys(String id, CategoryTree tree, String field, Object value) {
    SubSystem sub = new SubSystem() {
      @Override
      public String id() {
        return "w";
      }

      @Override
      public Set<String> writeRanges() {
        return Set.of(field);
      }

      @Override
      public void execute(com.aethelgard.engine.system.SubSystemIo io) {
        io.write(field, value);
      }
    };
    return new EngineSystem(new SystemConfig(id, tree.get("world"), List.of(sub), null));
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
