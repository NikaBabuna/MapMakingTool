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

`MapController` — `ProductSession`, layers, Advance / Play, `newWorld`, inspect, legend, busy status. **No Swing.** Covered by tests.

`ElevationRaster` — packed RGB for Elevation (ocean + hillshade), Plates, and Overlay (F-022 formulas).

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

Window: dark 512×512 tool (seed 0, Step 0) with layers, Advance, Play/Pause, speed, seed + New world, inspect, legend. Status **Working...** while compute runs off the EDT. Console is F-023. Do not construct `JFrame` in tests.

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md)
