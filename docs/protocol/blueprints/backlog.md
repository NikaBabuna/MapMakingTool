<!--
  File: docs/protocol/blueprints/backlog.md
  Purpose: Shape of the backlog
  Audience: Agents and humans
  Update when: The backlog shape changes
-->

# Backlog

This is `docs/paperwork/backlog.md`. It is the list of ideas that are not Goals and not Steps. An idea here is not being worked. If an agent treats a backlog row as permission to code, the row has been misread. Promotion is **Open goal**, which requires the human’s approval of a Goal text.

**Write or edit it when.** An idea is captured, or **Open goal** promotes one.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | “Not in progress. Promote into a Goal when ready.” This sentence is the whole status model |
| Table | The idea, and a note: deferred from where, or promoted to which Goal. A promoted row stays, so the idea can be traced. It does not stay as if it were still waiting |

## Skeleton

```markdown
# Backlog

Items here are not in progress. Promote one into a Goal when the human approves that Goal.

| Idea | Notes |
|------|-------|
| <idea> | Promoted to [G-0xx](goals/G-0xx-<slug>.md) |
| <idea> | After <condition> |
```

## Keep out

A status of `in progress`. Requirements. Implementation notes that belong in a paper.
