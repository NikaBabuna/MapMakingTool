<!--
  File: docs/architecture/world/crust/precedence.md
  Purpose: Precedence — which plate loses a collision, from the buoyancy of the crust at the two contact cells
  Audience: Agents and humans
  Update when: The precedence rule or the continental threshold changes
-->

# Precedence

When two plates collide, the one with the lighter, thinner crust at the contact goes under. Ocean always goes under continent. Between two oceans, the smaller plate goes under. Between two continents, neither can: they press together instead.

## What it reads

A collision contact, the settled occupancy and lockers (the crust before this generation moves anything), the registry (for areas), and the map size.

## What it writes

A plate id, the loser, or `NONE` (−1) when both contact cells are continental. It writes no field. A null argument throws `NullPointerException`.

## Model

With $\mathrm{cont}(c) \iff T(O(c)) \ge T_{\mathrm{land}}$, for a contact $\xi = (c, n, a, b, \textsf{COLLIDE})$ and $c_b = \nu(c, n)$:

$$\lambda(\xi) = \begin{cases} \textsf{NONE} & \mathrm{cont}(c) \wedge \mathrm{cont}(c_b) \\ b & \mathrm{cont}(c) \wedge \neg\,\mathrm{cont}(c_b) \\ a & \neg\,\mathrm{cont}(c) \wedge \mathrm{cont}(c_b) \\ \lambda_{\mathrm{area}}(a, b) & \text{otherwise,} \end{cases}$$

where $\lambda_{\mathrm{area}}$ is the smaller-area rule of [interaction](../interaction.md). Only the two contact cells count, not the plates as a whole.

## Procedure

1. A locker is continental when its thickness is at least $T_{\mathrm{land}}$, and a cell is continental when the locker under it is.
2. For a collision, the neighbour cell is found through the [topology](../topology.md), both cells are tested, and the answer is `NONE`, the oceanic side, or the area-only loser.

## What is true afterwards

Every caller in a generation asks with the same settled crust: the budgets and intents ([interaction](../interaction.md)), the collide sink ([sink](../motion/sink.md)), the stamps ([orogeny](orogeny.md)), and the key corrections ([subduct](subduct.md)). So the same contact has the same loser in all of them. A continent never loses to an ocean.

## Cost

$O(1)$ per contact.

Code: [world/crust/](../../../../product/src/main/java/com/aethelgard/product/world/crust/README.md)  
Parent: [crust](README.md). Why buoyancy decides: [ADR-013](../../../paperwork/decisions/ADR-013-crust-topology.md).
