<!--
  File: ui/README.md
  Purpose: Landmark index for the UI Maven module
  Audience: Agents and humans
  Update when: UI layout or usage changes
-->

# UI module

Maven artifact `com.aethelgard:ui` — headless map logic + localhost `MapHost`. Interactive UI is **Next** (`web/`) + **Tauri** (`desktop/`). Swing was removed in F-026.

**Depends on:** `product` and `cli` (ADR-010 — `cli` only for the console dispatcher). Never depended on by `engine` or `product`.

## Headless logic

`MapController` — `ProductSession`, layers, Advance / Play, `newWorld`, `restartEngine`, inspect, legend, `runCommand`, busy status. **No Swing.** Covered by tests.

`ElevationRaster` — packed RGB for Elevation (ocean + hillshade), Plates, and Overlay (F-022 formulas).

## Localhost host (F-024)

`com.aethelgard.ui.host.MapHost` — HTTP on `127.0.0.1` over `MapController`. Entry: `MapHostApp` (port 7420).

```bat
mvnw -pl ui -am install -DskipTests
mvnw -pl ui exec:java -Dexec.mainClass=com.aethelgard.ui.host.MapHostApp
```

## Next.js front (F-025)

Tool UI lives in **`ui/web/`** — see [web/README.md](web/README.md).

## Desktop shell (F-026)

Tauri 2 lives in **`ui/desktop/`** — see [desktop/README.md](desktop/README.md).

Primary launch from repo root:

```bat
run-product.cmd
```

Docs: [docs/architecture/studio/README.md](../docs/architecture/studio/README.md)
