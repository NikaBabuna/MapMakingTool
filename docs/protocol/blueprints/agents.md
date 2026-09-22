<!--
  File: docs/protocol/blueprints/agents.md
  Purpose: Shape of the agent door at the repository root
  Audience: Agents and humans
  Update when: The agent door shape changes
-->

# Agent door

This is `AGENTS.md` at the repository root. Tools look for that name. It stays there even though the rules live under `docs/protocol/`. If the rules are pasted into this file, every Step has to edit them twice, and the two copies diverge. That already happened. This file is a pointer.

**Write or edit it when.** The protocol door moves, or the active Goal’s id changes and the file still names the previous one.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Phase pointer | So the agent knows structural freedom without opening the whole phase page first. The link is `docs/protocol/environment/phase.md` |
| Protocol pointer | `docs/protocol/README.md`. One sentence: read that door, then the room the turn needs |
| Goal index pointer | `docs/paperwork/goals.md`, with the current Goal’s id, so a cold start knows which Goal file to open |
| Three standing sentences | Docs win over chat. Requirements are stored before implementation. A torn Step rolls back. These three are repeated because an agent that reads only this file must still hear them. They are not the rest of the protocol |
| Last completed Goal | A link, so the previous result is findable. Not a banner that claims it is current |

## Skeleton

```markdown
# Agents

**Phase:** alpha — [docs/protocol/environment/phase.md](docs/protocol/environment/phase.md)

Conduct: [docs/protocol/README.md](docs/protocol/README.md). Read that door, then the room the turn needs.

**Goal index:** [docs/paperwork/goals.md](docs/paperwork/goals.md) — G-0xx <name>

Docs win over chat. Store approved requirements before implementation. A torn Step rolls back.

Last completed Goal: [G-0xx](docs/paperwork/goals/G-0xx-<slug>.md).
```

## Keep out

A read-order of eleven files. A flow. A `**Active Goal:**` line.
