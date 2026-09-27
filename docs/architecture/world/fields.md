<!--
  File: docs/architecture/world/fields.md
  Purpose: Fields — the value types a world is made of, and their invariants
  Audience: Agents and humans
  Update when: A world value type, a field name, or an invariant a phase relies on changes
-->

# Fields

A world is nine named values held by the engine: grids over the map, and tables over plates and over crust columns. Each value is immutable, so a phase never changes a value it reads; it builds a new one and stages it.

## What it reads

Nothing. These are value types; the phases and the seed build them.

## What it writes

Values. Every constructor copies its arrays. Refusals: a `Grid` with no rows, an empty row, or rows of unequal length; a `WorldSpec` or an all-zero grid with a side below 1; a velocity component outside $\{-1, 0, 1\}$; a negative area; tables of unequal length or of length 0; and a read outside a grid or a table each throw `IllegalArgumentException`. Counting a registry throws `IllegalStateException` when a cell holds a plate id outside the velocity table.

## Model

The map is the cell set $\Omega = \{0, \dots, W-1\} \times \{0, \dots, H-1\}$ of a `WorldSpec` $(W, H, s)$ with $W, H \ge 1$; $x$ is the column and $y$ the row. The world after generation $g$ is

$$\mathcal{W}_g = \bigl(P,\; v,\; R,\; K,\; \Delta,\; \iota,\; O,\; T,\; E\bigr),$$

with the plate map $P : \Omega \to \{0, \dots, N-1\}$; the velocities $v_p = (v^x_p, v^y_p) \in \{-1, 0, 1\}^2$ for $p < N$; the registry $R = (A_p, v_p)_{p<N}$ with areas $A_p = |P^{-1}(p)|$; the contacts $K$ ([boundaries](boundaries.md)); the budgets $\Delta$ and intents $\iota$ ([interaction](interaction.md)); the occupancy $O : \Omega \to \{0, \dots, L-1\}$; the locker thickness table $T : \{0, \dots, L-1\} \to \mathbb{Z}$; and the elevation $E : \Omega \to \mathbb{Z}$. The seed $s$ travels with every velocity table and registry.

Occupancy keys are row-major cell indices at step 0:

$$\mathrm{id}(x, y) = y\,W + x, \qquad L_0 = W H .$$

The phases keep these invariants after every generation:

$$\text{(I1)}\;\; P(c) \in [0, N) \;\; \forall c \in \Omega, \qquad \text{(I2)}\;\; \textstyle\sum_{p} A_p = W H,\;\; A_p \ge 0, \qquad \text{(I3)}\;\; v_p \in \{-1,0,1\}^2,\;\; R.v_p = v_p,$$

$$\text{(I4)}\;\; O(c) \in [0, L) \;\; \forall c \in \Omega, \qquad \text{(I5)}\;\; E(c) = T(O(c)) - T_{\mathrm{ocean}} .$$

A plate may end a generation with $A_p = 0$; the next generation's renumbering removes it ([fission](motion/fission.md)). Two constants classify crust: $T_{\mathrm{ocean}} = 8$ is the thickness of new ocean, and a locker with $T \ge T_{\mathrm{land}} = 16$ is continental.

## Procedure

1. A run is sized by a `WorldSpec`. The default spec is $8 \times 8$ with seed 0, the fixture of the dumps; the view spec is $1920 \times 1080$ with seed 0, the map window.
2. The nine field names are the constants of `WorldFields`: `ELEVATION`, `PLATES`, `PLATE_VELOCITY`, `PLATE_REGISTRY`, `BOUNDARIES`, `AREA_FLUX`, `MOTION_INTENT`, `OCCUPANCY`, `LOCKERS`.
3. A `Grid` holds an `int` per cell, row-major, copied on construction. It can be built all zero, it is read with a bounds check, and two grids are equal when every cell is.
4. A `PlateVelocities` table holds the seed and $v^x, v^y$. It is read per plate, and it tells whether some plate has a non-zero component. Its site-motion helpers — the column $(x_i + g\,v^x_i) \bmod W$, the row clamped to $[0, H-1]$, and a deprecated wrap — are not called by the pipeline.
5. A `PlateRegistry` holds the seed, $A$, and a copy of $v$. It is counted from a plate map $P$ and velocities $v$; it can take new velocities while keeping its areas; and it is read per plate, with the total area $\sum_p A_p$.
6. The occupancy key of a cell is its row-major index, and there are $WH$ keys at step 0.
7. A `Lockers` table holds the thickness of every crust column. It is built as $n$ columns at $T_{\mathrm{ocean}}$, it can be extended by $n$ more such columns (the same table when $n = 0$), and it is read per column.

## What is true afterwards

Every value can be shared between the Pool, a snapshot, and a caller, because none can change after construction. Two values of the same type are equal exactly when their contents are equal, so a world dump can compare fields cell for cell.

Code: [world/fields/](../../../product/src/main/java/com/aethelgard/product/world/fields/README.md)  
Parent: [one generation](README.md). How the first world is built from a seed: [seed](seed.md).
