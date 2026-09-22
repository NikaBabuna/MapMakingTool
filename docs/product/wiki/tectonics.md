<!--
  File: docs/product/wiki/tectonics.md
  Purpose: Plates and crust — what moves, and what that does to the land
  Audience: Humans and agents
  Update when: A plate or crust rule changes
-->

# Plates and crust

This page owns the plates and the crust. It does not own the shape of the map, and it does not own how height is read off the crust. Those are the world page and the elevation page.

Plates are the pieces that move. A seed grows 12–24 of them. Each cell starts with the plate whose origin is nearest. If two origins are equally near, the earlier one wins.

At the start there are no continents. The crust is all oceanic, thickness 8, everywhere. Continents appear later, where collisions build them.

## Where plates meet

| Meeting | What the crust does |
|---------|---------------------|
| They pull apart | A ridge of new oceanic crust opens, at thickness 8. It does not inherit the mountains beside the rift |
| Ocean meets continent | The ocean subducts. The continent stays, even when its plate is the smaller one |
| Ocean meets ocean | The smaller plate gives way. The winner thickens by 8. If it is still under 16, it is raised to 16, so one meeting can make land |
| Continent meets continent | A suture. Each side thickens by 4, and thickening stops at 32. Neither side is consumed. The two plates do not weld into one |
| They slide past | Little crust is made or destroyed |

These meetings are the boundaries.

A rift also lowers the ocean beside it into a trough. A collision slopes the ocean nearby. Crust that is already land is left alone by that shaping.

A gap opened between two plates is filled from the plates that border it. It is not handed to some third plate that merely happens to be near. An older rule did that. That rule is retired.

## Plates appear and disappear

A plate with no cells left is gone.

If a plate's cells split into pieces that no longer touch, that split is fission. Each piece becomes its own plate and keeps the drift it had.

A piece smaller than 0.01% of the map is absorbed by the neighbor it shares the longest edge with.

## What rides

The crust rides with the plate. A mountain does not stay behind as a smear where the plates used to meet. Height is only a reading of that riding crust, and the elevation page owns that reading.

An older rule painted height directly onto the place where plates were touching, and left it there. That rule is retired.
