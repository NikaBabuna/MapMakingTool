<!--
  File: docs/protocol/flows/conflict-resolve.md
  Purpose: Resolve a disagreement between two sources of authority; change the losing source only on purpose, with approval
  Audience: Agents
  Update when: The conflict table, the fallback rule, or the routing of the loser changes
-->

# Conflict resolve

**Register:** legal. The conflict message uses the understanding register of [../environment/style.md](../environment/style.md).  
**Writes:** none. This flow only decides which source binds, and tells the human. If the loser is to change, that edit is made by the write flow named in Step 5, inside its own Step.  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md).

## When

1. Two sources give different instructions or state different facts about the same matter, and the agent must act on that matter in this turn; or  
2. Another flow sends a finding here ([global-docsync.md](global-docsync.md), [audit.md](audit.md), or [step.md](step.md)); or  
3. The human asks which of two sources binds.

A **source** is any one of: a chat statement, a stored document, a Step record, a Goal file, the Goal index, the scope document, a test, a status line, the source code, or a protocol instrument.

## Before

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | Both sources can be named: a path, or "chat" with the words quoted | Ask the human, or search until both can be named. Do not act on the matter meanwhile |
| B2 | The point of conflict can be stated as a single sentence on which the two sources disagree | Split it into separate conflicts, and run this flow once for each |

## Procedure (mandatory order)

### Step 1 — Write the conflict down

Record, for use in the message:

| Field | Content |
|-------|---------|
| Source A | Path and line, or "chat", and the quoted passage |
| Source B | Path and line, or "chat", and the quoted passage |
| Point | One sentence: what A says, and what B says instead |

### Step 2 — Find the row

Go through the table top to bottom and take the **first** row that matches. Do not average the two sources. Do not prefer what was said in chat because it is newer. Do not prefer the source that is more convenient for the current work.

| # | Conflict | Winner | What the agent does now |
|---|----------|--------|-------------------------|
| R1 | Two protocol instruments | Neither, by the agent's choice | Stop. Quote both passages. The human decides. The loser is changed by [amendment.md](amendment.md). Until then, take no action that depends on the point, and prefer no action over either reading |
| R2 | A convenient write sequence and [../core-workflow.md](../core-workflow.md) or a flow file | The core workflow, or the flow file | Do not take the shorter path. If no flow fits, stop and ask |
| R3 | A status line `done` and a witness that is red or was never run | The witness | Do not edit the status line by hand. A red test on an Accepted Step is a confirmed defect. Report it, and route it to [bugfix.md](bugfix.md) |
| R4 | An Accepted Step's test and a new Step that would delete, skip, or soften it | The old test | Refuse the deletion ([../environment/correctness.md](../environment/correctness.md) 3.2). The new Step changes its approach, or the human amends a requirement on purpose |
| R5 | Chat and a stored Step record | The Step record | Follow the record. The chat version becomes true only through **Amend step requirements** in [step.md](step.md) |
| R6 | A wish and the scope of `docs/product/concept.md` (**In scope** and **What it is not**) | The scope | Do not do the work. Scope changes first, through [amendment.md](amendment.md) (Scope), and then the work is done |
| R7 | A door or any other file that states an Active Goal, and the Goal index `docs/paperwork/goals.md` | The Goal index | Follow the index. The door is corrected to a pointer inside the next Step's Sync |
| R8 | An index (Goal index, Step registry, decision index, roadmap) and the record it lists | The record (the Goal file, the Step record, or the ADR file) | Follow the record. The index is corrected in the next Step's Sync |
| R9 | A Step record's requirement and the behaviour of source | The Step record | The behaviour is a suspected defect. Run [investigate.md](investigate.md), and then [bugfix.md](bugfix.md) if it is confirmed |
| R10 | An architecture page and the behaviour of source, with no Step requirement involved | The source, as the fact of what is built | The page is false. It is corrected in a Step's Sync, or in [global-docsync.md](global-docsync.md). If the source behaviour looks wrong rather than the page, run [investigate.md](investigate.md) |
| R11 | A product page (concept, journeys, wiki) and the behaviour of source | Neither, by the agent's choice | The product page states what the product is meant to be, and the source states what is built. Report both. The human decides whether the source is a defect or the page is to be amended |
| R12 | Chat and any other stored document | The document | Follow the document. The chat version becomes true only when the human approves an edit through the write flow that owns that document |
| R13 | Two stored documents not covered above | Neither, by the agent's choice | Stop. Quote both. The human decides |

### Step 3 — Act on the winner

1. From this point in the turn, follow the winner.  
2. If the row says "neither", take no action that depends on the point until the human decides.

### Step 4 — Tell the human

Send one message in the shape below. It is sent even when the winner is clear, because the loser is still false.

### Step 5 — Route the loser

1. If the human wants the loser to become true instead, take the route for that loser:

   | Loser | Route |
   |-------|-------|
   | A protocol instrument, a door, the entrance, the scope, or a standing page | [amendment.md](amendment.md) |
   | A stored requirement | **Amend step requirements** in [step.md](step.md) |
   | A claim or a planned Step on a Goal | **Amend goal** in [goal.md](goal.md) |
   | Source behaviour | A Step under [step.md](step.md), or [bugfix.md](bugfix.md) |
   | A status line | Not edited by hand. [bugfix.md](bugfix.md) or [rollback.md](rollback.md) as that flow requires |

2. If the human wants the winner to stay and the loser to be corrected, the same routes apply. The correction is made on purpose, inside its own Step.  
3. Do not edit both sources toward a compromise. One source is followed. If the other changes, it changes on purpose.

## Human message — conflict

```markdown
## Two sources disagree

**Point:** <one sentence>

**A says:** "<quote>" — <path or chat>

**B says:** "<quote>" — <path or chat>

**Which one I follow:** <A | B | neither until you decide> — because <rule in plain words> (`certain` | `probable`)

**What stays false until fixed:** <the loser>

**How it could be fixed:** <route> (`certain` | `probable` | `uncertain`)

**Need from you:** <nothing — I follow the winner | a decision between A and B | approval to fix the loser by <route>>
```

## Done

- Both sources and the point are quoted.  
- Exactly one row of the table was applied: the first row that matched.  
- The human has the message.  
- If a source is to change, it is routed to the write flow that owns it. No file was edited by this flow.

## Not done

- The agent followed chat against a stored document or a Step record.  
- Both sources were edited into a compromise.  
- A later row was chosen over an earlier one that also matched.  
- For R1, R11, or R13, the agent picked a winner without the human.  
- A status line was edited by hand to match a witness.
