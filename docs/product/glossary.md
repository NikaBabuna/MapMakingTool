<!--
  File: docs/product/glossary.md
  Purpose: Product and domain terminology
  Audience: Agents and humans
  Update when: Domain terms are introduced
-->

# Product glossary

Aethelgard domain terms. Engine terms: [../engine/glossary.md](../engine/glossary.md).

| Term | Definition |
|------|------------|
| **ProductHost** | Product factory that constructs an `Engine` via `EngineSetup`. |
| **ProductSession** | In-process owner of one run: serialized `advance`, read elevation/plates, settled dump. No Swing. Not a CLI parser. |
| **WorldSpec** | Step 0 seed: grid width, height, and recorded generation seed. |
| **Grid** | Immutable rectangular layer of `int` cells stored in the Pool. |
| **Layer** | Named Pool field. Grid layers share world geometry; `plate_velocity` is a per-site object. |
| **Elevation** | First relief layer (`elevation`); Step 0 is all zeros; later Steps are orogeny on standing plates. |
| **Plates** | Layer (`plates`) of integer plate ids; Step-0 Voronoi nearest-site assignment from seed (6–15 sites); kinematics advects after Step 0. |
| **Plate velocity** | Constant field (`plate_velocity`) of per-site integer `(vx, vy)` in `{-1,0,1}`, seeded from `WorldSpec.seed`. |
| **Suture** | Contact between different plate ids; toroidal 4-neighbor orogeny on **standing** plates. |
| **Voronoi plates** | Each cell’s plate id is the nearest site (Euclidean); ties take the lower site index. Leftover cells after advection use the same rule on moved sites. |
| **Collision uplift** | Retired (F-021). Replaced by orogeny. |
| **Orogeny** | Generative rule: standing-plate toroidal contacts; converge +1, diverge −1, transform/interior 0. |
| **Kinematics System** | Product `EngineSystem` (`kinematics`) that advects `plates` each generation Step. |
| **Tectonics System** | Product `EngineSystem` (`tectonics`) assigned to `world/tectonics`; Sub-System `Orogeny`. |
| **Generation tick** | `GenerationTickPolicy` emits `world/tectonics` after Step 0 (claimed by kinematics and tectonics). |
| **WorldDump** | Headless text snapshot of a settled run (header, elevation grid, plates grid, velocities). |
| **WorldSpec.VIEW** | Product window launch spec: 512×512 cells, seed 0 (dump fixture stays `DEFAULT` 8×8). |
| **ElevationRaster** | UI headless RGB image of a map layer (`com.aethelgard.ui`): ocean + hillshaded land, plate colors, or overlay. |
| **MapController** | UI headless map logic: `ProductSession`, layers, Advance/Play, newWorld, inspect, legend, busy / `Working...`. No Swing. |
| **MapLayer** | Visible layer: Elevation, Plates, Overlay. Switching does not advance the world. |
| **MapSpeed** | Play tick period: Slow 1000 ms, Normal 250 ms, Fast 100 ms. |
| **PlayScheduler** | Injected repeating ticks for Play. Production: `SwingPlayScheduler`. |
| **CellInspect** | Click-inspect snapshot: x, y, elevation, plate id, vx, vy. |
| **LegendEntry** | Headless legend row: packed RGB + label. |
| **Seed** | Initial configuration that deterministically produces a world variant |
| **Timeline** | Scrubbable history of world formation from simulation start to present |
| **Guide mode** | User nudges specific features; simulation resolves the rest consistently |
| **Explore mode** | User generates and evaluates worlds without manual constraint |
| **Causal chain** | Sequence of simulation steps explaining why a map feature exists |
| **Inspection** | Selecting a point to view its causal history and contributing processes |

_Add simulation-domain terms (plates, biomes, etc.) in [wiki/](wiki/) as they are defined._
