<!--
  File: docs/protocol/blueprints/goal-index.md
  Purpose: Shape of the goal index
  Audience: Agents and humans
  Update when: The goal-index shape changes
-->

# Goal index

This is `docs/project/goals.md`. It is the only file that carries the Active Goal line. Every door points here instead of repeating the sentence. When the line is copied, the copies rot, and agents follow the rotten one. That is why the line is singular.

**Write or edit it when.** **Open goal**, **Amend goal**, or **Close goal** runs.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Active Goal line | `**Active Goal:**` plus a link to the Goal file, and the last completed Goal as a second link. If no Goal is active, the line says none. It still lives only here |
| Table | Id, name, status, link. Status is `not started`, `in progress`, `done`, or `abandoned`. One row per Goal, including finished ones, so the history of results is one table |
| Procedure pointer | A link to the goal flows, so a reader of the index can find the algorithm |

## Skeleton

```markdown
# Goals

**Active Goal:** [G-0xx <name>](goals/G-0xx-<slug>.md) · Last completed: [G-0yy <name>](goals/G-0yy-<slug>.md)

| ID | Name | Status | Doc |
|----|------|--------|-----|
| G-0xx | <name> | in progress | [goals/G-0xx-<slug>.md](goals/G-0xx-<slug>.md) |

Procedure: [../protocol/flows/goals.md](../protocol/flows/goals.md)
```

## Keep out

Step requirements. A second status system. The line duplicated into the header of other files.
