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
| [F-013.md](F-013.md) | F-013 | G-003 | done |
| [F-014.md](F-014.md) | F-014 | G-003 | done |
| [F-015.md](F-015.md) | F-015 | G-003 | done |
| [F-016.md](F-016.md) | F-016 | G-003 | done |
| [F-017.md](F-017.md) | F-017 | G-004 | done |
| [F-018.md](F-018.md) | F-018 | G-004 | done |
| [F-019.md](F-019.md) | F-019 | G-005 | done |
| [F-020.md](F-020.md) | F-020 | G-005 | done |
| [F-021.md](F-021.md) | F-021 | G-005 | done |
| [F-022.md](F-022.md) | F-022 | G-005 | done |
| [F-023.md](F-023.md) | F-023 | G-005 | done |
| [F-024.md](F-024.md) | F-024 | G-006 | done |
| [F-025.md](F-025.md) | F-025 | G-006 | done |
| [F-027.md](F-027.md) | F-027 | G-007 | done |
| [F-028.md](F-028.md) | F-028 | G-007 | done |
| [F-029.md](F-029.md) | F-029 | G-007 | done |
| [F-030.md](F-030.md) | F-030 | G-008 | done |
| [F-031.md](F-031.md) | F-031 | G-008 | done |
| [F-032.md](F-032.md) | F-032 | G-008 | done |
| [F-049.md](F-049.md) | F-049 | G-009 | done |
| [F-050.md](F-050.md) | F-050 | G-009 | done |
| [F-051.md](F-051.md) | F-051 | G-009 | done |
| [F-052.md](F-052.md) | F-052 | G-009 | done |
| [F-053.md](F-053.md) | F-053 | G-009 | done |
| [F-054.md](F-054.md) | F-054 | G-009 | done |
| [F-055.md](F-055.md) | F-055 | G-010 | done |
| [F-056.md](F-056.md) | F-056 | G-010 | done |

Update this table when creating or closing a blocker file.
