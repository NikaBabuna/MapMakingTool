<!--
  File: docs/architecture/studio/web/styles.md
  Purpose: globals.css — the stylesheet: its tokens, the screen grid, the map stage's frame, and the narrow-screen and reduced-motion rules
  Audience: Agents and humans
  Update when: A token, the screen grid, the map frame, or a media rule in globals.css changes
-->

# Styles

One stylesheet gives the page its dark, map-first look and its layout: a menu row, a top bar, and below them the working area of rails and map above a terminal. Sizes that the person can drag are variables the root component sets, so dragging a splitter only changes a number.

## What it reads

The CSS variables the root sets on the page's outer element, `--perf-w`, `--dock-w`, and `--terminal-h` ([tool](tool.md)), and the font variables of the app shell. The class names the components use.

## What it writes

Style rules; nothing else.

## Model

**Tokens.** The `:root` block declares the palette and sizes every rule uses:

| Token | Value | Role |
|-------|-------|------|
| `--ink`, `--ink-lift`, `--panel`, `--rail-bg`, `--gutter` | `#1c1c1e`, `#262628`, `#2a2a2c`, `#202022`, `#0e0e10` | Page, raised bars, panels, rails, and the gutters between regions |
| `--text`, `--muted`, `--accent`, `--accent-strong` | `#d0d0d2`, `#8a8a8e`, `#6e6e72`, `#9a9a9e` | Text, secondary text, splitter hover, focus ring |
| `--ok`, `--danger` | `#5a9a6a`, `#a05a5a` | The host dot when online and when offline |
| `--terminal-bg`, `--terminal-fg`, `--terminal-dim` | `#121214`, `#c8c8c8`, `#7a7a7a` | Terminal |
| `--menu-h`, `--bar-h`, `--gutter-w` | `1.9rem`, `2.75rem`, `5px` | Menu row, top bar, splitter width |
| `--perf-w`, `--dock-w`, `--terminal-h` | `220px`, `280px`, `11rem` | Default region sizes, overridden by the root |
| `--font-ui`, `--font-mono` | IBM Plex Sans, IBM Plex Mono, with fallbacks | Fonts |

**Screen grid.** The page is a grid of three rows, the body a grid of three rows, and the working area a row of five items:

$$\mathrm{page} = \bigl[\,\texttt{--menu-h} \mid \texttt{--bar-h} \mid 1\mathrm{fr}\,\bigr], \qquad \mathrm{body} = \bigl[\,1\mathrm{fr} \mid \texttt{--gutter-w} \mid \texttt{--terminal-h}\,\bigr],$$

$$\mathrm{work} = \bigl(\,\texttt{--perf-w} \;\big\|\; \texttt{--gutter-w} \;\big\|\; 1\mathrm{fr}_{\mathrm{map}} \;\big\|\; \texttt{--gutter-w} \;\big\|\; \texttt{--dock-w}\,\bigr),$$

where the map stage takes the space left over. The top bar is a grid of three columns, $[1\mathrm{fr} \mid \mathrm{auto} \mid 1\mathrm{fr}]$, so the transport controls stay centred.

**Map frame.** Over the stage lie a neatline inset by 0.55 rem, a graticule, and edge ticks, all spaced at 12.5% of the stage, so they divide the stage into $8 \times 8$ fixed to the screen, not to map coordinates. The source canvas is hidden, and the view canvas renders its pixels without smoothing (`image-rendering: pixelated`).

## Procedure

1. The tokens and a border-box sizing rule for every element come first; the page and body fill the window on the `--ink` background, and focused controls get a 2 px ring in `--accent-strong`. [`globals.css`](../../../../ui/web/src/app/globals.css).
2. `.studio`, `.studio-body`, and `.studio-work` build the grid of the Model; `.rail-splitter` draws the gutters, with a row-resize variant for the terminal. [`globals.css`](../../../../ui/web/src/app/globals.css).
3. The menu (`.menu-bar`, `.menu-list`, `.menu-item`), the top bar (`.studio-bar`, `.runner-slot`, `.host-dot`), buttons and fields (`.btn`, `.field`), the status chip, and the error banner are styled next. [`globals.css`](../../../../ui/web/src/app/globals.css).
4. The map stage (`.map-stage`, `.map-view`, `.map-neatline`, `.map-graticule`, `.map-ticks`, `.map-hud`, `.map-layer-switch`) is styled with a crosshair cursor, and a grabbing cursor while panning. [`globals.css`](../../../../ui/web/src/app/globals.css).
5. The rails and panels (`.side-rail`, `.studio-panel`, `.panel-chrome`, `.inspect-grid`, `.legend-list`, `.perf-grid`), the terminal (`.terminal-panel`, `.console-log`, `.terminal-line`), and the dialogs (`.shortcuts-dialog`, `.confirm-dialog`) follow. The class `.console-drawer` is a hidden leftover. [`globals.css`](../../../../ui/web/src/app/globals.css).
6. With reduced motion preferred, animations and transitions are cut to 0.01 ms. Below 720 px of width, the world rail floats over the map, and the performance rail and the splitters are hidden. [`globals.css`](../../../../ui/web/src/app/globals.css).

## What is true afterwards

The map stage always takes the space the rails and terminal leave, and a dragged size changes only one variable. The look the stylesheet produces, and the words on the controls, are specified for a person in [the style guide](../../../product/style-guide.md).

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Stylesheet | — | `:root`, `.studio`, `.studio-body`, `.studio-work`, `.map-stage`, `.side-rail`, `.terminal-panel` | [`ui/web/src/app/globals.css`](../../../../ui/web/src/app/globals.css) |

Parent: [web front](README.md).
