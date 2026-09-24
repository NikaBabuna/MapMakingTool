<!--
  File: docs/protocol/blueprints/paperwork/backlog.md
  Purpose: Shape of the backlog (docs/paperwork/backlog.md), and the operations that edit it
  Audience: Agents
  Update when: The row forms or an operation change
-->

# Backlog

**Shapes:** `docs/paperwork/backlog.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the roadmap ([roadmap.md](roadmap.md)) — the order of Goals. A backlog row is not a Goal and is not being worked.

Ideas that are neither Goals nor Steps. A row here is never permission to build. Promotion is **Open goal**, which needs the human's approval of a Goal text.

## Skeleton

```markdown
<!--
  File: docs/paperwork/backlog.md
  Purpose: Work candidates not yet promoted to Goals/Steps
  Audience: Humans and agents
  Update when: Backlog changes
-->

# Backlog

Items here are **not** in progress. Promote into a Goal or Step when ready.

| Idea | Notes |
|------|-------|
| <idea> | <where it was deferred from, or After <condition>> |
| <idea> | Promoted to [G-0xx](goals/G-0xx-<slug>.md) |
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening sentence | yes | As in the Skeleton |
| Waiting row | no | `\| <idea> \| <Deferred from G-0xx \| After <condition> \| <one clause>> \|` |
| Promoted row | no | `\| <idea> \| Promoted to [G-0xx](goals/G-0xx-<slug>.md) \|`. A promoted row is never deleted |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add idea** | [../../flows/goal.md](../../flows/goal.md) Capture idea |
| **Mark promoted** | [../../flows/goal.md](../../flows/goal.md) Open goal (W5) |

### Add idea

**Before.** The human stated the idea, and approved that it be captured.

**Edit.**

1. Add at the bottom of the table: `| <idea, in the human's words> | <note> |`.

### Mark promoted

**Edit.**

1. In the idea's row, replace the Notes cell with `Promoted to [G-0xx](goals/G-0xx-<slug>.md)`.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | No row says `in progress` |
| 2 | Every `Promoted to` link resolves to a Goal file |

## Keep out

- Requirements and implementation notes: Step records and architecture pages.
