/*
 * File: cli/src/main/java/com/aethelgard/cli/CliRunner.java
 * Purpose: Headless product runner — parse args, advance N Steps, print settled world
 * Audience: Main / tests
 * Update when: Runner behavior or flags change
 */

package com.aethelgard.cli;

import com.aethelgard.product.ProductSession;

/**
 * Testable CLI core — no interactive stdin. Placeholder command surface (ADR-010): {@code --steps}
 * only. Does not parse a verb language into product.
 */
public final class CliRunner {

  private CliRunner() {}

  /**
   * Runs from argv. Supported flags: {@code --steps N}. Unknown flags or invalid numbers →
   * non-zero exit.
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
    ProductSession session = ProductSession.ofDefault();
    session.advance(options.steps());
    return new CliResult(0, session.settledWorld());
  }

  static CliOptions parse(String[] args) {
    int steps = CliOptions.DEFAULT_STEPS;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "--steps" -> {
          steps = parseInt(requireValue(args, i, "--steps"), "--steps");
          i++;
        }
        default -> throw new IllegalArgumentException("unknown argument: " + arg);
      }
    }
    if (steps < 0) {
      throw new IllegalArgumentException("--steps must be >= 0, was " + steps);
    }
    return new CliOptions(steps);
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
}
