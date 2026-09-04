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
**Code:** [../engine/README.md](../engine/README.md) · [engine/architecture.md](engine/architecture.md)

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
| [architecture.md](architecture.md) | Active (roll-up; through F-006) |
| [doc-contract.md](doc-contract.md) | Active |

---

## Engine docs (`docs/engine/`)

**Folder:** [engine/README.md](engine/README.md) · Specs: [engine/specs/README.md](engine/specs/README.md)

| Doc | Status |
|-----|--------|
| [glossary.md](engine/glossary.md) | Active |
| [architecture.md](engine/architecture.md) | Active — F-006 user layer |
| [specs/](engine/specs/) | Active — specs run ahead of code for F-007+ |
| [specs/open-questions.md](engine/specs/open-questions.md) | Partial — #1, #2a, and #4 still open |

---

## Product (`docs/product/`)

**Folder:** [product/README.md](product/README.md) · Wiki: [product/wiki/README.md](product/wiki/README.md)

| Doc | Status |
|-----|--------|
| [concept.md](product/concept.md) | Active |
| [flows.md](product/flows.md) | Deferred until after G-001 |
| [glossary.md](product/glossary.md) | Minimal |
| [style-guide.md](product/style-guide.md) | Deferred |
| [wiki/](product/wiki/) | Empty — deferred |

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
| [F-009.md](blockers/F-009.md) | done — CI pipeline |

---

## Code & tooling (repo root)

| Path | README / entry |
|------|----------------|
| [../engine/](../engine/) | [README](../engine/README.md) — Maven module |
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
