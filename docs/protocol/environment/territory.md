<!--
  File: docs/protocol/environment/territory.md
  Purpose: Shelves, entrance, and the two work levels
  Audience: Agents and humans
  Update when: A shelf is added or a work level changes
-->

# Territory

The repository is a set of shelves. The folder a file sits in is the job of that file. An agent uses the folder to decide whether it is reading a rule, a description of the product, a description of the machine, or a historical record.

## Shelves

| Place | Job | What a page here is allowed to say | Where the files are today |
|-------|-----|--------------------------------------|---------------------------|
| Entrance | The map. Not a fifth shelf | Which folders exist, and the door into each | [../../README.md](../../README.md) and [../../navigation.md](../../navigation.md) |
| Protocol | How to behave. Product-independent | Rules, flows, and document shapes | `docs/protocol/` |
| Product | What this product is, for a person | Concept, domain words, wiki, visual language. Not module paths and not Step logs | `docs/product/` for concept, glossary, style, wiki. Scope is still `docs/project/project.md`. A later Step gathers these |
| Architecture | What is actually built | Modules, dependencies, and how a system behaves, in plain English | `docs/architecture.md`, `docs/engine/`, `docs/product/architecture.md`. A later Step gathers these |
| Paperwork | Who did what, and when | Goals, Steps, requirements, decisions, changelog, roadmap, backlog | `docs/project/` and `docs/blockers/`. A later Step gathers these |

The agent door is [../../../AGENTS.md](../../../AGENTS.md) at the repository root. The editor door is `.cursor/rules/protocol.mdc`. Both are pointers into this folder. They are not copies of the rules. If a rule is only written on a door, it will drift. The rule lives here.

## Two levels of work

There used to be a third level, the session: a temporary file that restated the active Goal and the Steps this chat would attempt. It was rewritten every chat and never carried a fact the Goal and the Step registry did not already carry. It is retired. Do not create another one.

| Level | What it is | How long it lasts | Where it lives |
|-------|------------|-------------------|----------------|
| **Goal** | The result we want across many chats. It tracks which Steps belong to it and which claims are already true | Until it is done or abandoned | One file `docs/project/goals/G-0xx-*.md`, plus a row on [../../project/goals.md](../../project/goals.md) |
| **Step** | One job: agree the work, store the requirements, do the work, witness it, record it | Permanent, one row forever | A row on [../../project/features.md](../../project/features.md) and, once approved, one file `docs/blockers/F-0xx.md` |

A Goal groups many Steps so the registry stays readable. Steps of one Goal share that Goal’s id.

Only one Step is `in progress` at a time. That mark is the lock between chats. A new chat does not look for a session file. It reads the goal index, opens the active Goal, and reads the step registry. If a Step is `in progress`, or a Step record exists with requirements and no Accept, the work is torn and the judgment flow applies.

## What “active” means

The only Active Goal line in the repository is the line on `docs/project/goals.md` that begins `**Active Goal:**`. Doors may name the Goal’s id and must link to that index. They must not keep a second copy of the sentence. Two copies are how banners went stale while the index said something else.
