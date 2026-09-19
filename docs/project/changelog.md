<!--
  File: docs/project/changelog.md
  Purpose: High-signal structure, phase, and scope history
  Audience: Humans and agents
  Update when: Structure, phase, or scope shifts
-->

# Changelog

Not every commit — only structure, phase, and scope shifts.

---

## Structure

- **2026-09-19** — **F-024:** `MapHost` localhost HTTP under `ui` (`com.aethelgard.ui.host`); F-023 Active-Goal-none grep amended for G-006.
- **2026-09-19** — **G-006** Local webview front registered (Tauri + Next + Java HTTP host); **ADR-011** amends ADR-010 (localhost HTTP). Steps F-024–F-026 planned. Stack note in `project.md`.
- **2026-09-17** — **F-023:** placeholder CLI dispatcher + in-UI console; **G-005 complete**.
- **2026-09-17** — **F-022:** tool UI (ocean, hillshade, layers, play, seed, inspect, legend). F-018 negative-as-zero paint superseded.
- **2026-09-17** — **F-021:** motion-based orogeny (converge / diverge / transform; negative elevation). Foreign-neighbor `+1` retired.
- **2026-09-17** — **F-020:** plate kinematics (`plate_velocity` CONSTANT; `plates` STATIC advection). Elevation still standing-plate `+1` until F-021.
- **2026-09-17** — **F-019:** `ProductSession`; `ui`/`cli` depend on `product`; map window in `ui` (ADR-010).
- **2026-09-17** — **G-005** Living map Goal registered; Steps F-019–F-023 planned. **ADR-010:** `ui`/`cli` depend on `product`; in-UI console; CLI commands are placeholders.
- **2026-09-17** — **F-018:** product map window (512×512 height raster, Advance, busy status); **G-004 complete**.
- **2026-09-17** — **F-017:** Voronoi 6–15 plate seed (Euclidean nearest site); F-015 two-plate stripe superseded.
- **2026-09-17** — **G-004** See the world Goal registered; Steps F-017–F-018 planned.
- **2026-09-17** — **F-016:** headless `WorldDump` + G-003 complete.
- **2026-09-17** — **F-015:** first generative process (plates layer + collision uplift; ADR-009 category tree authorship).
- **2026-09-17** — **F-014:** world as Pool state (`WorldSpec`, `Grid`, field `elevation`).
- **2026-09-17** — **F-013:** `product` Maven module (`com.aethelgard:product`, `ProductHost`); G-003 in progress.
- **2026-09-17** — **G-003** First product world Goal registered; Steps F-013–F-016 planned.
- **2026-09-17** — Process: SYNC checklist + Goal-close entry-point ties (`doc-contract`, step-procedure, quality, global-prompt, rules, blockers template).
- **2026-09-16** — Doc sync after G-002: entry points, specs, glossary, navigation aligned to host-ready engine.
- **2026-09-16** — **F-012:** pluggable `EventEmissionPolicy`; **G-002 complete**.
- **2026-09-16** — **F-011:** `Object` field carrier + pluggable `FieldMergeType` (G-002).
- **2026-09-16** — **F-010:** pluggable `PoolCompute` / `SkeletonPoolCompute` default (G-002).
- **2026-09-16** — **G-002** Engine host readiness Goal registered; Steps F-010–F-012 planned (docs only; no code).
- **2026-09-04** — F-008: `ui` Maven module — basic Step advance / settled view; **G-001 complete**.
- **2026-09-04** — F-007: `cli` Maven module — headless N-Step runner.
- **2026-09-04** — F-006: User Input / Input View / User View (`user` package).
- **2026-09-04** — F-005: claim/finish barrier + determinism witness.
- **2026-09-04** — F-004: Systems + Sub-Systems + typed merge + provenance (`system`, `merge` packages).
- **2026-09-04** — Landmark folder READMEs required; navigation links them (PHASE + rules).
- **2026-09-04** — Compliance pass: docs aligned to F-003 code (status labels, claim wording, glossary/spec status notes).
- **2026-09-04** — F-003: event buffer, ancestry claiming, `EngineDiagnostics` + SLF4J (ADR-008).
- **2026-09-04** — F-002: Pool Step loop + Step 0 config (`com.aethelgard.engine.pool`).
- **2026-09-04** — F-009: GitHub Actions CI (JDK 21 + `mvnw test` on `main`).
- **2026-09-04** — F-001: Maven parent + `engine` module, wrapper, package `com.aethelgard.engine`, Java 21 (ADR-007).
- **2026-09-04** — FRs stored in `blockers/F-0xx.md` at APPROVE; incremental witness (prior Step tests must hold).
- **2026-09-04** — Goal / Session / Step procedure (`process/step-procedure.md`); `project/goals/`, `session.md`; G-001 engine skeleton Goal; ADR-004–006.
- **2026-08-28** — Document infrastructure: `process/`, `project/`, `engine/`, `product/`, `blockers/`. Drafts extracted and removed.

---

## Phase

- **2026-08-28** — Project starts in `alpha`.

---

## Scope

_(none yet)_
