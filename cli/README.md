<!--
  File: cli/README.md
  Purpose: Door to the cli module: running a world with no window, in the command language the studio's terminal shares
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# CLI module

Runs one world with no window: one invocation builds one session, runs the lines it was given in the shared command language, prints what they return, and exits with a status code.

**Paper:** [One run of the CLI](../docs/architecture/cli/README.md) · **Conventions:** [conventions.md](../docs/architecture/conventions.md)

## Why

A world can be driven and printed from a script or a terminal without starting the studio, which keeps checks and experiments cheap. This module holds the runner and the command language; the world and its session come from the product module, and the studio only reuses the language.

## How it works

The code is one package, [the command line](src/main/java/com/aethelgard/cli/README.md), under `src/main/java/com/aethelgard/cli/`: the runner turns the arguments into lines on one session, and `CommandDispatch` answers each line. Its tests are [the command-line tests](src/test/java/com/aethelgard/cli/README.md), under `src/test/java/com/aethelgard/cli/`.

**Start reading at:** `Main.main` in [Main.java](src/main/java/com/aethelgard/cli/Main.java).

## Depends on

- [product](../product/README.md) — the session, and the values the commands print

## Used by

- [ui](../ui/README.md) — the studio's terminal sends its lines to the same command language

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [pom.xml](pom.xml) | The module's build: its artifact, and its dependencies on the product and the engine | — |
| `src/` | The code and the tests, introduced above; exempt: layout segment | — |
| `target/` | What the build writes; exempt: build output | — |
