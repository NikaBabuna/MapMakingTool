/*
 * File: cli/src/main/java/com/aethelgard/cli/CliOptions.java
 * Purpose: Parsed CLI options for a headless product run
 * Audience: CliRunner / tests
 * Update when: CLI flags change
 */

package com.aethelgard.cli;

import java.util.List;
import java.util.Objects;

/**
 * Headless runner flags. Geometry defaults to {@code WorldSpec.DEFAULT} width/height;
 * {@code seed} overrides the recorded RNG seed.
 *
 * @param seed session seed (default {@code 0})
 * @param steps additional generation Steps when {@code stepsSpecified} (N ≥ 0)
 * @param stepsSpecified whether {@code --steps} appeared (empty argv implies dump at step 0)
 * @param commands dispatcher lines from {@code -c} / {@code --command} (may be empty)
 */
public record CliOptions(long seed, int steps, boolean stepsSpecified, List<String> commands) {

  public static final int DEFAULT_STEPS = 0;
  public static final long DEFAULT_SEED = 0L;

  public CliOptions {
    Objects.requireNonNull(commands, "commands");
    commands = List.copyOf(commands);
  }

  public static CliOptions defaults() {
    return new CliOptions(DEFAULT_SEED, DEFAULT_STEPS, true, List.of());
  }
}
