<!--
  File: docs/product/wiki/README.md
  Purpose: Door to the world rules
  Audience: Humans and agents
  Update when: A wiki page is added or removed
-->

# The world

The rules the world obeys, told for a person: what shape it has, what moves across it, and what makes it high or low. How the program computes these rules is not in this folder.

**Why:** The world's rules have their own folder, one page per part of the world, so each can be read and changed as a rule and not as code. A rule of the world belongs here, in words a person reads; how the program computes it belongs on the architecture shelf's world pages.

The pages build on one another in order. The map page sets the stage: the grid, its edges, and the seed. The tectonics page says what the plates do on that stage and what that does to the crust. The elevation page reads height off that crust and says how the map colours it.

| Page | Read it when |
|------|----------------|
| [world.md](world.md) | You need the size of the map, how its edges join, or what a seed decides |
| [tectonics.md](tectonics.md) | You need how plates begin, drift, meet, split, and disappear, and what each kind of meeting does to the crust: ridges, subduction, arcs, and sutures |
| [elevation.md](elevation.md) | You need what height means, how it is read from the crust, why a new world starts flat, or how the map colours height |
