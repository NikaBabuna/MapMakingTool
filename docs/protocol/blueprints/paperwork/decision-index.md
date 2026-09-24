<!--
  File: docs/protocol/blueprints/paperwork/decision-index.md
  Purpose: Shape of the decision index (docs/paperwork/decisions.md), the ADR number rule, and the operations that edit the index
  Audience: Agents
  Update when: The index shape, the number rule, or an operation changes
-->

# Decision index

**Shapes:** `docs/paperwork/decisions.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a decision record ([decision-record.md](decision-record.md)) — the decision itself. The index only lists them.

The sequence of decisions, so that their order and the next number are visible. What the code does today is stated in the architecture paper, which points at the ADR.

## Naming

**Next ADR number:** the highest `ADR-` number in the table, plus one, in three digits.

## Skeleton

```markdown
<!--
  File: docs/paperwork/decisions.md
  Purpose: Index of decision records
  Audience: Agents and humans
  Update when: A decision is added or its status changes
-->

# Decisions

One file per decision. The next number is one higher than the last row.

| ID | Title | Status | Doc |
|----|-------|--------|-----|
| ADR-0xx | <title> | <accepted \| superseded> | [ADR-0xx-<slug>.md](decisions/ADR-0xx-<slug>.md) |
…

How to write one: [../protocol/blueprints/paperwork/decision-record.md](../protocol/blueprints/paperwork/decision-record.md).
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening sentence | yes | As in the Skeleton |
| Table | yes | One row per decision record, in number order, with no gaps. **Title** equals the record's title after `— `. **Status** equals the record's `**Status:**` |
| How-to line | yes | As in the Skeleton |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add row** | [../../flows/step.md](../../flows/step.md) **Decide** step 5 |
| **Set row status** | [../../flows/step.md](../../flows/step.md) **Decide** step 6 |

### Add row

**Edit.**

1. Add at the bottom of the table:  
   `| ADR-0xx | <title> | accepted | [ADR-0xx-<slug>.md](decisions/ADR-0xx-<slug>.md) |`

### Set row status

**Before.** A new ADR supersedes this one.

**Edit.**

1. In the row of the older ADR, replace `accepted` with `superseded`.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | One row per file in `docs/paperwork/decisions/` matching `ADR-*.md`, and one file per row |
| 2 | Numbers start at 001 and run with no gap |
| 3 | Each row's Title and Status equal its record |

## Keep out

- The text of a decision: the record.  
- Step requirements and Goal commentary.  
- Deleting an old ADR: supersede it instead.
