<!--
  File: docs/product/wiki/world.md
  Purpose: The map itself — cells, edges, and the seed
  Audience: Humans and agents
  Update when: The world's geometry changes
-->

# The map

This page owns the shape of the world: the cells, how the edges join, and what a seed is. It does not own how plates move or how high the land stands. Those are the tectonics page and the elevation page.

A world is a rectangular grid of cells. Every layer of the world covers that same grid. The window shows 1920×1080 cells. The height on that grid is elevation. The elevation page owns what the number means.

## Edges

East joins west. A thing that leaves the eastern edge comes back on the west, still heading east.

North and south join as a sphere laid on the rectangle. A thing that crosses the north edge re-enters from the north, halfway around the world, and its direction of travel turns about. The same is true of the south.

An older rule joined north directly to south, like a torus. That rule is retired. A later rule stopped motion at the poles, like a cylinder. That rule is retired for the world itself. The view of the map still does not scroll past the top or the bottom of the rectangle. The world does.

## The seed

A seed is a whole number. It chooses where the plates begin and how they first drift. The same seed grows the same world. It does not paint the height. At the start the crust is all ocean, and the height is the ocean baseline. How many plates, and what the crust does after that, belong to the tectonics page.
