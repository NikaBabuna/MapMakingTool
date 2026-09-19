<!--
  File: docs/product/style-guide.md
  Purpose: Player-facing tone, visuals, and UX feel
  Audience: Agents implementing UI and copy
  Update when: Style direction is specified or G-007 Steps change chrome
-->

# Style guide

_Status: **active** — G-007 studio cartography (done). G-008 camera **F-032/F-033** (zoom clamp + **horizontal-only** loopback pan); panels/console still planned._

Tone, visual language, and map-tool chrome for Aethelgard’s Next front (`ui/web`).

---

## G-008 intent (planned / partial)

| Item | Intent | Status |
|------|--------|--------|
| **Studio** | Distinct panels/windows for controls, legend, inspect | planned F-039 |
| **Look** | More **mappy** (chart/atlas cues) | planned F-039 |
| **Console** | Traditional terminal appearance | planned F-040 |
| **Map camera** | Zoom **in** allowed; cannot zoom out past **fit whole map** | **F-032** |
| **Loopback pan** | **Horizontal only** (left/right); wraps on torus; vertical locked to stage center | **F-033** |

---

## Map interaction (G-007 / F-028 / F-032 / F-033)

- Wheel zoom toward cursor; clamped to **[fitScale, MAX_SCALE]** (no empty void beyond the map). Vertical position re-locks to stage center.
- Drag to pan **left/right only** with horizontal loopback; no up/down pan.
- Inspect clicks map into world cells (x wraps; y must hit the map band).
- **Reset view** (`R`) restores fitted centered transform.
- Busy: map overlay + status chip; do not queue Advance / New world.

**Studio cartography** — the window is a working map desk, not a marketing page.

- Map fills the work area.
- Chrome is thin bars and docks.
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
| **Map stage** | Full-bleed in the remaining viewport (no max-width marketing column) |
| **Right dock** | Inspect + Legend; collapsible; preference `localStorage` key `aethelgard.dockOpen` |
| **Console** | On-demand drawer (not permanently in the dock) |

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
