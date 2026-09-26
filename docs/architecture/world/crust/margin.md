<!--
  File: docs/architecture/world/crust/margin.md
  Purpose: MarginRelief — phase 7: a trough beside every rift, a short slope beside every collision, and a blended lip, on oceanic crust only
  Audience: Agents and humans
  Update when: MarginRelief.apply, trough, collideBonus, selectsLip, shoulderLocker, distances, or a margin constant changes
-->

# Margin

The ocean floor is not flat near plate edges. Beside a rift it dips into a trough that rises back to normal depth a few cells away; beside a collision it slopes up toward the edge. On some cells near a rift, the trough is blended with the floor just beyond it, so the lip is uneven. Continents are left alone, and no margin can lift ocean into land.

## What it reads

The staged `occupancy` and `lockers` of the ridge, and the staged `plates` and `plate_velocity` of the motion chapter, from which it traces the contacts again, after the move. The seed of the velocities drives the lip hash.

## What it writes

`lockers`, with oceanic thicknesses reshaped. A field of the wrong type throws `IllegalStateException`.

## Model

Let $K'$ be the contacts traced on the moved plates and velocities ([boundaries](../boundaries.md)). For a kind $\kappa$ and a radius $\rho$, $D_\kappa$ is the orthogonal distance on the [topology](../topology.md) from the nearest cell of a $\kappa$-contact (either of its two cells), found by breadth-first search and cut at $\rho$:

$$D_{\mathrm{sep}} : \Omega \to \{0, \dots, 8\} \cup \{\bot\}, \qquad D_{\mathrm{col}} : \Omega \to \{0, \dots, 4\} \cup \{\bot\} .$$

A locker $j$ takes the least distances over its cells, $d^{\mathrm{sep}}_j = \min_{O(c) = j} D_{\mathrm{sep}}(c)$ and $d^{\mathrm{col}}_j = \min_{O(c) = j} D_{\mathrm{col}}(c)$. Its representative $r_j$ is the first cell, in row-major order, that attains $d^{\mathrm{sep}}_j$. For every locker with $T(j) < T_{\mathrm{land}}$:

$$t = \begin{cases} \mathrm{trough}(d^{\mathrm{sep}}_j) = 4 + \lfloor d^{\mathrm{sep}}_j / 2 \rfloor & d^{\mathrm{sep}}_j \le 8 \\ T(j) & \text{otherwise,} \end{cases}$$

then the lip blend, when $1 \le d^{\mathrm{sep}}_j \le 4$ and $\mathrm{lip}(r_j)$, with the shoulder $h$ being the locker under the neighbour of $r_j$ that is farthest from the rift, beyond $r_j$ itself ($\bot$ counts as 9), with the lowest locker id on a tie:

$$t := \min\bigl(\lfloor (t + T(h)) / 2 \rfloor,\; T_{\mathrm{land}} - 1\bigr),$$

`MarginRelief.apply` (the trough and the lip) in [`MarginRelief.java`](../../../../product/src/main/java/com/aethelgard/product/MarginRelief.java):

```java
if (sepD[id] <= DIVERGE_RADIUS) {
  int d = sepD[id];
  int curve = trough(d);
  if (d >= 1 && d <= LIP && selectsLip(seed, repX[id], repY[id])) {
    int neighbor = shoulderLocker(repX[id], repY[id], separate, occupancy, width, height);
    if (neighbor >= 0 && neighbor < count) {
      curve = (curve + pre[neighbor]) / 2;
      if (curve >= Lockers.T_LAND) {
        curve = Lockers.T_LAND - 1;
      }
    }
  }
  thickness = curve;
  changed = true;
}
```

then the collision slope, when $d^{\mathrm{col}}_j < 4$:

$$t := \min\bigl(t + (4 - d^{\mathrm{col}}_j),\; T_{\mathrm{land}} - 1\bigr),$$

`MarginRelief.apply` (the slope) in [`MarginRelief.java`](../../../../product/src/main/java/com/aethelgard/product/MarginRelief.java):

```java
if (colD[id] < COLLIDE_RADIUS) {
  thickness += collideBonus(colD[id]);
  if (thickness >= Lockers.T_LAND) {
    thickness = Lockers.T_LAND - 1;
  }
  changed = true;
}
```

and $T'(j) = t$ when a trough or a slope applied. The lip hash, with $\phi_1 = \texttt{0x9E3779B97F4A7C15}$ and $\phi_5 = \texttt{0xC2B2AE3D27D4EB4F}$, selects half the cells:

$$z = s \oplus x\phi_1 \oplus y\phi_5, \qquad \mathrm{lip}(x, y) \iff \bigl(z \oplus (z \gg 33)\bigr) \bmod 2 = 0 .$$

A trough runs from 4 at the rift to 8, fresh-ocean thickness, at distance 8; a slope adds 4 at the collision down to 1 at distance 3. Where a locker is near both, the slope is added to the trough.

## Procedure

1. `execute` reads the occupancy, lockers, plates, and velocities (staged first) and stages the four-argument `apply`, which traces the moved contacts and calls the contact form with the velocities' seed. [`MarginRelief.execute`](../../../../product/src/main/java/com/aethelgard/product/MarginRelief.java).
2. `distances` seeds both cells of every contact of one kind at distance 0 through `seed`, and grows the distance field breadth-first until the radius: `DIVERGE_RADIUS` (8) for rifts, `COLLIDE_RADIUS` (4) for collisions. [`MarginRelief.distances`](../../../../product/src/main/java/com/aethelgard/product/MarginRelief.java).
3. For every locker, it keeps the least rift distance with its representative cell, and the least collision distance. [`MarginRelief.apply`](../../../../product/src/main/java/com/aethelgard/product/MarginRelief.java).
4. It skips continental lockers, applies `trough` and, on the cells `selectsLip` picks within `LIP` (4) of a rift, blends with the locker `shoulderLocker` finds. It then adds `collideBonus`, and caps both results at $T_{\mathrm{land}} - 1$. [`MarginRelief.apply`](../../../../product/src/main/java/com/aethelgard/product/MarginRelief.java).

## What is true afterwards

No margin turns ocean into land: every locker it touches ends below $T_{\mathrm{land}}$, and no continental locker changes. A trough replaces the thickness rather than subtracting from it, so near a rift the ocean floor lies at or below fresh-ocean level (elevation $-4$ to $0$ before the lip blend); near a collision it is shallower. The contacts here are those after the move, not those of phase 1, so the margins follow where the edges now are. [Continental collision](collide.md) is the only phase that can lift ocean across $T_{\mathrm{land}}$.

## Cost

Two breadth-first searches of $O(WH)$, and $O(WH + L)$ for the locker pass.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Phase | `MarginRelief` | `MarginRelief.id`, `MarginRelief.writeRanges`, `MarginRelief.execute`, `MarginRelief.apply`, `trough`, `collideBonus`, `selectsLip`, `shoulderLocker`, `distances`, `seed`, `DIVERGE_RADIUS`, `COLLIDE_RADIUS`, `LIP` | [`product/src/main/java/com/aethelgard/product/MarginRelief.java`](../../../../product/src/main/java/com/aethelgard/product/MarginRelief.java) |

Parent: [crust](README.md).
