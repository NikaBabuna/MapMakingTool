<!--
  File: docs/protocol/blueprints/product/glossary.md
  Purpose: Shape of the domain glossary (docs/product/glossary.md), and the operations that edit it
  Audience: Agents
  Update when: The glossary shape or an operation changes
-->

# Domain glossary

**Shapes:** `docs/product/glossary.md`  
**Register:** understanding · **Human-facing:** yes  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the paper glossary ([../architecture/glossary.md](../architecture/glossary.md)) — implementation names. Core definitions (`docs/protocol/environment/core-definition.md`) — protocol words. Three glossaries, so that a protocol word and a domain word are never explained in the same breath.

The words a person uses for the product's domain, one row each.

## Skeleton

```markdown
<!--
  File: docs/product/glossary.md
  Purpose: Words a person uses for this product's domain
  Audience: Humans and agents
  Update when: A domain word is introduced or its meaning changes
-->

# Glossary

Words for the domain. The rule each word points at lives on the wiki page that owns it.

| Word | Meaning | Not |
|------|---------|-----|
| <word> | <what a person should understand, with the unit or range when it is part of the meaning> | <the neighbouring word it is confused with, and how it differs> |
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening sentence | yes | One sentence naming what the words are for, and that the rule each word points at lives on the wiki page that owns it |
| Row | yes | **Word** is the word the screen and the wiki use. **Meaning** is for a person, not a class. **Not** names the confusable neighbour; `—` if there is none |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add word** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step introduces a domain word |
| **Amend word** | [../../flows/step.md](../../flows/step.md) SYNC (Ties); [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Remove word** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |

### Add word

**Edit.**

1. Add `| <word> | <meaning> | <not> |` directly below the row of the word it depends on, or at the bottom.

### Amend word

**Edit.**

1. Replace the Meaning or Not cell of that word's row with the approved text.

### Remove word

**Before.** No product page uses the word any more.

**Edit.**

1. Delete the row.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Each word appears once |
| 2 | No row defines a protocol word or a program type |
| 3 | No Meaning cell is "see F-0xx" or a class name |

## Keep out

- Protocol words: core definitions.  
- Program names: the paper glossary and the architecture paper.
