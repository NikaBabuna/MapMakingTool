<!--
  File: docs/protocol/blueprints/journeys.md
  Purpose: Shape of the user-journey document
  Audience: Agents and humans
  Update when: The journey shape changes
-->

# User journeys

This is `docs/product/flows.md`. The filename says “flows” because that is what the file was named when user journeys were first written. It is not a protocol flow. Protocol flows live in `docs/protocol/flows/` and they edit the repository. This document describes what a person does with the product: open it, look at a map, advance the world, read a number.

If you are about to add a bookkeeping step here, you are in the wrong file.

**Write or edit it when.** A person-facing sequence changes: a new control, a removed mode, a journey that was hypothetical and is now real.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | Who the person is, and that these are journeys rather than algorithms |
| One section per journey | The person’s actions in order, and what they see after each action. Name the control with the word the style guide uses |
| Not yet | A journey that is only intended is labeled as not built, so an agent does not describe it as present |

## Skeleton

```markdown
# Journeys

What a person does. Protocol bookkeeping is in `docs/protocol/flows/`.

## <Journey name>

1. The person <action>.
2. They see <result>.
```

## Keep out

File paths as the substance. Requirement tables. The witness command.
