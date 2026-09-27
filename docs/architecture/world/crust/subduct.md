<!--
  File: docs/architecture/world/crust/subduct.md
  Purpose: Subduct — correcting the moved crust keys: a collision's loser takes the winner's locker, and a locker split by a rift keeps one copy
  Audience: Agents and humans
  Update when: What the subduction corrections compute, or their order, changes
-->

# Subduct

Advection moves crust keys cell by cell and knows nothing of what happens at plate edges. Two corrections follow. Where a plate went under at a collision, the cell it left behind is covered by the winner's crust. Where a rift pulled one crust column in two, the column stays on one side only, and the other side gets a gap that fresh ocean will fill.

## What it reads

The moved crust keys $O'$ (a mutable grid inside [advection](../motion/advect.md)), the settled occupancy $O$, the phase-1 contacts $K$, the settled lockers, the registry, and the dense plates $P'$ and velocities $v'$ of the geometry pass, before any pole reversal.

## What it writes

$O'$, in place. A null argument throws `NullPointerException`.

## Model

For a cell $c$, let $\delta(c) = \alpha\bigl(c, v'_{P'(c)}\bigr)$ be where advection moved it ([topology](../topology.md)); a cell whose plate id is outside the velocity table is skipped.

**Consume.** For every contact $\xi = (c, n, a, b, \textsf{COLLIDE})$ of $K$ with a loser $\lambda(\xi) \ne \textsf{NONE}$ ([precedence](precedence.md)), let $\ell$ be the loser's contact cell and $w$ the winner's:

$$O'\bigl(\delta(\ell)\bigr) := O(w).$$

Every such contact counts; the ragged skip of the [sink](../motion/sink.md) does not apply here.

**Unshare.** For every contact $\xi = (c, n, a, b, \textsf{SEPARATE})$, for $c$ and then $\nu(c, n)$, remember the first destination of the locker there:

$$\mathrm{keep}\bigl(O(c)\bigr) := \delta(c) \quad \text{unless already set.}$$

Then every other copy of a remembered locker becomes a gap:

$$O'(d) = j \ge 0,\;\; \mathrm{keep}(j) \text{ set},\;\; d \ne \mathrm{keep}(j) \;\Longrightarrow\; O'(d) := \bot .$$

## Procedure

1. The arguments are checked, and the two corrections run in order.
2. Consume: the collision contacts are walked, the loser is found by [precedence](precedence.md), and the winner's settled key is written at the destination of the loser's cell.
3. Unshare: the rift contacts are walked, and for the locker at each side's cell the destination of that cell is remembered, the first one only. Then every other cell holding a remembered locker is cleared.

## What is true afterwards

At every consumed collision, the winner's locker now also lies under the cell where the loser's edge arrived, so several cells may share one locker. A later stamp of that locker changes all of them. No locker named at a rift contact appears at more than one cell. The gaps added here, and those advection left, are filled by the [ridge](ridge.md).

## Cost

$O(|K|)$ for the walks, plus one $O(WH)$ scan when some rift exists.

Code: [world/crust/](../../../../product/src/main/java/com/aethelgard/product/world/crust/README.md)  
Parent: [crust](README.md).
