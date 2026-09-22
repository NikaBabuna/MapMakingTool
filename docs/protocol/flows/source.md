<!--
  File: docs/protocol/flows/source.md
  Purpose: Bookkeeping when source files change
  Audience: Agents and humans
  Update when: The source flow changes
-->

# Source flows

Source is the expensive read. The papers exist so the next agent can open one page and then one file, instead of every file in the module. This algorithm is how those papers stay attached to the code. It runs inside **Implement** whenever source changes. It is not a substitute for the checks.

## Record source

**When.** A source file was added or its behavior changed.

**Before.** The Step is `in progress`. The file is inside the approved job.

**Steps.**

1. A new source file starts with the header in the source blueprint: File, Purpose, Audience, Update when. The header is how a later reader knows whether they opened the right file without reading the whole body.
2. If the new file created a landmark folder, that folder gets a door from the readme blueprint in the same Step. A type does not appear in a folder that has no door.
3. Update the page in `docs/architecture/` that owns the mechanism. A parent page keeps the pointer. The procedure stays on the child. Do not append a status banner as a substitute for the description.
4. If the change is host behavior, edit the one host page that owns that topic (`pool`, `events`, `systems`, `merge`, `determinism`, `user`, `diagnostics`). Do not copy the change into every host page.
5. If a new public framework word appeared, add one row to `docs/architecture/host/glossary.md`. If a new domain word appeared, add it to `docs/product/glossary.md` instead. Do not put either word in the protocol dictionary.
6. Leave the Step record’s test map to **Implement**. This algorithm does not invent checks, and it does not delete them.

**Done.** A reader can open the module paper, see the new behavior, and follow it to the file.

**Not done.** The code has the behavior and the paper still describes the previous one, with a status line that says the Goal is done.
