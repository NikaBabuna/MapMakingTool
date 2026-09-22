<!--
  File: docs/protocol/README.md
  Purpose: Door to conduct — which room to open, and why the rooms exist
  Audience: Agents and humans
  Update when: A protocol room is added or the entry order changes
-->

# Protocol

This folder is the conduct of the repository. It does not describe the product, and it does not describe how the simulation works. It tells an agent where it is, how to find a document, what shape a document must have, and the exact sequence of edits when something changes.

An agent may not invent its own sequence. Situations and the flow that handles each one are listed in [environment/dispatch.md](environment/dispatch.md). If a situation is not in that table, the agent stops and asks.

**Goal index:** [../paperwork/goals.md](../paperwork/goals.md)

## The four rooms

Read them in this order the first time. After that, open only the room the turn needs.

| Order | Room | What you get from it |
|-------|------|----------------------|
| 1 | [environment/](environment/README.md) | The territory, the laws, the dictionary, and which flow runs |
| 2 | [navigation/](navigation/README.md) | How to find the one document you need, and what you leave closed |
| 3 | [flows/](flows/README.md) | The bookkeeping: which files change, in which order, when a situation is real |
| 4 | [blueprints/](blueprints/README.md) | The shape of the file you are about to write. Open the one blueprint the flow named |

## How a turn actually goes

1. Read this page, then [environment/dispatch.md](environment/dispatch.md), so you know which flow is legal.
2. Use [navigation/pointers.md](navigation/pointers.md) to open the goal index, the active Goal, and the step registry. That is enough to know whether a Step is already in progress.
3. If dispatch says a flow, follow that flow. When the flow says “write a Goal” or “write a Step record,” open that blueprint and copy its skeleton.
4. When you speak to the human, follow [environment/engagement.md](environment/engagement.md).

## What this folder will not tell you

The language, the modules, and the meaning of a plate or a climate model live on the other shelves. Progress records live in `docs/paperwork/`. Scope is still `docs/project/project.md`. The implementation paper is `docs/architecture/`. The blueprints name the paths, so you can find the files today.

Session files are not part of this protocol. A chat does not get its own document. The active Goal and the Step marked `in progress` are the whole of “what we are doing now.”
