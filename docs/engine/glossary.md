<!--
  File: docs/engine/glossary.md
  Purpose: Engine terminology
  Audience: Agents and humans
  Update when: New engine terms are introduced
-->

# Engine glossary

Terms for the Pool-System Framework. Product domain terms: [../product/glossary.md](../product/glossary.md).

**Implementation note (through F-015):** Engine core is a clean host (G-002). Product module `com.aethelgard:product` (`ProductHost`) owns the category tree (ADR-009), generation tick, and tectonics System. See [architecture.md](architecture.md) and [../product/architecture.md](../product/architecture.md).

| Term | Definition |
|------|------------|
| **Pool** | Shared, public state of the simulated world. Engine object with an `update()` method that delegates to `PoolCompute`, invoked at the start of each Step's computation. Holds a typed field map (`Object` values) for System merge. Samples Input View once per update. |
| **PoolCompute** | Pluggable strategy for what the Pool does each update. Default: `SkeletonPoolCompute`. Wired via `EngineSetup`. |
| **EventEmissionPolicy** | Pluggable strategy for which events fire during Pool compute. Default: `ScriptedEventEmissionPolicy` (config path list). Wired via `EngineSetup`. |
| **EngineSetup** | Run wiring: category tree, claimers, Systems, field schema, user layer, `PoolCompute`, `EventEmissionPolicy`, diagnostics. |
| **Step** | One full transaction: stage Input View, Pool computes, emits events, claiming Systems react, outputs merge back into the Pool, event buffer clears, User View reads settled state. |
| **Event** | Notification written to a shared buffer during computation, carrying a category from the category tree. |
| **Category Tree** | Hierarchy of event categories. Each System (or stub claimer) is assigned one category at definition time. |
| **EventClaimer** | Claims by category ancestry. Stub claimers remain for F-003 tests; Systems expose a claimer identity for dispatch. |
| **System** | Independent computation unit (`EngineSystem`), triggered when an event's category equals or descends from the System's assigned category. Built from Sub-Systems wired by a System Config. |
| **System Config** | Rules that select and wire a System's Sub-Systems — comparable to a grammar assembling words into a sentence. |
| **Sub-System** | Modular, reusable computation block with declared write-ranges and its own IO against Pool snapshot / System staging. |
| **Out_spec** | Output a single Sub-System produces in one internal execution step (staging writes). |
| **OUT_SYS** | A System's aggregate output field map after its Sub-Systems run (`Map<String, Object>`). |
| **ProvenancedWrite** | `(systemId, Object value)` entering the Step output buffer for typed merge. |
| **FieldMergeType** | Pluggable merge rule for one field (`merge(standing, writers)`). Defaults: `FieldType` enum (Static, Increment, Constant, Destructive). Custom types implement the interface. |
| **Typed merge** | System→Pool resolution by each field's `FieldMergeType`. Static/Destructive defaults pick lex-min `systemId`. Increment default requires `Long`. |
| **EngineDiagnostics** | Observability port (Step/event/claim/unmatched). Default bridges to SLF4J; tests may record. |
| **User View** | Independent port that reads the settled Pool at end of Step (`UserView.onSettled`). Read-only. |
| **User Input** | Typed register of named actions (`press` / `release` / `consume`). Persistence lives in `InputKind`. |
| **Input View** | Staged snapshot of User Input the Pool reads during computation. All inputs count as simultaneous at compute time. |
| **Claim / Finish Counters** | Synchronization barrier: count of Systems that claimed an event must match count reporting finished before merge proceeds. Observable via `ClaimFinishSnapshot` / `Engine.lastClaimFinish()`. *(Systems finish synchronously today.)* |

Field merge types: [specs/merge-types.md](specs/merge-types.md). Host ports: [architecture.md](architecture.md).
