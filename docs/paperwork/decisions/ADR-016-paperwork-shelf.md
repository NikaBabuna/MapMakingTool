<!--
  File: docs/paperwork/decisions/ADR-016-paperwork-shelf.md
  Purpose: Decision record ADR-016
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-016 — Paperwork shelf

**Date:** 2026-09-22
**Status:** accepted
**Amended by:** [ADR-018](ADR-018-scope-in-concept.md)

Progress records live in `docs/paperwork/`. A record that has its own id, a status, and a life of its own is one file. A list whose value is seeing the whole sequence at once stays one file.

| Kind | Where |
|------|--------|
| Goal index | `docs/paperwork/goals.md`. This is the only Active Goal line |
| Goal | `docs/paperwork/goals/G-0xx-<slug>.md` |
| Step registry | `docs/paperwork/steps.md` |
| Step record | `docs/paperwork/steps/F-0xx.md` |
| Decision index | `docs/paperwork/decisions.md`. The next number is one higher than the last row |
| Decision | `docs/paperwork/decisions/ADR-0xx-<slug>.md` |
| Changelog, roadmap, backlog | one file each, beside the indexes |

The folder door of `goals/`, `steps/`, or `decisions/` points at the index and the blueprint. It does not copy the status table. Scope stays `docs/project/project.md`.

**Why:** Goals and Steps were already one file each, while every decision was appended to one log, and the step list was copied onto a second door that fell behind. An index keeps the sequence and the next number. A second status table does not.

**Amends:** [ADR-014](ADR-014-protocol-shelves.md) — the Active Goal line moves from `docs/project/goals.md` to `docs/paperwork/goals.md`.

**Goal:** [G-011 Docs restructuring](../goals/G-011-docs-restructuring.md)
