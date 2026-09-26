<!--
  File: docs/architecture/world/crust/precedence.md
  Purpose: CrustPrecedence — which plate loses a collision, from the buoyancy of the crust at the two contact cells
  Audience: Agents and humans
  Update when: CrustPrecedence.collideLoser or the continental threshold changes
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

`CrustPrecedence.collideLoser` in [`CrustPrecedence.java`](../../../../product/src/main/java/com/aethelgard/product/CrustPrecedence.java):

```java
boolean contA = continentalCell(occupancy, lockers, contact.x(), contact.y());
boolean contB = continentalCell(occupancy, lockers, b[0], b[1]);
if (contA && contB) {
  return NONE;
}
if (contA != contB) {
  return contA ? contact.plateB() : contact.plateA();
}
return AreaFlux.loser(contact.plateA(), contact.plateB(), registry);
```

## Procedure

1. `continental` tests a locker against $T_{\mathrm{land}}$, and `continentalCell` tests the locker under a cell. [`CrustPrecedence.continental`](../../../../product/src/main/java/com/aethelgard/product/CrustPrecedence.java).
2. `collideLoser` finds the neighbour cell with `SphereTopology.neighbor`, tests both cells, and returns `NONE`, the oceanic side, or the area-only loser. [`CrustPrecedence.collideLoser`](../../../../product/src/main/java/com/aethelgard/product/CrustPrecedence.java).

## What is true afterwards

Every caller in a generation asks with the same settled crust: the budgets and intents ([interaction](../interaction.md)), the collide sink ([sink](../motion/sink.md)), the stamps ([orogeny](orogeny.md)), and the key corrections ([subduct](subduct.md)). So the same contact has the same loser in all of them. A continent never loses to an ocean.

## Cost

$O(1)$ per contact.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Loser rule | `CrustPrecedence` | `NONE`, `continental`, `continentalCell`, `collideLoser` | [`product/src/main/java/com/aethelgard/product/CrustPrecedence.java`](../../../../product/src/main/java/com/aethelgard/product/CrustPrecedence.java) |

Parent: [crust](README.md). Why buoyancy decides: [ADR-013](../../../paperwork/decisions/ADR-013-crust-topology.md).
