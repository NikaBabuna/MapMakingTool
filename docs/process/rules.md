<!--
  File: docs/process/rules.md
  Purpose: Binding implementation and change rules
  Audience: AI agents and humans
  Update when: Discipline rules change
-->

# Rules

Binding rules. **Docs win over chat.** Conflicts with chat are resolved in favor of repository docs, especially this file, [project.md](../project/project.md), and [step-procedure.md](step-procedure.md).

---

## 1. Scope

- Only work listed as in-scope in [../project/project.md](../project/project.md).
- Expand scope by updating `project.md` first; add an ADR when the change is technical or structural.

---

## 2. Architecture

- No invented architecture: no deep package trees or frameworks ahead of [../architecture.md](../architecture.md) and ADRs.
- Smallest structure that ships the Step.
- Touch only files required for the approved Step; adjacent **docs** may be updated when [../doc-contract.md](../doc-contract.md) requires it.
- **Refactors beyond the Step bar:** ask first.

---

## 3. Language and build

- **Language:** Java unless an accepted ADR says otherwise.
- **JDK:** latest LTS available on the machine (record exact version in [../engine/architecture.md](../engine/architecture.md) when scaffolding).
- **Build:** Maven with wrapper (`mvnw` / `mvnw.cmd`) once scaffolded.
- **Witness:** full **incremental** suite via Maven tests (this Step + all prior Accepted Steps) — no exceptions without ADR + user approval.
- **FRs:** approved functional requirements are stored in `docs/blockers/F-0xx.md` before code ([step-procedure.md](step-procedure.md)).

---

## 4. Commits

- After a Step’s final check (witness green, docs synced), the agent **commits** that Step.
- Do not commit a torn or failed Step.
- Do not push unless the user asks.

---

## 5. Documentation

- AI updates docs as part of every Step (within protocol).
- Every modified source file ⇒ update every tied doc in [../doc-contract.md](../doc-contract.md).
- Update [../navigation.md](../navigation.md) when folders or major docs appear, move, or are removed.
- **Alpha structural changes:** also log in [../project/changelog.md](../project/changelog.md).
- Progress marks: set Step `in progress` before code; clear on Accept or rollback.

---

## 6. Domain content

- Worldbuilding and simulation semantics live under [../product/wiki/](../product/wiki/).
- Do not leave domain content only in chat or code.

---

## 7. Tests

- UI tests must be headless-safe (no `JFrame` in tests).
- Blockers are behavioral by default (see [quality.md](quality.md)).
- Engine Goal must eventually uphold all functional claims in [../engine/specs/](../engine/specs/) via tests (see active Goal).

---

## 8. Phase

- Current phase and what may change freely: [../PHASE.md](../PHASE.md).
- Alpha stays fluid; structure changes are logged, not forbidden.
- In **beta** and later: structural changes require ADR; core layout is locked.

---

## 9. Communication

- Explain plans and results in understandable English.
- The user must be able to judge meaning, not only see that files changed.
