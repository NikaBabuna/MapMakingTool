<!--
  File: docs/architecture/world/motion.md
  Purpose: IntegrateVelocity and ApplyGeometry, including advection
  Audience: Agents and humans
  Update when: IntegrateVelocity.integrate or ApplyGeometry.apply changes
-->

# Motion

Integrate writes velocities. Apply spends the area budgets, repairs the plate grid, then carries plates and occupancy forward by those velocities.

## IntegrateVelocity

### What it reads

Standing `plate_velocity` and `plate_registry`. Staged `motion_intent` when interaction has written it.

### What it writes

`plate_velocity` and a registry whose velocities match the new table. Counts must match or it throws.

### Procedure

Per plate, per axis: `v' = clamp(v + signum(intent), -1, 1)`. `signum(0)` is 0. If every plate would be `(0, 0)`, plate 0 is forced to `(1, 0)`.

### What is true afterwards

Velocities are still in `{-1, 0, 1}`, and the registry copy agrees. Plates have not moved.

### Where it lives

`IntegrateVelocity` in `product/src/main/java/com/aethelgard/product/IntegrateVelocity.java`.

## ApplyGeometry

### What it reads

Standing `plates` and `occupancy`. Staged velocities, registry, boundaries, flux, and lockers when earlier phases wrote them. The Pool heartbeat, as `generationIndex = value - 1`.

### What it writes

`plates`, `plate_registry`, `plate_velocity`, and `occupancy`.

### Procedure

`apply` edits a copy of the plate cells, in this order:

1. **Collide.** For each `COLLIDE` contact, `raggedSkip` drops about a quarter of the cells (`(mix & 3) == 0`). The loser comes from [precedence](crust/precedence.md). No loser means the cell stays. Otherwise the loser cell becomes `SINK` (`-1`). `raggedExtra` (`(mix & 7) == 0`, about one eighth) also sinks one orthogonal neighbor that still holds the loser.
2. **Separate.** For each `SEPARATE` contact that is not skipped, both sides of the contact can become `SINK`, with the same ragged extra.
3. **Flood.** `SINK` cells fill from plates that border the hole. The flood is not a nearest-site partition. A cell is not given to a plate that does not touch the hole.
4. **Fission and crumbs.** A plate whose cells no longer touch splits. Each piece becomes its own plate and keeps the velocity it had. A piece whose `area * 10000 < width * height` (smaller than 0.01% of the map) is absorbed by the neighbor it shares the longest edge with.
5. **Death and remap.** A plate with no cells is gone. Survivors are renumbered densely. Velocities follow the new ids.

`ApplyGeometry.execute` then retraces boundaries on the remapped plates so later ridge pairs use the new ids. `PlateKinematics.advect` moves each cell by its plate velocity through `SphereTopology.advectCell`. A pole crossing flips that plate's `vx` and `vy`.

Plate holes after advection flood until every cell is owned. Occupancy is carried by the same motion. A cell whose occupancy has no unique claim stays `PlateKinematics.UNRESOLVED` (`-1`). Contested claims do not allocate a third full-size grid to remember the winner. [Subduct](crust/subduct.md) then rewrites destination occupancy: an oceanic collide loser takes the survivor's locker, and extra copies of a separate-contact locker become unresolved so the ridge can mint.

The registry is rebuilt from the moved plates and the velocities that survived fission and pole flips.

### What is true afterwards

Plate ids are dense. There is no `SINK` left on `plates`. Occupancy may still contain `UNRESOLVED` where a gap opened. Those cells are the input of [ridge](crust/ridge.md). Lockers are unchanged by this phase. Elevation is unchanged.

### Where it lives

| Piece | Type | Path |
|-------|------|------|
| Sub-system | `ApplyGeometry` | `product/.../ApplyGeometry.java` |
| Advection | `PlateKinematics` | `product/.../PlateKinematics.java` |
| Wrap | `SphereTopology` | `product/.../SphereTopology.java` |

Parent: [one generation](README.md). Intent that feeds integrate: [boundaries](boundaries.md).
