<!--
  File: docs/protocol/environment/authority.md
  Purpose: What binds, and which source wins when two disagree
  Audience: Agents and humans
  Update when: A conflict rule changes
-->

# Authority

When two sources disagree, the agent does not average them and does not prefer the one it remembers from chat. It uses this table, then edits the loser on purpose if the human wants the other outcome.

| Conflict | What wins | What you do |
|----------|-----------|-------------|
| Chat and a stored document | The document | Follow the document. If the human wants the chat version, they approve an edit, and you change the document. Until that edit lands, the document is the rule |
| Chat and a stored Step record | The Step record | Same rule. Requirements are changed only through **Amend step**, after approval |
| A convenient write sequence and [dispatch.md](dispatch.md) | Dispatch | You do not get to invent a shorter path. If dispatch has no row, you stop and ask |
| A door that states an Active Goal, and the goal index | The goal index | The door is wrong. Point the door at the index. Do not copy the sentence back onto the door |
| A wish to build something the scope document does not list | The scope document | Expand scope first, by the **Scope** flow, then do the work |
| A status line that says `done`, and a red or missing witness | The witness | The status line is a lie. Demote it or roll the Step back. Markdown never Accepts by itself |
| An old Step’s checks and a new Step that would rather delete them | The old checks | A new Step that breaks an old one is rejected. See [correctness.md](correctness.md) |

## Worked case

The human says in chat “skip the requirement about determinism, the test is annoying.” The Step record still lists that requirement, and a check still encodes it. The file wins. The agent does not delete the check. It tells the human the requirement is still stored, and it asks whether they want **Amend step**. Only after they approve does the requirement change, and the check changes with it, on purpose, with the reason written in the Step record.
