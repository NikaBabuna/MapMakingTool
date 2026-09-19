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
| **Plate velocity** | **Code today:** Constant field (`plate_velocity`) of per-site `(vx, vy)` in `{-1,0,1}`. **G-008:** Constant-forever **superseded**; registry + edge-driven integrate (F-037). |
| **Plates** | Layer (`plates`) of integer plate ids. **Code today:** cylindrical nearest-site N=12–24 at Step 0; then kinematics advection. |
| **Suture** | Contact between different plate ids; cylinder 4-neighbor orogeny on **standing** plates. |
| **Voronoi plates** | Historical name for nearest-site partition; distance is **cylindrical** (wrap X, flat Y). Ties take the lower site index. |
| **Collision uplift** | Retired (F-021). Replaced by orogeny. |
| **Orogeny** | Generative rule: standing-plate cylinder contacts; converge +1, diverge −1, transform/interior 0. |
| **Kinematics System** | Product `EngineSystem` (`kinematics`) that advects `plates` each generation Step. |
| **Tectonics System** | Product `EngineSystem` (`tectonics`) assigned to `world/tectonics`; Sub-Systems `TraceBoundaries` + `Orogeny`. |
| **Generation tick** | `GenerationTickPolicy` emits `world/tectonics` after Step 0 (claimed by kinematics and tectonics). |
| **WorldDump** | Headless text snapshot of a settled run (header, elevation, plates, velocities, registry, boundaries). |
| **WorldSpec.VIEW** | Product window launch spec: **1920×1080** cells, seed 0 (F-031). Dump fixture stays `DEFAULT` small. |
| **Torus** | Earlier G-008 lock (wrap both axes). **Amended F-034** to cylinder. |
| **Cylinder map** | Wrap X (longitude); polar edges on Y (no wrap). Sphere-on-rectangle analogue. |
| **Boundaries** | STATIC Pool object (`boundaries`): classified contacts (separate / collide / pass-by). |
| **Plate registry** | STATIC Pool object (`plate_registry`): per-plate area + initial velocity (F-033). Edge-driven integrate F-037. |
| **Boundary tectonics** | G-008 model: edge classify / flux / flood / fission — [wiki/tectonics.md](wiki/tectonics.md). |
| **Fission** | When a plate’s cells become disconnected, each component becomes its own plate (crumbs &lt; 0.05% area absorbed). |
| **ElevationRaster** | UI headless RGB image of a map layer (`com.aethelgard.ui`): ocean + hillshaded land, plate colors, or overlay. |
| **MapController** | UI headless map logic: `ProductSession`, layers, Advance/Play, newWorld, inspect, legend, `runCommand` (cli dispatcher), busy / `Working...`. No Swing. |
| **MapHost** | Localhost HTTP facade over `MapController` (`com.aethelgard.ui.host`). Loopback only. Used by the Next/Tauri front. |
| **MapHostApp** | Entry that starts `MapHost` (default port 7420, `WorldSpec.VIEW`). |
| **ui/web** | Next.js studio cartography tool (F-025 / G-007). HTTP client to `MapHost`; client-timed Play; map-first shell. |
| **ui/desktop** | Tauri 2 shell (F-026). Spawns/stops `MapHostApp`; webview → Next. |
| **CommandDispatch** | Placeholder verb table in `cli` (`status`, `advance`, `dump`, `at`, `layers`). Unstable. Not a product API. |
| **MapLayer** | Visible layer: Elevation, Plates, Overlay. Switching does not advance the world. |
| **MapSpeed** | Play tick period: Slow 1000 ms, Normal 250 ms, Fast 100 ms. |
| **PlayScheduler** | Injected repeating ticks for Play. Host: `ExecutorPlayScheduler`. Next Play is client-timed. |
| **CellInspect** | Click-inspect snapshot: x, y, elevation, plate id, vx, vy. |
| **LegendEntry** | Headless legend row: packed RGB + label. |
| **Seed** | Initial configuration that deterministically produces a world variant |
| **Timeline** | Scrubbable history of world formation from simulation start to present |
| **Guide mode** | User nudges specific features; simulation resolves the rest consistently |
| **Explore mode** | User generates and evaluates worlds without manual constraint |
| **Causal chain** | Sequence of simulation steps explaining why a map feature exists |
| **Inspection** | Selecting a point to view its causal history and contributing processes |

_Add simulation-domain terms (plates, biomes, etc.) in [wiki/](wiki/) as they are defined._
