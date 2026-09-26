<!--
  File: docs/paperwork/backlog.md
  Purpose: Work candidates not yet promoted to Goals/Steps
  Audience: Humans and agents
  Update when: Backlog changes
-->

# Backlog

Ideas that are not Goals yet. Nothing here is in progress, and a row is never permission to build. An idea becomes work only when a Goal that contains it is approved. Rows are never deleted: a promoted or settled idea stays, so the list shows where every idea went.

| Idea | What it means | Where it stands |
|------|---------------|-----------------|
| Non-finishing System policy | What a step should do when a system claims work and never finishes it: wait, give up after a time, or settle without that system's output. Today every system runs to the end before a step settles. | Deferred from G-001. Needs a decision |
| Delete Request vs concurrent write | Whether a request to delete a value wins or loses against another system writing that value in the same step. The engine has no delete yet, so the choice has not been needed. | Deferred from G-001 |
| Category tree authorship rules | The rules for how the product names and nests the categories of events its systems listen for. ADR-009 already settled that the product, not the engine, builds the tree. | Application-level. To be taken up with a product Goal |
| Explore / Guide / Timeline journeys | The three ways of working the concept promises: rolling worlds until one feels right, nudging one feature while the rest of the world follows, and scrubbing through a world's history. | After the webview front and a living core of height |
| Product world generation (grid + tectonics) | A world of its own on the engine: a grid, plates, and relief. | Promoted to [G-003](goals/G-003-first-product-world.md) |
| See the world (Voronoi + large map UI) | Many plates, and a large coloured map to watch them on. | Promoted to [G-004](goals/G-004-see-the-world.md) |
| Living map (house + motion + tool UI + CLI) | Plates that move, a tool window, and a command line, all around one running world. | Promoted to [G-005](goals/G-005-living-map.md) |
| Local webview front (Tauri + Next + Java host) | The window rebuilt as a local web page inside a desktop shell. | Promoted to [G-006](goals/G-006-webview-front.md) |
| Studio cartography tool (redesign + QoL) | A map-first studio that is pleasant to work in. | Promoted to [G-007](goals/G-007-studio-cartography.md) |
| Boundary tectonics + cartography studio | Plates that meet, grow, split, and die, on a large world, in a fuller studio. | Promoted to [G-008](goals/G-008-boundary-tectonics-studio.md) |
| Simulation runner harden | A runner that tells the truth about plates, poles, cost, and control. | Promoted to [G-009](goals/G-009-simulation-runner-harden.md) |
| Crust topology | Land as crust that rides the plates, so that continents can form. | Promoted to [G-010](goals/G-010-crust-topology.md) |
| Docs restructuring (four shelves) | The documentation sorted onto four shelves behind one entrance. | Promoted to [G-011](goals/G-011-docs-restructuring.md) |
| Climate / further generation layers | Wind, rain, temperature, and biomes, grown from the land the plates make. | After G-011 |
| Package naming convention | How the packages of the code are named. | Settled by F-001 and ADR-007: each module's packages start with `com.aethelgard.<module>` |
