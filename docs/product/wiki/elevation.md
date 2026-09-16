<!--
  File: docs/product/wiki/elevation.md
  Purpose: Domain rule for Voronoi plates seed and collision-uplift elevation
  Audience: Agents and humans
  Update when: The elevation process changes
-->

# Elevation process

Relief is **caused** by plates grinding at sutures. It is not painted at Step 0.

Category: `world/tectonics`. The product emission policy ticks this category after Step 0. A tectonics System claims the tick and applies the rule below.

---

## Step 0 — plates

A **plates** layer (same width and height as elevation) stores an integer **plate id** per cell.

Plate count and sites are a deterministic function of `WorldSpec.seed`. Each cell belongs to the **nearest site** (Voronoi). This supersedes the G-003 two-plate vertical suture.

### Count

\[
N = 6 + \mathrm{floorMod}(\mathrm{seed}, 10)
\]

So \(N\) is an integer in **6–15**. `floorMod` is Java `Math.floorMod` (non-negative remainder). \(N\) is the **site count**. On a tiny grid some sites may share a cell or win no cells; occupied plate ids may be fewer than \(N\).

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

For each cell \((x, y)\), take the site \(i\) with the smallest **Euclidean** distance. Compare squared distance \((x-x_i)^2 + (y-y_i)^2\) as 64-bit integers (no square root). **Ties take the lower site index.**

The cell’s plate id is that site index.

**Elevation at Step 0 is still every cell `0`.** Plates are initial conditions. The seed is used only to place sites; it is not stored as its own Pool field.

---

## Later Steps — collision uplift

Each generation Step (every `advance` after create):

1. Read standing `plates` and `elevation`.
2. For every cell, if any **4-neighbor** (north, east, south, west) has a **different plate id**, that cell’s elevation increases by **1**.
3. Write the new elevation grid. Plates do not move.

Interior of each plate stays at its previous height (0 until a later process). Cells that touch a foreign plate form ridges of height \(n\) after \(n\) generation Steps.

A world whose Voronoi assignment is a single plate (for example a 1×1 grid) has no foreign neighbor, so elevation stays 0.

---

## Engine

`ProductHost` wires the category tree, `GenerationTickPolicy` (no tick on Step 0), and the tectonics `EngineSystem`. Ordinary world rules do not edit `engine` source.
