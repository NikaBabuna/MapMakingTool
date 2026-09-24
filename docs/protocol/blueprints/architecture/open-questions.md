<!--
  File: docs/protocol/blueprints/architecture/open-questions.md
  Purpose: Shape of the implementation paper's open-questions page, and the operations that edit it
  Audience: Agents
  Update when: The page shape or an operation changes
-->

# Open questions

**Shapes:** the one open-questions page of the implementation paper, at the path the paper abstract (`docs/architecture/README.md`) links as its open questions  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a mechanism page ([mechanism-page.md](mechanism-page.md)) — where a decided rule lives. The backlog ([../paperwork/backlog.md](../paperwork/backlog.md)) — work candidates. An open question is a gap in the design, not a task.

Implementation behaviour that is still undecided. A decided question does not stay here with its answer buried under it: the answer moves to the page that owns the rule.

## Skeleton

```markdown
<!--
  File: docs/architecture/<path>/open-questions.md
  Purpose: Implementation gaps that are still undecided
  Audience: Agents and humans
  Update when: A gap is decided or a new gap is found
-->

# Open questions

Decided questions live on the page that states the rule, with the decision record. This page lists only what is still open.

## Still open

| # | Topic | Question |
|---|-------|----------|
| <n> | <topic> | <the question, and what the code does today> |
…

## Decided elsewhere

| Topic | Where the rule is |
|-------|-------------------|
| <topic> | [<page>](<page>.md), [ADR-0xx](<relative path to the ADR>) |
…

Parent: [<the level page of this folder>](README.md).
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Still open | yes | Numbered rows. A number is never reused: a new question takes one more than the highest number ever used on this page, including decided ones |
| Decided elsewhere | yes | One row per decided question, linking the owning page and the ADR |
| Parent line | yes | Links the level page of the folder the page sits in |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add question** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step leaves a behaviour undecided; [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Decide question** | [../../flows/step.md](../../flows/step.md) **Decide**, when the decision answers an open question |

### Add question

**Edit.**

1. Add `| <n> | <topic> | <question, and what the code does today> |` at the bottom of **Still open**.

### Decide question

**Before.** The decision record exists, and the owning mechanism page states the rule.

**Edit.**

1. Delete the question's row from **Still open**.  
2. Add `| <topic> | [<page>](<page>.md), [ADR-0xx](<relative path to the ADR>) |` to **Decided elsewhere**.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | No row in **Still open** has its answer stated anywhere in the paper |
| 2 | Every **Decided elsewhere** link resolves |

## Keep out

- Answers: the owning mechanism page.  
- Work items: the backlog.
