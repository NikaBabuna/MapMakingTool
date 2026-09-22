<!--
  File: docs/architecture/world/README.md
  Purpose: Level 3 — world fields and the tectonics pipeline order
  Audience: Agents and humans
  Update when: ProductHost field set or sub-system order changes
-->

# One generation

A world is a set of Pool fields plus one system, id `tectonics`, assigned to category `world/tectonics`. `ProductHost.setup` builds that system. `ProductHost.create` seeds the fields and completes step 0. The map window is 1920×1080. Placement of that window is [plates](plates.md).

The product leaves `PoolCompute` at the skeleton default, so the heartbeat still increments. The world is not that heartbeat. It is the field map. `ApplyGeometry` reads the heartbeat as `generationIndex = value - 1`.

`GenerationTickPolicy` is the emission policy. It emits `world/tectonics` when `updateCount >= 2`. Create leaves `updateCount` at 1, so step 0 does not run tectonics. The first `advance` does.

Every world field uses `FieldType.STATIC`.

| Field | Value | What it holds |
|-------|--------|----------------|
| `elevation` | `Grid` | Height. Written only by isostasy. |
| `plates` | `Grid` | Plate id per cell. |
| `plate_velocity` | `PlateVelocities` | Per-plate `(vx, vy)` in `{-1,0,1}`. |
| `plate_registry` | `PlateRegistry` | Per-plate area and velocity. |
| `boundaries` | `Boundaries` | Classified contacts. |
| `area_flux` | `AreaFlux` | Per-plate create and destroy budgets. |
| `motion_intent` | `MotionIntent` | Per-plate preferred velocity change. |
| `occupancy` | `Grid` | Locker id per cell. |
| `lockers` | `Lockers` | Thickness per locker id. |

Step 0 writes zeros for elevation, a nearest-site plate grid, one locker per cell at thickness 8, velocities from the seed, a registry counted from the plates, and boundaries, flux, and intent traced from that standing state. The seed rules are [plates](plates.md).

## Pipeline

`ProductHost.setup` wires one list. Later sub-systems read earlier staging. The conflict resolver orders the overlapping writers as integrate, apply, orogeny, ridge, margin, collide. Trace and interaction sit before that set. Isostasy is last and is the only writer of `elevation`, so it does not overlap them.

The nine phases, in execution order:

1. [TraceBoundaries](boundaries.md) — refresh `boundaries` from standing plates and velocities.
2. [BoundaryInteraction](boundaries.md) — write `area_flux` and `motion_intent`.
3. [IntegrateVelocity](motion.md) — nudge velocities from intent.
4. [ApplyGeometry](motion.md) — flux, flood, fission, crumb, death, then advect plates and occupancy.
5. [Orogeny](crust/orogeny.md) — stamp locker thickness from standing contacts.
6. [RidgeCreate](crust/ridge.md) — mint thin ocean into unresolved occupancy.
7. [MarginRelief](crust/margin.md) — trough, collide slope, lip blend.
8. [ContinentalCollide](crust/collide.md) — arc and suture, capped.
9. [ThicknessToElevation](crust/isostasy.md) — `elevation = thickness − 8`.

Collide loser and the consume that follows advection are [CrustPrecedence](crust/precedence.md) and [Subduct](crust/subduct.md). The crust door lists those pages: [crust/](crust/README.md).

Timing wrappers (`TimingSubSystem`) record phase durations into the session hub. They do not change field values. The hub is [the session](../studio/session.md).

Host loop this generation runs inside: [../host/README.md](../host/README.md).
