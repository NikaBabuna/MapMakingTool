<!--
  File: docs/product/style-guide.md
  Purpose: How the screen looks and what its controls are called
  Audience: Humans and agents
  Update when: A visible label, color role, or layout rule changes
-->

# Style guide

The screen is a working map. The frame around it is gray and quiet, so the map is the first thing a person sees. One region, one job: the map shows the world, the rails describe it, the terminal questions it, and the bars along the top run it.

## The map

The map fills the middle of the screen. It shows one of three views of the same world.

| View | What the person sees |
|------|----------------------|
| Elevation | Height. Deep water is dark blue. Water near a shore is light blue. Low land is green. High land fades toward pale, and slopes are shaded so that ranges stand out. |
| Plates | Plates in muted gray, with a dark stroke along each edge |
| Overlay | The height, with that same dark stroke drawn over it, labeled Boundary |

Height 0, where open ocean stands, takes the lowest green, as the [height](wiki/elevation.md) page explains. Switching views does not move the world. The legend uses the same colors as the map. Drag pans the map, and scroll zooms toward the pointer. Reset view fits the map in the frame again. The view does not pass the top or the bottom.

## The frame

The frame is everything around the map. Nothing in it is brighter than the map.

| Place | What is there |
|-------|----------------|
| Menu bar | File, Edit, View, Simulation, Help, along the top |
| Name | The word Aethelgard, and a dot. The dot is green when the world is there and red when it is not |
| Transport | Play, Pause, and a speed: 1x, 2x, 4x, or Fastest. 1x is the calm rate |
| View | Reset view, and toggles for the Perf rail and the World rail |
| Layer switch | Elevation, Plates, and Overlay, at the top left of the map |
| Perf rail | Left side. How long the world is taking, and how much memory it is using |
| World rail | Right side. The Step, the Size, and the Seed; a Seed field with Random, Copy, and Reset world; a Steps field with Advance ×N; then the Inspect and Legend panels |
| Terminal | Always along the bottom. The prompt is `aethelgard>` |

A rail or the terminal can be resized by dragging its edge, and the studio keeps that layout. While a step is working, the World rail reads Working. When the world is not there, a banner says so and offers Retry.

## The menus

| Menu | What it holds |
|------|----------------|
| File | New world and Random seed. Open world…, Save world…, Export map image…, and Quit are shown dim |
| Edit | Copy seed. Undo, Redo, and Preferences… are shown dim |
| View | Perf rail, World rail, Focus terminal, Reset map view, and Reset layout |
| Simulation | Play, Pause, Advance one step, the four speeds, Restart UI, and Restart engine. Record history… is shown dim |
| Help | Shortcuts…. Documentation… and About Aethelgard are shown dim |

A menu item that does nothing yet is visible and dim. It cannot be chosen.

## Keys

| Key | What it does |
|-----|----------------|
| Space | Play or Pause |
| A or . | Advance the world one step |
| 1, 2, 3 | Elevation, Plates, Overlay |
| [ and ] | Slower or faster |
| N | New world. If the world has already moved, the person confirms |
| ` or C | The terminal |
| D | Show or hide the World rail |
| P | Show or hide the Perf rail |
| R | Reset the view of the map |
| ? | The list of these keys |
| Esc | Close a menu or a dialog |

Typing in a field ignores these keys, so a seed can be entered without advancing the world. The layer keys are 1, 2, and 3.

## The terminal

The terminal is a dark strip of fixed-width text, so it reads as a different kind of place from the panels.

| Place | What is there |
|-------|----------------|
| Prompt | `aethelgard>`, on the last line, where the person types |
| Transcript | Each line typed, and its answer, above the prompt |
| Clear | Empties the transcript. The world stays |

## What the map refuses

No purple gradients. No cream and terracotta. No glow. Nothing in the frame should shout louder than the map.
