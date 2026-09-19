<!--
  File: docs/product/flows.md
  Purpose: User journeys and interaction flows
  Audience: Agents implementing product behavior
  Update when: Flows are specified or extended per feature
-->

# Product flows

_Status: **G-007** done. **G-008** done (through **F-040** traditional console)._

Before production feature code, extend this file per [../process/quality.md](../process/quality.md).

---

## G-009 simulation runner (planned)

| Area | Intent | Steps |
|------|--------|-------|
| Docs | Diverge ridge; sphere poles; commands; observability | **F-041** done |
| Diagnostics | Controllable hub + first collectors + CLI | **F-042** done |
| Physics | Diverge fill; slivers/borders; sphere wrap | **F-043–F-044** done; F-045 |
| Perf | Step path + raster/host memory | F-046–F-047 |
| Control | Shared commands; CLI; rebuilt terminal | F-048–F-050 |
| Studio | Runner chrome; perf panels; UX; Goal close | F-051–F-054 |

Domain locks: [wiki/tectonics.md](wiki/tectonics.md) · [ADR-012](../project/decisions.md).

### Flow: Diagnostics (F-042)

| Step | Action |
|------|--------|
| 1 | Advance the session; hub records `advance.wall` / heap samples when enabled. |
| 2 | `stats` prints collector summaries; `diag list` shows enable + n/capacity. |
| 3 | `diag off advance.wall` stops new samples; `diag on` resumes; `diag clear` clears. |
| 4 | Map paint records `paint.wall` on the same hub. |

---

## G-008 (shipped)

| Area | Intent | Steps |
|------|--------|-------|
| World | VIEW **1920×1080**, cylinder loop | F-031–F-032 |
| Plates | Partition + boundaries/flux/fission/motion/orogeny | **F-033–F-038** |
| Camera | Zoom clamp; X loop + Y polar clamp | **F-038** |
| Studio | Multi-panel + mappy style | **F-039** |
| Console | Traditional terminal; Goal close | **F-040** |

Domain locks: [wiki/tectonics.md](wiki/tectonics.md).

---

## Flow: See the world (elevation + Advance)

**Goal:** User opens a large colored heightmap, steps generation, and sees ridges form. UI stays honest while compute runs.

| Step | Action |
|------|--------|
| 1 | Launch via `run-product.cmd` (MapHost + Next + Tauri). Studio tool shows `WorldSpec.VIEW` (**1920×1080**, seed 0) at **Step 0**. |
| 2 | User clicks **Advance**. Status shows **Working...**; map busy overlay; extra Advances are ignored while busy. |
| 3 | When the Step settles, the map paints the current layer. Status returns to `Step n`. |
| 4 | Repeat Advance. **Pan** wraps on X; vertical pan when zoomed, clamped at poles; zoom in; cannot zoom out past fit-map. |

**Edges / failures:** Clicks while busy are ignored for Advance/New world. Tests never construct `JFrame`. Dump fixture remains `WorldSpec.DEFAULT` 8×8.

---

## Flow: Studio tool (layers, play, inspect, dock)

**Goal:** User reads relief and plates in a map-first multi-panel studio, lets time run, reseeds, inspects a cell, and opens a traditional terminal console on demand.

| Step | Action |
|------|--------|
| 1 | Launch desktop/Next tool. Thin top bar; full-bleed map with neatline; **Elevation**, paused; right rail open with **Inspect** + **Legend** panel cards. |
| 2 | Switch **Plates** or **Overlay**. The world does not advance. |
| 3 | **Play** (client timer → `/api/advance`) or **Advance**. Status **Working...** while busy. **Pause** stops the timer. |
| 4 | Click a cell. **Inspect** panel shows x, y, elevation, plate id, velocity. Legend follows the layer. Collapse panel bodies independently. |
| 5 | **Dock** toggles the right rail (`localStorage` `aethelgard.dockOpen`). Panel bodies use `aethelgard.panelInspectOpen` / `panelLegendOpen`. **Console** opens the terminal drawer (`aethelgard>` prompt; ↑/↓ history). |
| 6 | Change **Seed**, **Random**, or **New world** (confirm when Step > 0). Ignored while busy. |
| 7 | Shortcuts: Space Play; `A`/`.` Advance; `1`–`3` layers; `[`/`]` speed; `N` New world; `` ` ``/`C` console; `D` dock; `R` reset view. |
| 8 | Console placeholder verbs via `CommandDispatch`. |

**Edges / failures:** Invalid seed text keeps the previous seed. Console unknown verbs print `error:`. Host offline shows banner + **Retry**. Tests never construct `JFrame`.

---

## Flow: Next / Desktop launch

**Goal:** User runs the studio Next map tool against a warm Java `MapHost`, with hot reload on the front.

| Step | Action |
|------|--------|
| 1 | Run `run-product.cmd`. Tauri starts Next (`beforeDevCommand`), waits for `:3000`, spawns MapHost, opens the window. |
| 2 | **Aethelgard** studio shows VIEW **1920×1080**. Switch layers, **Advance**, **Play**/Pause, speed, reseed, inspect, Console drawer. |
| 3 | While busy, status is **Working...**; Advance and New world do not queue. |

**Edges / failures:** Host offline shows a banner + Retry. Default host URL `NEXT_PUBLIC_MAP_HOST` = `http://127.0.0.1:7420`. If you see “Waiting for your frontend dev server…”, Next failed to start — check `ui/web` deps (`npm install`) and that port 3000 is free.

---

## Flow: Explore (generate and accept)

**Goal:** User rolls seeds until a world feels right.

| Step | _(to be specified)_ |
|------|---------------------|
| 1 | |
| 2 | |

**Edges / failures:** _(to be specified)_

---

## Flow: Guide (nudge and simulate)

**Goal:** User constrains specific features; simulation maintains consistency elsewhere.

| Step | _(to be specified)_ |
|------|---------------------|
| 1 | |
| 2 | |

**Edges / failures:** _(to be specified)_

---

## Flow: Timeline (scrub history)

**Goal:** User scrubs how the world formed.

| Step | _(to be specified)_ |
|------|---------------------|
| 1 | |
| 2 | |

**Edges / failures:** _(to be specified)_
