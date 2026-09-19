<!--
  File: docs/project/goals/G-008-boundary-tectonics-studio.md
  Purpose: Multi-session Goal — plate tectonics do-over + large torus world + studio UI
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-008 — Boundary tectonics + cartography studio

**Status:** `in progress`  
**Engine:** do not edit `engine` source for ordinary feature growth (G-002 host ports).  
**Prior:** [G-007](G-007-studio-cartography.md) delivered studio chrome QoL on a small Voronoi+Constant-velocity living map. This Goal **rebuilds plate physics**, grows the world, and deepens the cartography studio.

**Approved:** 2026-09-19 (user). Direction: boundary-first tectonics (fission, edge-driven size/motion); **1920×1080** rectangular **torus**; initial plates from a realistic partition; studio panels + mappy style + traditional console; map zoom clamp + loopback pan.

---

## Result we want

When this Goal is `done`:

1. **World geometry.** Primary view is **1920×1080** (rectangular). Space is a **cylinder map** (wrap X; polar Y — sphere-on-rectangle). Wiki and code agree.
2. **Initial plates.** At Step 0 the torus is fully partitioned into contiguous plates that **look realistic** (irregular, varied sizes — rule locked in F-030 / implemented F-033). Plates are the actors.
3. **Boundary tectonics.** Contacts classify as separate / collide / pass-by. Size changes via area flux; collide precedence v1 = **smaller loses** (no crust types yet). Motion is edge-driven (push / pull / damp), not forever-Constant random `{-1,0,1}`.
4. **Number.** Area → 0 ⇒ death. Connectivity break ⇒ **fission**. (Intentional rift-fracture birth may stay thin or follow-on if Steps stay small.)
5. **Engine-shaped.** Pool fields for registry, boundaries, flux/intent, plates, elevation, event ledger as needed. Category tree under `world/tectonics/…`. Sub-System pipeline + orogeny; emission used where useful. No `engine` production edits.
6. **Studio.** Multi-panel cartography feel; mappy visuals; traditional terminal console; map **zoom in** only within map limits; **loopback pan** matching the torus.
7. **Determinism.** Same seed + N Steps → identical fields. Incremental suite green. MapHost / Next house preserved.

Plain English: a large looping world whose plates behave like plates, shown in a real map studio.

---

## Out of scope (this Goal)

- Climate / biomes  
- Explore / Guide / Timeline product modes  
- Oceanic vs continental typology (unless a later Step explicitly expands)  
- True 3D globe mesh (torus is the sphere-analogue here)  
- Engine framework ports (non-finishing Systems, Delete Request, …)

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| View size | **1920×1080** for starters |
| Topology | 2D **cylinder** (wrap X; polar Y) — amended F-034 from full torus |
| Enclaves | **Fission** |
| Collide precedence v1 | Smaller plate loses (types later) |
| Initial plates | Realistic full partition (algorithm locked in F-030) |
| UI | Multi-panel studio, mappy style, traditional console, zoom clamp, loopback pan |

---

## Product claims (tests by Goal end)

- [x] 1920×1080 VIEW in code; wiki + witnesses
- [x] Initial realistic plate partition (F-033)
- [x] Boundaries + flux + flood + fission/death
- [x] Edge-driven motion; Constant random velocities gone
- [ ] Orogeny from new boundary model
- [ ] Studio panels + mappy style + traditional console
- [x] Zoom clamp + horizontal-only loopback pan (F-032/F-033)
- [ ] Determinism; no `engine` production edits; suite green

---

## Planned Steps

| Step | Intent | Status |
|------|--------|--------|
| F-030 | Wiki + decisions (torus, size, partition rule, Pool fields, fission) — **docs only** | done |
| F-031 | Large rectangular world (1920×1080); host/session tolerate size | done |
| F-032 | Toroidal wrap in product; studio loopback pan + zoom clamp | done |
| F-033 | Initial plate partition + registry skeleton | done |
| F-034 | Boundary trace + classify → `boundaries` | done |
| F-035 | Precedence + area flux + motion intent | done |
| F-036 | Apply flux, flood, fission, death; registry | done |
| F-037 | Integrate edge-driven velocities | done |
| F-038 | Orogeny from standing boundaries | not started |
| F-039 | Multi-panel studio + mappy style | not started |
| F-040 | Traditional console; Goal close | not started |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 8 / 11 |
| Claim boxes | 5 / 8 |
| Last Accept | F-037 |
