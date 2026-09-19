<!--
  File: docs/product/flows.md
  Purpose: User journeys and interaction flows
  Audience: Agents implementing product behavior
  Update when: Flows are specified or extended per feature
-->

# Product flows

_Status: **G-006** done — Tauri + Next + MapHost; Swing removed. Explore / Guide / Timeline still await later Goals._

Before production feature code, extend this file per [../process/quality.md](../process/quality.md).

---

## Flow: See the world (elevation + Advance)

**Goal:** User opens a large colored heightmap, steps generation, and sees ridges form. UI stays honest while compute runs.

| Step | Action |
|------|--------|
| 1 | Launch via `run-product.cmd` (MapHost + Next + Tauri). Window shows `WorldSpec.VIEW` (512×512, seed 0) at **Step 0**. |
| 2 | User clicks **Advance**. Status shows **Working...**; extra Advances are ignored while busy. |
| 3 | When the Step settles, the map paints the current layer. Status returns to `Step n`. |
| 4 | Repeat Advance. No pan/zoom; one pixel per cell. |

**Edges / failures:** Clicks while busy are ignored. Tests never construct `JFrame`. Dump fixture remains `WorldSpec.DEFAULT` 8×8.

---

## Flow: Tool window (layers, play, inspect)

**Goal:** User reads relief and plates, lets time run, reseeds, inspects a cell, and uses the placeholder console.

| Step | Action |
|------|--------|
| 1 | Launch desktop/Next tool. Dark elevated chrome at VIEW, Step 0, **Elevation**, paused. |
| 2 | Switch **Plates** or **Overlay**. The world does not advance. |
| 3 | **Play** (client timer → `/api/advance`) or **Advance**. Status **Working...** while busy. **Pause** stops the timer. |
| 4 | Click a cell. **Inspect** shows x, y, elevation, plate id, velocity. Legend follows the layer. |
| 5 | Change **Seed** and **New world**. Ignored while busy. |
| 6 | **Console** placeholder verbs via `CommandDispatch`. |

**Edges / failures:** Invalid seed text keeps the previous seed. Console unknown verbs print `error:`. No pan/zoom. Tests never construct `JFrame`.

---

## Flow: Next tool (F-025) / Desktop (F-026)

**Goal:** User runs the elevated Next map tool against a warm Java `MapHost`, with hot reload on the front.

| Step | Action |
|------|--------|
| 1 | Start `MapHostApp` (port 7420). In `ui/web`, `npm run dev`. Open http://localhost:3000. |
| 2 | **Aethelgard** tool shows VIEW 512×512. Switch layers, **Advance**, **Play**/Pause (client timer → `/api/advance`), change speed, reseed, inspect, Console. |
| 3 | While busy, status is **Working...**; Advance and New world do not queue. |

**Edges / failures:** Host offline shows a banner. Default host URL `NEXT_PUBLIC_MAP_HOST` = `http://127.0.0.1:7420`. Preferred: `run-product.cmd` (starts Next + Tauri; Tauri spawns MapHost).

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

**Goal:** User inspects how the world formed over simulated time.

| Step | _(to be specified)_ |
|------|---------------------|
| 1 | |
| 2 | |

**Edges / failures:** _(to be specified)_

---

## Flow: Inspect (why is this here)

**Goal:** User selects a map point and traces causal history.

| Step | _(to be specified)_ |
|------|---------------------|
| 1 | |

**Edges / failures:** _(to be specified)_
