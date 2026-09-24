<!--
  File: docs/protocol/flows/explain.md
  Purpose: Answer a question about structure, meaning, or layout so that the human understands it
  Audience: Agents
  Update when: The answer procedure, the understanding checklist, or the answer shape change
-->

# Explain

**Register:** legal. The answer uses the understanding register of [../environment/style.md](../environment/style.md).  
**Writes:** none in the repository.  
**Hands off to:** nothing automatically. If the answer shows that a change is needed, the change is named, and the human classifies it.  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md). Understanding: [../environment/style.md](../environment/style.md) Article 3.

## When

1. The human asks what something is, what it means, why it is where it is, how parts relate, or how something works; or  
2. [../core-workflow.md](../core-workflow.md) Article 4 requires Explain before an Amendment (question about structure vs edit of structure).

Use [investigate.md](investigate.md) instead when the answer depends on testing whether something is true. Use [audit.md](audit.md) instead when the human wants a judgment against criteria.

## Before

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | The question can be restated in one sentence | Ask. Offer your best restatement, with a confidence label |

## Procedure (mandatory order)

### Step 1 — Restate the question

Write the question as one sentence, in the human's vocabulary, so that they can correct it. If two readings are possible, answer the more likely one and name the other.

### Step 2 — Find the owner

1. Decide which shelf owns the subject, using [../environment/map.md](../environment/map.md):

   | Subject | Owner |
   |---------|-------|
   | How agents must work, a flow, a rule, a document shape | `docs/protocol/` |
   | What the product is for a person, a domain word, a domain rule | `docs/product/` |
   | How the program is built or behaves | `docs/architecture/` |
   | What happened, when, or why a decision was made | `docs/paperwork/` |
   | What is in or out of scope | `docs/project/project.md` |

2. Open that shelf's door, then the one page the door names for the subject. Stop there, unless that page points to a finer page that holds the answer ([../navigation/bounds.md](../navigation/bounds.md)).  
3. Open source only when the question is about behaviour and no architecture page answers it. In that case, say in the answer that the architecture paper is silent on it.

### Step 3 — Write the answer

1. Answer from what the pages say. Give the path of each page the answer relies on.  
2. Separate the three kinds of statement, and label each one:

   | Kind | Label |
   |------|-------|
   | Stated by a document, or by an explicit human statement | `certain` |
   | The agent's best reading, where a reasonable alternative exists | `probable` |
   | Not settled by any document | `uncertain`, with who could settle it |

3. If documents disagree on the answer, say so, and name [conflict-resolve.md](conflict-resolve.md). Do not pick a side silently.

### Step 4 — Check understanding

Before sending, confirm that each item holds for the answer as written. If one fails, rewrite the answer.

| # | The human can determine, from the answer alone | Holds? |
|---|------------------------------------------------|--------|
| 3.1 | What question the agent answered | |
| 3.2 | What, if anything, the agent needs from them | |
| 3.3 | What is known, what is assumed, and what is still open | |
| 3.4 | The confidence of every suggestion and assumption | |
| 3.5 | Enough to correct the answer without guessing the agent's intent | |

### Step 5 — Stop

1. Edit nothing, even when the answer shows that a document is wrong or missing.  
2. If a change is needed, end the answer with the change named and the flow that would make it ([amendment.md](amendment.md), a Step, [bugfix.md](bugfix.md)), with a confidence label.  
3. Await the human.

## Human message — answer

```markdown
**Question:** <one sentence>

<The answer, in plain words. Each claim either cites its page or carries a label.>

**Sources:** <paths>

**Still open:** <what no document settles, and who could settle it — or nothing>

**Change this suggests:** <change and route (`probable`) — or none>

**Need from you:** <a correction of the question | a decision | nothing — waiting for your instruction>
```

For a short factual question, the answer may be a few sentences. It still contains **Question**, the sources, and **Need from you**.

## Done

- The question was restated.  
- The answer rests on the owning page, which is cited.  
- Every assumption and suggestion carries a label.  
- The Step 4 checklist holds.  
- No file was edited.

## Not done

- The agent answered by silently restructuring or editing documents.  
- The answer mixes document facts and the agent's reading without labels.  
- The agent read the whole shelf instead of the owning page.
