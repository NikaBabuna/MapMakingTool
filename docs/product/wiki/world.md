<!--
  File: docs/product/wiki/world.md
  Purpose: Domain definition of World, grid, layer, and Step-0 elevation
  Audience: Agents and humans
  Update when: World geometry or layer semantics change
-->

# World

**Code status (through F-029):** VIEW launch is still **512×512** in code; plates follow [elevation.md](elevation.md).  
**G-008 target (F-030 lock):** VIEW **1920×1080**, **torus** topology, boundary tectonics — see [tectonics.md](tectonics.md). Runtime size change is **F-031**.

A **World** is a rectangular **grid** of cells plus named **layers** of data on that grid.

It is not the engine heartbeat counter. It lives in Pool typed fields that `ProductHost` declares.

---

## Grid

- **Width** — cell count east–west (`x` in `0 .. width-1`).
- **Height** — cell count north–south (`y` in `0 .. height-1`; `y` is row index, increasing downward in storage).
- Both must be at least 1.
- Geometry is shared: every layer has the same width and height.

### Specs

| Spec | Role | Code (F-030) | G-008 target |
|------|------|--------------|--------------|
| `WorldSpec.DEFAULT` | Dump / fast tests | Small (e.g. 8×8), seed 0 | May stay small |
| `WorldSpec.VIEW` | Product window | **Still 512×512 until F-031** | **1920×1080**, seed 0 |

A `WorldSpec` also records a long **seed**. Under G-008 the seed places **initial plate sites** (toroidal nearest-site; N = 12–24) — [tectonics.md](tectonics.md). It does not paint elevation.

### Topology (G-008)

**Torus:** both axes wrap (`floorMod`). The finite looping rectangle is the closed surface for this Goal (sphere analogue — not a 3D mesh).

---

## Layer

A **layer** is one named field on the Pool. Grid layers share world geometry. Per-plate actor data lives in a **registry** object under G-008 (not a forever-Constant velocity field).

Later climate (rainfall, temperature, …) is more layers of the same shape, not a second world object.

### Code today (pre–boundary tectonics)

| Field | Step 0 | Later Steps |
|-------|--------|-------------|
| `elevation` | every cell `0` | orogeny — [elevation.md](elevation.md) |
| `plates` | Voronoi nearest-site (6–15) | kinematics advection |
| `plate_velocity` | Constant `(vx,vy)` in `{-1,0,1}` | unchanged |

### G-008 planned fields

See [tectonics.md](tectonics.md) — `plates`, `elevation`, `plate_registry`, `boundaries`, `area_flux`, `motion_intent`, `tectonic_events`. Constant-forever `plate_velocity` is **superseded**.

---

## Elevation (Step 0)

The first relief layer is **`elevation`**: integer height per cell.

At Step 0 this is **initial condition**, not a finished map. Every cell starts at **0**. Relief is produced by tectonics (today: [elevation.md](elevation.md); G-008: boundary orogeny in F-038).

---

## Engine

`ProductHost` wires layers into `EngineSetup`’s field schema and seeds grids through `EngineConfig.initialFields`. `WorldDump` formats a settled snapshot for tests and later observers. Ordinary world rules do not edit `engine` source.
