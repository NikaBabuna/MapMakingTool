<!--
  File: ui/web/src/components/README.md
  Purpose: Door to the web front's React components: the map tool and the canvas, menu bar, panels, shortcut list, and terminal it is built from
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Components

Holds the React components of the studio page: `MapTool`, which owns the page's state and loops, and the pieces it renders.

**Paper:** [tool](../../../../docs/architecture/studio/web/tool.md) · [canvas](../../../../docs/architecture/studio/web/canvas.md) · [chrome](../../../../docs/architecture/studio/web/chrome.md) · [terminal](../../../../docs/architecture/studio/web/terminal.md) · **Conventions:** [conventions.md](../../../../docs/architecture/conventions.md)

## Why

Every piece of the screen that needs React lives here, one component per file with its test beside it. The pieces hold no knowledge of the host: `MapTool` makes every request and passes values and callbacks down. Logic that uses no React, such as the host client, the camera, and the declarations of menus and panels, belongs in [lib](../lib/README.md), and the route that renders `MapTool` belongs in [app](../app/README.md).

## How it works

1. `MapTool` restores its flags and sizes with `readFlag` and `readLayout`, then `connect` checks the host and calls `refresh`. A 200 ms poll repeats the health, status, and raster requests, and both apply an answer only under the stale guard of `applyGenRef`. While playing, a second interval posts advances.
2. Every action is a handler inside `MapTool`, such as `handleAdvance`, `handleLayer`, `handleSpeed`, `requestNewWorld`, `handleCell`, and `handleTerminalRun`. `handleMenuAction` maps menu ids to them, and `handleKey`, attached to the window, maps keys to them.
3. `MapTool` renders `MenuBar` from `MENUS`, one `Panel` per declared panel through `renderPanel` and `panelBody`, `MapCanvas` with the raster and the viewport, `Terminal` with `handleTerminalRun`, and `ShortcutsOverlay` when it is open. The splitters call `beginResize`.
4. `MapCanvas` decodes each raster with `decodePackedRaster`, draws it through `paint`, and turns wheel and pointer events into `zoomAt`, `panBy`, and `stageToCell`, reporting back through its `onViewportChange`, `onCell`, and `onStageMetrics` props.
5. `Terminal` runs a line through `run`, which awaits its `onRun` prop, and exposes `TerminalHandle.focus` to `MapTool`.

**Start reading at:** `MapTool` in [MapTool.tsx](MapTool.tsx).

## Depends on

- [lib](../lib/README.md) — the host client (`fetchStatus`, `postAdvance`, and the rest), `decodePackedRaster`, the camera functions, `MENUS`, `PANELS`, the layout functions, and `SHORTCUTS`
- [test](../test/README.md) — the tests beside the components use `fakeHost` and the test setup

## Used by

- [app](../app/README.md) — `HomePage` renders `MapTool`

## Where each step happens

### [Tool](../../../../docs/architecture/studio/web/tool.md)

| Step | Member | File |
|------|--------|------|
| 2. The root restores and writes back its flags and sizes | `readFlag`, `writeFlag`, `MapTool` | [MapTool.tsx](MapTool.tsx) |
| 3. Connecting checks the host's health, then starts the poll | `connect`, `refresh` | [MapTool.tsx](MapTool.tsx) |
| 4. While playing, the play timer posts one advance per period unless busy | `MapTool` (the play interval) | [MapTool.tsx](MapTool.tsx) |
| 5. The first real stage size fits the map; fitting on demand | `fitView`, `resetViewport` | [MapTool.tsx](MapTool.tsx), [MapCanvas.tsx](MapCanvas.tsx) |
| 6. The actions post to the host and apply the answer | `handleAdvance`, `handleAdvanceMany`, `handleLayer`, `handleSpeed`, `requestNewWorld`, `doNewWorld`, `handleRestartEngine`, `handleCell`, `handleTerminalRun` | [MapTool.tsx](MapTool.tsx) |
| 7. The menu actions, the rails, the layout reset, and the seed | `handleMenuAction`, `togglePanel`, `resetLayout`, `bumpSpeed`, `randomSeed`, `copySeed` | [MapTool.tsx](MapTool.tsx) |
| 8. The key handler, ignored while typing in a field | `handleKey`, `isTypingTarget` | [MapTool.tsx](MapTool.tsx) |
| 9. The root renders the whole screen and formats the rail | `MapTool`, `renderPanel`, `panelBody`, `beginResize`, `formatNs`, `formatBytes`, `formatDiag`, `formatStepRate`, `LAYERS`, `SPEEDS`, `PERF_ROWS` | [MapTool.tsx](MapTool.tsx) |

Step 1 is in [app](../app/README.md).

### [Canvas](../../../../docs/architecture/studio/web/canvas.md)

| Step | Member | File |
|------|--------|------|
| 1. A new raster is decoded into the source canvas, then the view is repainted and the sizes reported | `MapCanvas`, `reportMetrics` | [MapCanvas.tsx](MapCanvas.tsx) |
| 2. The resize observer records and reports the stage size, and repaints | `sync`, `reportMetrics` | [MapCanvas.tsx](MapCanvas.tsx) |
| 3. Paint draws the three copies through the camera transform | `paint` | [MapCanvas.tsx](MapCanvas.tsx) |
| 4. Wheel, drag, click, and hover become zoom, pan, inspection, and the corner readout | `MapCanvas` (the stage's wheel and pointer handlers), `stagePoint`, `updateHover` | [MapCanvas.tsx](MapCanvas.tsx) |
| 5. The layer switch | `MapCanvas` (the layer buttons) | [MapCanvas.tsx](MapCanvas.tsx) |
| 6. Resetting the view returns the fitted viewport | `resetViewport` | [MapCanvas.tsx](MapCanvas.tsx) |

### [Chrome](../../../../docs/architecture/studio/web/chrome.md)

| Step | Member | File |
|------|--------|------|
| 2. The menu bar renders the menus and handles clicks, hover, arrows, and Esc | `MenuBar`, `handleKey`, `handlePointerDown`, `step` | [MenuBar.tsx](MenuBar.tsx) |
| 4. A panel renders its header and, when open, its body | `Panel` | [Panel.tsx](Panel.tsx) |
| 6. The shortcut dialog shows the declared rows | `ShortcutsOverlay` | [ShortcutsOverlay.tsx](ShortcutsOverlay.tsx) |

Steps 1, 3, and 5, and the shortcut rows of step 6, are in [lib](../lib/README.md).

### [Terminal](../../../../docs/architecture/studio/web/terminal.md)

| Step | Member | File |
|------|--------|------|
| 1. Enter or Run runs the line and updates the transcript and history | `run`, `HISTORY_CAP`, `LOG_CAP` | [Terminal.tsx](Terminal.tsx) |
| 2. The arrow keys walk the history | `handleKeyDown` | [Terminal.tsx](Terminal.tsx) |
| 3. The transcript scrolls to its end, and shows a hint when empty | `Terminal`, `EMPTY_HINT` | [Terminal.tsx](Terminal.tsx) |
| 4. The root can focus the input; entries are of three kinds | `TerminalHandle`, `TerminalEntry` | [Terminal.tsx](Terminal.tsx) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [MapTool.tsx](MapTool.tsx) | The studio's root: its state, its poll, play, and action loops, its keys, and everything it renders | `MapTool` |
| [MapTool.test.tsx](MapTool.test.tsx) | Proves the studio page against the stand-in host: the style guide's keys, stale answers ignored, and the rails | — |
| [MapCanvas.tsx](MapCanvas.tsx) | Draws the raster with an east–west wrap, and turns wheel, drag, click, and hover into zoom, pan, inspection, and coordinates | `MapCanvas`, `resetViewport` |
| [MenuBar.tsx](MenuBar.tsx) | Renders the declared menus as a menu bar | `MenuBar` |
| [MenuBar.test.tsx](MenuBar.test.tsx) | Proves the menu bar: its menus in order, live items that act, and dim stubs | — |
| [Panel.tsx](Panel.tsx) | Renders one rail panel, with its collapse toggle | `Panel` |
| [Panel.test.tsx](Panel.test.tsx) | Proves that a panel collapses and expands, and that the studio remembers it | — |
| [ShortcutsOverlay.tsx](ShortcutsOverlay.tsx) | Shows the shortcut list in a dialog | `ShortcutsOverlay` |
| [Terminal.tsx](Terminal.tsx) | The always-open command line: running a line, the transcript, and the history | `Terminal`, `TerminalHandle`, `TerminalEntry` |
| [Terminal.test.tsx](Terminal.test.tsx) | Proves the terminal: its prompt, running a line and showing the answer, and its history | — |
