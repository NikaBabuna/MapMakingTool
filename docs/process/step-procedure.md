<!--
  File: docs/process/step-procedure.md
  Purpose: Exact Goal / Session / Step procedure for every AI coding job
  Audience: AI agents (mandatory); humans reviewing process
  Update when: How AI works with code or docs changes
-->

# Step procedure

This is the **binding** procedure for coding work. Chat cannot override it. Docs win over chat ([rules.md](rules.md)).

---

## Hierarchy

| Level | What it is | Persistence | File |
|-------|------------|-------------|------|
| **Goal** | Result we want across sessions; tracks progress | Permanent until done | [../project/goals/](../project/goals/) + [../project/goals.md](../project/goals.md) |
| **Session** | What this chat must achieve; keeps the agent in line | Temporary — rewrite or clear when session ends | [../project/session.md](../project/session.md) |
| **Step** | One AI job: negotiate → code → witness → sync → commit | Permanent registry | [../project/features.md](../project/features.md) (ID `F-0xx`) + [../blockers/F-0xx.md](../blockers/) |

A **Goal** groups many **Steps**. Steps share a Goal ID so the registry stays readable.

A **Session** points at one Goal and lists which Steps it will attempt. Prefer session size that can finish cleanly; incomplete Steps are rolled back (see below).

---

## Step lifecycle (mandatory order)

```
1. PROPOSE     Agent states the job for this Step (scope, files, intent) in plain English
2. REQUIRE     Agent drafts functional requirements (what must stay true later)
3. APPROVE     User approves job + requirements — no code before this
4. STORE       Write approved FRs into docs/blockers/F-0xx.md (canonical store)
               — before MARK and before any code. Chat is not the store.
5. MARK        Mark Step `in progress` in features.md + session.md + Goal
               (torn-step signal — BEFORE code)
6. PLAN        Agent states the code/doc change plan (what will be edited)
7. CODE        After plan is accepted (same approval wave OK if user already approved job+FRs),
               implement in one swoop (many files allowed)
8. TESTS       Implement tests mapped to the stored FRs; update F-0xx.md with test IDs
9. WITNESS     ./mvnw test (or mvnw.cmd) — THIS Step’s new tests AND all prior Steps’ tests
10. FIX LOOP   On failure: analyze → fix → retest.
               If failures persist: STOP, explain in English, ask whether to continue.
               If user declines → ROLLBACK to last Accept
11. SYNC       Update tied docs (doc-contract) — **full reconcile, not Step-local only**
               (see SYNC checklist below); bookkeeping; Goal progress
12. CHECK      Final review: suite green; docs match code; entry points match goals.md
13. COMMIT     Commit the Step (seals Accept)
14. CLOSE MARK Clear `in progress`; set Step `done` only when witness held
```

**Done / Accept** only after the **incremental** suite is green **and** SYNC/CHECK docs hold. Markdown never Accepts alone.

---

## SYNC checklist (mandatory before CHECK)

Complete every row that applies. Record N/A + reason in the Step’s `F-0xx.md` Witness / SYNC section when a row does not apply.

| Check | Requirement |
|-------|-------------|
| **Doc-contract ties** | Every modified source/doc artifact’s ties in [../doc-contract.md](../doc-contract.md) are updated |
| **Entry points** | [../project/goals.md](../project/goals.md) Active Goal text matches [../project/session.md](../project/session.md), [../navigation.md](../navigation.md) header, [../../AGENTS.md](../../AGENTS.md), [../../.cursor/rules/protocol.mdc](../../.cursor/rules/protocol.mdc), [../PHASE.md](../PHASE.md), [../../README.md](../../README.md), [../architecture.md](../architecture.md) |
| **Navigation status** | [../navigation.md](../navigation.md) Status cells for touched docs are not stale (“through F-00x”, “after G-00x”, “deferred until…”) |
| **Glossary** | New public engine terms/ports appear in [../engine/glossary.md](../engine/glossary.md) (or product glossary if product-only) |
| **Specs banners** | Relevant [../engine/specs/](../engine/specs/) (and architecture) **code-status** banners match the Step’s reality |
| **Goal close** | If this Step marks a Goal `done` (or changes Active Goal): run the **Goal status and entry points** table in [../doc-contract.md](../doc-contract.md) in full |

**Forbidden:** claiming Accept because an FR only grepped one architecture string while entry points or banners still describe the previous Goal/Step world.

---

## CHECK (mandatory before COMMIT)

1. Incremental suite green (this Step + all prior Accepted Steps).
2. SYNC checklist complete (or N/A recorded).
3. `goals.md` Active Goal wording agrees with AGENTS / navigation / README / session.
4. No known stale “Deferred until after G-00x” that this Goal already unlocked.

---

## Functional requirements (must be stored)

Approved FRs are **permanent**. Canonical file: `docs/blockers/F-0xx.md`.

| Rule | Meaning |
|------|---------|
| **Store at APPROVE** | Immediately after user approval, write the FR list into `F-0xx.md` (see [../blockers/README.md](../blockers/README.md) template). |
| **Before code** | No production code until FRs are on disk in that file. |
| **Chat is not authority** | If chat and `F-0xx.md` disagree, the file wins — update the file deliberately with user approval. |
| **Survive sessions** | A new chat reads FRs from `F-0xx.md`, not from memory. |
| **Tests map to FRs** | After code, each FR has at least one test; record the mapping in the same file. |
| **Do not weaken** | Deleting or softening FRs/tests to get green is forbidden. |

Requirements must be measurable, derived from the Goal and applicable specs, and negotiated with the user.

---

## Incremental witness

The suite is **cumulative**.

```
Accept(F-00n)  ⇔  tests(F-00n) green
               ∧  tests(F-001) … tests(F-00n-1) still green
               ∧  docs synced per doc-contract + SYNC checklist ∧ in scope
```

- New Step must not break older Steps.
- “Green new + red old” ⇒ Rejected (regression). Fix or roll back.
- Do not remove or skip prior Step tests to pass the current Step.
- Goal checklists (e.g. G-001 framework claims) are closed only when the matching tests exist and stay green across later Steps.

---

## Torn Step / recovery

If a new session finds any of:

- Step marked `in progress`
- `session.md` claiming an unfinished Step
- Code or tests that do not match a completed Accept
- `F-0xx.md` with FRs but Step never Accepted (and code half-done)
- Partial tests without a green incremental witness

…treat the Step as **torn**.

**Mandatory action:** ROLLBACK toward last Accept (discard in-progress Step work), clear false progress marks, explain what was rolled back, then ask the user how to proceed. Do **not** continue a torn Step as if it succeeded.

If FRs were STORED but code never started, keep the FR file only if the user still wants that Step; otherwise delete or revert it with the rollback.

> Failure must never look like success.

**No prior Accept yet:** rollback means discard uncommitted Step work and clear `in progress` marks; restore docs/code to the last committed state (or empty tree if none).

---

## Communication (human judgment)

Every propose / plan / stop / sync summary must be in clear English:

- What changed in the code
- What that means for behavior
- What was tested (this Step + confirmation prior Steps still hold)
- What remains for the Goal

---

## What agents may do without asking

- Create/update docs **within protocol** (including writing approved FRs into `blockers/F-0xx.md`)
- Fix adjacent docs required by [../doc-contract.md](../doc-contract.md)

## What requires asking first

- Starting a Step (job + requirements approval)
- Changing stored FRs after approval
- Refactors beyond the approved Step bar
- Scope expansion
- Continuing after persistent test failure
- Package naming / layout when not yet in architecture docs

---

## Default on “continue” / new chat

1. Read [../PHASE.md](../PHASE.md)
2. Read [../project/session.md](../project/session.md) and active Goal
3. Reconcile (torn Step? → rollback)
4. If no active Goal: **propose next Goal**, wait for approval  
5. If Session empty under an active Goal: propose next Step, wait for approval  
6. Otherwise: finish Session Steps per this procedure  
7. After any Accept: confirm entry-point Active Goal text still matches [../project/goals.md](../project/goals.md) before proposing further work
