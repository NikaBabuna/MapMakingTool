<!--
  File: docs/protocol/environment/dispatch.md
  Purpose: The only map from a situation to a flow
  Audience: Agents and humans
  Update when: A flow is added or a situation changes
-->

# Dispatch

An agent writes only by a flow in this table. Navigation gathers the facts. This page chooses the flow.

| Situation | Flow |
|-----------|------|
| A chat opens | [judgment.md](../flows/judgment.md) → **Reconcile** |
| Reconcile finds an in-progress Step, or a Step record with requirements and no Accept | [judgment.md](../flows/judgment.md) → **Rollback** |
| No active Goal, and the human has not approved one | Stop. Propose. No writes |
| The human approves a Goal | [goals.md](../flows/goals.md) → **Open goal** |
| The human approves a change to a stored Goal | [goals.md](../flows/goals.md) → **Amend goal** |
| The Goal’s last Step is Accepted and its claims hold | [goals.md](../flows/goals.md) → **Close goal** |
| The next Step is being discussed | Stop. Propose. No writes |
| The human approves the Step’s job and requirements | [steps.md](../flows/steps.md) → **Store step** |
| A stored Step is being implemented | [steps.md](../flows/steps.md) → **Implement** |
| The witness is green | [steps.md](../flows/steps.md) → **Sync**, then **Close step** |
| The human approves a change to stored requirements | [steps.md](../flows/steps.md) → **Amend step** |
| The witness stays red and the human stops the Step | [judgment.md](../flows/judgment.md) → **Rollback** |
| A folder is created, moved, or renamed | [structure.md](../flows/structure.md) → **Restructure** |
| Scope changes | [structure.md](../flows/structure.md) → **Scope** |
| A technical or structural decision is made | [structure.md](../flows/structure.md) → **Decide** |
| Source files change | [source.md](../flows/source.md) → **Record source** |
