<!--
  File: docs/protocol/blueprints/paperwork/decision-record.md
  Purpose: Shape of one decision record (ADR), and the operations that create and mark one
  Audience: Agents
  Update when: The record shape or an operation changes
-->

# Decision record

**Shapes:** every `docs/paperwork/decisions/ADR-0xx-<slug>.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** an architecture page ([../architecture/mechanism-page.md](../architecture/mechanism-page.md)) — what is true now. The ADR says why, and what was rejected.

A record answers a later reader who asks "why is it this way, and what did we reject?" It is never rewritten to match a new opinion. A newer ADR amends or supersedes it.

## Naming

Number: **Next ADR number** of [decision-index.md](decision-index.md). Slug: the rule in [goal.md](goal.md) (Naming), applied to the title.

## Skeleton

```markdown
<!--
  File: docs/paperwork/decisions/ADR-0xx-<slug>.md
  Purpose: Decision record ADR-0xx
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-0xx — <title>

**Date:** YYYY-MM-DD
**Status:** accepted

<What is now true, in sentences a reader can apply. A table when several choices were locked together.>

**Why:** <what the other option cost; the failure or pressure that made it worse>

**Supersedes:** <ADR-0yy, or the older sentence it replaces>

**Goal:** [G-0xx <name>](../goals/G-0xx-<slug>.md)
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# ADR-0xx — <title>` |
| `**Date:**` | yes | The date the decision was approved |
| `**Status:**` | yes | `accepted` or `superseded` |
| `**Amended by:**` / `**Superseded by:**` | when a later ADR touches it | Directly below `**Status:**`: `**Amended by:** [ADR-0yy](ADR-0yy-<slug>.md)` or `**Superseded by:** [ADR-0yy](ADR-0yy-<slug>.md)`. One line per later ADR |
| Decision | yes | Sentences, or a table. Approved by the human |
| `**Why:**` | yes | One paragraph |
| `**Supersedes:**` | when it replaces something older | Omit the line if nothing older moved |
| `**Goal:**` | when the decision belongs to a Goal | Link to the Goal file |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create** | [../../flows/step.md](../../flows/step.md) **Decide** step 4 |
| **Mark amended** | [../../flows/step.md](../../flows/step.md) **Decide** step 6 |
| **Mark superseded** | [../../flows/step.md](../../flows/step.md) **Decide** step 6 |

### Create

**Before.** The human approved the decision.

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md) as in the Skeleton.  
2. Copy the Skeleton. Fill the title, today's date, `accepted`, the decision, **Why**, and **Supersedes** (or omit it), and **Goal**.

### Mark amended

**Edit.**

1. Directly below `**Status:**`, add `**Amended by:** [ADR-0yy](ADR-0yy-<slug>.md)`. Change nothing else.

### Mark superseded

**Edit.**

1. Replace `**Status:** accepted` with `**Status:** superseded`.  
2. Directly below it, add `**Superseded by:** [ADR-0yy](ADR-0yy-<slug>.md)`. Change nothing else.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Its number and slug follow **Naming**, and it has a row on the decision index |
| 2 | It has a decision and a **Why** |
| 3 | A `superseded` record has a `**Superseded by:**` line |

## Keep out

- A second copy of the decision inside the index.  
- The implementation procedure: the architecture paper.  
- A requirement list: the Step record.
