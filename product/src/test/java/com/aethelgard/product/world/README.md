<!--
  File: product/src/test/java/com/aethelgard/product/world/README.md
  Purpose: Door to the tests of the world's wiring: step 0, and a whole generation against the reference pipeline
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# World tests

Proves what the world's wiring does: what a new world is at step 0, and what one whole generation does, phase by phase and against the reference pipeline.

**Paper:** [Seed](../../../../../../../../docs/architecture/world/seed.md) · [Wiring](../../../../../../../../docs/architecture/world/wiring.md) · [Reference pipeline](../../../../../../../../docs/architecture/world/reference.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

A test lives in the package of the code it drives, so these two drive the wiring in [world/](../../../../../../main/java/com/aethelgard/product/world/README.md); the tests of one mechanism live in that mechanism's test package below. They run the code and read no document. Each test names, in a comment beside it, the requirements it proves and the Step record that holds them.

## How it works

Both are JUnit 5 classes; each test's `@DisplayName` states the outcome it proves. `WorldCreationTest` builds worlds with `ProductHost.create` and a session, and checks their size, plates, crust, and velocities at step 0. `GenerationTest` advances sessions and checks the order of the nine phases, that the engine's generation equals `ProductGeneration.advance`, isostasy, determinism, and that land forms and rides; one test compares a dump with the stored world in `src/test/resources/worlds/`.

**Start reading at:** `GenerationTest` in [GenerationTest.java](GenerationTest.java).

## Depends on

- [world/](../../../../../../main/java/com/aethelgard/product/world/README.md) — `ProductHost` and `ProductGeneration`, the code under test
- [world/fields/](../../../../../../main/java/com/aethelgard/product/world/fields/README.md) — the values the tests build and compare
- [session/](../../../../../../main/java/com/aethelgard/product/session/README.md) — the session that runs the generations

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [WorldCreationTest.java](WorldCreationTest.java) | Proves what a new world is at step 0: its size, its plates, flat ocean crust, and seeded velocities | `WorldCreationTest` |
| [GenerationTest.java](GenerationTest.java) | Proves what a whole generation does: nine phases in order, the same result as the reference pipeline, isostasy, determinism, and land that forms and rides | `GenerationTest` |
| [topology/](topology/README.md) | The tests of the sphere topology | — |
| [boundaries/](boundaries/README.md) | The tests of the contacts | — |
| [motion/](motion/README.md) | The tests of plate motion | — |
| [crust/](crust/README.md) | The tests of the crust | — |
