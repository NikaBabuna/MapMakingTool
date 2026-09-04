<!--
  File: docs/engine/glossary.md
  Purpose: Engine terminology
  Audience: Agents and humans
  Update when: New engine terms are introduced
-->

# Engine glossary

Terms for the Pool-System Framework. Product domain terms: [../product/glossary.md](../product/glossary.md).

**Implementation note (through F-004):** Pool, Steps, events, category ancestry, stub claimers, Systems/Sub-Systems, typed merge (Static/Increment/Constant/Destructive), and diagnostics exist in code. Claim/finish barrier and User View/Input remain **spec targets** until F-005–F-006. See [architecture.md](architecture.md).

| Term | Definition |
|------|------------|
| **Pool** | Shared, public state of the simulated world. Engine object with an `update()` method (and other lifecycle methods as needed), invoked at the start of each Step's computation. Holds a typed field map for System merge. |
| **Step** | One full transaction: Pool computes, emits events, claiming Systems react, outputs merge back into the Pool, event buffer clears. *(Today: update → claim → Systems → merge → apply → clear.)* |
| **Event** | Notification the Pool writes to a shared buffer during computation, carrying a category from the category tree. |
| **Category Tree** | Hierarchy of event categories. Each System (or stub claimer) is assigned one category at definition time. |
| **EventClaimer** | Claims by category ancestry. Stub claimers remain for F-003 tests; Systems expose a claimer identity for dispatch. |
| **System** | Independent computation unit (`EngineSystem`), triggered when an event's category equals or descends from the System's assigned category. Built from Sub-Systems wired by a System Config. |
| **System Config** | Rules that select and wire a System's Sub-Systems — comparable to a grammar assembling words into a sentence. |
| **Sub-System** | Modular, reusable computation block with declared write-ranges and its own IO against Pool snapshot / System staging. |
| **Out_spec** | Output a single Sub-System produces in one internal execution step (staging writes). |
| **OUT_SYS** | A System's aggregate output field map after its Sub-Systems run. |
| **ProvenancedWrite** | `(systemId, value)` entering the Step output buffer for typed merge. |
| **Typed merge** | System→Pool resolution by field type (Static, Increment, Constant, Destructive). Static/Destructive pick-one uses lexicographically smallest `systemId`. |
| **EngineDiagnostics** | Observability port (Step/event/claim/unmatched). Default bridges to SLF4J; tests may record. |
| **User View** | Independent system that reads the settled Pool at end of Step and produces the frame the user sees. Read-only. *(F-006.)* |
| **User Input** | Typed register of currently or previously pressed inputs, gathered during a Step. *(F-006.)* |
| **Input View** | Staged snapshot of User Input the Pool reads during computation. All inputs count as simultaneous at compute time. *(F-006.)* |
| **Claim / Finish Counters** | Synchronization barrier: count of Systems that claimed an event must match count reporting finished before merge proceeds. *(F-005 — Systems currently finish synchronously.)* |

Field merge types: [specs/merge-types.md](specs/merge-types.md).
