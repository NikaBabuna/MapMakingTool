<!--
  File: cli/src/test/java/com/aethelgard/cli/README.md
  Purpose: Door to the tests of the command line: one run, and the command language
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Command-line tests

Proves what one run of the command line does — its default, its flags on one world, and its refusals — and the command language it shares with the studio's terminal.

**Paper:** [Runner](../../../../../../../docs/architecture/cli/runner.md) · [Command language](../../../../../../../docs/architecture/cli/language.md) · **Conventions:** [conventions.md](../../../../../../../docs/architecture/conventions.md)

## Why

The command line and the terminal both depend on the command language answering exactly as its catalogue says, so that is proved beside the code that answers. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

Both are JUnit 5 classes; each test's `@DisplayName` states the outcome it proves. `CliRunnerTest` calls the runner with argument vectors and checks the output and the exit code. `CommandLanguageTest` sends lines to a dispatcher on a session and checks every noun and verb, the aliases, the diag controls, the refusals, and that concurrent advances serialise.

**Start reading at:** `CommandLanguageTest` in [CommandLanguageTest.java](CommandLanguageTest.java).

## Depends on

- [the command line](../../../../../main/java/com/aethelgard/cli/README.md) — the code under test
- [product session/](../../../../../../../product/src/main/java/com/aethelgard/product/session/README.md), [product session diagnostics/](../../../../../../../product/src/main/java/com/aethelgard/product/session/diagnostics/README.md), [product world/fields/](../../../../../../../product/src/main/java/com/aethelgard/product/world/fields/README.md) — the session and the values the tests compare

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [CliRunnerTest.java](CliRunnerTest.java) | Proves what one run of the command line does: its default, its flags on one world, and its refusals | `CliRunnerTest` |
| [CommandLanguageTest.java](CommandLanguageTest.java) | Proves the command language the CLI and the studio terminal share: its catalogue, aliases, diag controls, refusals, and serialized advances | `CommandLanguageTest` |
