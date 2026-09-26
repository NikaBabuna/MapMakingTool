<!--
  File: docs/architecture/engine/pool.md
  Purpose: Pool, PoolCompute, PoolComputeContext, SkeletonPoolCompute, PoolSnapshot — the shared state and its once-per-step update
  Audience: Agents and humans
  Update when: Pool.update, Pool.applyFields, the PoolComputeContext API, SkeletonPoolCompute.compute, or PoolSnapshot changes
-->

# Pool

The Pool is the one shared state of a run: a heartbeat number and a map of named fields. Once per step, before any system runs, a pluggable compute may change the heartbeat, set fields directly, and fire events; system proposals reach the fields only later, through merge.

## What it reads

The `EngineConfig` (at construction), the `FieldSchema`, the wired `PoolCompute`, the `CategoryTree`, the `EventEmissionPolicy`, and, on every update, the event buffer, the resolved scripted emissions, the diagnostics, and the staged `InputView`.

## What it writes

The heartbeat `value`, the update count, and the field map. A compute writes the heartbeat and fields through the `PoolComputeContext`, and events into the buffer. `Pool.applyFields` replaces the whole field map with the merge result. Errors: `setField` of an undeclared field, `emitPath` of an unknown path, `PoolSnapshot.field` of an absent field, and `fieldLong` or `fieldOrZero` of a value that is not a `Long` each throw `IllegalArgumentException`.

## Model

The Pool is the tuple $(h, u, F, I)$: heartbeat $h \in \mathbb{Z}_{64}$ (a signed 64-bit integer), update count $u \in \mathbb{N}$, field map $F : \Phi \to \mathcal{V}$ over the declared names $\Phi$, and the last input view $I$. At step $k$ the update is

$$u \leftarrow u + 1 = k + 1, \qquad (h, F, B_k) \leftarrow \Gamma\bigl(h,\; F,\; I_k,\; \mathcal{T},\; \mathcal{P},\; \epsilon\bigr),$$

where $\Gamma$ is the wired `PoolCompute`, $\mathcal{T}$ the category tree, $\mathcal{P}$ the scripted paths, and $B_k$ the list of events it emits. The default $\Gamma$ is the skeleton:

$$h' = h + 1 + 100 \cdot [\,\texttt{nudge} \in I_k\,], \qquad B_k = \epsilon(\ldots), \qquad F' = F .$$

`SkeletonPoolCompute.compute` in [`SkeletonPoolCompute.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/SkeletonPoolCompute.java):

```java
public void compute(PoolComputeContext context) {
  context.setValue(context.value() + 1);
  if (context.inputView().isActive(NUDGE_ACTION)) {
    context.setValue(context.value() + 100);
  }
  context.applyEmissionPolicy();
}
```

After merge, $F \leftarrow F_k$, the merged map. A snapshot is the value $(h, u, F)$ with $F$ copied, so later writes to the Pool do not reach it.

## Procedure

1. Construction sets $h = h_0$ and $u = 0$, and puts every declared field into the map with its seed, or with the value `0L` when it has none. A seed for an undeclared field throws. A null schema becomes the empty schema. [`Pool`](../../../engine/src/main/java/com/aethelgard/engine/pool/Pool.java).
2. `Pool.update` keeps the staged view (an empty view when null), adds 1 to the update count, and calls the compute with a fresh `PoolComputeContext`. [`Pool.update`](../../../engine/src/main/java/com/aethelgard/engine/pool/Pool.java).
3. The context is the compute's whole API for one update. Reads: `value`, `updateCount`, `inputView`, `fieldSchema`, `field` (null when unset), `fieldOrZero` (0 when unset), `fields`, `categoryTree`, `scriptedEmissions`, `emissionPolicy`, `diagnostics`. Writes: `setValue`, and `setField` for a declared field only. Events: `emit`, `emitPath`, `emitScripted`, and `applyEmissionPolicy`, which hands the context to the wired policy. [`PoolComputeContext`](../../../engine/src/main/java/com/aethelgard/engine/pool/PoolComputeContext.java).
4. The default compute adds 1 to the heartbeat, adds 100 more when the action `nudge` is active, and then applies the emission policy. A custom compute may skip the policy and emit by itself. [`SkeletonPoolCompute.compute`](../../../engine/src/main/java/com/aethelgard/engine/pool/SkeletonPoolCompute.java).
5. After merge, `Pool.applyFields` clears the field map and puts every merged entry. [`Pool.applyFields`](../../../engine/src/main/java/com/aethelgard/engine/pool/Pool.java).
6. `Pool.snapshot` returns a `PoolSnapshot` of $(h, u, F)$, whose constructor copies the map. `field` throws when the name is absent, `fieldLong` also throws when the value is not a `Long`, and `fieldOrZero` returns 0 for an absent name. [`PoolSnapshot`](../../../engine/src/main/java/com/aethelgard/engine/pool/PoolSnapshot.java).

## What is true afterwards

After the update of step $k$: $u = k + 1$; the heartbeat has the value the compute left; the fields hold their standing values, except any the compute set directly; and every event the compute emitted is in the buffer, in emission order. After apply, every declared field holds a value, and `PoolSnapshot.updateCount()` equals the number of completed steps.

## Cost

An update is the cost of the compute. `PoolComputeContext.field` and `fields` copy the field map on each call, $O(|\Phi|)$ per read. A snapshot copies the map once, $O(|\Phi|)$; the field values themselves are shared, not copied.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| State | `Pool` | `Pool`, `update`, `applyFields`, `fieldValues`, `snapshot`, `NUDGE_ACTION` | [`engine/src/main/java/com/aethelgard/engine/pool/Pool.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/Pool.java) |
| Update port | `PoolCompute` | `compute` | [`engine/src/main/java/com/aethelgard/engine/pool/PoolCompute.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/PoolCompute.java) |
| Compute API | `PoolComputeContext` | `inputView`, `categoryTree`, `emissionPolicy`, `applyEmissionPolicy`, `value`, `setValue`, `updateCount`, `fieldSchema`, `field`, `fieldOrZero`, `setField`, `scriptedEmissions`, `emit`, `emitPath`, `emitScripted`, `diagnostics`, `fields` | [`engine/src/main/java/com/aethelgard/engine/pool/PoolComputeContext.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/PoolComputeContext.java) |
| Default update | `SkeletonPoolCompute` | `INSTANCE`, `NUDGE_ACTION`, `SkeletonPoolCompute.compute` | [`engine/src/main/java/com/aethelgard/engine/pool/SkeletonPoolCompute.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/SkeletonPoolCompute.java) |
| Settled read | `PoolSnapshot` | `PoolSnapshot`, `value`, `updateCount`, `fields`, `PoolSnapshot.field`, `fieldLong`, `PoolSnapshot.fieldOrZero` | [`engine/src/main/java/com/aethelgard/engine/pool/PoolSnapshot.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/PoolSnapshot.java) |

Parent: [one engine step](README.md). Which events fire: [events](events.md). How system writes reach the fields: [merge](merge.md).
