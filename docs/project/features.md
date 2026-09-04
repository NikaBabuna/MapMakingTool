<!--
  File: docs/project/features.md
  Purpose: Step registry (F-0xx) grouped by Goal
  Audience: Agents and humans
  Update when: Steps are added or change status
-->

# Steps (feature registry)

Each **Step** is one AI job under a **Goal**. Approved FRs + test mapping: `docs/blockers/F-0xx.md` (created at APPROVE / STORE, before code).

**Incremental:** Accept requires this Step’s tests **and** all earlier Accepted Steps’ tests to stay green.

Procedure: [../process/step-procedure.md](../process/step-procedure.md)

**Status values:** `not started` | `in progress` | `done` | `rolled back`

---

## G-001 — Engine skeleton

Goal doc: [goals/G-001-engine-skeleton.md](goals/G-001-engine-skeleton.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-001 | Layout + Maven scaffold | done | [F-001.md](../blockers/F-001.md) |
| F-002 | Pool + Step loop + Step 0 config | not started | — |
| F-003 | Events + claiming + unmatched log | not started | — |
| F-004 | Systems + typed merge + provenance | not started | — |
| F-005 | Claim/finish + determinism | not started | — |
| F-006 | User Input / Input View / User View | not started | — |
| F-007 | CLI runner | not started | — |
| F-008 | Basic UI + claim checklist closure | not started | — |
| F-009 | CI pipeline (GitHub Actions) | done | [F-009.md](../blockers/F-009.md) |

---

## Marking progress

- Set Status to `in progress` **before** writing code for that Step.  
- Set to `done` only after green witness + doc sync + commit.  
- Torn `in progress` on a new chat → rollback.
