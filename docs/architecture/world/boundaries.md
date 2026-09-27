<!--
  File: docs/architecture/world/boundaries.md
  Purpose: Boundaries — phase 1: finding and classifying every contact between two plates
  Audience: Agents and humans
  Update when: What the boundaries phase computes, or the order of its steps, changes
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

**Polar rows.** The south step of a cell on the last row crosses the south pole to the antipodal cell of the same row ([topology](topology.md)). For even $W$, the cells $(x, H-1)$ and $(\operatorname{ap}(x), H-1)$ are each other's south neighbour, so every such pair of different plates is traced twice, once from each side, with the same $n = (0, 1)$. The two contacts have opposite closing rates, so a pair with $v^y_a \ne v^y_b$ yields one `COLLIDE` and one `SEPARATE` contact. The first row has no north step, so no contact crosses the north pole.

## Procedure

1. The phase reads the settled plates and velocities, checks their types, and stages the traced contacts under `boundaries`.
2. The trace visits the cells row by row, and for each cell takes the east step, then the south step, each through the sphere [topology](topology.md). It skips a step that returns the cell itself or a cell of the same plate.
3. Each contact is classified: $\chi$ is computed from the two velocities and the direction, and gives the kind.
4. Each contact is stored as an immutable `BoundaryContact` with its direction, not its neighbour's coordinates; a reader recovers the neighbour through the topology.
5. The contacts are copied into an immutable `Boundaries` list, which answers the contacts, their number, and the number of one kind.

## What is true afterwards

The staged contacts describe the settled plates and velocities of the world before this generation moves anything. Later phases that need those contacts read this staged value: interaction, apply, orogeny, and, through apply, subduction. Margin relief and continental collision trace again, after the move ([margin](crust/margin.md), [collide](crust/collide.md)). A contact's kind depends only on the two velocities and the direction, never on the crust.

## Cost

$O(WH)$ time; the list holds one entry per edge between two plates.

Code: [world/boundaries/](../../../product/src/main/java/com/aethelgard/product/world/boundaries/README.md)  
Parent: [one generation](README.md). What the contacts drive next: [interaction](interaction.md). Whether the polar double contact is intended: [open questions](../open-questions.md).
