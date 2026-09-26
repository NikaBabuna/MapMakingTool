<!--
  File: docs/protocol/blueprints/paperwork/step-registry.md
  Purpose: Shape of the Step registry (docs/paperwork/steps.md), the Step id rule, and the operations that edit the registry
  Audience: Agents
  Update when: The registry shape, the id rule, or an operation changes
-->

# Step registry

**Shapes:** `docs/paperwork/steps.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a Step record ([step.md](step.md)) — the requirements. The registry holds status, one sentence per Step, and links.

The index of every Step, grouped by Goal, so status can be read without opening every record. It is mark M2 for [../../flows/reconcile.md](../../flows/reconcile.md).

## Naming

**Next Step id:** the highest `F-` number that appears anywhere in this file, or as a file name `docs/paperwork/steps/F-*.md`, plus one, in three digits.

## Skeleton

```markdown
<!--
  File: docs/paperwork/steps.md
  Purpose: Step registry (F-0xx) grouped by Goal
  Audience: Agents and humans
  Update when: Steps are added or change status
-->

# Steps (feature registry)

A **Step** is one job under a **Goal**: agree the work, store its requirements, do it, prove it, and record it. This registry lists every Step, grouped by the Goal it belongs to, with its status and one sentence on what it does. The Step's own record, `docs/paperwork/steps/F-0xx.md`, holds the approved job, the decisions, the requirements, the test that proves each one, and the witness. A record is written when the Step is approved, before any work starts.

**Incremental:** Accept requires this Step's tests **and** all earlier Accepted Steps' tests to stay green, except tests a Step record retires or lists as known defects.

Procedure: [../protocol/flows/step.md](../protocol/flows/step.md)

**Status values:** `not started` (planned, no record yet) | `in progress` (approved and underway; at most one) | `done` (proven and closed) | `rolled back` (the attempt was discarded)

---

## G-0xx — <Goal name>

Goal doc: [goals/G-0xx-<slug>.md](goals/G-0xx-<slug>.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-0xx | <short name> | not started | <one sentence> | — |
…

---
…

## Marking progress

- Set Status to `in progress` before any work on that Step, in the record, here, and on the Goal.  
- Set Status to `done` only after the witness is green and Sync is complete. The `done` marks go into the Step's Accept commit.  
- A torn Step found by Reconcile is rolled back, not continued.
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening lines | yes | The four paragraphs of the Skeleton, word for word |
| Goal section | yes, one per Goal | `---`, then `## G-0xx — <Goal name>`, then `Goal doc: [goals/G-0xx-<slug>.md](goals/G-0xx-<slug>.md)`, then the table. Sections in Goal id order |
| Row | yes | `\| F-0xx \| <short name> \| <status> \| <one sentence> \| <record> \|`. **What it does** is one sentence in plain words: while `not started`, the Intent of the Goal's Planned Steps row; from **Mark in progress** on, what the approved Job makes true. It names no file path, program type, or field. **Record** is `—` while `not started`, and `[F-0xx.md](steps/F-0xx.md)` otherwise. Rows in the order of the Goal's Planned Steps |
| Marking progress | yes | `---`, then the section exactly as in the Skeleton, last in the file |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add goal section** | [../../flows/goal.md](../../flows/goal.md) Open goal (W3) |
| **Add row** | [../../flows/goal.md](../../flows/goal.md) Amend goal (W3) |
| **Remove row** | [../../flows/goal.md](../../flows/goal.md) Amend goal (W3) |
| **Reorder rows** | [../../flows/goal.md](../../flows/goal.md) Amend goal (W3) |
| **Mark in progress** | [../../flows/step.md](../../flows/step.md) MARK (M2) |
| **Mark done** | [../../flows/step.md](../../flows/step.md) CLOSE step 3 |
| **Revert to in progress** | [../../flows/step.md](../../flows/step.md) CLOSE step 5 |

### Add goal section

**Edit.**

1. Directly above the `---` that precedes `## Marking progress`, insert:

```markdown
---

## G-0xx — <Goal name>

Goal doc: [goals/G-0xx-<slug>.md](goals/G-0xx-<slug>.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-0xx | <short name> | not started | <the Intent of that Planned Steps row> | — |
```

2. Write one row per planned Step, in planned order.

### Add row

**Edit.**

1. In the Goal's section, insert `| F-0xx | <short name> | not started | <the Intent of that Planned Steps row> | — |` at the position matching the Goal's Planned Steps.

### Remove row

**Before.** The row is `not started`.

**Edit.**

1. Delete the row.

### Reorder rows

**Edit.**

1. Reorder the rows of the Goal's section to match the Goal's Planned Steps. Change no id.

### Mark in progress

**Before.** The Step record exists.

**Edit.**

1. Replace the row with `| F-0xx | <short name> | in progress | <one sentence: what the approved Job makes true> | [F-0xx.md](steps/F-0xx.md) |`.

### Mark done

**Edit.**

1. In the row, replace `in progress` with `done`. Keep the sentence and the record link.

### Revert to in progress

**Edit.**

1. In the row, replace `done` with `in progress`.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every Goal in the Goal index has one section, and every Planned Steps row of that Goal has one row here, in the same order |
| 2 | Every row that is not `not started` links a record that exists, and the record's `**Status:**` equals the row |
| 3 | At most one row in the whole file is `in progress` |
| 4 | Every **What it does** cell is one sentence and names no file path, program type, or field |

## Keep out

- Requirement text and witness logs: the Step record.  
- The Active Goal line: the Goal index.
