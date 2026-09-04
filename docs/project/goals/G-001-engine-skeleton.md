<!--
  File: docs/project/goals/G-001-engine-skeleton.md
  Purpose: Current multi-session Goal — runnable Pool-System engine skeleton
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-001 — Engine skeleton

**Status:** in progress  
**Product work:** out of scope until this Goal is done (no tectonics, climate, map worldbuilding, timeline scrub as product features).

---

## Result we want

A **runnable Pool-System Framework skeleton** that:

1. Implements the engine loop described in [../../engine/specs/](../../engine/specs/) (Pool, Steps, events, Systems, typed merge, User View / Input View).
2. **Upholds the framework’s functional claims in tests** — determinism, claim/finish, merge behavior, cross-step propagation, etc. (see checklist below).
3. Can be **run** via automated tests, a **CLI**, and a **basic UI** (enough to see Steps advance and View read settled state — not the Aethelgard product UI).

When this Goal is `done`, another agent can trust the engine core and start product Features on top of it.

---

## Out of scope (this Goal)

- Aethelgard world generation (plates, climate, biomes)
- Timeline scrub / inspection product flows
- Polished art or style-guide UI
- Published standalone engine library
- Resolving every open engine question (only those marked decided below)

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Step 0 | Starting **config object** seeds the Pool ([../../engine/specs/open-questions.md](../../engine/specs/open-questions.md)) |
| Unmatched events | **Log** (no silent drop without a log) |
| Non-finishing Systems | Deferred — not required to close G-001 |
| Delete Request conflict policy | Deferred — may stub or omit until a later Step |
| Package / module names | `com.aethelgard.engine`; modules per ADR-007 / [engine/architecture.md](../../engine/architecture.md) |
| JDK | Java 21 (`maven.compiler.release` 21) — recorded at F-001 |

---

## Framework claims to uphold (tests)

These must be green by Goal completion (spread across Steps as needed):

- [x] Pool `update` + Step lifecycle order _(spine through F-004: update → claim → Systems → merge → apply → clear; claim/finish barrier + View later)_
- [x] Step 0 from config object
- [x] Event buffer + category ancestry claiming _(Systems claim via `EventClaimer`; stub claimers remain)_
- [x] Unmatched events logged
- [x] System independence (no same-Step System chaining)
- [x] Sub-System composition / conflict-resolution hook (as needed for skeleton)
- [x] Typed merge: Static, Increment, Constant, Destructive (Delete Request if included)
- [x] Provenance on conflicting writes
- [ ] Claim/finish barrier
- [ ] Determinism: same config + inputs → same Pool after N Steps
- [x] No same-Step event buffer refill _(System-output → next-Step events still deferred)_
- [ ] User Input → Input View sampling
- [ ] User View reads settled Pool only
- [ ] CLI can run N Steps and show settled state
- [ ] Basic UI can advance/view Steps (headless-safe tests for logic)

---

## Planned Steps

Registered in [../features.md](../features.md). Order may be refined when a Step is negotiated; do not start code until that Step is approved.

Each Step stores approved FRs in `docs/blockers/F-0xx.md`. Accept is **incremental**: later Steps must keep all earlier Step tests green.

| Step | Intent | Status |
|------|--------|--------|
| F-001 | Decide package/module layout; record in engine architecture; scaffold Maven (tests runnable) | done |
| F-002 | Pool + Step loop + Step 0 config object | done |
| F-003 | Events, category claiming, unmatched-event logging | done |
| F-004 | Systems + Sub-Systems + typed merge + provenance | done |
| F-005 | Claim/finish + determinism witness | not started |
| F-006 | User Input / Input View / User View | not started |
| F-007 | CLI runner | not started |
| F-008 | Basic UI + close remaining claim checklist gaps | not started |
| F-009 | CI pipeline (GitHub Actions witness) | done |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 5 / 9 |
| Framework claim boxes | 9 / 15 |

Update this section at the end of every successful Step.
