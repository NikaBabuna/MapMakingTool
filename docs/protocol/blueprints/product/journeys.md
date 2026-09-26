<!--
  File: docs/protocol/blueprints/product/journeys.md
  Purpose: Shape of the user journeys (docs/product/journeys.md), and the operations that edit them
  Audience: Agents
  Update when: The journey shape or an operation changes
-->

# Journeys

**Shapes:** `docs/product/journeys.md`  
**Register:** understanding · **Human-facing:** yes  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a protocol flow (`docs/protocol/flows/`) — edits the repository. A journey is what a person does with the product. The style guide ([style-guide.md](style-guide.md)) — the words on the controls a journey names.

What a person does, in order, and what they see after each action. If you are about to write a bookkeeping step here, you are in the wrong file.

## Skeleton

```markdown
<!--
  File: docs/product/journeys.md
  Purpose: What a person does with the product, and what they see
  Audience: Humans and agents
  Update when: A person-facing sequence changes
-->

# Journeys

What a person does with <product name>, step by step, and what they see at each step. These are not the rules of the domain. Those live in the [wiki](wiki/README.md). Controls are named with the word on the screen, as the [style guide](style-guide.md) sets it out.

## <Journey name>

<Optional: one sentence saying why a person takes this journey.>

1. <The person | They> <action>. <What they see.>
…
…

## Not built

| Journey | What it will be |
|---------|-----------------|
| <name> | <one sentence> |
…

A person cannot do these <n> yet.
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening | yes | Up to three sentences: that these are what a person does and sees, that they are not the rules of the domain (with a link to the wiki), and that controls are named as the style guide names them |
| Journey section | yes, one or more | `## <Journey name>` as a verb phrase ("Export a report"). Numbered steps. Each step is one action and its visible result. Controls are named with the exact word on the screen, per the style guide |
| Purpose sentence | no | One sentence directly under the journey heading, before the steps: why a person takes this journey. It states no step and no control |
| Not built | yes | Last section. One row per journey that is promised and not available. The closing sentence gives the count, in words or digits. If none, the table is replaced by `Every promised journey is built.` |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add journey** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a person-facing sequence |
| **Amend journey** | [../../flows/step.md](../../flows/step.md) SYNC (Ties); [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Move to built** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a not-built journey becomes available |
| **Add not-built row** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |

### Add journey

**Edit.**

1. Insert `## <Journey name>`, its purpose sentence if there is one, and its numbered steps directly above `## Not built`.

### Amend journey

**Edit.**

1. Replace the steps that changed with the new steps. Renumber the list.

### Move to built

**Edit.**

1. Delete the journey's row from **Not built**, and update the count in the closing sentence.  
2. Apply **Add journey** with the steps a person now takes.

### Add not-built row

**Edit.**

1. Add `| <name> | <what it will be> |` at the bottom of **Not built**, and update the count.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every control named in a step appears with the same word in the style guide |
| 2 | The count in the closing sentence equals the number of Not built rows |
| 3 | No journey describes as available something that is not built |

## Keep out

- File paths, requirement tables, the witness command.  
- Domain rules: the wiki.
