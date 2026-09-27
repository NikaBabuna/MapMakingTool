/*
 * File: cli/src/test/java/com/aethelgard/cli/CliRunnerTest.java
 * Purpose: Proves what one run of the command line does: its default, its flags on one world, and its refusals
 * Audience: Agents / CI
 * Update when: CliRunner or CliOptions changes
 */

package com.aethelgard.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aethelgard.product.session.ProductSession;
import com.aethelgard.product.world.fields.WorldSpec;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CliRunnerTest {

  /** Proves F-068 FR-46 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("With no arguments the CLI prints the seed-0 8 by 8 world at step 0 and exits 0")
  void noArgumentsPrintsTheSeedZeroWorld() {
    CliResult result = CliRunner.run(new String[0]);

    assertEquals(0, result.exitCode());
    assertTrue(result.output().startsWith("world w=8 h=8 seed=0 steps=0\n"));
    assertEquals(new ProductSession(new WorldSpec(8, 8, 0L)).settledWorld(), result.output());
  }

  /** Proves F-068 FR-47 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("--seed and repeated -c act on one world, and the command lines run in order")
  void seedStepsAndCommandsShareOneWorld() {
    CliResult result =
        CliRunner.run(new String[] {
          "--seed", "7", "-c", "session advance 2", "-c", "session get", "--command", "session get seed"
        });

    assertEquals(0, result.exitCode());
    assertEquals("step=2\nstep=2 width=8 height=8 seed=7\nseed=7", result.output());
  }

  /** Proves F-068 FR-47 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("--steps advances the world that many steps and then prints its dump")
  void stepsPrintTheSettledDump() {
    CliResult result = CliRunner.run(new String[] {"--seed", "3", "--steps", "4"});

    ProductSession expected = new ProductSession(new WorldSpec(8, 8, 3L));
    expected.advance(4);
    assertEquals(0, result.exitCode());
    assertEquals(expected.settledWorld(), result.output());
    assertTrue(result.output().startsWith("world w=8 h=8 seed=3 steps=4\n"));
  }

  /** Proves F-068 FR-48 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A bad argument prints an error, exits non-zero, and runs nothing")
  void badArgumentsAreRefused() {
    List<String[]> refused =
        List.of(
            new String[] {"--steps", "-1"},
            new String[] {"--bogus"},
            new String[] {"--seed", "north"},
            new String[] {"--steps"},
            new String[] {"--steps", "2", "-c"});
    for (String[] args : refused) {
      CliResult result = CliRunner.run(args);
      String label = String.join(" ", args);
      assertNotEquals(0, result.exitCode(), label);
      assertFalse(result.isOk(), label);
      assertTrue(result.output().startsWith("error: "), label + " → " + result.output());
      assertFalse(result.output().contains("world w="), label + ": nothing ran");
    }
  }
}
