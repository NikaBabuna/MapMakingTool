/*
 * File: cli/src/main/java/com/aethelgard/cli/CliOptions.java
 * Purpose: Parsed CLI options for a headless product run
 * Audience: CliRunner / tests
 * Update when: CLI flags change
 */

package com.aethelgard.cli;

/**
 * Placeholder flags for G-005. Unstable — do not treat as a product API.
 *
 * @param steps additional generation Steps after create (N ≥ 0)
 */
public record CliOptions(int steps) {

  public static final int DEFAULT_STEPS = 0;

  public static CliOptions defaults() {
    return new CliOptions(DEFAULT_STEPS);
  }
}
