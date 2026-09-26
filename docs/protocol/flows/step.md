<!--
  File: docs/protocol/flows/step.md
  Purpose: The Step lifecycle — select, propose, approve, store, mark, work, witness, sync, close — every file each stage writes, in order
  Audience: Agents
  Update when: A stage, a mark, the commit rule, a type rule, or a file written changes
-->

# Step flow

**Register:** legal. Every message to the human uses the understanding register of [../environment/style.md](../environment/style.md).  
**Writes:** the Step record ([../blueprints/paperwork/step.md](../blueprints/paperwork/step.md)), the Step registry ([../blueprints/paperwork/step-registry.md](../blueprints/paperwork/step-registry.md)), the Goal file ([../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md)), the files of the approved plan, and the pages SYNC names — each only through the operations of its blueprint.  
**Container:** the Step's own Accept commit, made in CLOSE.  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md) Articles 2–4. Correctness: [../environment/correctness.md](../environment/correctness.md).  
**Marks:** M1, M2, and M3 are the three mark files defined in [reconcile.md](reconcile.md) (Mark files).  
**Witness command:** the **Witness command** line of `docs/architecture/program.md` ([../blueprints/architecture/program.md](../blueprints/architecture/program.md)), run from the repository root.

A Step is one job. The stages below are the whole job, in order. PROPOSE, REQUIRE, and APPROVE may pass in one conversation. STORE may not be skipped, and nothing is edited before it.

## Lifecycle

| # | Stage | Who | End state |
|---|-------|-----|-----------|
| 0 | SELECT | Agent | The Step id to propose is known |
| 1 | PROPOSE | Agent | The human has the proposal: type, job, files, requirements |
| 2 | REQUIRE | Agent | Every requirement is measurable and names its witness |
| 3 | APPROVE | Human | Explicit approval of the latest full proposal |
| 4 | STORE | Agent | The Step record exists, with `in progress` (M1) |
| 5 | MARK | Agent | M2 and M3 say `in progress` |
| 6 | PLAN | Agent | The file list is stated in chat before the first edit |
| 7 | WORK | Agent | The job is implemented, according to its type |
| 8 | WITNESS | Agent | The standard of correctness for the type holds |
| 9 | FIX / ITERATE | Agent, Human | Repeat 7–8 until green (or complete, for Iterative), or stop, or roll back |
| 10 | SYNC | Agent | Every Sync box is ticked |
| 11 | CLOSE | Agent | Marks `done`, final witness green, Accept commit made, human told |

**Accept** is WITNESS, SYNC, and CLOSE together. The `done` marks record an Accept that the witness already established. The Accept commit seals it: it is a safe point for [rollback.md](rollback.md).

---

## 0 — SELECT

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | [reconcile.md](reconcile.md) ran in this chat and ended in Branch clean, or the human chose to keep unsealed changes ([rollback.md](rollback.md) Step 2) | Run Reconcile |
| B2 | No row in `docs/paperwork/steps.md` is `in progress` | Stop. One Step at a time |
| B3 | `docs/paperwork/goals.md` names an Active Goal | Stop. Offer [goal.md](goal.md) **Negotiate** |

**Steps.**

1. Open the Active Goal file. In its Planned Steps table, take the first row, top to bottom, whose status is `not started`. That row's id is the Step. If the human named a different listed Step, take that one.  
2. If the work the human wants is not a row of that table, a new Step is needed. Its id is **Next Step id** of [../blueprints/paperwork/step-registry.md](../blueprints/paperwork/step-registry.md). The proposal must also carry [goal.md](goal.md) **Amend goal** to add the row. Those Amend goal writes are made after APPROVE and before STORE, and this Step's Accept commit seals them.  
3. If the human chose to keep unsealed changes, this Step must adopt every kept path (PROPOSE step 3), unless the human now asks to discard them through [rollback.md](rollback.md).  
4. If no row is `not started` and the human named nothing, report that the Goal's planned Steps are exhausted, and offer a closing Step for [goal.md](goal.md) **Close goal**.

## 1 — PROPOSE and 2 — REQUIRE

**Steps.**

1. **Type.** Choose exactly one type. State it with a confidence label.

   | Type | Choose it when |
   |------|----------------|
   | `Modification` | The work was agreed in detail and can be done in one pass against fixed requirements |
   | `Iterative` | There is an aim, and the details will be found by showing increments to the human |
   | `Documentation` | Only documents change. No file under a source or test folder changes |
   | `Cleanup` | Dead matter is removed, or structure or organisation is restored, with no new product behaviour |

2. **Job.** A plain-English paragraph saying what will be true when the Step is done. No file list inside the paragraph.  
3. **Files.** Every file that will be created, edited, moved, deleted, or adopted, each with a confidence label. For every new source folder, say `landmark: yes` or `landmark: no`. A landmark folder gets a door (**Record source**). Every kept unsealed path appears with the action `adopt`, and for each the proposal says whether it will be kept as is, changed, or reverted.  
4. **Out of scope.** What will not change, especially the nearby things the human may assume will change.  
5. **Requirements.** Write the table:

   | Type | Rows |
   |------|------|
   | `Modification`, `Cleanup` with source | `FR-n`: one enforceable claim each. The witness is a named test, new or existing, by the name the test runner reports |
   | `Documentation`, `Cleanup` without source | `FR-n`: one observable document fact each. The witness is `reading <path>: <what is found>`, plus the existing suite remaining green |
   | `Iterative` | `AIM`: the aim, in one sentence. `BAR-n`: the initial bar, each one checkable. For each BAR about behaviour, a named test. The completion witness is the human writing that the Step is complete |

   Every table ends with the row: `Every test of every earlier Accepted Step remains green.` A Step the human approves to retire earlier tests, or to leave code defects in place, ends instead with: `Every test not listed under Retired tests or Known defects is green, and each Known defect fails only on the defect stated for it.` ([../environment/correctness.md](../environment/correctness.md) 3.2).

6. **Requirement wording.** Each requirement states a condition and an observable result. It contains none of the words "should", "properly", "clean", "better", "appropriately", or "as needed". A requirement that cannot fail its witness is rewritten or removed.  
7. **Assumptions.** List every assumption the plan relies on, each with a confidence label.  
8. **Send the proposal** in the shape below.

**Human message — Step proposal.**

```markdown
## Step proposal — F-0xx <short name>

**Goal:** G-0xx <name><; also asks to add this Step to the Goal>

**Type:** <type> (`certain` | `probable` | `uncertain`) — <reason>

**Job:** <what will be true>

**Files:**
- <path> — <create | edit | move | delete | adopt: keep | adopt: change | adopt: revert> (`certain` | `probable`)

**Out of scope:** <list>

**Requirements:**
| ID | Requirement | Witness |
|----|-------------|---------|
| FR-1 | <claim> | <test name | reading path> |

**Assumptions:** <each with a label, or none>

**Need from you:** approval of the type, the job, and the requirements — or corrections
```

## 3 — APPROVE

1. Approval is an explicit affirmative reply to the **latest full** proposal ("approved", "yes", "go ahead", or equivalent) that covers the type, the job, and the requirements.  
2. If the human corrects anything, revise, and send the full proposal again. Approval of an earlier version does not carry over.  
3. Partial approval ("yes, but change FR-3") is a correction, not an approval.  
4. Silence, a reply to a different message, or approval of the idea but not the text is not approval.  
5. Until approval: no Step record, no mark, and no edit to any file.  
6. After approval, if the proposal carried **Amend goal**, run [goal.md](goal.md) **Amend goal** steps 2–6 now, before STORE.

## 4 — STORE and 5 — MARK

**When.** Approval has been given, and any carried Amend goal writes are done.

**Steps.** Carry them out in this order, with no other edit in between.

1. **M1.** Apply **Create** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md). Include `### Change list` when this Step carries an Amendment, `### Adopted paths` when it adopts kept unsealed paths, and `### Confirmation` when it is a Bugfix.  
2. **M2.** Apply **Mark in progress** of [../blueprints/paperwork/step-registry.md](../blueprints/paperwork/step-registry.md).  
3. **M3.** Apply **Set step status** (`in progress`) of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md).  
4. **Verify.** Re-open M1, M2, and M3. All three say `in progress` for the same id. Now a crash leaves a torn Step that [reconcile.md](reconcile.md) will find.

**Not done.** Any other file changed before step 4. The requirements were paraphrased rather than copied.

## 6 — PLAN

1. Before the first edit, state in chat the files about to change.  
2. If that list is the approved **Files** list, or a subset of it, continue.  
3. If any file outside the approved list is needed, stop. Propose the addition, and obtain approval under **Amend step requirements**. Then continue.

## 7 — WORK

**Before.** STORE and MARK are verified.

**Steps, by type.**

| Type | Work rule |
|------|-----------|
| `Modification` | Implement the stored requirements in one pass. For each FR, write or adjust the test named in the Test map. Run **Record source** for each source file added, changed, or moved |
| `Documentation` | Edit documents only, each through the operations of its blueprint. Do not add a test whose only job is to search documents for phrases. A change to a standing document follows [amendment.md](amendment.md) Steps 3–9 |
| `Cleanup` | Remove or tidy only. No new behaviour. If a test must change because a path moved, retarget it. Never delete, skip, or soften it |
| `Iterative` | Work towards the AIM, one increment at a time. After each increment, send the increment message (FIX / ITERATE) and wait for the human's feedback |

**Adopted paths.** For each path under `### Adopted paths`, do what the approved proposal said: keep it as is, change it within the Job, or revert it (`git checkout -- <path>` for a tracked file, delete for an untracked one). Each adopted path that is a document must pass the **Check** of its blueprint before WITNESS.

**Shared rules.**

1. Change only files in the approved plan.  
2. Keep the Test map on M1 current, by **Update test map** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md).  
3. Do not delete, skip, disable, or loosen a test of an earlier Accepted Step. Do not change an expected value in an earlier test, unless an approved FR of this Step states the new value. The one exception is a Step whose approved Job retires earlier tests: it lists each one on M1 by **Add retired test** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md), and every retired test is listed before WITNESS.  
4. When a test fails because the code is wrong and the human has directed that the code not change in this Step, leave the test enabled and red, and apply **Add known defect** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md). Do not change the test to pass.  
5. Write each test beside a comment that names the requirements it proves, with the path of the Step record ([../environment/correctness.md](../environment/correctness.md) 3.3).

### Record source

**When.** A source file is added, changed, or moved during WORK.

1. A **new** source file: apply **Write header** of [../blueprints/headers/source-header.md](../blueprints/headers/source-header.md).  
2. A **changed** source file whose responsibility changed: apply **Update purpose** of [../blueprints/headers/source-header.md](../blueprints/headers/source-header.md).  
3. A **moved** source file: apply **Update path** of [../blueprints/headers/source-header.md](../blueprints/headers/source-header.md).  
4. A new source folder marked `landmark: yes`: apply **Create door** (Variant C) of [../blueprints/doors/folder-door.md](../blueprints/doors/folder-door.md) in that folder, then **Add child** of the same blueprint on its parent door or the module's `README.md`.  
5. The architecture and product pages are updated in SYNC (row Ties).

## 8 — WITNESS

**Steps.**

1. **Run.** Run the witness command from the repository root.  
2. **Green** means all of the following:
   1. the command exits with success;  
   2. no test failed and no test errored, except the tests M1 or an earlier record lists under **Known defects**, each failing only on its stated defect;  
   3. for each module, the number of tests run is not lower than the number recorded in the Witness section of the last Accepted Step's record, less the tests M1 lists under **Retired tests** for that module, plus the tests this Step adds. That is the Step named in the newest commit whose subject begins `Accept F-`. If that record gives no counts, skip this comparison and record counts now;  
   4. the number of skipped tests is not higher than that record's count.
3. **Per requirement.**

   | Type | Requirement is `met` when |
   |------|---------------------------|
   | Source (`Modification`, `Cleanup` with source, source-changing `Iterative`) | Its mapped test appears in the run and passed |
   | `Documentation`, `Cleanup` without source | You opened the named path and found the stated fact |
   | `Iterative` | Every BAR row is met. The human has written that the Step is complete |

4. **Record.** Apply **Add witness line** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md): a *Suite run* line; a *Documentation reading* line per document FR; and for `Iterative`, an *Iterative completion* line quoting the human.  
5. If every row is met and the run is green, go to SYNC. Otherwise go to FIX / ITERATE.

## 9 — FIX / ITERATE

**Red run.**

1. Read the first failing test's message and stack trace.  
2. Fix the cause, inside the approved plan.  
3. Run the **whole** witness command again. A single passing test is not a green run.  
4. If the same test fails after three consecutive fix attempts, or if the fix needs a file outside the plan, stop, and send the blocked message.  
5. If the human ends the Step, run [rollback.md](rollback.md) (When 2). Quote the human's words.

**Iterative increment.**

```markdown
## F-0xx — increment <n>

**What changed:** <plain words>
**Against the bar:** <each BAR — met | not yet>
**Suite:** <green | red | not run — documents only>
**Need from you:** feedback, or "complete" if the Step is done
```

On feedback, apply it within the AIM, and repeat. If the feedback moves outside the AIM, treat it as **Amend step requirements**.

**Human message — blocked.**

```markdown
## F-0xx — blocked

**What fails:** <test or requirement>
**What I tried:** <attempts, one line each>
**Why I stopped:** <three attempts | needs a file outside the plan: <path>>
**Options:** <amend the plan to include <path> (`probable`) | change the approach | end the Step and roll back>
**Need from you:** a decision
```

## 10 — SYNC

**When.** WITNESS holds.

**Steps.** Handle every row. For each, when done, apply **Tick sync box** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md), naming what was updated, or `N/A: <reason>`. Do not skip a row silently.

**Ties.** For each situation the Step created, apply the operation listed. Edit only the page that owns the matter.

| Situation | Apply |
|-----------|-------|
| A mechanism's behaviour changed | **Rewrite section** of [../blueprints/architecture/mechanism-page.md](../blueprints/architecture/mechanism-page.md), on the page found by searching `docs/architecture/` for the type or file name |
| A new mechanism has no page | **Create page** of [../blueprints/architecture/mechanism-page.md](../blueprints/architecture/mechanism-page.md), or **Add mechanism** on the existing page that groups it (Form B) |
| A stage was added, removed, or reordered in a level's run | **Add stage**, **Remove stage**, or **Reorder stages** of [../blueprints/architecture/level-page.md](../blueprints/architecture/level-page.md) |
| A module, a dependency, or the witness command changed | **Add module**, **Change dependency**, or **Set witness command** of [../blueprints/architecture/program.md](../blueprints/architecture/program.md) |
| Where tests live or how they are named, the project's languages, or the witness output format changed | **Set project fact** of [../blueprints/architecture/program.md](../blueprints/architecture/program.md) |
| A public implementation word was added, changed, or removed | **Add term**, **Amend term**, or **Remove term** of [../blueprints/architecture/glossary.md](../blueprints/architecture/glossary.md), on the glossary the paper abstract links |
| An implementation behaviour was left undecided | **Add question** of [../blueprints/architecture/open-questions.md](../blueprints/architecture/open-questions.md) |
| A domain word was added or changed | **Add word** or **Amend word** of [../blueprints/product/glossary.md](../blueprints/product/glossary.md) |
| A domain rule was added, changed, or replaced | **Create page**, **Amend rule**, or **Retire rule** of [../blueprints/product/wiki-page.md](../blueprints/product/wiki-page.md) |
| A person-facing sequence was added or changed | **Add journey**, **Amend journey**, or **Move to built** of [../blueprints/product/journeys.md](../blueprints/product/journeys.md) |
| A visible label, colour role, key, or screen region changed | **Add element**, **Amend element**, **Remove element**, or **Add area** of [../blueprints/product/style-guide.md](../blueprints/product/style-guide.md) |
| A promised facet of the product became available | **Mark facet built** of [../blueprints/product/concept.md](../blueprints/product/concept.md) |
| A folder's children changed | **Add child** or **Remove child** of [../blueprints/doors/folder-door.md](../blueprints/doors/folder-door.md), on that folder's door. For an architecture level page, **Add child row** or **Remove child row** of [../blueprints/architecture/level-page.md](../blueprints/architecture/level-page.md) |
| A document's responsibility changed | **Update purpose** of [../blueprints/headers/document-header.md](../blueprints/headers/document-header.md) |

**Entrance.** Apply the operations of [../blueprints/doors/doc-map.md](../blueprints/doors/doc-map.md) that match:

| Situation | Apply |
|-----------|-------|
| A document was added, moved, or removed, or a Status cell of `docs/navigation.md` became false | **Add row**, **Remove row**, **Relink row**, or **Correct status** |
| A module, or a code folder with its own door, was added | **Add code folder** |
| A generated folder appeared, or a tracked text file grew past 500 lines | **Add heavy place** |
| A heavy place was deleted, or shrank to 500 lines or fewer | **Remove heavy place** |

If a top-level docs folder changed, apply [amendment.md](amendment.md) Step 6.3.

**Changelog.** If this Step changed structure, phase, or scope: apply **Add line** of [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md), with the kind *Step structure change*, *Scope change*, or *Phase change*. Otherwise tick `N/A: no structure, phase, or scope change`.

**Decision.** If this Step made a technical or structural choice a later reader would question: run **Decide** below. Otherwise tick `N/A: no new decision`.

**Goal progress.** Ticked in CLOSE step 3.

Also confirm that no door carries an `**Active Goal:**` sentence.

**Done.** A reader of the entrance and of the pages this Step touched sees the tree as it now is.

**Not done.** The suite is green, and a door or page still describes the earlier state as current.

### Decide

**When.** SYNC row Decision applies, or [amendment.md](amendment.md) calls for it.

1. **Approval.** If the decision is not already written in the approved Job or requirements, stop. State the decision and its reason to the human, and obtain approval before writing it.  
2. **Number.** Take **Next ADR number** of [../blueprints/paperwork/decision-index.md](../blueprints/paperwork/decision-index.md).  
3. **Slug.** Apply **Naming** of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md) to the title.  
4. **Record.** Apply **Create** of [../blueprints/paperwork/decision-record.md](../blueprints/paperwork/decision-record.md).  
5. **Index.** Apply **Add row** of [../blueprints/paperwork/decision-index.md](../blueprints/paperwork/decision-index.md).  
6. **Older ADR.** If this decision amends an older ADR, apply **Mark amended** of [../blueprints/paperwork/decision-record.md](../blueprints/paperwork/decision-record.md) to it, and **Note amendment** of [../blueprints/paperwork/decision-index.md](../blueprints/paperwork/decision-index.md). If it supersedes one, apply **Mark superseded** to it and **Set row status** of [../blueprints/paperwork/decision-index.md](../blueprints/paperwork/decision-index.md).  
7. **Pointer.** On the architecture page the decision concerns, apply **Add pointer** of [../blueprints/architecture/mechanism-page.md](../blueprints/architecture/mechanism-page.md). For a stack decision, apply **Set stack row** of [../blueprints/architecture/program.md](../blueprints/architecture/program.md). For a scope decision, apply the scope operation of [../blueprints/product/concept.md](../blueprints/product/concept.md) that makes the change. If it answers an open question, also apply **Decide question** of [../blueprints/architecture/open-questions.md](../blueprints/architecture/open-questions.md).

## 11 — CLOSE

**When.** SYNC has finished.

**Steps.** Carry them out in this order.

1. **Boxes.** The Goal progress box is the only unticked Sync box on M1.  
2. **Requirements.** Apply **Set requirement status** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md). A row whose test is listed under **Known defects** is set to `known defect`. Any other row that cannot be `met` sends the Step back to FIX / ITERATE.  
3. **Marks and progress.**
   1. M1: apply **Set status** (`done`) of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md).  
   2. M2: apply **Mark done** of [../blueprints/paperwork/step-registry.md](../blueprints/paperwork/step-registry.md).  
   3. M3: apply **Set step status** (`done`) and **Update progress** of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md).  
   4. On M1, apply **Tick sync box** to Goal progress.
4. **Goal close.** If the approved Job names the Goal close, run [goal.md](goal.md) **Close goal** now. It returns here.  
5. **Final witness.** Run the witness command again: the suite reads documents that changed since the last run. It must be green by the WITNESS rule. Apply **Add witness line** (*Suite run*). If it is red: apply **Set status** (`in progress`) on M1, **Revert to in progress** on M2, and **Set step status** (`in progress`) on M3; undo **Update progress** and any Goal close writes; return to FIX / ITERATE.  
6. **Check the tree.** Run `git status --porcelain`. Every listed path must belong to one of: the approved plan, the adopted paths, the SYNC updates, the [global-docsync.md](global-docsync.md) fixes, M1, M2, M3, the carried Amend goal writes, or the Goal close writes. If any other path is listed, stop. List those paths, and ask the human whether they belong to this Step. Do not stage them, and do not discard them.  
7. **Commit.** Stage exactly the paths from step 6, including deletions. Commit with this subject line:  
   `Accept F-0xx: <one sentence saying what became true, for a reader or a user of the product>.`  
   The subject begins exactly `Accept F-0xx: `. It is one sentence and ends with a full stop. It contains no file list. Do not push unless the human asks.
8. **Verify the seal.**
   1. `git log -1 --format=%s` matches `^Accept F-[0-9]+: `.  
   2. In that commit, `docs/paperwork/steps/F-0xx.md` contains `**Status:** \`done\``.  
   3. `git status --porcelain` lists none of the Step's paths.
9. **Tell the human.**

```markdown
## F-0xx done

**What is now true:** <the commit sentence, in plain words>
**Witness:** <green on YYYY-MM-DD — counts>
**Goal:** G-0xx — <n> of <m> Steps done<; Goal closed>
**What you can do next:**
- <next planned Step F-0yy: intent> (`certain` | `probable`)
**Need from you:** <nothing — waiting for your instruction | approval to propose F-0yy>
```

**Done.** M1, M2, and M3 say `done` in the Accept commit. The commit subject matches the Rollback pattern. The work tree holds none of the Step's paths uncommitted.

**Not done.** `done` was written but not committed. The commit came before the `done` marks. The subject does not begin `Accept F-0xx: `. Unrelated paths were committed or discarded. The registry still says `in progress`.

---

## Amend step requirements

**When.** The human approves a change to a Step record's Job, Files, or Requirements, or asks to widen the plan (PLAN, step 3).

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | The old sentence and the new sentence can both be quoted | Ask for the exact change |
| B2 | The record to amend is `in progress`, or it is `done` and the amendment is made inside another Step that is `in progress` and whose Job names this amendment | Stop. An amendment to a `done` record needs a Step to carry it |

**Steps.**

1. **Propose.** Send the old and new text, quoted, with the reason. Await explicit approval, by the APPROVE rules.  
2. **Record.** Apply **Record amendment** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md).  
3. **Tests.** Keep every test that still matches. Add a test for every new or changed requirement. Change an existing test's expectation only as far as the approved new sentence states. Never delete a test.  
4. **Test map.** Apply **Update test map** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md).  
5. If the Files list widened, PLAN continues with the widened list.

**Done.** The record, the tests, and the human's approval describe the same bar.

**Not done.** A test changed and the requirement sentence did not, or the reverse. A test was weakened beyond what the approved sentence states.
