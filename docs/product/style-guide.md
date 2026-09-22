<!--
  File: docs/product/style-guide.md
  Purpose: How the screen looks and what its controls are called
  Audience: Humans and agents
  Update when: A visible label, color role, or layout rule changes
-->

# Style guide

The screen is a working map. The frame around it is gray and quiet, so the map is the first thing a person sees. One region, one job.

## The map

| View | What the person sees |
|------|----------------------|
| Elevation | Height. Deep water is dark blue. Water near a shore is light blue. Low land is green. High land fades toward pale. |
| Plates | Plates in muted gray, with a dark stroke along each edge |
| Overlay | The plates, and that same dark stroke, labeled Boundary |

Switching views does not move the world. The legend uses the same colors as the map. Drag pans the map. Scroll zooms toward the pointer. Reset view fits the map in the frame again. The view does not pass the top or the bottom.

## The frame

| Place | What is there |
|-------|----------------|
| Menu bar | File, Edit, View, Simulation, Help |
| Name | The word Aethelgard, and a dot. The dot is green when the world is there and red when it is not |
| Transport | Play, Pause, and a speed: 1x, 2x, 4x, or Fastest. 1x is the calm rate |
| View | Reset view, and toggles for the Perf rail and the World rail |
| Perf rail | Left side. How long the world is taking, and how much memory it is using |
| World rail | Right side. The step, the size, the seed, and sections for Inspect and Legend |
| Terminal | Always along the bottom. The prompt is `aethelgard>` |

Simulation holds Restart UI and Restart engine. A menu item that does nothing yet is visible and dim.

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

## What the map refuses

No purple gradients. No cream and terracotta. No glow. Nothing in the frame should shout louder than the map.
