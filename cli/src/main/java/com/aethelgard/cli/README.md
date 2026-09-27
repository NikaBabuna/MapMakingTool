<!--
  File: cli/src/main/java/com/aethelgard/cli/README.md
  Purpose: Door to the command line: the runner that turns an invocation into lines, and the command language that answers them
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Command line

Runs a world with no window: the runner turns the arguments of one invocation into command lines on one session, and the command language answers each line with an exit code and text.

**Paper:** [One run of the CLI](../../../../../../../docs/architecture/cli/README.md) · [Runner](../../../../../../../docs/architecture/cli/runner.md) · [Command language](../../../../../../../docs/architecture/cli/language.md) · **Conventions:** [conventions.md](../../../../../../../docs/architecture/conventions.md)

## Why

The command line is one area of the paper and small enough to be one package, so its own package is that area. The command language lives here, not in the studio, because it needs nothing but a session; the studio's terminal sends its lines to the same `CommandDispatch`, so both speak one language. The world and the session themselves belong to the product module.

## How it works

1. `Main.main` passes the arguments to `CliRunner.run`, prints the output, and exits with the result's code.
2. `CliRunner.run` decides between flag mode and verb mode with `isFlagMode`. In flag mode `parse` reads `--seed`, `--steps`, and repeated `-c` lines into `CliOptions`; in verb mode the words form one line.
3. The runner creates one `ProductSession` on the 8 by 8 map and runs the lines through `CommandDispatch.execute`: an optional advance, then each line, or the dump when there is none.
4. `CommandDispatch.execute` splits a line, answers the old flat aliases through `tryAlias`, and sends the rest to `dispatch`: `help`, `list <root>`, or `<noun-path> <verb>`, each answered by its own member (`getNoun`, `advanceNoun`, `diagControl`, `cellInspect`, `help`).
5. Every answer is a `CliResult`: an exit code, 0 or 2, and the text; `isOk` is true for 0.

**Start reading at:** `CommandDispatch.execute` in [CommandDispatch.java](CommandDispatch.java).

## Depends on

- [product session/](../../../../../../../product/src/main/java/com/aethelgard/product/session/README.md) — the session the lines drive
- [product session diagnostics/](../../../../../../../product/src/main/java/com/aethelgard/product/session/diagnostics/README.md) — the collectors the diag commands switch and report
- [product world/fields/](../../../../../../../product/src/main/java/com/aethelgard/product/world/fields/README.md), [product world/boundaries/](../../../../../../../product/src/main/java/com/aethelgard/product/world/boundaries/README.md), [product world/interaction/](../../../../../../../product/src/main/java/com/aethelgard/product/world/interaction/README.md) — the values the commands print

## Used by

- [ui](../../../../../../../ui/README.md) — the studio's terminal sends its lines to `CommandDispatch`
- [the command-line tests](../../../../../test/java/com/aethelgard/cli/README.md) — one run of the command line, and the command language

## Where each step happens

### [Runner](../../../../../../../docs/architecture/cli/runner.md)

| Step | Member | File |
|------|--------|------|
| 1. The entry point runs the arguments, prints the output, and exits with the code | `Main.main` | [Main.java](Main.java) |
| 2. Flag mode or verb mode is chosen, and a bad argument becomes an error result | `CliRunner.run`, `CliRunner.isFlagMode` | [CliRunner.java](CliRunner.java) |
| 3. The flags are read left to right | `CliRunner.parse` | [CliRunner.java](CliRunner.java) |
| 4. The options hold the seed, the steps, and the lines | `CliOptions` | [CliOptions.java](CliOptions.java) |
| 5. One session is created, and the lines are run on it | `CliRunner.run` | [CliRunner.java](CliRunner.java) |
| 6. A result pairs an exit code with its text | `CliResult` | [CliResult.java](CliResult.java) |

### [Command language](../../../../../../../docs/architecture/cli/language.md)

| Step | Member | File |
|------|--------|------|
| 1. A line is refused when blank, split, and tried as an alias, then dispatched | `CommandDispatch.execute` | [CommandDispatch.java](CommandDispatch.java) |
| 2. The aliases are answered, each with its arity checked | `CommandDispatch.tryAlias`, `CommandDispatch.oldDiag` | [CommandDispatch.java](CommandDispatch.java) |
| 3. Help, list, and noun-verb lines go to their handlers | `CommandDispatch.dispatch` | [CommandDispatch.java](CommandDispatch.java) |
| 4. A root's children are named | `CommandDispatch.listNoun` | [CommandDispatch.java](CommandDispatch.java) |
| 5. A path is read and summarised | `CommandDispatch.getNoun`, `CommandDispatch.summarize` | [CommandDispatch.java](CommandDispatch.java) |
| 6. The session is advanced by N | `CommandDispatch.advanceNoun` | [CommandDispatch.java](CommandDispatch.java) |
| 7. A collector is switched, cleared, or listed | `CommandDispatch.diagControl` | [CommandDispatch.java](CommandDispatch.java) |
| 8. One cell is inspected | `CommandDispatch.cellInspect` | [CommandDispatch.java](CommandDispatch.java) |
| 9. The help catalogue or one topic is returned | `CommandDispatch.help` | [CommandDispatch.java](CommandDispatch.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [Main.java](Main.java) | The program's entry point | `Main`, `main` |
| [CliRunner.java](CliRunner.java) | Turns an invocation into lines on one session | `CliRunner`, `run`, `parse`, `isFlagMode` |
| [CliOptions.java](CliOptions.java) | The parsed flags of one invocation | `CliOptions` |
| [CliResult.java](CliResult.java) | An exit code and its text | `CliResult`, `isOk` |
| [CommandDispatch.java](CommandDispatch.java) | The command language shared with the studio's terminal | `CommandDispatch`, `execute`, `dispatch`, `help` |
