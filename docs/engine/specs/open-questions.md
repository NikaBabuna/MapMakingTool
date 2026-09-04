<!--
  File: docs/engine/specs/open-questions.md
  Purpose: Unresolved and resolved framework design gaps
  Audience: Agents and humans
  Update when: Questions are resolved (move to specs or ADR)
-->

# Open questions

Gaps between current spec and complete framework.

## Resolved (for G-001)

| # | Topic | Decision | Where |
|---|-------|----------|-------|
| 2b | **Unmatched events** | **Log** when no System claims the event | [events.md](events.md), ADR-006 |
| 3 | **Step 0 bootstrap** | Seed from a starting **config object** | [pool-engine.md](pool-engine.md), ADR-005 |

## Still open

| # | Topic | Question |
|---|-------|----------|
| 1 | **Non-finishing Systems** | Wait indefinitely, timeout, or proceed without output? Deferred past G-001. |
| 2a | **Category tree authorship** | How the tree is defined and maintained (application concern). |
| 4 | **Delete Request resolution** | Against concurrent write: delete wins or write wins? Deferred past G-001. |

Resolve remaining items via ADR in [../../project/decisions.md](../../project/decisions.md).
