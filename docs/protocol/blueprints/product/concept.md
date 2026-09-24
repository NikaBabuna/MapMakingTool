<!--
  File: docs/protocol/blueprints/product/concept.md
  Purpose: Shape of the product concept (docs/product/concept.md), and the operations that edit it
  Audience: Agents
  Update when: The concept shape or an operation changes
-->

# Concept

**Shapes:** `docs/product/concept.md`  
**Register:** understanding · **Human-facing:** yes  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a wiki page ([wiki-page.md](wiki-page.md)) — the domain's rules. Scope ([../project/scope.md](../project/scope.md)) — what is in and out, as a binding list. The concept is the promise, in prose.

What the product is for, told to a person before any module, id, or file path. [../../flows/status-report.md](../../flows/status-report.md) reads its title and first paragraph at every startup.

## Skeleton

```markdown
<!--
  File: docs/product/concept.md
  Purpose: What <product name> is for, for a person
  Audience: Humans and agents
  Update when: The product's purpose changes
-->

# <Product name>

<The one-line promise.>

<Two or three paragraphs: the difficulty a person has today, and how this product answers it differently.>

## <Facet>

<What this facet of the promise means for the person. Last sentence: whether a person can do it yet.>
…

## What it is not

<What the product refuses to be, as short sentences.>
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# <Product name>`, as on `docs/project/project.md` |
| Promise | yes | One line, the first paragraph after the title. The repository README quotes it |
| Opening paragraphs | yes | Two or three. No module, type, path, id, or witness |
| Facet section | yes, one or more | `## <Facet>` in the person's words. When the facet is promised but not built, its last sentence says so plainly (e.g. "That is part of the promise, and it is not something a person can do yet.") |
| What it is not | yes | The last section |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Amend facet** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Add facet** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Mark facet built** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step makes a promised facet available |

### Amend facet

**Edit.**

1. Replace the approved old text of the section with the approved new text.

### Add facet

**Edit.**

1. Insert `## <Facet>` and its paragraph directly above `## What it is not`.

### Mark facet built

**Before.** The Step's witness shows a person can now do what the facet promises.

**Edit.**

1. Replace the facet's "not yet" sentence with one sentence saying what a person can now do.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | The first paragraph after the title is one line |
| 2 | It names no module, type, path, Step id, or Goal id |
| 3 | No facet says "not yet" about something a Goal marked `done` has made available |

## Keep out

- Package names, field names, ids: architecture pages and paperwork.  
- The rules of the domain: the wiki.
