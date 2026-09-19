<!--
  File: docs/project/goals/G-006-webview-front.md
  Purpose: Multi-session Goal — Tauri webview + Next.js front + Java session host
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-006 — Local webview front

**Status:** `in progress`  
**Engine:** do not edit `engine` source for ordinary feature growth (G-002 host ports).  
**Prior:** [G-005](G-005-living-map.md) delivered a living map in Swing with `ProductSession`, tool UI, and placeholder console. This Goal moves the **front** to a local webview app without changing world rules.

**Approved:** 2026-09-19 (user). Stack: **Tauri 2** shell + **Next.js** UI + **Java HTTP** host over `ProductSession`. Parity with today’s tool UI — not a redesign.

---

## Result we want

Launch a **desktop webview** that shows the same living map tool: layers, play, inspect, console, busy status. Edit the front with hot reload without restarting the simulation host. Simulation stays Java.

When this Goal is `done`:

1. **House.** Java owns one `ProductSession` (serialized advances) behind a **localhost HTTP** facade. Next.js is the tool UI. Tauri opens a webview and starts/stops the Java host. `product` / `engine` have no React, Tauri, or HTTP UI deps. Product still has no Swing.
2. **API parity.** Host exposes MapController-equivalent ops: status (step, seed/spec, layer, speed, playing, busy, status text); set layer/speed; play/pause; advance; newWorld(seed); inspect(x,y); console line via existing `CommandDispatch`; RGB raster for the current layer (same `ElevationRaster` formulas). Busy ignores further advances.
3. **Next tool UI.** Visual/behavior parity with F-022/F-023 (dark tool, layers Elevation / Plates / Overlay, Advance, Play/Pause, speed, seed + New world, inspect, legend, Console). Play may be client-timed calls to advance. No pan/zoom required.
4. **Tauri shell.** One launch path starts host + webview. Dev: warm Java host + Next hot reload.
5. **Swing demoted.** Swing map is no longer the primary product entry. Headless `MapController` / raster witnesses remain the behavioral bar the host must match. No `JFrame` in tests.
6. **Unchanged world.** Same seed + size + N Steps → identical fields. `WorldSpec.DEFAULT` / `VIEW` geometry unchanged. Placeholder CLI verbs stay thin. Incremental suite green. No `engine` production edits.

Plain English: the map is still the same world; the window is a local website in a desktop shell, talking to Java over localhost.

---

## Out of scope (this Goal)

- Climate, biomes, continent redesign, timeline / Guide flows
- Visual brand redesign beyond parity
- Multiplayer, remote host, auth
- Deep IPC redesign (localhost HTTP is enough)
- A finished command language
- Engine ports / non-finishing Systems / Delete Request
- Changing `WorldSpec.DEFAULT` geometry

If a stored FR cannot be met without an engine port: **stop**, ADR, do not sneak the change into this Goal.

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Shell | Tauri 2 — window; spawn/stop Java host; load UI |
| Front | Next.js (local SPA / App Router OK) |
| Host | Java HTTP over session + map logic; Maven-witnessed |
| Live access | Localhost HTTP amends ADR-010 “no socket” (ADR-011) |
| Commands | Still placeholder `cli` dispatch — not product physics |
| Parity | Match F-022/F-023 tool behavior; no redesign |
| Engine | No `engine` source edits for ordinary feature growth |

---

## Product / adapter claims (tests by Goal end)

- [x] Java HTTP host matches MapController semantics; witnessed without Tauri/browser
- [ ] Next tool UI parity against that host (layers, play, inspect, console, busy)
- [ ] Tauri launch starts host + webview; documented launch path
- [ ] Swing is not the primary map entry
- [ ] Same seed + spec + N Steps → identical fields; no `engine` production edits
- [ ] Incremental suite: all prior Accepted Step tests remain green

---

## Planned Steps

Registered in [../features.md](../features.md). Accept is **incremental**. Do not start code until that Step’s job + FRs are approved and stored.

| Step | Intent | Status |
|------|--------|--------|
| F-024 | Java session HTTP host (MapController parity); no Next/Tauri yet | done |
| F-025 | Next.js tool UI parity against host | not started |
| F-026 | Tauri shell + launch path; demote Swing; close G-006 | not started |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 1 / 3 |
| Claim boxes | 0 / 6 |
| Last Accept | F-024 |

ADR: [ADR-011](../decisions.md)
