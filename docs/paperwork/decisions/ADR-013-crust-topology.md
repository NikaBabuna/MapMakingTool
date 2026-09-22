<!--
  File: docs/paperwork/decisions/ADR-013-crust-topology.md
  Purpose: Decision record ADR-013
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-013 — Crust topology (G-010 locks)

**Date:** 2026-09-20  
**Status:** accepted

G-010 makes **crust material that rides plates** so continents can form. Locks (docs F-055; code in later Steps):

| Topic | Decision |
|-------|----------|
| **Assembly** | One `tectonics` `EngineSystem`. Crust Sub-Systems run **after** occupancy (same-Step staging). Not a second System this Goal. |
| **State** | Occupancy **keys** (cell → locker id) + **lockers** (id → thickness). Motion remaps keys. Contacts mint/merge lockers and increment thickness in **locker-id** space. |
| **Elevation** | Derived (integer isostasy of thickness at current keys). Contact-paint **Orogeny** is retired as the elevation author (code F-056+). |
| **Ridge mint** | New occupancy at SEPARATE / gaps gets thin oceanic crust (\(T_{ocean}\)). Gaps do not inherit a neighbor’s mountain. |
| **Buoyancy** | Thickness \(\ge T_{land}\) is continental; below is oceanic. COLLIDE: oceanic subducts; continental does not die because its plate is smaller. Ocean–ocean keeps smaller-loses. |
| **Suture / arc** | Ocean–ocean may thicken an arc on the winner. Continent–continent thickens both sides; neither locker dies. Thickness cap / light erosion so growth is bounded. |
| **Step 0** | All oceanic (\(T_{ocean}\)). No painted cratons. Continents emerge from arcs + suture. |
| **Weld** | No plate-id merge on suture. Thickness is the continent. |
| **Merge** | Occupancy STATIC (one writer). Lockers may use a product **custom** `FieldMergeType`. No `engine` source edits. |

**Amends:** G-008 collide precedence “smaller plate loses (no types yet)” and F-038 contact-paint orogeny as the **relief author** — superseded on paper for G-010; buoyancy runtime F-058; margin relief runtime F-059; suture runtime F-060.

**Out of scope:** climate/biomes; Explore/Guide/Timeline; second topology System; scratch-pad wait-notify; plate weld; age/sediment/plumes; 3D globe; engine framework ports.

**Why:** The runner shows moving plates, but elevation is paint on old contacts. Continents cannot accumulate. Crust must be cargo on occupancy, created thin at ridges, and destroyed only when it can subduct.

**Goal:** [G-010 Crust topology](../goals/G-010-crust-topology.md)
