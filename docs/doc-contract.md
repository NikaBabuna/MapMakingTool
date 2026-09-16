<!--
  File: docs/doc-contract.md
  Purpose: Source and doc sync obligations
  Audience: Agents at Accept time
  Update when: New artifacts appear or ties change
-->

# Documentation contract

Every modified artifact ⇒ update every tied doc. Incomplete docs ⇒ incomplete work.

**SYNC is not “only the files this Step edited.”** Local FR-named docs can be green while entry points, navigation status lines, glossary terms, and spec banners stay stale. Accept requires the **global reconcile** below when those surfaces are affected.

Binding procedure: [process/step-procedure.md](process/step-procedure.md) § SYNC / CHECK.

---

## Global ties

| Artifact | Tied docs |
|----------|-----------|
| Any folder under `docs/` | [navigation.md](navigation.md), folder `README.md` |
| New landmark folder (see [PHASE.md](PHASE.md)) | `README.md` in that folder + [navigation.md](navigation.md) link |
| Structural move/rename | [navigation.md](navigation.md), [project/changelog.md](project/changelog.md), affected folder READMEs |
| Scope change | [project/project.md](project/project.md), optionally [project/decisions.md](project/decisions.md) |
| Phase change | [PHASE.md](PHASE.md), [navigation.md](navigation.md), [project/changelog.md](project/changelog.md) |
| Process rule change | [process/rules.md](process/rules.md), [process/step-procedure.md](process/step-procedure.md), [process/global-prompt.md](process/global-prompt.md), [../AGENTS.md](../AGENTS.md), [../.cursor/rules/](../.cursor/rules/) |
| Goal / Session / Step progress | [project/goals.md](project/goals.md), active Goal file, [project/session.md](project/session.md), [project/features.md](project/features.md) |

---

## Goal status and entry points (mandatory)

When a Goal is created, marked `in progress`, marked `done`, or abandoned — or when `goals.md` **Active Goal** text changes — reconcile **all** of:

| Surface | Must match [project/goals.md](project/goals.md) |
|---------|--------------------------------------------------|
| [project/session.md](project/session.md) | Active / last Goal |
| [navigation.md](navigation.md) header | Active Goal line |
| [../AGENTS.md](../AGENTS.md) | Active Goal line |
| [../.cursor/rules/protocol.mdc](../.cursor/rules/protocol.mdc) | Active Goal line |
| [PHASE.md](PHASE.md) | Active Goal line |
| [../README.md](../README.md) | Current / Active Goal + docs table links |
| [architecture.md](architecture.md) | Goals section |
| [navigation.md](navigation.md) status rows | “through F-00x / after G-00x / deferred until…” for affected docs |

Do **not** Accept a Goal-closing Step while any of those still name a different active Goal or an obsolete “after G-00x” gate.

---

## Status banners and deferred labels

These are **tied artifacts**, not polish:

- Spec / architecture **“Code status (through F-00x)”** banners for topics the Step changed
- [navigation.md](navigation.md) **Status** column text for those docs
- Product/engine **“Deferred until…”** / **“after G-00x”** lines when a Goal completes or unlocks the next work

If code or Goal status moved past a banner, update or deliberately reword it in the same SYNC.

---

## Engine

| Path | Tied docs |
|------|-----------|
| `docs/engine/specs/*.md` | [engine/glossary.md](engine/glossary.md), [navigation.md](navigation.md) |
| Engine source (when exists) | [engine/architecture.md](engine/architecture.md), [architecture.md](architecture.md), relevant specs |
| **New public type / host port** (e.g. `PoolCompute`, `FieldMergeType`, `EventEmissionPolicy`) | [engine/glossary.md](engine/glossary.md) term; relevant specs’ code-status + body; [engine/architecture.md](engine/architecture.md); [architecture.md](architecture.md) roll-up if it lists host ports |

“Mentioned once in `engine/architecture.md` for an FR” is **not** enough if glossary / specs banners / navigation status still describe the old world.

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
| Step implementation | Blocker doc, features registry, Goal progress, session marks; **prior Step tests must remain green**; SYNC checklist in [blockers/README.md](blockers/README.md) |

---

## Source

| Path | Tied docs |
|------|-----------|
| `pom.xml`, `engine/pom.xml`, `mvnw*` | [engine/architecture.md](engine/architecture.md), [architecture.md](architecture.md), [project/decisions.md](project/decisions.md) (ADR-007) |
| `.github/workflows/*` | [engine/architecture.md](engine/architecture.md), [../README.md](../README.md), matching `blockers/F-0xx.md` |
| `engine/src/main/java/**` | [engine/architecture.md](engine/architecture.md), relevant [engine/specs/](engine/specs/) |
| `engine/src/test/java/**` | Matching `blockers/F-0xx.md` |

---

## File headers

Docs use an HTML comment header: File, Purpose, Audience, Update when.

Source files: add header when first created per [process/rules.md](process/rules.md).
