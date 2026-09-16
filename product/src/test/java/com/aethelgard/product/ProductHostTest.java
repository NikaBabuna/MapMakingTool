/*
 * File: product/src/test/java/com/aethelgard/product/ProductHostTest.java
 * Purpose: F-013 witness — product module, one-way deps, ProductHost Engine create
 * Audience: Agents / CI
 * Update when: F-013 FRs change
 */

package com.aethelgard.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.pool.PoolSnapshot;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductHostTest {

  @Test
  @DisplayName("FR-1: parent lists product; artifact product; Java 21 from parent")
  void parentListsProductModule() throws Exception {
    Path root = findRepoRoot();
    String parent = Files.readString(root.resolve("pom.xml"));
    assertTrue(parent.contains("<packaging>pom</packaging>"));
    assertTrue(parent.contains("<module>product</module>"));
    assertTrue(parent.contains("<maven.compiler.release>21</maven.compiler.release>"));

    String productPom = Files.readString(root.resolve("product/pom.xml"));
    assertTrue(productPom.contains("<artifactId>product</artifactId>"));
    assertTrue(
        productPom.contains("<groupId>com.aethelgard</groupId>")
            || parent.contains("<groupId>com.aethelgard</groupId>"));
  }

  @Test
  @DisplayName("FR-2: product depends on engine; engine/cli/ui do not depend on product")
  void moduleWiringOneWay() throws Exception {
    Path root = findRepoRoot();
    String productPom = Files.readString(root.resolve("product/pom.xml"));
    assertTrue(productPom.contains("<artifactId>engine</artifactId>"));

    String enginePom = Files.readString(root.resolve("engine/pom.xml"));
    String cliPom = Files.readString(root.resolve("cli/pom.xml"));
    String uiPom = Files.readString(root.resolve("ui/pom.xml"));
    assertFalse(enginePom.contains("<artifactId>product</artifactId>"));
    assertFalse(cliPom.contains("<artifactId>product</artifactId>"));
    assertFalse(uiPom.contains("<artifactId>product</artifactId>"));
  }

  @Test
  @DisplayName("FR-3: production types live under com.aethelgard.product")
  void packageRootIsComAethelgardProduct() throws Exception {
    assertEquals("com.aethelgard.product", ProductHost.class.getPackageName());
    Path info =
        findRepoRoot()
            .resolve("product/src/main/java/com/aethelgard/product/package-info.java");
    assertTrue(Files.isRegularFile(info), "package-info.java must exist under com.aethelgard.product");
  }

  @Test
  @DisplayName("FR-4: ProductHost constructs Engine via EngineSetup; Step 0 settled")
  void hostCreatesEngineAtStepZero() {
    EngineSetup setup = ProductHost.setup();
    assertNotNull(setup);

    Engine engine = ProductHost.create();
    assertEquals(0, engine.stepIndex());
    PoolSnapshot settled = engine.settled();
    assertNotNull(settled);
    assertEquals(1, settled.updateCount());
  }

  @Test
  @DisplayName("FR-5: product architecture doc and landmark README record layout")
  void architectureDocRecordsLayout() throws Exception {
    Path root = findRepoRoot();
    Path readme = root.resolve("product/README.md");
    assertTrue(Files.isRegularFile(readme), "product/README.md must exist");

    Path arch = root.resolve("docs/product/architecture.md");
    assertTrue(Files.isRegularFile(arch), "docs/product/architecture.md must exist");
    String text = Files.readString(arch);
    assertTrue(text.contains("com.aethelgard:product"), "must record Maven coordinates");
    assertTrue(text.contains("com.aethelgard.product"), "must record package root");
    assertTrue(
        text.toLowerCase().contains("one-way") || text.contains("never the reverse"),
        "must record one-way dependency rule");
  }

  private static Path findRepoRoot() {
    var dir = Path.of("").toAbsolutePath().normalize();
    for (var cursor = dir; cursor != null; cursor = cursor.getParent()) {
      if (Files.isRegularFile(cursor.resolve("pom.xml"))
          && Files.isDirectory(cursor.resolve("engine"))
          && Files.isDirectory(cursor.resolve("docs"))) {
        return cursor;
      }
    }
    throw new IllegalStateException("repo root not found");
  }
}
