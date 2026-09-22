<!--
  File: docs/protocol/environment/authority.md
  Purpose: What binds, and which source wins
  Audience: Agents and humans
  Update when: A conflict rule changes
-->

# Authority

| Conflict | Winner |
|----------|--------|
| Chat and a stored document | The document |
| Chat and a stored Step record | The Step record. Change it only after the human approves |
| Two write sequences | [dispatch.md](dispatch.md). No other sequence is allowed |
| Several copies of the Active Goal sentence | The goal index only. Doors point at the index |
| A wish to work outside the scope document | The scope document. Expand scope before the work |

The scope document is the one named by the scope blueprint. Protocol does not restate the product.

A Step’s requirements are stored in its Step record before any implementation of that Step. Chat is not the store.
