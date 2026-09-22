<!--
  File: docs/protocol/blueprints/dispatch.md
  Purpose: Shape of the dispatch table
  Audience: Agents and humans
  Update when: The dispatch shape changes
-->

# Dispatch

This is `docs/protocol/environment/dispatch.md`. It is the only place that says “in this situation, run that flow.” A flow file describes the algorithm. This file is the switch. If a write sequence exists only inside a flow and this table does not mention it, agents will not run it, which is the point. If an agent writes by a sequence this table does not name, it is outside the protocol.

**Write or edit it when.** A flow is added, removed, or the observation that triggers it changes.

## What each column is for

| Column | Why it is there |
|--------|-----------------|
| Situation you can observe | Facts, not intentions. “The human approves” means they accepted a text they were shown. Silence is not a row |
| Flow you run | A link to the file and the algorithm’s name in bold |
| What you must not do instead | The shortcut that has already caused a false success. Naming it is part of the rule |

Every named algorithm in `docs/protocol/flows/` appears in the table. A situation with no legal write says “No flow” and says what to do instead (usually: propose, and write nothing).

## Skeleton

```markdown
| Situation you can observe | Flow you run | What you must not do instead |
|---------------------------|--------------|------------------------------|
| The human approves a Goal text | [goals.md](../flows/goals.md) → **Open goal** | Start the first Step before its own approval |
```

## Keep out

The steps of the algorithm. Those live in the flow. Two rows that fire on the same observation and edit different files. Split the observation until only one row matches.
