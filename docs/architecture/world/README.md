<!--
  File: docs/architecture/world/README.md
  Purpose: Level 3 — one generation of the world, in the order ProductHost.setup wires the tectonics phases
  Audience: Agents and humans
  Update when: ProductHost.setup changes the phase list, the conflict order, or the field schema
-->

# One generation

A world is a set of engine fields and one system, id `tectonics`, that rewrites them once per engine step after step 0. That step is a generation. [`ProductHost.setup`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java) wires the system as nine phases, which run in the order below. The rule that holds across the whole level: every phase reads either the settled value of a field or the value an earlier phase of the same generation staged, and only the last staged value of each field survives, so a generation is a pipeline of pure transformations of the world.

$$\mathcal{W}_{g} \;=\; \mathrm{Isostasy} \circ \mathrm{Collide} \circ \mathrm{Margin} \circ \mathrm{Ridge} \circ \mathrm{Orogeny} \circ \mathrm{Apply} \circ \mathrm{Integrate} \circ \mathrm{Interaction} \circ \mathrm{Trace}\,\bigl(\mathcal{W}_{g-1}\bigr)$$

$\mathcal{W}_g$ is the world after generation $g$, and generation $g$ runs in engine step $k = g$. The fields it owns:

| Field | Value | What it holds |
|-------|-------|---------------|
| `plates` | `Grid` | $P : \Omega \to \{0..N-1\}$, the plate of every cell |
| `plate_velocity` | `PlateVelocities` | $v_p \in \{-1, 0, 1\}^2$ per plate |
| `plate_registry` | `PlateRegistry` | Area $A_p$ and a copy of $v_p$ per plate |
| `boundaries` | `Boundaries` | The classified contacts $K$ between cells of different plates |
| `area_flux` | `AreaFlux` | Per-plate area budget $\Delta_p$ and the sink budget |
| `motion_intent` | `MotionIntent` | Per-plate preferred velocity change $\iota_p$ |
| `occupancy` | `Grid` | $O : \Omega \to$ locker ids, the crust column under every cell |
| `lockers` | `Lockers` | $T : $ locker id $\to$ thickness |
| `elevation` | `Grid` | $E = T \circ O - T_{\mathrm{ocean}}$, the height of every cell |

1. **Trace.** `TraceBoundaries` classifies every contact between two plates from the settled plates and velocities. [Boundaries](boundaries.md).
2. **Interaction.** `BoundaryInteraction` turns the contacts into area budgets and a preferred velocity change per plate. [Interaction](interaction.md).
3. **Integrate.** `IntegrateVelocity` nudges each plate's velocity toward its preferred change. [Integrate](motion/integrate.md).
4. **Apply.** `ApplyGeometry` sinks and refills cells along contacts, splits and renumbers plates, and carries plates and crust keys forward by one step of velocity. [Motion](motion/README.md).
5. **Orogeny.** `Orogeny` thickens or thins the crust at the contact cells of the settled world. [Orogeny](crust/orogeny.md).
6. **Ridge.** `RidgeCreate` gives every cell left without crust a new thin oceanic column. [Ridge](crust/ridge.md).
7. **Margin.** `MarginRelief` shapes the oceanic crust near the moved contacts. [Margin](crust/margin.md).
8. **Collide.** `ContinentalCollide` raises arcs where oceans meet and thickens sutures where continents meet. [Collide](crust/collide.md).
9. **Isostasy.** `ThicknessToElevation` reads height from thickness. [Isostasy](crust/isostasy.md).

Every world field is `STATIC` and has one writer, the system `tectonics`, so merge keeps the system's last staged value. Step 0 runs no phase; it only seeds the fields ([seed](seed.md)).

The finer pages:
- How the system, its phases, the tick, and the conflict order are wired: [wiring](wiring.md).
- The value types and their invariants: [fields](fields.md).
- Neighbours and pole crossings on the map: [topology](topology.md).
- The single-call copy of this pipeline: [reference pipeline](reference.md).
- The chapters: [motion/](motion/README.md) and [crust/](crust/README.md).

The engine step this generation runs inside is described in [../engine/README.md](../engine/README.md); the run that owns the engine in [../session/README.md](../session/README.md); symbols in [../glossary.md](../glossary.md#symbols).
