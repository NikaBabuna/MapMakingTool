<!--
  File: docs/product/wiki/elevation.md
  Purpose: Domain rule for plates seed and collision-uplift elevation
  Audience: Agents and humans
  Update when: The first elevation process changes
-->

# Elevation process

Relief is **caused** by two plates grinding at a suture. It is not painted at Step 0.

Category: `world/tectonics`. The product emission policy ticks this category after Step 0. A tectonics System claims the tick and applies the rule below.

---

## Step 0 — plates

A **plates** layer (same width and height as elevation) stores an integer **plate id** per cell.

There are two plates.

- If `width == 1`: no suture. Every cell is plate `0`.
- Otherwise the vertical boundary is  
  `xBoundary = 1 + floorMod(seed, width - 1)`  
  (`floorMod` is the non-negative remainder).
- Cells with `x < xBoundary` are plate `0`.
- Cells with `x >= xBoundary` are plate `1`.

**Elevation at Step 0 is still every cell `0`.** Plates are initial conditions. The seed is used only to place this suture; it is not stored as its own Pool field.

---

## Later Steps — collision uplift

Each generation Step (every `advance` after create):

1. Read standing `plates` and `elevation`.
2. For every cell, if any **4-neighbor** (north, east, south, west) has a **different plate id**, that cell’s elevation increases by **1**.
3. Write the new elevation grid. Plates do not move.

Interior of each plate stays at its previous height (0 until a later process). The two columns that touch across the suture form a ridge of height `N` after `N` generation Steps.

A 1-cell-wide world has no foreign neighbor, so elevation stays 0.

---

## Engine

`ProductHost` wires the category tree, `GenerationTickPolicy` (no tick on Step 0), and the tectonics `EngineSystem`. Ordinary world rules do not edit `engine` source.
