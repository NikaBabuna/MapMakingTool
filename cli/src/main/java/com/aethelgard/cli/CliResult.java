/*
 * File: cli/src/main/java/com/aethelgard/cli/CliResult.java
 * Purpose: Exit code + report text from a CLI run
 * Audience: Main / tests
 * Update when: Result shape changes
 */

package com.aethelgard.cli;

import java.util.Objects;

/**
 * @param exitCode 0 on success; non-zero on failure
 * @param output settled-state report or error message
 */
public record CliResult(int exitCode, String output) {

  public CliResult {
    Objects.requireNonNull(output, "output");
  }

  public boolean isOk() {
    return exitCode == 0;
  }
}
