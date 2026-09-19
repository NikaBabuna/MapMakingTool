<!--
  File: docs/project/session.md
  Purpose: Temporary focus for the current AI chat — rewrite each session
  Audience: AI agents (read first after PHASE)
  Update when: Session starts, Step marks change, Session ends
-->

# Current session

**Updated:** 2026-09-20  
**Status:** F-053 Accepted · Active Goal G-009 · next Step **F-054** (Goal close)

---

## Session goal

Accept **F-052** (perf rail + runner fixes) and **F-053** (runner UI infrastructure + QoL) under G-009. Both done.

---

## Active Goal

**Active Goal:** [G-009 — Simulation runner harden](goals/G-009-simulation-runner-harden.md)  
**Last completed Goal:** [G-008](goals/G-008-boundary-tectonics-studio.md) — **done**

---

## Steps in this Session

| Step | Work | Status |
|------|------|--------|
| F-052 | Perf rail + runner fixes | done |
| F-053 | Runner UI infrastructure + QoL | done |

---

## Torn-Step check

- [x] F-053 marked `in progress` before code
- [x] F-053 Accepted (witness + SYNC)

---

## Notes

- F-053 shipped descriptor-driven chrome: `lib/panels.ts` registry + shared `Panel.tsx`, `lib/menus.ts` menu-bar row, `lib/layout.ts` resizable persisted sizes, `lib/shortcuts.ts` key source.
- Panel/rail persistence keys renamed: `aethelgard.panel.<id>.open`, `aethelgard.rail.<side>.open`, `aethelgard.layout.*`. Prior structural greps were moved to those files (claims unchanged).
- Palette brighter: ocean `(24,64,104)`→`(110,190,226)`, land `(150,196,120)`→`(255,250,236)`; `OCEAN_RGB` is now `0x6ebee2`. Hillshade untouched.
- Only F-054 (Goal close) remains in G-009; 13 / 14 Steps done, 7 / 8 claim boxes.
