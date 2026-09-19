<!--
  File: docs/project/session.md
  Purpose: Temporary focus for the current AI chat — rewrite each session
  Audience: AI agents (read first after PHASE)
  Update when: Session starts, Step marks change, Session ends
-->

# Current session

**Updated:** 2026-09-19  
**Status:** F-037 Accepted · next F-038

---

## Session goal

Ship **G-008** — boundary tectonics + large cylinder world + cartography studio (F-030 → F-040).

---

## Active Goal

**Active Goal:** [G-008 — Boundary tectonics + cartography studio](goals/G-008-boundary-tectonics-studio.md) — **in progress**  
**Last completed:** [G-007 — Studio cartography tool](goals/G-007-studio-cartography.md) — **done**

---

## Steps in this Session

| Step | Work | Status |
|------|------|--------|
| F-030–F-037 | Foundations through edge-driven velocities | done |
| F-038–F-040 | Orogeny → studio → Goal close | not started |

---

## Torn-Step check

- [x] No Step marked `in progress`
- [x] Incremental suite green (F-037 Accept)

---

## Notes

- Edge-driven integrate shipped in F-037.
- **F-038 rolled back** (`f5f01f0` revert): boundary orogeny was O(cells×contacts) — ~78s/Advance on VIEW; status polls blocked on session lock. Re-negotiate F-038 with O(contacts) relief + non-blocking status before retry.
- Next: **F-038** (orogeny + optional UI tweaks), redesigned.
