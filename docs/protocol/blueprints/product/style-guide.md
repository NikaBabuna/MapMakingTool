<!--
  File: docs/protocol/blueprints/product/style-guide.md
  Purpose: Shape of the style guide (docs/product/style-guide.md), and the operations that edit it
  Audience: Agents
  Update when: The style-guide shape or an operation changes
-->

# Style guide

**Shapes:** `docs/product/style-guide.md`  
**Register:** understanding · **Human-facing:** yes  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the architecture page that describes how the screen is drawn — the values that implement a colour. The style guide names the colour, the label, and the key.

How the screen looks and what its controls are called, so that a person can say "that label is wrong" from this page alone.

## Skeleton

```markdown
<!--
  File: docs/product/style-guide.md
  Purpose: How the screen looks and what its controls are called
  Audience: Humans and agents
  Update when: A visible label, color role, or layout rule changes
-->

# Style guide

<Two or three sentences: what the screen is for, and how it should feel.>

## <Area of the screen>

| <Element kind> | <What the person sees or gets> |
|----------------|--------------------------------|
| <element, with its exact on-screen word> | <the current rule> |
…

<Optional: up to three sentences of rules that are not per element.>
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Principles | yes | The paragraph after the title |
| Area section | yes, one or more | `## <Area>`: a region of the screen (e.g. The map, The frame), the keys, or what the screen refuses. One two-column table. The first column holds the exact word or name the person sees |
| Area sentences | no | At most three, below the table |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add element** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a visible element, label, or key |
| **Amend element** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a visible label, colour role, or key changes |
| **Remove element** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a visible element is removed |
| **Add area** | [../../flows/step.md](../../flows/step.md) SYNC (Ties); [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |

### Add element

**Edit.**

1. In the area's table, add `| <exact on-screen word> | <what the person sees> |`.

### Amend element

**Edit.**

1. Replace that row's cells with the current rule. State only the current rule, never "was X, now Y".

### Remove element

**Edit.**

1. Delete the row.

### Add area

**Edit.**

1. Insert `## <Area>` and its table at the position of that region on the screen, reading top to bottom, left to right.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every on-screen word in a table matches the product as built |
| 2 | No row names a function, a file, or a Step id |
| 3 | No row keeps a history of former rules |

## Keep out

- The drawing implementation and colour values in code: the architecture page that describes them.  
- Protocol rules.
