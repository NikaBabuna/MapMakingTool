<!--
  File: docs/protocol/blueprints/environment.md
  Purpose: Shape of a law page in the environment room
  Audience: Agents and humans
  Update when: The law-page shape changes
-->

# Law page

A law page states one part of the global order so an agent can obey it without reading the other parts. The pages that use this shape are `docs/protocol/environment/territory.md`, `authority.md`, `conduct.md`, `engagement.md`, `phase.md`, and `correctness.md`. The dictionary and the dispatch table are different shapes. They have their own blueprints. Putting a dispatch table on a law page makes two write maps.

**Write or edit it when.** That one part of the order changes. Do not edit a law page because a Step’s status changed.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | What question this page answers, in one short paragraph |
| The rule itself | A table when the rule is a set of cases. Prose when it is one argument. Each row says the case, the consequence, and why, when why is not obvious |
| A worked case | One concrete situation, so the table cannot be read two ways |
| Pointers | To the flow or the other law page that carries the procedure. This page does not become the procedure |

## Skeleton

```markdown
# <Question this page answers>

<One paragraph: what obedience looks like.>

| Case | What you do | Why |
|------|-------------|-----|
| <observable fact> | <action> | <the failure if you do the other thing> |

## Worked case

<A specific conflict and the winning source.>
```

## Keep out

Product vocabulary as if it were law. The witness command copied into every law page (it is named in correctness and in the Step flows). A status banner for the current Goal.
