<!--
  File: docs/project/goals/G-006-webview-front.md
  Purpose: Multi-session Goal — Tauri webview + Next.js front + Java session host
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-006 — Local webview front

**Status:** `done`  
**Engine:** do not edit `engine` source for ordinary feature growth (G-002 host ports).  
**Prior:** [G-005](G-005-living-map.md) delivered a living map in Swing with `ProductSession`, tool UI, and placeholder console. This Goal moved the **front** to a local webview app without changing world rules.

**Approved:** 2026-09-19 (user). Stack: **Tauri 2** shell + **Next.js** UI + **Java HTTP** host over `ProductSession`. Elevated visuals (F-025); Swing **removed** (F-026).

---

## Result we want

Launch a **desktop webview** that shows the living map tool: layers, play, inspect, console, busy status. Edit the front with hot reload without restarting the simulation host. Simulation stays Java.

When this Goal is `done`:

1. **House.** Java owns one `ProductSession` (serialized advances) behind a **localhost HTTP** facade. Next.js is the tool UI. Tauri opens a webview and starts/stops the Java host. `product` / `engine` have no React, Tauri, or HTTP UI deps. Product still has no Swing.
2. **API parity.** Host exposes MapController-equivalent ops: status; set layer/speed; play/pause; advance; newWorld; inspect; console via `CommandDispatch`; RGB raster (`ElevationRaster`). Busy ignores further advances.
3. **Next tool UI.** Elevated dark tool under `ui/web/` with layers, Advance, Play/Pause, speed, seed + New world, inspect, legend, Console. Client-timed Play.
4. **Tauri shell.** `ui/desktop/` starts host + webview; `run-product.cmd` is primary launch. Dev: Next hot reload + Tauri.
5. **Swing removed.** No Swing interactive types in `ui` main. Headless `MapController` / raster / `MapHost` remain. No `JFrame` in tests.
6. **Unchanged world.** Same seed + size + N Steps → identical fields. Incremental suite green. No `engine` production edits.

Plain English: the map is the same world; the window is a local website in a desktop shell, talking to Java over localhost.

---

## Out of scope (this Goal)

- Climate, biomes, continent redesign, timeline / Guide flows
- Multiplayer, remote host, auth
- Deep IPC redesign (localhost HTTP is enough)
- A finished command language
- Engine ports / non-finishing Systems / Delete Request
- Changing `WorldSpec.DEFAULT` geometry

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Shell | Tauri 2 under `ui/desktop/` |
| Front | Next.js under `ui/web/` |
| Host | Java HTTP `MapHost` in `ui` |
| Live access | Localhost HTTP (ADR-011) |
| Swing | **Removed** (F-026) — not demoted |
| Engine | No `engine` source edits for ordinary feature growth |

---

## Product / adapter claims (tests by Goal end)

- [x] Java HTTP host matches MapController semantics; witnessed without Tauri/browser
- [x] Next tool UI parity against that host (layers, play, inspect, console, busy)
- [x] Tauri launch starts host + webview; documented launch path
- [x] Swing interactive UI removed; not the product entry
- [x] Same seed + spec + N Steps → identical fields; no `engine` production edits
- [x] Incremental suite: all prior Accepted Step tests remain green

---

## Planned Steps

| Step | Intent | Status |
|------|--------|--------|
| F-024 | Java session HTTP host (MapController parity); no Next/Tauri yet | done |
| F-025 | Next.js tool UI against host | done |
| F-026 | Tauri shell; remove Swing; close G-006 | done |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 3 / 3 |
| Claim boxes | 6 / 6 |
| Last Accept | F-026 |

ADR: [ADR-011](../decisions.md)
