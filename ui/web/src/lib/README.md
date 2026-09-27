<!--
  File: ui/web/src/lib/README.md
  Purpose: Door to the web front's logic that uses no React: the host client, the raster decoder, the camera, and the declarations of menus, panels, layout, and shortcuts
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Lib

Holds the web front's logic that uses no React: the typed client of the map host, the raster decoder, the camera, and the menus, panels, layout, and shortcuts declared as data.

**Paper:** [client](../../../../docs/architecture/studio/web/client.md) · [viewport](../../../../docs/architecture/studio/web/viewport.md) · [chrome](../../../../docs/architecture/studio/web/chrome.md) · **Conventions:** [conventions.md](../../../../docs/architecture/conventions.md)

## Why

Code that needs no React is kept apart from the components so it can be tested as plain functions and reused by any component, one concern per file, each test beside its file. The camera's functions are pure, and the declarations are data, so changing a menu or a panel is an edit to a list here, not to a component. Rendering belongs in [components](../components/README.md), and the stand-in host the tests use belongs in [test](../test/README.md).

## How it works

- `host.ts` resolves the host address with `hostBase` and has one function per route, `fetchStatus`, `fetchRaster`, `fetchHealth`, and the `post…` functions, which parse answers through `readJson` into `HostStatus` or `CommandResult`.
- `raster.ts` turns the raster bytes into an `ImageData` with `decodePackedRaster`, and a packed colour into CSS text with `rgbCss`.
- `viewport.ts` is the camera: a `Viewport` of scale and translation, `fittedViewport` built on `fitScale` and `centeredViewport`, and `zoomAt` and `panBy` built on `clampScale`, `wrapPan`, and `clampVertical`. `stageToCell` turns a stage point into a map cell.
- `menus.ts`, `panels.ts`, and `shortcuts.ts` declare `MENUS`, `PANELS`, and `SHORTCUTS`. `panels.ts` also lists a rail's panels with `panelsFor` and names their storage keys.
- `layout.ts` declares the resizable regions, `DEFAULT_LAYOUT` and `LAYOUT_LIMITS`, and reads, clamps, writes, and clears their stored sizes.

**Start reading at:** `fetchStatus` in [host.ts](host.ts).

## Depends on

- nothing in this repository

## Used by

- [components](../components/README.md) — `MapTool` calls the host client, the camera, and the layout functions, and renders the declarations; `MapCanvas` decodes rasters and applies the camera; `Terminal` uses `CommandResult`
- [test](../test/README.md) — `fakeHost` answers with the `HostStatus` shape declared here

## Where each step happens

### [Client](../../../../docs/architecture/studio/web/client.md)

| Step | Member | File |
|------|--------|------|
| 1. The host address is resolved | `hostBase`, `DEFAULT_HOST` | [host.ts](host.ts) |
| 2. Each request builds its URL, sends it, and parses the answer | `fetchStatus`, `fetchRaster`, `fetchHealth`, `postAdvance`, `postLayer`, `postSpeed`, `postNewWorld`, `postRestartEngine`, `postInspect`, `postCommand`, `readJson` | [host.ts](host.ts) |
| 3. Decoding reads the header, checks the length, and fills an image | `decodePackedRaster` | [raster.ts](raster.ts) |
| 4. A packed colour as CSS text | `rgbCss` | [raster.ts](raster.ts) |

Step 5, the Next.js configuration, is in the [web front's folder](../../README.md).

### [Viewport](../../../../docs/architecture/studio/web/viewport.md)

| Step | Member | File |
|------|--------|------|
| 1. The fit scale, and the fitted viewport | `fitScale`, `fittedViewport`, `centeredViewport` | [viewport.ts](viewport.ts) |
| 2. A scale is bounded | `clampScale`, `MAX_SCALE` | [viewport.ts](viewport.ts) |
| 3. Wrapping $t_x$ | `wrapPan`, `floorMod` | [viewport.ts](viewport.ts) |
| 4. The vertical clamp | `clampVertical`, `lockVertical` | [viewport.ts](viewport.ts) |
| 5. Zoom at a point, and pan | `zoomAt`, `panBy` | [viewport.ts](viewport.ts) |
| 6. Stage to cell | `stageToCell` | [viewport.ts](viewport.ts) |
| 7. The identity viewport, and the unused CSS transform | `IDENTITY_VIEWPORT`, `cssTransform` | [viewport.ts](viewport.ts) |

### [Chrome](../../../../docs/architecture/studio/web/chrome.md)

| Step | Member | File |
|------|--------|------|
| 1. The menus are declared | `MENUS`, `MenuDescriptor`, `MenuItem`, `MenuActionId` | [menus.ts](menus.ts) |
| 3. The panels are declared, listed per rail, and keyed | `PANELS`, `PanelDescriptor`, `PanelDock`, `panelsFor`, `panelOpenKey`, `defaultPanelOpen` | [panels.ts](panels.ts) |
| 5. The regions are declared, and their sizes read, clamped, written, and cleared | `DEFAULT_LAYOUT`, `LAYOUT_LIMITS`, `LAYOUT_KEY_PREFIX`, `RAIL_OPEN_KEYS`, `layoutKey`, `clampSize`, `readLayout`, `writeLayout`, `clearLayout` | [layout.ts](layout.ts) |
| 6. The shortcut rows are declared | `SHORTCUTS`, `Shortcut` | [shortcuts.ts](shortcuts.ts) |

Steps 2 and 4, and the dialog of step 6, are in [components](../components/README.md).

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [host.ts](host.ts) | The typed client of the map host: one function per route, and the host address | `HostStatus`, `CommandResult`, `fetchStatus`, `postCommand`, `hostBase` |
| [host.test.ts](host.test.ts) | Proves what the client returns, the routes it calls, and the host address rule | — |
| [raster.ts](raster.ts) | Decodes the host's packed raster into an image, and writes a packed colour as CSS | `decodePackedRaster`, `rgbCss` |
| [raster.test.ts](raster.test.ts) | Proves that a decoded raster's pixels equal the host's packed colours | — |
| [viewport.ts](viewport.ts) | The camera: fit, zoom around a point, pan with an east–west wrap and a north–south clamp, and stage points to map cells | `Viewport`, `zoomAt`, `panBy`, `stageToCell` |
| [viewport.test.ts](viewport.test.ts) | Proves how the camera behaves: fit, zoom limits toward the pointer, east–west wrap, and the vertical clamp | — |
| [layout.ts](layout.ts) | The resizable regions and the rail flags, with their bounds and stored values | `LayoutSizes`, `clampSize`, `readLayout`, `writeLayout` |
| [layout.test.ts](layout.test.ts) | Proves that sizes stay within their limits, survive a reload, and reset | — |
| [menus.ts](menus.ts) | The menus and their items, declared as data | `MENUS`, `MenuDescriptor` |
| [panels.ts](panels.ts) | The rail panels, declared as data, with their order and storage keys | `PANELS`, `panelsFor` |
| [shortcuts.ts](shortcuts.ts) | The shortcut rows the shortcut dialog shows | `SHORTCUTS` |
