<!--
  File: docs/protocol/blueprints/doors/cursor-rule.md
  Purpose: Shape of the always-on editor rule (.cursor/rules/protocol.mdc), and the operations that edit it
  Audience: Agents
  Update when: What the rule must contain changes
-->

# Cursor rule

**Shapes:** `.cursor/rules/protocol.mdc`  
**Register:** legal, brief · **Human-facing:** no  
**Header:** none — the file must begin with the editor's front matter  
**Neighbours:** the agent door ([agent-door.md](agent-door.md)) — the same duty, for every agent.

The editor loads this rule into every session. Its only job is to send the agent to the global prompt.

## Skeleton

```markdown
---
description: Repository protocol — read the global prompt before work
globs:
alwaysApply: true
---

# Protocol

This repository is governed by a protocol. An agent shall learn that protocol before it edits source or documents.

Begin at the global prompt: [docs/protocol/brief.md](docs/protocol/brief.md). Protocol door: [docs/protocol/README.md](docs/protocol/README.md).

**Goal index:** [docs/paperwork/goals.md](docs/paperwork/goals.md) — <goal pointer>. Do not copy the Active Goal line; it lives only on that index.
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Front matter | yes | The five lines exactly as in the Skeleton, starting on line 1 |
| Title | yes | `# Protocol` |
| Orientation sentence | yes | Exactly as in the Skeleton |
| Global prompt line | yes | Links `docs/protocol/brief.md` and `docs/protocol/README.md` |
| Goal index line | yes | Carries the pointer of [goal-pointer.md](goal-pointer.md), followed by the sentence `Do not copy the Active Goal line; it lives only on that index.` |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Set goal pointer** | **Set pointer**, **Clear pointer**, and **Correct pointer** of [goal-pointer.md](goal-pointer.md) |
| **Relink protocol** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3 |

### Set goal pointer

**Edit.** Apply **Set pointer** or **Clear pointer** of [goal-pointer.md](goal-pointer.md) to the Goal index line. Keep the closing sentence.

### Relink protocol

**Edit.**

1. In the global prompt line, replace the moved path, in both the link text and the link target.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Line 1 is `---` and `alwaysApply: true` is present |
| 2 | It has exactly the parts above, in order |

## Keep out

- Protocol law, Step discipline, and read orders: the protocol.  
- The `**Active Goal:**` sentence.
