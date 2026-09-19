<!--
  File: docs/product/flows.md
  Purpose: User journeys and interaction flows
  Audience: Agents implementing product behavior
  Update when: Flows are specified or extended per feature
-->

# Product flows

_Status: **G-007** done (studio QoL). **G-008** in progress — VIEW **1920×1080** (F-031); tectonics still pre-boundary until later Steps._

Before production feature code, extend this file per [../process/quality.md](../process/quality.md).

---

## G-008 intent (planned)

| Area | Intent | Steps |
|------|--------|-------|
| World | VIEW **1920×1080**, torus loop | F-031–F-032 |
| Plates | Boundary tectonics, fission, edge-driven motion | F-033–F-038 |
| Camera | Zoom clamp (fit min); loopback pan | **F-032 done** |
| Studio | Multi-panel, mappy style, traditional terminal console | F-039–F-040 |

Domain locks: [wiki/tectonics.md](wiki/tectonics.md).

---

## Flow: See the world (elevation + Advance)

**Goal:** User opens a large colored heightmap, steps generation, and sees ridges form. UI stays honest while compute runs.

| Step | Action |
|------|--------|
| 1 | Launch via `run-product.cmd` (MapHost + Next + Tauri). Studio tool shows `WorldSpec.VIEW` (**1920×1080**, seed 0) at **Step 0**. |
| 2 | User clicks **Advance**. Status shows **Working...**; map busy overlay; extra Advances are ignored while busy. |
| 3 | When the Step settles, the map paints the current layer. Status returns to `Step n`. |
| 4 | Repeat Advance. **Pan loops** (torus); zoom in; cannot zoom out past fit-map (F-032). |

**Edges / failures:** Clicks while busy are ignored for Advance/New world. Tests never construct `JFrame`. Dump fixture remains `WorldSpec.DEFAULT` 8×8.

---

## Flow: Studio tool (layers, play, inspect, dock)

**Goal:** User reads relief and plates in a map-first studio, lets time run, reseeds, inspects a cell, and opens the console on demand.

| Step | Action |
|------|--------|
| 1 | Launch desktop/Next tool. Thin top bar; full-bleed map; **Elevation**, paused; dock open (Inspect + Legend). |
| 2 | Switch **Plates** or **Overlay**. The world does not advance. |
| 3 | **Play** (client timer → `/api/advance`) or **Advance**. Status **Working...** while busy. **Pause** stops the timer. |
| 4 | Click a cell. **Inspect** in the dock shows x, y, elevation, plate id, velocity. Legend follows the layer. |
| 5 | **Dock** toggles the right panel (`localStorage` `aethelgard.dockOpen`). **Console** opens the bottom drawer. |
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
