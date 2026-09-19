<!--
  File: docs/product/wiki/world.md
  Purpose: Domain definition of World, grid, layer, and Step-0 elevation
  Audience: Agents and humans
  Update when: World geometry or layer semantics change
-->

# World

**Code status (through F-037):** VIEW launch is **1920×1080**; cylindrical plates + registry + boundaries + area_flux + motion_intent + IntegrateVelocity — see [elevation.md](elevation.md) / [tectonics.md](tectonics.md).
**G-008 further targets:** orogeny rewrite / studio panels — see [tectonics.md](tectonics.md).

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
| `WorldSpec.VIEW` | Product window | **1920×1080**, seed 0 (**F-031**) | same |

A `WorldSpec` also records a long **seed**. Under G-008 the seed places **initial plate sites** (toroidal nearest-site; N = 12–24) — [tectonics.md](tectonics.md). It does not paint elevation.

### Topology (G-008)

**Cylinder:** wrap X (`floorMod`); Y does **not** wrap (polar edges). Sphere-on-rectangle analogue — not a 3D mesh.

---

## Layer

A **layer** is one named field on the Pool. Grid layers share world geometry. Per-plate actor data lives in a **registry** object under G-008 (not a forever-Constant velocity field).

Later climate (rainfall, temperature, …) is more layers of the same shape, not a second world object.

### Code today (F-036)

| Field | Step 0 | Later Steps |
|-------|--------|-------------|
| `elevation` | every cell `0` | orogeny — [elevation.md](elevation.md) |
| `plates` | B1 nearest-site (N=12–24, wrap X) | apply flux/fission then advection |
| `plate_registry` | STATIC area + `(vx,vy)` | refreshed after integrate + geometry |
| `boundaries` | STATIC classified contacts | refreshed each generation |
| `area_flux` | STATIC Δarea + sinkΔ | refreshed; **applied** each generation |
| `motion_intent` | STATIC preferred Δv | refreshed; drives IntegrateVelocity |
| `plate_velocity` | STATIC `(vx,vy)` in `{-1,0,1}` | integrate each generation; fission remap |

### G-008 planned fields

See [tectonics.md](tectonics.md) — `tectonic_events` still planned. Edge-driven velocity integrate shipped in F-037.

---

## Elevation (Step 0)

The first relief layer is **`elevation`**: integer height per cell.

At Step 0 this is **initial condition**, not a finished map. Every cell starts at **0**. Relief is produced by tectonics (today: [elevation.md](elevation.md); G-008: boundary orogeny in F-038).

---

## Engine

`ProductHost` wires layers into `EngineSetup`’s field schema and seeds grids through `EngineConfig.initialFields`. `WorldDump` formats a settled snapshot for tests and later observers. Ordinary world rules do not edit `engine` source.
