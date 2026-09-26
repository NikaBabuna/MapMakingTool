<!--
  File: docs/protocol/blueprints/doors/folder-door.md
  Purpose: Shape of a folder door (README.md), and the operations that create and edit one
  Audience: Agents
  Update when: The door shape, its variants, or its operations change
-->

# Folder door

**Shapes:** every `README.md` in a landmark folder, in `docs/` and in code, except the four doors with their own blueprint: `docs/README.md` ([docs-index.md](docs-index.md)), every architecture level page ([../architecture/level-page.md](../architecture/level-page.md)), `docs/architecture/README.md` ([../architecture/abstract.md](../architecture/abstract.md)), and the repository `README.md` ([repo-readme.md](repo-readme.md)).  
**Register:** legal, brief · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the doc map ([doc-map.md](doc-map.md)) — lists the whole tree. A door lists only its own folder.

A door stops an agent from opening every file in a folder to learn what they are. It says what the folder is for, and which child answers which question. If the door is an essay, it has failed.

## Skeleton

Variant A, a folder of named pages (the default):

```markdown
<!--
  File: <folder path>/README.md
  Purpose: Door to <what the folder holds>
  Audience: Agents and humans
  Update when: A child of this folder is added or removed
-->

# <Folder name>

<One sentence: what this folder is for.> <Optional: at most two pointer sentences, to a related door or the ADR that explains the folder.>

<Optional orientation paragraph: at most four sentences on how the children relate, or in what order to read them.>

| Page | Read it when |
|------|----------------|
| [<child>.md](<child>.md) | <the question this child answers> |
| [<subfolder>/](<subfolder>/README.md) | <the question this subfolder answers> |
…
```

Variant B, a folder of numbered records (`goals/`, `steps/`, `decisions/`):

```markdown
<!--
  File: <folder path>/README.md
  Purpose: Door to <record kind> records
  Audience: Agents and humans
  Update when: The filename pattern changes
-->

# <Records>

One file per <record>. <The index> is [<index file>](<relative path to index>). Write one from the [<kind> blueprint](<relative path to blueprint>).

<Optional: one sentence saying what one record holds, section by section.>

| Page | Read it when |
|------|----------------|
| [<index file>](<relative path to index>) | <the question the index answers> |
| `<filename pattern>` | <the question one record answers>. The index links each file |
```

Variant C, a code module or code folder: Variant A, plus up to three pointer lines after the first sentence, each `**<Label>:** <link>`, e.g. `**Docs:** [program](../docs/architecture/program.md)`. The table's first column may be `Package` or `Path` instead of `Page`.

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header comment | yes | Purpose begins `Door to` |
| Title | yes | `# <Folder name>` in the reader's words, e.g. `# Paperwork` |
| First sentence | yes | What the folder is for. Not how it came to be |
| Pointer sentences | no | At most two (A, B) or three pointer lines (C) |
| Orientation paragraph | no | Variant A only, and only on the door of a docs shelf or of a folder whose pages are read in order. At most four sentences, after the first sentence and its pointers: how the children relate, or the order to read them in. It names no child that the table does not list |
| Contents sentence | no | Variant B only. One sentence after the pointer sentence: what one record holds, section by section |
| Table | yes | Columns `Page` and `Read it when`. One row per child `.md` file (except this door) and one row per child folder, linking that folder's `README.md`. Variant B has exactly two rows. Rows in reading order |
| Anything else | no | Nothing else appears |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create door** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.1 and Step 6.2; [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 3 |
| **Add child** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.2; [../../flows/step.md](../../flows/step.md) SYNC (Ties); [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 3; **Create blueprint** of [../protocol/blueprint.md](../protocol/blueprint.md) |
| **Remove child** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.2; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 3 |
| **Relink child** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3 (`fix reference`); [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 5 |

### Create door

**Before.** The folder exists and has no `README.md`.

**Edit.**

1. Copy the Skeleton of the variant that fits: B if the folder holds numbered records, C if it is in code, A otherwise.  
2. **Write header** of [../headers/document-header.md](../headers/document-header.md).  
3. Write one table row per child that exists at this moment.

**Result.** The folder has a door. Its parent's door still needs **Add child** for this folder.

### Add child

**Before.** A file or folder was added to this door's folder.

**Edit.**

1. Add one row to the table: `| [<child>.md](<child>.md) | <the question it answers> |`, or for a folder `| [<subfolder>/](<subfolder>/README.md) | <question> |`.  
2. Place it where a reader would look for it: after the row it depends on, otherwise at the bottom.

**Result.** Every child has exactly one row.

### Remove child

**Before.** A child was deleted, or moved out of this folder.

**Edit.**

1. Delete that child's row. Leave every other row as it is.

**Result.** No row links a missing path.

### Relink child

**Before.** A child was renamed, or its target moved.

**Edit.**

1. In that child's row, replace the link text and the link target with the new name and path. Keep the question unless the approved change altered it.

**Result.** The row resolves.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | It has the header, the title, the first sentence, and one table |
| 2 | Every child file and child folder has exactly one row (Variant B: the index and the pattern) |
| 3 | Every link in the table resolves |
| 4 | It contains no `**Active Goal:**` sentence, no flow procedure, and no list of another folder's contents |

## Keep out

- The Active Goal sentence: only `docs/paperwork/goals.md`.  
- A restatement of a flow: `docs/protocol/flows/`.  
- The status table of numbered records: their index.
