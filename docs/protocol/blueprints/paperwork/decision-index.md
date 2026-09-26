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
**Neighbours:** a decision record ([decision-record.md](decision-record.md)) — the decision itself, and why. The index lists them, one sentence each.

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

One file per decision. The next number is one higher than the last row. Each row says in one sentence what was decided. The record holds the whole decision, the reason for it, and what it replaced. A decision that a later one changed in part stays `accepted`, and its row names the later decision.

| ID | Title | Status | Decided | Doc |
|----|-------|--------|---------|-----|
| ADR-0xx | <title> | <accepted \| superseded> | <one sentence><. Amended by ADR-0yy.> | [ADR-0xx-<slug>.md](decisions/ADR-0xx-<slug>.md) |
…

How to write one: [../protocol/blueprints/paperwork/decision-record.md](../protocol/blueprints/paperwork/decision-record.md).
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening paragraph | yes | As in the Skeleton, word for word |
| Table | yes | One row per decision record, in number order, with no gaps. **Title** equals the record's title after `— `. **Status** equals the record's `**Status:**`. **Decided** is one sentence in plain words stating what the record decided, followed by ` Amended by ADR-0yy.` for each record whose `**Amended by:**` line names a later ADR, or ` Superseded by ADR-0yy.` |
| How-to line | yes | As in the Skeleton |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add row** | [../../flows/step.md](../../flows/step.md) **Decide** step 5 |
| **Set row status** | [../../flows/step.md](../../flows/step.md) **Decide** step 6 |
| **Note amendment** | [../../flows/step.md](../../flows/step.md) **Decide** step 6 |

### Add row

**Edit.**

1. Add at the bottom of the table:  
   `| ADR-0xx | <title> | accepted | <one sentence: what was decided> | [ADR-0xx-<slug>.md](decisions/ADR-0xx-<slug>.md) |`

### Set row status

**Before.** A new ADR supersedes this one.

**Edit.**

1. In the row of the older ADR, replace `accepted` with `superseded`.  
2. At the end of its Decided cell, add ` Superseded by ADR-0yy.`

### Note amendment

**Before.** A new ADR amends this one, and **Mark amended** of [decision-record.md](decision-record.md) was applied to its record.

**Edit.**

1. At the end of the older ADR's Decided cell, add ` Amended by ADR-0yy.`

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | One row per file in `docs/paperwork/decisions/` matching `ADR-*.md`, and one file per row |
| 2 | Numbers start at 001 and run with no gap |
| 3 | Each row's Title and Status equal its record |
| 4 | Each Decided cell is one sentence of decision, plus one `Amended by` or `Superseded by` sentence for each later ADR the record names |

## Keep out

- The full text of a decision, or its reasons: the record. The index holds one sentence.  
- Step requirements and Goal commentary.  
- Deleting an old ADR: supersede it instead.
