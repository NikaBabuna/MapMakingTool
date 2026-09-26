<!--
  File: docs/protocol/blueprints/doors/repo-readme.md
  Purpose: Shape of the repository README (README.md at the root), and the operations that edit it
  Audience: Agents
  Update when: The front page shape or its operations change
-->

# Repository README

**Shapes:** `README.md` at the repository root  
**Register:** understanding · **Human-facing:** yes  
**Header:** none — hosting sites render this file's first lines as the project's front page  
**Neighbours:** the docs index ([docs-index.md](docs-index.md)) — the documentation's first page. This is the repository's first page.

The front page for a person who has just found the repository: what the product is, its phase, where the Goals are, and where to read next.

## Skeleton

```markdown
# <Product name>

**<One-line promise, from docs/product/concept.md.>**

<The one line from docs/product/concept.md.>

**Status:** <alpha | beta | prod>  
**Goal index:** [docs/paperwork/goals.md](docs/paperwork/goals.md) — <goal pointer>  
**CI:** <what runs, on which branch, and a link to the workflow file>

---

## Documentation

| | |
|-|-|
| [Docs tree](docs/README.md) | Documentation folders |
| [Goals index](docs/paperwork/goals.md) | <goal pointer> |
| [Protocol](docs/protocol/brief.md) | Conduct — begin at the global prompt |
| [Product concept](docs/product/concept.md) | <what the concept page is for> |
| [<Code index>](<module>/README.md) | Code module index |
| [Architecture](docs/architecture/README.md) | Implementation paper |
| [Navigation](docs/navigation.md) | Full documentation map |
| [Phase](docs/protocol/environment/phase.md) | Current phase and change rules |

## For agents

See [AGENTS.md](AGENTS.md). Begin at [docs/protocol/brief.md](docs/protocol/brief.md). Goal index: [docs/paperwork/goals.md](docs/paperwork/goals.md).
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# <Product name>` as on `docs/product/concept.md` |
| Promise | yes | The concept's one-line promise, in bold |
| One line | yes | The one line from `docs/product/concept.md` (its second paragraph), word for word |
| Status line | yes | The `**Current phase:**` word from `phase.md` |
| Goal index line | yes | Carries the pointer of [goal-pointer.md](goal-pointer.md) |
| CI line | yes | Names the workflow file it describes, linked |
| Documentation table | yes | The eight rows of the Skeleton. The Goals index row's second cell is the pointer text |
| For agents | yes | As in the Skeleton |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Set goal pointer** | **Set pointer**, **Clear pointer**, and **Correct pointer** of [goal-pointer.md](goal-pointer.md) |
| **Set status** | [../../flows/amendment.md](../../flows/amendment.md) Step 8, when `phase.md` changes phase |
| **Relink row** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3 |
| **Sync one line** | [../../flows/amendment.md](../../flows/amendment.md) Step 7, when the concept's one line changes |

### Set goal pointer

**Edit.** Apply **Set pointer** or **Clear pointer** of [goal-pointer.md](goal-pointer.md) to the Goal index line and to the second cell of the Goals index row.

### Set status

**Edit.**

1. Replace the value after `**Status:** ` with the new phase word.

### Relink row

**Edit.**

1. Replace the link target of the row whose page moved.

### Sync one line

**Edit.**

1. Replace the line under the promise with the new one line from `docs/product/concept.md`, word for word.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every link resolves |
| 2 | **Status** equals the phase in `phase.md`. The one line equals the concept's one line |
| 3 | Both Goal pointer places agree with [goal-pointer.md](goal-pointer.md) |

## Keep out

- Setup instructions and module detail: the module doors and the architecture paper.  
- The `**Active Goal:**` sentence.
