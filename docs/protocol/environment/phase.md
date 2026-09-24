<!--
  File: docs/protocol/environment/phase.md
  Purpose: Structural freedom by phase, and docs depth rules
  Audience: Agents
  Update when: The phase or the depth rule changes
-->

# Phase

**Current phase:** alpha

Phase is the amount of structural freedom the repository allows. It is not a Goal. Do not write the Active Goal into this page.

| Phase | Structure | How work proceeds |
|-------|-----------|-------------------|
| **alpha** | Create, move, and rename documents and folders. Log the change | Behaviour Steps are witnessed once the code exists. Documentation Steps use the existing suite to show behaviour did not change |
| **beta** | Layout locked. Structural change needs a decision record | Goal and Step per the flows |
| **prod** | Locked; migration note with the change | Versioned releases, changelog, continuous check |

## Depth

| Path shape | Allowed |
|------------|---------|
| `docs/<page>.md` | Yes |
| `docs/<folder>/<page>.md` | Yes |
| `docs/<folder>/<folder>/<page>.md` | Yes (three levels under `docs/`) |
| `docs/architecture/<area>/<chapter>/<page>.md` | Yes — on the architecture shelf, a chapter may use a fourth level |
| `docs/protocol/blueprints/<group>/<page>.md` | Yes — blueprints are grouped by the shelf or kind of file they shape, at a fourth level |
| A fourth level anywhere else | No |

## Landmark folders

A landmark folder an agent is expected to enter has a `README.md` door. Exempt: build output, tool caches, wrapper internals, and intermediate language-namespace segments.

## Alpha structural change

When a folder or major document is created, moved, or renamed:

1. The entrance map matches the disk, including the new door.
2. The changelog gains a Structure line.
3. If a reader could not have guessed the move, a decision record says why.
4. The depth rule still holds.
