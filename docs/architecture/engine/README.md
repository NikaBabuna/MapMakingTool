<!--
  File: docs/architecture/engine/README.md
  Purpose: Level 2 — one engine step, in the order the engine runs it
  Audience: Agents and humans
  Update when: The stages of a step or their order change
-->

# One engine step

The engine is a host for any step-based simulation. It knows nothing of worlds, plates, or maps: it holds a Pool of named fields, lets pluggable systems propose new values, and merges those proposals by a rule chosen per field. One step is one run of the stages below by the `Engine` ([engine/pool/](../../../engine/src/main/java/com/aethelgard/engine/pool/README.md)). One rule holds across the whole level: every system that runs in a step reads the same snapshot of the Pool, taken after compute and before merge, and no system reads another system's output in that step.

**Why:** A step is its own level because every mechanism of the engine runs inside it, in one fixed order, around one snapshot. What belongs here is that order and the rule of the shared snapshot; how each stage computes is on its own page, and what a simulation does with the engine is the [world](../world/README.md).

$$S_k \;=\; \mathrm{View} \circ \mathrm{Clear} \circ \mathrm{Merge} \circ \mathrm{Barrier} \circ \mathrm{Run} \circ \mathrm{Claim} \circ \mathrm{Consume} \circ \mathrm{Compute} \circ \mathrm{Stage}\,\bigl(S_{k-1},\, \mathcal{I}\bigr)$$

Here $S_k = (h_k, u_k, F_k)$ is the settled Pool after step $k$ (heartbeat, update count, field map), and $\mathcal{I}$ is the user-input register between steps. The symbols are defined in the [glossary](../glossary.md#symbols).

| Field | Value | What it holds |
|-------|-------|---------------|
| The last completed index | `int`, starts at −1 | $k$ of the last settled step |
| The event buffer | `EventBuffer` | The events of the step in progress. Empty between steps |
| The last step's input view, claim result, output, and claim/finish counts | records | What the last settled step saw, claimed, wrote, and balanced |

1. The start of step $k$, one more than the last completed index, is reported to the diagnostics. [Diagnostics](diagnostics.md).
2. The event buffer must be empty. A leftover event throws `IllegalStateException`. [Events](events.md).
3. **Stage.** The input register freezes the input view $I_k$. [User](user.md).
4. **Compute.** The Pool counts the update and runs the wired `PoolCompute`, which may change the heartbeat and emit events through the `EventEmissionPolicy`. [Pool](pool.md), [Events](events.md).
5. **Consume.** Every persistent action that was in $I_k$ is released. [User](user.md).
6. **Claim.** Every event is offered to every claimer, and each claim and each unmatched event goes to diagnostics. [Events](events.md).
7. **Run.** Each `EngineSystem` that claimed at least one event runs its sub-systems against the shared snapshot $\sigma_k$. Its field writes enter the `StepOutputBuffer` as `ProvenancedWrite`s carrying its id. [Systems](systems.md).
8. **Barrier.** The `ClaimFinishBarrier` checks that every claiming system finished. An imbalance throws, and merge does not start. [Systems](systems.md).
9. **Merge.** The writes are resolved field by field with each field's `FieldMergeType`, and the Pool stores the result. [Merge](merge.md).
10. **Clear.** The event buffer is cleared, and the step's claim result, output buffer, barrier snapshot, and index $k$ are kept. [Setup and lifecycle](setup.md).
11. **View.** The `UserView` port is shown the settled snapshot $S_k$. [User](user.md).
12. The settle of step $k$ is reported to the diagnostics. [Diagnostics](diagnostics.md).

Step 0 runs when the engine is created, and each advance runs one more step, so after create and $n$ advances the index is $n$. If any stage throws, the step does not settle: the index does not move, and events left in the buffer make the next step throw at stage 2, so a failed engine is replaced rather than repaired.

How an engine is built, and the lifecycle around this step: [setup and lifecycle](setup.md). Why equal inputs settle to equal fields: [determinism](determinism.md). The world that plugs into this loop: [../world/README.md](../world/README.md). The coarser level: [../program.md](../program.md). Words and symbols: [../glossary.md](../glossary.md). Undecided behaviour: [../open-questions.md](../open-questions.md).
