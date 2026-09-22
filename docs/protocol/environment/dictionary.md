<!--
  File: docs/protocol/environment/dictionary.md
  Purpose: Protocol words, each with a meaning, an example, and the word it is not
  Audience: Agents and humans
  Update when: A protocol word is added or its meaning changes
-->

# Dictionary

These are the words the protocol uses. Product words (what a world contains) and framework words (what the engine types are called) do not belong in this table. If a word is not here, do not invent a protocol meaning for it. Ask, or look on the shelf that owns that kind of word.

| Word | Meaning | Example | Do not confuse it with |
|------|---------|---------|------------------------|
| Goal | A result that lasts across chats. It owns a list of Steps and a list of claims | G-011, the file `docs/paperwork/goals/G-011-docs-restructuring.md`, and its row on the goal index | A Step. A Goal is not one coding job |
| Step | One negotiated job under a Goal: agree it, store requirements, do it, witness it, record it | F-062, the row on the step registry, and `docs/paperwork/steps/F-062.md` | A Goal, or a chat. A Step is the unit of Accept |
| Shelf | A top folder under `docs/` with one job | `docs/protocol/` is the conduct shelf | A room inside the protocol shelf. Environment is a room, not a shelf |
| Entrance | The two files that map the docs and are not themselves a shelf | `docs/README.md` and `docs/navigation.md` | A door. The entrance points at doors |
| Door | The `README.md` of a folder. It says what the folder is and links its children | `docs/protocol/flows/README.md` | The entrance. A door does not list the whole repository |
| Blueprint | The shape of one kind of document: sections, what each section is for, and a skeleton | `docs/protocol/blueprints/step.md` tells you how to write `docs/paperwork/steps/F-063.md` | A flow. A blueprint does not say when to write the file |
| Flow | A numbered sequence of edits. When a situation is true, these files change, in this order | **Store step** in `docs/protocol/flows/steps.md` | A blueprint, and also `docs/product/journeys.md`, which is what a person does, not a protocol flow |
| Dispatch | The table that picks a flow from a situation. The only legal write map | `docs/protocol/environment/dispatch.md` | A flow. Dispatch does not itself edit files |
| Requirement | A measurable statement, stored in the Step record before implementation, that a later check can fail | “`docs/project/session.md` is gone” | A wish such as “make the docs cleaner” |
| Accept | The Step is done: the condition in [correctness.md](correctness.md) holds | F-062 marked `done` after the suite was green and the docs matched | A status word typed before the suite ran |
| Witness | The executable check that covers this Step and every earlier Accepted Step. In this repository that command is `./mvnw test` or `mvnw.cmd test` | The suite run at the end of a Step | A paragraph, a grep of a document, or “it compiles” |
| Torn | A Step marked `in progress`, or a Step record with requirements and no Accept, while the tree does not match an Accept | A chat dies after MARK and before the suite is green | A Step that is merely not started |
| Door file | A pointer at the repository root or in the editor config. It sends the agent here | `AGENTS.md`, `.cursor/rules/protocol.mdc` | A copy of the protocol. If the rule’s text is only in the door file, it will drift |

Product words live in `docs/product/glossary.md`. Framework words live in `docs/engine/glossary.md`.
