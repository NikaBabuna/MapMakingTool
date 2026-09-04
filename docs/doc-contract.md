<!--
  File: docs/doc-contract.md
  Purpose: Source and doc sync obligations
  Audience: Agents at Accept time
  Update when: New artifacts appear or ties change
-->

# Documentation contract

Every modified artifact ⇒ update every tied doc. Incomplete docs ⇒ incomplete work.

---

## Global ties

| Artifact | Tied docs |
|----------|-----------|
| Any folder under `docs/` | [navigation.md](navigation.md) |
| Structural move/rename | [navigation.md](navigation.md), [project/changelog.md](project/changelog.md) |
| Scope change | [project/project.md](project/project.md), optionally [project/decisions.md](project/decisions.md) |
| Phase change | [PHASE.md](PHASE.md), [navigation.md](navigation.md), [project/changelog.md](project/changelog.md) |
| Process rule change | [process/rules.md](process/rules.md), [process/step-procedure.md](process/step-procedure.md), [process/global-prompt.md](process/global-prompt.md), [../AGENTS.md](../AGENTS.md), [../.cursor/rules/](../.cursor/rules/) |
| Goal / Session / Step progress | [project/goals.md](project/goals.md), active Goal file, [project/session.md](project/session.md), [project/features.md](project/features.md) |

---

## Engine

| Path | Tied docs |
|------|-----------|
| `docs/engine/specs/*.md` | [engine/glossary.md](engine/glossary.md), [navigation.md](navigation.md) |
| Engine source (when exists) | [engine/architecture.md](engine/architecture.md), [architecture.md](architecture.md), relevant specs |

---

## Product

| Path | Tied docs |
|------|-----------|
| [product/concept.md](product/concept.md) | [product/flows.md](product/flows.md), [product/glossary.md](product/glossary.md) |
| [product/flows.md](product/flows.md) | [project/features.md](project/features.md), relevant blockers |
| Domain wiki pages | [product/wiki/README.md](product/wiki/README.md), [navigation.md](navigation.md) |

---

## Features and blockers

| Path | Tied docs |
|------|-----------|
| `docs/blockers/F-0xx.md` | Approved FRs (at APPROVE) + test mapping; [project/features.md](project/features.md); active Goal |
| Step implementation | Blocker doc, features registry, Goal progress, session marks; **prior Step tests must remain green** |

---

## Source

| Path | Tied docs |
|------|-----------|
| `pom.xml`, `engine/pom.xml`, `mvnw*` | [engine/architecture.md](engine/architecture.md), [architecture.md](architecture.md), [project/decisions.md](project/decisions.md) (ADR-007) |
| `engine/src/main/java/**` | [engine/architecture.md](engine/architecture.md), relevant [engine/specs/](engine/specs/) |
| `engine/src/test/java/**` | Matching `blockers/F-0xx.md` |

---

## File headers

Docs use an HTML comment header: File, Purpose, Audience, Update when.

Source files: add header when first created per [process/rules.md](process/rules.md).
