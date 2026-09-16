# Blockers

Per-**Step** quality bars and **canonical store for approved functional requirements**.

Naming: `F-0xx.md` matching [../project/features.md](../project/features.md).

**Process:** [../process/step-procedure.md](../process/step-procedure.md) · [../process/quality.md](../process/quality.md)

---

## Rules

1. **STORE FRs at APPROVE** — create `F-0xx.md` with the approved FR list **before** MARK and before code.
2. **TESTS after code** — map each FR to test IDs; implement them.
3. **INCREMENTAL** — Accept requires this file’s tests **and** every earlier Accepted Step’s tests to stay green.
4. Chat is not the FR store. The file is.

---

## Template (`F-0xx.md`)

Copy when starting a Step (at STORE):

```markdown
<!--
  File: docs/blockers/F-0xx.md
  Purpose: Approved FRs + test mapping for Step F-0xx
  Audience: Agents and humans
  Update when: FRs change (with approval) or tests are added
-->

# F-0xx — <short name>

**Goal:** G-0xx  
**Status:** `fr-approved` | `in progress` | `done` | `rolled back`

## Job (approved)

<one paragraph>

## Functional requirements (approved)

| ID | Requirement (measurable) | Status |
|----|--------------------------|--------|
| FR-1 | … | pending test |
| FR-2 | … | pending test |

## Test mapping

| FR | Test ID / class method | Notes |
|----|------------------------|-------|
| FR-1 | _(fill after CODE)_ | |

## Incremental note

This Step’s Accept requires the full suite: prior Accepted Steps + tests above.

## SYNC (before Accept)

- [ ] Doc-contract ties for touched artifacts
- [ ] Entry points match `goals.md` (session, navigation, AGENTS, protocol rule, PHASE, README, architecture)
- [ ] Navigation Status cells for touched docs not stale
- [ ] Glossary terms for new public types/ports (or N/A: …)
- [ ] Specs / architecture code-status banners match this Step (or N/A: …)
- [ ] Goal close entry-point reconcile (or N/A: Goal not closed)

## Witness

- [ ] New tests green
- [ ] All prior Step tests green
- [ ] Docs synced (SYNC checklist above)
```

**Status `fr-approved`:** FRs stored, code not started (or not yet marked in progress).  
After MARK → `in progress`. After Accept → `done`.

SYNC checklist detail: [../process/step-procedure.md](../process/step-procedure.md) · [../doc-contract.md](../doc-contract.md).

---

## Registry

| Blocker doc | Step | Goal | Status |
|-------------|------|------|--------|
| [F-001.md](F-001.md) | F-001 | G-001 | done |
| [F-002.md](F-002.md) | F-002 | G-001 | done |
| [F-003.md](F-003.md) | F-003 | G-001 | done |
| [F-004.md](F-004.md) | F-004 | G-001 | done |
| [F-005.md](F-005.md) | F-005 | G-001 | done |
| [F-006.md](F-006.md) | F-006 | G-001 | done |
| [F-007.md](F-007.md) | F-007 | G-001 | done |
| [F-008.md](F-008.md) | F-008 | G-001 | done |
| [F-009.md](F-009.md) | F-009 | G-001 | done |
| [F-010.md](F-010.md) | F-010 | G-002 | done |
| [F-011.md](F-011.md) | F-011 | G-002 | done |
| [F-012.md](F-012.md) | F-012 | G-002 | done |

Update this table when creating or closing a blocker file.
