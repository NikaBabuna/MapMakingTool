<!--
  File: docs/protocol/blueprints/architecture/glossary.md
  Purpose: Shape of the implementation paper's glossary — its words and its shared symbols — and the operations that edit it
  Audience: Agents
  Update when: The glossary shape or an operation changes
-->

# Paper glossary

**Shapes:** the one glossary page of the implementation paper, at the path the paper abstract (`docs/architecture/README.md`) links as its glossary  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the domain glossary ([../product/glossary.md](../product/glossary.md)) — words a person uses for the product's domain. Core definitions (`docs/protocol/environment/core-definition.md`) — protocol words.

The public words of the implementation, and the mathematical symbols that more than one page's model uses. Words: names that must mean the same thing in every module, one row per word. Symbols: one row per shared symbol, with the page that introduces it. It is an index into the mechanism pages, not a second specification.

## Skeleton

```markdown
<!--
  File: docs/architecture/<path>/glossary.md
  Purpose: Implementation words and shared symbols
  Audience: Agents and humans
  Update when: The meaning of a public implementation word changes, or a symbol shared by several models changes
-->

# <Paper or area> glossary

Words and symbols for <what the paper describes>. Domain words live in [the product glossary](<relative path to docs/product/glossary.md>).

## Terms

| Term | Meaning | Do not confuse it with |
|------|---------|------------------------|
| <public name or term> | <what it guarantees, and the boundary people get wrong> | <the neighbouring term, and how it differs> |
…

## Symbols

A symbol used by the model of one page only is defined on that page. The symbols below are shared by several models.

| Symbol | Meaning | Defined on |
|--------|---------|------------|
| $<symbol>$ | <what it denotes, with its set, range, or unit> | [<page>](<relative link>) |
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening sentence | yes | Names what the words and symbols describe, and links the product glossary |
| Terms | yes | `## Terms`, then the table. **Term** is a public name exactly as in source, or a concept a mechanism page names. **Meaning** is its guarantee. **Do not confuse it with** names the neighbour; `—` if there is none |
| Symbols | yes | `## Symbols`, the sentence of the Skeleton, then the table. One row per symbol that appears in the Model sections of two or more pages. **Symbol** is written in LaTeX between `$` signs. **Meaning** states what it denotes and its set, range, or unit. **Defined on** links the page whose Model introduces it |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add term** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a public implementation word |
| **Amend term** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a term's guarantee changes |
| **Remove term** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a public name is removed |
| **Add symbol** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when a symbol comes to be used by the models of two or more pages |
| **Amend symbol** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when a shared symbol's meaning or defining page changes |
| **Remove symbol** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when fewer than two models still use the symbol |

### Add term

**Edit.**

1. Add `| <Term> | <meaning> | <neighbour> |` directly below the term it builds on, or at the bottom of the Terms table.

### Amend term

**Edit.**

1. Replace that row's Meaning or neighbour cell with the current guarantee.

### Remove term

**Edit.**

1. Delete the row.

### Add symbol

**Before.** The Model sections of two or more pages use the symbol with the same meaning.

**Edit.**

1. Add `| $<symbol>$ | <meaning, with its set, range, or unit> | [<page>](<relative link>) |` to the Symbols table, next to the symbols of the same page, or at the bottom.

**Result.** Every page that uses the symbol can rely on one definition.

### Amend symbol

**Edit.**

1. Replace that row's Meaning cell, or its Defined on link, with the symbol's meaning and home as they now are.

**Result.** The row agrees with the Model that introduces the symbol.

### Remove symbol

**Edit.**

1. Delete the row. If one page still uses the symbol, that page's Model defines it.

**Result.** The table holds only shared symbols.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every Term is declared in source (found by the **Declarations** pattern of `docs/architecture/program.md`), or is a concept named on a mechanism page |
| 2 | Private helpers, domain words, and protocol words have no row |
| 3 | Every symbol that appears in the Model sections of two or more pages has a Symbols row, and every **Defined on** link resolves |

## Keep out

- Domain words: the product glossary.  
- Protocol words: core definitions.  
- A history of renames.  
- A symbol used by one page only: that page's Model.
