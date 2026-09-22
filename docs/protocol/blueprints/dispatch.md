<!--
  File: docs/protocol/blueprints/dispatch.md
  Purpose: Shape of the dispatch table
  Audience: Agents and humans
  Update when: The dispatch shape changes
-->

# Dispatch

**Kind:** dispatch  
**Document:** `docs/protocol/environment/dispatch.md`

## Body

One table. Each row is a situation and exactly one flow (or “stop, no writes”). Every named algorithm in `docs/protocol/flows/` appears in the table.

## Must not

Contain the algorithm itself. Tell the agent to write by any path that is not a named flow.
