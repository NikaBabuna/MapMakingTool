<!--
  File: docs/project/goals/G-003-first-product-world.md
  Purpose: Multi-session Goal — first Aethelgard product slice on the engine host
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-003 — First product world

**Status:** `done`  
**Engine:** do not edit `engine` source for ordinary world rules (G-002 host ports).

---

## Result we want

The first **Aethelgard product** on the Pool-System host: a small caused world, not another engine demo.

When this Goal is `done`:

1. A **`product` Maven module** exists and depends on `engine`. `engine` does not depend on `product`.
2. **World state lives in Pool typed fields** — shared grid geometry plus at least an `elevation` layer. The skeleton heartbeat `value` is not the world.
3. **Step 0 is a seed**, not a finished map. Elevation after N Steps is produced by a product System from documented rules.
4. Generation is dispatched on the engine: product category tree, emission policy, and at least one `EngineSystem`.
5. **Same seed + dimensions + N Steps → identical elevation grid.**
6. The generative rule lives in [`docs/product/wiki/`](../../product/wiki/), not only in code.
7. A **headless product observer** can dump the settled grid (tests + ASCII or structured snapshot). Skeleton `cli` / `ui` may stay as they are.
8. All G-001 and G-002 Accept witnesses stay green (incremental suite).

Plain English: lock the product house (module, grid layers, process-as-System) and ship **one causal layer**. Later Goals add climate, biomes, and timeline on that seam.

---

## Out of scope (this Goal)

- Wind, rainfall, temperature, biomes
- Explore / Guide / Timeline / Inspect product flows (detail and UI)
- Rewriting skeleton `cli` / `ui` into product entry points
- Polished map renderer or style-guide UI
- Engine ports (event payloads, parameterized input, claimed events into `System.run`)
- Non-finishing Systems (open question #1)
- Delete Request merge policy (open question #4)

If a stored FR cannot be met without an engine port: **stop**, ADR, do not sneak the change into this Goal.

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Seam | World = shared grid geometry + named layers as Pool fields. `elevation` is the first layer; later climate is more fields of the same shape |
| Processes | Each natural process is an `EngineSystem`. Product emission ticks generation; do not grow world physics inside a single `PoolCompute` |
| Bootstrap | Step 0 seeds initial conditions (dimensions, seed, whatever the first process needs). The heightmap is worked out over Steps |
| Observation | Headless dump in `product` (tests + formatter). No product UI required |
| Engine | No `engine` source edits for ordinary feature growth |
| Physics detail | Exact elevation rule is negotiated in **F-015** FRs (keep small: plates-lite / collision uplift, not a full Wilson cycle) |

---

## Product claims (tests by Goal end)

- [x] `product` module exists; depends on `engine`; `engine` does not depend on `product`
- [x] World state is Pool typed fields (geometry + `elevation`); not the heartbeat `value`
- [x] Step 0 seeds initial conditions only; settled heightmap after N Steps is System-produced
- [x] Product category tree + emission policy + at least one `EngineSystem` drive generation without editing `engine`
- [x] Same seed + dimensions + N Steps → identical elevation grid
- [x] Generative rule is documented under `docs/product/wiki/`
- [x] Headless product observer dumps the settled grid
- [x] Incremental suite: all G-001 and G-002 Step tests remain green

---

## Planned Steps

Registered in [../features.md](../features.md). Accept is **incremental**. Do not start code until that Step’s job + FRs are approved and stored.

| Step | Intent | Status |
|------|--------|--------|
| F-013 | Product Maven module + architecture (one-way rule); product test constructs `Engine` via `EngineSetup` | done |
| F-014 | World as Pool state — geometry + `elevation` (and seed fields the first process needs); wiki: World / grid / layer | done |
| F-015 | First generative process — product emission tick + first System; wiki: elevation rule | done |
| F-016 | Witnessed world — determinism fixture, headless dump, close Goal claims + docs | done |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 4 / 4 |
| Product claim boxes | 8 / 8 |

Update this section at the end of every successful Step.
