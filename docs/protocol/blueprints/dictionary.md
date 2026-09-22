<!--
  File: docs/protocol/blueprints/dictionary.md
  Purpose: Shape of the protocol dictionary
  Audience: Agents and humans
  Update when: The dictionary shape changes
-->

# Protocol dictionary

This is `docs/protocol/environment/dictionary.md`. It fixes the words the other protocol pages use, so “Step” does not mean a chat in one page and a file in another. If a word is doing load-bearing work in a flow and it is not in this table, the flow is using a private language. Add the word here, or stop using it.

**Write or edit it when.** A protocol word is added, renamed, or its meaning changed. Not when a domain word is added. That word goes in `docs/product/glossary.md`. Not when a framework word is added. That word goes in `docs/engine/glossary.md`.

## What each column is for

| Column | Why it is there |
|--------|-----------------|
| Word | The form the other pages use |
| Meaning | One or two sentences a new reader can apply |
| Example | A real file or id in this repository, so the meaning is not abstract |
| Do not confuse it with | The nearby word agents mix up. This column is what stops the page being a list of synonyms |

## Skeleton

```markdown
| Word | Meaning | Example | Do not confuse it with |
|------|---------|---------|------------------------|
| Step | One negotiated job under a Goal | F-063, `docs/paperwork/steps/F-063.md` | A Goal, or a chat |
```

## Keep out

Plates, climate, module names, type names. Those are not protocol words.
