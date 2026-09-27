<!--
  File: ui/web/src/app/README.md
  Purpose: Door to the web front's Next.js route: the page's frame, its one route, and its stylesheet
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# App

Holds the web front's one Next.js route: the root layout that frames the page, the page that renders the map tool, and the global stylesheet.

**Paper:** [tool](../../../../docs/architecture/studio/web/tool.md) · [styles](../../../../docs/architecture/studio/web/styles.md) · **Conventions:** [conventions.md](../../../../docs/architecture/conventions.md)

## Why

Next.js reads its routes from this folder by fixed file names, so only what Next.js requires lives here: `layout.tsx`, `page.tsx`, and the stylesheet the layout imports. The studio itself is one component, and its code belongs in [components](../components/README.md), with the logic that uses no React in [lib](../lib/README.md).

## How it works

Next.js renders `RootLayout` around every route. It loads the IBM Plex Sans and Mono fonts into the CSS variables `--font-ui-loaded` and `--font-mono-loaded`, imports `globals.css`, and exports `metadata`, the page's title and description. The only route, `HomePage`, renders `MapTool`, and everything the person sees comes from there. `globals.css` styles every class the components use, from the tokens in its `:root` block.

**Start reading at:** `HomePage` in [page.tsx](page.tsx).

## Depends on

- [components](../components/README.md) — `MapTool`, the whole studio

## Used by

- nothing in this repository — Next.js serves this route, started by `npm run dev` in `ui/web`

## Where each step happens

### [Tool](../../../../docs/architecture/studio/web/tool.md)

| Step | Member | File |
|------|--------|------|
| 1. The app shell sets the title and description, loads the fonts, imports the stylesheet, and renders the root component on the only route | `RootLayout`, `metadata`, `HomePage` | [layout.tsx](layout.tsx), [page.tsx](page.tsx) |

The other steps are in [components](../components/README.md).

### [Styles](../../../../docs/architecture/studio/web/styles.md)

| Step | Member | File |
|------|--------|------|
| 1. The tokens, the border-box rule, the page and body, and the focus ring | `:root`, `*`, `html`, `body`, `:focus-visible` | [globals.css](globals.css) |
| 2. The screen grid and the splitters | `.studio`, `.studio-body`, `.studio-work`, `.rail-splitter` | [globals.css](globals.css) |
| 3. The menu, the top bar, buttons and fields, the status chip, and the error banner | `.menu-bar`, `.menu-list`, `.menu-item`, `.studio-bar`, `.runner-slot`, `.host-dot`, `.btn`, `.field` | [globals.css](globals.css) |
| 4. The map stage, its frame, its corner readout, and its layer switch | `.map-stage`, `.map-view`, `.map-neatline`, `.map-graticule`, `.map-ticks`, `.map-hud`, `.map-layer-switch` | [globals.css](globals.css) |
| 5. The rails and panels, the terminal, the dialogs, and the hidden drawer class | `.side-rail`, `.studio-panel`, `.panel-chrome`, `.inspect-grid`, `.legend-list`, `.perf-grid`, `.terminal-panel`, `.console-log`, `.terminal-line`, `.shortcuts-dialog`, `.confirm-dialog`, `.console-drawer` | [globals.css](globals.css) |
| 6. The reduced-motion and narrow-screen rules | `@media (prefers-reduced-motion: reduce)`, `@media (max-width: 720px)` | [globals.css](globals.css) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [layout.tsx](layout.tsx) | The page's root layout: its fonts, its title, and the global styles | `RootLayout`, `metadata` |
| [page.tsx](page.tsx) | The studio's one route, which renders the map tool | `HomePage` |
| [globals.css](globals.css) | The stylesheet: its tokens, the screen grid, the map stage's frame, and the narrow-screen and reduced-motion rules | `:root`, `.studio`, `.map-stage` |
