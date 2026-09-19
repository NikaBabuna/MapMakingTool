<!--
  File: docs/product/architecture.md
  Purpose: Product module layout and host wiring
  Audience: Agents implementing product code
  Update when: Product source layout or EngineSetup wiring changes
-->

# Product architecture

**Status:** active (G-009 in progress through F-051; G-008 done)  
**Roll-up:** [../architecture.md](../architecture.md)  
**Engine host:** [../engine/architecture.md](../engine/architecture.md)  
**Domain:** [wiki/world.md](wiki/world.md) · [wiki/elevation.md](wiki/elevation.md)  
**ADR:** [ADR-010](../project/decisions.md) · [ADR-011](../project/decisions.md)

---

## Module

| Setting | Value |
|---------|-------|
| Artifact | `com.aethelgard:product` |
| Path | `product/` |
| Package root | `com.aethelgard.product` |
| Java | 21 (parent `maven.compiler.release`) |

### One-way dependency rule

```
ui  →  cli  →  product  →  engine
ui  →  product  →  engine
cli →  product  →  engine
```

`product` depends on `engine`. **`engine` must never depend on `product`.** `ui` and `cli` depend on `product`. **Product has no Swing.** `cli` does not depend on `ui`. `ui` depends on `cli` **only** for the shared command language (`CommandDispatch`, F-048).

---

## Session (F-019), kinematics (F-020), orogeny (F-021)

`ProductSession` owns one `Engine` created via `ProductHost`. Callers **advance** and **read grids** through the session. Advances are **serialized** (one lock). Not a command parser — no CLI verb names in product.

| Piece | F-021 |
|-------|--------|
| Schema | all of `elevation` / `plates` / `plate_registry` / `boundaries` / `area_flux` / `motion_intent` / `plate_velocity` → STATIC |
| Values | Immutable `Grid`; `PlateRegistry`; `Boundaries`; `AreaFlux`; `MotionIntent`; `PlateVelocities` |
| Create | Seeds zero elevation, B1 plates (N=12–24), registry, boundaries, area_flux, motion_intent, velocities |
| Systems | One `tectonics` System: TraceBoundaries → BoundaryInteraction → IntegrateVelocity → ApplyGeometry (apply+advect) → Orogeny |
| Default | `ProductSession.ofDefault()` → `WorldSpec.DEFAULT` (8×8, seed `0`) |
| View | `ProductSession.view()` / `WorldSpec.VIEW` (**1920×1080**, seed `0`, F-031) |
| Category tree | Product-authored `CategoryTree.of("world/tectonics")` (ADR-009) |
| Emission | `GenerationTickPolicy` — emit `world/tectonics` when `updateCount >= 2` (skip Step 0) |
| Compute | Engine default (`SkeletonPoolCompute` heartbeat). World is **not** `PoolSnapshot.value`. Kinematics uses heartbeat−1 as generation index \(G\) under that default. |
| Dump | `ProductSession.settledWorld()` / `WorldDump.of(engine, spec)` — header + elevation + plates + velocities + registry + boundaries + area_flux + motion_intent; canonical golden is DEFAULT + `advance(3)` |

`WorldSpec.seed` places cylindrical nearest-site plates (`N = 12 + floorMod(seed, 13)`) and per-plate velocities in `{-1,0,1}`. Each cell takes the nearest site (wrap X; flat Y); ties take the lower site index. After Step 0, each generation integrates velocities from `motion_intent`, applies flux/fission, re-traces boundaries, then advects ownership (wrap X; Y off-map dropped). Unresolved cells after advection use **iterative flood** (F-044). Crossing a pole uses **sphere antipodal re-entry** and flips plate `vx,vy` (F-045). SEPARATE/COLLIDE flux apply uses **deterministic ragged** skips/nibbles along contacts (F-045 P3). Crumb absorb bar is **0.01%** of W×H (F-046). Orogeny stamps relief from standing classified `boundaries` (O(contacts)). Elevation may go negative. The seed is not its own Pool field.

MapHost `/api/status` uses **cached** step + `busy` so polls never wait on the session physics lock (F-038).

### Session diagnostics (F-042)

`ProductSession.diagnostics()` is a `DiagnosticsHub`: named collectors with enable/disable and bounded ring history (default capacity 64). Built-ins: `advance.wall`, `heap.used`, `heap.max`, `paint.wall`, and phase collectors `phase.trace` / `phase.interaction` / `phase.integrate` / `phase.apply` / `phase.orogeny` (F-046 — `TimingSubSystem` wrappers). Never writes Pool fields. CLI: `stats`, `diag list|on|off|clear`. MapController records paint into the same hub. Advection hotspot (F-046): contested claim tracking without a third `whoMin` WxH grid.

---

## Map view (in `ui`, F-022–F-026)

Headless paint and session control live in `MapController` + `ElevationRaster`. Interactive UI is **Next** (`ui/web/`) behind **Tauri** (`ui/desktop/`). Swing was **removed** (F-026).

Window opens at **Step 0** on `WorldSpec.VIEW`. One pixel per cell. **F-051** runner shell: Play/Pause/Speed (`1x`…`Fastest`), World rail (step/seed/reset), map layer HUD, Terminal. While compute is in flight the status text is **Working...** and further Advances are ignored. `newWorld(seed)` is ignored while busy. Next Play is a **client timer** posting `/api/advance`.

Terminal lines go through `com.aethelgard.cli.CommandDispatch` on the **same** `ProductSession` (host `/api/command` or `MapController.runCommand`). Dedicated `Terminal.tsx` drawer. **F-048** shared language: noun-path + verb (`session get`, `list pool`, `pool.plates get`, `systems.tectonics get`, `diag…`); deprecated aliases `status` / `advance` / `dump` / `at` / `layers` / `stats` / `diag …`.

Paint lives in `com.aethelgard.ui.ElevationRaster` (integer, truncating division). Packed as `0xRRGGBB` in a **flat** `int[]` (F-047). `MapController` **reuses** a **double buffer** when width×height is unchanged (prior snapshot keeps correct pixels). `MapHost` **caches/reuses** one packed `byte[]` keyed by step + layer + paint generation — refill on invalidate, O(1) allocations under Play soak. Same grids + layer → identical RGB.

**Land ramp** (`e >= 0`, F-051 physical atlas; clamp 32): piecewise RGB stops at e = 0, 8, 16, 24, 32 (`ElevationRaster.LAND_STOP_*`).

**Ocean** (`e < 0`): constant `(42, 78, 108)`.

**Hillshade** (Elevation and Overlay land cells; toroidal west/north). Ocean is not hillshaded. Flat land (`dw = dn = 0`) matches the land ramp.

```
dw = e(x,y) - e(x-1, y)
dn = e(x,y) - e(x, y-1)
lit = clamp(12 + 2*dw + 2*dn, 6, 18)
c' = min(255, (c * lit) / 12)
```

**Plates (F-045 bold / F-051 muted):** gray interior; boundary = half-edge **core** (east/south via `SphereTopology`) **dilated** by one orthogonal step (≥2 cells) so strokes survive zoom-out nearest-neighbor.

**Overlay:** elevation paint, then darken on the same bold stroke rule.

Play speeds (F-051): `1x` 250 ms, `2x` 125 ms, `4x` 62 ms, `Fastest` 1 ms. Default paused at `1x`.

`MapController` has no Swing types. No `MapFrame` / `ProductApp` / `SwingPlayScheduler`.

Launch from repo root: `run-product.cmd` (Next + Tauri; Tauri spawns `MapHostApp`).

Headless CLI (F-049): one `ProductSession` per invocation (`WorldSpec.DEFAULT` geometry; `--seed` overrides seed). `--steps N` advances via `session advance N` then dumps via `session get dump`. Repeatable `-c` / `--command` lines share that session through `CommandDispatch`. Bare argv (no flags) is one dispatcher line.

---

## Localhost HTTP host (F-024)

`com.aethelgard.ui.host.MapHost` serves one `MapController` on **127.0.0.1** (JDK `HttpServer`). No new Maven module — lives under `ui`. Product/engine stay free of HTTP UI types.

| Route | Meaning |
|-------|---------|
| `GET /health` | `ok` |
| `GET /api/status` | JSON: step, seed, width, height, layer, speed, playing, busy, statusText, inspect, legend |
| `GET /api/raster` | Packed RGB (`X-Width` / `X-Height`; body = BE width/height + BE `0xRRGGBB` ints) — same formulas as `ElevationRaster` |
| `POST /api/advance` | `advanceAsync` |
| `POST /api/play` / `pause` | Play / pause |
| `POST /api/layer` | `?layer=` or body (Elevation / Plates / Overlay) |
| `POST /api/speed` | `?speed=` or body (`1x` / `2x` / `4x` / `Fastest`) |
| `POST /api/new-world?seed=` | Reseed |
| `POST /api/inspect?x=&y=` | Cell inspect |
| `POST /api/command` | Plain-text line → `CommandDispatch` |

Launch: `com.aethelgard.ui.host.MapHostApp` (default port **7420**, `WorldSpec.VIEW`). CORS `*` for local Next. Writes temp PID file for Tauri quit.

## Next.js tool (F-025 / G-007)

Front lives in **`ui/web/`** (Next.js App Router). Talks only to `MapHost` over HTTP (`NEXT_PUBLIC_MAP_HOST`, default `http://127.0.0.1:7420`). Play is a **client timer** posting `/api/advance`. Visual chrome is **multi-panel studio cartography** with a rebuilt **Terminal** drawer (`Terminal.tsx`, F-050) — see [style-guide.md](style-guide.md). Map pixels still come from `ElevationRaster` via the host. Dev: [ui/web/README.md](../../ui/web/README.md).

## Tauri desktop (F-026)

Shell lives in **`ui/desktop/`**. Dev webview → `http://localhost:3000`. On start spawns `MapHostApp`; on quit stops it via PID file. Primary launch: `run-product.cmd`. [ui/desktop/README.md](../../ui/desktop/README.md).

## G-008 boundary tectonics (F-033 partition live)

Domain + Pool/System plan: [wiki/tectonics.md](wiki/tectonics.md). VIEW **1920×1080**; B1 partition; boundaries; flux/intent; **IntegrateVelocity**; **ApplyGeometry**; **boundary orogeny** (F-038).

---

## Source layout (through F-033)

```
product/
  pom.xml
  README.md
  src/main/java/com/aethelgard/product/
    package-info.java
    ProductHost.java
    ProductSession.java
    ProductCategories.java
    WorldSpec.java
    WorldFields.java
    Grid.java
    Plates.java
    PlateRegistry.java
    PlateVelocities.java
    PlateKinematics.java
    GenerationTickPolicy.java
    Orogeny.java
    WorldDump.java
  src/test/java/com/aethelgard/product/
    ProductHostTest.java
    ProductSessionTest.java
    WorldStateTest.java
    ElevationProcessTest.java
    VoronoiPlatesTest.java
    PlatePartitionTest.java
    WorldDumpTest.java
    PlateKinematicsTest.java
    OrogenyTest.java
    BoundaryTectonicsDocsTest.java
  src/test/resources/worlds/
    default-n3.txt

ui/
  src/main/java/com/aethelgard/ui/
    ElevationRaster.java
    MapLayer.java
    MapSpeed.java
    PlayScheduler.java
    SwingPlayScheduler.java
    CellInspect.java
    LegendEntry.java
    MapController.java
    ExecutorPlayScheduler.java
    host/
      MapHost.java
      MapHostApp.java
  web/                          # Next.js tool (F-025)
    package.json
    README.md
    src/
      components/
        MapTool.tsx
        Terminal.tsx              # F-050 rebuilt terminal
        MapCanvas.tsx
  desktop/                      # Tauri 2 shell (F-026)
    package.json
    README.md
    src-tauri/
  src/test/java/com/aethelgard/ui/
    MapViewTest.java
    ToolUiTest.java
    ConsoleUiTest.java
    host/
      MapHostTest.java
      WebFrontTest.java
      DesktopShellTest.java

cli/
  src/main/java/com/aethelgard/cli/
    CommandDispatch.java
    CliRunner.java
    CliOptions.java
    CliResult.java
    Main.java
```
