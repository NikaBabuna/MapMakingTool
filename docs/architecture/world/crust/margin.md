<!--
  File: docs/architecture/world/crust/margin.md
  Purpose: MarginRelief rift trough, collide slope, and lip blend
  Audience: Agents and humans
  Update when: MarginRelief radii or the ocean clamp changes
-->

# Margin

`MarginRelief` runs after ridge mint and before the continental thicken. It shapes oceanic lockers near a contact. Lockers that are already continental are left alone. `PASS_BY` writes nothing.

## What it reads

Staged or pool `occupancy` and `lockers`, and staged or pool `plates` and `plate_velocity`. It retraces contacts from those plates. It does not reuse the boundary list from the start of the generation.

## What it writes

`lockers`.

## Procedure

Distance is an orthogonal flood on the sphere-on-rectangle, seeded on both cells of each contact. A locker takes the smallest separate distance among its cells, and the smallest collide distance.

| Constant | Value | Rule |
|----------|-------|------|
| `DIVERGE_RADIUS` | 8 | If the separate distance `d` is within this radius, thickness is replaced by `trough(d) = 4 + d / 2`. Contact cells become 4. Distance 8 becomes 8. |
| `LIP` | 4 | When `d` is 1 through 4 and `selectsLip` is true (`(hash & 1) == 0` for the representative cell), thickness becomes the average of that trough and the farther orthogonal neighbor's thickness, then clamped to 15. |
| `COLLIDE_RADIUS` | 4 | If the collide distance is strictly inside this radius, add `4 - distance`. Distance 0 adds 4. |

A locker whose thickness was already `>= 16` is skipped. After the collide bonus, a result `>= 16` is clamped to 15. `PASS_BY` has no distance field. When both a rift and a collision reach the same locker, the trough is applied first and the bonus is added to that.

## What is true afterwards

Ocean beside a rift is a trough relative to thickness 8, and still below land. Ocean beside a collision is a short slope, still below land. Continental lockers are unchanged by this phase. [Continental collide](collide.md) is what pushes a locker across 16.

## Where it lives

`MarginRelief` in `product/src/main/java/com/aethelgard/product/MarginRelief.java`.

Parent: [crust](README.md).
