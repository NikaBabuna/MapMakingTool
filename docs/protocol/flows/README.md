<!--
  File: docs/protocol/flows/README.md
  Purpose: Door to the named flows of core-workflow
  Audience: Agents
  Update when: A flow file is added or removed
-->

# Flows

The complete algorithm for each situation named in Article 3 of [../core-workflow.md](../core-workflow.md). A flow says when to write, which file, and in what order. Every document it writes is shaped by a blueprint under [../blueprints/](../blueprints/README.md), and the page shape is [../blueprints/protocol/flow.md](../blueprints/protocol/flow.md).

**Why:** Flows have their own folder because each is a complete algorithm the agent runs when the core workflow selects it. One file per flow the core workflow names belongs here. The shape of each document a flow writes belongs to the blueprints, and standards belong to the environment.

| Page | Read it when |
|------|----------------|
| [reconcile.md](reconcile.md) | A chat starts, or new work needs proof that nothing is torn |
| [rollback.md](rollback.md) | Reconcile found torn work or unsealed changes, or the human ends a failing Step |
| [status-report.md](status-report.md) | Reconcile (or Rollback) finished and the human needs the startup briefing |
| [investigate.md](investigate.md) | An assumption must be tested before anyone relies on it |
| [explain.md](explain.md) | The human asks what something is, means, or why it is where it is |
| [audit.md](audit.md) | The human asks for an honest review against criteria |
| [goal.md](goal.md) | A Goal is negotiated, opened, amended, closed, or abandoned, or an idea is captured |
| [step.md](step.md) | One job is to be proposed, approved, stored, done, witnessed, and closed |
| [bugfix.md](bugfix.md) | A confirmed defect is to be corrected |
| [amendment.md](amendment.md) | A standing document, the protocol, the entrance, scope, or structure is to change |
| [global-docsync.md](global-docsync.md) | A Goal closes, or a whole-tree sync is directed |
| [conflict-resolve.md](conflict-resolve.md) | Two sources of authority disagree |
