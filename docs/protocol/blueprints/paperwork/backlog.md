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

Ideas that are not Goals yet. Nothing here is in progress, and a row is never permission to build. An idea becomes work only when a Goal that contains it is approved. Rows are never deleted: a promoted or settled idea stays, so the list shows where every idea went.

| Idea | What it means | Where it stands |
|------|---------------|-----------------|
| <idea> | <one or two sentences, in plain words> | <Deferred from G-0xx \| After <condition>><. One clause.> |
| <idea> | <one or two sentences> | Promoted to [G-0xx](goals/G-0xx-<slug>.md) |
| <idea> | <one or two sentences> | Settled by <ADR-0xx \| the Step or page that settled it>: <the answer, in one clause> |
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening paragraph | yes | As in the Skeleton, word for word |
| What it means | yes, on every row | One or two sentences a person who was not there can understand. No program type or field name unless the idea is about that name |
| Waiting row | no | `\| <idea> \| <what it means> \| <Deferred from G-0xx \| After <condition>><. One clause.> \|` |
| Promoted row | no | `\| <idea> \| <what it means> \| Promoted to [G-0xx](goals/G-0xx-<slug>.md) \|`. A promoted row is never deleted |
| Settled row | no | `\| <idea> \| <what it means> \| Settled by <where>: <the answer> \|`, for an idea answered without a Goal of its own. A settled row is never deleted |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add idea** | [../../flows/goal.md](../../flows/goal.md) Capture idea |
| **Mark promoted** | [../../flows/goal.md](../../flows/goal.md) Open goal (W5) |

### Add idea

**Before.** The human stated the idea, and approved that it be captured.

**Edit.**

1. Add at the bottom of the table: `| <idea, in the human's words> | <what it means, as the human explained it> | <where it stands> |`.

### Mark promoted

**Edit.**

1. In the idea's row, replace the **Where it stands** cell with `Promoted to [G-0xx](goals/G-0xx-<slug>.md)`.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | No row says `in progress` |
| 2 | Every `Promoted to` link resolves to a Goal file |

## Keep out

- Requirements and implementation notes: Step records and architecture pages.
