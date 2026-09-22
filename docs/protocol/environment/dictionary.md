<!--
  File: docs/protocol/environment/dictionary.md
  Purpose: Protocol words. Product and implementation words do not belong here
  Audience: Agents and humans
  Update when: A protocol word is added or its meaning changes
-->

# Dictionary

| Word | Meaning |
|------|---------|
| Goal | A result that lasts across chats. Stored as one Goal document and a row on the goal index |
| Step | One negotiated job under a Goal. Stored as one Step record and a row on the step registry |
| Shelf | A top folder under `docs/` with one job: protocol, product, architecture, or paperwork |
| Entrance | `docs/README.md` and `docs/navigation.md`. The map. Not a fifth shelf |
| Door | A folder `README.md`. What the folder is, and the pointers out |
| Blueprint | The shape of one document kind. Not the sequence that writes it |
| Flow | A numbered bookkeeping algorithm. Which documents to write or edit, in order |
| Dispatch | The table that chooses a flow from a situation. The only write map |
| Accept | The Step is done: its witness holds, earlier witnesses still hold, and the flow’s records are updated |
| Witness | The executable check named by the implementation paper. A status line is not a witness |
| Torn | A Step marked `in progress`, or a Step record with requirements and no Accept, when the tree does not match an Accept |
| Requirement | A measurable statement stored in the Step record before implementation |
| Door file | `AGENTS.md` or the editor rule. A pointer into this room. Not a copy of it |

Product words and implementation words are defined on their own shelves, not here.
