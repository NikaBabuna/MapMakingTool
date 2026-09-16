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
| **WorldSpec** | Step 0 seed: grid width, height, and recorded generation seed. |
| **Grid** | Immutable rectangular layer of `int` cells stored in the Pool. |
| **Layer** | Named Pool field whose value is a `Grid` of the world geometry. |
| **Elevation** | First relief layer (`elevation`); Step 0 is all zeros; later Steps are collision uplift. |
| **Plates** | Layer (`plates`) of integer plate ids; Step-0 Voronoi nearest-site assignment from seed (6–15 sites). |
| **Suture** | Contact between different plate ids; 4-neighbor collision uplifts both sides. |
| **Voronoi plates** | Each cell’s plate id is the nearest site (Euclidean); ties take the lower site index. |
| **Collision uplift** | Generative rule: each generation Step, cells touching a foreign plate gain +1 elevation. |
| **Tectonics System** | Product `EngineSystem` (`tectonics`) assigned to `world/tectonics`. |
| **Generation tick** | `GenerationTickPolicy` emits `world/tectonics` after Step 0. |
| **WorldDump** | Headless text snapshot of a settled run (header, elevation grid, plates grid). |
| **Seed** | Initial configuration that deterministically produces a world variant |
| **Timeline** | Scrubbable history of world formation from simulation start to present |
| **Guide mode** | User nudges specific features; simulation resolves the rest consistently |
| **Explore mode** | User generates and evaluates worlds without manual constraint |
| **Causal chain** | Sequence of simulation steps explaining why a map feature exists |
| **Inspection** | Selecting a point to view its causal history and contributing processes |

_Add simulation-domain terms (plates, biomes, etc.) in [wiki/](wiki/) as they are defined._
