<!--
  File: docs/protocol/blueprints/glossary.md
  Purpose: Shape of the domain glossary
  Audience: Agents and humans
  Update when: The domain-glossary shape changes
-->

# Domain glossary

This is `docs/product/glossary.md`. It defines the words a person uses for the world: what a plate is, what elevation means in this product, what a layer in the studio is called. It does not define protocol words. Those are in `docs/protocol/environment/dictionary.md`. It does not define framework type names. Those are in `docs/engine/glossary.md`. Three glossaries exist because mixing them produced pages where “Step” and “plate” were explained in the same breath and neither was clear.

**Write or edit it when.** A domain word is introduced or its meaning changes. One row. Do not append a Step id as the meaning.

## What each column is for

| Column | Why it is there |
|--------|-----------------|
| Word | The word the interface and the wiki use |
| Meaning | What a person should understand, including the unit or the range when that is part of the meaning |
| Not | The neighboring word, when agents confuse them |

## Skeleton

```markdown
| Word | Meaning | Not |
|------|---------|-----|
| <word> | <meaning a person can use> | <the word it is not> |
```

## Keep out

Protocol words. Class names. “See F-0xx” as a substitute for a meaning.
