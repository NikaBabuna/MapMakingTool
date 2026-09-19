<!--
  File: docs/project/features.md
  Purpose: Step registry (F-0xx) grouped by Goal
  Audience: Agents and humans
  Update when: Steps are added or change status
-->

# Steps (feature registry)

Each **Step** is one AI job under a **Goal**. Approved FRs + test mapping: `docs/blockers/F-0xx.md` (created at APPROVE / STORE, before code).

**Incremental:** Accept requires this Step’s tests **and** all earlier Accepted Steps’ tests to stay green.

Procedure: [../process/step-procedure.md](../process/step-procedure.md)

**Status values:** `not started` | `in progress` | `done` | `rolled back`

---

## G-001 — Engine skeleton

Goal doc: [goals/G-001-engine-skeleton.md](goals/G-001-engine-skeleton.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-001 | Layout + Maven scaffold | done | [F-001.md](../blockers/F-001.md) |
| F-002 | Pool + Step loop + Step 0 config | done | [F-002.md](../blockers/F-002.md) |
| F-003 | Events + claiming + unmatched log | done | [F-003.md](../blockers/F-003.md) |
| F-004 | Systems + typed merge + provenance | done | [F-004.md](../blockers/F-004.md) |
| F-005 | Claim/finish + determinism | done | [F-005.md](../blockers/F-005.md) |
| F-006 | User Input / Input View / User View | done | [F-006.md](../blockers/F-006.md) |
| F-007 | CLI runner | done | [F-007.md](../blockers/F-007.md) |
| F-008 | Basic UI + claim checklist closure | done | [F-008.md](../blockers/F-008.md) |
| F-009 | CI pipeline (GitHub Actions) | done | [F-009.md](../blockers/F-009.md) |

---

## G-002 — Engine host readiness

Goal doc: [goals/G-002-engine-host-readiness.md](goals/G-002-engine-host-readiness.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-010 | Pluggable Pool compute | done | [F-010.md](../blockers/F-010.md) |
| F-011 | Wider Pool field carrier | done | [F-011.md](../blockers/F-011.md) |
| F-012 | Pluggable event emission + host closure | done | [F-012.md](../blockers/F-012.md) |

---

## G-003 — First product world

Goal doc: [goals/G-003-first-product-world.md](goals/G-003-first-product-world.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-013 | Product Maven module + architecture | done | [F-013.md](../blockers/F-013.md) |
| F-014 | World as Pool state (grid + elevation) | done | [F-014.md](../blockers/F-014.md) |
| F-015 | First generative process | done | [F-015.md](../blockers/F-015.md) |
| F-016 | Witnessed world + G-003 closure | done | [F-016.md](../blockers/F-016.md) |

---

## G-004 — See the world

Goal doc: [goals/G-004-see-the-world.md](goals/G-004-see-the-world.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-017 | Voronoi multi-plate tectonics | done | [F-017.md](../blockers/F-017.md) |
| F-018 | Large colored map UI + loading | done | [F-018.md](../blockers/F-018.md) |

---

## G-005 — Living map

Goal doc: [goals/G-005-living-map.md](goals/G-005-living-map.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-019 | Product session + UI/CLI house | done | [F-019.md](../blockers/F-019.md) |
| F-020 | Plate kinematics | done | [F-020.md](../blockers/F-020.md) |
| F-021 | Motion-based orogeny | done | [F-021.md](../blockers/F-021.md) |
| F-022 | Tool UI | done | [F-022.md](../blockers/F-022.md) |
| F-023 | Placeholder CLI + in-UI console | done | [F-023.md](../blockers/F-023.md) |

---

## G-006 — Local webview front

Goal doc: [goals/G-006-webview-front.md](goals/G-006-webview-front.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-024 | Java session HTTP host | done | [F-024.md](../blockers/F-024.md) |
| F-025 | Next.js tool UI parity | done | [F-025.md](../blockers/F-025.md) |
| F-026 | Tauri shell + demote Swing; close G-006 | done | [F-026.md](../blockers/F-026.md) |

---

## G-007 — Studio cartography tool

Goal doc: [goals/G-007-studio-cartography.md](goals/G-007-studio-cartography.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-027 | Style guide + studio chrome + map-first shell | done | [F-027.md](../blockers/F-027.md) |
| F-028 | Pan / zoom + cell pick + reset view | done | [F-028.md](../blockers/F-028.md) |
| F-029 | Shortcuts, seed QoL, feedback, a11y; close G-007 | done | [F-029.md](../blockers/F-029.md) |

---

## G-008 — Boundary tectonics + cartography studio

Goal doc: [goals/G-008-boundary-tectonics-studio.md](goals/G-008-boundary-tectonics-studio.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-030 | Wiki + decisions (torus, size, partition, Pool fields) | done | [F-030.md](../blockers/F-030.md) |
| F-031 | Large rectangular world 1920×1080 | done | [F-031.md](../blockers/F-031.md) |
| F-032 | Toroidal wrap; loopback pan + zoom clamp | done | [F-032.md](../blockers/F-032.md) |
| F-033 | Initial plate partition + registry skeleton | done | [F-033.md](../blockers/F-033.md) |
| F-034 | Boundary trace + classify | done | [F-034.md](../blockers/F-034.md) |
| F-035 | Precedence + area flux + motion intent | done | [F-035.md](../blockers/F-035.md) |
| F-036 | Flux apply, flood, fission, death | done | [F-036.md](../blockers/F-036.md) |
| F-037 | Edge-driven velocity integrate | done | [F-037.md](../blockers/F-037.md) |
| F-038 | Orogeny from standing boundaries | not started | |
| F-039 | Multi-panel studio + mappy style | not started | |
| F-040 | Traditional console; close G-008 | not started | |

---

## Marking progress

- Set Status to `in progress` **before** writing code for that Step.  
- Set to `done` only after green witness + doc sync + commit.  
- Torn `in progress` on a new chat → rollback.
