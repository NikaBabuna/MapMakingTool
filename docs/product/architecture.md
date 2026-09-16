<!--
  File: docs/product/architecture.md
  Purpose: Product module layout and host wiring
  Audience: Agents implementing product code
  Update when: Product source layout or EngineSetup wiring changes
-->

# Product architecture

**Status:** active (F-018 map window; F-017 Voronoi plates; **G-004 done**)  
**Roll-up:** [../architecture.md](../architecture.md)  
**Engine host:** [../engine/architecture.md](../engine/architecture.md)  
**Domain:** [wiki/world.md](wiki/world.md) · [wiki/elevation.md](wiki/elevation.md)

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
product  →  engine  ←  cli
                ↑
                ui
```

`product` depends on `engine`. **`engine` must never depend on `product`.** `cli` and `ui` also must not depend on `product` (they remain skeleton adapters).

---

## Host wiring (F-018)

`ProductHost` constructs an `Engine` via `EngineSetup`.

| Piece | F-018 |
|-------|--------|
| Schema | `elevation` → `FieldType.STATIC`; `plates` → `FieldType.CONSTANT` |
| Values | Immutable `Grid` of `int` cells |
| Create | `ProductHost.create(WorldSpec)` seeds **zero** elevation and a **Voronoi** `plates` grid from `seed` (6–15 sites) |
| Default | `ProductHost.create()` → `WorldSpec.DEFAULT` (8×8, seed `0`) |
| View | `WorldSpec.VIEW` (512×512, seed `0`) — product window launch spec |
| Category tree | Product-authored `CategoryTree.of("world/tectonics")` (ADR-009) |
| Emission | `GenerationTickPolicy` — emit `world/tectonics` when `updateCount >= 2` (skip Step 0) |
| System | `EngineSystem` id `tectonics`, assigned `world/tectonics`, Sub-System `CollisionUplift` |
| Compute | Engine default (`SkeletonPoolCompute` heartbeat). World is **not** `PoolSnapshot.value`. |
| Dump | `WorldDump.of(engine, spec)` — header + elevation + plates; canonical golden is DEFAULT + `advance(3)` |
| Raster | `ElevationRaster.of(elevation)` — one packed RGB per cell; absolute height ramp (see below) |
| Map UI | `MapController` (headless) + `MapFrame` / `ProductApp` (Swing; not constructed in tests) |

`WorldSpec.seed` places Voronoi plate sites (`N = 6 + floorMod(seed, 10)`). Each cell takes the nearest site (Euclidean); ties take the lower site index. The seed is not a Pool field.

---

## Map view (F-018)

The product window opens at **Step 0** on `WorldSpec.VIEW`. One pixel per cell. Advance runs **one** generation Step on a worker thread (not the Swing EDT). While compute is in flight the status text is **Working...** and further Advances are ignored.

Absolute height ramp (integer, truncating division):

```
e = clamp(elevation, 0, 32)
R = 12 + (243 * e) / 32
G = 10 + (186 * e) / 32
B = 18 + (78 * e) / 32
```

Packed as `0xRRGGBB`. Low is dark (12, 10, 18); high is warm light (255, 196, 96). Same elevation grid → identical RGB.

`MapController` has no Swing types. `MapFrame` / `ProductApp` are interactive only.

Launch from repo root: `run-product.cmd` (or `mvnw -pl product -am install -DskipTests` then `mvnw -pl product exec:java`).

---

## Source layout (through F-018)

```
product/
  pom.xml
  README.md
  src/main/java/com/aethelgard/product/
    package-info.java
    ProductHost.java
    ProductCategories.java
    WorldSpec.java
    WorldFields.java
    Grid.java
    Plates.java
    GenerationTickPolicy.java
    CollisionUplift.java
    WorldDump.java
    ElevationRaster.java
    MapController.java
    MapFrame.java
    ProductApp.java
  src/test/java/com/aethelgard/product/
    ProductHostTest.java
    WorldStateTest.java
    ElevationProcessTest.java
    VoronoiPlatesTest.java
    WorldDumpTest.java
    MapViewTest.java
  src/test/resources/worlds/
    default-n3.txt
```
