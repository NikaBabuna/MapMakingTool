<!--
  File: docs/product/flows.md
  Purpose: User journeys and interaction flows
  Audience: Agents implementing product behavior
  Update when: Flows are specified or extended per feature
-->

# Product flows

_Status: **G-007** done. **G-008** done. **G-009** **done** (through **F-054**). **G-010** **in progress** (**F-058** buoyancy live)._

Before production feature code, extend this file per [../process/quality.md](../process/quality.md).

---

## G-009 simulation runner (shipped)

| Area | Intent | Steps |
|------|--------|-------|
| Docs | Diverge ridge; sphere poles; commands; observability | **F-041** done |
| Diagnostics | Controllable hub + first collectors + CLI | **F-042** done |
| Physics | Diverge fill; slivers/borders; sphere wrap | **F-043–F-045** done |
| Perf | Step path + raster/host memory | **F-046** / **F-047** done |
| Control | Shared commands; CLI full runner; rebuilt terminal | **F-048**–**F-050** done |
| Studio | Runner chrome; perf panels; UI infrastructure | **F-051**–**F-053** done |
| Close | Layer harden + doc hygiene; Goal seal | **F-054** done |

## G-010 crust topology (planned)

| Area | Intent | Steps |
|------|--------|-------|
| Docs | Keys + lockers; ride; ridge mint; buoyancy; suture; isostasy | **F-055** done |
| Ride | Occupancy keys + thickness lockers; elevation from isostasy | **F-056** done |
| Ridge | Thin oceanic mint in gaps | **F-057** done |
| Buoyancy | Ocean subducts; continent does not die by area; SEPARATE does not copy the border locker | **F-058** done |
| Margins | Rift trough + collide slope + lip blend | F-059 |
| Continents | Arc + suture + cap | F-060 |
| Close | Dump/wiki/UI hygiene; Goal seal | F-061 |

Runtime: occupancy remaps with plates; locker stamps ride; **gaps and SEPARATE copies mint thin ocean**; COLLIDE buoyancy (\(T_{land}=16\)); elevation is isostasy. Map still paints `elevation`. Simulation menu: Restart UI / Restart engine.

### Flow: Crust ride (F-056)

| Step | Action |
|------|--------|
| 1 | Step 0: occupancy is one locker per cell; all thickness \(T_{ocean}=8\); elevation 0. |
| 2 | Advance. Contact stamps thicken lockers; occupancy remaps with plate motion; elevation is isostasy of thickness at the new keys. |
| 3 | Interior crust is at the plate’s new cells, not left on last Step’s contact coordinates. |

### Flow: Ridge mint (F-057)

| Step | Action |
|------|--------|
| 1 | Plates diverge. Advection leaves some cells with zero unique claimants. |
| 2 | Plate-id flood still assigns those cells to bordering plates (F-044). |
| 3 | `RidgeCreate` mints **new** lockers at \(T_{ocean}=8\) in raster order. Elevation at the rift is 0, not a copied mountain. |

### Flow: Buoyancy + rift unshare (F-058)

| Step | Action |
|------|--------|
| 1 | At COLLIDE, thickness \(\ge 16\) is continental. Oceanic contact cells subduct even if that plate is larger. |
| 2 | Consumed ocean occupancy becomes the surviving side’s locker. Continent–continent does not sink or stamp. |
| 3 | SEPARATE copies of the standing contact locker are marked unresolved and minted as thin ocean (the stripe hole). |

### Flow: Restart UI / engine (F-057)

| Step | Action |
|------|--------|
| 1 | **Simulation → Restart UI** reloads the Next page. The Java session keeps its current world. |
| 2 | **Simulation → Restart engine** recreates `ProductSession` at Step 0 with the **same seed**, even if Advance is busy. Play pauses. |

### Flow: Raster / host memory (F-047)

| Step | Action |
|------|--------|
| 1 | Advance or set layer → MapController paints into a reused flat buffer; `paint.wall` records. |
| 2 | `GET /api/raster` packs once into a reused `byte[]`; repeat GETs at same step/layer hit cache. |
| 3 | Next advance / layer / newWorld bumps paint generation → cache refills (same allocation if size unchanged). |

### Flow: Shared commands (F-048)

| Step | Action |
|------|--------|
| 1 | Type `help` or `list pool` / `session get` in CLI or console (same dispatcher). |
| 2 | `session advance N` steps time; `pool.<field> get` reads summaries; `systems` / `schema` expose construction. |
| 3 | `diag.<id> on\|off` controls collectors; old flat verbs still work as aliases. |

### Flow: Headless CLI runner (F-049)

| Step | Action |
|------|--------|
| 1 | `Main` / `CliRunner` creates one `ProductSession` (`--seed` or default 0; DEFAULT 8×8). |
| 2 | Optional `--steps N` → `session advance N` then `session get dump` when no `-c`. |
| 3 | Repeatable `-c` / `--command` lines share that session; first failure exits non-zero. |

### Flow: Rebuilt terminal (F-050 / F-052)

| Step | Action |
|------|--------|
| 1 | Terminal is always visible as a bottom panel; `` ` `` / `C` focuses the input. |
| 2 | Empty state shows noun/verb hints; type `help` or `session get`; ↑/↓ history. |
| 3 | Transcript shows `aethelgard>` commands vs results; errors for non-zero exit. Same dispatcher as CLI. |

### Flow: Runner shell (F-051)

| Step | Action |
|------|--------|
| 1 | Top bar: brand + host dot; Play / Pause / Speed; Reset view / World. |
| 2 | World rail shows step, size, seed; Reset world; Inspect/Legend sections. |
| 3 | Layer chips on map top-left; map uses physical atlas colors. |

### Flow: Perf rail (F-052)

| Step | Action |
|------|--------|
| 1 | Left Perf rail lists mean paint/advance/phase/heap from `/api/status` `diag`. |
| 2 | Advance or paint → hub samples update; rail shows averages (or `—` when empty). |
| 3 | No Working… map overlay; busy still disables Reset world / ignores Advance. |
| 4 | Brighter bathymetry + land clamp 64 on Elevation paint. |

### Flow: Chrome infrastructure (F-053)

| Step | Action |
|------|--------|
| 1 | Rails render every panel whose descriptor in `lib/panels.ts` matches the dock; each uses shared `Panel` chrome and remembers its collapse state. |
| 2 | Menu bar row (File · Edit · View · Simulation · Help) comes from `lib/menus.ts`; wired items drive existing actions, stub items are dim and inert. |
| 3 | Drag the gutter beside a rail or above the terminal to resize; sizes clamp, persist, and restore via **View → Reset layout**. |
| 4 | Elevation paint is brighter (documented ocean/land stops); legend swatches read from the same ramps. |

### Flow: Runner QoL (F-053)

| Step | Action |
|------|--------|
| 1 | Press `?` (or Help → Shortcuts…) → overlay lists every key from `lib/shortcuts.ts`; `Esc` closes. |
| 2 | World rail: type a step count and **Advance ×N** → `session advance N` on the shared dispatcher. |
| 3 | **Copy** puts the live seed on the clipboard; Perf rail shows **Steps / sec** from mean `advance.wall`. |
| 4 | Terminal **Clear** wipes the on-screen transcript only — session and history survive. |

### Flow: Layer switch (F-054)

| Step | Action |
|------|--------|
| 1 | Click Elevation / Plates / Overlay on the map HUD, or press `1` / `2` / `3` when not typing. |
| 2 | Host layer + legend update; raster refresh matches that layer. Rapid clicks / poll cannot leave HUD disagreeing with pixels. |
| 3 | While Terminal, seed, or speed is focused, digits type into the field — blur or click the map first for layer keys. |

Domain locks: [wiki/tectonics.md](wiki/tectonics.md) · [ADR-012](../project/decisions.md).

### Flow: Diagnostics (F-042 / F-046)

| Step | Action |
|------|--------|
| 1 | Advance the session; hub records `advance.wall` / heap / phase samples when enabled. |
| 2 | `stats` prints collector summaries; `diag list` shows enable + n/capacity (includes `phase.*`). |
| 3 | `diag off advance.wall` stops new samples; `diag on` resumes; `diag clear` clears. |
| 4 | Map paint records `paint.wall` on the same hub. |

---

## G-008 (shipped)

| Area | Intent | Steps |
|------|--------|-------|
| World | VIEW **1920×1080**, cylinder loop (later sphere in G-009) | F-031–F-032 |
| Plates | Partition + boundaries/flux/fission/motion/orogeny | **F-033–F-038** |
| Camera | Zoom clamp; X loop + Y polar (sphere after F-045) | **F-038** / **F-045** |
| Studio | Multi-panel + mappy style | **F-039** |
| Console | Traditional terminal; Goal close | **F-040** |

Domain locks: [wiki/tectonics.md](wiki/tectonics.md).

---

## Flow: See the world (elevation + Advance)

**Goal:** User opens a large colored heightmap, steps generation, and sees ridges form. UI stays honest while compute runs.

| Step | Action |
|------|--------|
| 1 | Launch via `run-product.cmd` (MapHost + Next + Tauri). Runner shows `WorldSpec.VIEW` (**1920×1080**, seed 0) at **Step 0**. |
| 2 | User clicks **Advance**. Status shows **Working...** in chrome; no map busy overlay; extra Advances are ignored while busy. |
| 3 | When the Step settles, the map paints the current layer. Status returns to `Step n`. |
| 4 | Repeat Advance. **Pan** wraps on X; vertical pan uses sphere polar wrap; zoom in; cannot zoom out past fit-map. |

**Edges / failures:** Clicks while busy are ignored for Advance/New world. Tests never construct `JFrame`. Dump fixture remains `WorldSpec.DEFAULT` 8×8.

---

## Flow: Simulation runner (layers, play, inspect, rails)

**Goal:** User reads relief and plates in a map-first runner, lets time run, reseeds, inspects a cell, and uses the always-on terminal.

| Step | Action |
|------|--------|
| 1 | Launch desktop/Next tool. Menu bar + transport; full-bleed map with neatline; **Elevation**, paused; Perf + World rails open. |
| 2 | Switch **Plates** or **Overlay** (HUD or `1`–`3`). The world does not advance. |
| 3 | **Play** (client timer → `/api/advance`) or **Advance**. Status **Working...** while busy. **Pause** stops the timer. |
| 4 | Click a cell. **Inspect** panel shows x, y, elevation, plate id, velocity. Legend follows the layer. Collapse panel bodies independently. |
| 5 | **P** / **D** (or View menu) toggle Perf / World rails (`aethelgard.rail.left.open` / `.right.open`). Panel bodies use `aethelgard.panel.<id>.open`. Terminal is always on (`aethelgard>` prompt; ↑/↓ history). |
| 6 | Change **Seed**, **Random**, or **New world** (confirm when Step > 0). Ignored while busy. |
| 7 | Shortcuts: Space Play; `A`/`.` Advance; `1`–`3` layers (suppressed while typing); `[`/`]` speed; `N` New world; `` ` ``/`C` terminal; `P`/`D` rails; `R` reset view; `?` shortcuts overlay. |
| 8 | Terminal noun/verb via `CommandDispatch` (F-048–F-050). |

**Edges / failures:** Invalid seed text keeps the previous seed. Unknown commands print `error:`. Host offline shows banner + **Retry**. Tests never construct `JFrame`.

---

## Flow: Next / Desktop launch

**Goal:** User runs the Next map tool against a warm Java `MapHost`, with hot reload on the front.

| Step | Action |
|------|--------|
| 1 | Run `run-product.cmd`. Tauri starts Next (`beforeDevCommand`), waits for `:3000`, spawns MapHost, opens the window. |
| 2 | **Aethelgard** runner shows VIEW **1920×1080**. Switch layers, **Advance**, **Play**/Pause, speed, reseed, inspect, always-on Terminal. |
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
