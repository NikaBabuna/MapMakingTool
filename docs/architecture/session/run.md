<!--
  File: docs/architecture/session/run.md
  Purpose: Run — one run of a world: the engine it owns, the lock that serialises it, and what a caller can read
  Audience: Agents and humans
  Update when: How a session advances, what it lets a caller read, or its lock changes
-->

# Run

A session holds one world from its first step to its last. It is the only door to that world's engine: every step and every read passes through one lock, so a timer in the studio and a typed command can never step the same world at the same time, and nobody ever reads a half-finished step.

## What it reads

A `WorldSpec` at construction: the default session uses the $8 \times 8$ fixture and the view session the $1920 \times 1080$ window, both with seed 0.

## What it writes

Steps of its engine, and, after each step, three samples into its [diagnostics hub](diagnostics.md): the step's wall time, the heap in use, and the heap limit. It writes no world field itself. A null spec, field name, or system id throws `NullPointerException`; an advance of $n < 0$ steps, a field name that is not settled, and an unknown system id throw `IllegalArgumentException`.

## Model

A session is $(\mathit{spec}, \mathit{engine}, \mathbb{L}, \mathit{hub})$: a spec, an engine created from the spec by the [seed](../world/seed.md) (so its step index starts at 0), a monitor lock $\mathbb{L}$, and a hub. Every operation except reading the spec and the hub runs inside $\mathbb{L}$, so the history of a session is a sequence of whole operations:

$$\texttt{advance}(n) \;=\; \mathbb{L}.\mathrm{acquire}\;;\;\; \bigl(\mathrm{step}\;;\; \mathrm{sample}\bigr)^{n}\;;\;\; \mathbb{L}.\mathrm{release},$$

and a read between two advances sees the settled world of a whole step. For one step,

$$\mathrm{advance.wall} = t_{\mathrm{after}} - t_{\mathrm{before}}, \qquad \mathrm{heap.used} = M_{\mathrm{total}} - M_{\mathrm{free}}, \qquad \mathrm{heap.max} = M_{\max},$$

in nanoseconds and bytes, where the phase timings of the step reach the same hub because the step runs with the hub bound to its thread ([diagnostics](diagnostics.md)).

## Procedure

1. Construction keeps the spec and creates the engine from it, which runs step 0, and builds a hub with the default collectors.
2. An advance of one step is an advance of $n = 1$. An advance of $n$ steps refuses $n < 0$, then, holding the lock, runs $n$ single steps, each timed and bound to the hub, and records the three samples after each.
3. The world reads take the lock and return the settled value: the elevation, the plates, the velocities, the registry, the contacts, the budgets, the intents, or any field by its name; and the step index.
4. The settled world is also returned as its canonical text, under the lock ([dump](dump.md)).
5. The construction reads describe the wiring: the declared field names; each field's merge-type label, which is the constant name of a built-in `FieldType` or the simple class name of a product rule; the systems in registration order; and one system's detail, printed as `id=<id> category=<path>` and, on a second line, `subsystems=<ids, comma-separated>`.
6. The spec and the hub are returned without the lock.

## What is true afterwards

One session is one world, and its step index only grows. A second advance waits until the first has released the lock, and a read waits for a whole advance of $n$ steps, not only for its current step. The values returned are the immutable settled fields, so a caller can keep them while the world moves on. The field names and the merge-type labels follow the iteration order of the schema's immutable map, which is unspecified; the systems and their subsystems follow registration order.

## Cost

Each read copies the engine's field map once, $O(|\Phi|)$. An advance costs its steps plus three hub records per step.

Code: [session/](../../../product/src/main/java/com/aethelgard/product/session/README.md)  
Parent: [session](README.md). The step it advances: [../engine/README.md](../engine/README.md).
