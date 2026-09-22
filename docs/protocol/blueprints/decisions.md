<!--
  File: docs/protocol/blueprints/decisions.md
  Purpose: Shape of the decision log
  Audience: Agents and humans
  Update when: The decision-log shape changes
-->

# Decision log

This is `docs/project/decisions.md`. It is one file of ADR sections, not a folder of ADR files. One file is the point: a reader can see the sequence of choices, and the next number is obvious. The log’s introduction says what an ADR is. The sections are the decisions. What the code does today is stated in the paper, which points at the ADR. The ADR does not replace the paper.

**Write or edit it when.** **Decide** runs. You append. You do not rewrite an old ADR to match a new opinion. A new ADR amends or supersedes it, and the old section gains an “Amended by” line.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Title and a short introduction | What an ADR is, and that new ones are appended |
| Sections | `## ADR-0xx — <title>`, in number order. The shape of a section is [adr.md](adr.md) |

## Skeleton

```markdown
# Decisions (ADR log)

A decision record. The current behavior is in the paper that points here. New decisions are appended.

## ADR-001 — <title>

<see the decision-section blueprint>
```

## Keep out

Step requirements. A running commentary on the current Goal. Deleting an old ADR because it feels embarrassing. Supersede it instead.
