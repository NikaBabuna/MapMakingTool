<!--
  File: docs/project/decisions.md
  Purpose: Architecture Decision Record log
  Audience: Agents and humans
  Update when: Non-obvious choices are made
-->

# Decisions (ADR log)

Format: **ADR-0xx** — title — date — `proposed` | `accepted` | `superseded`

---

## ADR-001 — Monorepo for engine and product

**Date:** 2026-08-28  
**Status:** accepted

Engine (Pool-System Framework) and product (Aethelgard) live in one repository during alpha and beta.

**Why:** Single witness suite, one navigation map, simpler protocol loop. Extraction to a separate repo is a future ADR if reuse demands it.

---

## ADR-002 — Documentation namespaces

**Date:** 2026-08-28  
**Status:** accepted

Documentation groups into `process/`, `project/`, `engine/`, and `product/` under `docs/`. Domain content uses `product/wiki/` (not `game/`).

**Why:** Separates how we work from what the engine is from what the product does.

---

## ADR-003 — Java as implementation language

**Date:** 2026-08-28  
**Status:** accepted

Implementation language is Java unless superseded by ADR.

**Why:** Default per repository protocol; aligns with Maven witness tooling.

---

## ADR-004 — Goal / Session / Step agent procedure

**Date:** 2026-09-04  
**Status:** accepted

Coding work uses Goal (multi-session) → Session (temporary chat focus) → Step (`F-0xx` with blockers). Order: negotiate job + functional requirements → user approval → mark `in progress` → plan → code → blockers/tests from requirements → witness → sync docs → final check → commit. Torn Steps roll back. Docs win over chat.

**Why:** Makes AI progress autonomous across chats, honest on failure, and grouped under durable Goals without cluttering the registry.

**Supersedes:** Earlier “blockers before any production code” ordering in the draft protocol. Requirements still precede code; executable blockers witness after code for that Step.

---

## ADR-005 — Step 0 seeded by config object

**Date:** 2026-09-04  
**Status:** accepted

Pool Step 0 is initialized from a caller-supplied config object.

**Why:** Explicit, testable bootstrap without hidden global state.

---

## ADR-006 — Unmatched events are logged

**Date:** 2026-09-04  
**Status:** accepted

Events claimed by no System are logged, not silently discarded.

**Why:** Makes wiring mistakes visible during engine development.

---

## ADR-007 — Multi-module layout and Java 21

**Date:** 2026-09-04  
**Status:** accepted

Monorepo Maven parent `com.aethelgard:aethelgard` with module `engine` (`com.aethelgard:engine`) first. Package root `com.aethelgard.engine`. Java 21. Future sibling modules `cli`, `ui`, `product` depend on `engine`; engine never depends on them. Empty sibling modules are not created until their Steps.

**Why:** Preserves a production-grade dependency boundary without pre-carving unused trees. Details: [../engine/architecture.md](../engine/architecture.md).

---

## ADR-008 — Engine diagnostics via SLF4J + capturable port

**Date:** 2026-09-04  
**Status:** accepted

`engine` depends on **SLF4J API** only. `EngineDiagnostics` is the observability port (Step start/settle, emit, claim, unmatched). Default bridge logs to SLF4J (DEBUG for lifecycle/emit/claim; **WARN** for unmatched — ADR-006). Bindings (Logback, `slf4j-simple`, etc.) live in adapters or test scope — not as `engine` compile deps. Tests use `RecordingDiagnostics` to assert without scraping stdout. Diagnostics must not affect Pool determinism.

**Why:** Unmatched events are a correctness obligation; operators also need to see the machine run. A port keeps both testable and adapter-friendly.

---

## ADR-009 — Product authors the category tree in Java

**Date:** 2026-09-17  
**Status:** accepted

The event **category tree is application-owned**. For G-003, `product` builds it in Java (`CategoryTree.of("world/tectonics")` via `ProductCategories`). The engine provides `CategoryTree` / ancestry claiming only. No file format, DSL, or engine-owned world tree in this Goal.

**Why:** Open question #2a was an application concern. F-015 needs a real tree to dispatch generation; product Java is the smallest honest authorship model.

Resolves: [../engine/specs/open-questions.md](../engine/specs/open-questions.md) #2a.

---

## ADR-010 — UI and CLI are product adapters

**Date:** 2026-09-17  
**Status:** accepted

`ui` and `cli` depend on `product`. `product` depends on `engine`. `engine` never depends on `ui`, `cli`, or `product`.

| Module | Role |
|--------|------|
| **engine** | Abstract Pool-System loop |
| **product** | Aethelgard simulation — field values, Systems, session. **No Swing** |
| **ui** | View of those values (map window, tool chrome) |
| **cli** | Operator access — full headless runner + shared noun/verb language (F-048–F-049) |

`ui` may depend on `cli` **only** to reuse that command layer for an **in-window console**. `cli` must not depend on `ui`.

**Live access (G-005):** one in-process **session** owns the `Engine`. UI, console, and headless CLI call it. Advances are serialized. No socket.

**Commands (amended F-048 / F-049 / G-009):** Shared **noun-path + verb** language in `cli` (`session`, `pool`, `schema`, `systems`, `diag` + `list`/`get`/`advance`/…). Headless `CliRunner` owns one session per invocation (`--seed` / `--steps` / `-c`). Deprecated flat aliases (`status`, `advance`, …) remain through G-009. Do **not** put command names into product Systems, Pool fields, or merge types.

Skeleton heartbeat `ui` / `cli` as the product experience is retired (G-001 adapters were scaffolding). Engine tests still witness the loop without Aethelgard.

**Amends:** [ADR-007](#adr-007--multi-module-layout-and-java-21) — sibling modules may depend on `product`, not only on `engine`. Engine still never depends on siblings. **F-048** retires “commands are placeholders only” for the noun/verb catalog; **F-049** retires the headless CLI as a dump-only placeholder (ADR-012).

**Why:** The engine is abstract. Product computes the world. UI displays it. CLI (including a console button in the UI) interrogates the same run. Deep CLI integration would freeze a throwaway command set into the simulator.

**Goal:** [G-005 Living map](goals/G-005-living-map.md) · command language: [G-009](goals/G-009-simulation-runner-harden.md) F-048–F-049

---

## ADR-011 — Local webview UI (Tauri + Next + Java HTTP host)

**Date:** 2026-09-19  
**Status:** accepted

The interactive map front is a **local web app** inside a **Tauri 2** webview. Simulation stays in Java behind a **localhost HTTP** facade over `ProductSession` / MapController-equivalent logic. Next.js owns presentation. Tauri owns the window and process lifecycle for the Java host.

| Layer | Role |
|-------|------|
| **engine** / **product** | Unchanged — world rules and session; no React/Tauri/HTTP UI deps; product still has no Swing |
| **Java host** | Thin HTTP adapter: one session, serialized advances, raster + tool ops, placeholder console via `cli` dispatch |
| **Next.js** | Tool UI (parity with F-022/F-023) |
| **Tauri** | Desktop shell; spawn/stop host; load UI |

**Amends ADR-010:** G-005 live access was in-process only (“no socket”). G-006 allows **same-machine localhost HTTP** between webview front and Java host. Still one session owner; advances stay serialized. Not a remote multiplayer host. Commands remain placeholders — do not put verb names into Systems or Pool fields.

**Swing:** **removed** as the product map (F-026). Headless map/raster tests remain the behavioral bar.

**Why:** Visual iteration and hot reload need a web front; a webview shell keeps the “local app” feel without moving simulation out of Java.

**Goal:** [G-006 Local webview front](goals/G-006-webview-front.md)

---

## ADR-012 — Simulation runner harden (G-009 locks)

**Date:** 2026-09-19  
**Status:** accepted

G-009 hardens the living map into a **simulation runner**. Locks (docs F-041; code in later Steps):

| Topic | Decision |
|-------|----------|
| **Diverge** | SEPARATE gaps must not be filled by nearest arbitrary third plate. New crust at separate contacts belongs only to the **two contacting plates** (ridge accretion). |
| **Poles** | Replace G-008 **cylinder** hard-Y with **sphere-on-rectangle**: crossing north re-enters from the north at antipodal longitude (heading flips); same for south. Advection, neighbors, distance, fission, and camera must agree. |
| **Commands** | One command language for **CLI + in-app terminal**; studio panels are faces on the same session — not a second API. Placeholder verbs (ADR-010) are replaced under this Goal. |
| **Observability** | Per-Step timings, memory snapshots, and key counters are **recorded and queryable** via that command surface (not file-only). |

**Amends:** G-008 wiki cylinder topology (F-034) for poles — sphere polar wrap supersedes hard polar drop. ADR-010 “commands are placeholders” is retired as the end-state of G-009 (F-048+).

**Out of scope:** climate/biomes; Explore/Guide/Timeline modes; 3D globe mesh; engine framework ports.

**Why:** G-008 shipped working tectonics and studio chrome, but void-fill, polar drop, black stacking borders, placeholder CLI, and invisible cost keep the product from feeling like a real simulation environment.

**Goal:** [G-009 Simulation runner harden](goals/G-009-simulation-runner-harden.md)

---

## ADR-013 — Crust topology (G-010 locks)

**Date:** 2026-09-20  
**Status:** accepted

G-010 makes **crust material that rides plates** so continents can form. Locks (docs F-055; code in later Steps):

| Topic | Decision |
|-------|----------|
| **Assembly** | One `tectonics` `EngineSystem`. Crust Sub-Systems run **after** occupancy (same-Step staging). Not a second System this Goal. |
| **State** | Occupancy **keys** (cell → locker id) + **lockers** (id → thickness). Motion remaps keys. Contacts mint/merge lockers and increment thickness in **locker-id** space. |
| **Elevation** | Derived (integer isostasy of thickness at current keys). Contact-paint **Orogeny** is retired as the elevation author (code F-056+). |
| **Ridge mint** | New occupancy at SEPARATE / gaps gets thin oceanic crust (\(T_{ocean}\)). Gaps do not inherit a neighbor’s mountain. |
| **Buoyancy** | Thickness \(\ge T_{land}\) is continental; below is oceanic. COLLIDE: oceanic subducts; continental does not die because its plate is smaller. Ocean–ocean keeps smaller-loses. |
| **Suture / arc** | Ocean–ocean may thicken an arc on the winner. Continent–continent thickens both sides; neither locker dies. Thickness cap / light erosion so growth is bounded. |
| **Step 0** | All oceanic (\(T_{ocean}\)). No painted cratons. Continents emerge from arcs + suture. |
| **Weld** | No plate-id merge on suture. Thickness is the continent. |
| **Merge** | Occupancy STATIC (one writer). Lockers may use a product **custom** `FieldMergeType`. No `engine` source edits. |

**Amends:** G-008 collide precedence “smaller plate loses (no types yet)” and F-038 contact-paint orogeny as the **relief author** — superseded on paper for G-010; buoyancy runtime F-058; suture remaining F-059.

**Out of scope:** climate/biomes; Explore/Guide/Timeline; second topology System; scratch-pad wait-notify; plate weld; age/sediment/plumes; 3D globe; engine framework ports.

**Why:** The runner shows moving plates, but elevation is paint on old contacts. Continents cannot accumulate. Crust must be cargo on occupancy, created thin at ridges, and destroyed only when it can subduct.

**Goal:** [G-010 Crust topology](goals/G-010-crust-topology.md)

