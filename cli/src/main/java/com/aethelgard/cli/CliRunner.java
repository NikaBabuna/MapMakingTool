/*
 * File: cli/src/main/java/com/aethelgard/cli/CliRunner.java
 * Purpose: Headless product runner — flags or one placeholder command
 * Audience: Main / tests
 * Update when: Runner behavior or flags change
 */

package com.aethelgard.cli;

import com.aethelgard.product.ProductSession;

/**
 * Testable CLI core — no interactive stdin. Placeholder command surface (ADR-010). {@code --steps
 * N} is a batch dump. Bare argv is one {@link CommandDispatch} line. Does not parse a verb language
 * into product.
 */
public final class CliRunner {

  private CliRunner() {}

  /**
   * Runs from argv. Flag mode ({@code --steps N}, including empty argv): DEFAULT dump after N
   * Steps. Verb mode (no {@code -} args): one dispatcher line on a DEFAULT session. Unknown flags
   * or invalid numbers → non-zero exit.
   */
  public static CliResult run(String[] args) {
    String[] argv = args == null ? new String[0] : args;
    try {
      if (flagMode(argv)) {
        return run(parse(argv));
      }
      ProductSession session = ProductSession.ofDefault();
      return CommandDispatch.execute(session, String.join(" ", argv));
    } catch (IllegalArgumentException ex) {
      return new CliResult(2, "error: " + ex.getMessage());
    }
  }

  /** Batch dump: DEFAULT session, advance {@code steps}, print settled world. */
  public static CliResult run(CliOptions options) {
    if (options.steps() < 0) {
      return new CliResult(2, "error: --steps must be >= 0, was " + options.steps());
    }
    ProductSession session = ProductSession.ofDefault();
    session.advance(options.steps());
    return new CliResult(0, session.settledWorld());
  }

  static boolean flagMode(String[] args) {
    if (args.length == 0) {
      return true;
    }
    for (String arg : args) {
      if (arg.startsWith("-")) {
        return true;
      }
    }
    return false;
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
