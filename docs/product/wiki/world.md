<!--
  File: docs/product/wiki/world.md
  Purpose: The map itself — cells, edges, and the seed
  Audience: Humans and agents
  Update when: The world's geometry changes
-->

# The map

This page owns the shape of the world: the cells, how the edges join, and what a seed is. It does not own how plates move or how high the land stands. Those are the [tectonics](tectonics.md) page and the [elevation](elevation.md) page.

## Cells

A world is a rectangular grid of cells. The studio's world is 1920 cells from west to east and 1080 from north to south, 1920×1080 in all, a little over two million cells. A cell is the smallest place the world knows. Everything the world records, it records per cell: which plate the cell belongs to, how thick its crust is, and how high it stands. Every layer of the world covers that same grid. The height on that grid is elevation. The elevation page owns what the number means.

The grid never changes size while a world runs. Plates move across it. The cells stay where they are.

## Edges

The rectangle stands for a whole round world, so its edges are not walls. What happens at an edge decides where a plate that drifts off the map comes back.

East joins west. A thing that leaves the eastern edge comes back on the west, still heading east. A plate can drift all the way around the world and return to where it started.

North and south join as a sphere laid on the rectangle. A thing that crosses the north edge re-enters from the north, halfway around the world, and its direction of travel turns about. The same is true of the south. It is what happens to a traveller who walks north over the pole: they come down the far side of the globe, heading south.

The view of the map does not follow the world over the poles. A person can pan east or west without end, but the view stops at the top and the bottom of the rectangle. Only the world itself crosses the poles.

An older rule joined north directly to south, like a torus. That rule is retired. A later rule stopped motion at the poles, like a cylinder. That rule is retired for the world itself, and lives on only in the way the view stops at the top and the bottom.

## The seed

A seed is a whole number. It chooses how many plates there are, where they begin, and how each one first drifts. The same seed grows the same world: step for step, the same plates meet in the same places and raise the same land. A different seed grows a different world.

A seed does not paint the height. At the start the crust is all ocean, and the height is the ocean baseline everywhere. The shape of the world comes only from what the plates do after that, which belongs to the [tectonics](tectonics.md) page.
