<!--
  File: docs/product/wiki/tectonics.md
  Purpose: G-008 boundary tectonics domain locks (Pool fields, Systems, partition, fission)
  Audience: Agents and humans
  Update when: G-008 tectonics decisions or implementation Steps change
-->

# Boundary tectonics (G-008)

**Doc status:** F-030 locks (approved). **Code status:** not implemented — runtime still follows [elevation.md](elevation.md) (Voronoi 6–15, Constant velocities, advection kinematics) until F-031–F-038.

This page is the physics + Pool/System plan for **G-008**. When a later Step lands, update the **Code status** banner and retire conflicting lines in elevation.md.

---

## World geometry

| Item | Lock |
|------|------|
| **VIEW** | **1920×1080** cells, seed `0` for the product window (`WorldSpec.VIEW`). |
| **DEFAULT** | Dump / fast tests may stay a small rectangle (e.g. 8×8); not required to be 1920×1080. |
| **Topology** | **Torus** — both axes wrap with `floorMod`. Neighbors, site distance, pan, and ownership treat opposite edges as adjacent. |
| **Sphere analogue** | The looping rectangle *is* the finite closed surface for this Goal (not a 3D globe mesh). |

**Runtime note:** VIEW is **1920×1080** in code (**F-031**). Boundary tectonics behavior still follows [elevation.md](elevation.md) until F-033–F-038.

---

## Initial plates (Step 0)

Full cover of the torus: every cell has exactly one plate id. Shapes should look realistic (irregular, varied sizes).

### Count

\[
N = 12 + \mathrm{floorMod}(\mathrm{seed}, 13)
\]

So \(N \in \{12,\ldots,24\}\).

### Sites

Same SplitMix / `mix(seed, siteIndex, axis)` family as today’s wiki (see [elevation.md](elevation.md) constants), positions:

- \(x_i = \mathrm{floorMod}(\mathrm{mix}(\mathrm{seed}, i, 0), \mathrm{width})\)
- \(y_i = \mathrm{floorMod}(\mathrm{mix}(\mathrm{seed}, i, 1), \mathrm{height})\)

### Assignment

For each cell, nearest site under **toroidal** Euclidean distance (minimum-image on the torus). Squared distance in 64-bit ints. **Ties → lower site index.**

Elevation at Step 0 remains **0** everywhere.

### Velocities (initial only)

Registry may store an initial `(vx, vy)` per plate for Step 0. **Constant-forever random velocities are retired.** Edge-driven integration is **F-037**. Until then, document intent only; do not pretend F-020 Constant field is the end state.

---

## Actors and interactions

Plates are the only tectonic actors. **Number, motion, and size** come from boundary work.

### Boundary kinds

| Kind | Area flux | Motion feedback (intent) | Relief (orogeny) |
|------|-----------|---------------------------|------------------|
| **Separate** (diverge) | Create crust; both sides can grow | Ridge push (away) | Typically lower |
| **Collide** (converge) | Destroy crust into **sink**; loser shrinks | Dampen closing; slab-style pull on loser | Uplift / trench |
| **Pass-by** (transform) | ≈ none | Slide; little normal change | Little |

### Collide precedence (v1)

When types are equal (no oceanic/continental yet): **smaller plate by area loses** (subducts / is consumed). Ties: lower plate id loses.

### Number

| Event | Rule |
|-------|------|
| **Death** | Plate **area → 0** → remove from registry |
| **Fission** | If a plate’s cells become **disconnected** (4-connected on the torus), each component becomes its own plate (new ids; inherit velocity with documented perturbation later) |
| **Crumb absorb** | After fission, any component with area **&lt; 0.05%** of `width × height` is absorbed into the neighboring plate that shares the longest contact (deterministic tie: lower neighbor id) |

Intentional rift-fracture birth beyond pinch-fission may wait if Steps stay small.

---

## Planned Pool fields

| Field | Shape | Merge intent | Role |
|-------|-------|--------------|------|
| `plates` | grid int | Static | Surface ownership (or sink/none sentinel if needed) |
| `elevation` | grid int | Static | Relief symptom |
| `plate_registry` | object map id → area, velocity, flags… | Static | Actors |
| `boundaries` | object (edge list / ribbon) | Static | Classified contacts |
| `area_flux` | object | Static | Create/destroy budgets before geometry apply |
| `motion_intent` | object | Static | Preferred Δv / v from edges (may fold into registry later) |
| `tectonic_events` | object ledger | Static | Birth/death/split for next-Step emission / debug |

`plate_velocity` as a **Constant** forever field is **superseded** by registry + F-037.

---

## Planned Systems / Sub-Systems

Category tree (product-authored), under `world/tectonics/…`:

```text
world/tectonics/
  boundaries/
  interaction/
  geometry/
  motion/
  lifecycle/
  orogeny/
```

**Preferred assembly:** one fat tectonics `EngineSystem` with ordered Sub-Systems (same-Step staging), plus **orogeny** on standing boundaries (sibling System or lagged end of pipeline). `EventEmissionPolicy` may emit leaf categories / lifecycle events from `tectonic_events` on later Steps.

| Stage | Sub-Systems (names intent) | Writes |
|-------|----------------------------|--------|
| Boundaries | TraceContacts, ClassifyEdges | `boundaries` |
| Interaction | Precedence, FluxBudget, Resistance | `area_flux`, `motion_intent` |
| Geometry | ApplyFlux, FloodAssign, Connectivity (fission/death/crumbs), RegistryUpdate | `plates`, `plate_registry`, `tectonic_events` |
| Motion | IntegrateVelocity | `plate_registry` velocities |
| Lifecycle | FractureDetect / Spawn (optional thin) | registry / plates |
| Orogeny | ReliefFromBoundaries | `elevation` |

Implementation Steps: **F-034–F-038**.

---

## Studio UI

- **F-032:** loopback pan + zoom clamp (fit min) — shipped in `ui/web`.
- **F-039 / F-040:** multi-panel mappy studio + traditional console — planned.
