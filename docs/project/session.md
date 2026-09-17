<!--
  File: docs/project/session.md
  Purpose: Temporary focus for the current AI chat — rewrite each session
  Audience: AI agents (read first after PHASE)
  Update when: Session starts, Step marks change, Session ends
-->

# Current session

**Updated:** 2026-09-17  
**Status:** F-021 Accepted

---

## Session goal

Accept **F-021** (motion-based orogeny: converge / diverge / transform; negative elevation).

---

## Active Goal

**Active Goal:** [G-005 — Living map](goals/G-005-living-map.md) — **in progress**  
**Last completed:** [G-004 — See the world](goals/G-004-see-the-world.md) — **done**

---

## Steps in this Session

| Step | Work | Status |
|------|------|--------|
| F-021 | Motion-based orogeny | done |

---

## Torn-Step check

- [x] No Step marked `in progress`
- [x] Incremental suite green (129 tests, last Accept F-021)

---

## Next Session (suggested)

1. Propose **F-022** (tool UI) job + FRs.

## Notes

- CLI commands are **placeholders**; do not bake verbs into product.
- UI must include a **console** control that uses the same thin CLI layer (F-023).
- Map still clamps elevation to `[0, 32]` until F-022 (negatives exist in the grid).
