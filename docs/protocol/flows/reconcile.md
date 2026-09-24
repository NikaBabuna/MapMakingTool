<!--
  File: docs/protocol/flows/reconcile.md
  Purpose: Detect torn Steps by reading Step-start marks in exact order
  Audience: Agents
  Update when: Mark files, check order, or handoff fields change
-->

# Reconcile

**Register:** legal.  
**Writes:** none. This flow does not create, edit, or delete any file. It runs only read-only git commands (`log`, `status`).  
**Templates:** none written. The handoff fields below are filled into the Status report under [../blueprints/messages/status-report.md](../blueprints/messages/status-report.md) by the Status report flow, or into the Rollback notification under [rollback.md](rollback.md).  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md) (Torn, Step status, Safe point, Unsealed change).  
**Mark authority:** Step flow stages STORE and MARK in [step.md](step.md).

## When

1. [../core-workflow.md](../core-workflow.md) Article 1, order 2 (after reading the protocol, before Status report); or  
2. Whenever core-workflow requires verification that no Step is torn before new work.

## Before

1. The agent has not edited any repository file in this chat for the purpose of new work.  
2. The agent has read [../brief.md](../brief.md) in this startup (or re-establishment) sequence.

## Mark files (complete list)

At Step start (STORE + MARK), the agent shall leave `in progress` in **exactly these three places** and nowhere else:

| # | File | What the mark is |
|---|------|------------------|
| M1 | `docs/paperwork/steps/F-0xx.md` | The Step record. Line `**Status:** \`in progress\`` per [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md) |
| M2 | `docs/paperwork/steps.md` | The Step registry. The row for that Step id has status cell `in progress` and a link to M1 |
| M3 | `docs/paperwork/goals/G-0xx-<slug>.md` | The parent Goal file. The Planned Steps table row for that Step id has status `in progress` |

Supporting read (not a Step-start mark, but required to find M3):

| # | File | Why |
|---|------|-----|
| G0 | `docs/paperwork/goals.md` | The only Active Goal line: `**Active Goal:**`. Names which G-0xx file is M3’s home |

Do not treat `AGENTS.md`, `.cursor/rules/protocol.mdc`, `docs/navigation.md`, or any door as a mark file.

## Check order (mandatory)

Execute in this order. Do not skip. Do not reorder.

### Step 1 — Read G0

1. Open `docs/paperwork/goals.md`.  
2. Find the line that begins with `**Active Goal:**`.  
3. Record for handoff:
   - `active_goal_id`: the G-0xx named on that line, or `none` if the line says there is none.  
   - `active_goal_path`: `docs/paperwork/goals/G-0xx-<slug>.md` if an id is named and that file exists; else `none`.

### Step 2 — Scan M2 (registry) for `in progress`

1. Open `docs/paperwork/steps.md`.  
2. Find every table row whose status cell is exactly `in progress` (case-sensitive as written in the registry).  
3. Record `registry_in_progress`: the list of Step ids on those rows (may be empty).  
4. If the list length is greater than 1, set `torn = true`, `torn_reason = multiple_in_progress_on_registry`, and go to **Branch torn**.

### Step 3 — Resolve the candidate Step id

1. If `registry_in_progress` is empty, set `candidate = none`.  
2. If it has exactly one id, set `candidate` to that id.  
3. Open M1 if `candidate` is not `none`: path `docs/paperwork/steps/<candidate>.md` (the registry link column must agree; if the link path differs, use the link path).  
4. If `candidate` is not `none` and M1 does not exist, set `torn = true`, `torn_reason = registry_in_progress_without_record`, and go to **Branch torn**.

### Step 4 — Read M1 status (when candidate exists)

1. Open M1.  
2. Read `**Status:**`.  
3. Record `record_status`.  
4. If `record_status` is `in progress`, continue.  
5. If `record_status` is `done` or `rolled back` while M2 still says `in progress`, set `torn = true`, `torn_reason = mark_mismatch_registry_vs_record`, and go to **Branch torn**.  
6. If `record_status` is any other value while M2 says `in progress`, set `torn = true`, `torn_reason = mark_mismatch_registry_vs_record`, and go to **Branch torn**.

### Step 5 — Read M3 status (when candidate exists)

1. If `active_goal_path` is `none`, set `torn = true`, `torn_reason = in_progress_without_active_goal`, and go to **Branch torn**.  
2. Open M3 at `active_goal_path`.  
3. In the Planned Steps table, find the row for `candidate`.  
4. If no such row exists, set `torn = true`, `torn_reason = in_progress_missing_on_goal_table`, and go to **Branch torn**.  
5. Read that row’s status cell into `goal_row_status`.  
6. If `goal_row_status` is not `in progress`, set `torn = true`, `torn_reason = mark_mismatch_goal_vs_registry`, and go to **Branch torn**.

### Step 6 — Orphan record scan (mandatory even when registry is clean)

1. List every file matched by `docs/paperwork/steps/F-*.md` (do not open the steps folder door as authority).  
2. For each file, read `**Status:**`.  
3. If status is `in progress` and that Step id was not in `registry_in_progress`, set `torn = true`, `torn_reason = record_in_progress_not_on_registry`, `candidate` = that id, and go to **Branch torn**.  
4. If status is neither `done` nor `rolled back` nor `in progress`, and the file contains a Requirements table with one or more requirement rows, set `torn = true`, `torn_reason = record_with_requirements_not_closed`, `candidate` = that id, and go to **Branch torn**.  
5. If more than one such orphan condition is found, prefer the first in lexicographic file-name order; still `torn = true`.

### Step 7 — Agreement of all three marks

When `candidate` is not `none` and Steps 4–5 passed:

1. Confirm M1, M2, and M3 all say `in progress` for the same Step id.  
2. If any disagrees, set `torn = true`, `torn_reason = mark_triple_disagree`, and go to **Branch torn**.  
3. Otherwise set `torn = true`, `torn_reason = in_progress_unfinished` (an open Step is torn until Accept). Go to **Branch torn**.

### Step 8 — Unsealed changes (only when no earlier step set `torn`)

1. Walk `git log` on the current branch from newest to oldest. Take the first commit whose subject matches `^Accept F-[0-9]+: ` or `^Seal: `. Record:
   - `safe_point`: that commit's full hash;  
   - `safe_point_subject`: its subject line.  
2. If no commit matches, set `safe_point = none` and go to Step 9. The Status report states that no safe point exists.  
3. List the commits in `<safe_point>..HEAD`. Record `commits_after` (full hashes, oldest first; may be empty).  
4. Run `git status --porcelain`. Record `unsealed_paths`: every path it lists as modified, added, deleted, renamed, or untracked (may be empty).  
5. If `commits_after` and `unsealed_paths` are both empty, go to Step 9.  
6. Otherwise set `torn = true`, `torn_reason = unsealed_changes`, `candidate = none`, and go to **Branch torn**.

### Step 9 — Clean

If `registry_in_progress` is empty, Step 6 found no orphan, Step 8 found no unsealed change, and no branch set `torn`, set:

- `torn = false`  
- `torn_reason = none`  
- `candidate = none`  

Go to **Branch clean**.

## Branch torn — exact actions

1. **Write:** nothing.  
2. **Do not** continue the torn Step. **Do not** open Goal flow or Step flow for new work.  
3. **Invoke next:** [rollback.md](rollback.md), immediately, with handoff:

| Field | Value |
|-------|--------|
| `torn` | `true` |
| `torn_reason` | as set above |
| `candidate` | Step id or `none` |
| `active_goal_id` | from Step 1 |
| `mark_snapshot` | what M1/M2/M3 each said (or `missing`) |
| `safe_point` | from Step 8, or run Step 8.1 now if a mark branch skipped it |
| `unsealed_paths` | from Step 8.4, or run Step 8.4 now if a mark branch skipped it |
| `commits_after` | from Step 8.3, or run Step 8.3 now if a mark branch skipped it |

4. After Rollback completes, the Status report flow runs (core-workflow Article 1). Reconcile does not itself emit the Status report text.

## Branch clean — exact actions

1. **Write:** nothing.  
2. **Invoke next:** [status-report.md](status-report.md), with handoff:

| Field | Value |
|-------|--------|
| `torn` | `false` |
| `torn_reason` | `none` |
| `candidate` | `none` |
| `active_goal_id` | from Step 1 |
| `active_goal_path` | from Step 1 |
| `registry_in_progress` | empty list |
| `rollback_ran` | `false` |
| `safe_point` | from Step 8 |
| `unsealed_kept` | `false` |

3. Status report shall fill [../blueprints/messages/status-report.md](../blueprints/messages/status-report.md) using at least:
   - **Reconcile:** `clean`  
   - **Step state:** `none in progress`  
   - **Active Goal:** from `active_goal_id` / Goal file if present  

## Done

- `torn` is decided.  
- Exactly one of: Rollback invoked, or Status report invoked with clean handoff.  
- No file was written by Reconcile.

## Not done

- Any repository file was edited under this flow.  
- Check order was skipped or reordered.  
- Step 8 was skipped while no earlier step had set `torn`.  
- A mark file outside M1–M3 was treated as authoritative for `in progress`.  
- New Step or Goal work began while `torn = true`.  
- Status report was invented without the handoff fields above.
