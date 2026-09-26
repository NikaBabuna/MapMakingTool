<!--
  File: docs/architecture/glossary.md
  Purpose: Implementation words and shared symbols
  Audience: Agents and humans
  Update when: The meaning of a public implementation word changes, or a symbol shared by several models changes
-->

# Paper glossary

Words and symbols for the implementation of the whole program: engine, world, session, command line, and studio. Domain words live in [the product glossary](../product/glossary.md).

## Terms

| Term | Meaning | Do not confuse it with |
|------|---------|------------------------|
| Pool | The engine's one shared state: a heartbeat and a map of typed fields. Updated once per step by the compute, and by merge ([pool](engine/pool.md)) | A grid. Grids are values the product stores in Pool fields |
| Heartbeat | The Pool's `value`, a 64-bit counter the compute changes; the world reads the generation index from it | A field. The heartbeat is not in the field map |
| `PoolCompute` | The pluggable update run once per step before any system; default `SkeletonPoolCompute` | `EventEmissionPolicy`, which only chooses events |
| `EventEmissionPolicy` | The pluggable choice of which categories fire during compute; default the scripted paths ([events](engine/events.md)) | The category tree, which defines ancestry, not firing |
| `EngineSetup` | The wiring of a run: tree, stub claimers, systems, schema, diagnostics, input, view, compute, emission ([setup](engine/setup.md)) | `EngineConfig`, the step-0 seed |
| `EngineConfig` | The initial heartbeat, scripted paths, and field seeds: the only input to step 0 | A snapshot of a later step |
| Step | One `runStep` transaction: stage, compute, consume, claim, run, barrier, merge, clear, view ([one engine step](engine/README.md)) | A generation. A generation is the world's system running inside a step |
| Event | A notice in the buffer carrying a category, not addressed to anyone | A field write. Writes travel in the output buffer |
| Category tree | The hierarchy of `/`-separated paths; a claimer claims its category and every descendant | The world. The product builds the tree; the engine ships none |
| `EventClaimer` | Claims by ancestry. A system has one; stub claimers have no system | A system |
| System | An `EngineSystem`: runs its sub-systems when it claimed at least one event, and returns `OUT_SYS` ([systems](engine/systems.md)) | A sub-system. Sub-systems chain; systems do not |
| Sub-system | One block with a declared write range, run against the snapshot and the staging of earlier blocks | A phase. Each world phase is a sub-system, but the engine does not know phases |
| `OUT_SYS` | The map a system returns: the last staged value of every field it wrote | The Pool. `OUT_SYS` is merged afterwards |
| `ProvenancedWrite` | A `(systemId, value)` in the output buffer; merge rules see the id | A bare value |
| `FieldMergeType` | The rule that turns the standing value and the writes of one field into its next value ([merge](engine/merge.md)) | `FieldType`, the four built-in rules |
| Claim / finish barrier | Counts systems that claimed and systems that finished; merge waits until they are equal | A sub-system counter |
| `EngineDiagnostics` | The engine's report port: step start and settle, emission, claims, unmatched events ([engine diagnostics](engine/diagnostics.md)) | `DiagnosticsHub`, the session's measurements |
| Input view | The frozen set of actions active for one step ([user](engine/user.md)) | The input register, which lives between steps |
| Generation | One run of the nine tectonics phases, in every step after step 0 ([one generation](world/README.md)) | A step of the engine |
| Phase | One of the nine sub-systems of the system `tectonics`, in the order of `ProductHost.setup` ([wiring](world/wiring.md)) | A stage of the engine step |
| Staged value | A value written by an earlier phase of the same generation; a phase reads it before the settled value when it is present | The settled value, which the last generation left |
| Plate | An id in the plate grid, with a velocity in $\{-1,0,1\}^2$ and an area ([fields](world/fields.md)) | A locker. A plate owns cells; a locker is the crust under them |
| Contact | A `BoundaryContact`: two neighbouring cells of different plates, with a direction and a kind ([boundaries](world/boundaries.md)) | A plate edge as a line. A contact is one cell pair |
| Closing rate | $n \cdot (v_a - v_b)$ across a contact: positive collides, negative separates, zero passes by | Speed. It is a relative rate along one direction |
| Loser | The plate that goes under at a collision, or `NONE` when both contact cells are continental ([precedence](world/crust/precedence.md)) | The smaller plate. Crust decides first; area only breaks a tie |
| Area budget | `AreaFlux`: per-plate and sink counts from the contacts; staged and settled, not spent by the geometry ([interaction](world/interaction.md)) | The change in area. Areas change through sinking, flooding, and advection |
| Motion intent | `MotionIntent`: the per-plate direction the contacts push toward; only its sign is used ([integrate](world/motion/integrate.md)) | A velocity |
| Locker | One crust column: an id in the thickness table; several cells may share one | A plate |
| Occupancy | The grid of locker ids: which crust column is under each cell | The plate grid |
| Continental | A locker at least $T_{\mathrm{land}} = 16$ thick | Land. Elevation is thickness minus 8, so continental crust stands at 8 or more |
| Sink | $-1$ in the working plate grid: a cell a collision emptied, until the flood fills it ([sink](world/motion/sink.md)) | Unresolved |
| Unresolved | $-1$ after advection: a cell no plate reached (plate grid, filled at once) or no crust reached (occupancy, filled by the ridge) ([advect](world/motion/advect.md)) | Sink |
| Skip mask | The cells whose owner the geometry pass changed; their crust keys are not carried forward | The sink |
| Crumb | A plate piece smaller than 0.01% of the map, absorbed by the neighbour it touches most ([fission](world/motion/fission.md)) | A new plate from fission, which survives |
| Arc | The thickening of the winner's locker where two oceans collide, to at least $T_{\mathrm{land}}$ ([collide](world/crust/collide.md)) | A suture |
| Suture | The thickening of both lockers where two continents collide | An arc |
| Isostasy | Height is thickness minus $T_{\mathrm{ocean}}$, computed at the end of every generation ([isostasy](world/crust/isostasy.md)) | A physical buoyancy model |
| Reference pipeline | `ProductGeneration.advance`: the generation as one call, outside the engine ([reference](world/reference.md)) | The pipeline the engine runs |
| Session | A `ProductSession`: one world, one engine, one lock ([run](session/run.md)) | A protocol session. This is a program object |
| `DiagnosticsHub` | The session's named rings of step time, memory, paint time, and phase times ([session diagnostics](session/diagnostics.md)) | `EngineDiagnostics` |
| World dump | The canonical text of a settled world ([dump](session/dump.md)) | A save file. Nothing reads it back |
| Command language | The noun-path and verb lines of `CommandDispatch`, shared by the CLI and the studio terminal ([language](cli/language.md)) | The CLI flags, which only choose seed, steps, and lines |
| Controller | `MapController`: the studio's hold on one session, with background steps, play, layers, and paint ([controller](studio/controller.md)) | The session |
| Busy | The controller's flag while a background step runs; status, layers, and restarts still work | The session lock |
| Paint generation | A counter the controller bumps on every paint; part of the host's raster cache key | The generation index of the world |
| Raster | An `ElevationRaster`: one packed colour per cell for one layer ([raster](studio/raster.md)) | The packed body, which is its bytes on the wire |
| Map host | `MapHost`: the loopback HTTP server over the controller, on port 7420 ([http](studio/http.md)) | The engine, which the old paper called the host |
| Apply generation | The web front's counter that discards answers to requests older than the last picture-changing action ([tool](studio/web/tool.md)) | The paint generation |
| Viewport | The web front's camera: a scale and a translation, wrapping east–west ([viewport](studio/web/viewport.md)) | The map window size |

## Symbols

A symbol used by the model of one page only is defined on that page. The symbols below are shared by several models.

| Symbol | Meaning | Defined on |
|--------|---------|------------|
| $k$ | The index of an engine step, from 0 | [one engine step](engine/README.md) |
| $S_k$ | The settled Pool after step $k$, $(h_k, u_k, F_k)$ | [one engine step](engine/README.md) |
| $h$ | The heartbeat, a signed 64-bit integer | [pool](engine/pool.md) |
| $u$ | The update count; $u_k = k + 1$ | [pool](engine/pool.md) |
| $F$, $F'$ | The field map; $F'$ is the map after compute and before merge | [pool](engine/pool.md) |
| $\Phi$ | The declared field names | [setup](engine/setup.md) |
| $\tau_f$ | The merge rule of field $f$ | [setup](engine/setup.md) |
| $\mathcal{E}$ | The wiring of a run | [setup](engine/setup.md) |
| $\Sigma$ | The ordered systems of a run | [setup](engine/setup.md) |
| $\Gamma$, $\epsilon$ | The Pool compute, and the emission policy | [setup](engine/setup.md) |
| $\mathcal{I}$ | The input register | [user](engine/user.md) |
| $I_k$ | The input view of step $k$, a set of actions | [user](engine/user.md) |
| $B_k$ | The events of step $k$, in emission order | [events](engine/events.md) |
| $\mathit{cl}$, $B_{\mathit{cl}}$ | A claimer, and the events it claims; $B_\bot$ the unmatched events | [events](engine/events.md) |
| $\psi$ | A system | [systems](engine/systems.md) |
| $\sigma_k$ | The snapshot every system of step $k$ reads | [systems](engine/systems.md) |
| $\mathrm{Out}_\psi$ | The output map of system $\psi$ | [systems](engine/systems.md) |
| $\mathrm{Wr}_f$ | The writes of field $f$ in one step, in system order | [merge](engine/merge.md) |
| $\Omega$ | The cells of the map, $\{0..W-1\} \times \{0..H-1\}$ | [fields](world/fields.md) |
| $W$, $H$ | The width and the height of the map, in cells | [fields](world/fields.md) |
| $s$ | The world seed | [fields](world/fields.md) |
| $c = (x, y)$ | A cell: column $x$, row $y$ | [fields](world/fields.md) |
| $P$ | The plate map, $\Omega \to \{0..N-1\}$ | [fields](world/fields.md) |
| $N$ | The number of plates | [fields](world/fields.md) |
| $v_p$ | The velocity of plate $p$, in $\{-1, 0, 1\}^2$ | [fields](world/fields.md) |
| $A_p$ | The area of plate $p$, in cells | [fields](world/fields.md) |
| $R$ | The plate registry | [fields](world/fields.md) |
| $O$ | The occupancy: the locker id under every cell | [fields](world/fields.md) |
| $L$ | The number of lockers | [fields](world/fields.md) |
| $T$ | The thickness of a locker | [fields](world/fields.md) |
| $E$ | The elevation of a cell | [fields](world/fields.md) |
| $T_{\mathrm{ocean}}$, $T_{\mathrm{land}}$ | 8, the thickness of new ocean; 16, the least continental thickness | [fields](world/fields.md) |
| $g$ | The generation index; generation $g$ runs in step $k = g$ | [wiring](world/wiring.md) |
| $\mathcal{W}_g$ | The world after generation $g$ | [one generation](world/README.md) |
| $\nu(c, d)$ | The neighbour of cell $c$ in direction $d$ | [topology](world/topology.md) |
| $D_4$ | The four directions, in the order east, west, south, north | [topology](world/topology.md) |
| $\alpha(c, v)$ | One step of motion of cell $c$ by velocity $v$, with the velocity after it | [topology](world/topology.md) |
| $\operatorname{ap}(x)$ | The antipodal column of column $x$ | [topology](world/topology.md) |
| $\phi_1, \dots, \phi_5$ | The 64-bit constants of the seeded hashes: `0x9E3779B97F4A7C15`, `0xBF58476D1CE4E5B9`, `0x94D049BB133111EB`, `0x2545F4914F6CDD1D`, `0xC2B2AE3D27D4EB4F` | [seed](world/seed.md), [sink](world/motion/sink.md), [margin](world/crust/margin.md) |
| $K$, $K'$ | The contacts of phase 1, and the contacts traced again after the move | [boundaries](world/boundaries.md) |
| $\xi = (c, n, a, b, \kappa)$ | A contact: its cell, its direction, its two plates, its kind | [interaction](world/interaction.md) |
| $n$, $\kappa$ | The direction and the kind of a contact | [boundaries](world/boundaries.md) |
| $\chi$ | The closing rate of a contact | [boundaries](world/boundaries.md) |
| $\lambda(\xi)$ | The loser of a collision, or `NONE` | [precedence](world/crust/precedence.md) |
| $\Delta_p$, $\Delta_\bot$ | The area budget of plate $p$, and of the sink | [interaction](world/interaction.md) |
| $\iota_p$ | The motion intent of plate $p$ | [interaction](world/interaction.md) |
| $\bot$ | No plate or no locker ($-1$); as a subscript, the part that belongs to no plate or claimer | [sink](world/motion/sink.md) |
| $C$ | The working plate grid of the geometry pass | [sink](world/motion/sink.md) |
| $\mathrm{Skip}$ | The skip mask | [sink](world/motion/sink.md) |
| $P'$, $v'$ | The plates and velocities after the renumbering, before advection | [advect](world/motion/advect.md) |
| $\mathit{gen}$, $\mathit{layer}$, $\mathit{raster}$ | The controller's paint generation, layer, and painted raster | [controller](studio/controller.md) |
| $\mathit{vp} = (\zeta, t_x, t_y)$ | A viewport: scale and translation | [viewport](studio/web/viewport.md) |
| $V_w$, $V_h$ / $D_w$, $D_h$ | The stage size in CSS pixels / the map's display size | [viewport](studio/web/viewport.md) |
