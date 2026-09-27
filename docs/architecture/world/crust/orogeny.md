<!--
  File: docs/architecture/world/crust/orogeny.md
  Purpose: Orogeny — phase 5: every contact cell thickens or thins its crust by one, by a rank ladder
  Audience: Agents and humans
  Update when: What the orogeny phase computes, its rank ladder, or the order of its steps, changes
-->

# Orogeny

Along a collision, the winning side's crust crumples upward and the losing side's is dragged down; along a rift, both sides thin. Every contact cell changes its crust by one unit per generation, in whichever direction its strongest contact says.

## What it reads

The staged `boundaries` of phase 1, and the settled `occupancy`, `lockers`, and `plate_registry`. It stamps the crust under the contact cells as they were before this generation moved anything; the keys then ride with their cells.

## What it writes

`lockers`, with thicknesses changed by the stamps. A field of the wrong type throws `IllegalStateException`.

## Model

Each contact $\xi = (c, n, a, b, \kappa)$, with $c_b = \nu(c, n)$ and loser $\lambda(\xi)$ ([precedence](precedence.md)), proposes a rank for its two cells:

$$\textsf{SEPARATE}:\; c, c_b \mapsto 1; \qquad \textsf{COLLIDE},\ \lambda \ne \textsf{NONE}:\; \text{loser's cell} \mapsto 2,\;\; \text{winner's cell} \mapsto 3; \qquad \text{otherwise: nothing.}$$

A cell keeps the highest rank proposed for it, $r(c) \in \{0, 1, 2, 3\}$, and its change is

$$\delta(3) = +1, \qquad \delta(2) = \delta(1) = -1, \qquad \delta(0) = 0 .$$

Every ranked cell adds its change to the locker under it:

$$T'(j) = T(j) + \sum_{c \,:\, O(c) = j} \delta\bigl(r(c)\bigr).$$

A locker shared by several contact cells takes every one of their changes. There is no floor and no cap here: a locker can thin below $T_{\mathrm{ocean}}$, and even below 0.

## Procedure

1. The phase reads the settled occupancy, lockers, and registry, and the contacts (staged first), and stages the stamped lockers.
2. The contacts are walked once, and each cell keeps the highest rank proposed for it: 1 for a rift, 2 for a loser, 3 for a winner. A cell with no contact keeps rank 0.
3. The thicknesses are copied, and the change of each ranked cell is added to the locker under it. The order of the additions does not matter.
4. Two further forms stamp a grid of numbers instead of lockers, with the area-only loser. No phase of the pipeline calls them.

## What is true afterwards

Every locker under a contact cell has changed by the sum of its cells' changes; every other locker is unchanged. A collision of two continents stamps nothing, because it has no loser: the [suture](collide.md) thickens it instead. The stamp is staged before the ridge mints new lockers, so new crust is never stamped in the generation it appears.

## Cost

$O(|K|)$ for the ranks and $O(L)$ for the copy of the table.

Code: [world/crust/](../../../../product/src/main/java/com/aethelgard/product/world/crust/README.md)  
Parent: [crust](README.md). The closing rate that classifies a contact: [boundaries](../boundaries.md).
