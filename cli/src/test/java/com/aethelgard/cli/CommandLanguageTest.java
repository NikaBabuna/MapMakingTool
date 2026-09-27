/*
 * File: cli/src/test/java/com/aethelgard/cli/CommandLanguageTest.java
 * Purpose: Proves the command language the CLI and the studio terminal share: its catalogue, aliases, diag controls, refusals, and serialized advances
 * Audience: Agents / CI
 * Update when: CommandDispatch changes
 */

package com.aethelgard.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.session.ProductSession;
import com.aethelgard.product.session.diagnostics.RingDiagnosticCollector;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.WorldSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommandLanguageTest {

  private static final WorldSpec SPEC = new WorldSpec(16, 8, 5L);

  private static final Set<String> FIELDS =
      Set.of("elevation", "plates", "plate_velocity", "plate_registry", "boundaries",
          "area_flux", "motion_intent", "occupancy", "lockers");

  /** Proves F-068 FR-49 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("help lists every noun and verb, and a topic answers on its own")
  void helpListsTheCatalogue() {
    ProductSession session = new ProductSession(SPEC);
    String help = ok(session, "help");
    for (String word : List.of("session", "pool", "schema", "systems", "diag", "list", "get", "advance")) {
      assertTrue(help.contains(word), "help mentions " + word);
    }
    assertTrue(ok(session, "help session").contains("advance"));
  }

  /** Proves F-068 FR-49 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Each noun-verb command answers as the catalogue says")
  void nounVerbCommandsAnswer() {
    ProductSession session = new ProductSession(SPEC);

    assertEquals("step=0 width=16 height=8 seed=5", ok(session, "session get"));
    assertEquals("seed=5", ok(session, "session get seed"));
    assertEquals("width=16", ok(session, "session.width get"));
    assertEquals("step=3", ok(session, "session advance 3"));
    assertEquals("step=4", ok(session, "session advance"));
    assertEquals(4, session.stepIndex());
    assertEquals(session.settledWorld(), ok(session, "session get dump"));
    assertEquals("step\nwidth\nheight\nseed\ndump", ok(session, "list session"));

    assertEquals(FIELDS, lines(ok(session, "list pool")));
    assertEquals(FIELDS, lines(ok(session, "list schema")));
    Set<String> rules = lines(ok(session, "schema get"));
    for (String field : FIELDS) {
      assertTrue(rules.contains(field + "=STATIC"), field);
    }
    assertEquals("elevation=STATIC", ok(session, "schema.elevation get"));

    Grid elevation = session.elevation();
    assertEquals("elevation[2,3]=" + elevation.get(2, 3), ok(session, "pool.elevation get 2 3"));
    assertTrue(ok(session, "pool.elevation get").startsWith("elevation "));

    assertEquals("tectonics", ok(session, "list systems"));
    String systems = ok(session, "systems get");
    assertTrue(systems.contains("count=1") && systems.contains("tectonics"), systems);
    assertTrue(ok(session, "systems.tectonics get").contains("id=tectonics category=world/tectonics"));

    assertEquals(new TreeSet<>(session.diagnostics().ids()), lines(ok(session, "list diag")));
    assertEquals(session.diagnostics().ids().size(), ok(session, "diag get").split("\n").length);
  }

  /** Proves F-068 FR-49 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Each alias answers like its long form")
  void aliasesAnswerLikeTheirLongForms() {
    ProductSession a = new ProductSession(SPEC);
    ProductSession b = new ProductSession(SPEC);

    assertEquals(ok(a, "session get"), ok(a, "status"));
    assertEquals(ok(b, "session advance 2"), ok(a, "advance 2"));
    assertEquals(ok(b, "session advance"), ok(a, "advance"));
    assertEquals(ok(a, "session get dump"), ok(a, "dump"));
    assertEquals(ok(a, "diag get"), ok(a, "stats"));
    assertEquals("elevation\nplates\nplate_velocity", ok(a, "layers"));

    Grid elevation = a.elevation();
    int plate = a.plates().get(5, 2);
    assertEquals(
        "x=5 y=2 elevation=" + elevation.get(5, 2) + " plate=" + plate
            + " vx=" + a.plateVelocities().vx(plate) + " vy=" + a.plateVelocities().vy(plate),
        ok(a, "at 5 2"));
  }

  /** Proves F-068 FR-49 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("The diag controls turn a collector off and on, clear one or all, and list them")
  void diagControlsWork() {
    ProductSession session = new ProductSession(SPEC);
    RingDiagnosticCollector wall = (RingDiagnosticCollector) session.diagnostics().get("advance.wall");

    assertEquals("advance.wall enabled=false", ok(session, "diag.advance.wall off"));
    ok(session, "advance 2");
    assertEquals(0, wall.size(), "off: nothing recorded");

    assertEquals("advance.wall enabled=true", ok(session, "diag on advance.wall"));
    ok(session, "advance 2");
    assertEquals(2, wall.size());
    assertTrue(lines(ok(session, "diag list")).contains("advance.wall enabled=true n=2/64"));

    assertEquals("cleared=advance.wall", ok(session, "diag clear advance.wall"));
    assertEquals(0, wall.size());
    ok(session, "advance 1");
    assertEquals("cleared=all", ok(session, "diag clear"));
    assertEquals(0, wall.size());
    assertEquals("advance.wall enabled=false", ok(session, "diag off advance.wall"));
    assertEquals("cleared=phase.apply", ok(session, "diag.phase.apply clear"));
  }

  /** Proves F-068 FR-49 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A refused line answers with exit code 2 and an error, and changes nothing")
  void refusedLineChangesNothing() {
    ProductSession session = new ProductSession(SPEC);
    ok(session, "advance 1");
    String before = session.settledWorld();

    for (String line : List.of("", "nope", "session fly", "session advance -1", "session advance x",
        "pool.nofield get", "schema.nofield get", "session get nokey", "diag.nosuch off", "at x y", "pool.elevation get 99 99")) {
      CliResult result = CommandDispatch.execute(session, line);
      assertEquals(2, result.exitCode(), "'" + line + "' → " + result.output());
      assertTrue(result.output().startsWith("error: "), "'" + line + "' → " + result.output());
      assertEquals(1, session.stepIndex(), "'" + line + "' did not step");
      assertEquals(before, session.settledWorld(), "'" + line + "' changed nothing");
    }
  }

  /** Proves F-068 FR-50 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Concurrent advance commands on one session serialize: the final step is their sum")
  void concurrentAdvancesSerialize() throws Exception {
    ProductSession shared = new ProductSession(SPEC);
    ExecutorService pool = Executors.newFixedThreadPool(4);
    CountDownLatch start = new CountDownLatch(1);
    List<Future<CliResult>> answers = new ArrayList<>();
    for (int t = 0; t < 4; t++) {
      answers.add(pool.submit(() -> {
        start.await();
        return CommandDispatch.execute(shared, "session advance 3");
      }));
    }
    start.countDown();
    for (Future<CliResult> f : answers) {
      assertEquals(0, f.get().exitCode());
    }
    pool.shutdown();

    ProductSession alone = new ProductSession(SPEC);
    alone.advance(12);
    assertEquals(12, shared.stepIndex());
    assertEquals(alone.settledWorld(), shared.settledWorld(), "the same world as one caller advancing 12");
  }

  /** Proves F-075 FR-1 (docs/paperwork/steps/F-075.md). */
  @Test
  @DisplayName("help and every help topic name no Goal, Step, or decision id")
  void helpNamesNoRecordId() {
    ProductSession session = new ProductSession(SPEC);
    Pattern recordId = Pattern.compile("\\b(F|G)-\\d{3}\\b|\\bADR-\\d{3}\\b");
    String help = ok(session, "help");
    assertTrue(help.startsWith("Aethelgard command language\n"), help);
    assertTrue(help.contains("Deprecated aliases: status, advance"), help);
    assertFalse(recordId.matcher(help).find(), help);
    for (String topic : List.of("session", "pool", "schema", "systems", "diag", "list", "get", "advance", "help")) {
      String text = ok(session, "help " + topic);
      assertFalse(recordId.matcher(text).find(), "help " + topic + ": " + text);
    }
  }

  private static String ok(ProductSession session, String line) {
    CliResult result = CommandDispatch.execute(session, line);
    assertEquals(0, result.exitCode(), "'" + line + "' → " + result.output());
    return result.output();
  }

  private static Set<String> lines(String text) {
    return new TreeSet<>(Arrays.asList(text.split("\n")));
  }
}
