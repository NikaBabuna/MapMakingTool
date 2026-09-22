<!--
  File: docs/protocol/blueprints/goal.md
  Purpose: Shape of one Goal document
  Audience: Agents and humans
  Update when: The Goal shape changes
-->

# Goal

This is one file, `docs/project/goals/G-0xx-<slug>.md`. It is the durable result: what will be true when the Goal is done, what is refused, which decisions are already made, and which Steps will get there. A living example is `docs/project/goals/G-011-docs-restructuring.md`.

It is not a Step record. Requirements that a check must enforce live in `F-0xx.md`. The Goal states the claims. The Steps prove them.

**Write or edit it when.** **Open goal**, **Amend goal**, **Close goal**, or a Step’s status changes.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Status | `not started`, `in progress`, `done`, or `abandoned`. A reader knows whether to follow this file or only learn from it |
| Prior | What the previous Goal left true, so this Goal does not re-argue it |
| Approved | The date and that the human approved the text |
| Result we want | Numbered statements. Each one is something a later witness can support. A paragraph of mood is not a result |
| Out of scope | The work someone will otherwise sneak in. Writing it here is what makes “not this Goal” a document instead of a memory |
| Decided | Choices already made, so Steps do not relitigate them. Pointers to ADRs |
| Claims | Checkboxes. Checked only when a witness exists. These are the Goal’s Accept, at Goal scale |
| Planned Steps | Id, intent, status. The intent is one line. The requirements are not pasted here |
| Progress | Steps done, claims done, last Accept |

## Skeleton

```markdown
# G-0xx — <name>

**Status:** `in progress`
**Prior:** <what the previous Goal left true>
**Approved:** <date> (human). <one sentence of direction>

## Result we want

When this Goal is `done`:

1. <claim a witness can support>

## Out of scope (this Goal)

- <exclusion>

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| <topic> | <decision> |

## Product claims (tests by Goal end)

- [ ] <claim>

## Planned Steps

| Step | Intent | Status |
|------|--------|--------|
| F-0xx | <one line> | not started |

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 0 / n |
| Claim boxes | 0 / n |
| Last Accept | — |
```

## Keep out

A session section. The requirement tables of the Steps. A diary of the chat.
