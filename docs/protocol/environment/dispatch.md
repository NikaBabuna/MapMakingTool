<!--
  File: docs/protocol/environment/dispatch.md
  Purpose: The only map from a situation to a flow, with how to recognize each situation
  Audience: Agents and humans
  Update when: A flow is added or a situation changes
-->

# Dispatch

This is the only map from a situation to a write sequence. Navigation gathers the facts. This page chooses the flow. An agent that edits files by any other sequence is outside the protocol.

Read the situation column as observations, not as moods. “The human approves” means they said the job and the requirements are accepted, in this chat, after seeing them. Silence is not approval. “The witness is green” means the suite command finished with success, not that the agent believes it would.

| Situation you can observe | Flow you run | What you must not do instead |
|---------------------------|--------------|------------------------------|
| A chat opens | [judgment.md](../flows/judgment.md) → **Reconcile** | Start editing because the previous chat “was almost done” |
| Reconcile finds a Step `in progress`, or a Step record that has requirements and was never Accepted | [judgment.md](../flows/judgment.md) → **Rollback** | Continue the half-finished Step and call it a success |
| There is no active Goal, and the human has not approved one | No flow. Propose a Goal. No writes | Create `G-0xx` because the roadmap seems to imply it |
| The human approves a Goal text | [goals.md](../flows/goals.md) → **Open goal** | Start the first Step in the same breath, before its own approval |
| The human approves new wording for a Goal that already exists | [goals.md](../flows/goals.md) → **Amend goal** | Quietly change the claims |
| The Goal’s last Step is Accepted and every claim box holds | [goals.md](../flows/goals.md) → **Close goal** | Leave the Goal `in progress` because a wishlist remains that was never a claim |
| The next Step is being discussed | No flow. Propose the job and the requirements. No writes | Create `F-0xx` “so we don’t lose the idea” |
| The human approves that job and those requirements | [steps.md](../flows/steps.md) → **Store step** | Edit source in the same turn before the file exists and the mark is set |
| A stored Step is `in progress` and the plan was accepted | [steps.md](../flows/steps.md) → **Implement** | Wander into files the job did not name |
| The witness command succeeded | [steps.md](../flows/steps.md) → **Sync**, then **Close step** | Commit first and sync after, or skip Sync because “only docs” |
| The human approves a change to requirements already stored | [steps.md](../flows/steps.md) → **Amend step** | Delete a requirement to make the suite pass |
| The witness stays red and the human says stop | [judgment.md](../flows/judgment.md) → **Rollback** | Leave the red tree marked `in progress` and end the chat |
| A folder is created, moved, or renamed | [structure.md](../flows/structure.md) → **Restructure** | Move the files and leave the entrance pointing at the old path |
| The scope of the product changes | [structure.md](../flows/structure.md) → **Scope** | Build the new area and update scope afterwards |
| A technical or structural decision is made | [structure.md](../flows/structure.md) → **Decide** | Leave the reason only in chat |
| Source files are added or changed | [source.md](../flows/source.md) → **Record source** | Let the implementation paper keep describing the previous code |

Several rows can fire in one Step, in the order the Step flows name. **Implement** includes **Record source** when source changes. **Close step** includes **Close goal** when that Step was the last one. You still do not skip a row because a later row feels more important.
