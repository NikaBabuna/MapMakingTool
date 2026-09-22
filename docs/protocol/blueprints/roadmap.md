<!--
  File: docs/protocol/blueprints/roadmap.md
  Purpose: Shape of the roadmap
  Audience: Agents and humans
  Update when: The roadmap shape changes
-->

# Roadmap

This is `docs/project/roadmap.md`. It is the order of Goals, so a reader can see what came before and what is intended after, without opening every Goal file. It does not Accept anything. A row that says `done` is repeating the Goal’s status, not creating it. If the roadmap and the Goal disagree, the Goal wins, and the roadmap is fixed.

**Write or edit it when.** **Open goal** or **Close goal** runs, or the human reorders future work.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | One sentence: this is direction, and Accept lives on Steps |
| Table | Order, Goal link, intent in one line, and whether that Goal is done. Future rows may be unnamed, marked as later, with the condition that would promote them |
| Backlog pointer | Ideas that are not in the order yet |

## Skeleton

```markdown
# Roadmap

Ordered direction. Accept lives on Steps under Goals, not here.

| Order | Goal | Intent |
|-------|------|--------|
| n | [G-0xx <name>](goals/G-0xx-<slug>.md) | <one line> — **done** or **in progress** |
| n+1 | _(later)_ <theme> | After <condition> |

Candidates: [backlog.md](backlog.md)
```

## Keep out

Claim checkboxes. Requirement text. A duplicate of the changelog.
