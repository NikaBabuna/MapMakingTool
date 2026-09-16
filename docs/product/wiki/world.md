<!--
  File: docs/product/wiki/world.md
  Purpose: Domain definition of World, grid, layer, and Step-0 elevation
  Audience: Agents and humans
  Update when: World geometry or layer semantics change
-->

# World

A **World** is a rectangular **grid** of cells plus named **layers** of data on that grid.

It is not the engine heartbeat counter. It lives in Pool typed fields that `ProductHost` declares.

---

## Grid

- **Width** — cell count east–west (`x` in `0 .. width-1`).
- **Height** — cell count north–south (`y` in `0 .. height-1`; `y` is row index, increasing downward in storage).
- Both must be at least 1.
- Geometry is shared: every layer has the same width and height.

A `WorldSpec` also records a long **seed**. F-014 does not use the seed to paint cells. Later generation Steps may.

---

## Layer

A **layer** is one named field on the Pool whose value is a `Grid` of the world geometry.

Later climate (rainfall, temperature, …) is more layers of the same shape, not a second world object.

---

## Elevation (Step 0)

The first layer is **`elevation`**: integer height per cell.

At Step 0 this is **initial condition**, not a finished map. Every cell starts at **0**. Relief is produced by a later generative process (F-015), not copied in from config as the final heightmap.

---

## Engine

`ProductHost` wires `elevation` into `EngineSetup`’s field schema and seeds the zero grid through `EngineConfig.initialFields`. Ordinary world rules do not edit `engine` source.
