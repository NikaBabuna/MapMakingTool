<!--
  File: docs/architecture/host/README.md
  Purpose: Level 2 — one engine step, in the order the code runs it
  Audience: Agents and humans
  Update when: Engine.runStep order changes
-->

# One engine step

`Engine.runStep` in `engine/src/main/java/com/aethelgard/engine/pool/Engine.java` is one transaction. Systems that do not claim an event do not run. Systems that do run see the same Pool snapshot, taken after compute and before merge. They do not read each other's output in this step.

The order below is the order in `runStep`. Each stage names the page that owns that mechanism.

1. `EngineDiagnostics.stepStarted` records the upcoming step index. The index is one more than the last completed step. [Diagnostics](diagnostics.md).
2. The event buffer must be empty. A leftover event throws.
3. `UserInput.stage` freezes an `InputView`. [User](user.md).
4. `Pool.update` runs the wired `PoolCompute`, which may emit into the event buffer through `EventEmissionPolicy`. [Pool](pool.md).
5. Persistent actions that were present in that view are consumed.
6. `EventClaiming.claim` matches every event to every claimer by category ancestry. Claimed events and unmatched events are reported to diagnostics. [Events](events.md).
7. For each `EngineSystem` that claimed at least one event: the claim/finish barrier records a claim, `EngineSystem.run` executes its sub-systems against the shared snapshot, and each field write enters the step output buffer as a `ProvenancedWrite` carrying that system's id. The barrier then records a finish. [Systems](systems.md).
8. The barrier requires `claimCount == finishCount`. An imbalance throws. Merge does not start.
9. `TypedMerge.merge` resolves the output buffer against the standing fields. Each field uses its `FieldMergeType`. `Pool.applyFields` stores the result. [Merge](merge.md).
10. The event buffer is cleared. Claim result, output buffer, barrier snapshot, and the completed step index are stored.
11. `UserView.onSettled` receives a snapshot taken after the apply. [User](user.md).
12. `EngineDiagnostics.stepSettled` records the same index.

`Engine.create` completes step 0 inside the constructor path, so the first completed index is 0. Each `advance` runs one more `runStep`.

What makes the same inputs settle the same way: [determinism](determinism.md). Words: [glossary](glossary.md). Gaps: [open questions](open-questions.md).

The world that plugs into this loop: [../world/README.md](../world/README.md).
