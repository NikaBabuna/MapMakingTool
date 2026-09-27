<!--
  File: product/src/test/java/com/aethelgard/product/world/boundaries/README.md
  Purpose: Door to the tests of the contacts between plates
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Boundary tests

Proves how plates meet: every contact is found once and classified, who loses a collision, and each contact's budget and intent.

**Paper:** [Boundaries](../../../../../../../../../docs/architecture/world/boundaries.md) · [Interaction](../../../../../../../../../docs/architecture/world/interaction.md) · [Precedence](../../../../../../../../../docs/architecture/world/crust/precedence.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

The contacts feed every later phase, so how they are found and judged is proved in the package of the code that finds them. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`BoundaryTest` is a JUnit 5 class of four tests. It traces contacts on small grids and on seeded worlds, compares them with the contacts it expects, and checks the loser of a collision and the budgets and intents computed from the contacts. One test, `everyContactIsFoundOnceAndClassified`, fails on purpose: the trace misses contacts across the north pole and finds those across the south pole twice, and the test stays red until the trace is fixed.

**Start reading at:** `BoundaryTest` in [BoundaryTest.java](BoundaryTest.java).

## Depends on

- [world/boundaries/](../../../../../../../main/java/com/aethelgard/product/world/boundaries/README.md) — the code under test
- [world/fields/](../../../../../../../main/java/com/aethelgard/product/world/fields/README.md), [world/interaction/](../../../../../../../main/java/com/aethelgard/product/world/interaction/README.md), [world/crust/](../../../../../../../main/java/com/aethelgard/product/world/crust/README.md) — the grids, the budgets and intents, and the precedence rule the tests check
- [session/](../../../../../../../main/java/com/aethelgard/product/session/README.md) — the session that runs the seeded worlds

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [BoundaryTest.java](BoundaryTest.java) | Proves how plates meet: every contact found once and classified, who loses a collision, and each contact's budget and intent | `BoundaryTest` |
