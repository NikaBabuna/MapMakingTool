<!--
  File: docs/product/wiki/tectonics.md
  Purpose: G-008 boundary tectonics domain locks (Pool fields, Systems, partition, fission)
  Audience: Agents and humans
  Update when: G-008 tectonics decisions or implementation Steps change
-->

# Boundary tectonics (G-008)

**Doc status:** F-046 crumb 0.01% + phase collectors; F-045 sphere polar wrap; F-044 flood + bold borders. F-041 G-009 locks. Prior: F-030 / F-034 cylinder / F-036 B1. **Code status:** Sphere-on-rectangle F-045; flood fill F-044; crumb 0.01% F-046; session `DiagnosticsHub` F-042/F-046.

This page is the physics + Pool/System plan for boundary tectonics. When a later Step lands, update the **Code status** banner and retire conflicting lines in elevation.md.

---

## World geometry

| Item | Lock |
|------|------|
| **VIEW** | **1920×1080** cells, seed `0` for the product window (`WorldSpec.VIEW`). |
| **DEFAULT** | Dump / fast tests may stay a small rectangle (e.g. 8×8); not required to be 1920×1080. |
| **Topology (G-009)** | **Sphere-on-rectangle** — **X wraps** with `floorMod`; **polar wrap on Y**: crossing north re-enters from the **north** at antipodal longitude (`x + width/2`) with heading flip of **both** `vx` and `vy` (same for south). Neighbors, site distance (Step-0 still B1 cylindrical), advection, fission, and camera agree. Not a 3D globe mesh. |
| **Topology (runtime)** | **F-045 live** — `SphereTopology` shared helper. Cylinder hard-Y **retired**. |
| **History** | Earlier G-008 text said **torus**; amended F-034 to cylinder; F-041 amends to sphere polar wrap; **F-045** implements it. |

**Runtime note:** VIEW is **1920×1080** (F-031). B1 plates + boundaries + flux/intent + integrate + apply/fission + boundary orogeny live (F-033–F-038). **Sphere polar wrap live (F-045).**

---

## Initial plates (Step 0)

Full cover of the cylinder: every cell has exactly one plate id. Shapes should look realistic (irregular, varied sizes).

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

For each cell, nearest site under **B1** latitude-weighted cylindrical distance (wrap X; flat Y; east–west scaled by `cosQ(y)` at the query row):

\[
\cos_Q(y)=\max\bigl(1,\mathrm{round}(1024\cdot\sin(\pi\cdot(y+0.5)/H))\bigr)
\]

\[
d^2=\bigl(\mathrm{wrap}(x-s_x)\cdot\cos_Q(y)/1024\bigr)^2+(y-s_y)^2
\]

Squared distance in 64-bit ints. **Ties → lower site index.**

Elevation at Step 0 remains **0** everywhere.

### Velocities (initial only)

Registry may store an initial `(vx, vy)` per plate for Step 0. **Constant-forever random velocities are retired.** Each generation, `IntegrateVelocity` nudges standing velocities from `motion_intent`: `v' = clamp(v + sgn(intent), -1, 1)` (all-stop → plate 0 `(1,0)`).

---

## Actors and interactions

Plates are the only tectonic actors. **Number, motion, and size** come from boundary work.

### Boundary kinds

| Kind | Area flux | Motion feedback (intent) | Relief (orogeny) |
|------|-----------|---------------------------|------------------|
| **Separate** (diverge) | Create crust; **both contacting sides only** (ridge accretion) | Ridge push (away) | Typically lower |
| **Collide** (converge) | Destroy crust into **sink**; loser shrinks | Dampen closing; slab-style pull on loser | Uplift / trench |
| **Pass-by** (transform) | ≈ none | Slide; little normal change | Little |

### Diverge / void-fill (G-009)

**Lock:** Gaps opened by SEPARATE must **not** be filled by a nearest arbitrary **third** plate. New crust grows by **iterative flood** from bordering owned cells.

**Code (F-044):** After advection, unique claimants keep ownership; unresolved cells fill by flood:
1. Cells with **exactly one** distinct owned neighbor expand from that plate (repeat until stable).
2. Remaining cells with **two or more** owned neighbors take **longest orthogonal contact**, then **lower plate id**.
3. Cells with **zero** owned neighbors **wait** until a frontier appears.
4. Full cover required — no permanent unowned cells.

This fixes the F-043 miss at **triple junctions**: a gap between left and right that also touches an upper plate is not wrapped by the upper plate. Global nearest-site / nearest-owner refill remains **removed**.

**Prior (F-043):** SEPARATE-pair subset ridge check then neighbor flood — incorrect when a gap cell touched three plates (ridge bailed; flood could assign the third).

### Collide precedence (v1)

When types are equal (no oceanic/continental yet): **smaller plate by area loses** (subducts / is consumed). Ties: lower plate id loses.

### Number

| Event | Rule |
|-------|------|
| **Death** | Plate **area → 0** → remove from registry |
| Fission | If a plate’s cells become **disconnected** (4-connected on the **sphere map** — wrap X; polar wrap Y via `SphereTopology`), each component becomes its own plate (new ids; inherit velocity). |
| **Crumb absorb** | After fission, any component with area **&lt; 0.01%** of `width × height` is absorbed into the neighboring plate that shares the longest contact (deterministic tie: lower neighbor id). (F-046; was 0.1% after F-045 / 0.2% F-044; original G-008 was 0.05%.) |

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

`plate_velocity` is STATIC; seeded at Step 0 then rewritten by `IntegrateVelocity` + fission remap (F-036/F-037).

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
| Interaction | Precedence, FluxBudget, Resistance (`BoundaryInteraction`) | `area_flux`, `motion_intent` |
| Geometry | ApplyFlux, FloodAssign, Connectivity (fission/death/crumbs), RegistryUpdate | `plates`, `plate_registry`, `tectonic_events` |
| Motion | IntegrateVelocity | `plate_registry` velocities |
| Lifecycle | FractureDetect / Spawn (optional thin) | registry / plates |
| Orogeny | ReliefFromBoundaries | `elevation` |

Implementation Steps: **F-034–F-038**.

---

## Studio UI

- **Camera:** **X wrap**; **dark blank N/S** margins (no vertical loop tiles). Zoomed out: map centered. Zoomed in: free pan **between** top and bottom edges — cannot pan past the map band. Sphere physics still uses antipodal polar wrap (`SphereTopology`); view does not.
- **F-039:** multi-panel mappy studio.
- **F-040:** traditional terminal console (`aethelgard>`); **G-008 closed**.
- **G-009:** shared CLI/terminal command surface (**F-048**–**F-049**); scrap console chrome F-050; runner chrome + perf panels (F-051–F-053).
- **F-046:** crumb absorb **0.01%**; session phase collectors (`phase.trace` … `phase.orogeny`) on `DiagnosticsHub`.
