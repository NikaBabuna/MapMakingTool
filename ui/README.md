<!--
  File: ui/README.md
  Purpose: Landmark index for the UI Maven module
  Audience: Agents and humans
  Update when: UI layout or usage changes
-->

# UI module

Maven artifact `com.aethelgard:ui` — Aethelgard map view of product values.

**Depends on:** `product` and `cli` (ADR-010 — `cli` only for the console dispatcher). Never depended on by `engine` or `product`.

## Headless logic

`MapController` — `ProductSession`, layers, Advance / Play, `newWorld`, inspect, legend, `runCommand`, busy status. **No Swing.** Covered by tests.

`ElevationRaster` — packed RGB for Elevation (ocean + hillshade), Plates, and Overlay (F-022 formulas).

## Localhost host (F-024)

`com.aethelgard.ui.host.MapHost` — HTTP on `127.0.0.1` over `MapController`. Entry: `MapHostApp` (port 7420).

```bat
mvnw -pl ui -am install -DskipTests
mvnw -pl ui exec:java -Dexec.mainClass=com.aethelgard.ui.host.MapHostApp
```

Routes: `/health`, `/api/status`, `/api/raster`, `/api/advance`, `/api/play`, `/api/pause`, `/api/layer`, `/api/speed`, `/api/new-world`, `/api/inspect`, `/api/command`. See [docs/product/architecture.md](../docs/product/architecture.md).

## Next.js front (F-025)

Tool UI lives in **`ui/web/`** ([`web/`](web/) — not a repo-root app). Warm host + `npm run dev` — see [web/README.md](web/README.md).

## Interactive (Swing — until F-026)

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

Window: dark 512×512 tool (seed 0, Step 0) with layers, Advance, Play/Pause, speed, seed + New world, inspect, legend, **Console**. Status **Working...** while compute runs off the EDT. Console uses `cli` `CommandDispatch` on the same session. Do not construct `JFrame` in tests.

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md)
