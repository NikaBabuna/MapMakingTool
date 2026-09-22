/*
 * File: cli/src/test/java/com/aethelgard/cli/NounVerbCommandTest.java
 * Purpose: F-048 witness — noun/verb command language
 * Audience: Agents / CI
 * Update when: F-048 FRs change
 */

package com.aethelgard.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.ProductHost;
import com.aethelgard.product.ProductSession;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NounVerbCommandTest {

  @Test
  @DisplayName("FR-1: help catalog and topics")
  void helpCatalog() {
    ProductSession session = ProductSession.ofDefault();
    CliResult help = CommandDispatch.execute(session, "help");
    assertTrue(help.ok());
    assertTrue(help.output().contains("Nouns:"));
    assertTrue(help.output().contains("session"));
    CliResult topic = CommandDispatch.execute(session, "help pool");
    assertTrue(topic.ok());
    assertTrue(topic.output().contains("pool.<field>"));
  }

  @Test
  @DisplayName("FR-2: noun/verb session, pool, schema, systems, diag")
  void nounVerbCore() {
    ProductSession session = ProductSession.ofDefault();

    assertEquals(
        "step=0 width=8 height=8 seed=0",
        CommandDispatch.execute(session, "session get").output());
    assertEquals("step=2", CommandDispatch.execute(session, "session advance 2").output());

    CliResult fields = CommandDispatch.execute(session, "list pool");
    assertTrue(fields.ok());
    assertTrue(fields.output().contains("elevation"));
    assertTrue(fields.output().contains("plates"));
    assertTrue(fields.output().contains("boundaries"));

    CliResult plates = CommandDispatch.execute(session, "pool.plates get");
    assertTrue(plates.ok());
    assertTrue(plates.output().startsWith("plates type=Grid"));

    CliResult cell = CommandDispatch.execute(session, "pool.elevation get 0 0");
    assertTrue(cell.ok());
    // F-060 arc: this corner is continental thickness 17, so elevation is 9.
    assertEquals("elevation[0,0]=9", cell.output());

    CliResult schema = CommandDispatch.execute(session, "schema get");
    assertTrue(schema.ok());
    assertTrue(schema.output().contains("elevation=STATIC"));

    CliResult systems = CommandDispatch.execute(session, "list systems");
    assertTrue(systems.ok());
    assertTrue(systems.output().contains(ProductHost.TECTONICS_SYSTEM_ID));

    CliResult tectonics = CommandDispatch.execute(session, "systems.tectonics get");
    assertTrue(tectonics.ok());
    assertTrue(tectonics.output().contains("id=tectonics"));
    assertTrue(tectonics.output().contains("subsystems="));

    assertTrue(CommandDispatch.execute(session, "list diag").ok());
    assertTrue(CommandDispatch.execute(session, "diag get").output().contains("advance.wall"));
    assertEquals(
        "phase.apply enabled=false",
        CommandDispatch.execute(session, "diag.phase.apply off").output());
  }

  @Test
  @DisplayName("FR-3/FR-4: aliases preserve old outputs; product has no CommandDispatch")
  void aliasesAndIsolation() throws Exception {
    ProductSession session = ProductSession.ofDefault();
    assertEquals(
        "step=0 width=8 height=8 seed=0", CommandDispatch.execute(session, "status").output());
    assertEquals("step=1", CommandDispatch.execute(session, "advance").output());

    Path root = findRepoRoot();
    String productPom = Files.readString(root.resolve("product/pom.xml"));
    assertFalse(productPom.contains("<artifactId>cli</artifactId>"));
    String sessionSrc =
        Files.readString(
            root.resolve("product/src/main/java/com/aethelgard/product/ProductSession.java"));
    assertFalse(sessionSrc.contains("CommandDispatch"));
  }

  @Test
  @DisplayName("FR-5: docs noun/verb; Engine exposes read ports only")
  void docsAndEnginePorts() throws Exception {
    Path root = findRepoRoot();
    String arch = Files.readString(root.resolve("docs/architecture/studio/host.md"));
    assertTrue(arch.contains("F-048") || arch.toLowerCase().contains("noun"));
    String adr = Files.readString(root.resolve("docs/paperwork/decisions/ADR-010-product-adapters.md"));
    assertTrue(adr.contains("noun") || adr.contains("F-048"));

    String engine =
        Files.readString(
            root.resolve("engine/src/main/java/com/aethelgard/engine/pool/Engine.java"));
    assertTrue(engine.contains("public List<EngineSystem> systems()"));
    assertTrue(engine.contains("public FieldSchema fieldSchema()"));
  }

  private static Path findRepoRoot() throws Exception {
    Path dir = Path.of("").toAbsolutePath().normalize();
    for (Path cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("cli"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new Exception("repo root not found");
  }
}
