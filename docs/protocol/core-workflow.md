<!--
  File: docs/protocol/core-workflow.md
  Purpose: Global default of operation — startup sequence and flow catalogue
  Audience: Agents
  Update when: The startup order or the set of named flows changes
-->

# Core workflow

## Status

This instrument is the global default of operation. It is the highest level of conduct. It binds every agent.

It states what shall be done when a condition obtains. It names the flow to invoke. It does not restate the full procedure of that flow. Each named flow is prescribed in full under [flows/](flows/), except where this instrument assigns a shape to [blueprints/](blueprints/).

Definitions of Goal, Step, Torn, Accept, and related objects are prescribed in [environment/core-definition.md](environment/core-definition.md). This instrument assumes those definitions.

## Article 1 — Startup sequence

Upon entering a chat, or whenever the agent must (re)establish protocol before work, the agent shall execute the following in order. No other flow shall run first.

| Order | Action | Instrument |
|------:|--------|------------|
| 1 | Read the protocol, beginning at [brief.md](brief.md) | Exactly the **Startup read set** of [navigation/bounds.md](navigation/bounds.md). Further pages are opened only as the selected flow requires |
| 2 | Run **Reconcile** | [flows/](flows/) — Reconcile |
| 3 | Deliver **Status report** | [blueprints/messages/status-report.md](blueprints/messages/status-report.md) |

### 1.1 After Reconcile

| Condition | Then |
|-----------|------|
| Reconcile finds nothing torn | Proceed to Status report, then **await** human instruction |
| Reconcile finds a torn Step (a Step mark, or a record with requirements that is not closed) | Run **Rollback** immediately. Notify the human. Then **await** human instruction. Do not continue the torn work |
| Reconcile finds only unsealed changes, with no Step mark | Run **Rollback**, which asks the human before discarding anything. If the human keeps the changes, deliver Status report stating that they were kept, then **await** human instruction |

Status report shall include the result of Reconcile, including whether Rollback ran and whether unsealed changes were kept.

## Article 2 — Await and classify

While awaiting instruction, the agent shall not invent a write sequence. When the human speaks, or when the agent must request a lawful action, the agent shall classify the need and invoke exactly one primary flow from Article 3.

If no row fits, the agent shall stop and ask. Silence is not approval. Inventing a thirteenth flow is prohibited.

## Article 3 — Flow catalogue

| Flow | When it is invoked | What it is for |
|------|--------------------|----------------|
| **Reconcile** | Startup (Article 1), and whenever the agent must verify that no Step is torn before new work | Detect torn work |
| **Rollback** | Any torn Step or unsealed change; or the human ends a Step whose witness will not pass | Return to the safe point; notify; await |
| **Status report** | Immediately after Reconcile in the startup sequence | State what the project is and the current state of affairs, in the blueprint shape |
| **Investigate** | Agent or human needs an assumption tested before reliance | Test an assumption; report the outcome |
| **Explain** | A question about structure, meaning, or layout must be answered so the structure is understandable | Take a question; answer it under [environment/style.md](environment/style.md) |
| **Audit / review** | A direction is given for an honest review against hard criteria | Take a direction; review against stated criteria; no silent edit |
| **Goal flow** | A durable result across sessions is to be negotiated, opened, amended, closed, or abandoned; or an idea is to be captured on the backlog | Goal lifecycle per [environment/core-definition.md](environment/core-definition.md) and the Goal flow file |
| **Step flow** | One job is to be agreed, stored, implemented, witnessed, and closed | Step lifecycle per [environment/core-definition.md](environment/core-definition.md) and the Step flow file |
| **Bugfix** | A defect is already confirmed true; the work is to correct it | Fix under the Bugfix flow file; not for unconfirmed hunches |
| **Amendment** | Core documents must change without chaos | Change stored protocol, entrance, or standing docs by the Amendment flow so doors, indexes, and dependents remain true |
| **Global docsync** | Mandatory at the close of every Goal; also when a Step or the human invokes a whole-prior sync | Bring documents and source into a single prior across the tree the flow names |
| **Conflict resolve** | Two sources of authority disagree | Apply the conflict rule; edit the losing source on purpose when the human so approves |

## Article 4 — Precedence

| Conflict of intent | Precedence |
|--------------------|------------|
| Torn work vs any new Goal or Step work | **Rollback** first |
| Unconfirmed defect vs Bugfix | **Investigate** (or Audit) until confirmation; then Bugfix |
| Assumption vs build | **Investigate** before Step implement |
| Question about structure vs edit of structure | **Explain** (or Audit) before **Amendment**, including its Restructure procedure |
| Local Step sync vs Goal end | Step flow’s sync as that flow requires; **Global docsync** mandatory at Goal close |

## Article 5 — Lower instruments

| Need | Where it lives |
|------|----------------|
| Exact meaning of Goal, Step, Torn, Accept, Seal, safe point | [environment/core-definition.md](environment/core-definition.md) |
| Which files a named flow edits, in what order, and when it is done | [flows/](flows/) |
| The exact shape of each document, and the exact edit each operation makes | [blueprints/](blueprints/README.md) |
| Shape of the Status report | [blueprints/messages/status-report.md](blueprints/messages/status-report.md) |
| Whether source or document correctness applies | [environment/correctness.md](environment/correctness.md) |
| Whether organisation and sync duty hold | [environment/quality.md](environment/quality.md) |
| How to speak while classifying or reporting | [environment/style.md](environment/style.md) |
| What to open for a turn, how to read only the needed part, and when to stop | [navigation/](navigation/README.md) |

## Article 6 — Default prohibition

The agent shall not:

1. skip Article 1;
2. continue a torn Step;
3. run Bugfix on an unconfirmed defect;
4. treat Explain, Investigate, or Audit as licence to edit unless a write flow is separately invoked and approved;
5. invent a write sequence not named in Article 3 or not detailed under [flows/](flows/).

## Exclusion

This instrument does not define product features, witness commands, or the text of particular Goals. It defines the global if-then of operation.
