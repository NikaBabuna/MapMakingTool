<!--
  File: docs/architecture/engine/pool.md
  Purpose: Pool — the shared state and its once-per-step update
  Audience: Agents and humans
  Update when: What the Pool holds, how its update runs, what a compute may do, or what a snapshot is, changes
-->

# Pool

The Pool is the one shared state of a run: a heartbeat number and a map of named fields. Once per step, before any system runs, a pluggable compute may change the heartbeat, set fields directly, and fire events; system proposals reach the fields only later, through merge.

## What it reads

The `EngineConfig` (at construction), the `FieldSchema`, the wired `PoolCompute`, the `CategoryTree`, the `EventEmissionPolicy`, and, on every update, the event buffer, the resolved scripted emissions, the diagnostics, and the staged `InputView`.

## What it writes

The heartbeat, the update count, and the field map. A compute writes the heartbeat and fields through the `PoolComputeContext`, and events into the buffer. After merge, the whole field map is replaced with the merge result. Errors: setting an undeclared field, emitting an unknown path, reading an absent field from a snapshot, and reading as a number a value that is not a `Long` each throw `IllegalArgumentException`.

## Model

The Pool is the tuple $(h, u, F, I)$: heartbeat $h \in \mathbb{Z}_{64}$ (a signed 64-bit integer), update count $u \in \mathbb{N}$, field map $F : \Phi \to \mathcal{V}$ over the declared names $\Phi$, and the last input view $I$. At step $k$ the update is

$$u \leftarrow u + 1 = k + 1, \qquad (h, F, B_k) \leftarrow \Gamma\bigl(h,\; F,\; I_k,\; \mathcal{T},\; \mathcal{P},\; \epsilon\bigr),$$

where $\Gamma$ is the wired `PoolCompute`, $\mathcal{T}$ the category tree, $\mathcal{P}$ the scripted paths, and $B_k$ the list of events it emits. The default $\Gamma$ is the skeleton:

$$h' = h + 1 + 100 \cdot [\,\texttt{nudge} \in I_k\,], \qquad B_k = \epsilon(\ldots), \qquad F' = F .$$

After merge, $F \leftarrow F_k$, the merged map. A snapshot is the value $(h, u, F)$ with $F$ copied, so later writes to the Pool do not reach it.

## Procedure

1. Construction sets $h = h_0$ and $u = 0$, and puts every declared field into the map with its seed, or with the value `0L` when it has none. A seed for an undeclared field throws. A null schema becomes the empty schema.
2. The update keeps the staged view (an empty view when null), adds 1 to the update count, and calls the compute with a fresh `PoolComputeContext`.
3. The context is the compute's whole API for one update. It reads the heartbeat, the update count, the input view, the schema, one field (absent, or 0 when read as a number), all fields, the category tree, the scripted emissions, the emission policy, and the diagnostics. It writes the heartbeat, and a declared field only. It emits an event of a category, of a path, or of every scripted path, and it can hand itself to the wired emission policy.
4. The default compute adds 1 to the heartbeat, adds 100 more when the action `nudge` is active, and then applies the emission policy. A custom compute may skip the policy and emit by itself.
5. After merge, the field map is cleared and every merged entry is put.
6. A snapshot is a `PoolSnapshot` of $(h, u, F)$, whose constructor copies the map. Reading an absent field throws, reading as a number also throws when the value is not a `Long`, and reading with a zero default returns 0 for an absent name.

## What is true afterwards

After the update of step $k$: $u = k + 1$; the heartbeat has the value the compute left; the fields hold their standing values, except any the compute set directly; and every event the compute emitted is in the buffer, in emission order. After apply, every declared field holds a value, and a snapshot's update count equals the number of completed steps.

## Cost

An update is the cost of the compute. The context's field reads copy the field map on each call, $O(|\Phi|)$ per read. A snapshot copies the map once, $O(|\Phi|)$; the field values themselves are shared, not copied.

Code: [engine/pool/](../../../engine/src/main/java/com/aethelgard/engine/pool/README.md)  
Parent: [one engine step](README.md). Which events fire: [events](events.md). How system writes reach the fields: [merge](merge.md).
