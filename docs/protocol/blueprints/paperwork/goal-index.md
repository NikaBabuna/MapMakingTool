<!--
  File: docs/protocol/blueprints/paperwork/goal-index.md
  Purpose: Shape of the Goal index (docs/paperwork/goals.md), and the operations that edit it
  Audience: Agents
  Update when: The index shape, the Active Goal line, or an operation changes
-->

# Goal index

**Shapes:** `docs/paperwork/goals.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a Goal file ([goal.md](goal.md)) — one Goal's content. The Goal pointer ([../doors/goal-pointer.md](../doors/goal-pointer.md)) — ids shown on doors, never the Active Goal sentence.

The only file that carries the Active Goal line. Every door points here instead of repeating it. [../../flows/reconcile.md](../../flows/reconcile.md) reads this line first at every startup.

## Skeleton

```markdown
<!--
  File: docs/paperwork/goals.md
  Purpose: Index of multi-session Goals
  Audience: Agents and humans
  Update when: Goals are added or change status
-->

# Goals

A **Goal** is a durable result across sessions. Steps (`F-0xx`) belong to a Goal.

**Active Goal:** [G-0xx <name>](goals/G-0xx-<slug>.md) · Last completed: [G-0yy <name>](goals/G-0yy-<slug>.md)

| ID | Name | Status | Doc |
|----|------|--------|-----|
| G-0xx | <name> | <not started \| in progress \| done \| abandoned> | [goals/G-0xx-<slug>.md](goals/G-0xx-<slug>.md) |
…

**Status:** `not started` | `in progress` | `done` | `abandoned`

Procedure: [../protocol/flows/goal.md](../protocol/flows/goal.md)
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening sentence | yes | As in the Skeleton |
| Active Goal line | yes | Exactly one line in the file begins `**Active Goal:**`. It has one of three forms: (1) `**Active Goal:** [G-0xx <name>](goals/G-0xx-<slug>.md) · Last completed: [G-0yy <name>](goals/G-0yy-<slug>.md)`; (2) `**Active Goal:** none · Last completed: [G-0yy <name>](goals/G-0yy-<slug>.md)`; (3) either form with `Last completed: none` when no Goal is `done`. "Last completed" names the Goal most recently set to `done`. An `abandoned` Goal is never "last completed" |
| Table | yes | One row per Goal ever opened, in id order. Columns `ID`, `Name`, `Status`, `Doc`. **Name** equals the Goal file's title after `— `. **Status** equals the Goal file's `**Status:**` value. At most one row is `in progress` |
| Status legend | yes | As in the Skeleton |
| Procedure line | yes | As in the Skeleton |

## Naming

**Next Goal id:** the highest `G-` number in the table, plus one, in three digits.

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Set active goal** | [../../flows/goal.md](../../flows/goal.md) Open goal (W2) |
| **Clear active goal** | [../../flows/goal.md](../../flows/goal.md) Close goal (W2), Abandon goal (W2) |
| **Add row** | [../../flows/goal.md](../../flows/goal.md) Open goal (W2) |
| **Set row status** | [../../flows/goal.md](../../flows/goal.md) Close goal (W2), Abandon goal (W2) |
| **Rename row** | [../../flows/goal.md](../../flows/goal.md) Amend goal (W2) |

### Set active goal

**Before.** No row is `in progress` other than the Goal being opened.

**Edit.**

1. Replace the whole line that begins `**Active Goal:**` with  
   `**Active Goal:** [G-0xx <name>](goals/G-0xx-<slug>.md) · Last completed: <the "Last completed" part of the old line, unchanged>`.

**Result.** The line names the new Goal. "Last completed" is unchanged.

### Clear active goal

**Edit.**

1. If the Goal being closed is `done`: replace the line with  
   `**Active Goal:** none · Last completed: [G-0xx <name>](goals/G-0xx-<slug>.md)`, naming the Goal just closed.  
2. If the Goal is being abandoned: replace the line with  
   `**Active Goal:** none · Last completed: <the "Last completed" part of the old line, unchanged>`.

**Result.** The line says `none`.

### Add row

**Edit.**

1. Add at the bottom of the table:  
   `| G-0xx | <name> | in progress | [goals/G-0xx-<slug>.md](goals/G-0xx-<slug>.md) |`

### Set row status

**Edit.**

1. In the row whose ID is `G-0xx`, replace the Status cell with `done` or `abandoned`.

### Rename row

**Edit.**

1. In the row whose ID is `G-0xx`, replace the Name cell with the approved name.  
2. If that Goal is on the Active Goal line, or is its "Last completed" Goal, replace the name inside that link text as well.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Exactly one line begins `**Active Goal:**`, in one of the three forms |
| 2 | If the line names a Goal, that Goal's row is `in progress`. If it says `none`, no row is `in progress` |
| 3 | Each row's Name and Status equal the Goal file's title and `**Status:**` |
| 4 | Every Doc link resolves |

## Keep out

- Step requirements and Step status: the Step registry and records.  
- Claims and progress: the Goal file.  
- Any second copy of the Active Goal line, anywhere else.
