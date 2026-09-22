<!--
  File: docs/architecture/world/boundaries.md
  Purpose: TraceBoundaries and BoundaryInteraction
  Audience: Agents and humans
  Update when: Boundaries.trace, AreaFlux.from, or MotionIntent.from changes
-->

# Boundaries

Two sub-systems run first in the tectonics list. Trace classifies contacts. Interaction turns those contacts into area budgets and a velocity nudge. Neither one moves a plate or a locker.

## TraceBoundaries

### What it reads

Standing `plates` and standing `plate_velocity` from the Pool. It does not read staging.

### What it writes

`boundaries`, a list of `BoundaryContact`.

### Procedure

`Boundaries.trace` walks every cell and only the east and south unit steps, so each undirected edge is seen once. The neighbor comes from `SphereTopology.neighbor`. A step that lands on the same cell is skipped. A neighbor with the same plate id is skipped.

`Boundaries.classify` uses `Orogeny.closing`: `nx * (vxA - vxB) + ny * (vyA - vyB)`.

| Closing | Kind |
|---------|------|
| `> 0` | `COLLIDE` |
| `< 0` | `SEPARATE` |
| `0` | `PASS_BY` |

### What is true afterwards

`boundaries` matches the standing plates and velocities. Later phases in this generation that need contacts read this staging value.

### Where it lives

`TraceBoundaries` and `Boundaries` in `product/src/main/java/com/aethelgard/product/`.

## BoundaryInteraction

### What it reads

Staged `boundaries` when trace has written them, otherwise the Pool. Standing `plate_registry`, `occupancy`, and `lockers`.

### What it writes

`area_flux` and `motion_intent`. Plates and velocities stay as they were.

### Procedure

For each contact, `AreaFlux.from` and `MotionIntent.from` branch on kind.

`SEPARATE`: each plate's area budget gains 1, and the sink budget loses 2. Intent pushes both plates away from the contact normal.

`COLLIDE`: the loser is [precedence](crust/precedence.md). When precedence returns no loser, the contact adds no flux. Otherwise the loser's budget loses 1 and the sink gains 1. Intent first pushes both plates apart along the normal, then adds the normal back onto the loser, which dampens the close.

`PASS_BY`: no flux and no normal intent.

`AreaFlux` keeps `sum(deltaArea) + sinkDelta == 0`.

The area-only loser, used when occupancy is absent, is the plate with the smaller registry area. Equal area takes the lower plate id. Production passes occupancy and lockers, so the buoyancy rule is the one that runs.

### What is true afterwards

Flux and intent describe this generation's contacts. Plates have not moved. Lockers have not changed.

### Where it lives

| Piece | Type | Path |
|-------|------|------|
| Sub-system | `BoundaryInteraction` | `product/.../BoundaryInteraction.java` |
| Budgets | `AreaFlux` | `product/.../AreaFlux.java` |
| Nudge | `MotionIntent` | `product/.../MotionIntent.java` |
| Contact | `BoundaryContact` | `product/.../BoundaryContact.java` |
| Kind | `BoundaryKind` | `product/.../BoundaryKind.java` |

Parent: [one generation](README.md). Who consumes the budgets: [motion](motion.md).
