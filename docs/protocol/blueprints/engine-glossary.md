<!--
  File: docs/protocol/blueprints/engine-glossary.md
  Purpose: Shape of the framework glossary
  Audience: Agents and humans
  Update when: The framework-glossary shape changes
-->

# Framework glossary

This is `docs/architecture/host/glossary.md`. It defines the public words of the framework: the names an implementer must use the same way in every module. One row per word. It is not the protocol dictionary and not the domain glossary. Those three files stay separate so a search for “Step” does not land on a plate.

**Write or edit it when.** **Record source** adds a public framework word, or a word’s meaning changes. Private helpers do not get rows.

## What each column is for

| Column | Why it is there |
|--------|-----------------|
| Word | The public name |
| Meaning | What it guarantees. Include the boundary: what it does not do, when that boundary is the thing people get wrong |
| Where | The spec page or the package door, so the glossary is an index and not a second spec |

## Skeleton

```markdown
| Word | Meaning | Where |
|------|---------|-------|
| <name> | <guarantee> | [<page>](pool.md) |
```

## Keep out

Domain words. Protocol words. A history of renames longer than one clause.
