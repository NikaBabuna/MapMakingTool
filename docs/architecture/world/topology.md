<!--
  File: docs/architecture/world/topology.md
  Purpose: Topology — neighbours and one-step motion on the rectangle that stands for a sphere
  Audience: Agents and humans
  Update when: What the topology computes, or the order of its steps, changes
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

## Procedure

1. The antipode of a column is $\operatorname{ap}(x)$.
2. A neighbour is the cell itself for $d = (0, 0)$; otherwise the column wraps, then the row steps or, past a pole, the neighbour is the antipodal column of the wrapped column on the polar row.
3. A move returns the moved cell and the velocity after the move, $(n_x, n_y, v'_x, v'_y)$, negating the velocity when the move crossed a pole.
4. The four orthogonal directions are kept in the order of $D_4$.

## What is true afterwards

Every cell has four orthogonal neighbours on the map. A neighbour across a pole lies on the same polar row, so on the polar rows the "north" or "south" neighbour of a cell is the cell opposite it. When $W$ is even, $\operatorname{ap}(\operatorname{ap}(x)) = x$, so crossing a pole and crossing back returns to the start; the two map sizes the product uses are even. With $W = 1$ the antipode of every column is itself, so a step over a pole returns the same cell.

## Cost

Both functions are $O(1)$.

Code: [world/topology/](../../../product/src/main/java/com/aethelgard/product/world/topology/README.md)  
Parent: [one generation](README.md). Why the map is a sphere on a rectangle: [ADR-012](../../paperwork/decisions/ADR-012-simulation-runner.md).
