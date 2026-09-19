<!--
  File: docs/project/goals/G-009-simulation-runner-harden.md
  Purpose: Multi-session Goal — harden living map into a simulation runner
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-009 — Simulation runner harden

**Status:** `in progress`  
**Engine:** do not edit `engine` source for ordinary feature growth (G-002 host ports).  
**Prior:** [G-008](G-008-boundary-tectonics-studio.md) shipped boundary tectonics + studio; physics/UI still show void-fill artifacts, cylinder poles, placeholder commands, and weak observability.

**Approved:** 2026-09-19 (user). Direction: fix plate semantics + sphere topology; diagnostics; shared CLI/terminal command surface; Unity-like simulation runner studio with visible performance.

---

## Result we want

When this Goal is `done`:

1. **Diverge.** Gaps between separating plates are **not** filled by a nearest arbitrary third plate. New crust at SEPARATE contacts belongs only to the **two contacting plates** (ridge accretion).
2. **Borders.** Stacking solid-black plate borders from slivers / double-edge paint are gone or clearly reduced; Plates layer stays readable.
3. **Sphere topology.** Crossing north re-enters from the **north** at antipodal longitude (same for south); advection, neighbors, distance, fission, and camera agree. Cylinder hard-Y retired.
4. **Observability.** Per-Step timings (phases), memory snapshots, and key counters are **recorded and queryable** via the shared command surface.
5. **One control language.** CLI and in-app terminal share one dispatcher; studio panels are faces on the same session — not a second API. Placeholder verbs retired.
6. **Runner studio.** Unity-adjacent control density (seed/speed/play/env), perf + detail panels, UI/UX polish; map-first, not marketing chrome.
7. **Performance / memory.** Measurable step-path and raster/host improvements; no Play/raster leaks under soak.
8. **Determinism.** Same seed + N Steps → identical fields (where topology/physics Steps claim it). No `engine` production edits. Incremental suite green.

Plain English: a real simulation runner that tells the truth about plates, poles, cost, and control.

---

## Out of scope (this Goal)

- Climate / biomes / wind  
- Explore / Guide / Timeline as first-class product modes  
- True 3D globe mesh  
- Engine framework ports (non-finishing Systems, Delete Request, …)

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Diverge fill | Ridge accretion from **contacting plates only** — never nearest-third void fill |
| Poles | **Sphere-on-rectangle** polar wrap (antipodal X + heading flip); amends G-008 cylinder |
| Commands | One language for CLI + terminal; panels bind the same session |
| Observability | Timings + memory + counters queryable (not file-only) |
| UI | Simulation-runner feel; scrap placeholder console skin for real terminal on shared dispatcher |

See **ADR-012** in [../decisions.md](../decisions.md).

---

## Product claims (tests by Goal end)

- [x] Diverge: no third-plate fill of SEPARATE gaps (F-043)
- [ ] Slivers / stacking borders addressed (F-044)
- [ ] Sphere polar wrap end-to-end (F-045)
- [x] Diagnostics recorded + queryable (F-042+)
- [ ] Shared command surface + full CLI + rebuilt terminal (F-048–F-050)
- [ ] Runner chrome + perf panels + UX pass (F-051–F-053)
- [ ] Perf/memory improvements witnessed (F-046–F-047)
- [ ] Determinism; no `engine` production edits; suite green

---

## Planned Steps

| Step | Intent | Status |
|------|--------|--------|
| F-041 | Docs lock (wiki + ADR-012 + Goal) — **docs only** | done |
| F-042 | Diagnostics core (timings, memory, dump) | done |
| F-043 | Diverge / void-fill fix | done |
| F-044 | Slivers + border read | not started |
| F-045 | Sphere topology | not started |
| F-046 | Step path hotspots | not started |
| F-047 | Raster + host memory | not started |
| F-048 | Shared command model | not started |
| F-049 | CLI as full runner | not started |
| F-050 | Scrap + rebuild terminal | not started |
| F-051 | Runner chrome | not started |
| F-052 | Perf + detail panels | not started |
| F-053 | UI/UX pass | not started |
| F-054 | Goal close | not started |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 3 / 14 |
| Claim boxes | 2 / 8 |
| Last Accept | F-043 |
