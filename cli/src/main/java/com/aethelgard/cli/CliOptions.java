/*
 * File: cli/src/main/java/com/aethelgard/cli/CliOptions.java
 * Purpose: Parsed CLI options for a headless engine run
 * Audience: CliRunner / tests
 * Update when: CLI flags change
 */

package com.aethelgard.cli;

/**
 * @param steps additional Steps after create (N ≥ 0); create already completes Step 0
 * @param initialValue EngineConfig seed
 */
public record CliOptions(int steps, long initialValue) {

  public static final int DEFAULT_STEPS = 0;
  public static final long DEFAULT_INITIAL = 0L;

  public static CliOptions defaults() {
    return new CliOptions(DEFAULT_STEPS, DEFAULT_INITIAL);
  }
}
