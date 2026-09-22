<!--
  File: docs/protocol/blueprints/nav-page.md
  Purpose: Shape of a page about how to look, not how to write
  Audience: Agents and humans
  Update when: The navigation-page shape changes
-->

# Navigation page

These are `docs/protocol/navigation/pointers.md` and `bounds.md`. They tell an agent which document to open and which to leave closed. They never tell it to edit. If a navigation page grows a “then update the file” step, that step has escaped into the wrong room. Move it to a flow.

**Write or edit it when.** A pointer home moves, or the set of documents a turn is allowed to open changes.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| `pointers.md` | A table: the question, the single file that answers it, and how you know you are finished. The last column is what stops a reader continuing into siblings |
| `bounds.md` | Two lists, Open and Leave closed. The closed list has an Unless column, so the bound is a rule and not a wall the agent has to break in secret |
| A walk | One concrete question followed through the doors, so the rule is visible as behavior |

## Skeleton

```markdown
| Question | Open this | You are done when |
|----------|-----------|-------------------|
| Which Goal is active? | The Active Goal line on the goal index | You have the link |
```

## Keep out

Write steps. A second copy of dispatch.
