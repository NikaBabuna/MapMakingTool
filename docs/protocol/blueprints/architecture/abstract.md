<!--
  File: docs/protocol/blueprints/architecture/abstract.md
  Purpose: Shape of the abstract of the implementation paper (docs/architecture/README.md), and the operations that edit it
  Audience: Agents
  Update when: The abstract shape or an operation changes
-->

# Paper abstract

**Shapes:** `docs/architecture/README.md`  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a level page ([level-page.md](level-page.md)) — one level. The abstract names every level and the question each answers, with no procedure.

The first page of the implementation paper: the levels, and which question each one answers.

## Skeleton

```markdown
<!--
  File: docs/architecture/README.md
  Purpose: Abstract of the implementation paper — levels, no procedures
  Audience: Agents and humans
  Update when: A level is added or a level's question changes
-->

# Architecture

This shelf is the implementation paper. <One or two sentences: what the paper states, and that a page names the finer page while the procedure lives on the finer page.>

| Level | Question | Page |
|-------|----------|------|
| <Level> | <the question it answers> | [<page>](<link>) |
…

<Pointer sentences: the paper's glossary, its open-questions page, and the ADR that explains the shelf's shape.>
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening | yes | No procedure, no type names |
| Level table | yes | One row per level, from coarsest to finest. **Page** links `program.md` or the level's `README.md` |
| Pointer sentences | yes | Link the paper's glossary ([glossary.md](glossary.md)) and open-questions page ([open-questions.md](open-questions.md)), wherever the project keeps them, and the ADR that explains the shelf. These links are how every other page finds those two pages |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add level** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.2, when a new area folder is created |
| **Change question** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |

### Add level

**Edit.**

1. Insert `| <Level> | <question> | [<area>/](<area>/README.md) |` at its position from coarsest to finest.

### Change question

**Edit.**

1. Replace the Question cell of that level's row.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every area folder under `docs/architecture/` has a row |
| 2 | No procedure or type name appears |

## Keep out

- Procedures and types: level and mechanism pages.
