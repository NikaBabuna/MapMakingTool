<!--
  File: docs/protocol/blueprints/doors/code-door.md
  Purpose: Shape of a code folder's README — the deep dive into that folder's code — and the operations that create and edit one
  Audience: Agents
  Update when: The shape of a code README or one of its operations changes
-->

# Code door

**Shapes:** the `README.md` of every folder outside `docs/` that is not exempt under [../../environment/quality.md](../../environment/quality.md) 3.14: folders of source, tests, build files, scripts, and tool configuration, including the root folder of each module. Not the repository `README.md` ([repo-readme.md](repo-readme.md)).  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the folder door ([folder-door.md](folder-door.md)) — a folder under `docs/`: a short Why and one row per page. A mechanism page ([../architecture/mechanism-page.md](../architecture/mechanism-page.md)) — the concept and the mathematics of what the code does; the code door names the members that do it. The source header ([../headers/source-header.md](../headers/source-header.md)) — the responsibility of one file, not of the folder.

A code door is the introduction to its folder, and the shortcut that saves an agent from opening every file in it. It says what the folder is for, why it is built the way it is, how its parts are wired together and where to start reading, what it depends on and what depends on it, which member performs each step of the mechanisms the paper describes, and what each file and subfolder holds. An agent who has read the door knows which one file to open. A door that only lists the folder's files has failed.

## Skeleton

```markdown
<!--
  File: <folder path>/README.md
  Purpose: Door to <what the folder's code does>
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# <Folder name>

<One sentence: the folder's one job.>

**Paper:** [<page>](<relative link to the architecture page that explains the concept>) · **Conventions:** [conventions.md](<relative link to docs/architecture/conventions.md>)

## Why

<Two to five sentences: why this folder is its own folder, and why it is organised the way it is; what belongs in it; what does not belong in it, and where that goes instead.>

## How it works

<The wiring, in the order it runs or is used: which part is the entry point, which part calls which, and what passes between them. Every type or function is named in backticks.>

**Start reading at:** `<entry point>` in [<file>](<file>).

## Depends on

- [<folder>](<relative link to that folder's README.md>) — <what this folder uses from it>
…

## Used by

- [<folder>](<relative link to that folder's README.md>) — <what it uses from this folder>
…

## Where each step happens

### [<Mechanism>](<relative link to its architecture page>)

| Step | Member | File |
|------|--------|------|
| <n>. <the step, in the words of the page's Procedure, shortened> | `<Type.member>` | [<file>](<file>) |
…

…

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [<file>](<file>) | <its one responsibility, in one sentence> | `<Type>`, `<member>` |
| [<subfolder>/](<subfolder>/README.md) | <its one job, in one sentence> | — |
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header comment | yes | As in the Skeleton. `Purpose` begins `Door to` |
| Title | yes | `# <Folder name>` in the reader's words, e.g. `# Event buffer` |
| First sentence | yes | The folder's one job ([../../environment/quality.md](../../environment/quality.md) 3.15). Not how it came to be |
| Pointer line | yes | `**Paper:**` links each architecture page that explains the concept this folder performs, separated by ` · `, or reads `**Paper:** none — <why the paper does not describe it>` for a folder of build files, scripts, or tool configuration. `**Conventions:**` links `docs/architecture/conventions.md` |
| Why | yes | `## Why`, then two to five sentences: why the folder is its own folder and is organised this way, what belongs in it, and what does not belong in it and where that goes |
| How it works | yes | `## How it works`, then the wiring in running order, in prose or a numbered list, naming every type or function in backticks. It ends with the line ``**Start reading at:** `<entry point>` in [<file>](<file>).`` For a folder of one file, one or two sentences |
| Depends on | yes | `## Depends on`, then one bullet per folder of this repository whose code this folder uses, linking that folder's README, with what is used. `- nothing in this repository` when there is none |
| Used by | yes | `## Used by`, then one bullet per folder of this repository that uses this folder's code, linking that folder's README, with what it uses. `- nothing in this repository — <who runs it>` when there is none |
| Where each step happens | when the `Code:` line of an architecture page links this door | `## Where each step happens`, then one `### [<Mechanism>](<link>)` per such page, in the order of the paper. One row per numbered Procedure step of that page that this folder performs, in the page's order. **Member** is `<Type.member>` in backticks. **File** links the file that declares it |
| Contents | yes | `## Contents`, then one row per file beside this door (except the door) and one row per subfolder. A subfolder links its `README.md`, or is named in backticks with `exempt: <kind>` when quality 3.14 exempts it. **Main types or functions** names the types the file declares or the functions it exports, in backticks, or `—` for a subfolder or a file that declares none |
| Anything else | no | No Goal, Step, or decision id, and no history of changes, appears anywhere in the door |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create door** | [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 4; [../../flows/amendment.md](../../flows/amendment.md) Step 5.1 |
| **Add entry** | [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 5; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 3 |
| **Remove entry** | [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 5; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 3 |
| **Relink entry** | [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 5; [../../flows/amendment.md](../../flows/amendment.md) Step 5.3; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 5 |
| **Rewrite overview** | [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 6; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 3 |
| **Set step map** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a mechanism's Procedure changes or a step moves to another member; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 9 |

### Create door

**Before.** The folder exists, is outside `docs/`, is not exempt under quality 3.14, and has no `README.md`. Its files exist, so their responsibilities can be read from their source headers and declarations.

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md), with `Purpose: Door to <what the folder's code does>`.  
2. Copy the Skeleton. Write the first sentence, the pointer line, Why, How it works with its **Start reading at:** line, Depends on, and Used by, from the source as it now is.  
3. For each architecture page that describes a mechanism this folder performs, write its `###` under Where each step happens, one row per Procedure step performed here. Omit the section when there is none.  
4. Write one Contents row per file and per subfolder that exists at this moment.

**Result.** The folder has a door. Its parent's door still needs **Add entry** for this folder, and `docs/navigation.md` still needs **Add code folder** of [doc-map.md](doc-map.md).

### Add entry

**Before.** A file or subfolder was added to this door's folder.

**Edit.**

1. Add ``| [<file>](<file>) | <responsibility> | `<Type>` |``, or for a subfolder `| [<subfolder>/](<subfolder>/README.md) | <job> | — |`, to Contents, after the row it depends on, otherwise at the bottom.  
2. If the new file changes the wiring, the entry point, or a dependency, also apply **Rewrite overview**.

**Result.** Every child has exactly one Contents row.

### Remove entry

**Before.** A file or subfolder was deleted, or moved out of this folder.

**Edit.**

1. Delete its Contents row, and every Where each step happens row whose File cell links it.  
2. If the removal changes the wiring, the entry point, or a dependency, also apply **Rewrite overview**.

**Result.** No row links a missing path.

### Relink entry

**Before.** A file or subfolder of this folder was renamed, or a folder this door links moved.

**Edit.**

1. Replace the old name and path with the new ones in every row and line that links it: Contents, Where each step happens, Depends on, Used by, the pointer line, and **Start reading at:**.  
2. Replace an old type or member name in backticks with the new one wherever it appears.

**Result.** Every link resolves, and every name in backticks is declared in source.

### Rewrite overview

**Before.** The folder's job, its organisation, its wiring, its entry point, or its dependencies changed. You have read the source as it now is.

**Edit.**

1. Replace the text of each part the change made false — first sentence, pointer line, Why, How it works, **Start reading at:**, Depends on, Used by — with a description of the source as it now is.  
2. Do not add a Step id, a date, or a "previously" clause.

**Result.** The door describes the folder as it now is.

### Set step map

**Before.** A mechanism page whose `Code:` line links this door changed its Procedure, or a step of it is now performed by another member.

**Edit.**

1. Under that page's `###`, replace the rows with one row per numbered Procedure step this folder performs, in the page's order, each naming the member and linking the file that performs it now.  
2. If the page's `Code:` line no longer links this door, delete its `###`. If a new page's `Code:` line links it, add a `###` at the page's place in the paper order.

**Result.** Every step the page states is mapped to the member that performs it.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | It has the header, the title, the first sentence, the pointer line, and the sections Why, How it works, Depends on, Used by, and Contents, in that order, with Where each step happens before Contents when any architecture page's `Code:` line links this door |
| 2 | It is more than a list: Why has two to five sentences, and How it works describes the wiring and ends with a **Start reading at:** line |
| 3 | Every file beside the door (except the door) and every subfolder has exactly one Contents row, and every link in the door resolves |
| 4 | Every type or member it names in backticks is declared in source, found with the **Declarations** line of `docs/architecture/program.md` |
| 5 | For every architecture page whose `Code:` line links this door, Where each step happens has that page's `###`, with one row per Procedure step the folder performs |
| 6 | It contains no Goal, Step, or decision id, and no history of changes |

## Keep out

- The concept and the mathematics of a mechanism: its page under `docs/architecture/`.  
- How names are spelled and where a file goes: `docs/architecture/conventions.md`.  
- One file's responsibility beyond one sentence: that file's source header.  
- The Active Goal sentence: only `docs/paperwork/goals.md`.  
- A history of changes: the changelog.
