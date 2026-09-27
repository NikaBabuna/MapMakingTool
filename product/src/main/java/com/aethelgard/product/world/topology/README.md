<!--
  File: product/src/main/java/com/aethelgard/product/world/topology/README.md
  Purpose: Door to the sphere topology of the map: how the rectangle's edges and poles join
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Topology

Says how the rectangular map joins as a sphere: which cell lies next to which, across the east–west seam and across the poles, and where a moving cell lands.

**Paper:** [Topology](../../../../../../../../../docs/architecture/world/topology.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

Every phase that looks at a neighbour or moves a cell must agree on the same geometry, or contacts found by one phase would not match the cells another phase moves. So the geometry is one small package that everything below the wiring may call and that calls nothing. It holds no world state; values belong in `fields/`.

## How it works

`SphereTopology` is a set of pure functions. Columns wrap: stepping east from the last column reaches the first. Rows do not wrap: stepping north from the top row crosses the pole and lands in the top row again, at the antipode column `antipodeX`; the south pole works the same way on the bottom row. `neighbor` applies one step, such as one of the four orthogonal steps of `ORTHO`, with those rules. `advectCell` moves a cell by a whole velocity and returns the landing cell and the velocity after the move, which is turned round when the move crossed a pole.

**Start reading at:** `SphereTopology.neighbor` in [SphereTopology.java](SphereTopology.java).

## Depends on

- nothing in this repository

## Used by

- [boundaries/](../boundaries/README.md) — contacts are found through the east and south neighbours
- [motion/](../motion/README.md) — advection moves cells with `advectCell`
- [crust/](../crust/README.md) — margins, collisions, precedence, and subduction look at neighbours
- [ui](../../../../../../../../../ui/README.md) — the plates view finds contact cells through the same neighbours

## Where each step happens

### [Topology](../../../../../../../../../docs/architecture/world/topology.md)

| Step | Member | File |
|------|--------|------|
| 1. The antipode of a column | `SphereTopology.antipodeX` | [SphereTopology.java](SphereTopology.java) |
| 2. One step to a neighbour: wrap the column, then step the row or cross the pole | `SphereTopology.neighbor` | [SphereTopology.java](SphereTopology.java) |
| 3. A cell moved by a velocity, and the velocity after a pole crossing | `SphereTopology.advectCell` | [SphereTopology.java](SphereTopology.java) |
| 4. The four orthogonal steps | `SphereTopology.ORTHO` | [SphereTopology.java](SphereTopology.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [SphereTopology.java](SphereTopology.java) | The neighbours and moves of a cell on the sphere-shaped map | `SphereTopology`, `neighbor`, `advectCell`, `antipodeX`, `ORTHO` |
