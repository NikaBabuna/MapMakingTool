<!--
  File: docs/protocol/blueprints/paperwork/step.md
  Purpose: Shape of one Step record, and the operations that create and edit it
  Audience: Agents
  Update when: The record shape, a line format, or an operation changes
-->

# Step record

**Shapes:** every `docs/paperwork/steps/F-0xx.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the Step registry ([step-registry.md](step-registry.md)) — status and link only. The Goal file ([goal.md](goal.md)) — claims, not requirements.

The contract for one job: the approved job, its type, its requirements, the witness for each, the Sync record, and the witness log. It is mark M1 for [../../flows/reconcile.md](../../flows/reconcile.md), which reads its `**Status:**` line.

## Skeleton

```markdown
<!--
  File: docs/paperwork/steps/F-0xx.md
  Purpose: Approved requirements and witness map for Step F-0xx
  Audience: Agents and humans
  Update when: Requirements change with approval, or witnesses are added
-->

# F-0xx — <short name>

**Status:** `in progress`  
**Type:** `<Iterative | Modification | Documentation | Cleanup>`  
**Goal:** G-0xx

## Job

<the approved job, word for word>

## Requirements

| ID | Requirement | Status |
|----|-------------|--------|
| FR-1 | <enforceable claim> | unmet |
…
| FR-n | Every test of every earlier Accepted Step remains green. | unmet |

## Test map

| Requirement | Witness |
|-------------|---------|
| FR-1 | <test name \| reading <path>: <what is found>> |
…

## Sync

- [ ] Ties
- [ ] Entrance
- [ ] Changelog
- [ ] Decision
- [ ] Goal progress

## Witness

not run
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# F-0xx — <short name>`, the name on the registry row |
| `**Status:**` | yes | `` `in progress` ``, `` `done` ``, or `` `rolled back` ``, in backticks. A record never says `not started`: it exists only after approval |
| `**Type:**` | yes | The approved type, in backticks |
| `**Goal:**` | yes | The parent Goal id |
| Job | yes | The approved job, word for word. It may contain these subsections, in this order, each only when its flow requires it: `### Change list` ([../../flows/amendment.md](../../flows/amendment.md) Step 3), `### Adopted paths` ([../../flows/step.md](../../flows/step.md) PROPOSE), `### Confirmation` ([../../flows/bugfix.md](../../flows/bugfix.md) Step 1), `### Amendments` (**Record amendment**) |
| Requirements | yes | Rows `FR-n` (Modification, Documentation, Cleanup) or `AIM` and `BAR-n` (Iterative). The last row is always the earlier-Steps row of the Skeleton. **Status** is `unmet` or `met` |
| Test map | yes | One row per requirement, naming its witness: the test's name as the test runner reports it, or `reading <path>: <what is found>`, or for AIM `human completion mark` |
| Sync | yes | The five boxes, in this order. Unhandled: `- [ ] <Box>`. Handled: `- [x] <Box> — <what was updated>`. Not applicable: `- [x] <Box> — N/A: <reason>` |
| Witness | yes | `not run` until the first run. Then one line per event, newest last, in the forms under **Add witness line**. May contain a `### Global docsync — YYYY-MM-DD` subsection |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create** | [../../flows/step.md](../../flows/step.md) STORE (M1) |
| **Tick sync box** | [../../flows/step.md](../../flows/step.md) SYNC, CLOSE step 3; [../../flows/amendment.md](../../flows/amendment.md) Step 9 |
| **Add witness line** | [../../flows/step.md](../../flows/step.md) WITNESS, CLOSE step 5; [../../flows/bugfix.md](../../flows/bugfix.md) Step 5; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 17 |
| **Add docsync log** | [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 15 |
| **Set requirement status** | [../../flows/step.md](../../flows/step.md) CLOSE step 2 |
| **Set status** | [../../flows/step.md](../../flows/step.md) CLOSE step 3, CLOSE step 5 (revert) |
| **Record amendment** | [../../flows/step.md](../../flows/step.md) Amend step requirements |
| **Update test map** | [../../flows/step.md](../../flows/step.md) WORK (shared rule 2), Amend step requirements |

### Create

**Before.** The human approved the type, the job, and the requirements. The id is the registry row's id.

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md), exactly as in the Skeleton, with the id.  
2. Title, `**Status:** \`in progress\``, `**Type:**`, `**Goal:**`.  
3. Job: the approved job, word for word. Then, when they apply: `### Change list` (the Amendment table, copied), `### Adopted paths` (one bullet per path: `- <path> — adopted from unsealed changes kept on YYYY-MM-DD`), `### Confirmation` (the Bugfix defect table, copied).  
4. Requirements: the approved rows, word for word, each `unmet`, ending with the earlier-Steps row.  
5. Test map: one row per requirement.  
6. Sync: the five unticked boxes.  
7. Witness: `not run`.

**Result.** A record that Reconcile reads as `in progress`.

### Tick sync box

**Edit.**

1. Replace `- [ ] <Box>` with `- [x] <Box> — <what was updated, naming paths>`, or with `- [x] <Box> — N/A: <reason>`.

### Add witness line

**Edit.** If the section says `not run`, replace that line. Otherwise add the line at the bottom of the section (above any `### Global docsync` subsection). Use exactly one of these forms:

| Event | Line |
|-------|------|
| Suite run | `- YYYY-MM-DD — <witness command> — <green \| red> — <module> <run>/<failed>/<skipped>, …` |
| Regression test seen red (Bugfix) | `- YYYY-MM-DD — <test name> — red before fix — <one line of the failure>` |
| Iterative completion | `- YYYY-MM-DD — human marked complete: "<the human's words>"` |
| Documentation reading | `- YYYY-MM-DD — reading <path> — <FR id> met` |

### Add docsync log

**Edit.**

1. At the end of the Witness section, add:

```markdown
### Global docsync — YYYY-MM-DD

| Check | File | Finding | Action |
|-------|------|---------|--------|
| <Step n> | <path> | <what was false> | fixed \| needs approval — <human's decision> |
```

2. If there were no findings, write the heading and the line `No findings.`

### Set requirement status

**Edit.**

1. In each Requirements row, replace `unmet` with `met`.

### Set status

**Edit.**

1. Replace the value of `**Status:**` with `` `done` ``, or with `` `in progress` `` for a revert.

### Record amendment

**Edit.**

1. Replace the requirement's text in the Requirements table (or the Job text) with the approved new text.  
2. Add, under `### Amendments` at the end of Job (create the heading if absent):  
   `- **Amended YYYY-MM-DD:** <ID or Job> was "<old>"; now "<new>". Reason: <reason>. Approved by the human.`  
3. If the amendment is carried by another Step, add the same line to that Step's Job, followed by ` (on F-0yy)`.

### Update test map

**Edit.**

1. Replace the Witness cell of the requirement's row, or add a row for a new requirement.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | `**Status:**` is one of the three values, and equals the registry row and the Goal's Planned Steps row |
| 2 | Every requirement has a Test map row |
| 3 | If `**Status:**` is `` `done` ``: every requirement is `met`, every Sync box is `- [x]`, and the Witness section has a green suite line (or, for Documentation, reading lines and a green suite line) |

## Keep out

- Goal claim essays: the Goal file.  
- Unrelated file lists.  
- Requirements softened to pass a suite: forbidden by [../../environment/correctness.md](../../environment/correctness.md).
