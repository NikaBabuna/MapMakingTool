<!--
  File: docs/product/wiki/world.md
  Purpose: Domain definition of World, grid, layer, and Step-0 elevation
  Audience: Agents and humans
  Update when: World geometry or layer semantics change
-->

# World

**Code status (through F-058):** VIEW **1920×1080**; sphere-on-rectangle polar wrap (F-045); occupancy + lockers; ridge mint; buoyancy COLLIDE; elevation isostasy (\(T_{ocean}=8\), \(T_{land}=16\)).  
**G-010:** F-059 margin relief live; F-058 buoyancy live; suture remaining (F-060). Runtime no longer F-038 elevation paint. **G-008:** complete (cylinder amended by F-045).

A **World** is a rectangular **grid** of cells plus named **layers** of data on that grid.

It is not the engine heartbeat counter. It lives in Pool typed fields that `ProductHost` declares.

---

## Grid

- **Width** — cell count east–west (`x` in `0 .. width-1`).
- **Height** — cell count north–south (`y` in `0 .. height-1`; `y` is row index, increasing downward in storage).
- Both must be at least 1.
- Geometry is shared: every layer has the same width and height.

### Specs

| Spec | Role | Code | Notes |
|------|------|------|-------|
| `WorldSpec.DEFAULT` | Dump / fast tests | Small (e.g. 8×8), seed 0 | May stay small |
| `WorldSpec.VIEW` | Product window | **1920×1080**, seed 0 (**F-031**) | same |

A `WorldSpec` also records a long **seed**. The seed places **initial plate sites** (B1 cylindrical nearest-site at Step 0; N = 12–24) — [tectonics.md](tectonics.md). It does not paint elevation.

### Topology (G-009 live)

**Sphere-on-rectangle (F-045):** wrap X; crossing north/south re-enters from the same pole at antipodal longitude with heading flip. Step-0 B1 partition stays cylindrical. Cylinder hard-Y is **retired** for runtime advection/neighbors/camera.

---

## Layer

A **layer** is one named field on the Pool. Grid layers share world geometry. Per-plate actor data lives in a **registry** object under G-008 (not a forever-Constant velocity field).

Later climate (rainfall, temperature, …) is more layers of the same shape, not a second world object.

### Code today (F-036)

| Field | Step 0 | Later Steps |
|-------|--------|-------------|
| `elevation` | every cell `0` | isostasy of locker thickness at occupancy (F-056); \(T_{ocean}=8\) |
| `plates` | B1 nearest-site (N=12–24, wrap X) | apply flux/fission then advection |
| occupancy | one locker id per cell (`y*W+x`) | remapped with plate motion (F-056) |
| `lockers` | all thickness \(T_{ocean}\) | F-038 stamp ladder on thickness; ride with keys |
| `plate_registry` | STATIC area + `(vx,vy)` | refreshed after integrate + geometry |
| `boundaries` | STATIC classified contacts | refreshed each generation |
| `area_flux` | STATIC Δarea + sinkΔ | refreshed; **applied** each generation |
| `motion_intent` | STATIC preferred Δv | refreshed; drives IntegrateVelocity |
| `plate_velocity` | STATIC `(vx,vy)` in `{-1,0,1}` | integrate each generation; fission remap |

### G-008 planned fields

See [tectonics.md](tectonics.md) — `tectonic_events` still planned. Boundary orogeny shipped in F-038.

---

## Elevation (Step 0)

The first relief layer is **`elevation`**: integer height per cell.

At Step 0 this is **initial condition**, not a finished map. Every cell starts at **0**. Crust is all oceanic thickness \(T_{ocean}=8\); elevation is isostasy of that thickness (F-056). Contact stamps thicken lockers after Step 0.

---

## Engine

`ProductHost` wires layers into `EngineSetup`’s field schema and seeds grids through `EngineConfig.initialFields`. `WorldDump` formats a settled snapshot for tests and later observers. Ordinary world rules do not edit `engine` source.
