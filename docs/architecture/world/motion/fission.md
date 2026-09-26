<!--
  File: docs/architecture/world/motion/fission.md
  Purpose: ApplyGeometry.fissionAndCrumbs, absorbCrumbs, remapDense — splitting plates that fell apart, absorbing tiny pieces, and renumbering the survivors
  Audience: Agents and humans
  Update when: ApplyGeometry.fissionAndCrumbs, absorbCrumbs, longestNeighbor, floodComponent, remapDense, or CRUMB_DENOMINATOR changes
-->

# Fission, crumbs, and remap

When sinking and refilling cut a plate in two, the pieces go their own ways as separate plates, each keeping the drift of the plate it came from. A piece too small to matter is swallowed by the neighbour it touches most. Finally the plate ids are renumbered without gaps, and a plate that lost every cell disappears.

## What it reads

The working grid $C$ after the [flood](flood.md), with no empty cell, and the staged velocities $v$ of [integrate](integrate.md).

## What it writes

The dense plate grid, its velocities, and its registry, returned as the `Result` of `apply`. The stage throws `IllegalStateException` if an empty cell remains, or if no plate is left.

## Model

**Components.** For a plate $p$, the pieces of $p$ are the connected components of $C^{-1}(p)$ under the four-neighbour adjacency $\nu$ of the [topology](../topology.md), so pieces join across the east–west seam and across the poles. Let $M$ be the largest id in $C$ and $Q^p_0, Q^p_1, \dots$ the pieces of $p$ in the order the row-major scan first meets them. For $p = 0, \dots, M$:

$$C(Q^p_0) = p, \qquad C(Q^p_j) = \mathrm{new}_{p,j}, \quad v_{\mathrm{new}_{p,j}} = v_p \quad (j \ge 1),$$

where the new ids are $M+1, M+2, \dots$ in order of $p$, then $j$. An id above the velocity table gets $v = (0, 0)$.

`ApplyGeometry.fissionAndCrumbs` (the split) in [`ApplyGeometry.java`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java):

```java
for (int c = 1; c < components.size(); c++) {
  int newId = nextId++;
  vx.add(vx.get(plate));
  vy.add(vy.get(plate));
  for (int[] cell : components.get(c)) {
    cells[cell[1]][cell[0]] = newId;
  }
}
```

**Crumbs.** Then every piece $Q$ of every plate, met in scan order, is a crumb when

$$|Q| \cdot 10^4 < W H ,$$

that is, smaller than 0.01% of the map: at most 207 cells on the $1920 \times 1080$ window, and never on the $8 \times 8$ fixture. A crumb takes the id of the plate it shares the most edges with, the lowest id on a tie:

$$C(Q) := \min \operatorname*{arg\,max}_{q \ne C(Q)} \bigl|\{\, (c, d) \in Q \times D_4 : C(\nu(c, d)) = q \,\}\bigr| ,$$

and keeps its id when it touches no other plate.

`ApplyGeometry.absorbCrumbs` (the bar) in [`ApplyGeometry.java`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java):

```java
if (component.size() * (long) CRUMB_DENOMINATOR >= world) {
  continue;
}
```

**Remap.** With the areas $a_q = |C^{-1}(q)|$ and the living ids $q_0 < q_1 < \dots < q_{N'-1}$ with $a_q > 0$, the dense grid is $P'(c) = i$ where $C(c) = q_i$, with $v'_i = v_{q_i}$ and $A'_i = a_{q_i}$. The registry of the result carries these areas, counted before advection.

## Procedure

1. `fissionAndCrumbs` finds the largest id, extends the velocity lists to cover it, and for each original plate collects its pieces with `floodComponent`, a depth-first search over same-id neighbours. It gives every piece after the first a new id with the parent's velocity. [`ApplyGeometry.fissionAndCrumbs`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java), [`ApplyGeometry.floodComponent`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
2. `absorbCrumbs` scans the grid, collects every piece once, and relabels each piece below the bar `CRUMB_DENOMINATOR` to its `longestNeighbor`. [`ApplyGeometry.absorbCrumbs`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java), [`ApplyGeometry.longestNeighbor`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
3. The velocities of the pass travel as a `Lifecycle` record. [`ApplyGeometry.Lifecycle`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
4. `remapDense` counts the areas, drops the ids with none, renumbers the survivors in increasing order, and builds the grid, the velocities, and the registry, with `toArray` turning the lists into tables. [`ApplyGeometry.remapDense`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).

## What is true afterwards

Plate ids are $0, \dots, N'-1$ with no gap, and every plate is one connected piece of at least one cell: fission leaves one piece per id, and a crumb joins a plate it touches. Every new plate drifts like the plate it split from. Ids are assigned by scan order, so a plate's id can change from one generation to the next even when nothing happens to it.

## Cost

$O(WH)$ for the component searches and the remap, plus $O(WH)$ for the crumb scan.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Fission, crumbs, remap | `ApplyGeometry` | `fissionAndCrumbs`, `absorbCrumbs`, `longestNeighbor`, `floodComponent`, `remapDense`, `CRUMB_DENOMINATOR`, `Lifecycle`, `toArray` | [`product/src/main/java/com/aethelgard/product/ApplyGeometry.java`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java) |

Parent: [motion](README.md).
