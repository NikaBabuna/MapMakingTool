<!--
  File: docs/protocol/environment/phase.md
  Purpose: How freely structure may change, and how deep the docs tree may be
  Audience: Agents and humans
  Update when: The phase or the depth rule changes
-->

# Phase

**Current phase:** alpha

Phase is the amount of structural freedom the repository allows. It is not a Goal, and it is not a status banner. Do not write the Active Goal into this page.

| Phase | Structure | How work proceeds |
|-------|-----------|-------------------|
| **alpha** | Create, move, and rename documents and folders. Log the change | A Step that changes behavior is witnessed once the code exists. A documentation Step uses the existing suite to show behavior did not change |
| **beta** | The layout is locked. A structural change needs a decision record | Goal and Step, per the flows |
| **prod** | Locked, and a migration note accompanies the change | Versioned releases, a changelog, and a continuous check |

## Depth

A reader should reach a page in a few steps. The cap is what keeps the tree from becoming a maze.

| Path shape | Allowed? | Example |
|------------|----------|---------|
| `docs/<page>.md` | Yes | `docs/navigation.md` |
| `docs/<folder>/<page>.md` | Yes | `docs/protocol/README.md` |
| `docs/<folder>/<folder>/<page>.md` | Yes. This is three levels under `docs/` | `docs/protocol/flows/steps.md` |
| `docs/architecture/<area>/<chapter>/<page>.md` | Yes. The architecture shelf alone may use a fourth level, so a paper can have chapters | `docs/architecture/world/crust/subduct.md` |
| A fourth level anywhere else | No | `docs/protocol/flows/steps/store.md` is too deep. Put the extra detail in the page, or split a sibling page at the same depth |

## Landmark folders

A landmark folder is one an agent is expected to enter. It has a `README.md` that says what lives there and links the children. The entrance, [../../navigation.md](../../navigation.md), lists the landmarks and links those doors.

Exempt, because a door there would be noise: build output, local tool caches, wrapper internals, and the intermediate segments of a language namespace (`src`, `main`, and the package segments that are only a path).

## What an alpha structural change must leave behind

When a folder or a major document is created, moved, or renamed:

1. The entrance map matches the disk, including the new door.
2. The changelog gains a Structure line: date, what moved, why it matters.
3. If a reader could not have guessed the move, a decision record says why.
4. The depth rule above still holds.

Beta and later add the decision record even when the move seems obvious. That gate is not active in alpha.
