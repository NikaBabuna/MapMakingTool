/*
 * File: cli/src/main/java/com/aethelgard/cli/CommandDispatch.java
 * Purpose: Shared noun/verb command language on a ProductSession (F-048)
 * Audience: CliRunner / MapController / tests
 * Update when: Command grammar or catalog changes
 */

package com.aethelgard.cli;

import com.aethelgard.product.AreaFlux;
import com.aethelgard.product.Boundaries;
import com.aethelgard.product.DiagnosticCollector;
import com.aethelgard.product.DiagnosticsHub;
import com.aethelgard.product.Grid;
import com.aethelgard.product.MotionIntent;
import com.aethelgard.product.PlateRegistry;
import com.aethelgard.product.PlateVelocities;
import com.aethelgard.product.ProductSession;
import com.aethelgard.product.WorldFields;
import com.aethelgard.product.WorldSpec;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Shared operator language: {@code <noun-path> <verb> [args…]}, plus {@code help} and verb-first
 * {@code list <noun>}. Deprecated flat aliases ({@code status}, {@code advance}, …) remain for
 * G-009. Do not copy verb names into product Systems, Pool fields, or merge types.
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
      CliResult aliased = tryAlias(session, parts);
      if (aliased != null) {
        return aliased;
      }
      return dispatch(session, parts);
    } catch (IllegalArgumentException ex) {
      return new CliResult(2, "error: " + ex.getMessage());
    }
  }

  private static CliResult dispatch(ProductSession session, String[] parts) {
    String head = parts[0];
    if (head.equals("help")) {
      return help(parts);
    }
    if (head.equals("list") && parts.length >= 2) {
      return listNoun(session, parts[1], slice(parts, 2));
    }
    if (parts.length < 2) {
      throw new IllegalArgumentException("unknown command: " + head);
    }
    String nounPath = parts[0];
    String verb = parts[1];
    String[] args = slice(parts, 2);
    return switch (verb) {
      case "list" -> listNoun(session, nounPath, args);
      case "get" -> getNoun(session, nounPath, args);
      case "advance" -> advanceNoun(session, nounPath, args);
      case "on", "off", "clear" -> diagControl(session, nounPath, verb, args);
      default -> throw new IllegalArgumentException("unknown verb: " + verb);
    };
  }

  /** Deprecated flat verbs — preserve prior success outputs (F-048 / G-009). */
  private static CliResult tryAlias(ProductSession session, String[] parts) {
    return switch (parts[0]) {
      case "status" -> {
        requireArity(parts, 1, "status");
        yield sessionGet(session, new String[0]);
      }
      case "advance" -> {
        // bare "advance" / "advance N" — not "session advance"
        if (parts.length <= 2) {
          yield advanceNoun(session, "session", slice(parts, 1));
        }
        yield null;
      }
      case "dump" -> {
        requireArity(parts, 1, "dump");
        yield new CliResult(0, session.settledWorld());
      }
      case "at" -> {
        requireArity(parts, 3, "at");
        int x = parseInt(parts[1], "at x");
        int y = parseInt(parts[2], "at y");
        yield cellInspect(session, x, y);
      }
      case "layers" -> {
        requireArity(parts, 1, "layers");
        yield new CliResult(
            0,
            WorldFields.ELEVATION + "\n" + WorldFields.PLATES + "\n" + WorldFields.PLATE_VELOCITY);
      }
      case "stats" -> {
        requireArity(parts, 1, "stats");
        yield new CliResult(0, session.diagnostics().report());
      }
      case "diag" -> {
        if (parts.length >= 2
            && (parts[1].equals("list")
                || parts[1].equals("on")
                || parts[1].equals("off")
                || parts[1].equals("clear"))) {
          yield oldDiag(session, parts);
        }
        yield null;
      }
      default -> null;
    };
  }

  private static CliResult oldDiag(ProductSession session, String[] parts) {
    if (parts.length < 2) {
      throw new IllegalArgumentException("diag requires a subcommand: list|on|off|clear");
    }
    DiagnosticsHub hub = session.diagnostics();
    return switch (parts[1]) {
      case "list" -> {
        requireArity(parts, 2, "diag list");
        yield new CliResult(0, hub.listReport());
      }
      case "on" -> {
        requireArity(parts, 3, "diag on");
        hub.setEnabled(parts[2], true);
        yield new CliResult(0, parts[2] + " enabled=true");
      }
      case "off" -> {
        requireArity(parts, 3, "diag off");
        hub.setEnabled(parts[2], false);
        yield new CliResult(0, parts[2] + " enabled=false");
      }
      case "clear" -> {
        if (parts.length == 2) {
          hub.clearAll();
          yield new CliResult(0, "cleared=all");
        }
        if (parts.length == 3) {
          hub.clear(parts[2]);
          yield new CliResult(0, "cleared=" + parts[2]);
        }
        throw new IllegalArgumentException("diag clear takes 0 or 1 collector id");
      }
      default -> throw new IllegalArgumentException("unknown diag subcommand: " + parts[1]);
    };
  }

  private static CliResult help(String[] parts) {
    if (parts.length == 1) {
      return new CliResult(0, HELP_CATALOG);
    }
    if (parts.length == 2) {
      String topic = parts[1];
      String text = HELP_TOPICS.get(topic);
      if (text == null) {
        throw new IllegalArgumentException("unknown help topic: " + topic);
      }
      return new CliResult(0, text);
    }
    throw new IllegalArgumentException("help takes 0 or 1 argument");
  }

  private static CliResult listNoun(ProductSession session, String nounPath, String[] args) {
    if (args.length != 0) {
      throw new IllegalArgumentException("list takes no extra arguments");
    }
    String[] path = splitPath(nounPath);
    return switch (path[0]) {
      case "pool" -> {
        if (path.length != 1) {
          throw new IllegalArgumentException("list pool has no children path");
        }
        yield new CliResult(0, String.join("\n", session.fieldNames()));
      }
      case "schema" -> {
        if (path.length != 1) {
          throw new IllegalArgumentException("list schema has no children path");
        }
        yield new CliResult(0, String.join("\n", session.schemaTypes().keySet()));
      }
      case "systems" -> {
        if (path.length != 1) {
          throw new IllegalArgumentException("list systems has no children path");
        }
        yield new CliResult(0, String.join("\n", session.systemIds()));
      }
      case "diag" -> {
        if (path.length != 1) {
          throw new IllegalArgumentException("list diag has no children path");
        }
        yield new CliResult(0, String.join("\n", session.diagnostics().ids()));
      }
      case "session" -> {
        if (path.length != 1) {
          throw new IllegalArgumentException("list session has no children path");
        }
        yield new CliResult(0, "step\nwidth\nheight\nseed\ndump");
      }
      default -> throw new IllegalArgumentException("cannot list: " + nounPath);
    };
  }

  private static CliResult getNoun(ProductSession session, String nounPath, String[] args) {
    String[] path = splitPath(nounPath);
    return switch (path[0]) {
      case "session" -> sessionGet(session, path.length == 1 ? args : prepend(path[1], args));
      case "pool" -> poolGet(session, path, args);
      case "schema" -> schemaGet(session, path, args);
      case "systems" -> systemsGet(session, path, args);
      case "diag" -> diagGet(session, path, args);
      default -> throw new IllegalArgumentException("cannot get: " + nounPath);
    };
  }

  private static CliResult sessionGet(ProductSession session, String[] args) {
    if (args.length == 0) {
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
    if (args.length == 1 && args[0].equals("dump")) {
      return new CliResult(0, session.settledWorld());
    }
    if (args.length == 1) {
      return switch (args[0]) {
        case "step" -> new CliResult(0, "step=" + session.stepIndex());
        case "width" -> new CliResult(0, "width=" + session.spec().width());
        case "height" -> new CliResult(0, "height=" + session.spec().height());
        case "seed" -> new CliResult(0, "seed=" + session.spec().seed());
        default -> throw new IllegalArgumentException("unknown session field: " + args[0]);
      };
    }
    throw new IllegalArgumentException("session get takes 0 args, 'dump', or one key");
  }

  private static CliResult poolGet(ProductSession session, String[] path, String[] args) {
    if (path.length == 1) {
      throw new IllegalArgumentException("pool get requires a field: pool.<field> get");
    }
    if (path.length != 2) {
      throw new IllegalArgumentException("pool path too deep: " + String.join(".", path));
    }
    String field = path[1];
    Object value = session.field(field);
    if (args.length == 2) {
      int x = parseInt(args[0], "x");
      int y = parseInt(args[1], "y");
      return cellField(session, field, value, x, y);
    }
    if (args.length != 0) {
      throw new IllegalArgumentException("pool.<field> get takes 0 args or X Y");
    }
    return new CliResult(0, summarize(field, value));
  }

  private static CliResult cellField(
      ProductSession session, String field, Object value, int x, int y) {
    if (value instanceof Grid grid) {
      return new CliResult(0, field + "[" + x + "," + y + "]=" + grid.get(x, y));
    }
    if (field.equals(WorldFields.PLATES) || field.equals(WorldFields.ELEVATION)) {
      // unreachable if typed correctly
    }
    throw new IllegalArgumentException("field '" + field + "' is not a grid; cannot get X Y");
  }

  private static CliResult cellInspect(ProductSession session, int x, int y) {
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

  private static String summarize(String field, Object value) {
    if (value instanceof Grid grid) {
      return field + " type=Grid width=" + grid.width() + " height=" + grid.height();
    }
    if (value instanceof PlateVelocities vel) {
      return field + " type=PlateVelocities plates=" + vel.count() + " seed=" + vel.seed();
    }
    if (value instanceof PlateRegistry reg) {
      return field + " type=PlateRegistry plates=" + reg.count();
    }
    if (value instanceof Boundaries boundaries) {
      return field + " type=Boundaries contacts=" + boundaries.contacts().size();
    }
    if (value instanceof AreaFlux flux) {
      return field + " type=AreaFlux plates=" + flux.plateCount();
    }
    if (value instanceof MotionIntent intent) {
      return field + " type=MotionIntent plates=" + intent.plateCount();
    }
    return field + " type=" + value.getClass().getSimpleName();
  }

  private static CliResult schemaGet(ProductSession session, String[] path, String[] args) {
    if (args.length != 0) {
      throw new IllegalArgumentException("schema get takes no extra arguments");
    }
    Map<String, String> types = session.schemaTypes();
    if (path.length == 1) {
      StringJoiner sj = new StringJoiner("\n");
      for (var e : types.entrySet()) {
        sj.add(e.getKey() + "=" + e.getValue());
      }
      return new CliResult(0, sj.toString());
    }
    if (path.length == 2) {
      String t = types.get(path[1]);
      if (t == null) {
        throw new IllegalArgumentException("unknown field: " + path[1]);
      }
      return new CliResult(0, path[1] + "=" + t);
    }
    throw new IllegalArgumentException("schema path too deep");
  }

  private static CliResult systemsGet(ProductSession session, String[] path, String[] args) {
    if (args.length != 0) {
      throw new IllegalArgumentException("systems get takes no extra arguments");
    }
    if (path.length == 1) {
      List<String> ids = session.systemIds();
      StringJoiner sj = new StringJoiner("\n");
      for (String id : ids) {
        sj.add(id);
      }
      return new CliResult(0, "count=" + ids.size() + (ids.isEmpty() ? "" : "\n" + sj));
    }
    if (path.length == 2) {
      return new CliResult(0, session.systemDetail(path[1]));
    }
    throw new IllegalArgumentException("systems path too deep");
  }

  private static CliResult diagGet(ProductSession session, String[] path, String[] args) {
    if (args.length != 0) {
      throw new IllegalArgumentException("diag get takes no extra arguments");
    }
    DiagnosticsHub hub = session.diagnostics();
    if (path.length == 1) {
      return new CliResult(0, hub.report());
    }
    if (path.length >= 2) {
      String id = joinPath(path, 1);
      DiagnosticCollector c = hub.get(id);
      return new CliResult(0, c.summaryLine());
    }
    throw new IllegalArgumentException("diag path invalid");
  }

  private static CliResult diagControl(
      ProductSession session, String nounPath, String verb, String[] args) {
    String[] path = splitPath(nounPath);
    if (!path[0].equals("diag")) {
      throw new IllegalArgumentException(verb + " only applies to diag");
    }
    DiagnosticsHub hub = session.diagnostics();
    if (verb.equals("clear") && path.length == 1) {
      if (args.length != 0) {
        throw new IllegalArgumentException("diag clear takes no extra arguments");
      }
      hub.clearAll();
      return new CliResult(0, "cleared=all");
    }
    if (path.length < 2) {
      throw new IllegalArgumentException(verb + " requires diag.<collector>");
    }
    String id = joinPath(path, 1);
    if (args.length != 0) {
      throw new IllegalArgumentException("diag." + id + " " + verb + " takes no extra arguments");
    }
    return switch (verb) {
      case "on" -> {
        hub.setEnabled(id, true);
        yield new CliResult(0, id + " enabled=true");
      }
      case "off" -> {
        hub.setEnabled(id, false);
        yield new CliResult(0, id + " enabled=false");
      }
      case "clear" -> {
        hub.clear(id);
        yield new CliResult(0, "cleared=" + id);
      }
      default -> throw new IllegalArgumentException("unknown diag verb: " + verb);
    };
  }

  private static CliResult advanceNoun(ProductSession session, String nounPath, String[] args) {
    if (!nounPath.equals("session")) {
      throw new IllegalArgumentException("advance only applies to session");
    }
    int n = 1;
    if (args.length == 1) {
      n = parseInt(args[0], "advance");
    } else if (args.length != 0) {
      throw new IllegalArgumentException("session advance takes 0 or 1 argument");
    }
    if (n < 0) {
      throw new IllegalArgumentException("advance N must be >= 0, was " + n);
    }
    session.advance(n);
    return new CliResult(0, "step=" + session.stepIndex());
  }

  private static String[] splitPath(String nounPath) {
    if (nounPath == null || nounPath.isBlank()) {
      throw new IllegalArgumentException("empty noun path");
    }
    String[] path = nounPath.split("\\.");
    for (String p : path) {
      if (p.isEmpty()) {
        throw new IllegalArgumentException("invalid noun path: " + nounPath);
      }
    }
    return path;
  }

  private static String joinPath(String[] path, int from) {
    StringJoiner sj = new StringJoiner(".");
    for (int i = from; i < path.length; i++) {
      sj.add(path[i]);
    }
    return sj.toString();
  }

  private static String[] slice(String[] parts, int from) {
    if (from >= parts.length) {
      return new String[0];
    }
    String[] out = new String[parts.length - from];
    System.arraycopy(parts, from, out, 0, out.length);
    return out;
  }

  private static String[] prepend(String first, String[] rest) {
    String[] out = new String[rest.length + 1];
    out[0] = first;
    System.arraycopy(rest, 0, out, 1, rest.length);
    return out;
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

  private static final String HELP_CATALOG =
      """
      Aethelgard command language (F-048)
      Grammar: <noun-path> <verb> [args…]
               list <noun>
               help [<topic>]

      Nouns: session  pool  schema  systems  diag
      Verbs: list  get  advance  on  off  clear  help

      Examples:
        session get
        session advance 10
        list pool
        pool.plates get
        pool.elevation get 0 0
        list systems
        systems.tectonics get
        list diag
        diag.phase.apply get
        diag.advance.wall on

      Deprecated aliases (G-009): status, advance, dump, at, layers, stats, diag …
      """;

  private static final Map<String, String> HELP_TOPICS =
      Map.ofEntries(
          Map.entry("session", "session — the run\n  session list|get|advance [N]\n  session get dump"),
          Map.entry("pool", "pool — settled Pool fields\n  list pool\n  pool.<field> get [X Y]"),
          Map.entry("schema", "schema — field merge types\n  list schema\n  schema get\n  schema.<field> get"),
          Map.entry(
              "systems", "systems — registered EngineSystems\n  list systems\n  systems get\n  systems.<id> get"),
          Map.entry(
              "diag",
              "diag — DiagnosticsHub\n  list diag\n  diag get\n  diag.<id> get|on|off|clear\n  diag clear"),
          Map.entry("list", "list <noun> — name children of a root noun"),
          Map.entry("get", "<noun-path> get [args] — read summary or value"),
          Map.entry("advance", "session advance [N] — step the simulation"),
          Map.entry("help", "help [<noun>|<verb>] — this catalog or one topic"));
}
