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
| **Plate velocity** | STATIC field (`plate_velocity`) of per-plate `(vx, vy)` in `{-1,0,1}`: seeded at Step 0, then edge-driven by `IntegrateVelocity` from `motion_intent` (F-037). |
| **Plates** | Layer (`plates`) of integer plate ids. **Code today:** B1 nearest-site N=12–24 at Step 0; apply flux/fission then advection each generation. |
| **Suture** | Contact between different plate ids; cylinder contacts listed in `boundaries` and used by orogeny. |
| **Voronoi plates** | Historical name for nearest-site partition; distance is **B1** latitude-weighted cylindrical (wrap X, cosQ on Y). Ties take the lower site index. |
| **Collision uplift** | Retired (F-021). Replaced by orogeny. |
| **Orogeny** | Generative relief: standing classified `boundaries` — COLLIDE winner +1 / loser −1, SEPARATE both −1, PASS_BY 0 (F-038). |
| **Kinematics System** | Legacy name; advection now runs inside `ApplyGeometry` (F-036). |
| **Tectonics System** | Product `EngineSystem` (`tectonics`): TraceBoundaries → BoundaryInteraction → IntegrateVelocity → ApplyGeometry → Orogeny. |
| **ApplyGeometry** | Sub-System: apply `area_flux`, flood sink, fission/crumbs/death, advect, refresh registry + velocities. |
| **IntegrateVelocity** | Sub-System: `v' = clamp(v + sgn(intent), -1, 1)` per axis; all-stop → plate 0 `(1,0)` (F-037). |
| **B1 distance** | Equirectangular weight: east–west Δ scaled by `cosQ(y)` (F-036). |
| **Generation tick** | `GenerationTickPolicy` emits `world/tectonics` after Step 0 (claimed by kinematics and tectonics). |
| **WorldDump** | Headless text snapshot of a settled run (header, elevation, plates, velocities, registry, boundaries, area_flux, motion_intent). |
| **WorldSpec.VIEW** | Product window launch spec: **1920×1080** cells, seed 0 (F-031). Dump fixture stays `DEFAULT` small. |
| **Torus** | Earlier G-008 lock (wrap both axes). **Amended F-034** to cylinder. |
| **Sphere polar wrap** | Crossing north re-enters from north at antipodal longitude (heading flips vx+vy); same for south. **Live F-045** (`SphereTopology`). |
| **Cylinder map** | G-008: wrap X; hard polar Y. **Retired** by F-045. |
| **Ridge accretion** | Diverge gaps fill by iterative flood from bordering plates (F-044); no nearest-third void fill (F-043 intent). |
| **Simulation runner** | G-009 product feel: Unity-like control, shared CLI/terminal, visible performance. |
| **Session diagnostics** | Planned (F-042): per-Step timings, memory, counters — queryable via commands. |
| **DiagnosticsHub** | Session-owned controllable diagnostics: named collectors, enable/disable, ring history (F-042). Not Pool state. |
| **DiagnosticCollector** | One named sample stream on the hub (`advance.wall`, `heap.used`, `heap.max`, `paint.wall`, `phase.trace`, …). |
| **Phase collectors** | Per–Sub-System wall timings on advance (`phase.trace` … `phase.orogeny`) via `TimingSubSystem` (F-046). |
| **Boundaries** | STATIC Pool object (`boundaries`): classified contacts (separate / collide / pass-by). |
| **Area flux** | STATIC Pool object (`area_flux`): per-plate Δarea + sinkΔ budgets (F-035); applied in F-036. |
| **Motion intent** | STATIC Pool object (`motion_intent`): per-plate preferred Δv from edges (F-035); applied by `IntegrateVelocity` (F-037). |
| **Plate registry** | STATIC Pool object (`plate_registry`): per-plate area + velocity (F-033+); velocities edge-driven after Step 0 (F-037). |
| **Boundary tectonics** | G-008 model: edge classify / flux / flood / fission — [wiki/tectonics.md](wiki/tectonics.md). |
| **Fission** | When a plate’s cells become disconnected, each component becomes its own plate (crumbs &lt; 0.01% area absorbed). |
| **ElevationRaster** | UI headless RGB image of a map layer (`com.aethelgard.ui`): ocean + hillshaded land, plate colors, or overlay. |
| **MapController** | UI headless map logic: `ProductSession`, layers, Advance/Play, newWorld, inspect, legend, `runCommand` (cli dispatcher), busy / `Working...`. No Swing. |
| **MapHost** | Localhost HTTP facade over `MapController` (`com.aethelgard.ui.host`). Loopback only. Used by the Next/Tauri front. |
| **MapHostApp** | Entry that starts `MapHost` (default port 7420, `WorldSpec.VIEW`). |
| **ui/web** | Next.js studio cartography tool (F-025 / G-007). HTTP client to `MapHost`; client-timed Play; map-first shell. |
| **ui/desktop** | Tauri 2 shell (F-026). Spawns/stops `MapHostApp`; webview → Next. |
| **CommandDispatch** | CLI verb table in `cli` (`status`, `advance`, `dump`, `at`, `layers`, `stats`, `diag …`). Still unstable pending F-048; F-042 adds hub control. Not a product API. |
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
