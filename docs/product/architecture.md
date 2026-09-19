<!--
  File: docs/product/architecture.md
  Purpose: Product module layout and host wiring
  Audience: Agents implementing product code
  Update when: Product source layout or EngineSetup wiring changes
-->

# Product architecture

**Status:** active (F-025 Next `ui/web`; F-024 MapHost; F-023 console; G-006 in progress)  
**Roll-up:** [../architecture.md](../architecture.md)  
**Engine host:** [../engine/architecture.md](../engine/architecture.md)  
**Domain:** [wiki/world.md](wiki/world.md) · [wiki/elevation.md](wiki/elevation.md)  
**ADR:** [ADR-010](../project/decisions.md)

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

`product` depends on `engine`. **`engine` must never depend on `product`.** `ui` and `cli` depend on `product`. **Product has no Swing.** `cli` does not depend on `ui`. `ui` depends on `cli` **only** for the placeholder console (`CommandDispatch`, F-023).

---

## Session (F-019), kinematics (F-020), orogeny (F-021)

`ProductSession` owns one `Engine` created via `ProductHost`. Callers **advance** and **read grids** through the session. Advances are **serialized** (one lock). Not a command parser — no CLI verb names.

| Piece | F-021 |
|-------|--------|
| Schema | `elevation` → `FieldType.STATIC`; `plates` → `FieldType.STATIC`; `plate_velocity` → `FieldType.CONSTANT` |
| Values | Immutable `Grid` of `int` cells; `PlateVelocities` per-site `(vx, vy)` |
| Create | `ProductHost.create(WorldSpec)` / `new ProductSession(spec)` seeds **zero** elevation, a **Voronoi** `plates` grid, and CONSTANT velocities from `seed` (6–15 sites) |
| Default | `ProductSession.ofDefault()` → `WorldSpec.DEFAULT` (8×8, seed `0`) |
| View | `ProductSession.view()` / `WorldSpec.VIEW` (512×512, seed `0`) — map window launch spec |
| Category tree | Product-authored `CategoryTree.of("world/tectonics")` (ADR-009) |
| Emission | `GenerationTickPolicy` — emit `world/tectonics` when `updateCount >= 2` (skip Step 0) |
| Systems | `kinematics` (Sub-System `PlateKinematics`) and `tectonics` (Sub-System `Orogeny`); same snapshot; neither sees the other this Step |
| Compute | Engine default (`SkeletonPoolCompute` heartbeat). World is **not** `PoolSnapshot.value`. Kinematics uses heartbeat−1 as generation index \(G\) under that default. |
| Dump | `ProductSession.settledWorld()` / `WorldDump.of(engine, spec)` — header + elevation + plates + velocities; canonical golden is DEFAULT + `advance(3)` |

`WorldSpec.seed` places Voronoi plate sites (`N = 6 + floorMod(seed, 10)`) and per-plate velocities in `{-1,0,1}`. Each cell takes the nearest site (Euclidean); ties take the lower site index. After Step 0, kinematics advects ownership (toroidal wrap; leftover cells nearest moved site). Orogeny uses **standing** plates: toroidal 4-neighbor converge `+1` / diverge `−1` / transform `0` (any converge wins). Elevation may go negative. The seed is not its own Pool field.

---

## Map view (in `ui`, F-022)

The **UI** module paints product values. Window opens at **Step 0** on `WorldSpec.VIEW`. One pixel per cell. Dark tool chrome (`0x12141A`): layers Elevation / Plates / Overlay, Advance, Play/Pause, speed, seed + New world, inspect sidebar, legend, **Console**. Advance and play ticks run **one** generation Step on a worker thread (not the Swing EDT) via `ProductSession`. While compute is in flight the status text is **Working...** and further Advances (and play ticks) are ignored. `newWorld(seed)` is ignored while busy.

Console lines go through `com.aethelgard.cli.CommandDispatch` on the **same** `ProductSession` (`MapController.runCommand`). After `advance`, the raster refreshes. Placeholder verbs: `status`, `advance [N]`, `dump`, `at X Y`, `layers`. Unstable — not a product API.

Paint lives in `com.aethelgard.ui.ElevationRaster` (integer, truncating division). Packed as `0xRRGGBB`. Same grids + layer → identical RGB.

**Land ramp** (`e >= 0`, F-018; clamp 32):

```
e = min(elevation, 32)
R = 12 + (243 * e) / 32
G = 10 + (186 * e) / 32
B = 18 + (78 * e) / 32
```

**Ocean** (`e < 0`): constant `(18, 56, 92)` / `0x12385C`.

**Hillshade** (Elevation and Overlay land cells; toroidal west/north). Ocean is not hillshaded. Flat land (`dw = dn = 0`) matches the land ramp.

```
dw = e(x,y) - e(x-1, y)
dn = e(x,y) - e(x, y-1)
lit = clamp(12 + 2*dw + 2*dn, 6, 18)
c' = min(255, (c * lit) / 12)
```

**Plates:** `z = plateId * 0x9E3779B97F4A7C15`; `z ^= z >>> 30`; `R,G,B = 48 + ((z >>> shift) & 0x7F)` for shifts 0, 8, 16.

**Overlay:** elevation paint, then each channel `c / 3` when plate id differs from toroidal east or south neighbor.

Play speeds: Slow 1000 ms, Normal 250 ms (default), Fast 100 ms. Default paused. Play uses an injected `PlayScheduler` (`SwingPlayScheduler` in `ProductApp`).

`MapController` has no Swing types. `MapFrame` / `ProductApp` / `SwingPlayScheduler` are interactive only (`com.aethelgard.ui`).

Launch from repo root: `run-product.cmd` or `run-ui.cmd` (`mvnw -pl ui -am install -DskipTests` then `mvnw -pl ui exec:java`).

Headless CLI: `cli` creates `ProductSession.ofDefault()`. `--steps N` prints `settledWorld()`. Bare argv is one dispatcher line (`status`, `advance 3`, `at 1 2`, …). Placeholder (ADR-010).

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
| `POST /api/speed` | `?speed=` or body (Slow / Normal / Fast) |
| `POST /api/new-world?seed=` | Reseed |
| `POST /api/inspect?x=&y=` | Cell inspect |
| `POST /api/command` | Plain-text line → `CommandDispatch` |

Launch: `com.aethelgard.ui.host.MapHostApp` (default port **7420**, `WorldSpec.VIEW`). CORS `*` for local Next. Swing map remains until F-026.

## Next.js tool (F-025)

Front lives in **`ui/web/`** (Next.js App Router). Talks only to `MapHost` over HTTP (`NEXT_PUBLIC_MAP_HOST`, default `http://127.0.0.1:7420`). Play is a **client timer** posting `/api/advance`. Visual chrome is an elevated dark tool (not a Swing clone); map pixels still come from `ElevationRaster` via the host. Dev: warm host + `npm run dev` — [ui/web/README.md](../../ui/web/README.md).

---

## Source layout (through F-025)

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
    WorldDumpTest.java
    PlateKinematicsTest.java
    OrogenyTest.java
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
    MapFrame.java
    ProductApp.java
    ExecutorPlayScheduler.java
    host/
      MapHost.java
      MapHostApp.java
  web/                          # Next.js tool (F-025)
    package.json
    README.md
    src/app/
    src/components/
    src/lib/
  src/test/java/com/aethelgard/ui/
    MapViewTest.java
    ToolUiTest.java
    ConsoleUiTest.java
    host/
      MapHostTest.java
      WebFrontTest.java

cli/
  src/main/java/com/aethelgard/cli/
    CommandDispatch.java
    CliRunner.java
    CliOptions.java
    CliResult.java
    Main.java
```
