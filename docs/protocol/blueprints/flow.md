<!--
  File: docs/protocol/blueprints/flow.md
  Purpose: Shape of a flow page
  Audience: Agents and humans
  Update when: The flow-page shape changes
-->

# Flow page

These are `docs/protocol/flows/goals.md`, `steps.md`, `structure.md`, `source.md`, and `judgment.md`. A flow page holds one or more named algorithms. An algorithm is something an agent can follow without inventing a step. If a sentence can be read two ways, it is not finished.

**Write or edit it when.** Dispatch gains or loses a row that points here, or the files an algorithm touches change.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | What family of situations this file owns |
| Algorithm heading | The name dispatch uses, in bold, spelled the same way |
| When | The observation that makes the algorithm legal |
| Before | What must already be true, so the algorithm does not write a lie |
| Steps | Numbered. Each step names a file path or a document kind that has a blueprint. “Update the docs” is not a step |
| Done | What a later chat can see |
| Not done | The half-finished state that has been mistaken for success |

The Step file also carries the lifecycle table, because the algorithms are pieces of that lifecycle and the reader needs the whole order in one place.

## Skeleton

```markdown
## <Name>

**When.** <observation>

**Before.** <precondition>

**Steps.**

1. Edit `<path>`: <what changes>.

**Done.** <observable result>

**Not done.** <the false finish>
```

## Keep out

A second dispatch table. The witness command repeated in every algorithm (name it in the flows door and in the Step lifecycle). Product behavior.
