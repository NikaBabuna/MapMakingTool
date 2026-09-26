<!--
  File: docs/protocol/flows/goal.md
  Purpose: Negotiate, open, amend, close, and abandon Goals, capture backlog ideas, and seal Goal paperwork — every file each algorithm writes, in order
  Audience: Agents
  Update when: Goal bookkeeping, negotiation rules, sealing, or the files written change
-->

# Goal flow

**Register:** legal. Every message to the human uses the understanding register of [../environment/style.md](../environment/style.md).  
**Algorithms:** **Negotiate**, **Open goal**, **Amend goal**, **Close goal**, **Abandon goal**, **Capture idea**, **Seal**.  
**Writes:** the files W1–W7 below, each only through the named blueprint's operations.  
**Container:** a Seal commit (algorithm **Seal**), or the Accept commit of the Step the algorithm runs inside. See **Sealing rule**.  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md) Articles 1, 3, and 4.1.

These algorithms write the paperwork of a Goal. They do not implement Steps. Every Step, including the first, is a separate approval under [step.md](step.md).

## Files this flow writes

| Id | File | Blueprint |
|----|------|-----------|
| W1 | `docs/paperwork/goals/G-0xx-<slug>.md` | [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md) |
| W2 | `docs/paperwork/goals.md` | [../blueprints/paperwork/goal-index.md](../blueprints/paperwork/goal-index.md) |
| W3 | `docs/paperwork/steps.md` | [../blueprints/paperwork/step-registry.md](../blueprints/paperwork/step-registry.md) |
| W4 | `docs/paperwork/roadmap.md` | [../blueprints/paperwork/roadmap.md](../blueprints/paperwork/roadmap.md) |
| W5 | `docs/paperwork/backlog.md` | [../blueprints/paperwork/backlog.md](../blueprints/paperwork/backlog.md) |
| W6 | `docs/paperwork/changelog.md` | [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md) |
| W7 | The Goal pointer on `AGENTS.md`, `README.md`, `.cursor/rules/protocol.mdc`, `docs/README.md`, `docs/navigation.md` | [../blueprints/doors/goal-pointer.md](../blueprints/doors/goal-pointer.md) |

The `**Active Goal:**` sentence is written only in W2. W7 changes only the pointer text beside each door's link to W2.

## Sealing rule

Only an Accept commit or a Seal commit is a safe point ([rollback.md](rollback.md)). Every write of this flow is therefore sealed by one of them, before the chat ends:

| Algorithm | Sealed by |
|-----------|-----------|
| Open goal | **Seal**, subject `Seal: open G-0xx — <name>.` |
| Amend goal, invoked from a Step proposal ([step.md](step.md) SELECT step 2) | The Accept commit of that Step |
| Amend goal, invoked on its own | **Seal**, subject `Seal: amend G-0xx — <what changed, in a clause>.` |
| Close goal | The Accept commit of the Step it runs inside |
| Abandon goal | **Seal**, subject `Seal: abandon G-0xx — <reason, in a clause>.` |
| Capture idea | **Seal**, subject `Seal: capture idea — <idea, in a clause>.` |

---

## Negotiate

**When.** The human wants a durable result that no Goal on `docs/paperwork/goals.md` already covers, or a backlog idea is to be promoted.

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | [reconcile.md](reconcile.md) ran in this chat and ended in Branch clean, or the human chose to keep unsealed changes | Run Reconcile |
| B2 | No Step is `in progress` | Finish or roll back that Step first |

**Steps.**

1. **Classify the type.** Decide between `Polishing` (improve what exists; no new capability is the primary aim) and `Extension` (add what did not exist). State the choice with a confidence label and one sentence of reason. The type needs the human's explicit approval.  
2. **Collect the content for that type.** Every item must be present in the proposal:

   | Item | Polishing | Extension |
   |------|-----------|-----------|
   | Prior: what the last Goal left true | required | required |
   | What exists now that this Goal touches | required | — |
   | What "better" means, measurably | required | — |
   | What must not change | required | — |
   | What is new | — | required |
   | What it depends on that already exists | — | required |
   | Result we want: numbered claims, each of which a witness can support | required | required |
   | Out of scope | required | required |
   | Decided for this Goal: choices already made | required, or "none" | required, or "none" |
   | Planned Steps: intent, type, and order | required | required |
   | Slug, by **Naming** of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md) | required | required |

3. **Check each claim.** A claim is acceptable only if a witness can support it: a test that can fail, or a reading of a named document. If no such witness exists, rewrite the claim or drop it.  
4. **Send the proposal** in the shape below. Every suggested item carries a confidence label.  
5. **Revise** on each correction, and send the full revised text again. Approval applies only to the text the human last saw.  
6. **Approval** is an explicit affirmative reply to the latest full proposal ("approved", "yes", "go ahead", or equivalent), covering the type, the result, the out-of-scope list, the decisions, the planned Steps, and the slug. Anything less is not approval.  
7. Write no file during Negotiate.

**Human message — Goal proposal.**

```markdown
## Goal proposal — <name>

**Type:** <Polishing | Extension> (`certain` | `probable` | `uncertain`) — <reason>

**File name:** G-0xx-<slug>.md

**Prior:** <what is true now>

**Result we want** — when this Goal is done:
1. <claim> — witness: <test or document reading> (`probable`)

**Out of scope:**
- <exclusion>

**Already decided:**
| Topic | Decision |
|-------|----------|

**Planned Steps:**
| # | Intent | Type |
|---|--------|------|
| 1 | <one line> | <type> (`probable`) |

**Need from you:** approval of the type and this text, or corrections
```

**Done.** The human has approved the type and the full text, or has refused.

**Not done.** A file was written. The type was never approved. A claim has no possible witness.

---

## Open goal

**When.** The human has approved a Goal text under **Negotiate**.

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | No row of W2 has status `in progress` | If the human wants the new Goal to replace it, run **Close goal** (through a closing Step, if its claims hold) or **Abandon goal** on that Goal first |
| B2 | No Step is `in progress` | Stop |

**Steps.** Carry them out in this order.

1. **Goal id.** Take **Next Goal id** of [../blueprints/paperwork/goal-index.md](../blueprints/paperwork/goal-index.md).  
2. **Step ids.** Take **Next Step id** of [../blueprints/paperwork/step-registry.md](../blueprints/paperwork/step-registry.md), and assign consecutive ids to the planned Steps in their approved order.  
3. **W1.** Apply **Create** of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md).  
4. **W2.** Apply **Set active goal**, then **Add row**, of [../blueprints/paperwork/goal-index.md](../blueprints/paperwork/goal-index.md).  
5. **W3.** Apply **Add goal section** of [../blueprints/paperwork/step-registry.md](../blueprints/paperwork/step-registry.md). Create no Step record.  
6. **W4.** Apply **Add goal row** of [../blueprints/paperwork/roadmap.md](../blueprints/paperwork/roadmap.md).  
7. **W5.** If the Goal came from a backlog row, apply **Mark promoted** of [../blueprints/paperwork/backlog.md](../blueprints/paperwork/backlog.md).  
8. **W6.** Apply **Add line** of [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md), kind *Goal opened*.  
9. **W7.** Apply **Set pointer** of [../blueprints/doors/goal-pointer.md](../blueprints/doors/goal-pointer.md).  
10. **Verify.** Run the **Check** of each blueprint written in steps 3–9. The id, name, and slug are identical in every file.  
11. **Seal.** Run **Seal** with subject `Seal: open G-0xx — <name>.`  
12. **Continue.** Propose the first planned Step under [step.md](step.md) SELECT.

**Done.** A Seal commit contains W1–W7. A later chat reading W2 sees this Goal active, with every planned Step `not started` on W3.

**Not done.** A Step record was created. W2 names another Active Goal. The id, name, or slug differs between files. An `**Active Goal:**` sentence was written onto a door. The writes were left unsealed.

---

## Amend goal

**When.** The human approves a change to a Goal that already has a file: its name, a claim, the out-of-scope list, a decision, or the list or order of planned Steps. A change to a **stored requirement** is **Amend step requirements** in [step.md](step.md), not this algorithm.

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | The old text and the new text can both be quoted | Ask for the exact change |
| B2 | The Goal's status is `in progress` | A `done` or `abandoned` Goal is not amended. Propose a new Goal |
| B3 | No claim is removed or weakened while an Accepted Step's requirement still promises it | Also amend that Step's requirement under [step.md](step.md), with approval, or keep the claim |

**Steps.**

1. **Propose.** Send the old and new text, quoted, with the reason and a confidence label. Await explicit approval. When invoked from a Step proposal, this is part of that proposal message.  
2. **W1.** Apply, for each approved change, the matching operation of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md):

   | Change | Operation |
   |--------|-----------|
   | Adds a claim | **Add claim** |
   | Removes a claim | **Remove claim** |
   | Adds a planned Step | **Add planned step** |
   | Removes a `not started` planned Step | **Remove planned step** |
   | Reorders planned Steps | **Reorder planned steps** |
   | Changes the name, the result wording, out of scope, or a decision | **Amend text** |

   A planned Step that is `in progress`, `done`, or `rolled back` is never removed.
3. **W2.** If the name changed, apply **Rename row** of [../blueprints/paperwork/goal-index.md](../blueprints/paperwork/goal-index.md). If the result changed, apply **Set result** of the same blueprint.  
4. **W3.** If planned Steps changed, apply **Add row**, **Remove row**, or **Reorder rows** of [../blueprints/paperwork/step-registry.md](../blueprints/paperwork/step-registry.md), matching step 2.  
5. **W7.** If the name changed and this Goal is active, apply **Set pointer** of [../blueprints/doors/goal-pointer.md](../blueprints/doors/goal-pointer.md).  
6. **Verify.** Run the **Check** of the goal-file and step-registry blueprints.  
7. **Seal.** If invoked from a Step proposal, do nothing: that Step's Accept commit seals these writes. Otherwise run **Seal** with subject `Seal: amend G-0xx — <what changed>.`

**Done.** The Goal file, the index, and the registry describe the same Goal, and the writes are sealed or carried by a Step.

**Not done.** Claims changed while an Accepted Step still promises the old claim. A Step row exists in W1 but not in W3, or the reverse. A Step id was renumbered.

---

## Close goal

**When.** Close goal runs **inside the last planned Step**, during [step.md](step.md) CLOSE step 4, after that Step's `done` marks are set and before its Accept commit. That Step's approved Job must name the Goal close. If the last planned Step was already committed without closing the Goal, add a `Documentation` Step whose Job is "Goal close" by **Amend goal** (from a Step proposal), and run it.

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | Every row in W1's Planned Steps is `done`, including the Step now closing | Stop. The Goal cannot close |
| B2 | Every claim has a named witness that holds now | Stop. Tell the human which claim lacks one. Propose an Amend goal or a further Step |

**Steps.**

1. **Global docsync.** Run [global-docsync.md](global-docsync.md) in full. Do not continue while an unaccepted `needs approval` finding falsifies a claim.  
2. **Claims.** For each box, confirm its witness, then apply **Check claim** of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md). A box whose witness does not hold stays unchecked, and the close stops (B2).  
3. **W1.** Apply **Set goal status** (`done`) and **Update progress** of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md).  
4. **W2.** Apply **Set row status** (`done`), then **Clear active goal**, of [../blueprints/paperwork/goal-index.md](../blueprints/paperwork/goal-index.md). If the human has already approved a next Goal text, run **Open goal** for it after the closing Step's Accept commit.  
5. **W4.** Apply **Set row status** (`done`) of [../blueprints/paperwork/roadmap.md](../blueprints/paperwork/roadmap.md).  
6. **W6.** Apply **Add line** of [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md), kind *Goal closed*.  
7. **W7.** Apply **Clear pointer** of [../blueprints/doors/goal-pointer.md](../blueprints/doors/goal-pointer.md).  
8. **Verify.** Run the **Check** of the goal-file and goal-index blueprints. No row in W1 or in its W3 section is `in progress` or `not started`.  
9. **Return** to [step.md](step.md) CLOSE step 5.

**Done.** The index shows the Goal `done`, and the Active Goal line does not name it. Every box is checked with a named witness. Global docsync is recorded on the closing Step.

**Not done.** Global docsync was skipped. A box was checked without a named witness. A Step row is `in progress` or `not started` while the Goal says `done`. The close was committed apart from the closing Step's Accept commit.

---

## Abandon goal

**When.** The human says that the Active Goal is to end without its claims being completed.

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | The human's words ending the Goal can be quoted | Ask for confirmation. Silence is not approval |
| B2 | No Step is `in progress` | Stop. Tell the human that the open Step must be rolled back first. On their explicit consent, run [rollback.md](rollback.md) for that Step. Then return here |

**Steps.**

1. **W1.** Apply **Set goal status** (`abandoned`) and **Record abandonment** of [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md). Leave unchecked boxes and `not started` rows as they are.  
2. **W2.** Apply **Set row status** (`abandoned`), then **Clear active goal** (abandon form), of [../blueprints/paperwork/goal-index.md](../blueprints/paperwork/goal-index.md).  
3. **W4.** Apply **Set row status** (`abandoned`) of [../blueprints/paperwork/roadmap.md](../blueprints/paperwork/roadmap.md).  
4. **W6.** Apply **Add line** of [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md), kind *Goal abandoned*.  
5. **W7.** Apply **Clear pointer** of [../blueprints/doors/goal-pointer.md](../blueprints/doors/goal-pointer.md).  
6. **Seal.** Run **Seal** with subject `Seal: abandon G-0xx — <reason>.`

**Done.** A Seal commit shows the Goal `abandoned`, and the Active Goal line says none.

**Not done.** A claim box was checked. A Step record was deleted. The Goal was marked `done`.

---

## Capture idea

**When.** The human states an idea to keep for later, not to work on now.

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | No Step is `in progress` | Tell the human the idea will be captured after the open Step closes. Capture it then |
| B2 | The human approved the wording of the row | Propose the wording, and wait |

**Steps.**

1. **W5.** Apply **Add idea** of [../blueprints/paperwork/backlog.md](../blueprints/paperwork/backlog.md).  
2. **Seal.** Run **Seal** with subject `Seal: capture idea — <idea>.`

**Done.** A Seal commit holds the new backlog row.

**Not done.** The row was written without approval of its wording, or left unsealed.

---

## Seal

**When.** Open goal, Amend goal (invoked on its own), Abandon goal, or Capture idea has finished its writes and its Verify step.

**Before.**

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | No Step is `in progress` | Stop. The writes belong to that Step's Accept commit |

**Steps.**

1. **Check the tree.** Run `git status --porcelain`. Every listed path must be one the invoking algorithm wrote (its W-files), or one of the unsealed paths the human chose to keep ([rollback.md](rollback.md) Step 2). If any other path is listed, stop. List it, and ask the human. Do not stage it, and do not discard it.  
2. **Stage** exactly the invoking algorithm's W-files, including deletions. Do not stage kept unsealed paths. If a W-file also holds a kept unsealed change, tell the human, before staging, that sealing will include that change, and wait for their answer.  
3. **Commit** with the subject from the **Sealing rule** table. The subject begins exactly `Seal: `, is one line, and ends with a full stop.  
4. **Verify.**
   1. `git log -1 --format=%s` begins `Seal: `.  
   2. `git status --porcelain` lists none of the staged W-files.  
   3. No file `docs/paperwork/steps/F-*.md` contains `**Status:** \`in progress\``.
5. Do not push unless the human asks.

**Done.** The newest commit is a Seal commit containing exactly the W-files. It is now the safe point.

**Not done.** An unrelated path was committed. The subject does not begin `Seal: `. A Seal commit was made while a Step was `in progress`.
