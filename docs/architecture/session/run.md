<!--
  File: docs/architecture/session/run.md
  Purpose: ProductSession — one run of a world: the engine it owns, the lock that serialises it, and what a caller can read
  Audience: Agents and humans
  Update when: ProductSession.advance, a ProductSession read, or its lock changes
-->

# Run

A session holds one world from its first step to its last. It is the only door to that world's engine: every step and every read passes through one lock, so a timer in the studio and a typed command can never step the same world at the same time, and nobody ever reads a half-finished step.

## What it reads

A `WorldSpec` at construction: `ofDefault()` uses the $8 \times 8$ fixture and `view()` the $1920 \times 1080$ window, both with seed 0.

## What it writes

Steps of its engine, and, after each step, three samples into its [diagnostics hub](diagnostics.md): the step's wall time, the heap in use, and the heap limit. It writes no world field itself. A null spec, field name, or system id throws `NullPointerException`; `advance(n)` with $n < 0$, a field name that is not settled, and an unknown system id throw `IllegalArgumentException`.

## Model

A session is $(\mathit{spec}, \mathit{engine}, \mathbb{L}, \mathit{hub})$: a spec, an engine created by `ProductHost.create(spec)` (so its step index starts at 0), a monitor lock $\mathbb{L}$, and a hub. Every operation except `spec` and `diagnostics` runs inside $\mathbb{L}$, so the history of a session is a sequence of whole operations:

$$\texttt{advance}(n) \;=\; \mathbb{L}.\mathrm{acquire}\;;\;\; \bigl(\mathrm{step}\;;\; \mathrm{sample}\bigr)^{n}\;;\;\; \mathbb{L}.\mathrm{release},$$

and a read between two advances sees the settled world of a whole step. For one step,

$$\mathrm{advance.wall} = t_{\mathrm{after}} - t_{\mathrm{before}}, \qquad \mathrm{heap.used} = M_{\mathrm{total}} - M_{\mathrm{free}}, \qquad \mathrm{heap.max} = M_{\max},$$

in nanoseconds and bytes, where the phase timings of the step reach the same hub because the step runs inside `PhaseTiming.withHub`.

`ProductSession.advance` (one step) in [`ProductSession.java`](../../../product/src/main/java/com/aethelgard/product/ProductSession.java):

```java
long t0 = System.nanoTime();
PhaseTiming.withHub(
    diagnostics,
    () -> {
      engine.advance(1);
      return null;
    });
long dt = System.nanoTime() - t0;
diagnostics.record(DiagnosticIds.ADVANCE_WALL, dt);
Runtime rt = Runtime.getRuntime();
diagnostics.record(DiagnosticIds.HEAP_USED, rt.totalMemory() - rt.freeMemory());
diagnostics.record(DiagnosticIds.HEAP_MAX, rt.maxMemory());
```

## Procedure

1. The constructor keeps the spec and creates the engine with `ProductHost.create`, which runs step 0. It builds a hub with the default collectors. [`ProductSession`](../../../product/src/main/java/com/aethelgard/product/ProductSession.java).
2. `advance()` is `advance(1)`. `advance(n)` rejects $n < 0$, then, holding the lock, runs $n$ single steps, each timed and bound to the hub, and records the three samples after each. [`ProductSession.advance`](../../../product/src/main/java/com/aethelgard/product/ProductSession.java).
3. The world reads take the lock and return the settled value: `elevation`, `plates`, `plateVelocities`, `plateRegistry`, `boundaries`, `areaFlux`, `motionIntent`, and the generic `field(name)`. The step index is `stepIndex`. [`ProductSession.field`](../../../product/src/main/java/com/aethelgard/product/ProductSession.java).
4. `settledWorld` returns the canonical text of the settled world under the lock ([dump](dump.md)). [`ProductSession.settledWorld`](../../../product/src/main/java/com/aethelgard/product/ProductSession.java).
5. The construction reads describe the wiring. `fieldNames` lists the declared fields. `schemaTypes` maps each field to its merge-type label, the constant name of a built-in `FieldType` or the simple class name of a product rule, through `mergeTypeName`. `systemIds` lists the systems in registration order. `systemDetail(id)` prints `id=<id> category=<path>` and, on a second line, `subsystems=<ids, comma-separated>`. [`ProductSession.systemDetail`](../../../product/src/main/java/com/aethelgard/product/ProductSession.java).
6. `spec` and `diagnostics` return the spec and the hub without the lock. [`ProductSession.diagnostics`](../../../product/src/main/java/com/aethelgard/product/ProductSession.java).

## What is true afterwards

One session is one world, and its step index only grows. A second `advance` waits until the first has released the lock, and a read waits for a whole `advance(n)`, not only for its current step. The values returned are the immutable settled fields, so a caller can keep them while the world moves on. `fieldNames` and `schemaTypes` follow the iteration order of the schema's immutable map, which is unspecified; `systemIds` and `subsystems` follow registration order.

## Cost

Each read copies the engine's field map once, $O(|\Phi|)$. An advance costs its steps plus three hub records per step.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Session | `ProductSession` | `ProductSession`, `ofDefault`, `view`, `spec`, `diagnostics`, `stepIndex`, `advance`, `elevation`, `plates`, `plateVelocities`, `plateRegistry`, `boundaries`, `areaFlux`, `motionIntent`, `settledWorld`, `fieldNames`, `schemaTypes`, `systemIds`, `systemDetail`, `field`, `mergeTypeName` | [`product/src/main/java/com/aethelgard/product/ProductSession.java`](../../../product/src/main/java/com/aethelgard/product/ProductSession.java) |

Parent: [session](README.md). The step it advances: [../engine/README.md](../engine/README.md).
