<!--
  File: docs/protocol/flows/judgment.md
  Purpose: How a chat starts, and how a torn Step is rolled back
  Audience: Agents and humans
  Update when: A judgment flow changes
-->

# Judgment flows

These two algorithms are how failure stays visible. **Reconcile** runs at the start of every chat, before any proposal. **Rollback** runs when reconcile finds a torn Step, or when the human ends a Step whose witness will not pass.

## Legal states

Reminders, so a status word is not used as a claim. The full table is in [../environment/correctness.md](../environment/correctness.md).

| State | Claim allowed |
|-------|----------------|
| Not started | None |
| `fr-approved` | The requirements are stored. Nothing has been implemented |
| `in progress` | Work is underway. `done` is a lie |
| `done` | Accept held, and the Step was committed |
| `rolled back` | The attempt was discarded toward the last Accept |

## Reconcile

**When.** A chat opens, including a chat that says “continue.”

**Before.** You have not edited anything in this chat.

**Steps.**

1. Read the Active Goal line on `docs/paperwork/goals.md`. Open that Goal if there is one.
2. Read `docs/paperwork/steps.md` for any row `in progress`.
3. If a Step record exists whose status is not `done` and not `rolled back`, read it.
4. If nothing is `in progress` and every existing Step record is `done` or `rolled back`, stop. Propose the next Goal if none is active, or the next Step if a Goal is active and its next row is `not started`. Write nothing.
5. If a Step is `in progress`, or a Step record holds requirements and was never Accepted, the Step is torn. Run **Rollback**. Do not continue it.

**Torn means any of these:**

| What you see | Why it is torn |
|--------------|----------------|
| A registry row `in progress` | Someone started and did not Accept |
| A Step record with requirements, and no Accept | The contract was stored and the work did not finish |
| Code or checks that do not match the last `done` Step | The tree moved without an Accept |
| A partial check with no green witness | The suite never sealed the Step |
| A status line `done` while the suite is red or was never run for that Step | False Accept. Demote it as part of rollback |

**Done.** You can tell the human either “nothing is open” or “this is what was left half-done, and I am rolling it back.”

**Not done.** You began a new Step on top of an `in progress` row.

## Rollback

**When.** **Reconcile** found a torn Step, or the human stopped a Step during the fix loop.

**Before.** You know the last Accept: the last commit whose Step is `done`.

**Steps.**

1. Return the tree to that Accept. Discard the uncommitted work of the torn Step. Do not keep “the good parts” unless the human asks to make them a new Step, which starts again at PROPOSE.
2. Clear `in progress` on `docs/paperwork/steps.md` and on the Goal’s Step table.
3. If the human still wants that Step’s requirements, set the Step record to `rolled back` and leave the file. If they do not, delete the Step record and remove its registry link. Do not leave a file that looks current.
4. Do not commit the failed attempt. A commit of a red tree is a false Accept.
5. Tell the human what was discarded, in the reply shape: what changed, what it means, what was not witnessed, what remains.

**Done.** The tree matches the last Accept, no row is `in progress`, and the human has heard what was thrown away.

**Not done.** The half-written files remain, the row says `done`, and the chat ends.

> Failure must never look like success.
