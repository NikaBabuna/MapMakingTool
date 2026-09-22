<!--
  File: docs/navigation.md
  Purpose: Documentation map — authoritative index
  Audience: Agents and humans
  Update when: Any doc or folder is added, moved, or removed
-->

# Navigation

**Phase:** alpha ([protocol/environment/phase.md](protocol/environment/phase.md))  
**Goal index:** [paperwork/goals.md](paperwork/goals.md) — G-011 Docs restructuring  
**Protocol:** [protocol/README.md](protocol/README.md)  
**Code:** [../engine/README.md](../engine/README.md) · [../product/README.md](../product/README.md) · [engine/architecture.md](engine/architecture.md) · [product/architecture.md](product/architecture.md)

Folder indexes are **README.md** in each landmark directory. Prefer those links when entering a folder.

---

## Entry points

| Audience | Start |
|----------|-------|
| Agents | [../AGENTS.md](../AGENTS.md) |
| Humans | [../README.md](../README.md) |
| Docs tree | [README.md](README.md) |
| Protocol | [protocol/README.md](protocol/README.md) |
| Flows | [protocol/flows/README.md](protocol/flows/README.md) |

---

## Protocol (`docs/protocol/`)

**Folder:** [protocol/README.md](protocol/README.md)

| Room | Status |
|------|--------|
| [environment/](protocol/environment/README.md) | Active — territory, dispatch, dictionary |
| [navigation/](protocol/navigation/README.md) | Active — pointers and bounds |
| [blueprints/](protocol/blueprints/README.md) | Active — one shape per document kind |
| [flows/](protocol/flows/README.md) | Active — bookkeeping algorithms |

---

## Project (`docs/project/`)

**Folder:** [project/README.md](project/README.md)

| Doc | Status |
|-----|--------|
| [project.md](project/project.md) | Active — scope. Progress records are on the paperwork shelf |

---

## Paperwork (`docs/paperwork/`)

**Folder:** [paperwork/README.md](paperwork/README.md)

| Doc | Status |
|-----|--------|
| [goals.md](paperwork/goals.md) | Active — the only Active Goal line |
| [goals/](paperwork/goals/README.md) | Active — one file per Goal. The index holds status |
| [goals/G-001-engine-skeleton.md](paperwork/goals/G-001-engine-skeleton.md) | done |
| [goals/G-002-engine-host-readiness.md](paperwork/goals/G-002-engine-host-readiness.md) | done |
| [goals/G-003-first-product-world.md](paperwork/goals/G-003-first-product-world.md) | done |
| [goals/G-004-see-the-world.md](paperwork/goals/G-004-see-the-world.md) | done |
| [goals/G-005-living-map.md](paperwork/goals/G-005-living-map.md) | done |
| [goals/G-006-webview-front.md](paperwork/goals/G-006-webview-front.md) | done |
| [goals/G-007-studio-cartography.md](paperwork/goals/G-007-studio-cartography.md) | done |
| [goals/G-008-boundary-tectonics-studio.md](paperwork/goals/G-008-boundary-tectonics-studio.md) | done |
| [goals/G-009-simulation-runner-harden.md](paperwork/goals/G-009-simulation-runner-harden.md) | done |
| [goals/G-010-crust-topology.md](paperwork/goals/G-010-crust-topology.md) | done |
| [goals/G-011-docs-restructuring.md](paperwork/goals/G-011-docs-restructuring.md) | in progress — F-066 paperwork shelf done |
| [steps.md](paperwork/steps.md) | Active — step registry. F-066 done. F-065 not started |
| [steps/](paperwork/steps/README.md) | Active — one file per Step |
| [decisions.md](paperwork/decisions.md) | Active — index through ADR-016. Next number is one higher than the last row |
| [decisions/](paperwork/decisions/README.md) | Active — one file per decision |
| [changelog.md](paperwork/changelog.md) | Active |
| [roadmap.md](paperwork/roadmap.md) | Active |
| [backlog.md](paperwork/backlog.md) | Active |

---

## Cross-cutting

| Doc | Status |
|-----|--------|
| [architecture.md](architecture.md) | Active (roll-up; goal index points at G-011) |

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
| [architecture.md](product/architecture.md) | Active — G-010 done (through F-061); G-009 done (through F-054); panel registry + menu model; layer harden; Perf rail; Terminal |
| [concept.md](product/concept.md) | Active — conceptual; no program names |
| [journeys.md](product/journeys.md) | Active — what a person does. Explore, Guide, and Timeline are not built |
| [glossary.md](product/glossary.md) | Active — domain words |
| [style-guide.md](product/style-guide.md) | Active — screen and control labels |
| [wiki/](product/wiki/) | Active — [world.md](product/wiki/world.md) · [tectonics.md](product/wiki/tectonics.md) · [elevation.md](product/wiki/elevation.md) |

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
