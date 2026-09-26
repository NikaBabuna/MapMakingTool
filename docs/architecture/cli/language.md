<!--
  File: docs/architecture/cli/language.md
  Purpose: CommandDispatch — the shared command language: grammar, nouns, verbs, outputs, aliases, and errors
  Audience: Agents and humans
  Update when: CommandDispatch.execute, a noun, a verb, an alias, an output format, or the help text changes
-->

# Command language

One small language drives a running world, whether the line comes from the command line or from the studio's terminal. A line names a thing, then says what to do with it: read it, list its children, advance it, or switch a measurement on or off. A few older short commands still work as aliases.

## What it reads

A `ProductSession` and one line of text.

## What it writes

A `CliResult`: exit code 0 and the answer, or exit code 2 and `error: <message>`. Only `advance` changes the world, and only `on`, `off`, and `clear` change the session's diagnostics; every other line only reads.

## Model

A line is split on whitespace into tokens. In extended Backus–Naur form:

```ebnf
line      = alias | help | list | command ;
help      = "help", [ topic ] ;
topic     = "session" | "pool" | "schema" | "systems" | "diag" | "list" | "get" | "advance" | "help" ;
list      = "list", root ;
root      = "session" | "pool" | "schema" | "systems" | "diag" ;
command   = path, verb, { arg } ;
path      = segment, { ".", segment } ;
verb      = "list" | "get" | "advance" | "on" | "off" | "clear" ;
alias     = "status" | "advance", [ int ] | "dump" | "at", int, int | "layers" | "stats"
          | "diag", ( "list" | "on", id | "off", id | "clear", [ id ] ) ;
```

Resolution is ordered: aliases first, then `help`, then `list <root>`, then `<path> <verb>`. The meaning of every accepted line:

| Line | Answer |
|------|--------|
| `session get` | `step=<k> width=<W> height=<H> seed=<s>` |
| `session get <key>`, `session.<key> get` (key: `step`, `width`, `height`, `seed`) | `<key>=<value>` |
| `session get dump` | The canonical dump ([dump](../session/dump.md)) |
| `session advance [N]` | Advances $N \ge 0$ steps (default 1); `step=<k>` |
| `list session` | `step`, `width`, `height`, `seed`, `dump`, one per line |
| `list pool` | The declared field names, one per line |
| `pool.<field> get` | `<field> type=<Type> …`: size, plate count, contact count, or locker count, by type |
| `pool.<field> get X Y` | `<field>[X,Y]=<value>`, for a grid field only |
| `list schema` | The declared field names, one per line |
| `schema get` | `<field>=<RULE>`, one per line |
| `schema.<field> get` | `<field>=<RULE>` |
| `list systems` | The system ids, one per line |
| `systems get` | `count=<n>` and the ids |
| `systems.<id> get` | `id=<id> category=<path>` and `subsystems=<ids>` |
| `list diag` | The collector ids, one per line |
| `diag get` | The hub report, one summary line per collector |
| `diag.<id> get` | That collector's summary line |
| `diag.<id> on`, `diag.<id> off` | `<id> enabled=true` or `false` |
| `diag.<id> clear`, `diag clear` | `cleared=<id>`, or `cleared=all` |
| `help`, `help <topic>` | The catalog, or one topic |

| Alias | Same as, or answer |
|-------|--------------------|
| `status` | `session get` |
| `advance [N]` | `session advance [N]` |
| `dump` | `session get dump` |
| `at X Y` | `x=<X> y=<Y> elevation=<e> plate=<p> vx=<vx> vy=<vy>` |
| `layers` | `elevation`, `plates`, `plate_velocity`, one per line |
| `stats` | `diag get` |
| `diag list` | `<id> enabled=<bool> n=<m>/<cap>`, one per collector |
| `diag on <id>`, `diag off <id>`, `diag clear [<id>]` | The same as the `diag.<id>` forms |

A collector id may itself contain dots: in `diag.phase.apply get`, everything after the first segment is the id.

`CommandDispatch.dispatch` (the verb table) in [`CommandDispatch.java`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java):

```java
return switch (verb) {
  case "list" -> listNoun(session, nounPath, args);
  case "get" -> getNoun(session, nounPath, args);
  case "advance" -> advanceNoun(session, nounPath, args);
  case "on", "off", "clear" -> diagControl(session, nounPath, verb, args);
  default -> throw new IllegalArgumentException("unknown verb: " + verb);
};
```

## Procedure

1. `execute` rejects a blank line, splits the rest on whitespace, tries `tryAlias`, then `dispatch`, and turns any `IllegalArgumentException` into exit code 2. [`CommandDispatch.execute`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).
2. `tryAlias` answers the aliases, checking each one's arity with `requireArity`. `advance` with more than one argument, and `diag` with any other subcommand, fall through to `dispatch`. The old `diag` forms go through `oldDiag`. [`CommandDispatch.tryAlias`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java), [`CommandDispatch.oldDiag`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).
3. `dispatch` sends `help` to `help`, `list <root>` to `listNoun`, and `<path> <verb>` to the verb table. [`CommandDispatch.dispatch`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).
4. `listNoun` names the children of a root, and refuses a path below a root or extra arguments. [`CommandDispatch.listNoun`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).
5. `getNoun` splits the path with `splitPath`, which rejects an empty segment, and hands it to `sessionGet`, `poolGet`, `schemaGet`, `systemsGet`, or `diagGet`. `poolGet` answers a cell through `cellField` and a whole field through `summarize`. [`CommandDispatch.getNoun`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java), [`CommandDispatch.summarize`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).
6. `advanceNoun` accepts only the root `session`, parses $N$ with `parseInt`, rejects $N < 0$, advances the session, and answers the step index. [`CommandDispatch.advanceNoun`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).
7. `diagControl` accepts only `diag` paths, joins the id with `joinPath`, and switches or clears one collector or all of them. [`CommandDispatch.diagControl`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).
8. `at X Y` answers through `cellInspect`, from the settled elevation, plates, and velocities. [`CommandDispatch.cellInspect`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).
9. `help` returns `HELP_CATALOG`, or the `HELP_TOPICS` entry of one topic, and refuses an unknown topic or more than one argument. `slice` and `prepend` rearrange token arrays. [`CommandDispatch.help`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java).

## What is true afterwards

A line either succeeds with code 0, or is refused with code 2; every argument is checked before anything changes, so a refused line changes nothing. The exception is a failure inside the world's step during `advance`: the steps before it stay done, and a failure that is not an `IllegalArgumentException` reaches the caller as an exception instead of code 2. The same line on the same session gives the same answer from the CLI and from the studio, because both call `execute`. `list pool`, `list schema`, and `schema get` follow the session's field order, which is unspecified ([run](../session/run.md)).

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Language | `CommandDispatch` | `execute`, `dispatch`, `tryAlias`, `oldDiag`, `help`, `listNoun`, `getNoun`, `sessionGet`, `poolGet`, `cellField`, `cellInspect`, `summarize`, `schemaGet`, `systemsGet`, `diagGet`, `diagControl`, `advanceNoun`, `splitPath`, `joinPath`, `slice`, `prepend`, `requireArity`, `CommandDispatch.parseInt`, `HELP_CATALOG`, `HELP_TOPICS` | [`cli/src/main/java/com/aethelgard/cli/CommandDispatch.java`](../../../cli/src/main/java/com/aethelgard/cli/CommandDispatch.java) |

Parent: [one run of the CLI](README.md). Why a noun-and-verb language: [ADR-010](../../paperwork/decisions/ADR-010-product-adapters.md). Why the CLI and the terminal share it: [ADR-012](../../paperwork/decisions/ADR-012-simulation-runner.md).
