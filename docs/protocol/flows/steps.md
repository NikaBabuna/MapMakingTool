<!--
  File: docs/protocol/flows/steps.md
  Purpose: The Step lifecycle and the bookkeeping algorithms that implement it
  Audience: Agents and humans
  Update when: A step flow changes
-->

# Step flows

A Step is one job. The lifecycle below is the whole job, in order. The algorithms under it are the parts that edit files. You do not skip a stage because the human seems to be in a hurry. You may pass PROPOSE, REQUIRE, and APPROVE in one conversation. You may not pass STORE.

## Lifecycle

| # | Stage | Who acts | What is true at the end of the stage |
|---|-------|----------|--------------------------------------|
| 1 | PROPOSE | Agent | The human has a plain-English job: scope, files, intent |
| 2 | REQUIRE | Agent | The human has a list of measurable requirements |
| 3 | APPROVE | Human | The human has accepted that job and that list. Until this moment, no Step record and no implementation |
| 4 | STORE | Agent | `docs/blockers/F-0xx.md` contains the approved list. Chat is not the store. This is **Store step**, through the mark |
| 5 | MARK | Agent | The Step is `in progress` on the Step record, the step registry, and the Goal’s Step table. This happens before any implementation, so a crash leaves a visible torn Step |
| 6 | PLAN | Agent | The human has heard which files will change. If they already approved the job and the requirements, that approval covers the plan. A wider plan needs a new approval |
| 7 | CODE | Agent | The approved files are edited. This is **Implement**, together with the next stage |
| 8 | TESTS | Agent | Every requirement has a check, and the Step record maps requirement to check. A documentation Step maps to the existing suite. It does not add a program whose only job is to search documents for phrases |
| 9 | WITNESS | Agent | `./mvnw test` or `mvnw.cmd test` is green for this Step and for every earlier Accepted Step |
| 10 | FIX LOOP | Agent | A failure is analyzed and fixed, then the witness runs again. If it keeps failing, the agent stops, explains, and asks. If the human says stop, **Rollback** |
| 11 | SYNC | Agent | The **Sync** algorithm below has run |
| 12 | CHECK | Agent | The witness is green, Sync is done, and the doors still point at the goal index |
| 13 | COMMIT | Agent | The Step is committed. The commit is the seal, not the proof. The proof was the witness |
| 14 | CLOSE MARK | Agent | `in progress` is cleared. The Step is `done` only because the witness held. This is **Close step** |

**Accept** is stages 9, 11, 12, and 14 together. The definition is in [../environment/correctness.md](../environment/correctness.md). A status word written early is not Accept.

## Store step

**When.** The human has approved the job and the requirements.

**Before.** The id `F-0xx` is the next free id. The Goal file already lists this Step.

**Steps.**

1. Create `docs/blockers/F-0xx.md` from the step blueprint. Status `fr-approved`. The requirement table is the approved list, not a paraphrase that drops a clause.
2. Add a row to the registry table in `docs/blockers/README.md`.
3. On `docs/project/features.md`, set the row’s blocker link and then set status `in progress`.
4. Set the same status on the Step record and on the Goal’s Step table.

**Done.** A crash after this algorithm leaves `in progress` behind, which **Reconcile** can see.

**Not done.** Source files changed in the same turn before the file existed.

Implementation starts only after step 4.

## Implement

**When.** The Step is `in progress` and the plan is inside the approved job.

**Before.** **Store step** has finished.

**Steps.**

1. Change only the files the job named.
2. Add the checks the requirements name. For a behavior change, each requirement gets a check that can fail. For a documentation change, do not add a check that only searches prose for a phrase. Say in the Step record that the existing suite is the witness.
3. Fill the Step record’s test map so every requirement points at a check, or at the existing suite with a written reason.
4. Run the witness.
5. If it fails, read the failure, fix the cause, and run it again.
6. If it keeps failing, stop and ask, in the reply shape from [../environment/engagement.md](../environment/engagement.md).
7. If the human ends the Step, run **Rollback**. Do not leave the red tree marked `in progress` and walk away.

**Done.** The witness is green, or the human has chosen rollback.

**Not done.** A requirement has no check and no written reason, or an older check was deleted to get green.

## Sync

**When.** The witness is green, and before **Close step**.

**Before.** You can list the files the Step actually changed.

**Steps.** Do them in this order. If a row does not apply, write `N/A` and the reason in the Step record. Do not skip the row silently.

| # | Check | What you update |
|---|-------|-----------------|
| 1 | Ties | The one paper, door, or glossary the change actually touched. One mechanism page, plus its parent list if a child was added or removed. Not every sibling |
| 2 | Entrance | `docs/navigation.md` and `docs/README.md` when a document was added, moved, or removed. Status cells that still say “through F-00x” or “deferred until” after this Step made that false |
| 3 | Changelog | A line when the change is structure, phase, or scope. Not a line for every edit |
| 4 | Decision | A new `ADR-0xx` section when the change is a technical or structural decision |
| 5 | Step record | The sync boxes in `F-0xx.md` checked, or marked `N/A` |
| 6 | Goal progress | Counts on the Goal file if they changed |

Do not copy the Active Goal sentence onto `AGENTS.md`, the editor rule, `README.md`, or `docs/navigation.md`. Those doors point at `docs/project/goals.md`.

**Done.** A reader of the entrance and of the one paper you touched sees the same world the code is in.

**Not done.** The suite is green and a door still describes the previous Goal as current.

## Close step

**When.** **Sync** has finished.

**Before.** The witness is still green. You did not edit after the last green run without running it again.

**Steps.**

1. Confirm the sync boxes, and confirm the doors point at the goal index rather than carrying their own Active Goal line.
2. Commit the Step. The message says what became true, not a file list.
3. Set `done` on the Step record, on `docs/project/features.md`, and on the Goal’s Step table.
4. Update the Goal’s progress counts.
5. If that was the last Step and the claims hold, run **Close goal**.

**Done.** The Step is `done` in all three places, and the commit contains that tree.

**Not done.** `done` is written and the commit is missing, or the Goal still lists the Step as `in progress`.

## Amend step

**When.** The human approves a change to requirements that are already stored.

**Before.** You can quote the old sentence and the new sentence.

**Steps.**

1. Edit the requirement table in `docs/blockers/F-0xx.md`. Write the reason in the record.
2. Keep checks that still match. Add checks for any new requirement.
3. Do not delete a requirement, and do not weaken a check, in order to make the witness pass. If the human wants a weaker bar, the new sentence must say the weaker bar, and they must have approved it.

**Done.** The file, the checks, and the human’s approval describe the same bar.

**Not done.** The check changed and the requirement sentence did not, or the reverse.
