<!--
  File: docs/paperwork/decisions/ADR-017-architecture-paper.md
  Purpose: Decision record ADR-017
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-017 — Architecture paper by abstraction

**Date:** 2026-09-22
**Status:** accepted

The implementation record is one paper at `docs/architecture/`, arranged by abstraction level.

| Level | Path | What it owns |
|-------|------|----------------|
| Abstract | `docs/architecture/README.md` | The four levels. No procedures |
| Program | `docs/architecture/program.md` | Modules and the dependency direction |
| Host | `docs/architecture/host/` | One engine step, and one page per host mechanism |
| World | `docs/architecture/world/` | One generation, and one page per grid-writing procedure |
| Studio | `docs/architecture/studio/` | Session, raster, and the HTTP host |

A page states its own mechanism and points at the finer page. The procedure lives on that finer page. `docs/architecture/world/crust/<page>.md` is the fourth level allowed on this shelf. The changelog keeps Step history. The paper does not.

**Why:** A folder of moved specs still read as a log of which Step added which sentence. An agent could not see the machine without reconstructing it. A parent that repeats every child goes stale the first time a child changes.

**Goal:** [G-011 Docs restructuring](../goals/G-011-docs-restructuring.md)
