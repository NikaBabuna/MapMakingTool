<!--
  File: docs/architecture/world/boundaries.md
  Purpose: TraceBoundaries, Boundaries, BoundaryContact, BoundaryKind, Orogeny.closing — phase 1: finding and classifying every contact between two plates
  Audience: Agents and humans
  Update when: Boundaries.trace, Boundaries.classify, Orogeny.closing, or TraceBoundaries.execute changes
-->

# Boundaries

Wherever two cells of different plates touch, the plates either close on each other, pull apart, or slide past. This phase walks the map once, finds every such touching pair, and labels it by how the two plates move relative to each other across that edge.

## What it reads

The settled `plates` and `plate_velocity`. It does not read staging, so it describes the world as the last generation left it.

## What it writes

`boundaries`: a `Boundaries` list of `BoundaryContact`s, each with its cell $(x, y)$, its direction $n = (n_x, n_y)$, the plate $a$ of the cell, the plate $b$ of the neighbour, and a `BoundaryKind`. A contact with $n = (0, 0)$ throws `IllegalArgumentException`; a settled field of the wrong type throws `IllegalStateException`.

## Model

A contact is traced from every cell toward its east and its south neighbour only, $n \in \{(1,0), (0,1)\}$, so each interior edge of the map is seen once:

$$K = \Bigl(\,(c,\, n,\, P(c),\, P(\nu(c, n)),\, \kappa) \;:\; c \in \Omega \text{ in row-major order},\; n \in \bigl((1,0),(0,1)\bigr),\; \nu(c,n) \ne c,\; P(\nu(c,n)) \ne P(c)\Bigr).$$

The kind comes from the closing rate, the relative velocity of $a$ toward $b$ projected on the direction from $a$'s cell to $b$'s cell:

$$\chi = n \cdot (v_a - v_b) = n_x\,(v^x_a - v^x_b) + n_y\,(v^y_a - v^y_b), \qquad \kappa = \begin{cases} \textsf{COLLIDE} & \chi > 0 \\ \textsf{SEPARATE} & \chi < 0 \\ \textsf{PASS\_BY} & \chi = 0 . \end{cases}$$

`Orogeny.closing` in [`Orogeny.java`](../../../product/src/main/java/com/aethelgard/product/Orogeny.java):

```java
if (plateA == plateB) {
  return 0;
}
int dvx = velocities.vx(plateA) - velocities.vx(plateB);
int dvy = velocities.vy(plateA) - velocities.vy(plateB);
return nx * dvx + ny * dvy;
```

`Boundaries.classify` in [`Boundaries.java`](../../../product/src/main/java/com/aethelgard/product/Boundaries.java):

```java
int closing = Orogeny.closing(plateA, plateB, velocities, nx, ny);
if (closing > 0) {
  return BoundaryKind.COLLIDE;
}
if (closing < 0) {
  return BoundaryKind.SEPARATE;
}
return BoundaryKind.PASS_BY;
```

**Polar rows.** The south step of a cell on the last row crosses the south pole to the antipodal cell of the same row ([topology](topology.md)). For even $W$, the cells $(x, H-1)$ and $(\operatorname{ap}(x), H-1)$ are each other's south neighbour, so every such pair of different plates is traced twice, once from each side, with the same $n = (0, 1)$. The two contacts have opposite closing rates, so a pair with $v^y_a \ne v^y_b$ yields one `COLLIDE` and one `SEPARATE` contact. The first row has no north step, so no contact crosses the north pole.

## Procedure

1. `TraceBoundaries.execute` reads the settled plates and velocities, checks their types, and stages `Boundaries.trace` of them under `boundaries`. [`TraceBoundaries.execute`](../../../product/src/main/java/com/aethelgard/product/TraceBoundaries.java).
2. `trace` visits the cells row by row, and for each cell the east step, then the south step, each through `SphereTopology.neighbor`. It skips a step that returns the cell itself or a cell of the same plate. [`Boundaries.trace`](../../../product/src/main/java/com/aethelgard/product/Boundaries.java).
3. `classify` computes $\chi$ with `Orogeny.closing` and returns the kind. [`Boundaries.classify`](../../../product/src/main/java/com/aethelgard/product/Boundaries.java).
4. Each contact is stored as an immutable `BoundaryContact` with its direction, not its neighbour's coordinates; a reader recovers the neighbour with `SphereTopology.neighbor`. [`BoundaryContact`](../../../product/src/main/java/com/aethelgard/product/BoundaryContact.java).
5. The list is copied into an immutable `Boundaries`, which answers `contacts`, `size`, and `count` of one kind. [`Boundaries.count`](../../../product/src/main/java/com/aethelgard/product/Boundaries.java).

## What is true afterwards

The staged contacts describe the settled plates and velocities of the world before this generation moves anything. Later phases that need those contacts read this staged value: interaction, apply, orogeny, and, through apply, subduction. Margin relief and continental collision trace again, after the move ([margin](crust/margin.md), [collide](crust/collide.md)). A contact's kind depends only on the two velocities and the direction, never on the crust.

## Cost

$O(WH)$ time; the list holds one entry per edge between two plates.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Phase | `TraceBoundaries` | `TraceBoundaries.id`, `TraceBoundaries.writeRanges`, `TraceBoundaries.execute` | [`product/src/main/java/com/aethelgard/product/TraceBoundaries.java`](../../../product/src/main/java/com/aethelgard/product/TraceBoundaries.java) |
| Contact list | `Boundaries` | `Boundaries`, `empty`, `trace`, `classify`, `contacts`, `size`, `count` | [`product/src/main/java/com/aethelgard/product/Boundaries.java`](../../../product/src/main/java/com/aethelgard/product/Boundaries.java) |
| Contact | `BoundaryContact` | `BoundaryContact`, `x`, `y`, `nx`, `ny`, `plateA`, `plateB`, `kind` | [`product/src/main/java/com/aethelgard/product/BoundaryContact.java`](../../../product/src/main/java/com/aethelgard/product/BoundaryContact.java) |
| Kind | `BoundaryKind` | `SEPARATE`, `COLLIDE`, `PASS_BY` | [`product/src/main/java/com/aethelgard/product/BoundaryKind.java`](../../../product/src/main/java/com/aethelgard/product/BoundaryKind.java) |
| Closing rate | `Orogeny` | `closing` | [`product/src/main/java/com/aethelgard/product/Orogeny.java`](../../../product/src/main/java/com/aethelgard/product/Orogeny.java) |

Parent: [one generation](README.md). What the contacts drive next: [interaction](interaction.md). Whether the polar double contact is intended: [open questions](../open-questions.md).
