<!--
  File: engine/README.md
  Purpose: Door to the engine module: the host that runs any step-based simulation
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Engine module

A host for any step-based simulation: it holds a Pool of named fields, lets pluggable systems propose new values when their events are claimed, and merges the proposals by a rule chosen per field, one step at a time. It knows nothing of the world it runs.

**Paper:** [One engine step](../docs/architecture/engine/README.md) · **Conventions:** [conventions.md](../docs/architecture/conventions.md)

## Why

Keeping the stepper free of any world lets the product change its whole model without touching the engine, and keeps the engine's promises — one snapshot per step, repeatable runs, no silent event — provable on their own. Nothing of plates, maps, or windows belongs here; it lives in the product and ui modules. The engine depends on no other module of this repository.

## How it works

The code is under `src/main/java/com/aethelgard/engine/`, one package per mechanism of the paper:

1. [pool/](src/main/java/com/aethelgard/engine/pool/README.md) — the engine that runs each step, its setup, the Pool, and the compute and emission inside each update; the entry point
2. [events/](src/main/java/com/aethelgard/engine/events/README.md) — categories, the event buffer, and claiming by ancestry
3. [systems/](src/main/java/com/aethelgard/engine/systems/README.md) — claiming systems, their sub-systems and order, and the claim/finish barrier
4. [merge/](src/main/java/com/aethelgard/engine/merge/README.md) — the field schema, provenanced writes, and the merge rules
5. [user/](src/main/java/com/aethelgard/engine/user/README.md) — the input register, the frozen input view, and the view port
6. [diagnostics/](src/main/java/com/aethelgard/engine/diagnostics/README.md) — the report port and its bindings

The mechanism packages are peers: the pool calls the others, and systems and the user view read the pool's snapshot ([conventions](../docs/architecture/conventions.md)). The tests are under `src/test/java/com/aethelgard/engine/`, [one test package per engine package](src/test/java/com/aethelgard/engine/README.md).

**Start reading at:** `Engine.create` in [Engine.java](src/main/java/com/aethelgard/engine/pool/Engine.java).

## Depends on

- nothing in this repository

## Used by

- [product](../product/README.md) — the world plugs its fields, phases, and tick into the engine, and the session drives it

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [pom.xml](pom.xml) | The module's build: its artifact, and its one library, the SLF4J API | — |
| `src/` | The code and the tests, introduced above; exempt: layout segment | — |
| `target/` | What the build writes; exempt: build output | — |
