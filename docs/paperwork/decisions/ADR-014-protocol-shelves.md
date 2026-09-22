<!--
  File: docs/paperwork/decisions/ADR-014-protocol-shelves.md
  Purpose: Decision record ADR-014
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-014 — Protocol shelves; session retired

**Date:** 2026-09-22  
**Status:** accepted

Conduct lives in `docs/protocol/` in four rooms: environment, navigation, blueprints, and flows. An agent writes only by a flow named in `environment/dispatch.md`. Each document kind has one blueprint.

Work has two levels: Goal and Step. Session is not a work level. `docs/project/session.md` is removed. The Active Goal line lives only on `docs/project/goals.md`. Doors point at that index and at `docs/protocol/README.md`.

The architecture shelf may use four docs levels. Other shelves stay within three.

**Supersedes:** the Session row of ADR-004. ADR-004’s order (approve, mark, witness, sync, commit, roll back torn work) remains, and now lives in the flow pages.

**Why:** Session repeated the Goal and the Step table. Conduct files mixed law, checklists, and history, so a Step spent its context recopying banners.

**Goal:** [G-011 Docs restructuring](../goals/G-011-docs-restructuring.md)


**Amended by:** [ADR-016](ADR-016-paperwork-shelf.md) — the Active Goal line lives on `docs/paperwork/goals.md`.
