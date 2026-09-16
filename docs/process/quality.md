<!--
  File: docs/process/quality.md
  Purpose: Correctness, witnesses, recovery — used with step-procedure.md
  Audience: AI agents and humans
  Update when: Quality bar or recovery rules change
-->

# Quality

Markdown status is not Accept. The executable witness is.

**How a Step runs:** [step-procedure.md](step-procedure.md) (binding order).

---

## Definition of correctness

```
Correct(F-00n)  ⇔  tests for F-00n green
                ∧  tests for all earlier Accepted Steps green   ← incremental
                ∧  docs required by doc-contract are synced
                ∧  SYNC checklist held (entry points / banners / glossary as applicable)
                ∧  approved FRs stored in docs/blockers/F-00n.md
                ∧  scope ∈ project.md
                ∧  Step belongs to an active Goal (or explicit doc-only Session)
```

- Compiling alone is **never** sufficient.
- Vague aspirations (“clean code”) are **not** requirements or blockers.
- Weakening or deleting tests (including **prior Step** tests) to get green is **forbidden**.
- If a blocker/FR was wrong: update `F-0xx.md` with rationale and user agreement, then change tests deliberately — never silently.
- **Stale Active Goal / “through F-00x” / “deferred until after G-00x” lines** that this Step made false ⇒ **not Accept** until SYNC fixes them ([../doc-contract.md](../doc-contract.md), [step-procedure.md](step-procedure.md) § SYNC).

**Accept** (`done`, commit of the Step) only when the witness above holds.

---

## Incremental suite

Each Accept **adds** to the bar. Later Steps must keep every earlier Step’s tests green.

| Situation | Result |
|-----------|--------|
| New tests green, old tests red | **Reject** — regression |
| Old tests deleted/skipped to pass | **Forbidden** |
| FR removed from an Accepted Step without ADR + user approval | **Forbidden** |

---

## Functional requirements store

| When | What |
|------|------|
| After user APPROVE | Write FRs into `docs/blockers/F-0xx.md` (canonical) |
| Before MARK / code | File must already contain the approved FR list |
| After CODE | Map each FR → test ID(s) in the same file; implement those tests |

Template: [../blockers/README.md](../blockers/README.md).

---

## Blocker kinds

| Kind | Prefer when |
|------|-------------|
| **Behavioral tests** | Domain logic, rules, algorithms — **default** |
| **Structural / contract tests** | API shape, invariants, validation, illegal states |
| **Other gates** | Build flags, static analysis, goldens, scripts — only when tests cannot express the bar; justify in `F-0xx.md` |

---

## Blocked states (do not claim done)

| State | Meaning |
|-------|---------|
| No user approval for the Step | Blocked — negotiate first |
| Approved FRs not in `F-0xx.md` | Blocked — STORE before code |
| No progress mark before code | Blocked — MARK first |
| Tests missing for stored FRs | Blocked — implement mapping |
| New green, old red | Blocked — fix regressions (incremental) |
| Tests weakened/deleted to pass | Blocked — restore strictness |
| Vague FRs | Blocked — rewrite as measurable |
| Status/`done` without suite witness | **Invalid Accept** — recovery |
| Docs lag / stale entry points or banners after code or Goal close | **Blocked** — SYNC then Accept ([step-procedure.md](step-procedure.md)) |
| Torn Step marks / mid-step debris | **Rollback** |

---

## Spec before a Step

1. Active **Goal** — [../project/goals.md](../project/goals.md)
2. **Step** entry — [../project/features.md](../project/features.md)
3. Architecture notes if layout changes
4. Flows if user-facing

Engine Steps lean on [../engine/specs/](../engine/specs/).

---

## Recovery (agent failure)

> Agents fail. The protocol’s job is that **failure never looks like success.**

### Legal Step states

| State | Allowed claims |
|-------|----------------|
| **Not started** | — |
| **In progress** | Must **not** claim `done`; FRs stored; mark before code |
| **Accept / done** | Incremental witness holds — may commit and mark done |
| **Rolled back** | Clear false done; tree toward last Accept |

### Recovery transition (mandatory after any stop or new chat)

1. Read session + Step marks + any `F-0xx.md` for in-progress Steps.
2. If torn → **ROLLBACK**, clear marks, explain, ask user.
3. Status says done but witness missing or red → demote; fix or roll back.
4. Code+tests green but docs lag → sync docs, then Accept/commit.
5. Incoherent mix → prefer **roll back** to last Accept.
6. Only then start or resume a clean Step.

### Fix loop vs stop

- Transient failure: analyze → fix → retest (including prior Steps).
- Persistent failure: stop, explain, ask. Decline → rollback.

---

## Step checklist

- [ ] Active Goal and Session understood
- [ ] Job + FRs approved by user
- [ ] Approved FRs **stored** in `docs/blockers/F-0xx.md`
- [ ] Step marked `in progress`
- [ ] Change plan stated
- [ ] Code written to plan
- [ ] Tests mapped to FRs and implemented
- [ ] **Incremental** suite green (this Step + all prior Steps)
- [ ] Tied docs synced per [../doc-contract.md](../doc-contract.md); **SYNC checklist** complete ([step-procedure.md](step-procedure.md))
- [ ] Entry points match [../project/goals.md](../project/goals.md); no stale banners this Step made false
- [ ] If Goal closed: Goal-status entry-point reconcile done
- [ ] Final check → commit → mark `done`

---

## Definition of done (Accept)

1. Approved FRs are stored in `F-0xx.md` and were followed.
2. Tests map to those FRs and are green.
3. **All earlier Accepted Steps’ tests remain green.**
4. Code meets [rules.md](rules.md) and this file.
5. Tied docs updated; Goal progress honest; **SYNC checklist** held (not Step-local FR greps alone).
6. On Goal close: all Active Goal entry points agree with `goals.md`.
7. Commit records the Accept.
