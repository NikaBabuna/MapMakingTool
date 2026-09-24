<!--
  File: docs/protocol/flows/investigate.md
  Purpose: Test one assumption against evidence before anyone relies on it
  Audience: Agents
  Update when: The allowed actions, the outcome rules, or the report shape change
-->

# Investigate

**Register:** legal. The report uses the understanding register of [../environment/style.md](../environment/style.md).  
**Writes:** none in the repository. Scratch files may be written only outside the repository.  
**Hands off to:** nothing automatically. The outcome may make [bugfix.md](bugfix.md) or a Step lawful, but only after the human classifies the next need.  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md).

## When

1. The agent is about to rely on an assumption that no document or explicit human statement establishes, and a wrong assumption would change what gets written or built ([../core-workflow.md](../core-workflow.md) Article 4: assumption vs build); or  
2. A defect is suspected but not confirmed ([../core-workflow.md](../core-workflow.md) Article 4: unconfirmed defect vs Bugfix); or  
3. [conflict-resolve.md](conflict-resolve.md) rows R9 or R10 send a suspected source defect here; or  
4. The human asks for something to be checked ("is it true that…", "does X still…", "find out whether…").

## Before

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | The assumption can be written as one sentence that could turn out false | Rewrite it until it can. If it cannot be made falsifiable, this is a question for [explain.md](explain.md), not an investigation |
| B2 | Only one assumption is under test | Split it, and run this flow once for each |

## Allowed and forbidden actions

| Allowed | Forbidden |
|---------|-----------|
| Read any file in the repository | Edit, create, move, or delete any file in the repository |
| Search the repository | Any git operation that changes state: commit, stash, checkout, reset, merge, rebase, or branch creation |
| Run the witness command, or a single existing test | Add a test, or change a test |
| Run the program, through any of its entry points, in a way that writes no tracked file | Leave a process running at the end of the turn |
| Read git history: `log`, `show`, `diff`, `blame` | Mark any status, check any box, or fill any paperwork |
| Write scratch files outside the repository | Start a fix "while I am here" |

## Procedure (mandatory order)

### Step 1 — State the assumption

Record:

| Field | Content |
|-------|---------|
| Assumption | One sentence: "<subject> <is / does / contains> <claim>" |
| Raised by | The human, or the agent |
| Why it matters now | Which decision or action in this turn depends on it |
| Provisional answer | What the agent currently believes, with a confidence label (`certain`, `probable`, or `uncertain`) and the reason |

### Step 2 — Fix the test before gathering

Write down, before looking:

| Field | Content |
|-------|---------|
| Evidence | Which exact things will be examined: paths, test names, commands, or commits |
| Confirms if | The observation that would make the assumption true |
| Refutes if | The observation that would make it false |

If the confirm and refute conditions cannot both be written, return to Step 1 and narrow the assumption.

### Step 3 — Gather

1. Examine every item listed under Evidence, and only those items. If a further item turns out to be needed, add it to the Evidence list first, then examine it.  
2. For each item, note what was observed and where. Give a path and line, a test name and its result, or a command and its output summary.  
3. If an action would require a forbidden operation, do not take it. Record "not examined — needs <operation>", and continue.

### Step 4 — Decide the outcome

| Outcome | Rule | Confidence |
|---------|------|------------|
| Confirmed | The "confirms if" observation was made, and no observation refutes | `certain` if the evidence is a document passage, a test result, or observed program behaviour. `probable` if it rests on reading source without running it |
| Refuted | The "refutes if" observation was made | `certain` or `probable`, by the same rule |
| Open | Neither observation was made, or the evidence conflicts | `uncertain`. State what evidence would settle it |

If the evidence shows two sources of authority in disagreement, report that as well and name [conflict-resolve.md](conflict-resolve.md).

### Step 5 — Report

Send one message in the shape below.

### Step 6 — Stop

1. Do not edit anything, even when the outcome is Confirmed and the fix is obvious.  
2. If the outcome confirms a defect, say so in the report: "This confirms a defect. [bugfix.md](bugfix.md) is now lawful if you want it fixed."  
3. Await the human. Their reply is classified afresh under [../core-workflow.md](../core-workflow.md) Article 2.

## Human message — investigation report

```markdown
## Investigation

**Assumption:** <one sentence>

**Why it mattered:** <the decision that depends on it>

**What I examined:**
- <path:line | test | command> — <what I saw>

**Outcome:** <Confirmed | Refuted | Open> (`certain` | `probable` | `uncertain`) — <one-sentence reason>

**Not examined:** <items that needed a forbidden action, or none>

**What this makes possible:** <e.g. Bugfix is now lawful | nothing changes | still need X> (`certain` | `probable` | `uncertain`)

**Need from you:** <a decision on what to do next | a fact that would settle it | nothing — waiting for your instruction>
```

## Done

- The assumption, the evidence, and the confirm and refute conditions were written before gathering.  
- The outcome follows the Step 4 rule, and carries a confidence label.  
- The human has the report.  
- Nothing in the repository changed. No process was left running.

## Not done

- A hunch was treated as confirmed, and Bugfix or Implement began.  
- Evidence was gathered before the confirm and refute conditions were written.  
- A file was edited, a test was added, or git state changed.  
- The report states an outcome without naming what was examined.
