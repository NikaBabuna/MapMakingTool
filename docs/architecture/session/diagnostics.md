<!--
  File: docs/architecture/session/diagnostics.md
  Purpose: DiagnosticsHub, DiagnosticCollector, RingDiagnosticCollector, DiagnosticIds, PhaseTiming, TimingSubSystem — measuring a run without touching the world
  Audience: Agents and humans
  Update when: DiagnosticsHub, RingDiagnosticCollector, PhaseTiming, TimingSubSystem, or the collector ids change
-->

# Session diagnostics

A run measures itself: how long each step took, how much memory is in use, how long painting took, and how long each timed phase of the generation took. Each measure keeps only its last few dozen samples, can be switched off or cleared, and never changes the world.

## What it reads

Samples: a collector id and a `long` value. The session records step time and memory; the timed phases record their durations; the studio's controller records paint time ([controller](../studio/controller.md)).

## What it writes

The samples, into fixed-size rings, and text reports of them. No world field. Refusals: registering an id twice, and `get`, `setEnabled`, or `clear` of an unknown id, throw `IllegalArgumentException`; a ring of capacity below 1 throws `IllegalArgumentException`.

## Model

The hub is an ordered map from ids to collectors, in registration order. `withDefaults` registers ten rings of capacity $\mathit{cap} = 64$:

| Id | Records | Unit |
|----|---------|------|
| `advance.wall` | One engine step, as the session times it | ns |
| `heap.used` | $M_{\mathrm{total}} - M_{\mathrm{free}}$ after a step | bytes |
| `heap.max` | $M_{\max}$ after a step | bytes |
| `paint.wall` | One paint of the map | ns |
| `phase.trace`, `phase.interaction`, `phase.integrate`, `phase.apply`, `phase.orogeny`, `phase.isostasy` | One run of that phase | ns |

A ring holds the last $m \le \mathit{cap}$ recorded samples $x_1, \dots, x_m$, oldest first. Recording while disabled does nothing; otherwise it overwrites the oldest sample once $m = \mathit{cap}$. Its reads are

$$\mathrm{latest} = x_m, \qquad \mathrm{mean} = \operatorname{trunc}\Bigl(\frac{1}{m}\sum_{i=1}^{m} x_i\Bigr),$$

both absent when $m = 0$, with $\operatorname{trunc}$ the rounding of `long` division toward zero.

`RingDiagnosticCollector.record` in [`RingDiagnosticCollector.java`](../../../product/src/main/java/com/aethelgard/product/RingDiagnosticCollector.java):

```java
if (!enabled) {
  return;
}
ring[next] = value;
next = (next + 1) % ring.length;
if (size < ring.length) {
  size++;
}
```

A timed phase measures its own run and hands the duration to the hub bound to the current thread, if any:

$$\mathrm{record}\bigl(\text{phase id},\; t_{\mathrm{end}} - t_{\mathrm{start}}\bigr) \quad \text{(also when the phase throws).}$$

## Procedure

1. `withDefaults` registers the ten collectors named by `DiagnosticIds`, each a `RingDiagnosticCollector` of `DEFAULT_CAPACITY` (64). [`DiagnosticsHub.withDefaults`](../../../product/src/main/java/com/aethelgard/product/DiagnosticsHub.java), [`DiagnosticIds`](../../../product/src/main/java/com/aethelgard/product/DiagnosticIds.java).
2. `record(id, value)` forwards to the collector when the id is registered, and does nothing otherwise. `register`, `ids`, `get`, `has`, `setEnabled`, `clear`, `clearAll`, and `collectors` manage the map. Every hub method holds the hub's monitor. [`DiagnosticsHub.record`](../../../product/src/main/java/com/aethelgard/product/DiagnosticsHub.java).
3. A ring writes at its cursor, advances the cursor modulo the capacity, and grows its size up to the capacity. `clear` resets size and cursor. `latest`, `mean`, `samples` (oldest first), `size`, `capacity`, `enabled`, and `setEnabled` read and switch it. Every ring method except `id` and `capacity` holds the ring's monitor. [`RingDiagnosticCollector.record`](../../../product/src/main/java/com/aethelgard/product/RingDiagnosticCollector.java).
4. `DiagnosticCollector` is the port a collector implements, so a product can add one with `register`. [`DiagnosticCollector`](../../../product/src/main/java/com/aethelgard/product/DiagnosticCollector.java).
5. `report` prints one `summaryLine` per collector, `<id> enabled=<bool> n=<m>/<cap> last=<v|-> mean=<v|->`. `listReport` prints `<id> enabled=<bool> n=<m>/<cap>`. [`DiagnosticsHub.report`](../../../product/src/main/java/com/aethelgard/product/DiagnosticsHub.java), [`RingDiagnosticCollector.summaryLine`](../../../product/src/main/java/com/aethelgard/product/RingDiagnosticCollector.java).
6. `PhaseTiming.withHub(hub, action)` binds the hub to the current thread for the duration of the action, and restores the previous binding afterwards. `PhaseTiming.record` records into the bound hub, and does nothing when none is bound. [`PhaseTiming.withHub`](../../../product/src/main/java/com/aethelgard/product/PhaseTiming.java).
7. `TimingSubSystem` wraps a phase: it reports the phase's own `id` and write range, so the engine sees the phase unchanged, and around `execute` it measures the elapsed time and calls `PhaseTiming.record` in a `finally` block. [`TimingSubSystem.execute`](../../../product/src/main/java/com/aethelgard/product/TimingSubSystem.java).

## What is true afterwards

A world is the same with every collector enabled, disabled, or cleared: no sample reaches a field. Each ring holds at most 64 samples, so memory is bounded. A phase timed outside a session's `advance`, for example in the [reference pipeline](../world/reference.md), records nothing, because no hub is bound. The engine's own report port is a different mechanism: [engine diagnostics](../engine/diagnostics.md).

## Cost

A record is $O(1)$; a mean or a copy of samples is $O(\mathit{cap})$.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Hub | `DiagnosticsHub` | `DEFAULT_CAPACITY`, `withDefaults`, `register`, `ids`, `get`, `has`, `DiagnosticsHub.record`, `DiagnosticsHub.setEnabled`, `DiagnosticsHub.clear`, `clearAll`, `report`, `listReport`, `collectors` | [`product/src/main/java/com/aethelgard/product/DiagnosticsHub.java`](../../../product/src/main/java/com/aethelgard/product/DiagnosticsHub.java) |
| Collector port | `DiagnosticCollector` | `DiagnosticCollector.id`, `DiagnosticCollector.enabled`, `DiagnosticCollector.setEnabled`, `DiagnosticCollector.capacity`, `DiagnosticCollector.size`, `DiagnosticCollector.record`, `DiagnosticCollector.clear`, `DiagnosticCollector.latest`, `DiagnosticCollector.mean`, `DiagnosticCollector.samples`, `DiagnosticCollector.summaryLine` | [`product/src/main/java/com/aethelgard/product/DiagnosticCollector.java`](../../../product/src/main/java/com/aethelgard/product/DiagnosticCollector.java) |
| Ring | `RingDiagnosticCollector` | `RingDiagnosticCollector`, `RingDiagnosticCollector.record`, `RingDiagnosticCollector.latest`, `RingDiagnosticCollector.mean`, `RingDiagnosticCollector.samples`, `RingDiagnosticCollector.summaryLine` | [`product/src/main/java/com/aethelgard/product/RingDiagnosticCollector.java`](../../../product/src/main/java/com/aethelgard/product/RingDiagnosticCollector.java) |
| Ids | `DiagnosticIds` | `ADVANCE_WALL`, `HEAP_USED`, `HEAP_MAX`, `PAINT_WALL`, `PHASE_TRACE`, `PHASE_INTERACTION`, `PHASE_INTEGRATE`, `PHASE_APPLY`, `PHASE_OROGENY`, `PHASE_ISOSTASY` | [`product/src/main/java/com/aethelgard/product/DiagnosticIds.java`](../../../product/src/main/java/com/aethelgard/product/DiagnosticIds.java) |
| Thread binding | `PhaseTiming` | `withHub`, `PhaseTiming.record` | [`product/src/main/java/com/aethelgard/product/PhaseTiming.java`](../../../product/src/main/java/com/aethelgard/product/PhaseTiming.java) |
| Phase timer | `TimingSubSystem` | `TimingSubSystem`, `TimingSubSystem.id`, `TimingSubSystem.writeRanges`, `TimingSubSystem.execute` | [`product/src/main/java/com/aethelgard/product/TimingSubSystem.java`](../../../product/src/main/java/com/aethelgard/product/TimingSubSystem.java) |

Parent: [session](README.md). Which phases are timed: [wiring](../world/wiring.md).
