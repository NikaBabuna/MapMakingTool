<!--
  File: docs/architecture/world/interaction.md
  Purpose: Interaction — phase 2: area budgets and preferred velocity changes from the contacts
  Audience: Agents and humans
  Update when: What the interaction phase computes, or the order of its steps, changes
-->

# Interaction

Each contact tells its two plates something: a rift says both plates are growing and should keep pulling apart; a collision says one plate is going down and the other should back away. This phase adds up those messages per plate, as an area budget and as a preferred change of velocity.

## What it reads

The staged `boundaries` of phase 1 (the settled value if none was staged), and the settled `plate_registry`, `occupancy`, and `lockers`. The crust decides which plate loses a collision.

## What it writes

`area_flux` (an `AreaFlux`: a signed budget $\Delta_p$ per plate and a sink budget $\Delta_\bot$) and `motion_intent` (a `MotionIntent`: a signed integer vector $\iota_p$ per plate). Plates, velocities, and crust are unchanged. A field of the wrong type throws `IllegalStateException`; a table of length 0 or of unequal lengths throws `IllegalArgumentException`.

## Model

Start from $\Delta \equiv 0$, $\Delta_\bot = 0$, $\iota \equiv 0$, and fold every contact $\xi = (c, n, a, b, \kappa)$ of $K$ in order. With $\lambda(\xi)$ the collision loser ([precedence](crust/precedence.md)), which may be `NONE` when both contact cells are continental:

$$\begin{aligned}
\textsf{SEPARATE}:&\quad \Delta_a \mathrel{+}= 1,\;\; \Delta_b \mathrel{+}= 1,\;\; \Delta_\bot \mathrel{-}= 2; \qquad \iota_a \mathrel{-}= n,\;\; \iota_b \mathrel{+}= n. \\
\textsf{COLLIDE}:&\quad \iota_a \mathrel{-}= n,\;\; \iota_b \mathrel{+}= n; \quad \text{then, unless } \lambda(\xi) = \textsf{NONE}:\;\; \Delta_{\lambda} \mathrel{-}= 1,\;\; \Delta_\bot \mathrel{+}= 1,\;\; \iota_{\lambda} \mathrel{+}= \begin{cases} +n & \lambda = a \\ -n & \lambda = b. \end{cases} \\
\textsf{PASS\_BY}:&\quad \text{nothing.}
\end{aligned}$$

So a rift pushes both plates away from the edge; a collision with a loser pushes only the winner away from the edge and leaves the loser's intent unchanged; a collision of two continents pushes both apart and changes no budget. Every contact conserves area:

$$\sum_{p<N} \Delta_p + \Delta_\bot = 0 .$$

The loser rule depends on whether crust is supplied. With crust, it is the [precedence](crust/precedence.md) rule. Without crust, it is the area-only rule: the plate with the smaller area loses, and the lower id loses a tie,

$$\lambda_{\mathrm{area}}(a, b) = \begin{cases} a & A_a < A_b \\ b & A_b < A_a \\ \min(a, b) & A_a = A_b, \end{cases} \qquad \mathrm{winner}(a, b) = \text{the other plate}.$$

The pipeline always supplies crust. The step-0 seed does not, and at step 0 every column is oceanic, so both rules agree there.

## Procedure

1. The phase reads the contacts (staged first), the registry, the occupancy, and the lockers, and stages both results.
2. The contacts are folded into $\Delta$ and $\Delta_\bot$ as in the Model. Without crust, the fold uses the area-only loser.
3. The area rule gives $\lambda_{\mathrm{area}}$ and its complement, the winner. It is also the tie rule of the precedence and of the arc.
4. The contacts are folded into $\iota$ the same way, with or without crust.
5. The results are read per plate — $\Delta_p$, $\Delta_\bot$, the net $\sum_p \Delta_p + \Delta_\bot$, and $\iota_p$ — and empty tables of $N$ plates can be built.

## What is true afterwards

The net $\sum_p \Delta_p + \Delta_\bot$ is 0. The intent is what [integrate](motion/integrate.md) turns into a velocity change, one unit at most per axis. The budget is staged and settles as a field, but no phase changes the geometry by it: `GeometryApplication` reads `area_flux` only to check that it is present, and sinks and refills cells directly from the contacts ([sink](motion/sink.md)). Whether the budgets should drive the geometry is an [open question](../open-questions.md).

## Cost

$O(|K|)$ time, $O(N)$ memory.

Code: [world/interaction/](../../../product/src/main/java/com/aethelgard/product/world/interaction/README.md)  
Parent: [one generation](README.md). The contacts it folds: [boundaries](boundaries.md).
