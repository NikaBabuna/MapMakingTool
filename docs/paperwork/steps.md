<!--
  File: docs/paperwork/steps.md
  Purpose: Step registry (F-0xx) grouped by Goal
  Audience: Agents and humans
  Update when: Steps are added or change status
-->

# Steps (feature registry)

A **Step** is one job under a **Goal**: agree the work, store its requirements, do it, prove it, and record it. This registry lists every Step, grouped by the Goal it belongs to, with its status and one sentence on what it does. The Step's own record, `docs/paperwork/steps/F-0xx.md`, holds the approved job, the decisions, the requirements, the test that proves each one, and the witness. A record is written when the Step is approved, before any work starts.

**Incremental:** Accept requires this Step's tests **and** all earlier Accepted Steps' tests to stay green, except tests a Step record retires or lists as known defects.

Procedure: [../protocol/flows/step.md](../protocol/flows/step.md)

**Status values:** `not started` (planned, no record yet) | `in progress` (approved and underway; at most one) | `done` (proven and closed) | `rolled back` (the attempt was discarded)

---

## G-001 — Engine skeleton

Goal doc: [goals/G-001-engine-skeleton.md](goals/G-001-engine-skeleton.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-001 | Layout + Maven scaffold | done | Set up the build: one repository with an engine module, its names and Java version recorded, and a test run that passes. | [F-001.md](steps/F-001.md) |
| F-002 | Pool + Step loop + Step 0 config | done | Gave the engine a heartbeat: its state advances in discrete steps, and the first step is set up only from a configuration the caller supplies. | [F-002.md](steps/F-002.md) |
| F-003 | Events + claiming + unmatched log + logging foundation | done | Added events: systems claim the kinds of event they handle, an event nobody claims is logged, and the engine reports what it does as it runs. | [F-003.md](steps/F-003.md) |
| F-004 | Systems + typed merge + provenance | done | Added systems that write into each step's output, merged into the engine's state by typed rules that remember where each value came from. | [F-004.md](steps/F-004.md) |
| F-005 | Claim/finish + determinism witness | done | Made every system finish before a step settles, and proved that the same setup always settles to the same state, whatever order the systems run in. | [F-005.md](steps/F-005.md) |
| F-006 | User Input / Input View / User View | done | Added a user layer: input the engine samples once per step, and a view that reads only settled state after each step. | [F-006.md](steps/F-006.md) |
| F-007 | CLI runner | done | Added a command-line runner that creates an engine, advances it a number of steps, and prints the settled state. | [F-007.md](steps/F-007.md) |
| F-008 | Basic UI + G-001 closure | done | Added a basic window that advances steps and shows the settled state, and closed the Goal. | [F-008.md](steps/F-008.md) |
| F-009 | CI pipeline | done | Made every push and pull request to the main branch run the same test suite as a local run. | [F-009.md](steps/F-009.md) |

---

## G-002 — Engine host readiness

Goal doc: [goals/G-002-engine-host-readiness.md](goals/G-002-engine-host-readiness.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-010 | Pluggable Pool compute | done | Let a product replace how the engine's state is updated each step, without editing the engine, and kept the old behaviour as the default. | [F-010.md](steps/F-010.md) |
| F-011 | Wider Pool field carrier + pluggable merge types | done | Let the engine's fields hold any kind of value, and let a product add its own rules for merging them. | [F-011.md](steps/F-011.md) |
| F-012 | Pluggable event emission + G-002 closure | done | Let a product decide which events are emitted each step, without editing the engine, and closed the Goal. | [F-012.md](steps/F-012.md) |

---

## G-003 — First product world

Goal doc: [goals/G-003-first-product-world.md](goals/G-003-first-product-world.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-013 | Product Maven module + architecture | done | Created the product as its own module on top of the engine, with its layout recorded. | [F-013.md](steps/F-013.md) |
| F-014 | World as Pool state (grid + elevation) | done | Put the world into the engine's state: a shared grid and a height layer, flat at the first step. | [F-014.md](steps/F-014.md) |
| F-015 | First generative process | done | Added the first process that shapes the world: relief rises where two plates meet, by a rule written in the wiki. | [F-015.md](steps/F-015.md) |
| F-016 | Witnessed world + G-003 closure | done | Added a text dump of the world, used it to prove that the same seed always grows the same world, and closed the Goal. | [F-016.md](steps/F-016.md) |

---

## G-004 — See the world

Goal doc: [goals/G-004-see-the-world.md](goals/G-004-see-the-world.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-017 | Voronoi multi-plate tectonics | done | Replaced the two-plate world with many plates, each cell belonging to the nearest plate origin. | [F-017.md](steps/F-017.md) |
| F-018 | Large colored map UI + loading | done | Added a window that paints a large height map in colour and advances the world without freezing, showing when it is busy. | [F-018.md](steps/F-018.md) |

---

## G-005 — Living map

Goal doc: [goals/G-005-living-map.md](goals/G-005-living-map.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-019 | Product session + UI/CLI house | done | Gave the world one session that owns the engine, shared by the window and the command line. | [F-019.md](steps/F-019.md) |
| F-020 | Plate kinematics | done | Made plates drift: each plate takes a velocity from the seed and carries its cells with it every step. | [F-020.md](steps/F-020.md) |
| F-021 | Motion-based orogeny | done | Made relief follow motion: converging plates raise the land and diverging plates lower it. | [F-021.md](steps/F-021.md) |
| F-022 | Tool UI | done | Turned the window into a tool: a dark frame, ocean and hill shading, views, play and speed, seed and new world, click to inspect, and a legend. | [F-022.md](steps/F-022.md) |
| F-023 | Placeholder CLI + in-UI console | done | Added a small set of commands, usable from the command line and from a console in the window, and closed the Goal. | [F-023.md](steps/F-023.md) |

---

## G-006 — Local webview front

Goal doc: [goals/G-006-webview-front.md](goals/G-006-webview-front.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-024 | Java session HTTP host | done | Put the world behind a small web server on the local machine, so any front can drive it. | [F-024.md](steps/F-024.md) |
| F-025 | Next.js tool UI | done | Built a web page with the old window's behaviour and a better look, talking to that server. | [F-025.md](steps/F-025.md) |
| F-026 | Tauri shell + remove Swing; close G-006 | done | Wrapped the web page in a desktop app that starts and stops the world, removed the old window, and closed the Goal. | [F-026.md](steps/F-026.md) |

---

## G-007 — Studio cartography tool

Goal doc: [goals/G-007-studio-cartography.md](goals/G-007-studio-cartography.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-027 | Style guide + studio chrome + map-first shell | done | Wrote the style guide and rebuilt the page as a map-first studio with a thin top bar and a collapsible side dock. | [F-027.md](steps/F-027.md) |
| F-028 | Pan / zoom + cell pick + reset view | done | Added panning, zooming towards the pointer, resetting the view, and clicking a cell through the zoomed view. | [F-028.md](steps/F-028.md) |
| F-029 | Shortcuts, seed QoL, feedback, a11y; close G-007 | done | Added keyboard shortcuts, random seeds, a confirmation before replacing a world that has moved, busy and offline feedback, and accessibility, and closed the Goal. | [F-029.md](steps/F-029.md) |

---

## G-008 — Boundary tectonics + cartography studio

Goal doc: [goals/G-008-boundary-tectonics-studio.md](goals/G-008-boundary-tectonics-studio.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-030 | Wiki + decisions (G-008 foundations) | done | Wrote down the Goal's world rules and decisions before any code changed: a wrapping world, its size, the first plates, and how plates split and die. | [F-030.md](steps/F-030.md) |
| F-031 | Large rectangular world (1920×1080) | done | Made the studio's world 1920 by 1080 cells. | [F-031.md](steps/F-031.md) |
| F-032 | Torus camera (loopback pan + zoom clamp) | done | Made the camera loop around when panning, and stop zooming out at the whole map. | [F-032.md](steps/F-032.md) |
| F-033 | Initial plate partition + registry skeleton | done | Grew 12 to 24 first plates per seed on a wrapping map, and kept a record of each plate's size and first velocity. | [F-033.md](steps/F-033.md) |
| F-034 | Boundary trace + classify (+ cylinder map) | done | Found every place two plates touch and classed it as pulling apart, colliding, or sliding past, and stopped the map wrapping from north to south. | [F-034.md](steps/F-034.md) |
| F-035 | Precedence + area flux + motion intent | done | Decided which plate gives way in a collision, and turned each boundary into how much each plate should grow or shrink and which way it is pushed. | [F-035.md](steps/F-035.md) |
| F-036 | Apply flux, flood, fission, death (+ B1 map distance) | done | Applied those changes to the plates, so they grow, shrink, fill gaps, split into pieces, and die. | [F-036.md](steps/F-036.md) |
| F-037 | Edge-driven velocity integrate | done | Made plate velocities change each step with what their edges push, instead of staying fixed from the seed. | [F-037.md](steps/F-037.md) |
| F-038 | Orogeny from standing boundaries (+ status / camera / plates) | done | Raised and lowered relief from the classified boundaries, fast enough for the large world, and kept the studio responsive while a step runs. | [F-038.md](steps/F-038.md) |
| F-039 | Multi-panel studio + mappy style | done | Split the side dock into Inspect and Legend panels, and gave the map a neatline, a graticule, and coordinates. | [F-039.md](steps/F-039.md) |
| F-040 | Traditional console; close G-008 | done | Restyled the console as a terminal with an `aethelgard>` prompt and a command history, and closed the Goal. | [F-040.md](steps/F-040.md) |

---

## G-009 — Simulation runner harden

Goal doc: [goals/G-009-simulation-runner-harden.md](goals/G-009-simulation-runner-harden.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-041 | Docs lock (G-009 foundations) | done | Wrote down the Goal's rules before code changed: rifts filled by the two plates that part, poles joined as on a sphere, and one surface for commands and measurements. | [F-041.md](steps/F-041.md) |
| F-042 | Diagnostics infrastructure | done | Added named measurements that the running world keeps, such as step time and memory, which can be switched on, off, and queried. | [F-042.md](steps/F-042.md) |
| F-043 | Ridge accretion (diverge fill) | done | Filled a rift's gap with new crust from the two plates that part, instead of from whichever plate is nearest. | [F-043.md](steps/F-043.md) |
| F-044 | Slivers + border read (+ triple-junction fill) | done | Filled gaps where three plates meet from the plates around them, absorbed thin slivers, and drew plate borders one cell thick. | [F-044.md](steps/F-044.md) |
| F-045 | Sphere topology (+ bold / natural borders) | done | Joined the poles as on a sphere in motion, boundaries, painting, and the camera, and drew borders bolder and fronts ragged. | [F-045.md](steps/F-045.md) |
| F-046 | Step path hotspots + crumb retune | done | Measured each phase of a step, fixed a slow spot, and set tiny plate pieces to be absorbed below 0.01% of the map. | [F-046.md](steps/F-046.md) |
| F-047 | Raster + host memory | done | Cut memory churn when painting the map and sending it to the page, with the pictures unchanged. | [F-047.md](steps/F-047.md) |
| F-048 | Noun/verb command language | done | Replaced the first small command set with one noun-and-verb language for the command line and the terminal. | [F-048.md](steps/F-048.md) |
| F-049 | CLI as full runner | done | Made the command line a full runner of one world: set it up, advance it, question it, and dump it, all in that language. | [F-049.md](steps/F-049.md) |
| F-050 | Scrap + rebuild terminal | done | Rebuilt the in-app terminal around that language, with help, history, and a map that refreshes after an advance. | [F-050.md](steps/F-050.md) |
| F-051 | Runner shell (foundation) | done | Turned the studio into a quiet runner: a gray frame, Play, Pause, and speeds, a World rail, a layer switch, and atlas colours on the map. | [F-051.md](steps/F-051.md) |
| F-052 | Perf rail + runner fixes | done | Added the Perf rail, kept the terminal always open, moved the layer switch to the top left, and brightened the height colours. | [F-052.md](steps/F-052.md) |
| F-053 | Runner UI infrastructure + QoL | done | Made panels and menus into lists the studio reads, made the layout resizable and remembered, and added five conveniences. | [F-053.md](steps/F-053.md) |
| F-054 | Goal close + layer harden + doc hygiene | done | Fixed races between switching views and advancing, brought the product pages up to date, and closed the Goal. | [F-054.md](steps/F-054.md) |

---

## G-010 — Crust topology

Goal doc: [goals/G-010-crust-topology.md](goals/G-010-crust-topology.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-055 | Docs lock (G-010 foundations) | done | Wrote down the Goal's crust rules before code changed: crust carried by plates, height from thickness, ridges, subduction, sutures, and a cap. | [F-055.md](steps/F-055.md) |
| F-056 | Occupancy keys + lockers + ride + isostasy | done | Made the crust ride with the plates, and read height from its thickness instead of painting it where plates touch. | [F-056.md](steps/F-056.md) |
| F-057 | Ridge mint + Simulation restart | done | Gave rifts new thin ocean crust instead of a neighbour's mountain, and added Restart UI and Restart engine. | [F-057.md](steps/F-057.md) |
| F-058 | Buoyancy precedence + oceanic subduction + SEPARATE mint | done | Made ocean crust go under at collisions while continents stay, and gave every new rift cell fresh ocean crust. | [F-058.md](steps/F-058.md) |
| F-059 | Margin relief | done | Shaped the ocean beside boundaries: a trough beside each rift, and a short slope beside each collision. | [F-059.md](steps/F-059.md) |
| F-060 | Continental suture + arc + cap | done | Let ocean collisions raise arcs of new land and continental collisions thicken both sides, up to a cap. | [F-060.md](steps/F-060.md) |
| F-061 | Goal close + dump/wiki/UI hygiene | done | Proved that height is read from crust thickness everywhere, renamed the Overlay swatch to Boundary, and closed the Goal. | [F-061.md](steps/F-061.md) |

---

## G-011 — Docs restructuring

Goal doc: [goals/G-011-docs-restructuring.md](goals/G-011-docs-restructuring.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-062 | Protocol shelf | done | Gathered the rules of conduct into one protocol folder, retired the session file, and kept the Active Goal on the Goal index alone. | [F-062.md](steps/F-062.md) |
| F-063 | Protocol pages rewritten in full | done | Rewrote every protocol page so each rule, flow, and blueprint can be followed without guessing. | [F-063.md](steps/F-063.md) |
| F-064 | Conceptual product shelf | done | Rewrote the product pages so a person can read about the world without meeting the program. | [F-064.md](steps/F-064.md) |
| F-065 | Architecture paper | done | Wrote the implementation as one paper arranged by level of detail, each page stating its mechanism and pointing at the finer page. | [F-065.md](steps/F-065.md) |
| F-066 | Paperwork shelf | done | Put progress records on one shelf, one file per record and one file per list. | [F-066.md](steps/F-066.md) |
| F-067 | Architecture paper of the whole app | done | Describes the whole app as one engineering paper, every layer in plain English and in mathematics, backed by the code it cites. | [F-067.md](steps/F-067.md) |
| F-068 | Functional test suite | done | Replaces the test suite with tests that prove the app's requirements by running its code, and closes the Goal. | [F-068.md](steps/F-068.md) |

---

## G-012 — Code structure and conventions

Goal doc: [goals/G-012-code-structure-and-conventions.md](goals/G-012-code-structure-and-conventions.md)

| ID | Name | Status | What it does | Record |
|----|------|--------|--------------|--------|
| F-069 | Conventions page | done | Gives the project one page of code conventions, for every language it uses, and makes that page a standing part of every project the protocol governs. | [F-069.md](steps/F-069.md) |
| F-070 | Folder and code rules in the protocol | done | Makes every agent give each folder it creates an introduction and a place on the map, keep code one job per folder under the project's conventions, and leave the paper to concept and mathematics. | [F-070.md](steps/F-070.md) |
| F-071 | Product module organised | done | Sorts the product module into packages that follow the paper, names every part by the conventions, and gives each of its folders an introduction. | [F-071.md](steps/F-071.md) |
| F-072 | Engine and command line organised | not started | The engine and command-line modules: the same treatment, with their pages. | — |
| F-073 | Studio and tooling organised | not started | The ui module (Java host, web front, desktop shell) and the build and tooling folders: the same, with the studio pages. | — |
| F-074 | Whole-tree audit and close | not started | Whole-tree audit against the new rules, Why parts added to every docs README, then close the Goal. | — |

---

## Marking progress

- Set Status to `in progress` before any work on that Step, in the record, here, and on the Goal.  
- Set Status to `done` only after the witness is green and Sync is complete. The `done` marks go into the Step's Accept commit.  
- A torn Step found by Reconcile is rolled back, not continued.
