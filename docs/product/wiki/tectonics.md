<!--
  File: docs/product/wiki/tectonics.md
  Purpose: Plates and crust — what moves, where plates meet, and what that does to the land
  Audience: Humans and agents
  Update when: A plate or crust rule changes
-->

# Plates and crust

This page owns the plates and the crust. It does not own the shape of the map, and it does not own how height is read off the crust. Those are the [world](world.md) page and the [elevation](elevation.md) page.

## Plates

Plates are the pieces of the world's surface that move. At every step, every cell belongs to exactly one plate, so the plates always cover the whole map with no gaps and no overlaps.

A seed grows 12–24 of them. Each plate starts from an origin, a point the seed chooses, and each cell starts with the plate whose origin is nearest. If two origins are equally near, the earlier one wins. Nearness is measured as on a globe: the rows near the top and the bottom of the map stand for short circles near the poles, so an east-west gap there counts for less than the same gap at the equator.

## How plates drift

Every plate has a drift. A plate moves at most one cell per step east or west, and at most one cell per step north or south, so its drift is either standing still or one of the eight compass directions.

The seed chooses each plate's first drift. After that, the plate's edges change it. Where two plates pull apart, both are pushed further apart. Where two plates collide, the plate that holds its ground is pushed back, which slows the collision, while the plate that gives way is not pushed back and keeps going under. In one step a drift changes by at most one cell in each direction. If every plate would come to a stop, one plate is set moving again, so the world never freezes.

A plate that crosses a pole turns about, as the [world](world.md) page describes.

## The crust

The crust is what the plates carry. Every piece of crust has a thickness, a whole number, and the thickness decides what the crust is.

| Thickness | What it is |
|-----------|------------|
| Below 16 | Oceanic crust. New ocean is always born at thickness 8 |
| 16 or more | Continental crust, the stuff of continents. It is never drawn down under another plate |
| 32 | The cap. Arcs and sutures never thicken crust past this |

At the start there are no continents. The crust is all oceanic, thickness 8, everywhere. Continents appear later, where collisions build them.

The crust rides with the plate. When a plate moves, the crust on it moves too, so a range travels with the plate that carries it. A mountain does not stay behind as a smear where the plates used to meet. Height is only a reading of that riding crust, and the elevation page owns that reading.

An older rule painted height directly onto the place where plates were touching, and left it there. That rule is retired.

## Where plates meet

Wherever two plates touch, they meet along a boundary. Whether a stretch of boundary pulls apart, collides, or slides depends only on whether the two plates' drifts carry them towards each other across it, away from each other, or neither.

| Meeting | What the crust does |
|---------|---------------------|
| They pull apart | A ridge of new oceanic crust opens, at thickness 8. It does not inherit the mountains beside the rift |
| Ocean meets continent | The ocean subducts: it is drawn down under the continent and is gone. The continent stays, even when its plate is the smaller one |
| Ocean meets ocean | The smaller plate gives way. The winner thickens by 8. If it is still under 16, it is raised to 16, so one meeting can make land. Land made this way is an arc |
| Continent meets continent | A suture. Each side thickens by 4, and thickening stops at 32. Neither side is consumed. The two plates do not weld into one |
| They slide past | No crust is made or destroyed, and neither plate grows or shrinks there |

These meetings are the boundaries. The Overlay view draws them as a dark line.

Besides those meetings, every step each boundary nudges the crust right on the line by one. Where one plate gives way in a collision, the crust on the winning side thickens by 1 and the crust on the losing side thins by 1. Along a rift, the crust on both sides thins by 1. Where two continents meet, or plates slide past, there is no nudge.

A boundary does not advance as a straight line. In each step, about one cell in four along a collision or a rift holds still, and about one in eight bites one cell further, so fronts stay ragged, as real coasts and ranges are.

## The ocean beside a boundary

Rifts and collisions also shape the ocean crust a little way out from the line.

| Beside | What the ocean crust does |
|--------|---------------------------|
| A rift | It sinks into a trough. On the rift the crust is 4 thick. It deepens back to 8 by eight cells away. Within four cells of the rift, some cells are blended with their outer neighbour, so the edge of the trough is broken rather than a straight band |
| A collision | It is pushed up into a short slope: 4 thicker on the collision line, 1 less for each cell outwards, and unchanged from four cells away |

Crust that is already land is left alone by this shaping, and this shaping alone never raises ocean to land. It stops at thickness 15.

## Plates grow, split, and disappear

Plates change size where they meet. Both plates gain cells at a rift, because the new ridge belongs to them. The plate that gives way in a collision loses cells along it.

A gap opened between two plates is filled from the plates that border it. It is not handed to some third plate that merely happens to be near. An older rule did that. That rule is retired.

A plate with no cells left is gone.

If a plate's cells split into pieces that no longer touch, that split is fission. Each piece becomes its own plate and keeps the drift it had.

A piece smaller than 0.01% of the map, about 200 cells, is absorbed by the neighbour it shares the longest edge with.
