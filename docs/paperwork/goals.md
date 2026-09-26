<!--
  File: docs/paperwork/goals.md
  Purpose: Index of multi-session Goals
  Audience: Agents and humans
  Update when: Goals are added or change status
-->

# Goals

A **Goal** is a durable result across sessions: what should be true when it is done, what it refuses, and the Steps that will get there. Steps (`F-0xx`) belong to a Goal. This index lists every Goal ever opened, with its status and the result it set out to make true. The Goal's own file holds its claims, its decisions, and its progress. Only one Goal is active at a time, and the line below names it.

**Active Goal:** none · Last completed: [G-011 Docs restructuring](goals/G-011-docs-restructuring.md)

| ID | Name | Status | Result | Doc |
|----|------|--------|--------|-----|
| G-001 | Engine skeleton | done | A general step-by-step simulation engine that runs, keeps its core promises under test, and can be driven from a command line and a basic window. | [goals/G-001-engine-skeleton.md](goals/G-001-engine-skeleton.md) |
| G-002 | Engine host readiness | done | The engine became a clean host: a product can plug in its own rules, fields, and events without editing the engine. | [goals/G-002-engine-host-readiness.md](goals/G-002-engine-host-readiness.md) |
| G-003 | First product world | done | The first world: a product built on the engine, a grid with a height layer, and one process that raises relief where two plates meet. | [goals/G-003-first-product-world.md](goals/G-003-first-product-world.md) |
| G-004 | See the world | done | A large map in a window, where many plates divide the world, relief rises along their contacts, and the window stays honest while it works. | [goals/G-004-see-the-world.md](goals/G-004-see-the-world.md) |
| G-005 | Living map | done | The map became the product: plates drift, the window is a tool with views, speeds, and inspection, and a console waits for real commands. | [goals/G-005-living-map.md](goals/G-005-living-map.md) |
| G-006 | Local webview front | done | The window became a local web page in a desktop shell, talking to the same world over localhost, with the world's rules unchanged. | [goals/G-006-webview-front.md](goals/G-006-webview-front.md) |
| G-007 | Studio cartography tool | done | The same living map in a better tool: a map-first studio with pan, zoom, keyboard shortcuts, and clearer feedback. | [goals/G-007-studio-cartography.md](goals/G-007-studio-cartography.md) |
| G-008 | Boundary tectonics + cartography studio | done | A 1920 by 1080 world whose plates behave like plates, meeting at classified boundaries and growing, shrinking, splitting, and dying, shown in a multi-panel studio. | [goals/G-008-boundary-tectonics-studio.md](goals/G-008-boundary-tectonics-studio.md) |
| G-009 | Simulation runner harden | done | A simulation runner that tells the truth: rifts fill from the plates that part, the poles join as on a sphere, costs are measured, and one command language drives both the terminal and the command line. | [goals/G-009-simulation-runner-harden.md](goals/G-009-simulation-runner-harden.md) |
| G-010 | Crust topology | done | Land became material that rides on plates: oceans are born thin at ridges, ocean crust is drawn under, and continents rise from arcs and sutures. | [goals/G-010-crust-topology.md](goals/G-010-crust-topology.md) |
| G-011 | Docs restructuring | done | Four documentation shelves behind one entrance, an architecture paper of every layer tied to its code, and a suite that proves what the app does by running it. | [goals/G-011-docs-restructuring.md](goals/G-011-docs-restructuring.md) |

**Status:** `not started` | `in progress` | `done` | `abandoned`

Procedure: [../protocol/flows/goal.md](../protocol/flows/goal.md)
