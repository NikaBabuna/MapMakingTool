<!--
  File: docs/protocol/flows/goals.md
  Purpose: Bookkeeping when a Goal is opened, amended, or closed
  Audience: Agents and humans
  Update when: A goal flow changes
-->

# Goal flows

A Goal is the durable result. These algorithms write the paperwork of that result. They do not implement it. The first Step of a new Goal is a separate approval and a separate flow.

## Open goal

**When.** The human has approved a Goal text: the result, the out-of-scope list, the decisions, and the planned Steps.

**Before.** The goal index has no other Goal `in progress`, or the human has said this one replaces it. The id `G-0xx` is the next free id.

**Steps.**

1. Write `docs/paperwork/goals/G-0xx-<slug>.md` from the goal blueprint. Status `in progress`. The planned Steps are listed. Their status is `not started`.
2. On `docs/paperwork/goals.md`, set the Active Goal line to this Goal, and add the table row with status `in progress`. The goals folder door points at this index. Do not copy a status table onto that door.
3. On `docs/paperwork/steps.md`, add a section for the Goal. Each planned Step is a row with status `not started` and no record link. Do not create `F-0xx.md` files yet. A Step record exists only after that Step is approved.
4. Add a row to `docs/paperwork/roadmap.md`.
5. If the idea had a row on `docs/paperwork/backlog.md`, change that row so it says the idea was promoted to this Goal.
6. Append a Structure line to `docs/paperwork/changelog.md`: the date, the Goal id, and that it was opened. No behavior claim.

**Done.** A later chat can open the goal index, see this Goal active, and see every planned Step as `not started`.

**Not done.** The Goal file exists but the index still names another Goal, or Step records were created before approval.

## Amend goal

**When.** The human approves a change to the wording of a Goal that already has a file.

**Before.** You can point at the sentences that change. A vague “clean it up” is not an amendment.

**Steps.**

1. Edit the Goal file. Keep the id. If a claim is added or removed, the claim list and the Step table must still agree.
2. If the name or the status changed, fix the row on `docs/paperwork/goals.md`. Do not copy that status onto the goals folder door.
3. Leave Step records alone unless the human also approved a change to a stored requirement. That second change is **Amend step**, not this algorithm.

**Done.** The Goal file and the index describe the same Goal.

**Not done.** Claims changed in the Goal while an Accepted Step record still promises the old claim, and nobody amended that record.

## Close goal

**When.** The last planned Step is Accepted, and every claim checkbox in the Goal holds because a witness exists, not because the box was ticked in hope.

**Before.** **Close step** has already run for that last Step.

**Steps.**

1. Set the Goal file’s status to `done`. Fill the progress table. Every claim box is checked only if the witness for it is named.
2. On `docs/paperwork/goals.md`, set that row to `done`. Change the Active Goal line. If no next Goal has been approved, the line says there is none, and it still lives only on this file. Do not copy that status onto the goals folder door.
3. Mark the roadmap row done.
4. Append a changelog line: the Goal id, the date, and that it closed. Do not claim a behavior the witnesses do not cover.

**Done.** The index shows the Goal `done`, and the Active Goal line does not still name it.

**Not done.** The Goal file says `done` while a Step row is still `in progress` or `not started`.
