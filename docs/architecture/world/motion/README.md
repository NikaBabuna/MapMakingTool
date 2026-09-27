<!--
  File: docs/architecture/world/motion/README.md
  Purpose: Level 4 — phases 3 and 4 of a generation: integrate the velocities, then the stages of the geometry phase that reshape and move the plates
  Audience: Agents and humans
  Update when: The integration or the geometry phase changes its stages or their order
-->

# Motion

This chapter is how plates change shape and move in one generation. First each plate's drift is nudged by `VelocityIntegration`. Then the geometry phase, `GeometryApplication` ([world/motion/](../../../../product/src/main/java/com/aethelgard/product/world/motion/README.md)), runs the stages below on a private copy of the plate grid: every stage edits that copy in place, in row-major sweeps, so a stage sees the edits of the stages before it and of the cells it has already swept.

**Why:** Everything that changes which plate owns a cell happens in this chapter, so where a plate went is answered here. What belongs here is the order of those stages; how the crust under the cells changes is the [crust](../crust/README.md) chapter.

$$(P, v, O)_g \;=\; \mathrm{Register} \circ \mathrm{Subduct} \circ \mathrm{Advect} \circ \mathrm{Remap} \circ \mathrm{Fission} \circ \mathrm{Flood} \circ \mathrm{Separate} \circ \mathrm{Collide} \circ \mathrm{Integrate}\,\bigl((P, v, O)_{g-1}\bigr)$$

| State | Value | What it holds |
|-------|-------|---------------|
| $C$ | `int[][]` | The working plate grid; $\bot = -1$ marks a cell no plate owns |
| $\mathrm{Skip}$ | `boolean[][]` | The skip mask $\mathrm{Skip} \subseteq \Omega$: cells whose owner the stages changed, whose crust key is not carried forward |

1. **Integrate.** Each plate's velocity moves one unit toward its intent, per axis. [Integrate](integrate.md).
2. The geometry phase reads its inputs: the settled plates and occupancy, the staged velocities, registry, contacts, budgets, and lockers, and the generation index from the heartbeat. [Sink](sink.md).
3. **Collide.** At each collision, the loser's contact cell sinks. [Sink](sink.md).
4. **Separate.** At each rift, both plates claim a sunken cell next to the contact, and sometimes nibble one cell from a neighbour. [Sink](sink.md).
5. **Flood.** Every cell still sunken goes to the neighbouring plate that touches it most. [Flood](flood.md).
6. **Fission.** A plate in several pieces keeps its first piece, and each other piece becomes a new plate. Tiny pieces are absorbed by the neighbour they touch most. [Fission](fission.md).
7. **Remap.** Plates with no cell are dropped, and the survivors are renumbered densely. [Fission](fission.md).
8. The remapped plates are traced again, and the contacts are handed to advection, which only checks that they are present. [Advect](advect.md).
9. **Advect.** Every cell moves by its plate's velocity; plate gaps are flooded, crust keys ride with their cells, and a plate that crossed a pole reverses. [Advect](advect.md).
10. **Subduct.** Inside advection, the crust keys at collisions and rifts are corrected. [Subduct](../crust/subduct.md).
11. **Register.** The registry is recounted from the moved plates, and plates, registry, velocities, and occupancy are staged. [Advect](advect.md).

The contacts every stage uses are those of phase 1, traced before anything moved. Occupancy may leave this chapter with gaps; the crust chapter fills them ([ridge](../crust/ridge.md)).

The generation this chapter belongs to: [../README.md](../README.md). The crust procedures that follow it: [../crust/README.md](../crust/README.md).
