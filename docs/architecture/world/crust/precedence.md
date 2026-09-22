<!--
  File: docs/architecture/world/crust/precedence.md
  Purpose: CrustPrecedence collide loser
  Audience: Agents and humans
  Update when: CrustPrecedence.collideLoser changes
-->

# Precedence

`CrustPrecedence` decides the loser of one `COLLIDE` contact. It does not write a field. Flux, motion intent, geometry, and subduction call it.

## What it reads

The contact, standing occupancy, lockers, the registry, and the map size. The neighbor cell is `SphereTopology.neighbor` of the contact.

## What it writes

An `int`: a plate id, or `NONE` (`-1`).

## Procedure

A locker is continental when `thickness >= 16` (`Lockers.T_LAND`).

| Contact cells | Loser |
|---------------|--------|
| Both continental | `NONE`. Neither plate is consumed. |
| One continental | The oceanic plate, even when its area is smaller. |
| Both oceanic | `AreaFlux.loser`: smaller registry area, then the lower plate id on a tie. |

## What is true afterwards

Callers that see `NONE` add no sink and no consume for that contact. Callers that see a plate id treat that plate as the one that gives way.

## Where it lives

`CrustPrecedence` in `product/src/main/java/com/aethelgard/product/CrustPrecedence.java`.

Parent: [crust](README.md). Who calls it: [boundaries](../boundaries.md), [motion](../motion.md), [subduct](subduct.md).
