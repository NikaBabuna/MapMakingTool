<!--
  File: engine/src/main/java/com/aethelgard/engine/events/README.md
  Purpose: Door to the events: the category tree, the event buffer of a step, and claiming events by category ancestry
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Events

Carries the events of a step: the tree of categories they belong to, the buffer they are written to, and the claiming that offers each event to every claimer whose category is the event's category or one of its ancestors.

**Paper:** [Events](../../../../../../../../docs/architecture/engine/events.md) · [Determinism](../../../../../../../../docs/architecture/engine/determinism.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

Events are how a step decides which systems run, so the rules for naming, buffering, and claiming them live together and depend on nothing else in the engine. Deciding which events fire is the compute's emission policy in `pool/`; what a claiming system then does is `systems/`.

## How it works

1. A product builds its `CategoryTree` of `Category` paths; the engine resolves the scripted paths when it is created.
2. During compute, each emission wraps a category in an `EngineEvent` and appends it to the step's `EventBuffer`.
3. `EventClaiming.claim` walks the events in buffer order and the claimers in their fixed order. An `EventClaimer` claims an event when `claims` finds the event's category to be its own or a descendant (`Category.isSelfOrDescendantOf`). The claimed pairs and the unmatched events become a `ClaimResult`.
4. After merge, the engine clears the buffer, so no event survives its step.

**Start reading at:** `EventClaiming.claim` in [EventClaiming.java](EventClaiming.java).

## Depends on

- nothing in this repository

## Used by

- [pool/](../pool/README.md) — the engine resolves the tree, owns the buffer, and calls the claiming
- [systems/](../systems/README.md) — each system builds its claimer
- [diagnostics/](../diagnostics/README.md) — reports name the event and the claimer
- [product world/](../../../../../../../../product/src/main/java/com/aethelgard/product/world/README.md) — the product's category tree
- [the events tests](../../../../../../test/java/com/aethelgard/engine/events/README.md) — claiming by ancestry, unmatched events, and an empty buffer after each step

## Where each step happens

### [Events](../../../../../../../../docs/architecture/engine/events.md)

| Step | Member | File |
|------|--------|------|
| 1. The product builds the tree, and paths are ensured in it | `CategoryTree.ensure` | [CategoryTree.java](CategoryTree.java) |
| 2. The engine resolves the scripted paths when it is created | `CategoryTree.resolveAll` | [CategoryTree.java](CategoryTree.java) |
| 4. Each emission becomes an event appended to the buffer | `EventBuffer.add`, `EngineEvent` | [EventBuffer.java](EventBuffer.java), [EngineEvent.java](EngineEvent.java) |
| 5. The claimers are gathered and offered every event | `EventClaiming.claim` | [EventClaiming.java](EventClaiming.java) |
| 6. A claimer claims an event of its category or a descendant | `EventClaimer.claims`, `Category.isSelfOrDescendantOf` | [EventClaimer.java](EventClaimer.java), [Category.java](Category.java) |
| 7. The claims and the unmatched events become an immutable result | `ClaimResult` | [ClaimResult.java](ClaimResult.java) |
| 8. After merge, the buffer is emptied | `EventBuffer.clear` | [EventBuffer.java](EventBuffer.java) |

### [Determinism](../../../../../../../../docs/architecture/engine/determinism.md)

| Step | Member | File |
|------|--------|------|
| 4. Claiming walks events and claimers in fixed orders | `EventClaiming.claim` | [EventClaiming.java](EventClaiming.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [Category.java](Category.java) | One node of the category tree | `Category`, `path`, `parent`, `isSelfOrDescendantOf` |
| [CategoryTree.java](CategoryTree.java) | The hierarchy of categories | `CategoryTree`, `of`, `ensure`, `resolveAll` |
| [EngineEvent.java](EngineEvent.java) | One event, of one category | `EngineEvent` |
| [EventBuffer.java](EventBuffer.java) | The events of the step in progress | `EventBuffer`, `add`, `clear` |
| [EventClaimer.java](EventClaimer.java) | Claims the events of its category and its descendants | `EventClaimer`, `claims` |
| [EventClaiming.java](EventClaiming.java) | Offers every event to every claimer | `EventClaiming`, `claim` |
| [ClaimResult.java](ClaimResult.java) | The claimed pairs and unmatched events of one step | `ClaimResult` |
