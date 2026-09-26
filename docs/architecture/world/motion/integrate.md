<!--
  File: docs/architecture/world/motion/integrate.md
  Purpose: IntegrateVelocity — phase 3: each plate's velocity moves one unit toward its intent
  Audience: Agents and humans
  Update when: IntegrateVelocity.integrate or IntegrateVelocity.execute changes
-->

# Integrate

A plate does not jump to the velocity its edges push it toward; it turns by at most one unit per axis each generation. This phase applies that turn to every plate, and guarantees that the world never comes entirely to rest.

## What it reads

The settled `plate_velocity` and `plate_registry`, and the staged `motion_intent` of phase 2 (the settled value if none was staged).

## What it writes

`plate_velocity`, the nudged velocities, and `plate_registry`, the settled areas with the nudged velocities. A velocity table and an intent of different lengths throw `IllegalArgumentException`; a field of the wrong type throws `IllegalStateException`.

## Model

Per plate $p$ and per axis, with $\operatorname{sgn}$ the sign function ($\operatorname{sgn} 0 = 0$) and $\mathrm{clamp}$ to $[-1, 1]$:

$$v'^{\,x}_p = \mathrm{clamp}\bigl(v^x_p + \operatorname{sgn} \iota^x_p\bigr), \qquad v'^{\,y}_p = \mathrm{clamp}\bigl(v^y_p + \operatorname{sgn} \iota^y_p\bigr),$$

and if $v'_p = (0, 0)$ for every $p$, then $v'_0 := (1, 0)$. The size of an intent does not matter, only its sign: many rifts and one rift turn a plate equally.

`IntegrateVelocity.integrate` in [`IntegrateVelocity.java`](../../../../product/src/main/java/com/aethelgard/product/IntegrateVelocity.java):

```java
for (int i = 0; i < n; i++) {
  vx[i] = clampUnit(standing.vx(i) + Integer.signum(intent.ix(i)));
  vy[i] = clampUnit(standing.vy(i) + Integer.signum(intent.iy(i)));
  if (vx[i] != 0 || vy[i] != 0) {
    anyMove = true;
  }
}
if (!anyMove) {
  vx[0] = 1;
}
```

## Procedure

1. `execute` reads the settled velocities and registry and the intent, staged first. [`IntegrateVelocity.execute`](../../../../product/src/main/java/com/aethelgard/product/IntegrateVelocity.java).
2. `integrate` checks that the tables have the same length, then adds the sign of each intent component and clamps with `clampUnit`. [`IntegrateVelocity.integrate`](../../../../product/src/main/java/com/aethelgard/product/IntegrateVelocity.java).
3. If no plate moves, plate 0 is set to $(1, 0)$. [`IntegrateVelocity.integrate`](../../../../product/src/main/java/com/aethelgard/product/IntegrateVelocity.java).
4. `execute` stages the new velocities, and a registry that keeps the settled areas with the new velocities. [`IntegrateVelocity.execute`](../../../../product/src/main/java/com/aethelgard/product/IntegrateVelocity.java).

## What is true afterwards

Every component is still in $\{-1, 0, 1\}$, at least one plate moves, and the staged registry agrees with the staged velocities. No cell has moved. The geometry pass that follows reads these staged velocities, so a plate that splits in this generation gives its pieces the nudged velocity ([fission](fission.md)).

## Cost

$O(N)$.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Phase | `IntegrateVelocity` | `IntegrateVelocity.id`, `IntegrateVelocity.writeRanges`, `IntegrateVelocity.execute`, `integrate`, `clampUnit` | [`product/src/main/java/com/aethelgard/product/IntegrateVelocity.java`](../../../../product/src/main/java/com/aethelgard/product/IntegrateVelocity.java) |

Parent: [motion](README.md). Where the intent comes from: [interaction](../interaction.md).
