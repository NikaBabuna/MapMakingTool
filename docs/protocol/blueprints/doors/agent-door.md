<!--
  File: docs/protocol/blueprints/doors/agent-door.md
  Purpose: Shape of the repository agent door (AGENTS.md), and the operations that edit it
  Audience: Agents
  Update when: What the agent door must contain changes
-->

# Agent door

**Shapes:** `AGENTS.md` at the repository root  
**Register:** legal, brief · **Human-facing:** no  
**Header:** none — agent tools read this file's first lines directly  
**Neighbours:** the Cursor rule ([cursor-rule.md](cursor-rule.md)) — the same duty, for one editor.

Its only job is to send an agent into the protocol before it edits anything. It does not copy protocol law.

## Skeleton

```markdown
# Agents

This repository is governed by a protocol. An agent shall learn that protocol before it edits source or documents.

Begin at the global prompt: [docs/protocol/brief.md](docs/protocol/brief.md). Protocol door: [docs/protocol/README.md](docs/protocol/README.md).

**Goal index:** [docs/paperwork/goals.md](docs/paperwork/goals.md) — <goal pointer>
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# Agents`, line 1 |
| Orientation sentence | yes | Exactly as in the Skeleton |
| Global prompt line | yes | Links `docs/protocol/brief.md` and `docs/protocol/README.md` |
| Goal index line | yes | Carries the pointer of [goal-pointer.md](goal-pointer.md) |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Set goal pointer** | **Set pointer**, **Clear pointer**, and **Correct pointer** of [goal-pointer.md](goal-pointer.md) |
| **Relink protocol** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3, when the global prompt or protocol door moves |

### Set goal pointer

**Edit.** Apply **Set pointer** or **Clear pointer** of [goal-pointer.md](goal-pointer.md) to the Goal index line.

### Relink protocol

**Edit.**

1. In the global prompt line, replace the moved path, in both the link text and the link target.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | It has exactly the four parts, in order, and nothing else |
| 2 | Both links on the global prompt line resolve |

## Keep out

- Phase text, flow catalogues, product words: the protocol and product shelves.  
- The `**Active Goal:**` sentence.
