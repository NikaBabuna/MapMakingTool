<!--
  File: docs/protocol/blueprints/doors/docs-index.md
  Purpose: Shape of the docs entrance index (docs/README.md), and the operations that edit it
  Audience: Agents
  Update when: The index shape or its operations change
-->

# Docs index

**Shapes:** `docs/README.md`  
**Register:** legal, brief · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the doc map ([doc-map.md](doc-map.md)) — lists every standing page. This index lists only the shelves.

The first page of the documentation, for a reader who has not chosen a shelf yet. It names the shelves and points at the map. If it lists every file, it has become a second map and both will drift.

## Skeleton

```markdown
<!--
  File: docs/README.md
  Purpose: Index of the documentation tree
  Audience: Agents and humans
  Update when: Top-level docs folders change
-->

# Documentation

Permanent prior. **Map:** [navigation.md](navigation.md). **Conduct:** [protocol/README.md](protocol/README.md).

**Goal index:** [paperwork/goals.md](paperwork/goals.md) — <goal pointer>

| Folder | README | Role |
|--------|--------|------|
| [protocol/](protocol/) | [README](protocol/README.md) | Conduct. Begin at [brief.md](protocol/brief.md) |
| [paperwork/](paperwork/) | [README](paperwork/README.md) | Progress: Goals, Steps, decisions, changelog, roadmap, backlog |
| [architecture/](architecture/) | [README](architecture/README.md) | Implementation paper: what is built, by level |
| [product/](product/) | [README](product/README.md) | What the product is, for a person, and what it includes and refuses |

Phase: [protocol/environment/phase.md](protocol/environment/phase.md).
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header comment | yes | As in the Skeleton |
| Title | yes | `# Documentation` |
| Pointer line | yes | `Permanent prior. **Map:** … **Conduct:** …` exactly as in the Skeleton |
| Goal index line | yes | Carries the Goal pointer per [goal-pointer.md](goal-pointer.md) |
| Shelf table | yes | One row per folder directly under `docs/`. Columns `Folder`, `README`, `Role`. **Role** is the shelf's job in at most one sentence, matching Article 2 of `docs/protocol/environment/map.md` |
| Phase line | yes | `Phase: [protocol/environment/phase.md](protocol/environment/phase.md).` |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Set goal pointer** | **Set pointer**, **Clear pointer**, and **Correct pointer** of [goal-pointer.md](goal-pointer.md) |
| **Add shelf** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.3 |
| **Remove shelf** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.3 |
| **Change role** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.3, when a shelf's job changes |

### Set goal pointer

**Edit.** Apply **Set pointer** or **Clear pointer** of [goal-pointer.md](goal-pointer.md) to the Goal index line.

### Add shelf

**Before.** A folder was created directly under `docs/`, with its door.

**Edit.**

1. Add a row: `| [<folder>/](<folder>/) | [README](<folder>/README.md) | <role> |`, at the position a reader would look for it.

**Result.** Every top-level docs folder has one row.

### Remove shelf

**Before.** A folder directly under `docs/` was removed.

**Edit.**

1. Delete its row.

### Change role

**Before.** Article 2 of `docs/protocol/environment/map.md` now gives the shelf a different job.

**Edit.**

1. Replace the **Role** cell of that shelf's row with the new sentence.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every folder directly under `docs/` has exactly one row, and no row names a missing folder |
| 2 | The Goal index line matches [goal-pointer.md](goal-pointer.md) |
| 3 | It lists no individual page other than `navigation.md`, `protocol/README.md`, `protocol/brief.md`, and `phase.md` |

## Keep out

- A list of every page: [doc-map.md](doc-map.md).  
- Implementation detail, blueprint lists, and the witness command.  
- The `**Active Goal:**` sentence.
