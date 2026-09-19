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

- **2026-09-20** — **F-054 / G-009 close:** layer-switch harden (applyGen + digit preventDefault; MapController paintLock); comprehensive product/wiki/glossary SYNC; Active Goal none; last completed G-009.
- **2026-09-20** — **F-053:** descriptor-driven chrome (panel registry + shared `Panel`, menu-bar row from `lib/menus.ts`, resizable persisted layout in `lib/layout.ts`); separation tokens; brighter elevation ramps; QoL (shortcuts overlay, Advance ×N, copy seed, steps/sec, Terminal Clear).
- **2026-09-20** — **F-052:** left Perf rail (`/api/status` diag means); always-on Terminal panel; layer HUD top-left + pointer fix; brighter bathymetry + land clamp 64; remove Working… map overlay.
- **2026-09-19** — **F-051:** runner shell (gray chrome, Play/Pause/`1x`…`Fastest`, World rail, map layer HUD); physical atlas paint; terminal continuous surface.
- **2026-09-19** — **F-050:** scrap placeholder console chrome; rebuild `Terminal.tsx` on shared noun/verb dispatcher.
- **2026-09-19** — **F-049:** CLI as full headless runner (`--seed`, `--steps` via dispatch, repeatable `-c`); placeholder batch path retired.
- **2026-09-19** — **F-048:** shared noun/verb command language (`session`/`pool`/`schema`/`systems`/`diag`); Engine read ports for systems/schema; deprecated flat aliases.
- **2026-09-19** — **F-047:** flat/reuse ElevationRaster paint buffer; MapHost packed-body cache (O(1) soak).
- **2026-09-19** — **F-046:** phase collectors on DiagnosticsHub; crumb absorb 0.01%; advection drops whoMin WxH grid.
- **2026-09-19** — **F-045:** sphere-on-rectangle polar wrap (`SphereTopology`); antipodal camera loop; bold dilated borders; ragged flux fronts; golden refreshed.
- **2026-09-19** — **F-044:** triple-junction flood gap fill; crumb absorb 0.2%; half-edge Plates/Overlay borders; golden dump refreshed.
- **2026-09-19** — **F-044 rollback:** Accept reverted (compromised chat mid-Step); tree back at F-043.
- **2026-09-19** — **F-043:** ridge accretion fill (no global nearest-site/owner); golden dump refreshed.
- **2026-09-19** — **F-042:** `DiagnosticsHub` (collectors, enable/disable, ring history); CLI `stats` / `diag`; paint.wall from MapController.
- **2026-09-19** — **F-041 / G-009 open:** docs lock (diverge ridge; sphere polar wrap; ADR-012; shared commands/observability planned). Active Goal G-009. Runtime unchanged.
- **2026-09-19** — **F-040 / G-008 close:** traditional terminal console (`aethelgard>`, ↑/↓ history); Active Goal none; last completed G-008.
- **2026-09-19** — **F-039:** multi-panel Inspect/Legend cards + mappy neatline/graticule/HUD; panel `localStorage` prefs.
- **2026-09-19** — **F-038:** boundary orogeny O(contacts) from standing `boundaries`; cached MapController step for non-blocking status; Y polar-clamp pan; plates interior/boundary paint; golden refreshed.
- **2026-09-19** — **F-037:** `IntegrateVelocity` from `motion_intent`; pipeline Trace → Interaction → Integrate → ApplyGeometry → Orogeny; golden dump refreshed.
- **2026-09-19** — **F-036:** apply `area_flux` (sink/flood/fission/death); B1 latitude-weighted partition; `plate_velocity` STATIC; single tectonics System pipeline.
- **2026-09-19** — **F-035:** `area_flux` + `motion_intent` (precedence / budgets); Constant velocity still drives motion.
- **2026-09-19** — **F-034:** classified `boundaries`; cylinder topology (wrap X, polar Y); UI blank N/S.
- **2026-09-19** — **F-033:** toroidal plate partition N=12–24 + `plate_registry`; studio pan **horizontal only**.
- **2026-09-19** — **F-032:** studio loopback pan + zoom-to-fit clamp (tiled torus draw); Voronoi distance still F-033.
- **2026-09-19** — **F-031:** `WorldSpec.VIEW` is **1920×1080**; host/raster smoke; DEFAULT dump unchanged.
- **2026-09-19** — **F-030:** G-008 wiki locks ([tectonics.md](../product/wiki/tectonics.md)); VIEW 1920×1080 documented; runtime unchanged.
- **2026-09-19** — **G-008** Boundary tectonics + cartography studio registered (1920×1080 torus; plate do-over; studio panels). Steps F-030–F-040 planned. Active Goal G-008.
- **2026-09-19** — **F-029:** shortcuts, seed QoL, busy/offline polish, a11y; **G-007 complete**.
- **2026-09-19** — **F-028:** pan/zoom + cell pick + reset view (`viewport.ts`).
- **2026-09-19** — **F-027:** studio cartography style guide + map-first Next shell (collapsible dock, console drawer). F-026 Active-Goal witness amended for G-007.
- **2026-09-19** — **G-007** Studio cartography tool registered (Next redesign + QoL); Steps F-027–F-029 planned. Active Goal G-007.
- **2026-09-19** — **F-026:** Tauri desktop under `ui/desktop/`; Swing map UI removed; **G-006 complete**.
- **2026-09-19** — **F-025:** Next.js tool under `ui/web/` against MapHost (elevated visuals; client-timed Play).
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
