<!--
  File: docs/protocol/blueprints/product/concept.md
  Purpose: Shape of the product concept (docs/product/concept.md) — the promise and the binding scope — and the operations that edit it
  Audience: Agents
  Update when: The concept shape, the scope sections, or an operation changes
-->

# Concept

**Shapes:** `docs/product/concept.md`  
**Register:** understanding · **Human-facing:** yes  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a wiki page ([wiki-page.md](wiki-page.md)) — the domain's rules. The journeys ([journeys.md](journeys.md)) — what a person does, step by step. The program page ([../architecture/program.md](../architecture/program.md)) — the stack the product is built with. The concept names no stack.

What the product is for, and what it includes and refuses, told to a person before any module, id, or file path. It is the only scope document: the **In scope** table and the **What it is not** list bind every Goal and Step. Work outside **In scope**, or inside **What it is not**, is not done until this file changes first ([../../flows/conflict-resolve.md](../../flows/conflict-resolve.md) R6). [../../flows/status-report.md](../../flows/status-report.md) reads its title and first paragraph at every startup.

## Skeleton

```markdown
<!--
  File: docs/product/concept.md
  Purpose: What <product name> is for, what it includes, and what it refuses
  Audience: Humans and agents
  Update when: The product's purpose changes, or something enters or leaves its scope
-->

# <Product name>

<The one-line promise.>

<The one line: the product in one sentence a stranger can repeat.>

<Two or three paragraphs: the difficulty a person has today, and how this product answers it differently.>

## <Facet>

<What this facet of the promise means for the person. Last sentence: whether a person can do it yet.>
…

## What a person can do today

<One paragraph: what a person can do with the product as it stands, in the order they meet it. A link to the journeys.>

## In scope

This list is binding. Work on anything that is not in it does not start until the list has been changed to include it.

| Area | What it includes |
|------|------------------|
| **<Area>** | <what the area includes, in a person's words> |
…

## What it is not

<What the product refuses to be, as short sentences.> The refusals below are binding in the same way as the list above.

- <one refusal>
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# <Product name>` |
| Promise | yes | One line, the first paragraph after the title. The repository README quotes it |
| One line | yes | One sentence, the second paragraph after the title: what the product is, so that a stranger can repeat it. The repository README quotes it word for word |
| Opening paragraphs | yes | Two or three. No module, type, path, id, or witness |
| Facet section | yes, one or more | `## <Facet>` in the person's words. When the facet is promised but not built, its last sentence says so plainly (e.g. "That is part of the promise, and it is not something a person can do yet.") |
| What a person can do today | yes | One paragraph, after the last facet. Only what a person can do now. If nothing is built, the paragraph says so |
| In scope | yes | The sentence of the Skeleton, then one row per area. **Area** is bold. **What it includes** names what is promised, built or not, in a person's words. Work outside the table is not allowed |
| What it is not | yes | The last section. Short sentences of what the product refuses to be, the binding sentence of the Skeleton, then one bullet per refusal. No placeholder bullet, and no editorial comment |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Amend facet** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Add facet** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Mark facet built** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step makes a promised facet available |
| **Add in-scope area** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Amend in-scope area** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Add exclusion** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Remove exclusion** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Change one line** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |

### Amend facet

**Edit.**

1. Replace the approved old text of the section with the approved new text.

### Add facet

**Edit.**

1. Insert `## <Facet>` and its paragraph directly above `## What a person can do today`.

### Mark facet built

**Before.** The Step's witness shows a person can now do what the facet promises.

**Edit.**

1. Replace the facet's "not yet" sentence with one sentence saying what a person can now do.  
2. Add that capability to the paragraph under `## What a person can do today`, in the place a person meets it.

### Add in-scope area

**Edit.**

1. Add `| **<Area>** | <the approved sentence> |` at the bottom of the **In scope** table.

### Amend in-scope area

**Edit.**

1. Replace the **What it includes** cell of that area with the approved sentence.

### Add exclusion

**Edit.**

1. Add `- <the approved sentence>` at the bottom of the bullet list under **What it is not**.

### Remove exclusion

**Edit.**

1. Delete that bullet.

### Change one line

**Edit.**

1. Replace the second paragraph after the title with the approved sentence. The repository README then needs **Sync one line** of [../doors/repo-readme.md](../doors/repo-readme.md).

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | The first paragraph after the title is one line, and the second is one sentence |
| 2 | It names no module, type, path, Step id, or Goal id |
| 3 | No facet says "not yet" about something a Goal marked `done` has made available |
| 4 | **In scope** has at least one row, and **What it is not** holds no placeholder bullet or editorial comment |

## Keep out

- Package names, field names, ids: architecture pages and paperwork.  
- The stack (languages, build tools, frameworks): the program page.  
- The rules of the domain: the wiki.
