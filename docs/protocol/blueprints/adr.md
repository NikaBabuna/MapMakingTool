<!--
  File: docs/protocol/blueprints/adr.md
  Purpose: Shape of one decision record
  Audience: Agents and humans
  Update when: The decision-record shape changes
-->

# Decision record

This is one file, `docs/paperwork/decisions/ADR-0xx-<slug>.md`. It is not a section inside the index. The index is `docs/paperwork/decisions.md`. It lists every ADR in number order, and the next number is one higher than the last row.

An ADR answers a future reader who asks “why is it this way, and what did we reject?” It does not answer “what does the code do right now?” That answer goes in the paper, with a pointer back to this id.

**Write it when.** **Decide** runs.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Title | `# ADR-0xx — <short title>` so the index can be scanned |
| Date and status | `accepted` unless a later ADR supersedes it. Do not delete. Mark the relationship |
| The decision | Sentences a reader can apply. Tables when several choices were locked together |
| Why | The failure or the pressure that made the other option worse. Without why, the next agent will relitigate |
| Supersedes or amends | The older ADR or the older sentence. Omit only if nothing older is touched |
| Goal link | When the decision belongs to a Goal, so it can be found from that Goal |

## Skeleton

```markdown
# ADR-0xx — <title>

**Date:** YYYY-MM-DD
**Status:** accepted

<What is now true, in sentences.>

**Why:** <what the other option cost.>

**Supersedes:** <ADR-0yy, or a sentence>. Omit if nothing older moved.

**Goal:** [G-0xx <name>](../goals/G-0xx-<slug>.md)
```

## Keep out

A second copy of the decision inside the index. The implementation procedure. A requirement list.
