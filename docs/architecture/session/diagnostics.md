<!--
  File: docs/architecture/session/diagnostics.md
  Purpose: Session diagnostics — measuring a run without touching the world
  Audience: Agents and humans
  Update when: What the diagnostics record or keep, how phases are timed, or the collector ids change
-->

# Session diagnostics

A run measures itself: how long each step took, how much memory is in use, how long painting took, and how long each timed phase of the generation took. Each measure keeps only its last few dozen samples, can be switched off or cleared, and never changes the world.

## What it reads

Samples: a collector id and a `long` value. The session records step time and memory; the timed phases record their durations; the studio's controller records paint time ([controller](../studio/controller.md)).

## What it writes

The samples, into fixed-size rings, and text reports of them. No world field. Refusals: registering an id twice, and reading, switching, or clearing an unknown id, throw `IllegalArgumentException`; a ring of capacity below 1 throws `IllegalArgumentException`.

## Model

The hub is an ordered map from ids to collectors, in registration order. The default hub registers ten rings of capacity $\mathit{cap} = 64$:

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

A timed phase measures its own run and hands the duration to the hub bound to the current thread, if any:

$$\mathrm{record}\bigl(\text{phase id},\; t_{\mathrm{end}} - t_{\mathrm{start}}\bigr) \quad \text{(also when the phase throws).}$$

## Procedure

1. The default hub registers the ten collectors of the table, each a `RingDiagnosticCollector` of capacity 64.
2. A sample is forwarded to the collector with its id when that id is registered, and dropped otherwise. The hub also registers, lists, reads, switches, and clears collectors, one or all. Every hub operation holds the hub's monitor.
3. A ring writes at its cursor, advances the cursor modulo the capacity, and grows its size up to the capacity. Clearing resets size and cursor. The ring's latest sample, its mean, its samples (oldest first), its size, its capacity, and whether it is enabled can be read, and it can be switched on or off. Every ring operation except reading its id and capacity holds the ring's monitor.
4. `DiagnosticCollector` is the port a collector implements, so a product can register one of its own.
5. The full report prints one line per collector, `<id> enabled=<bool> n=<m>/<cap> last=<v|-> mean=<v|->`; the short list prints `<id> enabled=<bool> n=<m>/<cap>`.
6. A hub is bound to the current thread for the duration of one action, and the previous binding is restored afterwards. A timed phase records into the bound hub, and records nothing when none is bound.
7. A `TimingSubSystem` wraps a phase: it reports the phase's own id and write range, so the engine sees the phase unchanged, and it measures the elapsed time of each run and records it, even when the run throws.

## What is true afterwards

A world is the same with every collector enabled, disabled, or cleared: no sample reaches a field. Each ring holds at most 64 samples, so memory is bounded. A phase timed outside a session's advance, for example in the [reference pipeline](../world/reference.md), records nothing, because no hub is bound. The engine's own report port is a different mechanism: [engine diagnostics](../engine/diagnostics.md).

## Cost

A record is $O(1)$; a mean or a copy of samples is $O(\mathit{cap})$.

Code: [session/diagnostics/](../../../product/src/main/java/com/aethelgard/product/session/diagnostics/README.md)  
Parent: [session](README.md). Which phases are timed: [wiring](../world/wiring.md).
