<!--
  File: docs/project/session.md
  Purpose: Temporary focus for the current AI chat — rewrite each session
  Audience: AI agents (read first after PHASE)
  Update when: Session starts, Step marks change, Session ends
-->

# Current session

**Updated:** 2026-09-19  
**Status:** F-024 Accepted · propose F-025 next

---

## Session goal

Accept **F-024** (Java session HTTP host under `ui`).

---

## Active Goal

**Active Goal:** [G-006 — Local webview front](goals/G-006-webview-front.md) — **in progress**  
**Last completed:** [G-005 — Living map](goals/G-005-living-map.md) — **done**

---

## Steps in this Session

| Step | Work | Status |
|------|------|--------|
| F-024 | Java session HTTP host (MapController parity) | done |

---

## Torn-Step check

- [x] No Step marked `in progress`
- [x] Incremental suite green (F-024 Accept)

---

## Next Session (suggested)

1. Propose **F-025** — Next.js tool UI parity against `MapHost`.

## Notes

- Host lives under `ui` (`com.aethelgard.ui.host`). No root `host/` module.
- Swing map still primary until F-026.
