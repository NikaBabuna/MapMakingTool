<!--
  File: ui/README.md
  Purpose: Landmark index for the UI Maven module
  Audience: Agents and humans
  Update when: UI layout or usage changes
-->

# UI module

Maven artifact `com.aethelgard:ui` — Aethelgard map view of product values.

**Depends on:** `product` (ADR-010). Never depended on by `engine` or `product`.

## Headless logic

`MapController` — `ProductSession`, `advance()` / `advanceAsync()`, elevation raster, busy status. **No Swing.** Covered by tests.

`ElevationRaster` — packed RGB height ramp (F-018 formula).

## Interactive

From the repo root in **cmd** (recommended):

```bat
run-product.cmd
```

(`run-ui.cmd` is the same launch.)

Or manually — **install** (not just package), then run only `ui`:

```bat
mvnw -pl ui -am install -DskipTests
mvnw -pl ui exec:java
```

Window: 512×512 elevation raster (seed 0, Step 0) + **Advance**. Status **Working...** while compute runs off the EDT. Do not construct `JFrame` in tests.

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md)
