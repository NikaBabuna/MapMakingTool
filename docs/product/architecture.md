<!--
  File: docs/product/architecture.md
  Purpose: Product module layout and host wiring
  Audience: Agents implementing product code
  Update when: Product source layout or EngineSetup wiring changes
-->

# Product architecture

**Status:** active (F-016 WorldDump; **G-003 done**)  
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

## Host wiring (F-016)

`ProductHost` constructs an `Engine` via `EngineSetup`.

| Piece | F-016 |
|-------|--------|
| Schema | `elevation` → `FieldType.STATIC`; `plates` → `FieldType.CONSTANT` |
| Values | Immutable `Grid` of `int` cells |
| Create | `ProductHost.create(WorldSpec)` seeds **zero** elevation and a two-plate `plates` grid from `seed` |
| Default | `ProductHost.create()` → `WorldSpec.DEFAULT` (8×8, seed `0`) |
| Category tree | Product-authored `CategoryTree.of("world/tectonics")` (ADR-009) |
| Emission | `GenerationTickPolicy` — emit `world/tectonics` when `updateCount >= 2` (skip Step 0) |
| System | `EngineSystem` id `tectonics`, assigned `world/tectonics`, Sub-System `CollisionUplift` |
| Compute | Engine default (`SkeletonPoolCompute` heartbeat). World is **not** `PoolSnapshot.value`. |
| Dump | `WorldDump.of(engine, spec)` — header + elevation + plates; canonical golden is DEFAULT + `advance(3)` |

`WorldSpec.seed` places the plates suture. It is not a Pool field.

---

## Source layout (through F-016)

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
  src/test/java/com/aethelgard/product/
    ProductHostTest.java
    WorldStateTest.java
    ElevationProcessTest.java
    WorldDumpTest.java
  src/test/resources/worlds/
    default-n3.txt
```
