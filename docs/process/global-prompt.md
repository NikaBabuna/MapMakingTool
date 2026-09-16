<!--
  File: docs/process/global-prompt.md
  Purpose: Standing instructions for every agent session
  Audience: AI agents
  Update when: Process or read order changes
-->

# Global prompt

Standing instructions. Apply on every non-trivial task.

---

## Mission

Raise the probability of **useful attempts** (documentation prior) and **detecting bad attempts** (executable witnesses). Do not rely on model wisdom. Rely on the protocol.

---

## Before any work

Read in order:

1. [../PHASE.md](../PHASE.md)
2. This file
3. [rules.md](rules.md)
4. [step-procedure.md](step-procedure.md) — Goal / Session / Step
5. [quality.md](quality.md)
6. [../project/session.md](../project/session.md) — current session (reconcile first)
7. [../project/goals.md](../project/goals.md) — active Goal
8. [../project/project.md](../project/project.md) — scope
9. [../architecture.md](../architecture.md)
10. [../navigation.md](../navigation.md)
11. Feature/Step-relevant docs and [../blockers/](../blockers/)

---

## Always

- **Docs win** over chat if they conflict.
- Work only within [../project/project.md](../project/project.md) unless scope is expanded in docs first.
- Follow [step-procedure.md](step-procedure.md) for coding Steps.
- After FR approval: **store** them in `docs/blockers/F-0xx.md` before MARK/code.
- Mark Step `in progress` **before** code (torn-step protection).
- Treat `./mvnw test` / `mvnw.cmd test` as Accept — **this Step and all prior Steps** (incremental).
- Sync tied docs at end of Step per [../doc-contract.md](../doc-contract.md) and the **SYNC checklist** in [step-procedure.md](step-procedure.md) — not only FR-named files.
- Do **not** claim Accept if entry points (AGENTS, README, navigation, session, PHASE) disagree with [../project/goals.md](../project/goals.md).
- Explain in clear English: what changed, what it means, what was tested.
- On uncertain or torn state: **rollback / reconcile first** ([quality.md](quality.md)).

---

## Never

- Claim `done` without a green witness.
- Claim `done` with green tests but stale Active Goal / “through F-00x” / “deferred until…” docs this Step made false.
- Continue a torn Step as if it succeeded — rollback instead.
- Weaken or delete tests to get green.
- Invent deep package trees ahead of architecture docs and ADRs.
- Start code before user approval of job + functional requirements.
- Leave domain content only in chat — persist under [../product/wiki/](../product/wiki/).

---

## Default autonomy

New chat or “continue”:

1. Reconcile Session + Step marks  
2. If torn → rollback and report  
3. Confirm Active Goal wording matches [../project/goals.md](../project/goals.md) across entry points; if drift → fix docs before new work  
4. Else work Session / next Step of active Goal per procedure  
5. If no active Goal → propose next **Goal**; if Session empty under an active Goal → propose next Step — wait for approval  
6. After Accept → re-check entry points vs `goals.md` before proposing further work  
