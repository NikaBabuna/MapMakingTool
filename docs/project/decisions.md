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
