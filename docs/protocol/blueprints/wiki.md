<!--
  File: docs/protocol/blueprints/wiki.md
  Purpose: Shape of a domain page
  Audience: Agents and humans
  Update when: The wiki-page shape changes
-->

# Domain page

These are the pages in `docs/product/wiki/` other than that folder’s door. Today: `world.md`, `elevation.md`, `tectonics.md`. A domain page says what is true of the world, in language a person can argue with. It is the place domain rules live so they are not only in source and not only in chat.

The folder door indexes them. A new page is added to that door in the same edit.

**Write or edit it when.** A world rule changes or a new part of the world is defined. The edit describes the current rule. It does not add a banner that says which Step last touched the page and leave the old rule underneath.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | What part of the world this page owns, and what it refuses (so elevation rules do not leak into the tectonics page, or the reverse) |
| Rules | Tables and short sections. Quantities have units. A rule that replaced an older rule states the current rule. The older rule is named once, as retired, not left as a second procedure |
| Relations | Links to the other wiki pages a reader needs. Not links to every Step record |

## Skeleton

```markdown
# <Part of the world>

<What this page owns.>

## <Rule>

| Item | What is true |
|------|----------------|
| <name> | <the current rule, with units> |
```

## Keep out

A Step log at the top (“F-061 done, F-060 live, F-059 live”). That log is paperwork. A domain page that starts with it cannot be read as a description of the world. Also keep out module paths, except a single pointer when the reader must find the paper.
