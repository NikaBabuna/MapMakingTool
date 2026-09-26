<!--
  File: docs/architecture/world/topology.md
  Purpose: SphereTopology — neighbours and one-step motion on the rectangle that stands for a sphere
  Audience: Agents and humans
  Update when: SphereTopology.neighbor, SphereTopology.advectCell, or SphereTopology.antipodeX changes
-->

# Topology

The map is a rectangle that stands for a sphere. Walking off the east edge comes back on the west edge; walking over a pole comes back on the same polar row, on the far side of the world, heading the other way. Every phase that looks at a neighbour or moves a cell uses these rules, so the map has no edge.

## What it reads

A cell $(x, y)$, a direction or a velocity with components in $\{-1, 0, 1\}$, and the map size $W \times H$.

## What it writes

A cell on the map, and, for a move, the velocity after it. Neither function throws, and neither ever returns a cell off the map.

## Model

The antipode of a column is $\operatorname{ap}(x) = (x + \lfloor W/2 \rfloor) \bmod W$, with $\bmod$ the floor modulus. The neighbour of $(x, y)$ in direction $d = (d_x, d_y) \in \{-1,0,1\}^2 \setminus \{(0,0)\}$ is

$$\nu\bigl((x, y), d\bigr) = \begin{cases} \bigl((x + d_x) \bmod W,\; y + d_y\bigr) & 0 \le y + d_y < H \\ \bigl(\operatorname{ap}((x + d_x) \bmod W),\; 0\bigr) & y + d_y < 0 \\ \bigl(\operatorname{ap}((x + d_x) \bmod W),\; H - 1\bigr) & y + d_y \ge H \end{cases}$$

and $\nu(c, (0,0)) = c$. The four orthogonal directions, in the order every flood and search uses them, are $D_4 = \bigl((1,0), (-1,0), (0,1), (0,-1)\bigr)$: east, west, south, north.

A cell of a plate with velocity $v$ moves by

$$\alpha\bigl((x, y), v\bigr) = \begin{cases} \bigl(((x + v_x) \bmod W,\; y + v_y),\; v\bigr) & 0 \le y + v_y < H \\ \bigl((\operatorname{ap}(x + v_x),\; 0),\; -v\bigr) & y + v_y < 0 \\ \bigl((\operatorname{ap}(x + v_x),\; H - 1),\; -v\bigr) & y + v_y \ge H . \end{cases}$$

Crossing a pole keeps the row, jumps to the antipodal column, and negates both velocity components. The rectangle is not a sphere mesh: rows keep their width $W$ at every latitude, and the only latitude correction in the world is the distance weight of the seed ([seed](seed.md)).

`SphereTopology.neighbor` (crossing the north pole) in [`SphereTopology.java`](../../../product/src/main/java/com/aethelgard/product/SphereTopology.java):

```java
if (dy < 0) {
  if (y + dy >= 0) {
    ny = y + dy;
  } else {
    // Cross north pole → antipodal x, stay on north edge row.
    nx = antipodeX(nx, width);
    ny = 0;
  }
```

`SphereTopology.advectCell` in [`SphereTopology.java`](../../../product/src/main/java/com/aethelgard/product/SphereTopology.java):

```java
int nx = Math.floorMod(x + vx, width);
int ny = y + vy;
int ovx = vx;
int ovy = vy;
if (ny < 0) {
  nx = antipodeX(x + vx, width);
  ny = 0;
  ovx = -vx;
  ovy = -vy;
```

## Procedure

1. `antipodeX` returns $\operatorname{ap}(x)$. [`SphereTopology.antipodeX`](../../../product/src/main/java/com/aethelgard/product/SphereTopology.java).
2. `neighbor` returns the cell itself for $d = (0, 0)$; otherwise it wraps the column, then either steps the row or, past a pole, moves to the antipodal column of the wrapped column on the polar row. [`SphereTopology.neighbor`](../../../product/src/main/java/com/aethelgard/product/SphereTopology.java).
3. `advectCell` returns the moved cell and the velocity after the move, $(n_x, n_y, v'_x, v'_y)$, negating the velocity when the move crossed a pole. [`SphereTopology.advectCell`](../../../product/src/main/java/com/aethelgard/product/SphereTopology.java).
4. `ORTHO` lists $D_4$. [`SphereTopology.ORTHO`](../../../product/src/main/java/com/aethelgard/product/SphereTopology.java).

## What is true afterwards

Every cell has four orthogonal neighbours on the map. A neighbour across a pole lies on the same polar row, so on the polar rows the "north" or "south" neighbour of a cell is the cell opposite it. When $W$ is even, $\operatorname{ap}(\operatorname{ap}(x)) = x$, so crossing a pole and crossing back returns to the start; the two map sizes the product uses are even. With $W = 1$ the antipode of every column is itself, so a step over a pole returns the same cell.

## Cost

Both functions are $O(1)$.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Topology | `SphereTopology` | `antipodeX`, `neighbor`, `advectCell`, `ORTHO` | [`product/src/main/java/com/aethelgard/product/SphereTopology.java`](../../../product/src/main/java/com/aethelgard/product/SphereTopology.java) |

Parent: [one generation](README.md). Why the map is a sphere on a rectangle: [ADR-012](../../paperwork/decisions/ADR-012-simulation-runner.md).
