<!--
  File: docs/protocol/flows/bugfix.md
  Purpose: Correct a defect that is already confirmed, inside one Step, with a regression witness seen red before the fix
  Audience: Agents
  Update when: The confirmation rule, the regression rule, or the Step mapping changes
-->

# Bugfix

**Register:** legal. Messages to the human use the understanding register of [../environment/style.md](../environment/style.md).  
**Writes:** only through [step.md](step.md), and through the blueprint operations that flow names. This flow adds the rules that make a Step a bugfix. It does not write paperwork itself.  
**Container:** one Step under [step.md](step.md).  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md). Correctness: [../environment/correctness.md](../environment/correctness.md).

## When

A defect is **confirmed**, and the work is to correct it. A defect is confirmed only by one of these:

| # | Confirmation | What must exist |
|---|--------------|-----------------|
| K1 | An [investigate.md](investigate.md) outcome of Confirmed | The investigation report in this chat, or cited from an earlier chat |
| K2 | An [audit.md](audit.md) verdict of `fail` | The review row, with evidence |
| K3 | A failing test that names the defect | The test name and its failure output |
| K4 | An explicit human statement that the defect is real | The human's words, quoted |
| K5 | [conflict-resolve.md](conflict-resolve.md) row R3 (a status line `done` with a red witness) | The red test on the Accepted Step |

Anything else, such as a hunch, "this looks wrong", or a suspicion drawn from reading code, is not confirmation. Run [investigate.md](investigate.md) first.

## Before

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | [reconcile.md](reconcile.md) has run in this chat and ended in Branch clean, or the human chose to keep unsealed changes that this Step will adopt | Run Reconcile |
| B2 | One confirmation from K1–K5 exists | Run [investigate.md](investigate.md) |
| B3 | The defect is one defect. Several defects are several bugfixes | Split them. Propose them one at a time, or ask the human to order them |
| B4 | A Goal is active on `docs/paperwork/goals.md` | Stop. Tell the human that a Bugfix needs a Step, and a Step needs a Goal. Offer to negotiate a Goal under [goal.md](goal.md) |

## Procedure (mandatory order)

### Step 1 — State the defect

Record, for the proposal:

| Field | Content |
|-------|---------|
| Defect | One sentence: "<what happens>, when <condition>; it should <what should happen>" |
| Expected behaviour source | Where "should" comes from: a Step requirement (id), an architecture page, a product page, or the human's statement |
| Confirmation | Which of K1–K5, with the evidence quoted or cited. Confidence `certain` |
| Where it lives | The files that the defect is believed to be in, with a confidence label on each |

### Step 2 — Choose the Step type

| Defect is in | Step type | Witness kind |
|--------------|-----------|--------------|
| Source behaviour | `Modification` | A regression test (Step 3) plus the full suite |
| A document that is false, with source unaffected | `Documentation` | Reading the corrected passage, plus the suite green |
| Dead or duplicate matter that causes the fault, and removing it changes no intended behaviour | `Cleanup` | Per the files touched, as in [../environment/correctness.md](../environment/correctness.md) |

### Step 3 — Write the requirements

The requirement table of the Step always begins with:

| ID | Requirement |
|----|-------------|
| FR-1 | `<regression test name>` reproduces the defect: it fails on the tree before the fix and passes after it |
| FR-2 | `<the corrected behaviour, stated measurably>` |
| FR-n | Every test of every earlier Accepted Step remains green |

1. For a `Documentation` bugfix, FR-1 is replaced by "`<path>` states `<the corrected fact>`", and there is no regression test.  
2. Add further FRs only for parts of the same defect. Do not add features.  
3. If the defect was confirmed by K3, the failing test itself may serve as FR-1. Name it.

### Step 4 — Propose through the Step flow

1. Run [step.md](step.md) from SELECT through APPROVE, using Steps 1–3 as the content of the proposal.  
2. If the Active Goal's Planned Steps table does not list this Step, the proposal carries **Amend goal** ([step.md](step.md) SELECT step 2), to add it.  
3. At STORE, the Step record carries the Step 1 table under `### Confirmation` (**Create** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md)).  
4. Nothing is edited before [step.md](step.md) STORE and MARK have finished.

### Step 5 — See it red (source bugfixes only)

1. Write the FR-1 regression test first, and change nothing else.  
2. Run that test alone. It must **fail**, and the failure must show the defect.  
3. Record the red run: apply **Add witness line** (*Regression test seen red*) of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md).  
4. If it passes before any fix, the test does not reproduce the defect. Rewrite the test. If it cannot be made to fail, stop: the confirmation may be wrong. Report it to the human, and propose [investigate.md](investigate.md).

### Step 6 — Fix

1. Change only the files in the approved plan.  
2. Change the least code that makes FR-1 and FR-2 hold. Do not refactor neighbouring code, rename, or tidy unless an FR requires it.  
3. Continue [step.md](step.md) at WITNESS. The whole suite must be green. The FR-1 test must pass.

### Step 7 — Sync and close

Continue [step.md](step.md) SYNC and CLOSE. In Sync:

1. If the defect contradicted an architecture page, apply **Rewrite section** of [../blueprints/architecture/mechanism-page.md](../blueprints/architecture/mechanism-page.md) to that page (Ties).  
2. If the defect was a false document (a `Documentation` bugfix), the correction is the work itself. Sync checks that no other page repeats the false statement. Search for its key phrase.

## Done

- The confirmation (K1–K5) is recorded on the Step record's Job.  
- For source bugfixes: the regression test was seen red, and the red run is recorded. It is now green.  
- Every earlier Accepted Step's tests are green.  
- The Step is closed under [step.md](step.md), with its Accept commit.

## Not done

- Bugfix ran on an unconfirmed assumption.  
- The fix landed without a Step record.  
- The regression test was written after the fix, or was never seen red.  
- The fix changed behaviour beyond the stated defect.  
- An earlier test was deleted, skipped, or softened to make the fix pass.
