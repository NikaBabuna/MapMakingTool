<!--
  File: docs/protocol/blueprints/changelog.md
  Purpose: Shape of the changelog
  Audience: Agents and humans
  Update when: The changelog shape changes
-->

# Changelog

This is `docs/project/changelog.md`. It records structure, phase, and scope. It does not record every commit, and it does not explain how a system works. A reader uses it to learn what moved, on which date, and why that move matters. The mechanism stays in the paper. The reason for a controversial choice stays in an ADR. The changelog is the index of those events.

**Write or edit it when.** **Restructure**, **Scope**, **Open goal**, or **Close goal** says to append a line. A behavior Step that did not move a folder and did not change scope does not get a line.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | “Not every commit.” Without that sentence the file becomes a second git log |
| Structure | Dated lines, newest first. One line: date, what changed, why a reader of the tree should care |
| Other headings | Phase or scope, when the file already has them. Do not invent a heading per Step |

## Skeleton

```markdown
- **YYYY-MM-DD** — **F-0xx:** <what moved or what scope changed>. <why it matters>.
```

## Keep out

A description of an algorithm. The Active Goal line. A line whose only content is “updated docs.”
