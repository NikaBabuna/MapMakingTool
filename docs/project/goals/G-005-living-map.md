<!--
  File: docs/project/goals/G-005-living-map.md
  Purpose: Multi-session Goal — product hosts the world; UI and CLI are the tools; plates move
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-005 — Living map

**Status:** `in progress`  
**Engine:** do not edit `engine` source for ordinary world rules (G-002 host ports).  
**Prior:** [G-004](G-004-see-the-world.md) made a frozen Voronoi heightmap visible inside `product`. This Goal makes **Aethelgard the product**: real module roles, moving plates, a tool UI, and a loosely coupled operator console.

**Approved:** 2026-09-17 (user). Corrections: CLI is reachable **from the UI** (console control); CLI **commands are placeholders** — do not bake them into product.

---

## Result we want

Launch **UI** and see plates **move**, with mountains and rifts following contacts. Play, inspect, switch layers. Open a **console in that window** for operator commands against the **same session**. Headless **CLI** uses the same thin command layer.

When this Goal is `done`:

1. **House.** `ui` and `cli` depend on `product`. `product` depends on `engine`. `engine` depends on neither. **Product has no Swing.** Product computes field values. UI displays them. CLI (and the in-UI console) is operator access, not an engine heartbeat demo.
2. **Session.** One session object owns the `Engine`. UI and CLI/console call it. Advances are **serialized**. That is live CLI for this Goal (same process, not a socket).
3. **Console in UI.** A console control (button / panel) in the map window talks to that session through the **same placeholder command layer** as headless CLI. The command table is **unstable** — a thin dispatch, not a product API. Do not thread verb names through tectonics, fields, or merge types.
4. **Kinematics.** `plates` becomes **STATIC**. A kinematics System advects ownership each generation Step (toroidal wrap; leftover cells nearest-site + lower-index tie, same as Voronoi). Per-plate integer velocities `{-1,0,1}` seeded from `WorldSpec.seed` (not every plate both-zero). Field `plate_velocity` is CONSTANT after seed.
5. **Orogeny.** A second System writes `elevation` from **standing** plates (last Step’s motion). Converge `+1`, diverge `−1`, transform `0`. Elevation may go **negative**. Interior unchanged. Both events fire every generation Step after Step 0; the two Systems do **not** see each other’s output this Step.
6. **Geometry unchanged.** `WorldSpec.DEFAULT` 8×8 dump golden; **VIEW 512×512**, seed 0. F-017 “plates do not move” / foreign-neighbor `+1` is **superseded** in wiki + witnesses with user agreement.
7. **UI** is a dark tool window: hillshade + height ramp; ocean where elevation `< 0`; layers Elevation / Plates / Overlay; Step, Play/Pause, speed; seed + New world; click-inspect sidebar; legend; honest busy status. No pan/zoom required. No `JFrame` in tests.
8. **CLI** is a placeholder command set (`status`, `advance`, `dump`, `at`, `layers` or equivalent) on the session. Headless CLI without UI still works. **Replaceable** without rewriting Systems or session physics.
9. Same seed + size + N Steps → identical plates, velocities, elevation. Incremental suite green. No `engine` production edits.

Plain English: the window is the product, the world drifts, and a console is there for later real commands — without welding those command names into the simulator.

---

## Out of scope (this Goal)

- Wind, rainfall, temperature, biomes
- Timeline scrub / Guide nudges / a finished command language
- Pan/zoom, sockets / second-process attach
- Subduction polarity, Wilson cycle, crust-type layer (unless added by later FR approval)
- Engine ports, non-finishing Systems (#1), Delete Request (#4)
- Changing `WorldSpec.DEFAULT` geometry
- Deep CLI integration (parsers in product, verbs as Pool fields, command-specific Systems)

If a stored FR cannot be met without an engine port: **stop**, ADR, do not sneak the change into this Goal.

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Modules | `ui` → `product` → `engine`; `cli` → `product` → `engine`; `ui` may depend on `cli` **only** for the placeholder console (ADR-010) |
| Product | Values only — no Swing |
| Live CLI | Same-process session; **console control in the UI**; not stdin-only, not a socket |
| Commands | **Placeholder.** Thin dispatch in `cli`. Do not couple product physics to verb names |
| Physics | Two Systems (kinematics + orogeny); ridge lag = one Step |
| Motion | Toroidal advection + nearest-site fill |
| View | 512×512, seed 0; dump 8×8 |
| Engine | No `engine` source edits for ordinary feature growth |

---

## Product claims (tests by Goal end)

- [x] `ui` / `cli` Maven modules depend on `product`; `product` has no Swing types in main sources
- [ ] Shared session: UI + console + headless CLI serialize advances on one `Engine`
- [ ] UI console uses the same placeholder dispatcher as headless CLI; dispatcher is isolated (not product Systems)
- [x] Kinematics: plates move; velocities seeded; wrap + fill as wiki
- [x] Orogeny: converge/diverge/transform; negative elevation allowed; old suture-`+1` retired
- [ ] Determinism: same seed + spec + N Steps → identical fields
- [ ] Tool UI claims (layers, play, inspect, hillshade/ocean, busy) with no `JFrame` in tests
- [ ] Placeholder CLI verbs work headless and from the in-UI console
- [ ] Incremental suite: all prior Accepted Step tests remain green

---

## Planned Steps

Registered in [../features.md](../features.md). Accept is **incremental**. Do not start code until that Step’s job + FRs are approved and stored.

| Step | Intent | Status |
|------|--------|--------|
| F-019 | ADR-010 + deps + session; map shell in `ui`; product no Swing | done |
| F-020 | Velocities + kinematics; plates move; wiki + dump/tests | done |
| F-021 | Orogeny by relative motion; negative elevation; retire foreign-neighbor `+1` | done |
| F-022 | Tool UI: layers, play, seed, inspect, hillshade, ocean | not started |
| F-023 | Placeholder CLI + **in-UI console**; same dispatcher; close G-005 | not started |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 3 / 5 |
| Product claim boxes | 3 / 9 |

Update this section at the end of every successful Step.
