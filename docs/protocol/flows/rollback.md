<!--
  File: docs/protocol/flows/rollback.md
  Purpose: Return the branch and work tree to the safe point, then repeat Reconcile
  Audience: Agents
  Update when: Safe-point identification, the consent rule, or the post-rollback sequence changes
-->

# Rollback

**Register:** legal for file operations; understanding for the human messages ([../environment/style.md](../environment/style.md)).  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md) (Accept commit, Seal commit, Safe point, Unsealed change, Torn).  
**Depends on:** [reconcile.md](reconcile.md).  
**Writes:** no paperwork. The marks are restored by returning to the safe point, never by hand. The human messages use the skeletons in Steps 2 and 5.

## When

1. Reconcile Branch torn invoked this flow with a handoff; or  
2. The human ends a Step whose witness will not pass; or  
3. [../core-workflow.md](../core-workflow.md) requires Rollback.

## Before

1. A Reconcile handoff is present (`torn`, `torn_reason`, `candidate`, `active_goal_id`, `mark_snapshot`, `safe_point`, `unsealed_paths`, `commits_after`), **or** the human has named the Step to abandon.  
2. The agent has not claimed the torn work `done`.

## Safe point (definition)

The **safe point** is the newest commit on the current branch whose subject matches `^Accept F-[0-9]+: ` (an Accept commit) or `^Seal: ` (a Seal commit), and that also satisfies:

| Kind | Also required at that revision |
|------|--------------------------------|
| Accept commit `Accept F-0xx: …` | `docs/paperwork/steps/F-0xx.md` contains `**Status:** \`done\`` |
| Seal commit `Seal: …` | No file matching `docs/paperwork/steps/F-*.md` contains `**Status:** \`in progress\`` |

If a matching commit fails its extra requirement, skip it and continue to the next older commit.

If no commit qualifies, stop. Notify the human that Rollback cannot run. Do not invent a baseline. Do not delete marks by hand as a substitute.

## Procedure (mandatory order)

### Step 1 — Identify the safe point

1. Walk `git log` on the current branch from newest to oldest.  
2. Take the first commit that qualifies under **Safe point**.  
3. Record:
   - `safe_point`: that commit's full hash;  
   - `safe_point_subject`: its subject line.  
4. If none qualifies, **stop** (see the definition). Write nothing else.  
5. Recompute, against this `safe_point`:
   - `commits_after`: every commit in `<safe_point>..HEAD`, oldest first;  
   - `discard_paths`: every path that `git status --porcelain` lists (modified, added, deleted, renamed, untracked).

### Step 2 — Consent (only when `torn_reason = unsealed_changes`)

When the torn reason is a Step mark or an open record, skip this step. The torn Step is rolled back at once.

When the torn reason is `unsealed_changes`, there is no Step mark. The changes may be the human's own. Do not discard anything until the human answers:

1. Send one message:

```markdown
## Unsealed changes found

**What I found:** changes since the last safe point (`<safe_point_subject>`) that no Step owns.

**Uncommitted paths:** <count>
- <path>
- …

**Commits after the safe point:** <none | hash — subject, one per line>

**If you discard:** the branch and work tree return to `<safe_point_subject>`. These changes are lost, except that the commits stay reachable in the reflog.

**If you keep:** nothing changes. The next Step must adopt every one of these paths, or you must discard them later.

**Need from you:** "discard" or "keep"
```

2. Await the answer. Silence is not consent.  
3. If the answer is **keep**: write nothing, and go to Step 6 with `unsealed_kept = true`.  
4. If the answer is **discard**: continue to Step 3.  
5. Any other answer: ask again. Take no other action.

### Step 3 — Go to the safe point

1. Discard all uncommitted changes in the work tree and the index, including untracked files that are not ignored, so that none survive the move.  
2. Move the branch and the work tree to `safe_point`, so that every tracked file matches it.  
   - Intent: after this step, `git status` is clean and `HEAD` is `safe_point`.  
3. **Do not** create a new commit.  
4. **Do not** keep "the good parts" of the torn work in the tree.  
5. **Do not** hand-edit M1, M2, or M3. The marks at `HEAD` are whatever the safe point left.

### Step 4 — Repeat Reconcile

1. Invoke [reconcile.md](reconcile.md) from its **Check order**, Step 1, as if startup order 2 were running again.  
2. Make no file edits between Step 3 of Rollback and that Reconcile.  
3. Reconcile outcomes:

| Reconcile result | Then |
|------------------|------|
| Branch clean | Continue to Step 5 with `reconcile_after = clean` |
| Branch torn | **Stop.** Do not loop Rollback again. Notify the human that the safe point still reconciles torn (`torn_reason` from the second Reconcile). Await instruction |

### Step 5 — Notify the human

Deliver **one** message in the understanding register. Every suggestion carries a confidence label.

Skeleton (required sections, in this order):

```markdown
## Rollback notice

**What changed:** Work tree and HEAD restored to the safe point (`<safe_point>`, `<safe_point_subject>`).

**Discarded paths:** <count>
- <path>
- …

**Commits removed from the branch:** <none | hash — subject, one per line> (still reachable in the reflog)

**What it means:** The torn attempt is not Accepted. Marks and files are those of the safe point.

**Reconcile after Rollback:** <clean | still torn: <torn_reason>>

**Prior torn handoff:** reason `<torn_reason>`; candidate `<candidate or none>`

**Need from you:** await instruction
```

### Step 6 — Status report

1. If `reconcile_after = clean`, or if the human chose **keep** in Step 2, invoke [status-report.md](status-report.md) with this handoff:

| Field | Value |
|-------|--------|
| `torn` | `false` if `reconcile_after = clean`; `true` if kept |
| `torn_reason` | `none`, or `unsealed_changes` if kept |
| `candidate` | `none` |
| `active_goal_id` | from the latest Reconcile |
| `active_goal_path` | from the latest Reconcile |
| `registry_in_progress` | empty |
| `rollback_ran` | `true` if Step 3 ran; `false` if kept |
| `safe_point` | from Step 1 |
| `unsealed_kept` | `true` if the human chose keep; otherwise `false` |
| `unsealed_paths` | the kept paths, if kept; otherwise empty |

2. Status report fills [../blueprints/messages/status-report.md](../blueprints/messages/status-report.md).  
3. Then **await** human instruction.  
4. If Step 4 stopped on still torn, do **not** run Status report. The Rollback notice is enough until the human instructs.

## Done

- Either `HEAD` is the safe point and Reconcile has run once after the move, or the human chose **keep** and nothing was changed.  
- If Reconcile after the move was clean, or the changes were kept: Status report was delivered, and the agent awaits.  
- The human was told every discarded path and every removed commit.  
- No commit was created by this flow.

## Not done

- Unsealed changes with no Step mark were discarded without the human's explicit "discard".  
- Marks were edited by hand instead of restoring the safe point.  
- A second Rollback was chained because Reconcile was still torn.  
- A commit was created for the torn tree.  
- Status report claimed `clean` without a clean Reconcile after the restore.  
- "Good parts" of the torn work were kept in the tree.
