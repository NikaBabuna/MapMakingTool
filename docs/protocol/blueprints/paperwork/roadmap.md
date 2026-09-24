<!--
  File: docs/protocol/blueprints/paperwork/roadmap.md
  Purpose: Shape of the roadmap (docs/paperwork/roadmap.md), and the operations that edit it
  Audience: Agents
  Update when: The row forms or an operation change
-->

# Roadmap

**Shapes:** `docs/paperwork/roadmap.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the Goal index ([goal-index.md](goal-index.md)) — the status authority. The roadmap repeats status, and if it disagrees, the Goal wins. The backlog ([backlog.md](backlog.md)) — ideas not yet in the order.

The order of Goals: what came before, and what is intended after. It Accepts nothing.

## Skeleton

```markdown
<!--
  File: docs/paperwork/roadmap.md
  Purpose: Direction — Goals, not Accept claims
  Audience: Humans and agents
  Update when: Direction shifts
-->

# Roadmap

Ordered direction. **Accept** lives on Steps under Goals, not here.

| Order | Goal | Intent |
|-------|------|--------|
| <n> | [G-0xx <name>](goals/G-0xx-<slug>.md) | <one-line intent> — **<in progress \| done \| abandoned>** |
…
| <n> | _(later)_ <theme> | After <condition> |
…

Candidates and ideas: [backlog.md](backlog.md)
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening sentence | yes | As in the Skeleton |
| Goal row | yes, one per Goal | `\| <n> \| [G-0xx <name>](goals/G-0xx-<slug>.md) \| <intent> — **<status>** \|`. Status equals the Goal index |
| Later row | no | `\| <n> \| _(later)_ <theme> \| After <condition> \|`, below every Goal row |
| Order | yes | 1, 2, 3, … top to bottom, with no gap |
| Backlog line | yes | As in the Skeleton |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add goal row** | [../../flows/goal.md](../../flows/goal.md) Open goal (W4) |
| **Set row status** | [../../flows/goal.md](../../flows/goal.md) Close goal (W4), Abandon goal (W4) |

### Add goal row

**Edit.**

1. If a later row names this Goal's theme, replace that whole row with  
   `| <its order> | [G-0xx <name>](goals/G-0xx-<slug>.md) | <intent> — **in progress** |`.  
2. Otherwise, insert that row directly below the last Goal row, with order one more than that row's, and add 1 to the order of every row below it.

### Set row status

**Edit.**

1. In the Goal's row, replace `— **in progress**` with `— **done**` or `— **abandoned**`.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | One row per Goal in the Goal index, and each row's status word equals the index |
| 2 | Orders run 1, 2, 3, … with no gap, and later rows are below every Goal row |

## Keep out

- Claim checkboxes and requirement text.  
- A duplicate of the changelog.
