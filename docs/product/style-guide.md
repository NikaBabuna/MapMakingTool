<!--
  File: docs/product/style-guide.md
  Purpose: Player-facing tone, visuals, and UX feel
  Audience: Agents implementing UI and copy
  Update when: Style direction is specified or studio Steps change chrome
-->

# Style guide

_Status: **active** — G-009 **done** (F-054). G-010 **in progress** (F-059 margin relief). Menu bar, panel registry, resizable rails, brighter map, layer-switch harden._

Tone, visual language, and map-tool chrome for Aethelgard’s Next front (`ui/web`). **Studio cartography** (G-007/G-008 multi-panel Inspect/Legend + mappy neatline) remains the map-desk metaphor; F-051+ reframes chrome as a quiet **runner shell**. Terminal language F-048–F-050 (rebuilt `Terminal.tsx`); F-052 makes the terminal an always-on panel; F-053 turns chrome into descriptor-driven infrastructure; F-054 hardens layer switching and closes the Goal.

---

## G-009 runner

| Item | Intent | Status |
|------|--------|--------|
| **Feel** | Quiet simulation runner; stable layout slots | **F-051** |
| **Terminal** | Always-on bottom panel on shared dispatcher | **F-052** |
| **Perf** | Left rail: mean timings / memory from DiagnosticsHub | **F-052** |
| **Chrome** | Menu bar row + panel registry + resizable rails | **F-053** |
| **Close** | Layer harden + doc hygiene + Goal wrap-up | **F-054** done |

---

## G-010 crust (F-058 live)

Map still reads `elevation`, now **isostasy** of riding crust. Contact stamps thicken lockers so mountains **move** with plates. **Ridge mint:** rifts are thin ocean, not copied mountains (including SEPARATE copies). **Buoyancy:** ocean subducts at COLLIDE; continent does not die by area. **Margin relief:** a split is a trough and a collision slopes nearby ocean, with a hashed lip. Simulation menu: **Restart UI** (reload page) and **Restart engine** (same seed, Step 0). Exact HUD/crust layer: F-061 or later. Suture remains **planned** (F-060).

---

## Runner shell (F-051 / F-052 / F-053)

| Slot | Content |
|------|---------|
| **Menu bar** | Slim top row: File · Edit · View · Simulation · Help. Simulation includes Restart UI and Restart engine (F-057). |
| **Top · identity** | Brand wordmark + host **dot only** (green/red) |
| **Top · transport** | Play · Pause · Speed (`1x` / `2x` / `4x` / `Fastest`) |
| **Top · view** | Reset view · Perf (rail toggle) · World (rail toggle) |
| **Left · Perf** | Mean paint/advance/phase/heap from `/api/status` `diag` |
| **Map HUD** | Layer switch **top-left** on the map (pointer-isolated from pan) |
| **World rail** | Step, size, seed, reset world; Inspect/Legend sections |
| **Terminal** | Always-visible bottom panel |

**Principles:** map first; chrome gray and quiet; one job per region; progressive disclosure; same session as CLI/terminal language.

### Layer shortcuts (F-054)

| Keys | Action |
|------|--------|
| `1` / `2` / `3` | Elevation / Plates / Overlay — `preventDefault` when handled |
| Typing targets | `INPUT` / `TEXTAREA` / `SELECT` (and contentEditable) **suppress** all runner shortcuts, including layer digits — focus Terminal or seed/speed first, then type |

Layer clicks and keys bump an apply generation so the 200 ms status/raster poll cannot overwrite a fresher layer frame.

### Speeds

| Label | Period |
|-------|--------|
| `1x` | 250 ms (default) |
| `2x` | 125 ms |
| `4x` | 62 ms |
| `Fastest` | 1 ms tick (scheduler floor) |

---

## Terminal (F-052 panel)

- Always present under the map stage (`terminal-panel`); not a hide-by-default drawer.
- Continuous dark surface (`--terminal-bg`); **no boxed input field**.
- Prompt prefix **`aethelgard>`** + typing on one row; scrollback above; light-gray text (`--terminal-fg`), dim hints.
- Empty state promotes noun/verb (`help` · `session get` · `list pool` …).
- Input placeholder: `help`. ↑ / ↓ history (cap 32); Enter runs; `` ` `` / `C` focuses the input.
- Same `CommandDispatch` via `/api/command`.

---

## Chrome infrastructure (F-053)

**Alpha — descriptor shapes may change (no ADR yet).**

| Model | File | Adds a new… |
|-------|------|-------------|
| **Panel registry** | `lib/panels.ts` | rail panel: one descriptor (`id`, `title`, `dock`, `order`, `defaultOpen`, `collapsible`) + a body case |
| **Menu bar** model | `lib/menus.ts` | menu / item: one entry; `enabled: false` renders an inert stub |
| **Layout** | `lib/layout.ts` | resizable region: size, clamp, and storage key |
| **Shortcuts** | `lib/shortcuts.ts` | key row shown by the Shortcuts overlay (`?`) |

Rail panels share one `Panel.tsx` chrome (title, collapse caret, body). Stub menu items are visibly dim, `aria-disabled`, and do nothing — they advertise the shape of later Steps without faking behaviour.

---

## Physical map palette (F-053 brighter)

Elevation reads like a brighter printed atlas (deterministic in `ElevationRaster`):

- **Ocean bathymetry** (`e < 0`): piecewise stops at e = −64, −32, −16, −8, −1 — `(24,64,104)` deep → `(110,190,226)` shore
- **Land** piecewise stops at e = 0, 12, 24, 40, 64 — `(150,196,120)` lowland green → `(214,190,124)` upland → `(255,250,236)` peaks
- Hillshade formula unchanged (flat 12, ±6…18) and still land-only
- Legend swatches are read from the same ramps, so they always match the map
- **Plates:** muted gray interior + dark boundary

---

## Map interaction

- Wheel zoom toward cursor; clamped to **[fitScale, MAX_SCALE]**. Vertical pan clamped at poles when zoomed.
- Drag pan: **X loopback**, **Y polar clamp**; blank N/S margins. Layer HUD does not capture pan.
- Inspect click → World rail Inspect section.
- **Reset view** (`R`); no Working… map overlay — busy still gates Advance / Reset world (World rail status chip).
- Neatline + graticule/ticks + coords HUD remain.

Avoid: purple gradients; cream+terracotta; glow-heavy chrome; attention-seeking accents.

---

## Tokens (CSS)

| Token | Role | Example |
|-------|------|---------|
| `--ink` | App ground | `#1c1c1e` |
| `--ink-lift` | Raised | `#262628` |
| `--panel` | Bar / rail | `#2a2a2c` |
| `--line` | Hairlines | `rgba(255,255,255,0.1)` |
| `--text` / `--muted` | Type | `#d0d0d2` / `#8a8a8e` |
| `--accent` | Quiet focus | `#6e6e72` |
| `--ok` / `--danger` | Host dot | muted green / red |
| `--terminal-bg` / `--terminal-fg` | Terminal | `#121214` / `#c8c8c8` |
| `--perf-w` | Left Perf rail | `220px` (resizable) |
| `--menu-h` | Menu bar row | `1.9rem` |
| `--gutter` / `--gutter-w` | Space between regions + drag handle | `#0e0e10` / `5px` |
| `--panel-edge` | Panel outline | `rgba(255,255,255,0.16)` |
| `--rail-bg` | Rail ground (distinct from panels) | `#202022` |

Fonts: `--font-ui` (sans), `--font-mono` (seed, terminal, coords, perf).

---

## Layout

| Region | Rule |
|--------|------|
| **Menu bar** | Own row above the transport bar |
| **Top bar** | Three slots: identity \| transport \| view |
| **Left Perf rail** | Registry panels with `dock: "left"`; toggle via **Perf** / `P`; drag-resizable |
| **Map stage** | Full-bleed; layer HUD top-left |
| **World rail** | Registry panels with `dock: "right"` (World + Inspect + Legend); toggle via **World** / `D`; drag-resizable |
| **Terminal** | Permanent bottom panel; drag-resizable height |

Regions are separated by a gutter that doubles as the drag handle (`rail-splitter`). Sizes clamp inside `LAYOUT_LIMITS`; **View → Reset layout** restores defaults.

### Persistence (`localStorage`)

| Key | Meaning |
|-----|---------|
| `aethelgard.rail.left.open` | Perf rail visible |
| `aethelgard.rail.right.open` | World rail visible |
| `aethelgard.panel.<id>.open` | Panel body expanded (`aethelgard.panel.inspect.open`, `…legend.open`, `…perf.open`) |
| `aethelgard.layout.leftRail` / `.rightRail` / `.terminal` | Region sizes in px |

---

## Keyboard

| Key | Action |
|-----|--------|
| `Space` | Play / Pause |
| `A` or `.` | Advance one step |
| `1` / `2` / `3` | Elevation / Plates / Overlay |
| `[` / `]` | Speed slower / faster |
| `N` | Reset world / New world (confirm when Step > 0) |
| `` ` `` or `C` | Focus terminal |
| `D` | Toggle World rail |
| `P` | Toggle Perf rail |
| `R` | Reset map view |
| `?` | **Shortcuts** overlay (same list, from `lib/shortcuts.ts`) |
| `Esc` | Close menu / dialog |

Ignore shortcuts while focus is in an input / textarea / select (except Enter in seed/terminal fields as documented).

---

## Quality of life (F-053)

- **Shortcuts overlay** — `?` or Help → Shortcuts…; single source is `lib/shortcuts.ts`.
- **Advance ×N** — World rail steps field; runs `session advance N` through the shared dispatcher (no new verb).
- **Copy seed** — copies the live seed to the clipboard; button flips to `Copied` briefly.
- **Steps / sec** — Perf rail row derived from the mean `advance.wall` sample.
- **Terminal Clear** — clears the on-screen transcript only; history and session are untouched.
