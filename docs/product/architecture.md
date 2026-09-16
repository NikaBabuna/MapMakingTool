<!--
  File: docs/product/architecture.md
  Purpose: Product module layout and host wiring
  Audience: Agents implementing product code
  Update when: Product source layout or EngineSetup wiring changes
-->

# Product architecture

**Status:** active (F-014 world fields; generation F-015)  
**Roll-up:** [../architecture.md](../architecture.md)  
**Engine host:** [../engine/architecture.md](../engine/architecture.md)  
**Domain:** [wiki/world.md](wiki/world.md)

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

## Host wiring (F-014)

`ProductHost` constructs an `Engine` via `EngineSetup`.

| Piece | F-014 |
|-------|--------|
| Schema | Field `elevation` (`WorldFields.ELEVATION`) → `FieldType.STATIC` |
| Value | Immutable `Grid` of `int` cells |
| Create | `ProductHost.create(WorldSpec)` seeds a **zero** grid of `width` × `height` |
| Default | `ProductHost.create()` → `WorldSpec.DEFAULT` (8×8, seed `0`) |
| Compute / emission | Engine defaults (skeleton heartbeat). World is **not** `PoolSnapshot.value`. |

`WorldSpec.seed` is stored for later Steps; it does not fill elevation in F-014.

---

## Source layout (through F-014)

```
product/
  pom.xml
  README.md
  src/main/java/com/aethelgard/product/
    package-info.java
    ProductHost.java
    WorldSpec.java
    WorldFields.java
    Grid.java
  src/test/java/com/aethelgard/product/
    ProductHostTest.java
    WorldStateTest.java
```

No generative Systems in this Step.
