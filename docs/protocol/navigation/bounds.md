<!--
  File: docs/protocol/navigation/bounds.md
  Purpose: What to open for a turn, and what stays closed
  Audience: Agents and humans
  Update when: The read bounds change
-->

# Bounds

Reading is expensive when it is source, and it is wasteful when it is a second copy of a page you already opened. These bounds are the default for every turn. A flow may name one extra page. It does not name “the whole shelf.”

## Open

1. The protocol door, then the environment door if you have not yet seen dispatch and the dictionary in this chat.
2. The goal index’s Active Goal line, the active Goal file, the step registry row for the Step you are judging, and that Step’s record if the row links one.
3. If you are about to write, the one blueprint for that kind.
4. If dispatch named a flow, that one flow file. Not the other flow files.

## Leave closed

| Closed | Unless |
|--------|--------|
| Sibling pages in a folder you entered for a single link | The page you opened tells you the sibling is the next step of the same answer |
| Other shelves | The flow names one file on that shelf |
| Other flows | Dispatch named more than one, in order. Open them one at a time |
| History (changelog, old Goals, old Step records) | You are appending a line, or the human asked what was decided |
| Source | The flow is **Implement** or **Record source**, and the file is in the approved job |
| This room’s other page | You opened the navigation door and the question is specifically pointers or bounds |

The same bounds apply to the protocol folder itself. Opening `docs/protocol/README.md` does not oblige you to read every blueprint. That is the mistake the thin index was built to prevent, and it is still a mistake if the pages are long.
