<!--
  File: docs/architecture/world/motion/advect.md
  Purpose: Advect — carrying every cell and its crust key one step along its plate's velocity, filling plate gaps, and reversing plates that crossed a pole; and the registry staged after it
  Audience: Agents and humans
  Update when: What advection computes, or the order of its steps, changes
-->

# Advect

Every cell moves one step in the direction its plate drifts, and carries its crust with it. Where two plates arrive at the same cell, the lower-numbered plate keeps it; where no plate arrives, the gap is filled from the plates around it, but no crust comes with the fill, so the crust chapter must make new crust there. A plate any of whose cells went over a pole turns around.

## What it reads

The dense plates $P'$ and velocities $v'$ of the geometry pass ([remap](fission.md)), the settled occupancy $O$, the skip mask $\mathrm{Skip}$, the generation index $g$, the contacts re-traced on $P'$, and, for the corrections, the contacts $K$ of phase 1, the lockers, and the registry.

## What it writes

An `AdvectResult`: the moved plates, the velocities after pole reversal, and the moved occupancy, which may hold $\bot$ = −1. The geometry phase then stages the plates, the registry recounted from the moved plates, the velocities, and the occupancy. Refusals: $g < 1$ and an occupancy of another size throw `IllegalArgumentException`; a plate id outside the velocity table throws `IllegalStateException`, and so does a cell left unowned after the fill.

## Model

Every source cell $c$, in row-major order, with $p = P'(c)$ and $(c', \hat v) = \alpha(c, v'_p)$ ([topology](../topology.md)), updates

$$\mathrm{claims}(c') \mathrel{+}= 1, \qquad \mathrm{who}(c') := \begin{cases} p & \text{first arrival} \\ \min(\mathrm{who}(c'), p) & \text{otherwise,} \end{cases} \qquad \mathrm{crossed}_p \mathrel{\vee}= (\hat v \ne v'_p),$$

and, when $c \notin \mathrm{Skip}$ and $p = \mathrm{who}(c')$ at that moment, $O'(c') := O(c)$. A contested cell goes to the lowest plate id, and its crust key comes from the last arrival that was, when it arrived, the lowest id so far and not skipped. A cell no arrival reaches keeps $O'(c') = \bot$.

The moved plate grid is $P''(c') = \mathrm{who}(c')$ where $\mathrm{claims}(c') \ge 1$, and $\bot$ elsewhere. Its gaps are filled in two passes of in-place row-major sweeps, each repeated until a sweep changes nothing:

$$\text{(A)}\;\; P''(c) := p \;\text{ if every owned neighbour of } c \text{ is } p; \qquad \text{(B)}\;\; P''(c) := \min \operatorname*{arg\,max}_{p} \mathrm{contact}(c, p),$$

with $\mathrm{contact}$ as in the [flood](flood.md). The velocities become $v''_p = -v'_p$ if $\mathrm{crossed}_p$, else $v'_p$, and the registry is $R'' = \mathrm{from}(P'', v'')$.

## Procedure

1. The arguments are checked; then every cell moves by $\alpha$ on the [topology](../topology.md), counting arrivals, keeping the lowest arriving plate, recording pole crossings per plate, and writing crust keys as in the Model.
2. Every cell no plate reached is marked unresolved, and the plate gaps are filled in the two passes of the Model: first from a single owned neighbour, then from the neighbour that touches most.
3. When the contacts, lockers, and registry are given, the [subduction](../crust/subduct.md) corrections rewrite the moved crust keys at collisions and rifts.
4. The velocity of every plate that crossed a pole is negated, and the result is returned. The re-traced contacts received are checked for null and not used, and neither is $g$ beyond its check.
5. The geometry phase recounts the registry from the moved plates and velocities and stages plates, registry, velocities, and occupancy.

Shorter forms of advection take no corrections and no mask, and one moves the step-0 keys. `PlateKinematics` is also a `SubSystem` of its own, which reads plates, velocities, and contacts (staged first) and stages plates, velocities, and a registry, but no phase of the pipeline uses it: the geometry phase calls advection directly.

## What is true afterwards

Every cell is owned by a plate; plate ids are those of the geometry pass, and a plate can end with no cell if every destination it reached was taken by a lower id. The staged velocities are reversed for every plate that touched a pole. The occupancy holds $\bot$ exactly at the cells that no unskipped source reached and that no correction filled: those are the cells the [ridge](../crust/ridge.md) mints. Crust keys that did move still point at their old lockers, whose thicknesses are unchanged by this chapter.

## Cost

$O(WH)$ for the move; each gap-fill sweep is $O(WH)$.

Code: [world/motion/](../../../../product/src/main/java/com/aethelgard/product/world/motion/README.md)  
Parent: [motion](README.md). Why an unused re-trace is passed in: [open questions](../../open-questions.md).
