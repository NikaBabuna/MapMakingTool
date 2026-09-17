/*
 * File: cli/src/main/java/com/aethelgard/cli/CommandDispatch.java
 * Purpose: Placeholder command dispatcher on a ProductSession (unstable verbs)
 * Audience: CliRunner / MapController / tests
 * Update when: Placeholder verb table changes
 */

package com.aethelgard.cli;

import com.aethelgard.product.Grid;
import com.aethelgard.product.PlateVelocities;
import com.aethelgard.product.ProductSession;
import com.aethelgard.product.WorldFields;
import com.aethelgard.product.WorldSpec;
import java.util.Objects;

/**
 * Thin placeholder language. Not a product API. Do not copy these verb names into product
 * Systems, Pool fields, or merge types.
 */
public final class CommandDispatch {

  private CommandDispatch() {}

  /** Executes one line against {@code session}. Never null. */
  public static CliResult execute(ProductSession session, String line) {
    Objects.requireNonNull(session, "session");
    String trimmed = line == null ? "" : line.trim();
    if (trimmed.isEmpty()) {
      return new CliResult(2, "error: empty command");
    }
    String[] parts = trimmed.split("\\s+");
    try {
      return switch (parts[0]) {
        case "status" -> status(session, parts);
        case "advance" -> advance(session, parts);
        case "dump" -> dump(session, parts);
        case "at" -> at(session, parts);
        case "layers" -> layers(parts);
        default -> new CliResult(2, "error: unknown command: " + parts[0]);
      };
    } catch (IllegalArgumentException ex) {
      return new CliResult(2, "error: " + ex.getMessage());
    }
  }

  private static CliResult status(ProductSession session, String[] parts) {
    requireArity(parts, 1, "status");
    WorldSpec spec = session.spec();
    return new CliResult(
        0,
        "step="
            + session.stepIndex()
            + " width="
            + spec.width()
            + " height="
            + spec.height()
            + " seed="
            + spec.seed());
  }

  private static CliResult advance(ProductSession session, String[] parts) {
    int n = 1;
    if (parts.length == 2) {
      n = parseInt(parts[1], "advance");
    } else if (parts.length != 1) {
      throw new IllegalArgumentException("advance takes 0 or 1 argument");
    }
    if (n < 0) {
      throw new IllegalArgumentException("advance N must be >= 0, was " + n);
    }
    session.advance(n);
    return new CliResult(0, "step=" + session.stepIndex());
  }

  private static CliResult dump(ProductSession session, String[] parts) {
    requireArity(parts, 1, "dump");
    return new CliResult(0, session.settledWorld());
  }

  private static CliResult at(ProductSession session, String[] parts) {
    requireArity(parts, 3, "at");
    int x = parseInt(parts[1], "at x");
    int y = parseInt(parts[2], "at y");
    Grid elevation = session.elevation();
    Grid plates = session.plates();
    int id = plates.get(x, y);
    PlateVelocities velocities = session.plateVelocities();
    return new CliResult(
        0,
        "x="
            + x
            + " y="
            + y
            + " elevation="
            + elevation.get(x, y)
            + " plate="
            + id
            + " vx="
            + velocities.vx(id)
            + " vy="
            + velocities.vy(id));
  }

  private static CliResult layers(String[] parts) {
    requireArity(parts, 1, "layers");
    return new CliResult(
        0,
        WorldFields.ELEVATION + "\n" + WorldFields.PLATES + "\n" + WorldFields.PLATE_VELOCITY);
  }

  private static void requireArity(String[] parts, int expected, String verb) {
    if (parts.length != expected) {
      throw new IllegalArgumentException(verb + " takes " + (expected - 1) + " argument(s)");
    }
  }

  private static int parseInt(String raw, String name) {
    try {
      return Integer.parseInt(raw);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(name + " must be an integer, was '" + raw + "'");
    }
  }
}
