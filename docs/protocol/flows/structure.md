<!--
  File: docs/protocol/flows/structure.md
  Purpose: Bookkeeping algorithms for structure, scope, and decisions
  Audience: Agents and humans
  Update when: A structure flow changes
-->

# Structure flows

## Restructure

When a folder is created, moved, or renamed:

1. Put a door on any landmark folder that lacks one.
2. Update the entrance map and the docs index.
3. Append a changelog Structure line.
4. Append a decision record when the move is not obvious.
5. Refuse a fourth docs level except under the architecture shelf, and only as [../environment/phase.md](../environment/phase.md) allows.

## Scope

When scope changes:

1. Edit the scope document first.
2. If the change is technical, run **Decide**.
3. Append a changelog line.

## Decide

When a technical or structural decision is made:

1. Append the next decision section to the decisions log, from the decision blueprint.
2. Point the affected paper or scope page at that id.
