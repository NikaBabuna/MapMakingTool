<!--
  File: docs/engine/glossary.md
  Purpose: Engine terminology
  Audience: Agents and humans
  Update when: New engine terms are introduced
-->

# Engine glossary

Terms for the Pool-System Framework. Product domain terms: [../product/glossary.md](../product/glossary.md).

**Implementation note (through F-003):** Pool, Steps, events, category ancestry, stub claimers (`EventClaimer`), and diagnostics exist in code. Systems, Sub-Systems, merge, claim/finish, and User View/Input remain **spec targets** until their Steps. See [architecture.md](architecture.md).

| Term | Definition |
|------|------------|
| **Pool** | Shared, public state of the simulated world. Engine object with an `update()` method (and other lifecycle methods as needed), invoked at the start of each Step's computation. |
| **Step** | One full transaction: Pool computes, emits events, claiming Systems react, outputs merge back into the Pool, event buffer clears. *(Today: update → claim stubs → clear; merge/View later.)* |
| **Event** | Notification the Pool writes to a shared buffer during computation, carrying a category from the category tree. |
| **Category Tree** | Hierarchy of event categories. Each System (or stub claimer) is assigned one category at definition time. |
| **EventClaimer** | F-003 stub that claims by category ancestry only. Precursor to full **System**. |
| **System** | Independent computation unit, triggered when an event's category equals or descends from the System's assigned category. Built from Sub-Systems wired by a System Config. *(Not implemented yet — F-004.)* |
| **System Config** | Rules that select and wire a System's Sub-Systems — comparable to a grammar assembling words into a sentence. |
| **Sub-System** | Modular, reusable computation block with its own input and output buffers. Independent of any particular System. |
| **Out_spec** | Output a single Sub-System produces in one internal execution step. |
| **OUT_SYS** | A System's aggregate output, built by merging its Sub-Systems' Out_spec values. |
| **EngineDiagnostics** | Observability port (Step/event/claim/unmatched). Default bridges to SLF4J; tests may record. |
| **User View** | Independent system that reads the settled Pool at end of Step and produces the frame the user sees. Read-only. *(F-006.)* |
| **User Input** | Typed register of currently or previously pressed inputs, gathered during a Step. *(F-006.)* |
| **Input View** | Staged snapshot of User Input the Pool reads during computation. All inputs count as simultaneous at compute time. *(F-006.)* |
| **Claim / Finish Counters** | Synchronization barrier: count of Systems that claimed an event must match count reporting finished before merge proceeds. *(F-005.)* |

Field merge types: [specs/merge-types.md](specs/merge-types.md).
