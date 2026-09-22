<!--
  File: docs/architecture/host/events.md
  Purpose: Category tree, ancestry claiming, unmatched events, no same-step refill
  Audience: Agents and humans
  Update when: EventClaiming or the emission port changes
-->

# Events

An event is a notification in a shared buffer. It is not addressed to a system. It carries a category from the category tree. The application defines the tree. The engine defines how claiming works.

## What it reads

The buffer filled during pool compute, and the list of claimers: stub `EventClaimer`s plus one claimer per `EngineSystem`. Each claimer has one assigned category, fixed when the system is built.

## What it writes

`ClaimResult`: for each claimer, the events it claimed, and the list of unmatched events. Diagnostics receives each claimed pair and each unmatched event. The buffer itself is not modified by claiming.

## Procedure

`EventClaiming.claim` walks events in buffer order. For each event it walks claimers in registration order. `EventClaimer.claims` is true when the event's category equals the assigned category or descends from it at any depth. One event may be claimed by more than one claimer. An event claimed by none is unmatched.

Unmatched events are logged through `EngineDiagnostics.unmatchedEvent`. They are not dropped silently. The reason is [ADR-006](../../paperwork/decisions/ADR-006-unmatched-events.md).

The buffer is filled only during pool compute. After merge, `runStep` clears it. Nothing in the system phase appends a new event. A consequence that should be noticed becomes eligible when the next step's compute emits again, by reading the settled fields. Same-step recursive triggering does not occur.

`EventEmissionPolicy.emitEvents` is the port that decides which categories fire during compute. The default, `ScriptedEventEmissionPolicy`, emits the paths on the config. A product replaces that policy through `EngineSetup` without editing the engine. The product authors its tree in Java with `CategoryTree.of`. The engine does not ship a world tree. The reason is [ADR-009](../../paperwork/decisions/ADR-009-category-tree.md).

## What is true afterwards

Every event has been offered to every claimer. Systems with an empty claim list do not run. The buffer still holds the events until merge finishes and the step clears it. The next step throws if the buffer is not empty at the start.

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| Dispatch | `EventClaiming` | `engine/.../event/EventClaiming.java` |
| Claimer | `EventClaimer` | `engine/.../event/EventClaimer.java` |
| Buffer | `EventBuffer` | `engine/.../event/EventBuffer.java` |
| Tree | `CategoryTree`, `Category` | `engine/.../event/` |
| Emission port | `EventEmissionPolicy` | `engine/.../pool/EventEmissionPolicy.java` |

Parent: [one engine step](README.md). Who runs after a claim: [systems](systems.md).
