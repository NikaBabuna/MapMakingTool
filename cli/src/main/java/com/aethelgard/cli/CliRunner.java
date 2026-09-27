/*
 * File: cli/src/main/java/com/aethelgard/cli/CliRunner.java
 * Purpose: Headless product runner — flags and CommandDispatch batch
 * Audience: Main / tests
 * Update when: Runner behavior or flags change
 */

package com.aethelgard.cli;

import com.aethelgard.product.session.ProductSession;
import com.aethelgard.product.world.fields.WorldSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Testable CLI core — no interactive stdin. One invocation owns one {@link ProductSession}. Flag
 * mode: {@code --seed}, {@code --steps}, repeatable {@code -c}/{@code --command}. Bare argv (no
 * {@code -} args) is one {@link CommandDispatch} line. Advances and dumps go through the shared
 * dispatcher.
 */
public final class CliRunner {

  private CliRunner() {}

  /**
   * Runs from argv. Flag mode ({@code --seed}, {@code --steps}, {@code -c}, including empty argv):
   * one session for the whole invocation. Verb mode (no {@code -} args): one dispatcher line on a
   * DEFAULT session. Unknown flags or invalid numbers → non-zero exit.
   */
  public static CliResult run(String[] args) {
    String[] argv = args == null ? new String[0] : args;
    try {
      if (isFlagMode(argv)) {
        return run(parse(argv));
      }
      ProductSession session = ProductSession.ofDefault();
      return CommandDispatch.execute(session, String.join(" ", argv));
    } catch (IllegalArgumentException ex) {
      return new CliResult(2, "error: " + ex.getMessage());
    }
  }

  /**
   * Flag-mode run: create session from seed (DEFAULT geometry), optional {@code --steps} via
   * dispatch, then {@code -c} lines; dump via dispatch when no commands were given.
   */
  public static CliResult run(CliOptions options) {
    if (options.steps() < 0) {
      return new CliResult(2, "error: --steps must be >= 0, was " + options.steps());
    }
    ProductSession session =
        new ProductSession(
            new WorldSpec(WorldSpec.DEFAULT.width(), WorldSpec.DEFAULT.height(), options.seed()));

    if (options.commands().isEmpty()) {
      if (options.stepsSpecified() && options.steps() > 0) {
        CliResult advanced =
            CommandDispatch.execute(session, "session advance " + options.steps());
        if (!advanced.isOk()) {
          return advanced;
        }
      }
      return CommandDispatch.execute(session, "session get dump");
    }

    StringJoiner out = new StringJoiner("\n");
    if (options.stepsSpecified() && options.steps() > 0) {
      CliResult advanced = CommandDispatch.execute(session, "session advance " + options.steps());
      if (!advanced.isOk()) {
        return advanced;
      }
      out.add(advanced.output());
    }
    for (String line : options.commands()) {
      CliResult next = CommandDispatch.execute(session, line);
      if (!next.isOk()) {
        if (out.length() > 0) {
          return new CliResult(next.exitCode(), out + "\n" + next.output());
        }
        return next;
      }
      out.add(next.output());
    }
    return new CliResult(0, out.toString());
  }

  static boolean isFlagMode(String[] args) {
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
    long seed = CliOptions.DEFAULT_SEED;
    int steps = CliOptions.DEFAULT_STEPS;
    boolean stepsSpecified = false;
    List<String> commands = new ArrayList<>();
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "--seed" -> {
          seed = parseLong(requireValue(args, i, "--seed"), "--seed");
          i++;
        }
        case "--steps" -> {
          steps = parseInt(requireValue(args, i, "--steps"), "--steps");
          stepsSpecified = true;
          i++;
        }
        case "-c", "--command" -> {
          commands.add(requireValue(args, i, arg));
          i++;
        }
        default -> throw new IllegalArgumentException("unknown argument: " + arg);
      }
    }
    if (steps < 0) {
      throw new IllegalArgumentException("--steps must be >= 0, was " + steps);
    }
    // Empty argv: dump at step 0 (legacy default).
    if (args.length == 0) {
      stepsSpecified = true;
    }
    return new CliOptions(seed, steps, stepsSpecified, commands);
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
      throw new IllegalArgumentException(flag + " must be an integer, was '" + raw + "'");
    }
  }
}
