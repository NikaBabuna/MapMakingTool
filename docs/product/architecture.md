<!--
  File: docs/product/architecture.md
  Purpose: Product module layout and host wiring
  Audience: Agents implementing product code
  Update when: Product source layout or EngineSetup wiring changes
-->

# Product architecture

**Status:** active (F-013 `ProductHost`; world fields F-014)  
**Roll-up:** [../architecture.md](../architecture.md)  
**Engine host:** [../engine/architecture.md](../engine/architecture.md)

---

## Module

| Setting | Value |
|---------|-------|
| Artifact | `com.aethelgard:product` |
| Path | `product/` |
| Package root | `com.aethelgard.product` |
| Java | 21 (parent `maven.compiler.release`) |

`ProductHost` constructs an `Engine` via `EngineSetup`. F-013: `ProductHost.setup()` is `EngineSetup.defaults()` (skeleton compute and emission). Later G-003 Steps add field schema, category tree, and Systems here — not in `engine`.

### One-way dependency rule

```
product  →  engine  ←  cli
                ↑
                ui
```

`product` depends on `engine`. **`engine` must never depend on `product`.** `cli` and `ui` also must not depend on `product` (they remain skeleton adapters).

---

## Source layout (F-013)

```
product/
  pom.xml
  README.md
  src/main/java/com/aethelgard/product/
    package-info.java
    ProductHost.java
  src/test/java/com/aethelgard/product/
    ProductHostTest.java
```

No world grid, elevation, or generative Systems in this Step.
