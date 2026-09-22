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
3. Update the implementation paper for that module (`docs/engine/architecture.md` or `docs/product/architecture.md`, and the roll-up `docs/architecture.md` if the roll-up lists the fact). Describe the current behavior. Do not append a “through F-0xx” banner as a substitute for the description.
4. If the change is framework behavior, update the one spec page in `docs/engine/specs/` that owns that topic. Do not copy the change into every spec.
5. If a new public framework word appeared, add one row to `docs/engine/glossary.md`. If a new domain word appeared, add it to `docs/product/glossary.md` instead. Do not put either word in the protocol dictionary.
6. Leave the Step record’s test map to **Implement**. This algorithm does not invent checks, and it does not delete them.

**Done.** A reader can open the module paper, see the new behavior, and follow it to the file.

**Not done.** The code has the behavior and the paper still describes the previous one, with a status line that says the Goal is done.
