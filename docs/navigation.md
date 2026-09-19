<!--
  File: docs/navigation.md
  Purpose: Documentation map — authoritative index
  Audience: Agents and humans
  Update when: Any doc or folder is added, moved, or removed
-->

# Navigation

**Phase:** alpha ([PHASE.md](PHASE.md))  
**Active Goal:** [G-009 Simulation runner harden](project/goals/G-009-simulation-runner-harden.md) (**in progress**) · Last: [G-008](project/goals/G-008-boundary-tectonics-studio.md) (**done**)  
**Session:** [project/session.md](project/session.md)  
**Code:** [../engine/README.md](../engine/README.md) · [../product/README.md](../product/README.md) · [engine/architecture.md](engine/architecture.md) · [product/architecture.md](product/architecture.md)

Folder indexes are **README.md** in each landmark directory. Prefer those links when entering a folder.

---

## Entry points

| Audience | Start |
|----------|-------|
| Agents | [../AGENTS.md](../AGENTS.md) |
| Humans | [../README.md](../README.md) |
| Docs tree | [README.md](README.md) |
| Process | [process/README.md](process/README.md) |
| How AI works a Step | [process/step-procedure.md](process/step-procedure.md) |

---

## Process (`docs/process/`)

**Folder:** [process/README.md](process/README.md)

| Doc | Status |
|-----|--------|
| [global-prompt.md](process/global-prompt.md) | Active |
| [rules.md](process/rules.md) | Active |
| [step-procedure.md](process/step-procedure.md) | Active — binding |
| [quality.md](process/quality.md) | Active |
| [protocol-overview.md](process/protocol-overview.md) | Active |

---

## Project (`docs/project/`)

**Folder:** [project/README.md](project/README.md) · Goals: [project/goals/README.md](project/goals/README.md)

| Doc | Status |
|-----|--------|
| [project.md](project/project.md) | Active |
| [goals.md](project/goals.md) | Active |
| [goals/G-001-engine-skeleton.md](project/goals/G-001-engine-skeleton.md) | done |
| [goals/G-002-engine-host-readiness.md](project/goals/G-002-engine-host-readiness.md) | done |
| [goals/G-003-first-product-world.md](project/goals/G-003-first-product-world.md) | done |
| [goals/G-004-see-the-world.md](project/goals/G-004-see-the-world.md) | done |
| [goals/G-005-living-map.md](project/goals/G-005-living-map.md) | done |
| [goals/G-006-webview-front.md](project/goals/G-006-webview-front.md) | done |
| [goals/G-007-studio-cartography.md](project/goals/G-007-studio-cartography.md) | done |
| [goals/G-008-boundary-tectonics-studio.md](project/goals/G-008-boundary-tectonics-studio.md) | done |
| [goals/G-009-simulation-runner-harden.md](project/goals/G-009-simulation-runner-harden.md) | in progress |
| [session.md](project/session.md) | Active (temporary) |
| [features.md](project/features.md) | Active — G-001–G-009 Steps registered |
| [roadmap.md](project/roadmap.md) | Active |
| [backlog.md](project/backlog.md) | Active |
| [decisions.md](project/decisions.md) | Active (12 ADRs) |
| [changelog.md](project/changelog.md) | Active |

---

## Cross-cutting

| Doc | Status |
|-----|--------|
| [PHASE.md](PHASE.md) | Active |
| [architecture.md](architecture.md) | Active (roll-up; G-009 in progress; G-008 done) |
| [doc-contract.md](doc-contract.md) | Active — SYNC checklist / Goal-close / host-port ties |

---

## Engine docs (`docs/engine/`)

**Folder:** [engine/README.md](engine/README.md) · Specs: [engine/specs/README.md](engine/specs/README.md)

| Doc | Status |
|-----|--------|
| [glossary.md](engine/glossary.md) | Active |
| [architecture.md](engine/architecture.md) | Active — F-012 host ports; G-002 done; F-023 console; F-024 MapHost in `ui`; F-019 `cli` → `product` |
| [specs/](engine/specs/) | Active — through G-002 (host-ready) |
| [specs/open-questions.md](engine/specs/open-questions.md) | Partial — #1 and #4 still open; #2a decided (ADR-009) |

---

## Product (`docs/product/`)

**Folder:** [product/README.md](product/README.md) · Wiki: [product/wiki/README.md](product/wiki/README.md)

| Doc | Status |
|-----|--------|
| [architecture.md](product/architecture.md) | Active — G-009 F-050 Terminal; F-049 CLI runner; F-048 noun/verb; G-008 done |
| [concept.md](product/concept.md) | Active |
| [flows.md](product/flows.md) | G-009 F-050 Terminal; F-049 CLI; F-048 commands; F-047 raster; G-008 done (through F-040) |
| [glossary.md](product/glossary.md) | Active — Terminal; CliRunner; CommandDispatch; DiagnosticsHub; ElevationRaster flat/reuse |
| [style-guide.md](product/style-guide.md) | Active — G-009 F-050 rebuilt terminal; runner chrome F-051+; G-008 studio shipped |
| [wiki/](product/wiki/) | Active — [tectonics.md](product/wiki/tectonics.md) (F-046 crumb 0.01% + phases) · [world.md](product/wiki/world.md) · [elevation.md](product/wiki/elevation.md) |

---

## Blockers (`docs/blockers/`)

**Folder:** [blockers/README.md](blockers/README.md)

| Doc | Status |
|-----|--------|
| [F-001.md](blockers/F-001.md) | done — layout + Maven scaffold |
| [F-002.md](blockers/F-002.md) | done — Pool + Step loop |
| [F-003.md](blockers/F-003.md) | done — events + claiming + diagnostics |
| [F-004.md](blockers/F-004.md) | done — Systems + typed merge + provenance |
| [F-005.md](blockers/F-005.md) | done — claim/finish + determinism |
| [F-006.md](blockers/F-006.md) | done — User Input / Input View / User View |
| [F-007.md](blockers/F-007.md) | done — CLI runner |
| [F-008.md](blockers/F-008.md) | done — basic UI + G-001 closure |
| [F-009.md](blockers/F-009.md) | done — CI pipeline |
| [F-010.md](blockers/F-010.md) | done — pluggable Pool compute |
| [F-011.md](blockers/F-011.md) | done — Object fields + FieldMergeType |
| [F-012.md](blockers/F-012.md) | done — EventEmissionPolicy + G-002 closure |
| [F-013.md](blockers/F-013.md) | done — product Maven module |
| [F-014.md](blockers/F-014.md) | done — world as Pool state |
| [F-015.md](blockers/F-015.md) | done — first generative process |
| [F-016.md](blockers/F-016.md) | done — witnessed world + G-003 closure |
| [F-017.md](blockers/F-017.md) | done — Voronoi multi-plate tectonics |
| [F-018.md](blockers/F-018.md) | done — large colored map UI |
| [F-019.md](blockers/F-019.md) | done — product session + UI/CLI house |
| [F-020.md](blockers/F-020.md) | done — plate kinematics |
| [F-021.md](blockers/F-021.md) | done — motion-based orogeny |
| [F-022.md](blockers/F-022.md) | done — tool UI |
| [F-023.md](blockers/F-023.md) | done — placeholder CLI + in-UI console |
| [F-024.md](blockers/F-024.md) | done — Java session HTTP host (`ui.host`) |
| [F-025.md](blockers/F-025.md) | done — Next.js tool UI (`ui/web`) |
| [F-026.md](blockers/F-026.md) | done — Tauri desktop; Swing removed; G-006 closed |
| [F-027.md](blockers/F-027.md) | done — studio chrome + map-first shell |
| [F-028.md](blockers/F-028.md) | done — pan / zoom |
| [F-029.md](blockers/F-029.md) | done — shortcuts + QoL; G-007 closed |
| [F-030.md](blockers/F-030.md) | done — G-008 wiki + decisions |
| [F-031.md](blockers/F-031.md) | done — VIEW 1920×1080 |
| [F-032.md](blockers/F-032.md) | done — loopback pan + zoom clamp |
| [F-033.md](blockers/F-033.md) | done — plate partition + registry |
| [F-034.md](blockers/F-034.md) | done — boundaries + cylinder map |
| [F-035.md](blockers/F-035.md) | done — precedence + area_flux + motion_intent |
| [F-036.md](blockers/F-036.md) | done — apply flux + fission + B1 distance |
| [F-037.md](blockers/F-037.md) | done — edge-driven IntegrateVelocity |
| [F-038.md](blockers/F-038.md) | done — boundary orogeny O(contacts) + status/camera/plates |
| [F-039.md](blockers/F-039.md) | done — multi-panel studio + mappy style |
| [F-040.md](blockers/F-040.md) | done — traditional console; G-008 closed |
| [F-041.md](blockers/F-041.md) | done — G-009 docs lock |
| [F-042.md](blockers/F-042.md) | done — DiagnosticsHub + CLI diag/stats |
| [F-043.md](blockers/F-043.md) | done — ridge accretion diverge fill |
| [F-044.md](blockers/F-044.md) | done — flood fill + crumb 0.2% + half-edge borders |
| [F-045.md](blockers/F-045.md) | done — sphere polar wrap + bold/ragged borders |
| [F-046.md](blockers/F-046.md) | done — phase collectors + crumb 0.01% + step-path hotspot |
| [F-047.md](blockers/F-047.md) | done — raster buffer reuse + MapHost packed cache |
| [F-048.md](blockers/F-048.md) | done — noun/verb command language |
| [F-049.md](blockers/F-049.md) | done — CLI as full headless runner |
| [F-050.md](blockers/F-050.md) | done — scrap + rebuild in-app terminal |

---

## Code & tooling (repo root)

| Path | README / entry |
|------|----------------|
| [../engine/](../engine/) | [README](../engine/README.md) — Maven module |
| [../cli/](../cli/) | [README](../cli/README.md) — F-049 headless runner |
| [../ui/](../ui/) | [README](../ui/README.md) — MapHost + headless map · [web](../ui/web/README.md) · [desktop](../ui/desktop/README.md) |
| [../product/](../product/) | [README](../product/README.md) — Maven module |
| [../engine/.../pool/](../engine/src/main/java/com/aethelgard/engine/pool/) | [README](../engine/src/main/java/com/aethelgard/engine/pool/README.md) |
| [../engine/.../event/](../engine/src/main/java/com/aethelgard/engine/event/) | [README](../engine/src/main/java/com/aethelgard/engine/event/README.md) |
| [../engine/.../diag/](../engine/src/main/java/com/aethelgard/engine/diag/) | [README](../engine/src/main/java/com/aethelgard/engine/diag/README.md) |
| [../engine/.../system/](../engine/src/main/java/com/aethelgard/engine/system/) | [README](../engine/src/main/java/com/aethelgard/engine/system/README.md) |
| [../engine/.../merge/](../engine/src/main/java/com/aethelgard/engine/merge/) | [README](../engine/src/main/java/com/aethelgard/engine/merge/README.md) |
| [../engine/.../user/](../engine/src/main/java/com/aethelgard/engine/user/) | [README](../engine/src/main/java/com/aethelgard/engine/user/README.md) |
| [../.github/](../.github/) | [README](../.github/README.md) |
| [../.github/workflows/](../.github/workflows/) | [README](../.github/workflows/README.md) · [ci.yml](../.github/workflows/ci.yml) |
| [../.cursor/](../.cursor/) | [README](../.cursor/README.md) |
| [../.cursor/rules/](../.cursor/rules/) | [README](../.cursor/rules/README.md) |
| [../AGENTS.md](../AGENTS.md) | Agent entry |

**Exempt from README:** build output (`target/`), local bootstrap (`.tools/`), Maven wrapper internals (`.mvn/`), intermediate Java namespace segments (`com/`, `java/`, …).
