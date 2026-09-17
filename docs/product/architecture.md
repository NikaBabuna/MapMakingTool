<!--
  File: docs/product/architecture.md
  Purpose: Product module layout and host wiring
  Audience: Agents implementing product code
  Update when: Product source layout or EngineSetup wiring changes
-->

# Product architecture

**Status:** active (F-019 session + house; F-017 Voronoi plates; **G-005** in progress)  
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
ui  →  product  →  engine
cli →  product  →  engine
```

`product` depends on `engine`. **`engine` must never depend on `product`.** `ui` and `cli` depend on `product`. **Product has no Swing.** `cli` does not depend on `ui`. `ui` may depend on `cli` later only for the placeholder console (F-023).

---

## Session (F-019)

`ProductSession` owns one `Engine` created via `ProductHost`. Callers **advance** and **read grids** through the session. Advances are **serialized** (one lock). Not a command parser — no CLI verb names.

| Piece | F-019 |
|-------|--------|
| Schema | `elevation` → `FieldType.STATIC`; `plates` → `FieldType.CONSTANT` |
| Values | Immutable `Grid` of `int` cells |
| Create | `ProductHost.create(WorldSpec)` / `new ProductSession(spec)` seeds **zero** elevation and a **Voronoi** `plates` grid from `seed` (6–15 sites) |
| Default | `ProductSession.ofDefault()` → `WorldSpec.DEFAULT` (8×8, seed `0`) |
| View | `ProductSession.view()` / `WorldSpec.VIEW` (512×512, seed `0`) — map window launch spec |
| Category tree | Product-authored `CategoryTree.of("world/tectonics")` (ADR-009) |
| Emission | `GenerationTickPolicy` — emit `world/tectonics` when `updateCount >= 2` (skip Step 0) |
| System | `EngineSystem` id `tectonics`, assigned `world/tectonics`, Sub-System `CollisionUplift` |
| Compute | Engine default (`SkeletonPoolCompute` heartbeat). World is **not** `PoolSnapshot.value`. |
| Dump | `ProductSession.settledWorld()` / `WorldDump.of(engine, spec)` — header + elevation + plates; canonical golden is DEFAULT + `advance(3)` |

`WorldSpec.seed` places Voronoi plate sites (`N = 6 + floorMod(seed, 10)`). Each cell takes the nearest site (Euclidean); ties take the lower site index. The seed is not a Pool field.

---

## Map view (in `ui`, F-019)

The **UI** module paints product values. Window opens at **Step 0** on `WorldSpec.VIEW`. One pixel per cell. Advance runs **one** generation Step on a worker thread (not the Swing EDT) via `ProductSession`. While compute is in flight the status text is **Working...** and further Advances are ignored.

Absolute height ramp lives in `com.aethelgard.ui.ElevationRaster` (integer, truncating division):

```
e = clamp(elevation, 0, 32)
R = 12 + (243 * e) / 32
G = 10 + (186 * e) / 32
B = 18 + (78 * e) / 32
```

Packed as `0xRRGGBB`. Low is dark (12, 10, 18); high is warm light (255, 196, 96). Same elevation grid → identical RGB.

`MapController` has no Swing types. `MapFrame` / `ProductApp` are interactive only (`com.aethelgard.ui`).

Launch from repo root: `run-product.cmd` or `run-ui.cmd` (`mvnw -pl ui -am install -DskipTests` then `mvnw -pl ui exec:java`).

Headless CLI: `cli` creates `ProductSession.ofDefault()`, `--steps N`, prints `settledWorld()`. Placeholder flags (ADR-010).

---

## Source layout (through F-019)

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
    GenerationTickPolicy.java
    CollisionUplift.java
    WorldDump.java
  src/test/java/com/aethelgard/product/
    ProductHostTest.java
    ProductSessionTest.java
    WorldStateTest.java
    ElevationProcessTest.java
    VoronoiPlatesTest.java
    WorldDumpTest.java
  src/test/resources/worlds/
    default-n3.txt

ui/
  src/main/java/com/aethelgard/ui/
    ElevationRaster.java
    MapController.java
    MapFrame.java
    ProductApp.java
```
