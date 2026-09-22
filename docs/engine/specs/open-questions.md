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

## Resolved (for G-003)

| # | Topic | Decision | Where |
|---|-------|----------|-------|
| 2a | **Category tree authorship** | Product authors the tree in Java (`CategoryTree.of(...)`). Engine does not own a world tree or file format. | ADR-009, [../../product/architecture.md](../../product/architecture.md) |

## Still open

| # | Topic | Question |
|---|-------|----------|
| 1 | **Non-finishing Systems** | Wait indefinitely, timeout, or proceed without output? Still open after G-002. |
| 4 | **Delete Request resolution** | Against concurrent write: delete wins or write wins? Still open after G-002. |

G-002 added host ports (`PoolCompute`, `FieldMergeType`, `EventEmissionPolicy`) and did **not** resolve #1 or #4.

Resolve remaining items via ADR in [../../paperwork/decisions.md](../../paperwork/decisions.md).
