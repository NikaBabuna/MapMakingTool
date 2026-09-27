<!--
  File: docs/architecture/world/crust/README.md
  Purpose: Level 4 — door to the crust procedures: who loses a collision, how crust keys and locker thicknesses change, and how height is read from thickness
  Audience: Agents and humans
  Update when: A crust page is added, or the question it answers changes
-->

# Crust

Every cell stands on a crust column, a locker, and a locker has a thickness. These pages are the procedures that decide which plate goes under at a collision, which locker each cell points at, and how thick each locker is; height is read from thickness at the end. The order in which they run is the generation's phase list, [../README.md](../README.md).

**Why:** The crust is a material of its own that rides on the plates, so every rule that changes a column's key or thickness is gathered in this chapter, and height always has one cause. Which plate owns a cell is decided in the [motion](../motion/README.md) chapter.

| Page | Question |
|------|----------|
| [precedence.md](precedence.md) | Which plate loses a collision, and when does neither lose? |
| [subduct.md](subduct.md) | How are the moved crust keys corrected at collisions and rifts? |
| [orogeny.md](orogeny.md) | How does each contact thicken or thin the crust by one? |
| [ridge.md](ridge.md) | What crust fills a cell that no crust reached? |
| [margin.md](margin.md) | How do rifts and collisions shape the ocean floor beside them? |
| [collide.md](collide.md) | How do ocean collisions raise arcs, and continental collisions thicken sutures? |
| [isostasy.md](isostasy.md) | How is height read from thickness? |

Shared quantities: new ocean is $T_{\mathrm{ocean}} = 8$ thick, a locker with $T \ge T_{\mathrm{land}} = 16$ is continental, collisions stop thickening at $32$, and height is $E = T - T_{\mathrm{ocean}}$, so fresh ocean stands at 0.

The generation these procedures belong to: [../README.md](../README.md).
