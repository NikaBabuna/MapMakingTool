<!--
  File: docs/protocol/navigation/README.md
  Purpose: How an agent acquires information before it chooses a flow
  Audience: Agents and humans
  Update when: A navigation page is added
-->

# Navigation

This room does not modify files. It is how an agent decides which flow it needs, using the environment as the standard.

The failure mode is reading thirty files to discover which one mattered, or reading none and guessing. Both are navigation failures. The rule is a door, then one pointer, then stop.

Before choosing a flow, read [../environment/dispatch.md](../environment/dispatch.md) and [../environment/dictionary.md](../environment/dictionary.md). Dispatch tells you what facts count as a situation. The dictionary stops you from treating a product word as a protocol word.

| Page | What it settles |
|------|-----------------|
| [pointers.md](pointers.md) | Where the pointers live, and which pointer answers which question |
| [bounds.md](bounds.md) | What you open for a turn, and what you leave closed even if it is interesting |

## A walk, so the rule is concrete

The human asks what a Step record must contain.

1. Dispatch is not involved yet. This is a reading question, not a write.
2. The protocol door says blueprints are shapes. Open [../blueprints/README.md](../blueprints/README.md).
3. That door’s table names `step.md` as the Step record. Open that one file.
4. Do not also open `goal.md`, `wiki.md`, and the engine specs “for context.” They are siblings and other shelves. They stay closed unless the blueprint itself points at them.
