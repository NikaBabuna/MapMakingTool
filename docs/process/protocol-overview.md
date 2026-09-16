<!--
  File: docs/process/protocol-overview.md
  Purpose: Protocol theory and division of labor (reference)
  Audience: Humans and agents needing the full picture
  Update when: Protocol theory changes
-->

# Protocol overview

Reference. **Day-to-day:** [global-prompt.md](global-prompt.md), [rules.md](rules.md), [step-procedure.md](step-procedure.md), [quality.md](quality.md).

---

## Purpose

Make work by **stochastic AI agents** checkable and serializable:

1. **Permanent prior** (documentation) — biases what the agent attempts  
2. **Goals** — multi-session results with grouped Steps  
3. **Sessions** — temporary focus for one chat  
4. **Steps** — negotiated jobs with requirements, code, blockers, witness, sync, commit  
5. **Recovery** — torn Steps roll back; failure never looks like success  

---

## Core thesis

| Claim | Meaning |
|-------|---------|
| **Correctness is contractual** | Correct iff blockers green ∧ full suite green ∧ docs synced (doc-contract + SYNC checklist) ∧ in scope |
| **Requirements before code** | User approves FRs; they are **stored** in `blockers/F-0xx.md` before implementation |
| **Blockers witness the code** | Tests map to stored FRs; suite is **incremental** (prior Steps must stay green) |
| **Docs are permanent context** | Docs win over chat; what docs point to is where agents go |
| **Markdown is not Accept** | Only the suite Accepts — and stale entry points / banners block Accept |
| **Steps are serialized** | One Step at a time through the procedure |
| **Agents fail** | Torn progress → rollback |

---

## Documentation roles

| Artifact | Role |
|----------|------|
| [../project/goals.md](../project/goals.md) | Multi-session Goals and progress |
| [../project/session.md](../project/session.md) | Current chat focus (temporary) |
| [../project/features.md](../project/features.md) | Step registry (`F-0xx`) under Goals |
| [../blockers/](../blockers/) | Per-Step quality bars |
| [../project/project.md](../project/project.md) | Hard scope lock |
| [../architecture.md](../architecture.md) | Known structure |
| [../project/decisions.md](../project/decisions.md) | ADRs — no silent architecture invention |
| [../navigation.md](../navigation.md) | Map of the prior |
| Glossaries / wiki / style | Vocabulary and domain content |

---

## Division of labor

| Mechanism | Job |
|-----------|-----|
| Documentation prior | Raise P(useful attempt) |
| Goal + Session | Persist direction; focus one chat |
| Approved requirements | Define what the Step must make true |
| Blockers + suite | Sole Accept witness |
| Step procedure | Propose → approve → mark → code → test → sync → commit |
| Rollback | Honesty after failure or torn state |

---

## Prior hygiene

Stale or contradictory docs lower P(correct attempt). Syncing after each Step keeps the permanent context pointing one way.
