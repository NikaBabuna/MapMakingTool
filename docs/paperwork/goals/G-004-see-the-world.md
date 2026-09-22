<!--
  File: docs/paperwork/goals/G-004-see-the-world.md
  Purpose: Multi-session Goal — Voronoi tectonics and a large colored map you can see
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-004 — See the world

**Status:** `done`  
**Engine:** do not edit `engine` source for ordinary world rules (G-002 host ports).  
**Prior:** [G-003](G-003-first-product-world.md) locked the product house and a two-plate stripe. This Goal makes that world **look like a map** and **visible**.

---

## Result we want

A **large, colored elevation map** driven by a richer plates-lite process, with a product window you can Advance — and a loading indication when a Step takes time.

When this Goal is `done`:

1. **Tectonics is a suture web, not a single stripe.** Step 0 still seeds `plates` and zero `elevation`. Plate count is **6–15** from the seed. Each cell belongs to the **nearest site** (Voronoi); ties take the **lower site index**. Sites are a deterministic function of `WorldSpec.seed` (exact mix documented in the wiki at F-017).
2. **Collision uplift is unchanged in kind:** each generation Step, a cell with a 4-neighbor on another plate gains **+1**. Plates **do not move** in this Goal.
3. The wiki and **F-015 / F-016 witnesses** describe this rule (the two-plate vertical suture is **superseded** with user agreement, not silently weakened).
4. **`WorldSpec.DEFAULT` stays 8×8** so the headless dump remains a small golden. Product **launch / view spec is 512×512**, seed 0, painted at about one pixel per cell.
5. A **product** window shows elevation as a **height-colored** grid (not the skeleton heartbeat text). **Advance** runs generation. If a Step (or batch) is slow, the UI shows **loading / busy** and compute does **not** freeze the Swing thread.
6. Headless tests cover the new plate rule and raster (and dump golden). **No `JFrame` in tests.** Incremental suite stays green.
7. Skeleton `cli` / `ui` stay engine demos. **`ui` and `cli` do not depend on `product`.** The map lives in `product` (JDK Swing). No `engine` production edits.

Plain English: you should open a window, see a **large** caused heightmap, step it, and watch ridges along **many** plate contacts — with the UI honest when work is in flight.

---

## Out of scope (this Goal)

- Plate motion, subduction, rifting, Wilson cycle
- Wind, rainfall, temperature, biomes
- Explore / Guide / Timeline / Inspect product flows (beyond “see elevation + Advance”)
- Rewriting skeleton `cli` / `ui` into the product entry point (`ui` may stay heartbeat-only)
- Style-guide polish, legends, pan/zoom beyond a fixed 512×512 view
- Engine ports (event payloads, parameterized input, claimed events into `System.run`)
- Non-finishing Systems (open question #1)
- Delete Request merge policy (open question #4)
- Climate / further generation layers (later Goal)
- Changing `WorldSpec.DEFAULT` geometry (8×8 dump fixture stays)

If a stored FR cannot be met without an engine port: **stop**, ADR, do not sneak the change into this Goal.

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Physics | Voronoi multi-plate (6–15) + existing +1 collision uplift; no plate motion |
| Rule change | F-015 two-plate vertical suture is superseded; update wiki + those Step tests/golden in **F-017** |
| Dump default | `WorldSpec.DEFAULT` remains 8×8, seed 0 |
| View size | Product UI launches **512×512**, seed 0 |
| Color | Height ramp (low dark → high light/warm). Not biomes |
| UI home | `product` (headless raster + Swing frame). Skeleton `ui` unchanged |
| Loading | Off the EDT; busy/progress while a Step or long advance runs |
| Engine | No `engine` source edits for ordinary feature growth |

---

## Product claims (tests by Goal end)

- [x] Voronoi plates: 6–15 ids from seed; nearest-site assignment; documented in wiki
- [x] Collision uplift on the new sutures; Step 0 elevation still all zeros
- [x] F-015/F-016 witnesses match the new rule (golden dump still 8×8)
- [x] Same seed + dimensions + N Steps → identical elevation (and dump for DEFAULT)
- [x] 512×512 view spec; headless raster is deterministic RGB per cell
- [x] Product window paints that raster; Advance; loading while compute runs; no `JFrame` in tests
- [x] `ui` / `cli` do not depend on `product`; no `engine` production edits
- [x] Incremental suite: all prior Accepted Step tests remain green

---

## Planned Steps

Registered in [../features.md](../steps.md). Accept is **incremental**. Do not start code until that Step’s job + FRs are approved and stored.

| Step | Intent | Status |
|------|--------|--------|
| F-017 | Voronoi multi-plate seed + wiki; update F-015/F-016 witnesses | done |
| F-018 | 512×512 colored map window, Advance, loading | done |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 2 / 2 |
| Product claim boxes | 8 / 8 |

Update this section at the end of every successful Step.
