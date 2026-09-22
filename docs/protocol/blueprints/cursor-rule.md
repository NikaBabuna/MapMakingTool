<!--
  File: docs/protocol/blueprints/cursor-rule.md
  Purpose: Shape of the editor rule that points at the protocol
  Audience: Agents and humans
  Update when: The editor door shape changes
-->

# Editor door

This is `.cursor/rules/protocol.mdc`. The editor loads it on every turn, before the agent has chosen to open `AGENTS.md`. It exists so the pointer is present even when the agent does not follow the written read order. It is the same kind of pointer as `AGENTS.md`, in the place the editor requires. The front matter is the editor’s, not ours. Do not put the protocol in the front matter.

**Write or edit it when.** The protocol door moves, or the Goal id in the file is stale.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Front matter | `description`, `globs`, `alwaysApply: true`, so the editor actually loads the file |
| Protocol pointer | `docs/protocol/README.md` |
| Goal index pointer | `docs/paperwork/goals.md` and the current Goal id |
| The three standing sentences | Same three as the agent door, for the same reason |
| Last completed Goal | A link, not a second Active Goal line |

## Skeleton

```markdown
---
description: Repository protocol — read docs/protocol before work
globs:
alwaysApply: true
---

# Protocol

Read [docs/protocol/README.md](docs/protocol/README.md), then the room the turn needs.

**Goal index:** [docs/paperwork/goals.md](docs/paperwork/goals.md) — G-0xx <name>

Docs win over chat. Store approved requirements before implementation. A torn Step rolls back. The Active Goal line lives only on the goal index.

Last completed Goal: [G-0xx](docs/paperwork/goals/G-0xx-<slug>.md).
```

## Keep out

The rooms pasted in full. A flow. A `**Active Goal:**` line.
