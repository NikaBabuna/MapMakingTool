<!--
  File: docs/architecture/world/motion/flood.md
  Purpose: ApplyGeometry.floodSink — the stage that gives every sunken cell to a bordering plate
  Audience: Agents and humans
  Update when: ApplyGeometry.floodSink, pickFloodOwner, or countContact changes
-->

# Flood

After collisions and rifts, some cells belong to no plate. They are filled from their borders inward: each empty cell goes to the neighbouring plate that surrounds it most, until no empty cell is left. A plate that does not touch a hole never receives any of it.

## What it reads

The working grid $C$ after the [sink and claim](sink.md) stages.

## What it writes

$C$ with no $\bot$ left. If an empty cell has no owned neighbour and never gains one, the stage throws `IllegalStateException`; that happens only when no cell is owned.

## Model

For an empty cell $c$ and a plate $p$, the contact count is the number of its four neighbours that $p$ holds:

$$\mathrm{contact}(c, p) = \bigl|\{\, d \in D_4 : C(\nu(c, d)) = p \,\}\bigr| .$$

A sweep visits the cells in row-major order and, for each empty cell with at least one owned neighbour, sets

$$C(c) := \min \operatorname*{arg\,max}_{p \,\in\, \{C(\nu(c,d)) \,:\, d \in D_4\} \setminus \{\bot\}} \mathrm{contact}(c, p),$$

the most-touching neighbour, the lowest id on a tie. Sweeps repeat until one changes nothing. A sweep updates in place, so a cell filled early in a sweep already counts for the cells after it: the fill advances in scan order, not in rings.

`ApplyGeometry.pickFloodOwner` in [`ApplyGeometry.java`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java):

```java
for (int[] d : DIRS) {
  int[] n = SphereTopology.neighbor(x, y, d[0], d[1], width, height);
  int id = cells[n[1]][n[0]];
  if (id < 0) {
    continue;
  }
  int contact = countContact(cells, width, height, x, y, id);
  if (contact > bestContact || (contact == bestContact && (bestId < 0 || id < bestId))) {
    bestContact = contact;
    bestId = id;
  }
}
return bestId;
```

## Procedure

1. `floodSink` sweeps the grid, and for every empty cell asks `pickFloodOwner` for an owner, until a sweep fills nothing. [`ApplyGeometry.floodSink`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
2. `pickFloodOwner` looks at the four neighbours in the order of $D_4$, and for each owned one counts that plate's contacts with `countContact`. It keeps the highest count, and the lowest id on a tie. [`ApplyGeometry.pickFloodOwner`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
3. A final scan throws if any cell is still empty. [`ApplyGeometry.floodSink`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).

## What is true afterwards

Every cell is owned by a plate that already existed. A hole is shared only among the plates that border it. A plate may now be in several pieces, which the next stage resolves ([fission](fission.md)). Filled cells are not added to the skip mask.

## Cost

Each sweep is $O(WH)$; the number of sweeps is at most one more than the depth of the deepest hole, measured in cells from its border.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Flood | `ApplyGeometry` | `floodSink`, `pickFloodOwner`, `countContact` | [`product/src/main/java/com/aethelgard/product/ApplyGeometry.java`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java) |

Parent: [motion](README.md).
