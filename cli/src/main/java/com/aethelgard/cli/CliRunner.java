/*
 * File: cli/src/main/java/com/aethelgard/cli/CliRunner.java
 * Purpose: Headless engine runner — parse args, advance N Steps, report settled state
 * Audience: Main / tests
 * Update when: Runner behavior or flags change
 */

package com.aethelgard.cli;

import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.PoolSnapshot;
import java.util.Map;

/** Testable CLI core — no interactive stdin. */
public final class CliRunner {

  private CliRunner() {}

  /**
   * Runs from argv. Supported flags: {@code --steps N}, {@code --initial V}. Unknown flags or
   * invalid numbers → non-zero exit.
   */
  public static CliResult run(String[] args) {
    try {
      CliOptions options = parse(args == null ? new String[0] : args);
      return run(options);
    } catch (IllegalArgumentException ex) {
      return new CliResult(2, "error: " + ex.getMessage());
    }
  }

  /** Runs with already-validated options. */
  public static CliResult run(CliOptions options) {
    if (options.steps() < 0) {
      return new CliResult(2, "error: --steps must be >= 0, was " + options.steps());
    }
    Engine engine = Engine.create(new EngineConfig(options.initialValue()));
    engine.advance(options.steps());
    return new CliResult(0, formatSettled(engine));
  }

  static CliOptions parse(String[] args) {
    int steps = CliOptions.DEFAULT_STEPS;
    long initial = CliOptions.DEFAULT_INITIAL;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "--steps" -> {
          steps = parseInt(requireValue(args, i, "--steps"), "--steps");
          i++;
        }
        case "--initial" -> {
          initial = parseLong(requireValue(args, i, "--initial"), "--initial");
          i++;
        }
        default -> throw new IllegalArgumentException("unknown argument: " + arg);
      }
    }
    if (steps < 0) {
      throw new IllegalArgumentException("--steps must be >= 0, was " + steps);
    }
    return new CliOptions(steps, initial);
  }

  static String formatSettled(Engine engine) {
    PoolSnapshot snap = engine.settled();
    StringBuilder sb = new StringBuilder();
    sb.append("stepIndex=").append(engine.stepIndex()).append('\n');
    sb.append("value=").append(snap.value()).append('\n');
    sb.append("updateCount=").append(snap.updateCount()).append('\n');
    Map<String, Long> fields = snap.fields();
    if (!fields.isEmpty()) {
      sb.append("fields=").append(fields).append('\n');
    }
    return sb.toString();
  }

  private static String requireValue(String[] args, int flagIndex, String flag) {
    if (flagIndex + 1 >= args.length) {
      throw new IllegalArgumentException(flag + " requires a value");
    }
    return args[flagIndex + 1];
  }

  private static int parseInt(String raw, String flag) {
    try {
      return Integer.parseInt(raw);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(flag + " must be an integer, was '" + raw + "'");
    }
  }

  private static long parseLong(String raw, String flag) {
    try {
      return Long.parseLong(raw);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(flag + " must be a long, was '" + raw + "'");
    }
  }
}
