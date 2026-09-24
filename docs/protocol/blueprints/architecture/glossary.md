<!--
  File: docs/protocol/blueprints/architecture/glossary.md
  Purpose: Shape of the implementation paper's glossary, and the operations that edit it
  Audience: Agents
  Update when: The glossary shape or an operation changes
-->

# Paper glossary

**Shapes:** the one glossary page of the implementation paper, at the path the paper abstract (`docs/architecture/README.md`) links as its glossary  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the domain glossary ([../product/glossary.md](../product/glossary.md)) — words a person uses for the product's domain. Core definitions (`docs/protocol/environment/core-definition.md`) — protocol words.

The public words of the implementation: names that must mean the same thing in every module. One row per word. It is an index into the mechanism pages, not a second specification.

## Skeleton

```markdown
<!--
  File: docs/architecture/<path>/glossary.md
  Purpose: Implementation words
  Audience: Agents and humans
  Update when: The meaning of a public implementation word changes
-->

# <Paper or area> glossary

Words for <what the paper describes>. Domain words live in [the product glossary](<relative path to docs/product/glossary.md>).

| Term | Meaning | Do not confuse it with |
|------|---------|------------------------|
| <public name or term> | <what it guarantees, and the boundary people get wrong> | <the neighbouring term, and how it differs> |
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening sentence | yes | Names what the words describe, and links the product glossary |
| Row | yes | **Term** is a public name exactly as in source, or a concept a mechanism page names. **Meaning** is its guarantee. **Do not confuse it with** names the neighbour; `—` if there is none |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add term** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a public implementation word |
| **Amend term** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a term's guarantee changes |
| **Remove term** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a public name is removed |

### Add term

**Edit.**

1. Add `| <Term> | <meaning> | <neighbour> |` directly below the term it builds on, or at the bottom.

### Amend term

**Edit.**

1. Replace that row's Meaning or neighbour cell with the current guarantee.

### Remove term

**Edit.**

1. Delete the row.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every Term is declared in source (found by the **Declarations** pattern of `docs/architecture/program.md`), or is a concept named on a mechanism page |
| 2 | Private helpers, domain words, and protocol words have no row |

## Keep out

- Domain words: the product glossary.  
- Protocol words: core definitions.  
- A history of renames.
