/*
 * File: cli/src/main/java/com/aethelgard/cli/Main.java
 * Purpose: CLI entry point — delegates to CliRunner
 * Audience: Operators / agents
 * Update when: Process entry behavior changes
 */

package com.aethelgard.cli;

/** JVM entry for the headless runner. */
public final class Main {

  private Main() {}

  public static void main(String[] args) {
    CliResult result = CliRunner.run(args);
    System.out.print(result.output());
    if (!result.output().endsWith("\n") && !result.output().isEmpty()) {
      System.out.println();
    }
    System.exit(result.exitCode());
  }
}
