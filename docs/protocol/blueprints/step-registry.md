<!--
  File: docs/protocol/blueprints/step-registry.md
  Purpose: Shape of the step registry
  Audience: Agents and humans
  Update when: The step-registry shape changes
-->

# Step registry

This is `docs/project/features.md`. It is the index of every Step, grouped by Goal, so a reader can see status without opening every `F-0xx.md`. The requirements are not here. If they are pasted here, they will diverge from the Step record, and the record is the one that wins.

**Write or edit it when.** A Goal is opened (add a section of `not started` rows), a Step is stored (add the blocker link and mark `in progress`), or a Step is accepted or rolled back (change the status).

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | What a Step is, and where the requirements live. A pointer to the Step flows |
| One section per Goal | The Goal’s name and a link to the Goal file, so the registry is not an unlabeled list of ids |
| Row | Id, name, status, link. Status is `not started`, `in progress`, `done`, or `rolled back`. A `not started` row has no link, because the Step record does not exist yet |
| Marking note | The one-line reminder: `in progress` before implementation, `done` only after Accept |

## Skeleton

```markdown
## G-0xx — <name>

Goal doc: [goals/G-0xx-<slug>.md](goals/G-0xx-<slug>.md)

| ID | Name | Status | Blocker |
|----|------|--------|---------|
| F-0xx | <short name> | not started | — |
| F-0yy | <short name> | in progress | [F-0yy.md](../blockers/F-0yy.md) |
```

## Keep out

Requirement text. Witness logs. A second Active Goal line.
