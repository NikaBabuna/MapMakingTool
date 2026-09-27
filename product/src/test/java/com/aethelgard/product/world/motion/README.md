<!--
  File: product/src/test/java/com/aethelgard/product/world/motion/README.md
  Purpose: Door to the tests of plate motion
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Motion tests

Proves how plates move: velocities nudged by intent, rifts filled by their own plates, split plates renumbered, and crumbs absorbed.

**Paper:** [Motion](../../../../../../../../../docs/architecture/world/motion/README.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

Which plate owns a cell is decided in one package, so its outcomes are proved together, in the package of the code they drive. Being in that package also lets a test call the package-private gap flood of advection directly. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`PlateMotionTest` is a JUnit 5 class of six tests. It integrates velocities, runs the geometry pass and advection on small grids, fills gaps with `PlateKinematics.fillUnresolvedFlood`, and advances seeded sessions, then checks the velocities, the owners of cells, and the plate numbers that result.

**Start reading at:** `PlateMotionTest` in [PlateMotionTest.java](PlateMotionTest.java).

## Depends on

- [world/motion/](../../../../../../../main/java/com/aethelgard/product/world/motion/README.md) — the code under test
- [world/fields/](../../../../../../../main/java/com/aethelgard/product/world/fields/README.md), [world/boundaries/](../../../../../../../main/java/com/aethelgard/product/world/boundaries/README.md), [world/interaction/](../../../../../../../main/java/com/aethelgard/product/world/interaction/README.md) — the values, contacts, and budgets the tests build
- [session/](../../../../../../../main/java/com/aethelgard/product/session/README.md) — the session that runs the seeded worlds

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [PlateMotionTest.java](PlateMotionTest.java) | Proves how plates move: velocities nudged by intent, rifts filled by their own plates, split plates renumbered, crumbs absorbed | `PlateMotionTest` |
