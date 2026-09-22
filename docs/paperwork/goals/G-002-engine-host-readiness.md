<!--
  File: docs/paperwork/goals/G-002-engine-host-readiness.md
  Purpose: Multi-session Goal — engine as clean host for product
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-002 — Engine host readiness

**Status:** `done`  
**Product world generation:** out of scope for this Goal (no tectonics, climate, biomes, timeline scrub as product features).

---

## Result we want

Bring the Pool-System **engine** up to a standard where **Aethelgard can live beside it**, not inside it.

When this Goal is `done`:

1. **Pool compute is pluggable** — callers supply how the Pool updates each Step; the G-001 heartbeat/`nudge` demo remains the **default** so prior witnesses stay green.
2. **Pool field values are not Long-only** — domain state can live in the Pool without encoding hacks; numeric Increment merge still works for `Long`.
3. **Event emission is pluggable** — what events fire each Step can come from state/policy; the G-001 scripted config-path list remains the **default**.
4. **All G-001 Accept witnesses stay green** (incremental suite).
5. A later product Goal can add Systems/mechanics in a **`product` module** (or other non-engine code) **without editing `engine`** for ordinary feature growth.

Plain English: finish the engine as a **clean host**. The loop stays in engine; the world rules move to product later.

---

## Out of scope (this Goal)

- Aethelgard world generation (plates, climate, biomes)
- Creating the `product` Maven module (belongs to the next product Goal)
- Timeline / Explore / Guide product flows
- Polished product UI
- Non-finishing Systems (open question #1)
- Delete Request merge policy (open question #4)
- Passing claimed events into `System.run` (defer unless a Step later proves need)
- Rewriting CLI/UI into product entry points (adapters may keep skeleton defaults)

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Separation | Product must not require engine edits for new Systems/mechanics; engine never depends on product (ADR-007) |
| Skeleton demo | Heartbeat + `nudge` + scripted emissions stay available as **defaults** for regression; not hardwired as the only path |
| Field carrier | Widen to `Object`; pluggable `FieldMergeType`; defaults via `FieldType` enum; Increment remains numeric (`Long`) — **F-011** |
| Emissions | Pluggable `EventEmissionPolicy`; default `ScriptedEventEmissionPolicy`; skeleton always applies policy — **F-012** |
| Corrections | Minimal and incremental — no unrelated engine refactors |

---

## Host-readiness claims (tests by Goal end)

- [x] Custom Pool compute can replace the default without editing `Pool` internals for product rules
- [x] Default compute preserves G-001 heartbeat / `nudge` behavior
- [x] Non-`Long` field values can be stored, merged (Static/Destructive pick-one), and read from settled snapshots
- [x] Numeric Increment merge still works for `Long` fields
- [x] Custom emission policy can choose events from state/input without editing engine for each product feature
- [x] Default emission preserves scripted config-path behavior
- [x] Incremental suite: all G-001 Step tests remain green
- [x] Architecture docs record the host extension points (`EngineSetup` / ports)

---

## Planned Steps

Registered in [../features.md](../steps.md). Accept is **incremental**.

| Step | Intent | Status |
|------|--------|--------|
| F-010 | Pluggable Pool compute — extract heartbeat/`nudge` to default strategy; wire via setup | done |
| F-011 | Wider Pool field carrier — non-`Long` values; keep Increment for numbers | done |
| F-012 | Pluggable event emission — default = scripted paths; close host-readiness claims + docs | done |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 3 / 3 |
| Host-readiness claim boxes | 8 / 8 |

Update this section at the end of every successful Step.
