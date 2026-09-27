<!--
  File: docs/protocol/blueprints/headers/source-header.md
  Purpose: Shape of the comment at the top of a source file, and the operations that write it
  Audience: Agents
  Update when: The header lines, their placement, or the covered files change
-->

# Source header

**Shapes:** the first comment of every new source file, including test files, in every module named in `docs/architecture/program.md`. Existing files without a header are not retrofitted unless a Step's job says so.  
**Register:** legal · **Human-facing:** no  
**Header:** this is the header  
**Neighbours:** document header ([document-header.md](document-header.md)) — the same four facts, for documentation files.

The header lets a reader who opened a source file from a search know what it is responsible for without reading the body. The behaviour itself is described on the architecture page that owns the mechanism, not here.

## Skeleton

The four lines, written as one block comment in the file's own comment syntax. Shown here with `<c>` standing for the comment marker of that syntax:

```text
<c> File: <path from the repository root>
<c> Purpose: <one sentence: what this file is responsible for>
<c> Audience: <who changes or calls it>
<c> Update when: <the event that should change this file's purpose>
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Placement | yes | The first comment in the file. If the language requires a line before any comment (for example an encoding or interpreter line), the header follows that line directly. Otherwise nothing precedes it |
| Form | yes | The comment form that the File headers section of `docs/architecture/conventions.md` gives for the file's language or kind of file. A kind of file that section marks `none` carries no header |
| `File:` | yes | Path from the repository root, forward slashes |
| `Purpose:` | yes | One sentence of responsibility. It carries no Goal, Step, or decision id and no history: not a list of recent edits, and not the Step that added the file |
| `Audience:` | yes | Who is expected to change it or call it |
| `Update when:` | yes | The event that should change the Purpose |
| Separation | yes | One blank line between the header and the first line of code |
| Consistency | yes | Every header of one language or kind of file uses the same form, the one the conventions page gives |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Write header** | [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 1 |
| **Update purpose** | [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 2 |
| **Update path** | [../../flows/step.md](../../flows/step.md) WORK, **Record source**, step 3 |

### Write header

**Before.** A new source file is being created.

**Edit.**

1. Write the four lines, in the form the conventions page gives for the file's language, at the placement **Parts** gives, followed by one blank line.

**Result.** The first comment in the file is the header.

### Update purpose

**Before.** The file's responsibility changed in this Step.

**Edit.**

1. Replace the text after `Purpose: ` with the new sentence.  
2. If the triggering event changed, replace the text after `Update when: `.

**Result.** The header matches the file's current responsibility.

### Update path

**Before.** The file was moved or renamed.

**Edit.**

1. Replace the text after `File: ` with the new path.

**Result.** The header names the file's actual path.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | A file created after this blueprint existed starts, at its placement, with the four lines in order |
| 2 | The `File:` path is the file's actual path |
| 3 | The header uses the form the conventions page gives for its language, and its `Purpose:` names no Goal, Step, or decision id |

## Keep out

- Change history of the file: version control.  
- The Active Goal sentence and Step ids: paperwork.  
- What the folder does, and how its files are wired together: the folder's code door ([../doors/code-door.md](../doors/code-door.md)). Every folder that is not exempt gets one.
