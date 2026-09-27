<!--
  File: docs/architecture/engine/systems.md
  Purpose: Systems — how a claiming system runs its sub-systems and hands back one output
  Audience: Agents and humans
  Update when: How a system orders and runs its sub-systems, what a sub-system may read or write, or the barrier changes
-->

# Systems

A system is one independent worker of the step. It wakes only when an event it cares about arrived, reads the shared snapshot, runs a fixed list of smaller blocks in turn, and returns one map of proposed field values. Inside a system the blocks may build on each other; across systems nothing is shared until merge.

## What it reads

The snapshot $\sigma_k$ taken after compute and before merge; every system of the step receives that same snapshot. Its `SystemConfig`: an `id`, an `assignedCategory`, the ordered `subSystems`, and an optional `conflictResolver`. Each `SubSystem` declares its `writeRanges`, the field names it may write, and reads through its `SubSystemIo`: the snapshot, and the staging written by the sub-systems before it in the same system.

## What it writes

`OUT_SYS`, an immutable map from field name to value, returned when the system runs. The engine copies each entry into the `StepOutputBuffer` as a `ProvenancedWrite` of the system's id and the value. A system never writes the Pool. Refusals: a write through the io throws `IllegalArgumentException` for a field outside the declared range and `NullPointerException` for a null value. Running a system throws `IllegalStateException` when write ranges overlap and no resolver is configured, or when the resolver does not return each conflicting sub-system exactly once. The barrier's check throws `IllegalStateException` when the claim and finish counts differ.

## Model

A system is $\psi = (\mathrm{id}_\psi,\; \mathrm{cat}_\psi,\; (q_1, \dots, q_m),\; \rho_\psi)$, and each sub-system $q$ has a write range $\omega(q) \subseteq \Phi$. The conflict set is every sub-system that shares a written field with another:

$$Q^{\cap} = \{\, q_i \;:\; \exists\, j \neq i,\;\; \omega(q_i) \cap \omega(q_j) \neq \emptyset \,\}.$$

The execution order $\pi$ is the registration order when $Q^{\cap} = \emptyset$. Otherwise the resolver must return a permutation $\rho_\psi(Q^{\cap})$ of the conflict set, and that permutation replaces the conflict set as one block at the position of its first member, while every other sub-system keeps its registration position. Execution threads a staging map $Z$ through the ordered list:

$$Z_0 = \emptyset, \qquad Z_i = Z_{i-1} \oplus \delta_i, \quad \delta_i = q_{\pi(i)}\bigl(\sigma_k,\, Z_{i-1}\bigr), \quad \operatorname{dom}\delta_i \subseteq \omega\bigl(q_{\pi(i)}\bigr), \qquad \mathrm{Out}_\psi = Z_m ,$$

where $\oplus$ overwrites: the last write to a field inside a system is the one in $\mathrm{Out}_\psi$. Over the systems of the step, the barrier requires

$$\bigl|\{\, \psi : B_{\mathit{cl}(\psi)} \neq \emptyset \,\}\bigr| \;=\; \bigl|\{\, \psi \text{ that returned } \mathrm{Out}_\psi \,\}\bigr| .$$

## Procedure

1. The configuration rejects a null id or category and keeps an immutable copy of the sub-system list.
2. Building a system builds its claimer once, an `EventClaimer` with the system's id and category; the engine finds the system's claims through that object.
3. The sub-systems are grouped by the fields they declare; every field with more than one writer puts all its writers into the conflict set.
4. When the conflict set is empty, registration order is kept. Otherwise a resolver is required; it is asked to order the conflict set, its answer is checked to hold each member exactly once, and that order is spliced in at the first conflicting position.
5. Running the system gives each sub-system, in that order, a `SubSystemIo` over the snapshot, the staging so far, and its own write range, and executes it.
6. Through the io, a sub-system reads the snapshot (a read of an unset field throws), reads it as a number, reads the heartbeat, reads earlier staging (absent when nothing was staged), and writes, each write checked against the declared range.
7. The run returns an immutable copy of the staging map: that is `OUT_SYS`.
8. Around each claiming system the engine counts a claim before the run and a finish after it. Before merge it checks that the counts balance, and it keeps the counts and the finish order as a `ClaimFinishSnapshot`.

## What is true afterwards

`OUT_SYS` holds the last staged value of every field the system wrote, and every key lies in the union of its sub-systems' write ranges. Overlapping sub-systems ran in the resolver's order; the others ran in registration order. The Pool is unchanged until [merge](merge.md). The barrier counts systems, not sub-systems. The loop is synchronous: a finish is counted after every run that returns, and a run that throws ends the step before the barrier is checked, so in this loop the check cannot fail. It guards a loop that would run systems concurrently, and what such a loop does with a system that never finishes is not decided ([open question 1](../open-questions.md)).

The conflict set reaches the resolver in an order that follows the iteration order of the sub-systems' declared sets, which the engine does not fix. A resolver that returns the same order for any input order keeps the run deterministic ([determinism](determinism.md)).

## Cost

Ordering is $O\bigl(\sum_q |\omega(q)| + m\bigr)$ plus the resolver. A run costs the sum of its sub-systems.

Code: [engine/systems/](../../../engine/src/main/java/com/aethelgard/engine/systems/README.md)  
Parent: [one engine step](README.md). How the outputs of several systems meet: [merge](merge.md).
