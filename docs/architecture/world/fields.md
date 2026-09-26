<!--
  File: docs/architecture/world/fields.md
  Purpose: WorldSpec, WorldFields, Grid, PlateVelocities, PlateRegistry, Occupancy, Lockers — the value types a world is made of, and their invariants
  Audience: Agents and humans
  Update when: A world value type, a field name, or an invariant a phase relies on changes
-->

# Fields

A world is nine named values held by the engine: grids over the map, and tables over plates and over crust columns. Each value is immutable, so a phase never changes a value it reads; it builds a new one and stages it.

## What it reads

Nothing. These are value types; the phases and the seed build them.

## What it writes

Values. Every constructor copies its arrays. Refusals: a `Grid` with no rows, an empty row, or rows of unequal length; a `WorldSpec` or `Grid.zeros` with a side below 1; a velocity component outside $\{-1, 0, 1\}$; a negative area; tables of unequal length or of length 0; and a read outside a grid or a table each throw `IllegalArgumentException`. `PlateRegistry.from` throws `IllegalStateException` when a cell holds a plate id outside the velocity table.

## Model

The map is the cell set $\Omega = \{0, \dots, W-1\} \times \{0, \dots, H-1\}$ of a `WorldSpec` $(W, H, s)$ with $W, H \ge 1$; $x$ is the column and $y$ the row. The world after generation $g$ is

$$\mathcal{W}_g = \bigl(P,\; v,\; R,\; K,\; \Delta,\; \iota,\; O,\; T,\; E\bigr),$$

with the plate map $P : \Omega \to \{0, \dots, N-1\}$; the velocities $v_p = (v^x_p, v^y_p) \in \{-1, 0, 1\}^2$ for $p < N$; the registry $R = (A_p, v_p)_{p<N}$ with areas $A_p = |P^{-1}(p)|$; the contacts $K$ ([boundaries](boundaries.md)); the budgets $\Delta$ and intents $\iota$ ([interaction](interaction.md)); the occupancy $O : \Omega \to \{0, \dots, L-1\}$; the locker thickness table $T : \{0, \dots, L-1\} \to \mathbb{Z}$; and the elevation $E : \Omega \to \mathbb{Z}$. The seed $s$ travels with every velocity table and registry.

Occupancy keys are row-major cell indices at step 0:

$$\mathrm{id}(x, y) = y\,W + x, \qquad L_0 = W H .$$

`Occupancy.id` in [`Occupancy.java`](../../../product/src/main/java/com/aethelgard/product/Occupancy.java):

```java
public static int id(int x, int y, int width) {
  return y * width + x;
}
```

The phases keep these invariants after every generation:

$$\text{(I1)}\;\; P(c) \in [0, N) \;\; \forall c \in \Omega, \qquad \text{(I2)}\;\; \textstyle\sum_{p} A_p = W H,\;\; A_p \ge 0, \qquad \text{(I3)}\;\; v_p \in \{-1,0,1\}^2,\;\; R.v_p = v_p,$$

$$\text{(I4)}\;\; O(c) \in [0, L) \;\; \forall c \in \Omega, \qquad \text{(I5)}\;\; E(c) = T(O(c)) - T_{\mathrm{ocean}} .$$

A plate may end a generation with $A_p = 0$; the next generation's renumbering removes it ([fission](motion/fission.md)). Two constants classify crust: $T_{\mathrm{ocean}} = 8$ is the thickness of new ocean, and a locker with $T \ge T_{\mathrm{land}} = 16$ is continental.

## Procedure

1. A run is sized by a `WorldSpec`. `DEFAULT` is $8 \times 8$ with seed 0, the fixture of the dumps; `VIEW` is $1920 \times 1080$ with seed 0, the map window. [`WorldSpec`](../../../product/src/main/java/com/aethelgard/product/WorldSpec.java).
2. The nine field names are the constants of `WorldFields`: `ELEVATION`, `PLATES`, `PLATE_VELOCITY`, `PLATE_REGISTRY`, `BOUNDARIES`, `AREA_FLUX`, `MOTION_INTENT`, `OCCUPANCY`, `LOCKERS`. [`WorldFields`](../../../product/src/main/java/com/aethelgard/product/WorldFields.java).
3. A `Grid` holds an `int` per cell, row-major, copied on construction. `zeros` builds the all-zero grid, `get(x, y)` reads with a bounds check, and equality compares every cell. [`Grid`](../../../product/src/main/java/com/aethelgard/product/Grid.java).
4. A `PlateVelocities` holds the seed and $v^x, v^y$. `vx`, `vy`, and `count` read it, and `anyMoving` is true when some plate has a non-zero component. The site-motion helpers `movedSiteX` (column $(x_i + g\,v^x_i) \bmod W$, through `wrapX`), `movedSiteY` (row clamped to $[0, H-1]$, through `clampY`), and the deprecated `wrap` are not called by the pipeline. [`PlateVelocities`](../../../product/src/main/java/com/aethelgard/product/PlateVelocities.java).
5. A `PlateRegistry` holds the seed, $A$, and a copy of $v$. `from(P, v)` counts the cells of every plate. `withVelocities(R, v)` keeps the areas of $R$ and takes the velocities of $v$. `area`, `vx`, `vy`, `count`, `seed`, and `totalArea` ($\sum_p A_p$) read it. [`PlateRegistry.from`](../../../product/src/main/java/com/aethelgard/product/PlateRegistry.java).
6. `Occupancy.id` is the row-major key of a cell, and `Occupancy.count` is $WH$. [`Occupancy.id`](../../../product/src/main/java/com/aethelgard/product/Occupancy.java).
7. A `Lockers` table holds the thickness of every crust column. `oceanic(n)` is $n$ columns at $T_{\mathrm{ocean}}$. `appendOceanic(n)` returns a table with $n$ more columns at $T_{\mathrm{ocean}}$, or the same table when $n = 0$. `thickness`, `thicknesses`, and `count` read it. [`Lockers`](../../../product/src/main/java/com/aethelgard/product/Lockers.java).

## What is true afterwards

Every value can be shared between the Pool, a snapshot, and a caller, because none can change after construction. Two values of the same type are equal exactly when their contents are equal, so a world dump can compare fields cell for cell.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Map size | `WorldSpec` | `WorldSpec`, `width`, `height`, `seed`, `DEFAULT`, `VIEW` | [`product/src/main/java/com/aethelgard/product/WorldSpec.java`](../../../product/src/main/java/com/aethelgard/product/WorldSpec.java) |
| Field names | `WorldFields` | `ELEVATION`, `PLATES`, `PLATE_VELOCITY`, `PLATE_REGISTRY`, `BOUNDARIES`, `AREA_FLUX`, `MOTION_INTENT`, `OCCUPANCY`, `LOCKERS` | [`product/src/main/java/com/aethelgard/product/WorldFields.java`](../../../product/src/main/java/com/aethelgard/product/WorldFields.java) |
| Grid | `Grid` | `Grid`, `zeros`, `width`, `height`, `get`, `equals`, `hashCode` | [`product/src/main/java/com/aethelgard/product/Grid.java`](../../../product/src/main/java/com/aethelgard/product/Grid.java) |
| Velocities | `PlateVelocities` | `PlateVelocities`, `count`, `vx`, `vy`, `anyMoving`, `movedSiteX`, `movedSiteY`, `wrapX`, `clampY`, `wrap` | [`product/src/main/java/com/aethelgard/product/PlateVelocities.java`](../../../product/src/main/java/com/aethelgard/product/PlateVelocities.java) |
| Registry | `PlateRegistry` | `PlateRegistry`, `withVelocities`, `from`, `seed`, `count`, `area`, `vx`, `vy`, `totalArea` | [`product/src/main/java/com/aethelgard/product/PlateRegistry.java`](../../../product/src/main/java/com/aethelgard/product/PlateRegistry.java) |
| Occupancy keys | `Occupancy` | `id`, `count` | [`product/src/main/java/com/aethelgard/product/Occupancy.java`](../../../product/src/main/java/com/aethelgard/product/Occupancy.java) |
| Crust columns | `Lockers` | `Lockers`, `T_OCEAN`, `T_LAND`, `oceanic`, `appendOceanic`, `count`, `thickness`, `thicknesses` | [`product/src/main/java/com/aethelgard/product/Lockers.java`](../../../product/src/main/java/com/aethelgard/product/Lockers.java) |

Parent: [one generation](README.md). How the first world is built from a seed: [seed](seed.md).
