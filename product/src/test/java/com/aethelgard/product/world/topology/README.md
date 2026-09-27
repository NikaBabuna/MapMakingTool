<!--
  File: product/src/test/java/com/aethelgard/product/world/topology/README.md
  Purpose: Door to the tests of the sphere topology
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Topology tests

Proves how the map joins as a sphere: columns wrap east to west, and a pole joins each cell to its antipode.

**Paper:** [Topology](../../../../../../../../../docs/architecture/world/topology.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

The topology is shared by tracing, advection, and the crust phases, so its rules are proved on their own, in the package of the code they drive. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`SphereTopologyTest` is a JUnit 5 class of three tests. It calls `SphereTopology` directly for the seam and the poles, and also runs tracing and advection on small grids to see that a contact and a moving cell cross the seam and the poles as the rules say.

**Start reading at:** `SphereTopologyTest` in [SphereTopologyTest.java](SphereTopologyTest.java).

## Depends on

- [world/topology/](../../../../../../../main/java/com/aethelgard/product/world/topology/README.md) — the code under test
- [world/fields/](../../../../../../../main/java/com/aethelgard/product/world/fields/README.md), [world/boundaries/](../../../../../../../main/java/com/aethelgard/product/world/boundaries/README.md), [world/motion/](../../../../../../../main/java/com/aethelgard/product/world/motion/README.md) — the grids, the trace, and the advection the tests run across the seam and the poles

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [SphereTopologyTest.java](SphereTopologyTest.java) | Proves how the map joins as a sphere: columns wrap east to west, and a pole joins each cell to its antipode | `SphereTopologyTest` |
