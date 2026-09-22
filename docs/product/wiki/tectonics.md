<!--
  File: docs/product/wiki/tectonics.md
  Purpose: G-008 boundary tectonics domain locks (Pool fields, Systems, partition, fission)
  Audience: Agents and humans
  Update when: G-008 tectonics decisions or implementation Steps change
-->

# Boundary tectonics (G-008)

**Doc status:** F-059 margin relief live. F-058 buoyancy + SEPARATE unshare live. F-057 ridge mint live. F-056 occupancy + lockers + isostasy live. F-046 crumb 0.01% + phase collectors; F-045 sphere polar wrap; F-044 flood + bold borders. F-055 G-010 locks. Prior: F-030 / F-034 cylinder / F-036 B1. **Code status:** Occupancy keys remap with plates; gaps and SEPARATE copies mint thin oceanic lockers (\(T_{ocean}=8\)); margin relief then shapes oceanic thickness near splits and collisions; COLLIDE uses buoyancy (\(T_{land}=16\)); locker stamps ride; elevation is isostasy. Sphere-on-rectangle F-045; flood fill F-044. **G-010 remaining:** suture/arc F-060.

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

**Runtime note:** VIEW is **1920×1080** (F-031). B1 plates + boundaries + flux/intent + integrate + apply/fission live (F-033–F-038). **Sphere polar wrap live (F-045).** **F-059 live:** margin relief after mint (rift trough, collide slope, lip blend). **F-058 live:** occupancy remaps; advection gaps and SEPARATE contact-locker copies mint thin oceanic lockers; COLLIDE buoyancy (\(T_{land}=16\)); elevation is isostasy of riding thickness. Suture / arc not implemented until F-060.

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

**G-010 (F-057 live):** relief is **not** standing-cell elevation paint. Thickness lockers ride occupancy; new rift occupancy is **minted** thin ocean; elevation is isostasy. Contact-paint orogeny is **superseded for G-010**. The F-038 stamp ladder now writes locker thickness.

### Diverge / void-fill (G-009)

**Lock:** Gaps opened by SEPARATE must **not** be filled by a nearest arbitrary **third** plate. New crust grows by **iterative flood** from bordering owned cells.

**Code (F-044):** After advection, unique claimants keep ownership; unresolved cells fill by flood:
1. Cells with **exactly one** distinct owned neighbor expand from that plate (repeat until stable).
2. Remaining cells with **two or more** owned neighbors take **longest orthogonal contact**, then **lower plate id**.
3. Cells with **zero** owned neighbors **wait** until a frontier appears.
4. Full cover required — no permanent unowned cells.

This fixes the F-043 miss at **triple junctions**: a gap between left and right that also touches an upper plate is not wrapped by the upper plate. Global nearest-site / nearest-owner refill remains **removed**.

**Prior (F-043):** SEPARATE-pair subset ridge check then neighbor flood — incorrect when a gap cell touched three plates (ridge bailed; flood could assign the third).

### Collide precedence (F-058 live)

**Ocean vs continent first** (per contact **cell**, standing locker thickness). Thickness \(\ge T_{land}=16\) is continental; below is oceanic. Oceanic cell subducts even if its plate is larger. Ocean–ocean keeps smaller plate by area (ties: lower plate id). Continent–continent: neither sinks, 0 flux, 0 stamps (suture is F-060).

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

### G-010 fields (F-056 live)

| Field | Shape | Merge intent | Role |
|-------|-------|--------------|------|
| occupancy keys | grid int | Static (ApplyGeometry remap, then RidgeCreate mint) | Cell → locker id (`plates` remains plate ownership) |
| `lockers` | object table | Static (Orogeny stamps, then RidgeCreate append) | id → thickness |
| `elevation` | grid int | Static (one writer: ThicknessToElevation) | **Derived** isostasy: `thickness − T_ocean` (\(T_{ocean}=8\)) |

\(T_{land}=16\) (F-058). Ridge mint of new lockers is **F-057 / F-058 live** (gaps and SEPARATE copies do not inherit neighbor occupancy).

---

## Crust topology (G-010)

**Lock (F-055 / ADR-013).** **F-058 live:** occupancy remaps; gaps and SEPARATE copies mint \(T_{ocean}\) lockers; buoyancy consume at COLLIDE; locker stamps ride; elevation is isostasy.

Crust is **material**. Occupancy **keys** move with plates. Locker **thickness** moves with those keys. Interiors keep their cargo. Elevation is a **view** of thickness (integer isostasy), not contact-paint \(\pm 1\) on standing coordinates. Contact-paint **Orogeny** is **superseded for G-010** as the elevation author (code F-056).

### Ride

When a cell’s occupancy key remaps, that locker (thickness) appears at the new cell. Land **rides**. The map must not leave a mountain ribbon where the contact used to be.

### Ridge mint (F-057 / F-058 live)

New occupancy at SEPARATE / gaps gets thin **oceanic** crust (\(T_{ocean}\)). Gaps **do not** inherit a neighbor’s mountain (flood of plate id stays; flood of thickness does not). Extra copies of a standing SEPARATE contact locker are unresolved then minted (F-058). `RidgeCreate` appends locker ids in raster order.

### Buoyancy and subduction (F-058 live)

Thickness \(\ge T_{land}=16\) is **continental**; below is **oceanic**. At COLLIDE, **ocean subducts** (loser occupancy becomes the surviving side’s locker). Continental occupancy is **not** deleted because its plate is smaller. Ocean–ocean keeps smaller-loses. Continent–continent is frozen this Goal until F-060. Step 0 is **all oceanic** — no painted cratons.

### Margin relief (F-059 live)

After ridge mint, before isostasy. Post-move contacts. Oceanic lockers within distance 8 of a split are set to `4 + d/2` (4 on the contact, 8 at distance 8). Distance 1..4: a seed hash blends half of those lockers toward the farther neighbor. A collision adds `4 - d` for distance under 4, and the result stays below 16. Thickness \(\ge 16\) is not written. Pass-by writes nothing.

### Suture, arc, cap (F-060)

Ocean–ocean collide may thicken an **arc** on the winner (proto-continents). Continent–continent **suture**: thickness up on both sides; neither locker dies. **No plate-id weld.** Cap / light erosion so suture cannot grow without bound.

### Assembly

One `tectonics` System. Crust Sub-Systems **after** occupancy, same-Step staging:

```text
world/tectonics/
  boundaries/
  interaction/     (amended F-058: crust precedence)
  motion/
  geometry/        occupancy keys
  crust/ridge/     RidgeCreate
  crust/subduct/   Subduct
  crust/suture/    ContinentalCollide
  crust/isostasy/  ThicknessToElevation
```

Not a second `EngineSystem` this Goal. No `engine` source edits. Custom locker merge type allowed in product.

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
| Geometry | ApplyFlux, FloodAssign, Connectivity, RegistryUpdate, occupancy remap | `plates`, occupancy, `plate_registry` |
| Motion | IntegrateVelocity | `plate_registry` velocities |
| Lifecycle | FractureDetect / Spawn (optional thin) | registry / plates |
| Orogeny | ReliefFromBoundaries → locker thickness (F-056) | `lockers` |
| Crust (G-010) | ThicknessToElevation + RidgeCreate + MarginRelief + Subduct live; ContinentalCollide later | derived `elevation`; mint F-057/F-058; margin F-059; subduct F-058; suture F-060 |

Implementation Steps: **F-034–F-038**.

---

## Studio UI

- **Camera:** **X wrap**; **dark blank N/S** margins (no vertical loop tiles). Zoomed out: map centered. Zoomed in: free pan **between** top and bottom edges — cannot pan past the map band. Sphere physics still uses antipodal polar wrap (`SphereTopology`); view does not.
- **F-039:** multi-panel mappy studio.
- **F-040:** traditional terminal console (`aethelgard>`); **G-008 closed**.
- **G-009:** shared CLI/terminal (**F-048**–**F-050**); runner chrome + perf rail (**F-051**–**F-052**); UI infrastructure + QoL (**F-053**).
- **G-010:** occupancy + lockers + isostasy **F-056 live**; ridge mint **F-057 live**; buoyancy + SEPARATE unshare **F-058 live**; margin relief **F-059 live**; suture planned F-060.
- **F-046:** crumb absorb **0.01%**; session phase collectors (`phase.trace` … `phase.orogeny` / `phase.isostasy`) on `DiagnosticsHub`.
