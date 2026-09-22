<!--
  File: docs/protocol/blueprints/navigation.md
  Purpose: Shape of the documentation map
  Audience: Agents and humans
  Update when: The map shape changes
-->

# Doc map

This is `docs/navigation.md`. It is the map of the documentation that exists, not a design for documentation you wish existed. An agent who knows the name of a document and not the path starts here, finds the row, and opens that one file.

**Write or edit it when.** A document is added, moved, removed, or its status sentence became false (“through F-00x”, “after G-00x”, “deferred until”).

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Header | Pointers to the protocol door, the goal index, and the phase page. The active Goal is named by id. There is no `**Active Goal:**` line here. That line lives only on the goal index |
| One section per folder | Opens with that folder’s door, then a table |
| Status cell | `Active`, `Draft`, or `Historical`, plus a short fact if the reader would otherwise open a stale page. The fact is not a second changelog |
| Code section | Landmark code folders and their doors, so an agent can enter a module without listing every source file |

## Skeleton

```markdown
# Navigation

**Phase:** alpha ([protocol/environment/phase.md](protocol/environment/phase.md))
**Goal index:** [paperwork/goals.md](paperwork/goals.md) — G-0xx <name>
**Protocol:** [protocol/README.md](protocol/README.md)

## <Folder>

**Folder:** [<door>](<door>)

| Doc | Status |
|-----|--------|
| [page.md](page.md) | Active — <one fact, or just Active> |
```

## Keep out

The text of a mechanism. A blueprint’s skeleton. A copied Active Goal sentence.
