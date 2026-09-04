<!--
  File: docs/navigation.md
  Purpose: Documentation map — authoritative index
  Audience: Agents and humans
  Update when: Any doc or folder is added, moved, or removed
-->

# Navigation

**Phase:** alpha ([PHASE.md](PHASE.md))  
**Active Goal:** [project/goals/G-001-engine-skeleton.md](project/goals/G-001-engine-skeleton.md)  
**Session:** [project/session.md](project/session.md)  
**Code:** parent + `engine/` (Java 21) — see [engine/architecture.md](engine/architecture.md)

---

## Entry points

| Audience | Start |
|----------|-------|
| Agents | [../AGENTS.md](../AGENTS.md) |
| Humans | [../README.md](../README.md) |
| Process | [process/README.md](process/README.md) |
| How AI works a Step | [process/step-procedure.md](process/step-procedure.md) |

---

## Process (`docs/process/`)

| Doc | Status |
|-----|--------|
| [global-prompt.md](process/global-prompt.md) | Active |
| [rules.md](process/rules.md) | Active |
| [step-procedure.md](process/step-procedure.md) | Active — binding |
| [quality.md](process/quality.md) | Active |
| [protocol-overview.md](process/protocol-overview.md) | Active |

---

## Project (`docs/project/`)

| Doc | Status |
|-----|--------|
| [project.md](project/project.md) | Active |
| [goals.md](project/goals.md) | Active |
| [goals/G-001-engine-skeleton.md](project/goals/G-001-engine-skeleton.md) | Active (in progress) |
| [session.md](project/session.md) | Active (temporary) |
| [features.md](project/features.md) | Active — G-001 Steps registered |
| [roadmap.md](project/roadmap.md) | Active |
| [backlog.md](project/backlog.md) | Active |
| [decisions.md](project/decisions.md) | Active (8 ADRs) |
| [changelog.md](project/changelog.md) | Active |

---

## Cross-cutting

| Doc | Status |
|-----|--------|
| [PHASE.md](PHASE.md) | Active |
| [architecture.md](architecture.md) | Active (roll-up; through F-003) |
| [doc-contract.md](doc-contract.md) | Active |

---

## Engine (`docs/engine/`)

| Doc | Status |
|-----|--------|
| [glossary.md](engine/glossary.md) | Active |
| [architecture.md](engine/architecture.md) | Active — F-003 events + diagnostics |
| [specs/](engine/specs/) | Active (see folder README) — specs run ahead of code for F-004+ |
| [specs/open-questions.md](engine/specs/open-questions.md) | Partial — #1, #2a, and #4 still open |

---

## Product (`docs/product/`)

| Doc | Status |
|-----|--------|
| [concept.md](product/concept.md) | Active |
| [flows.md](product/flows.md) | Deferred until after G-001 |
| [glossary.md](product/glossary.md) | Minimal |
| [style-guide.md](product/style-guide.md) | Deferred |
| [wiki/](product/wiki/) | Empty — deferred |

---

## Blockers (`docs/blockers/`)

| Doc | Status |
|-----|--------|
| [README.md](blockers/README.md) | Active |
| [F-001.md](blockers/F-001.md) | done — layout + Maven scaffold |
| [F-002.md](blockers/F-002.md) | done — Pool + Step loop |
| [F-003.md](blockers/F-003.md) | done — events + claiming + diagnostics |
| [F-009.md](blockers/F-009.md) | done — CI pipeline |

---

## Root

| Path | Purpose |
|------|---------|
| [../AGENTS.md](../AGENTS.md) | Agent entry |
| [../README.md](../README.md) | Human entry |
| [../.cursor/rules/](../.cursor/rules/) | IDE rules |
| [../.github/workflows/ci.yml](../.github/workflows/ci.yml) | CI — Maven witness (F-009) |
