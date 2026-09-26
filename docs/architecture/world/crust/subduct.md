<!--
  File: docs/architecture/world/crust/subduct.md
  Purpose: Subduct — correcting the moved crust keys: a collision's loser takes the winner's locker, and a locker split by a rift keeps one copy
  Audience: Agents and humans
  Update when: Subduct.correct, consumeCollide, or unshareSeparate changes
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

`Subduct.consumeCollide` in [`Subduct.java`](../../../../product/src/main/java/com/aethelgard/product/Subduct.java):

```java
int destPlate = postFluxPlates.get(lx, ly);
if (destPlate < 0 || destPlate >= velocities.count()) {
  continue;
}
int[] step =
    SphereTopology.advectCell(
        lx, ly, velocities.vx(destPlate), velocities.vy(destPlate), width, height);
occDest[step[1]][step[0]] = standingOccupancy.get(wx, wy);
```

**Unshare.** For every contact $\xi = (c, n, a, b, \textsf{SEPARATE})$, for $c$ and then $\nu(c, n)$, remember the first destination of the locker there:

$$\mathrm{keep}\bigl(O(c)\bigr) := \delta(c) \quad \text{unless already set.}$$

Then every other copy of a remembered locker becomes a gap:

$$O'(d) = j \ge 0,\;\; \mathrm{keep}(j) \text{ set},\;\; d \ne \mathrm{keep}(j) \;\Longrightarrow\; O'(d) := \bot .$$

## Procedure

1. `correct` checks its arguments and runs the two corrections in order. [`Subduct.correct`](../../../../product/src/main/java/com/aethelgard/product/Subduct.java).
2. `consumeCollide` walks the collision contacts, finds the loser with `CrustPrecedence.collideLoser`, and writes the winner's settled key at the loser cell's destination. [`Subduct.consumeCollide`](../../../../product/src/main/java/com/aethelgard/product/Subduct.java).
3. `unshareSeparate` walks the rift contacts and lets `rememberKeep` store, for the locker at each side's cell, the destination of that cell, packed by `key`. It then clears every other cell holding a remembered locker. [`Subduct.unshareSeparate`](../../../../product/src/main/java/com/aethelgard/product/Subduct.java), [`Subduct.rememberKeep`](../../../../product/src/main/java/com/aethelgard/product/Subduct.java).

## What is true afterwards

At every consumed collision, the winner's locker now also lies under the cell where the loser's edge arrived, so several cells may share one locker. A later stamp of that locker changes all of them. No locker named at a rift contact appears at more than one cell. The gaps added here, and those advection left, are filled by the [ridge](ridge.md).

## Cost

$O(|K|)$ for the walks, plus one $O(WH)$ scan when some rift exists.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Key corrections | `Subduct` | `correct`, `consumeCollide`, `unshareSeparate`, `rememberKeep`, `key` | [`product/src/main/java/com/aethelgard/product/Subduct.java`](../../../../product/src/main/java/com/aethelgard/product/Subduct.java) |

Parent: [crust](README.md).
