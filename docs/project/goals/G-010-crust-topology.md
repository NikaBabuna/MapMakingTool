<!--
  File: docs/project/goals/G-010-crust-topology.md
  Purpose: Multi-session Goal — crust that rides plates; continents can form
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-010 — Crust topology

**Status:** `in progress`  
**Engine:** do not edit `engine` source for ordinary feature growth (G-002 host ports). Custom `FieldMergeType` for lockers is allowed in **product**.  
**Prior:** [G-009](G-009-simulation-runner-harden.md) shipped a simulation runner; elevation is still contact-paint orogeny on standing coordinates. Plates move; land does not ride. Collide precedence is area-only (no oceanic/continental crust).

**Approved:** 2026-09-20 (user). Direction: occupancy keys + thickness lockers; crust rides plates; ridge mint thin ocean; buoyancy-aware subduction; continental suture + arc; elevation from isostasy. One tectonics System.

---

## Result we want

When this Goal is `done`:

1. **Crust rides.** Occupancy keys move with plates. Locker thickness moves with those keys. Interiors keep their material. The map no longer paints \(\pm 1\) on last Step’s contact coordinates while plates have already left.
2. **Ridges mint ocean.** New occupancy at SEPARATE / gaps gets **thin oceanic** crust. Gaps do not inherit a neighbor’s mountain.
3. **Buoyancy.** Thickness \(\ge T_{land}\) is continental; below is oceanic. At COLLIDE, **oceanic subducts**; continental does not vanish because its plate is smaller.
4. **Continents can form.** Ocean–ocean collide may thicken an arc. Continent–continent collide **sutures** (thickness up, neither side destroyed). Thick crust stays above water as it rides.
5. **Elevation is a view.** `elevation` is isostasy of thickness at current keys, not a second physics. Cap (and/or light erosion) so suture cannot grow without bound.
6. **One tectonics System.** New Sub-Systems after occupancy, same-Step staging. No `engine` production edits.
7. **Determinism.** Same seed + N Steps → identical occupancy, lockers, elevation. Incremental suite green.

Plain English: land is material on plates. Oceans are born thin at ridges. Continents are thick crust that will not subduct.

---

## Out of scope (this Goal)

- Climate / biomes / wind / rain  
- Explore / Guide / Timeline product modes  
- Second `EngineSystem` for topology; scratch-pad / wait-notify  
- Plate **weld** (ids stay; thickness is the continent)  
- Age, sediment, plumes, Wilson cycle as features  
- True 3D globe mesh  
- Engine framework ports (non-finishing Systems, Delete Request, …)

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Assembly | One `tectonics` System; crust Sub-Systems **after** occupancy |
| State | Occupancy keys + thickness lockers; mint/merge at gaps and subduction |
| Elevation | Derived from thickness (integer isostasy); contact-paint Orogeny retired as author |
| Step 0 | **All oceanic** (\(T_{ocean}\)). Continents emerge from arcs + suture |
| Weld | **No** plate-id merge on continent–continent |
| Precedence | Ocean vs continent first; smaller-loses remains for ocean–ocean |
| Engine | No `engine` source edits; custom locker merge type allowed in product |

See **ADR-013** in [../decisions.md](../decisions.md). \(T_{ocean}=8\), \(T_{land}=16\) (F-058).

---

## Product claims (tests by Goal end)

- [x] Interior thickness/elevation follows the plate claim map (not contact paint left behind)
- [x] Ridge/gap cells are thin oceanic, not inherited high crust
- [x] Oceanic crust is consumed at COLLIDE; continental is not deleted by area-only precedence
- [x] After enough generations on a collide-friendly seed, some cells stay \(\ge T_{land}\) while riding (arc and/or suture)
- [ ] `elevation` matches isostasy of lockers at occupancy; determinism; no `engine` edits; suite green

---

## Planned Steps

| Step | Intent | Status |
|------|--------|--------|
| F-055 | Docs lock (wiki + ADR-013 + Goal) — **docs only** | done |
| F-056 | Occupancy keys + thickness lockers + ride + isostasy elevation | done |
| F-057 | Ridge mint (thin oceanic in gaps) + Simulation restart | done |
| F-058 | Buoyancy precedence + oceanic subduction + SEPARATE mint | done |
| F-059 | Margin relief (rift trough + collide slope + lip blend) | done |
| F-060 | Continental suture + arc thickening + cap | done |
| F-061 | Goal close + dump/wiki/UI hygiene | not started |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 6 / 7 |
| Claim boxes | 4 / 5 |
| Last Accept | F-060 |
