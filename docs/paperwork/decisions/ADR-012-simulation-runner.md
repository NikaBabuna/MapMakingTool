<!--
  File: docs/paperwork/decisions/ADR-012-simulation-runner.md
  Purpose: Decision record ADR-012
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-012 — Simulation runner harden (G-009 locks)

**Date:** 2026-09-19  
**Status:** accepted

G-009 hardens the living map into a **simulation runner**. Locks (docs F-041; code in later Steps):

| Topic | Decision |
|-------|----------|
| **Diverge** | SEPARATE gaps must not be filled by nearest arbitrary third plate. New crust at separate contacts belongs only to the **two contacting plates** (ridge accretion). |
| **Poles** | Replace G-008 **cylinder** hard-Y with **sphere-on-rectangle**: crossing north re-enters from the north at antipodal longitude (heading flips); same for south. Advection, neighbors, distance, fission, and camera must agree. |
| **Commands** | One command language for **CLI + in-app terminal**; studio panels are faces on the same session — not a second API. Placeholder verbs (ADR-010) are replaced under this Goal. |
| **Observability** | Per-Step timings, memory snapshots, and key counters are **recorded and queryable** via that command surface (not file-only). |

**Amends:** G-008 wiki cylinder topology (F-034) for poles — sphere polar wrap supersedes hard polar drop. ADR-010 “commands are placeholders” is retired as the end-state of G-009 (F-048+).

**Out of scope:** climate/biomes; Explore/Guide/Timeline modes; 3D globe mesh; engine framework ports.

**Why:** G-008 shipped working tectonics and studio chrome, but void-fill, polar drop, black stacking borders, placeholder CLI, and invisible cost keep the product from feeling like a real simulation environment.

**Goal:** [G-009 Simulation runner harden](../goals/G-009-simulation-runner-harden.md)
