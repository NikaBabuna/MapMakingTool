<!--
  File: docs/protocol/flows/status-report.md
  Purpose: Human-facing startup briefing after Reconcile (and after Rollback when clean, or when the human kept unsealed changes)
  Audience: Agents
  Update when: Gather sources or report shape change
-->

# Status report

**Register:** understanding ([../environment/style.md](../environment/style.md)).  
**Writes:** none. Chat only.  
**Shape:** [../blueprints/messages/status-report.md](../blueprints/messages/status-report.md).  
**Depends on:** Reconcile handoff (and Rollback handoff when `rollback_ran` is true or `unsealed_kept` is true).

## When

1. Immediately after Reconcile Branch clean in [../core-workflow.md](../core-workflow.md) Article 1; or  
2. After Rollback Step 6, when `reconcile_after = clean` or the human chose **keep**.

## Before

1. Reconcile has finished in this startup (or post-Rollback) sequence.  
2. Handoff fields are present: at minimum `torn`, `active_goal_id`, `rollback_ran`, `unsealed_kept`, `safe_point`. Use `false` for `rollback_ran` and `unsealed_kept` if they are omitted after a clean Reconcile with no Rollback.  
3. If `torn` is true and `unsealed_kept` is not true, this flow does not run. The Rollback notice owns that case.

## Gather order (mandatory)

Read only. Do not edit. Stop after these sources.

| Order | Open | Take |
|------:|------|------|
| 1 | Handoff from Reconcile / Rollback | `torn`, `torn_reason`, `candidate`, `active_goal_id`, `active_goal_path`, `rollback_ran`, `unsealed_kept`, `unsealed_paths`, `safe_point` |
| 2 | `docs/product/concept.md` | First heading title, and the first short paragraph under it (or the one-line promise if that is clearer). Enough for a person to know what the product is — not the whole file |
| 3 | `docs/paperwork/goals.md` | Confirm Active Goal line matches `active_goal_id` |
| 4 | `active_goal_path` if not `none` | Goal **Status**, **Type** if present, Progress table (steps done / claims), next Step row still `not started` if any |
| 5 | `docs/paperwork/steps.md` | Confirm no row is `in progress` |

Do not open architecture, flows, or source for this report.

## Write (chat only)

Apply **Deliver** of [../blueprints/messages/status-report.md](../blueprints/messages/status-report.md): **one** message. Plain language. Short. No file paths unless the human needs them to act. Every suggestion or "what next" item carries a confidence label (`certain` / `probable` / `uncertain`).

Fill the blueprint sections in this order. The wording below is the meaning each section must carry. The skeleton in the blueprint gives the required headings.

| Section | What the human must understand |
|---------|--------------------------------|
| **Product** | What this project is building, in one or two short sentences from concept — not jargon |
| **Where we are** | Active Goal name and status, or that there is no active Goal. If a Goal is active: type (Polishing / Extension) when known, and progress in plain words (e.g. "3 of 5 steps done; claims still open") |
| **Open work** | Exactly one of: "No step is in progress"; which step was torn and that it was rolled back; or that unsealed changes were kept by the human's decision, with their count. Never leave this ambiguous |
| **Health** | Exactly one of: Reconcile clean; Rollback ran, then Reconcile clean; unsealed changes kept, so the tree is not at the safe point; or no safe point exists |
| **What you can do next** | At most three concrete options (continue a planned step, open a goal, ask a question). Each with a confidence label. If unsealed changes were kept, the first option is a Step that adopts them. If nothing is obvious: say you are waiting |
| **Need from you** | Exactly one of: a decision, a fact, an approval, or "nothing — waiting for your instruction" |

## Forbidden in the report

- Dumping registry tables or raw handoff field names  
- Starting implementation in the same message  
- Claiming Accept or "all good" without the Health line matching a clean Reconcile  
- Copying the Active Goal sentence onto any door or file  

## Done

- The human has one understandable status message.  
- No repository file was written.  
- The agent awaits instruction.

## Not done

- Report delivered before Reconcile handoff existed.  
- Report claimed clean while a Step is still `in progress` on the registry, or while unsealed changes were kept.  
- Report buried the product or the open-work fact under protocol jargon.
