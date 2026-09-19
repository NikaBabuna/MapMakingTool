<!--
  File: docs/product/style-guide.md
  Purpose: Player-facing tone, visuals, and UX feel
  Audience: Agents implementing UI and copy
  Update when: Style direction is specified or studio Steps change chrome
-->

# Style guide

_Status: **active** — G-009 **F-051** runner shell. G-008 studio done. Terminal language F-048–F-050; terminal look F-051._

Tone, visual language, and map-tool chrome for Aethelgard’s Next front (`ui/web`). **Studio cartography** remains the map-desk metaphor; F-051 reframes the chrome as a quiet **runner shell**.

---

## G-009 runner

| Item | Intent | Status |
|------|--------|--------|
| **Feel** | Quiet simulation runner; stable layout **slots** for later controls | **F-051** |
| **Terminal** | Real terminal surface on shared noun/verb dispatcher | **F-050** component; **F-051** look |
| **Perf** | Visible step timings / memory (panel + commands) | **F-042** hub; panel **F-052** |
| **Look** | Residual polish if needed | planned F-053 |

---

## Runner shell (F-051)

Stable regions (grow by filling slots, not lengthening the top bar):

| Slot | Content |
|------|---------|
| **Top · identity** | Brand wordmark + host **dot only** (green/red) |
| **Top · transport** | Play · Pause · Speed (`1x` / `2x` / `4x` / `Fastest`) |
| **Top · view** | Reset view · World (rail toggle) · Terminal |
| **Map HUD** | Layer switch **bottom-left** on the map |
| **World rail** | Step, size, seed, reset world; Inspect/Legend sections |
| **Terminal** | Full-width drawer under the stage |

**Principles:** map first; chrome gray and quiet; one job per region; progressive disclosure; same session as CLI/terminal language.

### Speeds

| Label | Period |
|-------|--------|
| `1x` | 250 ms (default) |
| `2x` | 125 ms |
| `4x` | 62 ms |
| `Fastest` | 1 ms tick (scheduler floor) |

---

## Terminal (F-051 look)

- Continuous dark surface (`--terminal-bg`); **no boxed input field**.
- Prompt prefix **`aethelgard>`** + typing on one row; scrollback above; light-gray text (`--terminal-fg`), dim hints.
- Empty state promotes noun/verb (`help` · `session get` · `list pool` …) — no placeholder-verb copy.
- Input placeholder: `help`. ↑ / ↓ history (cap 32); Enter runs; autofocus when opened.
- Same `CommandDispatch` via `/api/command`.

---

## Physical map palette (F-051)

Elevation reads like a printed atlas (deterministic in `ElevationRaster`):

- **Ocean** (`e < 0`): `(42, 78, 108)`
- **Land** piecewise stops at e = 0, 8, 16, 24, 32 (lowland greens → highland browns → pale peaks)
- Hillshade retained on land
- **Plates:** muted gray interior + dark boundary

---

## Map interaction

- Wheel zoom toward cursor; clamped to **[fitScale, MAX_SCALE]**. Vertical pan clamped at poles when zoomed.
- Drag pan: **X loopback**, **Y polar clamp**; blank N/S margins.
- Inspect click → World rail Inspect section.
- **Reset view** (`R`); busy overlay; do not queue Advance / Reset world.
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

Fonts: `--font-ui` (sans), `--font-mono` (seed, terminal, coords).

---

## Layout

| Region | Rule |
|--------|------|
| **Top bar** | Three slots: identity \| transport \| view |
| **Map stage** | Full-bleed; layer HUD bottom-left |
| **World rail** | World + Inspect + Legend; toggle via **World** / `D` |
| **Terminal** | On-demand drawer (not a studio-panel card) |

### Persistence (`localStorage`)

| Key | Meaning |
|-----|---------|
| `aethelgard.dockOpen` | World rail visible |
| `aethelgard.panelInspectOpen` | Inspect expanded |
| `aethelgard.panelLegendOpen` | Legend expanded |

---

## Keyboard

| Key | Action |
|-----|--------|
| `Space` | Play / Pause |
| `A` or `.` | Advance one step |
| `1` / `2` / `3` | Elevation / Plates / Overlay |
| `[` / `]` | Speed slower / faster |
| `N` | Reset world / New world (confirm when Step > 0) |
| `` ` `` or `C` | Toggle terminal |
| `D` | Toggle World rail |
| `R` | Reset map view |

Ignore shortcuts while focus is in an input / textarea / select (except Enter in seed/terminal fields as documented).
