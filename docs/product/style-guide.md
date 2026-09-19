<!--
  File: docs/product/style-guide.md
  Purpose: Player-facing tone, visuals, and UX feel
  Audience: Agents implementing UI and copy
  Update when: Style direction is specified or studio Steps change chrome
-->

# Style guide

_Status: **active** — G-007 studio cartography (done). G-008 **done** (F-040). **G-009 planned:** simulation-runner chrome, shared terminal/CLI, perf visibility._

Tone, visual language, and map-tool chrome for Aethelgard’s Next front (`ui/web`).

---

## G-009 runner (planned)

| Item | Intent | Status |
|------|--------|--------|
| **Feel** | Unity-like simulation runner: dense control over session env (seed, speed, play, layers, …) | planned F-051+ |
| **Terminal** | Traditional terminal on **shared** noun/verb dispatcher with CLI | **F-048**–**F-049** language/runner; rebuild chrome F-050 |
| **Perf** | Visible step timings / memory (panel + commands) | **F-042** hub; panel F-052 |
| **Look** | UI/UX polish; map-first serious tool, not marketing page | planned F-053 |

---

## G-008 studio (shipped)

| Item | Intent | Status |
|------|--------|--------|
| **Studio** | Distinct titled **Inspect** + **Legend** panel cards on the right rail | **F-039** |
| **Look** | Chart/atlas cues: **neatline**, **graticule**/ticks, coords + scale HUD | **F-039** |
| **Console** | Traditional terminal appearance (`aethelgard>` prompt, phosphor-on-black) | **F-040** |
| **Map camera** | Zoom **in** allowed; cannot zoom out past **fit whole map** | **F-032** |
| **Loopback pan** | Drag pan: **X wraps**; **Y clamped** at polar edges | **F-038** |

---

## Traditional terminal console (F-040)

- Drawer under the map (not a slate studio-panel card).
- Near-black well (`#070a08`), mono type, green phosphor text (`#8dffaa` / `#7dff9a`).
- Prompt prefix **`aethelgard>`** on the input line; log echoes the same prompt.
- ↑ / ↓ recalls prior commands (cap 32). Enter runs; autofocus when opened.
- Same `CommandDispatch` noun/verb language as CLI (F-048); deprecated flat aliases still work.
- Contrast: Inspect/Legend stay cool slate panels; the console is CRT/terminal.

---

## Map interaction (G-007 / F-028 / F-032 / F-038 / F-039)

- Wheel zoom toward cursor; clamped to **[fitScale, MAX_SCALE]** (no empty void beyond the map). Vertical pan clamped at polar edges when zoomed.
- Drag to pan with **X loopback** and **Y polar clamp**; regions above/below the map band stay **blank** (no vertical loop tiles).
- Inspect clicks map into world cells (x wraps; y must hit the map band).
- **Reset view** (`R`) restores fitted centered transform.
- Busy: map overlay + status chip; do not queue Advance / New world. Status polls use cached step (non-blocking).
- **Plates** layer: gray interior + dark **bold** boundary stroke (half-edge core + dilate; sphere neighbors).
- **Mappy stage:** neatline frame + faint graticule/ticks; HUD shows hover cell coords and zoom scale.

**Studio cartography** — the window is a working map desk, not a marketing page.

- Map fills the work area.
- Chrome is thin bars and **multi-panel** cards.
- Brand is a quiet wordmark in the top bar (not a gradient hero headline).
- Type is restrained UI sans + mono for seed/coords/console.

Avoid: purple-on-white / purple–indigo gradients; cream + terracotta + display serif; dense broadsheet newspaper grids; glow-heavy “AI dark” chrome.

---

## Tokens (CSS)

| Token | Role | Example |
|-------|------|---------|
| `--ink` | App ground | `#12161c` |
| `--ink-lift` | Raised panels | `#1a2029` |
| `--panel` | Dock / bar fill | `rgba(26, 32, 41, 0.92)` |
| `--line` | Hairline borders | `rgba(148, 163, 184, 0.22)` |
| `--text` | Primary text | `#e8eef4` |
| `--muted` | Secondary labels | `#8b9aab` |
| `--accent` | Primary action | `#5b8fa8` |
| `--ok` / `--danger` | Host / error | green / coral |

Fonts: `--font-ui` (sans), `--font-mono` (seed, inspect, console).

---

## Layout

| Region | Rule |
|--------|------|
| **Top bar** | Thin; brand wordmark + host pill + tool controls + status |
| **Map stage** | Full-bleed in the remaining viewport; neatline + graticule + HUD |
| **Right rail** | Stacked **studio-panel** cards: Inspect, Legend; whole rail toggles via Dock |
| **Panel collapse** | Each panel body collapses independently |
| **Console** | On-demand **terminal** drawer (phosphor prompt; not a studio-panel card) |

### Persistence (`localStorage`)

| Key | Meaning |
|-----|---------|
| `aethelgard.dockOpen` | Right rail visible |
| `aethelgard.panelInspectOpen` | Inspect panel body expanded |
| `aethelgard.panelLegendOpen` | Legend panel body expanded |

---

## Keyboard (G-007 / F-029)

| Key | Action |
|-----|--------|
| `Space` | Play / Pause |
| `A` or `.` | Advance |
| `1` / `2` / `3` | Elevation / Plates / Overlay |
| `[` / `]` | Speed slower / faster |
| `N` | New world (same confirm as UI) |
| `` ` `` or `C` | Toggle console |
| `D` | Toggle dock |
| `R` | Reset map view |

Ignore shortcuts while focus is in an input / textarea / select (except Enter in seed/console fields as documented).
