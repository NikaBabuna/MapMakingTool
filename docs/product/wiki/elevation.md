<!--
  File: docs/product/wiki/elevation.md
  Purpose: Domain rule for Voronoi plates, kinematics, and motion-based orogeny
  Audience: Agents and humans
  Update when: The elevation process changes
-->

# Elevation process

> **Code status (through F-038):** Step-0 plates use **B1** latitude-weighted cylindrical nearest-site (N=12–24). Boundaries + flux/intent + IntegrateVelocity + ApplyGeometry; **orogeny from standing `boundaries`** (not velocity-neighbor closing). `plate_velocity` STATIC.  
> **G-008:** Boundary tectonics **supersedes** Constant-forever velocities and advection-as-size-engine. Target rules: [tectonics.md](tectonics.md).

Relief is **caused** by plates converging and diverging at sutures. It is not painted at Step 0.

Category: `world/tectonics`. The product emission policy ticks this category after Step 0. Two Systems claim the tick: **kinematics** (writes `plates`) and **tectonics** (`TraceBoundaries` + `Orogeny`). They read the same standing Pool snapshot; neither sees the other's output this Step.

---

## Step 0 — plates and velocities

A **plates** layer (same width and height as elevation) stores an integer **plate id** per cell.

Plate count and sites are a deterministic function of `WorldSpec.seed`. Each cell belongs to the **nearest site** under **cylindrical** distance (G-008 / F-033–F-034). Authority: [tectonics.md](tectonics.md).

A **plate_velocity** field stores one integer `(vx, vy)` per plate. It is **STATIC**: seeded at Step 0, nudged by `IntegrateVelocity` from `motion_intent` (F-037), rewritten on fission/death; advected ownership still uses it. A **plate_registry** STATIC object mirrors area + velocities. A **boundaries** STATIC object lists classified contacts. **`area_flux`** / **`motion_intent`** hold budgets (F-035); flux is **applied** in F-036. `plates` is **Static**.

### Count

\[
N = 12 + \mathrm{floorMod}(\mathrm{seed}, 13)
\]

So \(N\) is an integer in **12–24**. `floorMod` is Java `Math.floorMod` (non-negative remainder). \(N\) is the **site count**. On a tiny grid some sites may share a cell or win no cells; occupied plate ids may be fewer than \(N\).

### Site placement (SplitMix)

All arithmetic is Java `long` two’s-complement wrapping. `>>>` is unsigned shift.

Constants:

- `GOLDEN = 0x9E3779B97F4A7C15`
- `SILVER = 0xBF58476D1CE4E5B9`
- `BRONZE = 0x94D049BB133111EB`

```
splitmix64(z):
  z = z + GOLDEN
  z = (z ^ (z >>> 30)) * SILVER
  z = (z ^ (z >>> 27)) * BRONZE
  z = z ^ (z >>> 31)
  return z

mix(seed, siteIndex, axis):
  z = seed
  z = z ^ (siteIndex as long * GOLDEN)
  z = z ^ (axis as long * SILVER)
  return splitmix64(z)
```

Site \(i\) for \(i = 0 .. N-1\):

- \(x_i = \mathrm{floorMod}(\mathrm{mix}(\mathrm{seed}, i, 0), \mathrm{width})\)
- \(y_i = \mathrm{floorMod}(\mathrm{mix}(\mathrm{seed}, i, 1), \mathrm{height})\)

Axis `0` is east–west; axis `1` is north–south.

### Assignment

For each cell \((x, y)\), take the site \(i\) with the smallest **cylindrical** Euclidean distance (wrap X; flat Y). Compare squared distance as 64-bit integers (no square root). **Ties take the lower site index.**

The cell’s plate id is that site index.

**Elevation at Step 0 is still every cell `0`.** Plates and velocities are initial conditions. The seed is used to place sites and velocities; it is not stored as its own Pool field (the Constant `plate_velocity` object records the seed so kinematics can move sites).

### Velocities

Each site \(i\) gets integer components in `{-1, 0, 1}`:

- \(v_{x,i} = \mathrm{floorMod}(\mathrm{mix}(\mathrm{seed}, i, 2), 3) - 1\)
- \(v_{y,i} = \mathrm{floorMod}(\mathrm{mix}(\mathrm{seed}, i, 3), 3) - 1\)

Axis `2` is \(v_x\); axis `3` is \(v_y\).

If every plate would be \((0, 0)\), plate `0` is forced to \((1, 0)\). Not every plate is stationary.

---

## Later Steps — kinematics and standing orogeny

Each generation Step (every `advance` after create) both Systems run against the **standing** plates from the previous Step.

### Kinematics (advection)

Generation index \(G\) is `1` on the first tectonics tick, `2` on the next, and so on (Pool heartbeat value after that Step’s update, minus one, under the default host compute).

1. Translate each cell by its plate’s \((v_x, v_y)\): **wrap X**; **Y off-map claims are dropped** (polar edge).
2. If exactly one cell claims a destination, that destination keeps the claimant’s plate id.
3. Leftover cells (zero claimants or two or more) take the **nearest moved site**, cylindrical distance, lower index on ties — the same rule as Step-0 partition.

Moved site \(i\) after \(G\) generation Steps:

- \(x_i(G) = \mathrm{floorMod}(x_i + G \cdot v_{x,i}, \mathrm{width})\)
- \(y_i(G) = \mathrm{floorMod}(y_i + G \cdot v_{y,i}, \mathrm{height})\)

This supersedes “plates do not move” from G-004 / F-017.

### Orogeny (standing boundaries)

1. Read standing `plates`, `elevation`, `plate_registry`, and this Step’s `boundaries` (from Trace on standing plates).
2. For each cell, look at contacts that touch it:
   - any **COLLIDE** where this cell’s plate is the **winner** → `+1`
   - else any **COLLIDE** where this cell’s plate is the **loser** → `−1`
   - else any **SEPARATE** → `−1`
   - else `0` (PASS_BY or interior)
3. Loser = smaller registry area; area ties → lower plate id.
4. Write the new elevation grid. **No floor** — elevation may go negative.

This supersedes F-021 velocity-neighbor closing for elevation (classification still uses closing when tracing boundaries).

A world whose standing assignment is a single plate (for example a 1×1 grid) has no foreign neighbor, so elevation stays 0.

### Map display (F-022)

The grid may be negative. The **UI** paints `e < 0` as ocean, hillshades land, and can show plates / overlay. Paint formulas live in [architecture.md](../architecture.md), not in this physics rule. Interior cells are unchanged **in the grid** even when the window shows ocean.

---

## Engine

`ProductHost` wires the category tree, `GenerationTickPolicy` (no tick on Step 0), the kinematics `EngineSystem`, and the tectonics `EngineSystem` (`Orogeny`). Ordinary world rules do not edit `engine` source.
