<!--
  File: docs/protocol/environment/phase.md
  Purpose: How freely structure may change
  Audience: Agents and humans
  Update when: The phase or the depth rule changes
-->

# Phase

**Current phase:** alpha

| Phase | Structure | Work |
|-------|-----------|------|
| alpha | Create, move, and rename, and log the change | A Step witnesses once code exists |
| beta | Structural changes need a decision record | Goal and Step, per the flows |
| prod | Structural changes need a decision record and a migration note | Versioned releases |

## Depth

Pages under `docs/` are at most three levels deep.

The architecture shelf may use four levels: `docs/architecture/<area>/<chapter>/<page>`.

No other shelf may use a fourth level.

## Landmarks

A landmark folder has a `README.md`. The entrance lists the landmarks. Build output, local tool caches, wrapper internals, and intermediate namespace segments are exempt.

## Alpha log

A structural create, move, or rename updates the entrance, appends a changelog line, and appends a decision record when the move is not obvious.
