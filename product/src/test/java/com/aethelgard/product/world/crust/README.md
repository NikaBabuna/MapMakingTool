<!--
  File: product/src/test/java/com/aethelgard/product/world/crust/README.md
  Purpose: Door to the tests of the crust
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Crust tests

Proves what happens to crust: it rides with its plate, rifts make fresh ocean, ocean sinks under continents, arcs and sutures thicken, margins shape the floor, and height is thickness minus 8.

**Paper:** [Crust](../../../../../../../../../docs/architecture/world/crust/README.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

Every rule that changes the crust lives in one package, so its outcomes are proved together, in the package of the code they drive. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`CrustTest` is a JUnit 5 class of twelve tests. It builds small worlds of plates, occupancy, and lockers, runs one crust phase at a time, or the whole reference pipeline through `ProductGeneration.advance`, and checks the thickness, the keys, and the height that result.

**Start reading at:** `CrustTest` in [CrustTest.java](CrustTest.java).

## Depends on

- [world/crust/](../../../../../../../main/java/com/aethelgard/product/world/crust/README.md) — the code under test
- [world/](../../../../../../../main/java/com/aethelgard/product/world/README.md) — the reference pipeline
- [world/fields/](../../../../../../../main/java/com/aethelgard/product/world/fields/README.md), [world/boundaries/](../../../../../../../main/java/com/aethelgard/product/world/boundaries/README.md), [world/interaction/](../../../../../../../main/java/com/aethelgard/product/world/interaction/README.md), [world/motion/](../../../../../../../main/java/com/aethelgard/product/world/motion/README.md) — the values, contacts, budgets, and advection the tests build and run

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [CrustTest.java](CrustTest.java) | Proves what happens to crust: it rides, rifts make fresh ocean, ocean sinks under continents, arcs and sutures thicken, margins shape the floor, and height is thickness minus 8 | `CrustTest` |
