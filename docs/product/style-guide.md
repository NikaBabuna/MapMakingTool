<!--
  File: docs/product/style-guide.md
  Purpose: Player-facing tone, visuals, and UX feel
  Audience: Agents implementing UI and copy
  Update when: Style direction is specified or G-007 Steps change chrome
-->

# Style guide

_Status: **active** — G-007 studio cartography (done)._

Tone, visual language, and map-tool chrome for Aethelgard’s Next front (`ui/web`).

---

## Direction

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

---

## Map interaction (G-007 / F-028)

- Wheel zoom toward cursor; drag to pan; **Reset view** (`R`) restores identity transform.
- Click inspect maps through the viewport transform to world cells.
- Busy: map overlay + status chip; do not queue Advance / New world.
