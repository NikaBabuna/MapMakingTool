<!--
  File: docs/architecture/world/plates.md
  Purpose: Step-0 plate partition, velocities, and sphere-on-rectangle steps
  Audience: Agents and humans
  Update when: Plates.seed, PlateVelocities.seed, or SphereTopology changes
-->

# Plates

Step 0 places the plates. Later generations move them. This page is the placement and the geometry of a step. Motion of existing plates is [motion](motion.md).

## What it reads

`WorldSpec`: `width`, `height`, and `seed`. The view spec is 1920×1080, seed 0. The dump fixture is 8×8, seed 0.

## What it writes

`plates`, `plate_velocity`, and the registry counted from them. Occupancy at step 0 is one locker id per cell, `y * width + x`, and every locker thickness is 8. That key rule belongs to the crust pages. The plate grid does not store thickness.

## Procedure

`Plates.count(seed)` is `12 + floorMod(seed, 13)`, so a seed grows 12 to 24 sites.

`Plates.mix` is a SplitMix64 of `(seed, siteIndex, axis)`. Axis 0 places `siteX` in `[0, width)`. Axis 1 places `siteY` in `[0, height)`.

`Plates.assign` gives each cell the nearest site. Distance is cylindrical: X wraps, Y does not. The east-west gap is scaled by `cosQ(y, height) = max(1, round(1024 * sin(π * (y + 0.5) / height)))`. Ties take the lower site index.

`PlateVelocities.seed` uses the same mix with axis 2 for `vx` and axis 3 for `vy`, each reduced into `{-1, 0, 1}`. If every plate would be `(0, 0)`, plate 0 is set to `(1, 0)`.

`SphereTopology` is how a neighbor or an advection step stays on the map. X uses `floorMod`. Crossing the north edge re-enters at antipodal longitude `floorMod(x + width / 2, width)` and stays on row 0. Crossing the south edge does the same onto row `height - 1`. `advectCell` also negates both `vx` and `vy` when the step crosses a pole. It is not a 3D mesh.

## What is true afterwards

Every cell has a plate id in `0 .. N-1`. N is in 12..24. Every plate has a unit-or-zero velocity, and at least plate 0 can move. Elevation is still 0 because every locker is thickness 8 and isostasy has not needed to run for the seed to sit at sea level.

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| Partition | `Plates` | `product/.../Plates.java` |
| Velocities | `PlateVelocities` | `product/.../PlateVelocities.java` |
| Spec | `WorldSpec` | `product/.../WorldSpec.java` |
| Wrap | `SphereTopology` | `product/.../SphereTopology.java` |
| Seed keys | `Occupancy` | `product/.../Occupancy.java` |

Parent: [one generation](README.md).
