<!--
  File: docs/architecture/engine/determinism.md
  Purpose: Why the same configuration, wiring, and inputs settle the same fields — the conditions and the argument
  Audience: Agents and humans
  Update when: Engine.runStep, EngineSystem.orderSubSystems, TypedMerge.merge, or a FieldType rule changes
-->

# Determinism

Running the same starting configuration with the same wiring and the same inputs for the same number of steps settles the same fields, every time. This page states the conditions under which that holds and the argument that it does.

## What it reads

The whole run: the `EngineConfig`, the `EngineSetup`, and the presses and releases a caller makes between steps.

## What it writes

Nothing. Determinism is a property of the step, not a mechanism of its own.

## Model

With the wiring $\mathcal{E}$ fixed, one step is a map $\mathrm{step}_{\mathcal{E}} : (S_{k-1}, \mathcal{I}) \mapsto (S_k, \mathcal{I}')$ on the Pool state and the input register. The claim is:

$$\text{(D1)} \wedge \text{(D2)} \wedge \text{(D3)} \;\Longrightarrow\; \forall k \ge 0:\;\; S_k \text{ is the same in every run},$$

under three conditions:

- **(D1)** The compute $\Gamma$, the emission policy $\epsilon$, every sub-system, and every product merge rule are functions of their arguments: they read no clock, no randomness, and no state outside those arguments that reaches a field.
- **(D2)** Every conflict resolver returns an order that depends only on the set of conflicting sub-systems, not on the order of the list it receives: $\rho(\pi(Q)) = \rho(Q)$ for every permutation $\pi$.
- **(D3)** Every run makes the same presses and releases between the same steps.

The argument is an induction on $k$. $S_{-1}$ is a function of the configuration. Assume $S_{k-1}$ is fixed. Then each stage of step $k$ is a function of fixed inputs:

$$I_k = \mathrm{stage}(\mathcal{I}),\quad (h, F', B_k) = \Gamma(\dots),\quad (B_{\mathit{cl}})_{\mathit{cl}} = \mathrm{claim}(B_k, \mathit{claimers}),\quad \mathrm{Out}_\psi = \mathrm{run}_\psi(\sigma_k),\quad F_k = \bigl(\tau_f(F'(f), \mathrm{Wr}_f)\bigr)_f .$$

The claimer list and the system list $\Sigma$ are fixed at creation. $B_k$ is in emission order. Each $\mathrm{Wr}_f$ lists writes in system order. $\mathrm{STATIC}$ and $\mathrm{DESTRUCTIVE}$ depend only on the ids and values of the writes, $\mathrm{INCREMENT}$ on their multiset, and $\mathrm{CONSTANT}$ on the standing value. So $S_k$ is fixed.

## Procedure

1. Step 0 starts from $S_{-1}$, which is built from the configuration and the schema alone. [`Engine.create`](../../../engine/src/main/java/com/aethelgard/engine/pool/Engine.java).
2. The input view is a set computed from the register, so by (D3) it is the same in every run. [`UserInput.stage`](../../../engine/src/main/java/com/aethelgard/engine/user/UserInput.java).
3. The compute and the emission policy are functions by (D1), so the heartbeat, the directly set fields, and the event list are the same. [`PoolCompute.compute`](../../../engine/src/main/java/com/aethelgard/engine/pool/PoolCompute.java).
4. Claiming walks the events in buffer order and the claimers in the fixed creation order, so every claim list is the same. [`EventClaiming.claim`](../../../engine/src/main/java/com/aethelgard/engine/event/EventClaiming.java).
5. Every system reads the one snapshot $\sigma_k$. It orders its sub-systems by registration and by the resolver, which is the same for any input order by (D2), and each sub-system is a function by (D1), so each `OUT_SYS` is the same. [`EngineSystem.orderSubSystems`](../../../engine/src/main/java/com/aethelgard/engine/system/EngineSystem.java).
6. Merge applies to each field a rule that reads only the standing value and the writes, as the Model states, so every field value is the same. [`TypedMerge.merge`](../../../engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java).

## What is true afterwards

Under (D1)–(D3), the fields after every step are equal across runs. These things may still differ between runs without affecting any field:

- The iteration order of the immutable copies the engine makes (each `OUT_SYS`, the claim map, the output buffer's map), and so the order of `eventClaimed` reports.
- The diagnostics binding and the view port, which only observe.
- The order in which the engine hands the conflict set to a resolver. That order follows the unordered write-range sets of the sub-systems, which is why (D2) is a condition.

Within a system, order can matter, because sub-systems read each other's staging. That is why overlapping sub-systems get one order from the resolver for the whole conflict set ([systems](systems.md)). Across systems, order cannot leak into a field except through a merge rule, and the built-in rules read none ([merge](merge.md)). Across steps, the buffer is filled once, during compute, and cleared after merge, so a chain of consequences is a sequence of settled snapshots and never a same-step cascade ([events](events.md)).

The guarantee covers only rules that exist. A delete rule has not been decided; one that let a concurrent write win or lose by arrival order would break it ([open question 4](../open-questions.md)).

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Step order | `Engine` | — | [`engine/src/main/java/com/aethelgard/engine/pool/Engine.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/Engine.java) |
| Sub-system order | `EngineSystem` | — | [`engine/src/main/java/com/aethelgard/engine/system/EngineSystem.java`](../../../engine/src/main/java/com/aethelgard/engine/system/EngineSystem.java) |
| Merge rules | `TypedMerge`, `FieldType` | — | [`engine/src/main/java/com/aethelgard/engine/merge/`](../../../engine/src/main/java/com/aethelgard/engine/merge/) |

The members are described on the pages linked above; this page owns none.

Parent: [one engine step](README.md).
