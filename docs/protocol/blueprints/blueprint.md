<!--
  File: docs/protocol/blueprints/blueprint.md
  Purpose: Shape of a blueprint page, so this folder stays a set of forms
  Audience: Agents and humans
  Update when: The blueprint shape changes
-->

# Blueprint page

Every file in `docs/protocol/blueprints/` except `README.md` is a blueprint. This page is the form of those files, so a new kind does not come out as a three-line stub. A reader who has never seen the document kind must be able to write a legal one from the blueprint alone.

**Write or edit it when.** A new document kind is added, or an existing kind’s sections change.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening paragraphs | What the document is, in plain language, and the real path. A reader who did not know the kind existed should recognize the file they have already seen |
| When | The flow event that creates or edits it. Not “update when anything happens” |
| Section table | Each part of the finished document, and why that part exists. If you cannot say why, the part does not belong |
| Skeleton | A copyable markdown block with placeholders. This is the part that makes it a blueprint rather than a description of a blueprint |
| Keep out | The contents that would turn this file into a second copy of another kind |

## Skeleton

```markdown
# <Kind>

<What this document is. The real path. Why it is not the neighboring kind.>

**Write or edit it when.** <the event>

## What each part is for

| Part | Why it is there |
|------|-----------------|
| <section> | <the job of that section> |

## Skeleton

```markdown
# <Title>
```

## Keep out

<The thing that belongs in a different file.>
```

## Keep out of a blueprint

The algorithm that writes the file. Step history. An Active Goal line.
