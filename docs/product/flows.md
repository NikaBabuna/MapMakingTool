<!--
  File: docs/product/flows.md
  Purpose: User journeys and interaction flows
  Audience: Agents implementing product behavior
  Update when: Flows are specified or extended per feature
-->

# Product flows

_Status: **See the world** (G-004 / F-018) specified. **G-005 Living map** done (tool UI F-022, console F-023). **G-006** Next front (F-025) under `ui/web/` against MapHost. Explore / Guide / Timeline still await later Goals._

Before production feature code, extend this file per [../process/quality.md](../process/quality.md).

---

## Flow: See the world (elevation + Advance)

**Goal:** User opens a large colored heightmap, steps generation, and sees ridges form. UI stays honest while compute runs.

| Step | Action |
|------|--------|
| 1 | Launch `com.aethelgard.ui.ProductApp` (`run-product.cmd`). Window shows `WorldSpec.VIEW` (512×512, seed 0) at **Step 0** — a dark flat field (zero elevation). |
| 2 | User clicks **Advance**. Status shows **Working...**; the button does not queue extra clicks. Compute runs off the Swing thread. |
| 3 | When the Step settles, the map paints the current layer (ocean + hillshaded height, plates, or overlay). Status returns to `Step n`. Plates have drifted (F-020). Ridges and rifts follow **standing** converge / diverge (F-021). Elevation `< 0` paints ocean. |
| 4 | Repeat Advance to grow suture ridges. No pan/zoom; one pixel per cell. |

**Edges / failures:** Clicks while busy are ignored. Tests never construct `JFrame`. Dump fixture remains `WorldSpec.DEFAULT` 8×8.

---

## Flow: Tool window (layers, play, inspect)

**Goal:** User reads relief and plates, lets time run, reseeds, inspects a cell, and uses the placeholder console.

| Step | Action |
|------|--------|
| 1 | Launch `ProductApp`. Dark window at `WorldSpec.VIEW` (512×512, seed 0), Step 0, **Elevation** layer, paused. Legend shows ocean / low / high. |
| 2 | Switch **Plates** or **Overlay**. The world does not advance. Overlay darkens east/south plate contacts. |
| 3 | **Play** (Slow / Normal / Fast) or **Advance**. Status is **Working...** while a Step is in flight; extra ticks are ignored. **Pause** stops the scheduler. |
| 4 | Click a cell. **Inspect** shows x, y, elevation, plate id, velocity. Legend follows the current layer. |
| 5 | Change **Seed** and **New world**. Session restarts at Step 0. Ignored while busy. |
| 6 | Type a placeholder line in **Console** (`status`, `advance`, `dump`, `at X Y`, `layers`) and **Run**. Same dispatcher as headless CLI. Map refreshes after `advance`. |

**Edges / failures:** Invalid seed text keeps the previous seed. Console unknown verbs print `error:`. No pan/zoom. Tests never construct `JFrame`.

---

## Flow: Next tool (F-025)

**Goal:** User runs the elevated Next map tool against a warm Java `MapHost`, with hot reload on the front.

| Step | Action |
|------|--------|
| 1 | Start `MapHostApp` (port 7420). In `ui/web`, `npm run dev`. Open http://localhost:3000. |
| 2 | **Aethelgard** tool shows VIEW 512×512. Switch layers, **Advance**, **Play**/Pause (client timer → `/api/advance`), change speed, reseed, inspect, Console. |
| 3 | While busy, status is **Working...**; Advance and New world do not queue. |

**Edges / failures:** Host offline shows a banner. Default host URL `NEXT_PUBLIC_MAP_HOST` = `http://127.0.0.1:7420`.

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
