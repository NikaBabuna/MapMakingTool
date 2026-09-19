<!--
  File: docs/project/session.md
  Purpose: Temporary focus for the current AI chat — rewrite each session
  Audience: AI agents (read first after PHASE)
  Update when: Session starts, Step marks change, Session ends
-->

# Current session

**Updated:** 2026-09-19  
**Status:** F-047 Accepted · Active Goal G-009 · next negotiate F-048

---

## Session goal

Continue **G-009** one Step at a time (negotiate → approve → Accept).

---

## Active Goal

**Active Goal:** [G-009 — Simulation runner harden](goals/G-009-simulation-runner-harden.md)  
**Last completed Goal:** [G-008](goals/G-008-boundary-tectonics-studio.md) — **done**

---

## Steps in this Session

| Step | Work | Status |
|------|------|--------|
| F-046 | Step path hotspots + crumb 0.01% | done |
| F-047 | Raster + host memory | done |

---

## Torn-Step check

- [x] No Step marked `in progress`
- [x] F-047 Accepted (witness + SYNC)

---

## Notes

- F-047: flat ElevationRaster; MapController double-buffer reuse; MapHost packed-body cache (O(1) soak).
