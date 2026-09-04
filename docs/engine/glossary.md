<!--
  File: docs/engine/glossary.md
  Purpose: Engine terminology
  Audience: Agents and humans
  Update when: New engine terms are introduced
-->

# Engine glossary

Terms for the Pool-System Framework. Product domain terms: [../product/glossary.md](../product/glossary.md).

| Term | Definition |
|------|------------|
| **Pool** | Shared, public state of the simulated world. Engine object with an `update()` method (and other lifecycle methods as needed), invoked at the start of each Step's computation. |
| **Step** | One full transaction: Pool computes, emits events, claiming Systems react, outputs merge back into the Pool, event buffer clears. |
| **Event** | Notification the Pool writes to a shared buffer during computation, carrying a category from the category tree. |
| **Category Tree** | Hierarchy of event categories. Each System is assigned one category at definition time. |
| **System** | Independent computation unit, triggered when an event's category equals or descends from the System's assigned category. Built from Sub-Systems wired by a System Config. |
| **System Config** | Rules that select and wire a System's Sub-Systems — comparable to a grammar assembling words into a sentence. |
| **Sub-System** | Modular, reusable computation block with its own input and output buffers. Independent of any particular System. |
| **Out_spec** | Output a single Sub-System produces in one internal execution step. |
| **OUT_SYS** | A System's aggregate output, built by merging its Sub-Systems' Out_spec values. |
| **User View** | Independent system that reads the settled Pool at end of Step and produces the frame the user sees. Read-only. |
| **User Input** | Typed register of currently or previously pressed inputs, gathered during a Step. |
| **Input View** | Staged snapshot of User Input the Pool reads during computation. All inputs count as simultaneous at compute time. |
| **Claim / Finish Counters** | Synchronization barrier: count of Systems that claimed an event must match count reporting finished before merge proceeds. |

Field merge types: [specs/merge-types.md](specs/merge-types.md).
