<!--
  File: docs/protocol/blueprints/concept.md
  Purpose: Shape of the product concept
  Audience: Agents and humans
  Update when: The concept shape changes
-->

# Concept

This is `docs/product/concept.md`. It tells a person what the product is for, before any module, any Step id, or any file path. An agent reads it to avoid solving a different product than the one being built. It is not the wiki (the world’s rules) and it is not the architecture (how the program is built).

**Write or edit it when.** The human changes what the product is for. That is rare. A Step that adds a system does not rewrite the concept unless the product’s purpose changed.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| The promise | What a person gets that they did not get from a picture they painted by hand |
| The parts of that promise | Short sections, each one a facet (the world, the history, the act of guiding it). No procedures |
| What it is not | The exclusions that would otherwise sneak in as features |

## Skeleton

```markdown
# Concept

<Two or three paragraphs a person can read without knowing the repository.>

## <Facet>

<What this facet means for the person, not for the code.>
```

## Keep out

Package names. Step ids. Field names. Those belong in a paper or a Step record.
