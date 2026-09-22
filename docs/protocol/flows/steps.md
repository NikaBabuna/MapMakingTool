<!--
  File: docs/protocol/flows/steps.md
  Purpose: Bookkeeping algorithms for Steps
  Audience: Agents and humans
  Update when: A step flow changes
-->

# Step flows

## Store step

After the human approves the job and the requirements:

1. Create the Step record from the step blueprint, with those requirements and status `fr-approved`.
2. Link it from the step registry and from the blockers folder door.
3. Mark `in progress` on the Step record, the step registry, and the Goal’s Step table.

Implementation starts only after that mark.

## Implement

With the Step `in progress`:

1. Change only the files named in the approved job.
2. Add the checks the requirements name.
3. Fill the Step record’s test map so every requirement has a check.
4. Run the witness.
5. If it fails, fix and run it again.
6. If it keeps failing, stop and ask.
7. If the human ends the Step, run **Rollback**.

## Sync

After the witness is green, in this order:

1. Update each tied document the change actually touched: one paper, that folder’s door, and the glossary of that shelf when a new public word appeared.
2. Refresh the entrance map when a doc was added, moved, or removed, including its status cell.
3. Append a changelog line when the change is structure, phase, or scope.
4. Append a decision record when the change is a technical or structural decision.
5. Check the boxes in the Step record’s sync section.

Do not copy the Active Goal sentence onto the doors.

## Close step

After Sync:

1. Confirm the witness is green, the sync boxes are checked, and the doors still point at the goal index.
2. Commit.
3. Set the Step record, the step registry, and the Goal’s Step table to `done`.
4. Update the Goal’s progress counts.
5. If that was the last Step, run **Close goal**.

## Amend step

After the human approves a change to stored requirements:

1. Edit the Step record’s requirement table.
2. Keep already-mapped checks, and add checks for any new requirement.
3. Do not delete a requirement to make the witness pass.
