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

The first page of the implementation paper: the levels, which question each one answers, and how every page of the paper is to be read.

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

<Conventions: one paragraph saying how a page states a mechanism — first in plain words, then as a model in LaTeX whose shared symbols are defined in the glossary — how a page cites code — each step names its member, linked to the source file — and that quoted lines are copied from the source unchanged.>

<Pointer sentences: the paper's glossary, its open-questions page, and the ADR that explains the shelf's shape.>
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening | yes | No procedure, no type names |
| Level table | yes | One row per level, from coarsest to finest. **Page** links `program.md` or the level's `README.md` |
| Conventions | yes | One paragraph. States the order in which a page explains a mechanism (plain words, then a model), the notation of the model (LaTeX, with shared symbols in the glossary), the form of a code citation (a member in backticks, linked to its source file), and that excerpts are copied unchanged. No type names |
| Pointer sentences | yes | Link the paper's glossary ([glossary.md](glossary.md)) and open-questions page ([open-questions.md](open-questions.md)), wherever the project keeps them, and the ADR that explains the shelf. These links are how every other page finds those two pages |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add level** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.2, when a new area folder is created |
| **Change question** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Relink level** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A6), when an area folder is renamed or moved |
| **Set conventions** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when the shape of a mechanism page changes how a page states or cites |

### Add level

**Edit.**

1. Insert `| <Level> | <question> | [<area>/](<area>/README.md) |` at its position from coarsest to finest.

### Change question

**Edit.**

1. Replace the Question cell of that level's row.

### Relink level

**Before.** The area folder has its new name on disk.

**Edit.**

1. In that level's row, replace the Level cell with the new name, and the Page cell's link text and target with the new folder and its `README.md`. Keep the question unless the approved change altered it.

**Result.** The row names and links the folder as it now is.

### Set conventions

**Before.** The approved conventions are known, word for word.

**Edit.**

1. Replace the Conventions paragraph with the approved text.

**Result.** The abstract says how every page of the paper is to be read.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every area folder under `docs/architecture/` has a row |
| 2 | No procedure or type name appears |
| 3 | The Conventions paragraph agrees with the Parts of [mechanism-page.md](mechanism-page.md) |

## Keep out

- Procedures and types: level and mechanism pages.
