<!--
  File: docs/protocol/blueprints/step.md
  Purpose: Shape of a Step record, including the copyable template
  Audience: Agents and humans
  Update when: The Step-record shape changes
-->

# Step record

This is `docs/blockers/F-0xx.md`. It is the contract for one Step. The human-approved requirements live here, and only here. The checks that witness them are mapped here. A later chat reads this file, not the chat that approved it.

A living example is `docs/blockers/F-063.md`. The blockers door, `docs/blockers/README.md`, indexes every record. That index is a door plus a registry table, not a second contract.

**Write it when.** **Store step** runs, which is immediately after the human approves the job and the requirements, and before any implementation.

**Edit it when.** Checks are mapped (**Implement**), sync boxes are filled (**Sync**), status moves to `done` or `rolled back`, or the human approves an amendment (**Amend step**).

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Header comment | File, Purpose, Audience, Update when. A reader knows the file’s job before the title |
| Title | `F-0xx — <short name>`, the same name as the registry row |
| Goal and status | Which Goal owns it. Status moves `fr-approved` → `in progress` → `done` or `rolled back`. `fr-approved` means the contract is stored and implementation has not started |
| Job | The approved work in one paragraph. Scope and non-scope, so a later edit can be judged against it |
| Requirement table | Id, the measurable sentence, status. Each sentence must be capable of failing a check. “Clean up” is not a row |
| Test map | After implementation, every requirement points at a check, or at the existing suite with a written reason. Empty cells before implementation are honest. Missing cells after implementation are a blocked Step |
| Incremental note | This Step’s Accept includes every earlier Accepted Step |
| Sync checklist | The rows from the **Sync** flow, checked or marked `N/A` with a reason |
| Witness checklist | The suite is green, and the docs match |

## Skeleton

Copy this at STORE. Replace the placeholders. Do not delete a section because this Step feels small.

```markdown
<!--
  File: docs/blockers/F-0xx.md
  Purpose: Approved requirements and check mapping for Step F-0xx
  Audience: Agents and humans
  Update when: Requirements change with approval, or checks are added
-->

# F-0xx — <short name>

**Goal:** G-0xx
**Status:** `fr-approved`

## Job (approved)

<One paragraph: what will be different, and what will not.>

## Functional requirements (approved)

| ID | Requirement (measurable) | Status |
|----|--------------------------|--------|
| FR-1 | <sentence a check can fail> | pending |

## Test mapping

| FR | Check | Notes |
|----|-------|-------|
| FR-1 | <check id, or “existing suite” with a reason> | |

## Incremental note

Accept requires this Step’s checks and every earlier Accepted Step’s checks.

## SYNC (before Accept)

- [ ] Ties for what this Step changed (or N/A: …)
- [ ] Entrance updated if a doc was added, moved, or removed (or N/A: …)
- [ ] Doors still point at the goal index
- [ ] Glossary row for a new public word (or N/A: …)
- [ ] The one paper that describes this behavior (or N/A: …)
- [ ] Goal close (or N/A: Goal not closed)

## Witness

- [ ] This Step’s checks green
- [ ] Earlier Steps’ checks still green
- [ ] Sync section above filled
```

## Keep out

The mechanism itself. A Step record that also explains how subduction works will be the only copy, and it will go stale. Point at the paper. Also keep out a second statement of the Active Goal line.
