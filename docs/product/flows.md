<!--
  File: docs/product/flows.md
  Purpose: User journeys and interaction flows
  Audience: Agents implementing product behavior
  Update when: Flows are specified or extended per feature
-->

# Product flows

_Status: **See the world** (G-004 / F-018) is specified. Detailed Explore / Guide / Timeline / Inspect steps await later Goals._

Before production feature code, extend this file per [../process/quality.md](../process/quality.md).

---

## Flow: See the world (elevation + Advance)

**Goal:** User opens a large colored heightmap, steps generation, and sees ridges form. UI stays honest while compute runs.

| Step | Action |
|------|--------|
| 1 | Launch `ProductApp`. Window shows `WorldSpec.VIEW` (512×512, seed 0) at **Step 0** — a dark flat field (zero elevation). |
| 2 | User clicks **Advance**. Status shows **Working...**; the button does not queue extra clicks. Compute runs off the Swing thread. |
| 3 | When the Step settles, the map paints the new elevation raster (absolute height ramp) and status returns to `Step n`. |
| 4 | Repeat Advance to grow suture ridges. No pan/zoom; one pixel per cell. |

**Edges / failures:** Clicks while busy are ignored. Tests never construct `JFrame`. Dump fixture remains `WorldSpec.DEFAULT` 8×8.

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
