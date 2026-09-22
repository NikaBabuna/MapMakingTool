<!--
  File: docs/protocol/environment/territory.md
  Purpose: Shelves, entrance, and the two work levels
  Audience: Agents and humans
  Update when: A shelf is added or a work level changes
-->

# Territory

The repository has an entrance and four shelves. A file’s folder is its job.

| Place | Job | Path today |
|-------|-----|------------|
| Entrance | The map of the docs | [../../README.md](../../README.md), [../../navigation.md](../../navigation.md) |
| Protocol | Conduct. Product-independent | `docs/protocol/` |
| Product | What the product is. Concept, domain language, wiki. No implementation | Still the current product and scope docs. Moved in a later Step |
| Architecture | What is built. Plain English, graphs, and theory | Still the current architecture and spec docs. Moved in a later Step |
| Paperwork | History. Who did what, and when | Still the current goal, step, changelog, and decision docs. Moved in a later Step |

The agent door is [../../../AGENTS.md](../../../AGENTS.md) at the repository root. The editor door is `.cursor/rules/protocol.mdc`. Both point here. They do not copy this room.

## Work

Two levels.

| Level | What it is | Where it lives |
|-------|------------|----------------|
| **Goal** | A result that lasts across chats | The goal index and one Goal document |
| **Step** | One job: negotiate, store requirements, change, witness, record | The step registry and one Step record |

One Step is `in progress` at a time. That mark is the lock between chats.

There is no session. Do not create a session document. Do not record chat focus in a third file. A new chat reads the goal index, the active Goal, and the step registry.
