<!--
  File: docs/protocol/blueprints/decisions.md
  Purpose: Shape of the decision index
  Audience: Agents and humans
  Update when: The decision-index shape changes
-->

# Decision index

This is `docs/paperwork/decisions.md`. It is the sequence of decisions, not the decisions themselves. Each decision is one file under `decisions/`. The index makes the order visible and makes the next number obvious: one higher than the last row. What the code does today is stated in the paper, which points at the ADR. The ADR does not replace the paper.

**Write or edit it when.** **Decide** runs. You add a row. You do not rewrite an old ADR to match a new opinion. A new ADR amends or supersedes it, and the old file gains an “Amended by” line.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | One file per decision, and how the next number is chosen |
| Table | Id, title, status, link, in number order. The shape of a file is [adr.md](adr.md) |

## Skeleton

```markdown
# Decisions

One file per decision. The next number is one higher than the last row.

| ID | Title | Status | Doc |
|----|-------|--------|-----|
| ADR-0xx | <title> | accepted | [ADR-0xx-<slug>.md](decisions/ADR-0xx-<slug>.md) |
```

## Keep out

The text of the decision. Step requirements. A running commentary on the current Goal. Deleting an old ADR because it feels embarrassing. Supersede it instead.
