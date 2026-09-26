<!--
  File: docs/protocol/blueprints/architecture/level-page.md
  Purpose: Shape of an architecture level page (a README that is both door and overview), and the operations that edit one
  Audience: Agents
  Update when: The level-page forms or an operation change
-->

# Level page

**Shapes:** the `README.md` of every area folder `docs/architecture/<area>/` and of every chapter folder `docs/architecture/<area>/<chapter>/`  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a folder door ([../doors/folder-door.md](../doors/folder-door.md)) — only lists children. A level page also states the order in which its children run. A mechanism page ([mechanism-page.md](mechanism-page.md)) — one mechanism in full.

The door of one level of the paper. It states what that level does as a whole, in the order the code runs it, and names the page that owns each stage. The procedures live on those pages.

## Skeleton

Form A, an ordered level (the code runs the children in a fixed sequence, e.g. the stages of one request, one step, or one pipeline run):

```markdown
<!--
  File: docs/architecture/<area>/README.md
  Purpose: Level <n> — <what this level does>, in the order the code runs it
  Audience: Agents and humans
  Update when: <the method whose order this page states> changes
-->

# <What one run of this level is, e.g. One request>

<One paragraph: the entry point (type and method in backticks, linked to its source file), and the rule that holds across the whole level.>

<Optional: the run as a composition of its stages, in LaTeX, e.g. $$S_{k+1} = f_n \circ \dots \circ f_1 (S_k)$$ with each $f_i$ named in the stage list.>

<Optional: a table of the state this level owns, | Field | Value | What it holds |.>

1. <Stage, naming the type or method in backticks.> [<Page>](<page>.md).
…

<Optional: at most three sentences of cross-cutting rules.>

<Pointer sentences to the finer and coarser levels.>
```

Form B, a set of peers with no fixed order (e.g. independent services, or a chapter of procedures that the parent level orders):

```markdown
<!--
  File: docs/architecture/<area>/README.md
  Purpose: Level <n> — <what this level holds> | Door to <chapter> procedures
  Audience: Agents and humans
  Update when: A page is added or the question it answers changes
-->

# <Level or chapter name>

<One paragraph: what these pages have in common, and the page that orders them.>

| Page | Question |
|------|----------|
| [<page>.md](<page>.md) | <the question it answers> |
…

<Optional: one sentence of quantities shared by every page.>

<Pointer sentences to the coarser level.>
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | What one run of the level is (Form A), or the level's name (Form B) |
| Opening | yes | Names the entry point in backticks, linked to its source file (Form A), or the common subject (Form B) |
| Composition | no | Form A only. One display formula in LaTeX that writes the run as the composition of its stages. Each function in it is named by a stage of the list. Symbols follow the paper glossary |
| Stage list | Form A | One numbered item per stage, in source order, each ending with a link to its owning page |
| Page table | Form B | One row per page in the folder, `\| [<page>.md](<page>.md) \| <question> \|` |
| Pointer sentences | yes | Link the parent level and, when there is one, the finer level |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add stage** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a stage to the run |
| **Remove stage** | [../../flows/step.md](../../flows/step.md) SYNC (Ties) |
| **Reorder stages** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes the run order; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 9.4 (after approval) |
| **Add child row** | [../../flows/step.md](../../flows/step.md) SYNC (Ties); **Create page** of [mechanism-page.md](mechanism-page.md) |
| **Remove child row** | [../../flows/step.md](../../flows/step.md) SYNC (Ties) |
| **Create** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.1 and Step 3.1, when an area or chapter folder of the paper is created |
| **Rewrite** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when a level page no longer states the level as the source runs it |

### Add stage

**Edit.**

1. Insert `<n>. <Stage, naming the type or method>. [<Page>](<page>.md).` at its position in source order. Renumber the list.  
2. If the page has a Composition, add the stage's function to it at the same position.

### Remove stage

**Edit.**

1. Delete the item and renumber.  
2. If the page has a Composition, remove the stage's function from it.

### Reorder stages

**Before.** You have read the method that runs the stages.

**Edit.**

1. Reorder the items to match the source. Renumber.  
2. If the page has a Composition, reorder its functions the same way.

### Add child row

**Edit.**

1. Form B: add `| [<page>.md](<page>.md) | <question> |` to the table. Form A: apply **Add stage**, or add the link to the stage the page belongs to.

### Remove child row

**Edit.**

1. Delete the row (Form B), or the link (Form A).

### Create

**Before.** The folder exists. You have read the method whose order the page states (Form A), or the pages the folder will hold (Form B).

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md).  
2. Copy Form A or Form B, and fill every part from the source as it now is.  
3. On the parent level page, apply **Add child row**. For an area, apply **Add level** of [abstract.md](abstract.md) instead.

**Result.** The folder has its level page, and the coarser level links it.

### Rewrite

**Before.** You have read the method the page names in its opening.

**Edit.**

1. Replace the opening, the Composition, the state table, and the stage list (Form A) or the page table (Form B) with the level as the source now runs it.  
2. Keep the header's `File` line. Apply **Update purpose** of [../headers/document-header.md](../headers/document-header.md) if the level's job changed.

**Result.** The page states the level as it now is, and links every page in its folder.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every page in the folder is linked from this page |
| 2 | Form A: the stage order matches the order in the source it names |
| 3 | No stage repeats the procedure of its page |
| 4 | Form A: the entry point in the opening links a source file that declares it |
| 5 | If a Composition is present, each of its functions is named by a stage, in the same order |

## Keep out

- A child's procedure: the child page.  
- Step history.
