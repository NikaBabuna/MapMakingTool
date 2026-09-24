<!--
  File: docs/protocol/blueprints/headers/document-header.md
  Purpose: Shape of the comment at the top of every documentation file, and the operations that write it
  Audience: Agents
  Update when: The header lines, their order, or the exempt files change
-->

# Document header

**Shapes:** the first lines of every `.md` file under `docs/`, and of every folder door `README.md` in the repository. Exempt: `README.md` at the repository root, `AGENTS.md`, and `.cursor/rules/*.mdc` (their first lines are read by tools and people before any comment).  
**Register:** legal · **Human-facing:** no  
**Header:** this is the header  
**Neighbours:** source header ([source-header.md](source-header.md)) — the same four facts, in a program's comment syntax.

The header tells a reader who arrived from a search whether they opened the right file, before they read the body. It is an HTML comment, so it does not render.

## Skeleton

```markdown
<!--
  File: <path from the repository root>
  Purpose: <one sentence: what this file is for>
  Audience: <Agents | Agents and humans | Humans and agents>
  Update when: <the event that should cause this file to change>
-->
```

The line after `-->` is the file's title (`# …`). There is no blank line before `<!--`.

## Parts

| Part | Required | Rule |
|------|----------|------|
| `<!--` | yes | Line 1 of the file |
| `File:` | yes | Two spaces, `File: `, then the path from the repository root with forward slashes, e.g. `docs/paperwork/steps/F-0xx.md` |
| `Purpose:` | yes | One sentence. What the file is responsible for. Not a history of edits |
| `Audience:` | yes | `Agents` for protocol pages. `Agents and humans` for doors, paperwork, and architecture. `Humans and agents` for product pages |
| `Update when:` | yes | The event, e.g. `A child of this folder is added or removed`. Not "when needed" |
| `-->` | yes | Closes the comment. The title follows on the next line |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Write header** | Every **Create** operation of every other blueprint, as its first edit |
| **Update path** | [../../flows/amendment.md](../../flows/amendment.md) Step 5, when a file is moved or renamed |
| **Update purpose** | [../../flows/amendment.md](../../flows/amendment.md) Step 5, or [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a file's job changes |

### Write header

**Before.** The file is being created.

**Edit.**

1. Write the Skeleton as lines 1–6, with the file's own path, purpose, audience, and update event.

**Result.** Line 7 is the title.

### Update path

**Before.** The file has moved to a new path.

**Edit.**

1. Replace the text after `File: ` on line 2 with the new path.

**Result.** Line 2 names the path at which the file now sits.

### Update purpose

**Before.** The approved change alters what the file is responsible for.

**Edit.**

1. Replace the text after `Purpose: ` with the new sentence.  
2. If the event that should cause changes also changed, replace the text after `Update when: `.

**Result.** The header describes the file's current job.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Line 1 is `<!--`, and lines 2–5 begin `  File: `, `  Purpose: `, `  Audience: `, `  Update when: `, in that order |
| 2 | The `File:` path is the file's actual path |
| 3 | Line 6 is `-->` and line 7 begins `# ` |

## Keep out

- Status, Step ids, Goal ids, and dates: paperwork.  
- The Active Goal sentence: only `docs/paperwork/goals.md`.
