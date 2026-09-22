<!--
  File: docs/protocol/blueprints/style-guide.md
  Purpose: Shape of the visual style guide
  Audience: Agents and humans
  Update when: The style-guide shape changes
-->

# Style guide

This is `docs/product/style-guide.md`. It is how the product looks and how its controls are named, so a screen can be judged without reading the paint code. A person should be able to say “that label is wrong” from this page alone. The numbers that implement a color live in the implementation paper and in the source. This page names the color and the label.

**Write or edit it when.** A visible label, a color role, or a layout rule changes. Not when an internal function is renamed and nothing on screen moves.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Principles | A few sentences: what the screen is for (a working map, not a poster) |
| Color and type | Named roles, with the values a designer needs. Say what each role is used for |
| Controls | The words on buttons and menus, exactly as shown |
| What changed the look | Only if a rule replaced an older rule. Do not keep a stack of “F-0xx also changed the swatch” lines. State the current rule |

## Skeleton

```markdown
# Style guide

<What the screen is supposed to feel like, in a few sentences.>

## <Area of the screen>

| Element | Rule |
|---------|------|
| <label or color role> | <what the person sees> |
```

## Keep out

The paint implementation. Step-by-step history of every swatch. Protocol rules.
