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
| **Elevation** | Relief layer (`elevation`); Step 0 is all zeros (isostasy of \(T_{ocean}\)). Later Steps: derived isostasy of locker thickness at occupancy (F-056). |
| **Orogeny** | F-038 stamp ladder (COLLIDE winner +1 / loser −1, SEPARATE both −1, PASS_BY 0) now writes **locker thickness**. Superseded as elevation author (F-056). |
| **Tectonics System** | Product `EngineSystem` (`tectonics`): TraceBoundaries → BoundaryInteraction → IntegrateVelocity → ApplyGeometry → Orogeny → ThicknessToElevation. |
| **Locker** | Crust payload keyed by id (thickness). Occupancy keys point at lockers (F-056). |
| **Occupancy** | Cell → locker id. Motion remaps keys; locker contents ride. Distinct from plate id. |
| **Isostasy** | Integer height `thickness − T_ocean` (\(T_{ocean}=8\)) at current occupancy. Sole writer of `elevation` (F-056). |
| **Plate velocity** | STATIC field (`plate_velocity`) of per-plate `(vx, vy)` in `{-1,0,1}`: seeded at Step 0, then edge-driven by `IntegrateVelocity` from `motion_intent` (F-037). |
| **Plates** | Layer (`plates`) of integer plate ids. **Code today:** B1 nearest-site N=12–24 at Step 0; apply flux/fission then advection each generation. |
| **Suture** | G-010: continent–continent collide that thickens both sides and destroys neither locker (planned F-059). Historically also: contact between different plate ids listed in `boundaries`. |
| **Voronoi plates** | Historical name for nearest-site partition; distance is **B1** latitude-weighted cylindrical (wrap X, cosQ on Y). Ties take the lower site index. |
| **Collision uplift** | Retired (F-021). Replaced by orogeny. |
| **Kinematics System** | Legacy name; advection now runs inside `ApplyGeometry` (F-036). |
| **ApplyGeometry** | Sub-System: apply `area_flux`, flood sink, fission/crumbs/death, advect plates **and occupancy**, refresh registry + velocities. |
| **IntegrateVelocity** | Sub-System: `v' = clamp(v + sgn(intent), -1, 1)` per axis; all-stop → plate 0 `(1,0)` (F-037). |
| **B1 distance** | Equirectangular weight: east–west Δ scaled by `cosQ(y)` (F-036). |
| **Generation tick** | `GenerationTickPolicy` emits `world/tectonics` after Step 0 (claimed by kinematics and tectonics). |
| **WorldDump** | Headless text snapshot of a settled run (header, elevation, plates, occupancy, lockers, velocities, registry, boundaries, area_flux, motion_intent). |
| **WorldSpec.VIEW** | Product window launch spec: **1920×1080** cells, seed 0 (F-031). Dump fixture stays `DEFAULT` small. |
| **Torus** | Earlier G-008 lock (wrap both axes). **Amended F-034** to cylinder. |
| **Sphere polar wrap** | Crossing north re-enters from north at antipodal longitude (heading flips vx+vy); same for south. **Live F-045** (`SphereTopology`). |
| **Cylinder map** | G-008: wrap X; hard polar Y. **Retired** by F-045. |
| **Ridge accretion** | Diverge gaps fill by iterative flood from bordering plates (F-044); no nearest-third void fill (F-043 intent). |
| **Simulation runner** | G-009 product feel: Unity-like control, shared CLI/terminal, visible performance. |
| **Session diagnostics** | Live (F-042+): per-Step timings, memory, counters — queryable via `stats` / `diag` and `/api/status` `diag`. |
| **DiagnosticsHub** | Session-owned controllable diagnostics: named collectors, enable/disable, ring history (F-042). Not Pool state. |
| **DiagnosticCollector** | One named sample stream on the hub (`advance.wall`, `heap.used`, `heap.max`, `paint.wall`, `phase.trace`, …). |
| **Phase collectors** | Per–Sub-System wall timings on advance (`phase.trace` … `phase.orogeny` / `phase.isostasy`) via `TimingSubSystem` (F-046 / F-056). |
| **Boundaries** | STATIC Pool object (`boundaries`): classified contacts (separate / collide / pass-by). |
| **Area flux** | STATIC Pool object (`area_flux`): per-plate Δarea + sinkΔ budgets (F-035); applied in F-036. |
| **Motion intent** | STATIC Pool object (`motion_intent`): per-plate preferred Δv from edges (F-035); applied by `IntegrateVelocity` (F-037). |
| **Plate registry** | STATIC Pool object (`plate_registry`): per-plate area + velocity (F-033+); velocities edge-driven after Step 0 (F-037). |
| **Boundary tectonics** | G-008 model: edge classify / flux / flood / fission — [wiki/tectonics.md](wiki/tectonics.md). |
| **Fission** | When a plate’s cells become disconnected, each component becomes its own plate (crumbs &lt; 0.01% area absorbed). |
| **Oceanic crust** | Thickness below \(T_{land}\) (threshold F-058). Step 0 all \(T_{ocean}=8\). Subducts at COLLIDE (F-058). |
| **Continental crust** | **G-010 planned:** thickness \(\ge T_{land}\). Does not die by area-only precedence. Sutures instead of subducting. |
| **Ridge mint** | **G-010 planned (F-057):** new gap occupancy gets thin oceanic lockers; does not inherit neighbor mountains. |
| **Command language** | Shared noun-path + verb operator grammar in `cli` (F-048): point at `session`/`pool`/`schema`/`systems`/`diag`, act with `list`/`get`/`advance`/…. |
| **CommandDispatch** | Single execute entry for headless CLI, MapHost `/api/command`, and in-app console (F-048; deprecated flat aliases remain). |
| **CliRunner** | Headless full runner (F-049): one session per invocation; `--seed` / `--steps` / `-c` over `CommandDispatch`. |
| **MapSpeed** | Play tick rate labels: `1x` / `2x` / `4x` / `Fastest` (F-051). |
| **Runner shell** | Layout slots: menu bar / identity / transport / view / Perf rail / World rail / map HUD / terminal (F-051–F-053). |
| **Perf rail** | Left sidebar listing DiagnosticsHub mean samples via `/api/status` `diag`, plus steps/sec (F-052–F-053). |
| **Panel registry** | `ui/web/src/lib/panels.ts` — descriptors (`id`, `title`, `dock`, `order`, `defaultOpen`, `collapsible`) that rails map through one `Panel.tsx`. Adding a panel means adding a descriptor (F-053, alpha). |
| **Menu bar** | Slim top row rendered from `ui/web/src/lib/menus.ts` (File · Edit · View · Simulation · Help). Items without an action are `enabled: false` stubs — visible, dim, inert (F-053). |
| **Layout keys** | `aethelgard.layout.leftRail` / `.rightRail` / `.terminal` (px sizes) and `aethelgard.rail.<side>.open` / `aethelgard.panel.<id>.open` visibility flags; clamps in `lib/layout.ts` (F-053). |
| **Shortcuts overlay** | `?` dialog listing every runner key from `lib/shortcuts.ts` — the single source shared with menu labels (F-053). |
| **ElevationRaster** | UI headless RGB image of a map layer (`com.aethelgard.ui`): flat `int[]` pixels; bathymetry + hillshaded land (clamp 64), plate colors, or overlay. Controllers may reuse the buffer (F-047). |
| **MapController** | UI headless map logic: `ProductSession`, layers, Advance/Play, newWorld, inspect, legend, `runCommand` (cli dispatcher), busy. No Swing. No map Working… overlay (F-052). |
| **MapHost** | Localhost HTTP facade over `MapController` (`com.aethelgard.ui.host`). Loopback only. Used by the Next/Tauri front. |
| **MapHostApp** | Entry that starts `MapHost` (default port 7420, `WorldSpec.VIEW`). |
| **ui/web** | Next.js studio cartography tool (F-025 / G-007). HTTP client to `MapHost`; client-timed Play; map-first shell. |
| **ui/desktop** | Tauri 2 shell (F-026). Spawns/stops `MapHostApp`; webview → Next. |
| **MapLayer** | Visible layer: Elevation, Plates, Overlay. Switching does not advance the world. HUD + keys `1`/`2`/`3` (suppressed while typing in INPUT/TEXTAREA/SELECT). |
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
