<!--
  File: docs/protocol/blueprints/messages/status-report.md
  Purpose: Shape of the human-facing startup status report
  Audience: Agents
  Update when: The report sections or their allowed values change
-->

# Status report

**Shapes:** one chat message, delivered by [../../flows/status-report.md](../../flows/status-report.md). Not a file.  
**Register:** understanding ([../../environment/style.md](../../environment/style.md)) · **Human-facing:** yes  
**Header:** none — a chat message  
**Neighbours:** the Rollback notice (in [../../flows/rollback.md](../../flows/rollback.md)) — sent instead of this report when the tree is still torn.

The first thing the human reads in a chat: what the project is, where it stands, and what the agent needs.

## Skeleton

```markdown
## Status

**Product:** <one or two short sentences>

**Where we are:** <active Goal and progress, or no active Goal>

**Open work:** <none in progress | rolled back <id>: <one clause> | <n> unsealed changes kept by your decision>

**Health:** <reconcile clean | rollback ran, then reconcile clean | unsealed changes kept; the tree is not at the safe point | no safe point exists>

**What you can do next:**
- <option> (`certain`|`probable`|`uncertain`)
- …

**Need from you:** <decision | fact | approval | waiting for your instruction>
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Product | yes | From the concept's title and promise. No jargon |
| Where we are | yes | Goal name, status, type when known, and progress in plain words |
| Open work | yes | Exactly one of the three forms in the Skeleton |
| Health | yes | Exactly one of the four forms in the Skeleton |
| What you can do next | yes | At most three options, each with a confidence label. When unsealed changes were kept, the first option is a Step that adopts them |
| Need from you | yes | Exactly one ask, or the explicit wait |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Deliver** | [../../flows/status-report.md](../../flows/status-report.md) Write |

### Deliver

**Edit.**

1. Fill each section from the handoff and the gathered sources, in Skeleton order, and send as one message.

## Check

| # | The message is legal only if |
|---|------------------------------|
| 1 | Every section is present, in order, with one of its allowed forms |
| 2 | Every option carries a confidence label |
| 3 | It contains no file dump, no handoff field name, and no implementation |

## Keep out

- File dumps, handoff field names, implementation, protocol essays.  
- A second Active Goal line.
