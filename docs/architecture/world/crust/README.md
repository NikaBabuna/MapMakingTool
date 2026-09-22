<!--
  File: docs/architecture/world/crust/README.md
  Purpose: Door to crust procedures
  Audience: Agents and humans
  Update when: A crust page is added or removed
-->

# Crust

These pages are the procedures that write locker thickness and the occupancy keys that point at lockers. The generation order that calls them is [the world page](../README.md).

| Page | Question |
|------|----------|
| [precedence.md](precedence.md) | Which plate loses a collision? |
| [subduct.md](subduct.md) | What happens to the loser's occupancy, and to a rift that would stretch one locker? |
| [orogeny.md](orogeny.md) | How does a standing contact change thickness by one? |
| [ridge.md](ridge.md) | What fills an occupancy gap? |
| [margin.md](margin.md) | How do a rift and a collision shape the ocean beside them? |
| [collide.md](collide.md) | How do an ocean-ocean arc and a continental suture thicken? |
| [isostasy.md](isostasy.md) | How does thickness become elevation? |

Quantities shared by these pages: ocean thickness `T_ocean = 8`, continental threshold `T_land = 16`, thickness cap `32`. A locker at or above 16 is continental. Elevation is thickness minus 8, so oceanic crust at 8 is elevation 0.
