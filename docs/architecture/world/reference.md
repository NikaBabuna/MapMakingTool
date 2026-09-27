<!--
  File: docs/architecture/world/reference.md
  Purpose: Reference pipeline — the whole generation as one function call, outside the engine
  Audience: Agents and humans
  Update when: What the reference pipeline computes changes, or the phase order of the wiring changes
-->

# Reference pipeline

The same generation can be computed without an engine: one function takes a world and returns the next one by calling the phase procedures directly, in the pipeline's order. It exists so a check can compare the engine's result with an independent composition of the same steps.

## What it reads

A `ProductGeneration.Snapshot` of six values, the plates, velocities, registry, occupancy, lockers, and elevation, and a generation index $g \ge 1$. The short `Snapshot` constructor takes the plates, velocities, registry, and elevation, and adds the step-0 occupancy and an all-oceanic locker table.

## What it writes

A new `Snapshot` after one generation. A null snapshot or part throws `NullPointerException`; $g < 1$ throws `IllegalArgumentException`. Contacts, budgets, and intents are computed inside the call and not returned.

## Model

With $\mathcal{S} = (P, v, R, O, T, E)$ and the same phase functions as the [pipeline](README.md),

$$\mathrm{advance}(\mathcal{S}, g) = \mathcal{S}' \quad\text{with}\quad \mathcal{S}' = \bigl(\mathcal{W}_g.P,\; \mathcal{W}_g.v,\; \mathcal{W}_g.R,\; \mathcal{W}_g.O,\; \mathcal{W}_g.T,\; \mathcal{W}_g.E\bigr)$$

where $\mathcal{W}_g$ is the generation of the level page applied to a world whose settled fields are $\mathcal{S}$. The call differs from the engine run in three ways that do not change its result on a consistent world: it recounts the registry from $P$ and $v$ instead of reading the settled one, it calls each procedure directly instead of reading staged fields, and it records no phase timing.

## Procedure

1. The registry is counted from the given plates and velocities.
2. The contacts, budgets, and intents are computed from the given world with its crust, then the velocities are integrated.
3. The geometry pass runs with the integrated velocities and a fresh skip mask, and the orogeny stamps are computed on the given occupancy and lockers.
4. The plates and crust keys are advected, with the subduction corrections applied from the given contacts.
5. Ridge mint, margin relief, continental collision, the registry of the moved plates, and isostasy follow, and the six results form the new snapshot.

## What is true afterwards

When the snapshot's registry is the one counted from its plates and velocities, $R = \mathrm{from}(P, v)$, which every settled world satisfies, each procedure receives the same arguments as in the engine run. So the call on a snapshot and $g$ computes the same six values as generation $g$ of an engine whose settled fields at step $g-1$ are the snapshot. This class is part of the main source, and nothing in the running program calls it.

Code: [world/](../../../product/src/main/java/com/aethelgard/product/world/README.md)  
Parent: [one generation](README.md).
