<!--
  File: docs/architecture/world/crust/collide.md
  Purpose: Collide — phase 8: an arc where two oceans collide, a suture where two continents collide, both capped
  Audience: Agents and humans
  Update when: What the collision phase computes, its arc, suture, or cap, or the order of its steps, changes
-->

# Collide

This is how land is born and grows. Where two oceanic plates collide, the winner's crust at the contact is raised into an island arc, thick enough to stand above the sea. Where two continents collide, both sides are pressed thicker along the suture. Thickening stops at a cap, so mountains do not grow without bound.

## What it reads

The staged `occupancy` and `lockers` of the margin, and the staged `plates` and `plate_velocity` of the motion chapter. From these it traces the moved contacts and counts a registry of the moved plates.

## What it writes

`lockers`, with arc and suture lockers thickened. A field of the wrong type throws `IllegalStateException`.

## Model

For every contact $\xi = (c, n, a, b, \textsf{COLLIDE})$ of the moved contacts $K'$, with $j_a = O(c)$ and $j_b = O(\nu(c, n))$ both valid locker ids, and continental meaning $T \ge T_{\mathrm{land}}$:

$$\text{both continental} \Rightarrow j_a, j_b \in \mathit{Sut}; \qquad \text{both oceanic} \Rightarrow j_{\mathrm{winner}(a, b)} \in \mathit{Arc}; \qquad \text{otherwise nothing,}$$

where $\mathit{Sut}$ is the suture set, $\mathit{Arc}$ the arc set, and $\mathrm{winner}$ the larger-area rule of [interaction](../interaction.md) on the moved registry. Then, once per locker, with $\mathrm{ARC} = 8$, $\mathrm{SUTURE} = 4$, $\mathrm{CAP} = 32$:

$$T'(j) = \begin{cases} T(j) & T(j) \ge \mathrm{CAP} \\ \min\bigl(T(j) + \mathrm{SUTURE},\; \mathrm{CAP}\bigr) & j \in \mathit{Sut} \\ \max\bigl(\min(T(j) + \mathrm{ARC},\; \mathrm{CAP}),\; T_{\mathrm{land}}\bigr) & j \in \mathit{Arc} \setminus \mathit{Sut} \\ T(j) & \text{otherwise.} \end{cases}$$

A locker is thickened once per generation however many contacts name it, and a suture wins over an arc. An arc from fresh ocean ($T = 8$) lands exactly on $T_{\mathrm{land}}$: new land.

## Procedure

1. The phase reads the occupancy, lockers, plates, and velocities (staged first), traces the moved contacts, counts the moved registry, and stages the result.
2. The collision contacts are walked; one whose locker id is out of range is skipped. Both lockers are marked as suture when both sides are continental, or the winner's locker as arc when both are oceanic.
3. Each marked locker is thickened once, by $\mathrm{SUTURE}$ or $\mathrm{ARC}$, capped at $\mathrm{CAP}$, and an arc is lifted to at least $T_{\mathrm{land}}$.

## What is true afterwards

Every arc locker is continental, and no locker thickened here exceeds 32. A locker already at 32 or more is unchanged. Mixed collisions, one ocean and one continent, add nothing here: the ocean goes under instead ([precedence](precedence.md), [subduct](subduct.md)). Plate ids are never merged: two continents that suture remain two plates.

## Cost

$O(|K'|)$ for the marks, $O(L)$ for the thickening, and $O(WH)$ for the trace and the count.

Code: [world/crust/](../../../../product/src/main/java/com/aethelgard/product/world/crust/README.md)  
Parent: [crust](README.md). Why collisions make continents: [ADR-013](../../../paperwork/decisions/ADR-013-crust-topology.md).
