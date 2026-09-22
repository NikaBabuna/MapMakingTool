<!--
  File: docs/protocol/blueprints/docs-index.md
  Purpose: Shape of the docs entrance index
  Audience: Agents and humans
  Update when: The docs index shape changes
-->

# Docs index

This is `docs/README.md`. It is the first documentation page, for a human or an agent who has not chosen a shelf yet. It names the shelves and points at the map. It is not the map. If it lists every file, it has become a second `navigation.md` and both will drift.

**Write or edit it when.** A top-level docs folder appears, disappears, or changes job.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| One sentence | What the docs tree is |
| Pointer to the map | `docs/navigation.md`, so the reader knows where the full list is |
| Shelf table | Folder, door, job. One row per shelf, plus folders that have not moved to their shelf yet, with that fact stated |
| Goal index line | A link. Not a copy of the Active Goal sentence |

## Skeleton

```markdown
# Documentation

<One sentence.>

**Map:** [navigation.md](navigation.md).
**Goal index:** [paperwork/goals.md](paperwork/goals.md) — <Goal id and name>

| Folder | Door | Job |
|--------|------|-----|
| [protocol/](protocol/) | [README](protocol/README.md) | Conduct |
```

## Keep out

Implementation detail. A list of every blueprint. The witness command.
