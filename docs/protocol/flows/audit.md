<!--
  File: docs/protocol/flows/audit.md
  Purpose: Honest review of a named target against stated hard criteria, with no edits
  Audience: Agents
  Update when: The default criteria, the verdict rules, or the report shape change
-->

# Audit / review

**Register:** legal. The review uses the understanding register of [../environment/style.md](../environment/style.md).  
**Writes:** none in the repository.  
**Hands off to:** nothing automatically. Each failed criterion names the write flow that would fix it. The human chooses.  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md).

## When

1. The human asks for a review, an audit, a check "against" something, or an honest opinion on the state of a named target; or  
2. [../core-workflow.md](../core-workflow.md) Article 4 requires Audit before a Bugfix or an Amendment, and the human chooses Audit over Investigate.

## Before

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | The **direction** is known: exactly which files, folders, Step, Goal, or behaviour are under review | Ask. Offer your best reading with a confidence label, and do not start until it is confirmed |
| B2 | The **criteria** are known. If the human names none, the default criteria below apply, and the report says so | — |
| B3 | Direction and criteria are both unambiguous. If either can be read two ways, it is `uncertain` | Ask the human to confirm. Do not start on an `uncertain` direction |

## Default criteria

Used when the human names none. Each row is one criterion, and each gets its own verdict.

| Id | Criterion | Source |
|----|-----------|--------|
| Q3.1–Q3.7 | Structural hard rules for documents and shelves | [../environment/quality.md](../environment/quality.md) 3.A |
| Q3.8–Q3.11 | Structural hard rules for source | [../environment/quality.md](../environment/quality.md) 3.B |
| Q3.12 | Scope of edits (for a Step's diff) | [../environment/quality.md](../environment/quality.md) 3.C |
| Q3.13 | Independence of the protocol (for any page under `docs/protocol/`) | [../environment/quality.md](../environment/quality.md) 3.D |
| Q4 | Synchronisation: the documents describing the target are true | [../environment/quality.md](../environment/quality.md) Article 4 |
| C3 | For source: every requirement mapped to a test, and the suite green | [../environment/correctness.md](../environment/correctness.md) Article 3 |
| C4 | For documents: shape matches the named blueprint | [../environment/correctness.md](../environment/correctness.md) Article 4 |
| S2 | Protocol text is in the legal register | [../environment/style.md](../environment/style.md) Article 2 |

Omit a row only when it cannot apply to the direction (for example, C3 for a folder of product pages). Mark such rows `N/A`, with the reason, in the report.

## Procedure (mandatory order)

### Step 1 — Fix direction and criteria

Record:

| Field | Content |
|-------|---------|
| Direction | The exact list of paths, or the Step or Goal id with the paths it covers |
| Criteria | The list of criterion ids with their sources. Either the human's own, or "default" |
| Out of view | What will deliberately not be looked at |

### Step 2 — Inspect

1. Open every path in the direction, and nothing outside it. [../navigation/bounds.md](../navigation/bounds.md) applies. A sibling file may be opened only when a criterion requires comparing against it (for example, a link target for Q3.6). Note each such file.  
2. For each criterion, for each target, decide:

   | Verdict | Rule |
   |---------|------|
   | `hold` | The criterion is met, and you can point at where |
   | `fail` | The criterion is not met, and you can point at the passage or line that breaks it |
   | `N/A` | The criterion cannot apply to this target. Say why |

3. Every `hold` and every `fail` carries evidence as a path and line. A verdict without evidence is not a verdict. Mark it `uncertain` and say what would settle it.  
4. Do not soften. A breach of a hard rule is `fail` even when it is small or old. Put the size in the **Weight** column, not in the verdict.

### Step 3 — Weigh each failure

| Weight | Meaning |
|--------|---------|
| `blocks` | It makes a claim, an Accept, or a later flow false or unsafe |
| `degrades` | It makes the tree harder to follow, but nothing false results |
| `cosmetic` | Wording or format only |

### Step 4 — Name the fix route for each failure

For each `fail`, name the write flow that would fix it: a Step under [step.md](step.md), [bugfix.md](bugfix.md), [amendment.md](amendment.md), a Cleanup Step, or [global-docsync.md](global-docsync.md). Add a confidence label. Name the route. Do not perform it.

### Step 5 — Report

Send one message in the shape below. List every failure. Do not sample or summarise away failures.

### Step 6 — Stop

Edit nothing. Await the human's classification of what to fix, under [../core-workflow.md](../core-workflow.md) Article 2.

## Human message — review

```markdown
## Review — <direction in plain words>

**What I reviewed:** <paths or Step/Goal>
**Against:** <criteria list, or "the default protocol criteria">
**Not looked at:** <out of view>

**Summary:** <n> criteria hold, <n> fail (<n> blocking), <n> not applicable.

| # | Criterion | Target | Verdict | Evidence | Weight | Fix route |
|---|-----------|--------|---------|----------|--------|-----------|
| 1 | <id> | <path> | fail | <path:line — what> | blocks | <flow> (`probable`) |

**Uncertain:** <verdicts I could not settle, and what would settle them — or none>

**Need from you:** <which failures to fix, and by which route | nothing — waiting for your instruction>
```

The table lists every `fail` and every uncertain item. `hold` rows may be summarised as a count, unless the human asked to see them.

## Done

- Direction and criteria were fixed before inspection.  
- Every criterion has a verdict for every target, with evidence.  
- Every failure has a weight and a named fix route.  
- The human has the review. No file was edited.

## Not done

- Issues were fixed during the audit.  
- A failure was omitted, merged into another, or reworded as a hold.  
- Files outside the direction were read without a criterion requiring it.  
- A verdict was given without a path and line.
