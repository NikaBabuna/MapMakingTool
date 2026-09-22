<!--
  File: docs/paperwork/goals/G-007-studio-cartography.md
  Purpose: Multi-session Goal — studio cartography tool redesign + QoL
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-007 — Studio cartography tool

**Status:** `done`  
**Engine:** do not edit `engine` source for ordinary feature growth (G-002 host ports).  
**Prior:** [G-006](G-006-webview-front.md) delivered Tauri + Next + MapHost with an elevated marketing-style chrome. This Goal redesigned the **front** into a studio cartography workspace and shipped QoL — without changing world rules or MapHost contracts.

**Approved:** 2026-09-19 (user). Direction: **studio cartography** + full QoL (layout, pan/zoom, shortcuts, seed UX, feedback, a11y).

---

## Result we want

Open the desktop/Next tool and read it as a **map studio**, not a branded landing page. Map-dominant layout, dense docks, restrained type. Pan/zoom, shortcuts, seed/world QoL, busy/offline polish, and accessible controls.

When this Goal is `done`:

1. **Style.** [`docs/product/style-guide.md`](../../product/style-guide.md) specifies studio cartography (cool slate ink, fog panels, one accent; quiet wordmark; no purple-gradient / cream+terracotta / broadsheet).
2. **Layout.** Full-bleed map stage; thin top bar; collapsible right dock (Inspect + Legend, `localStorage`); Console on demand (drawer/toggle).
3. **Pan / zoom.** Wheel zoom toward cursor; drag pan; reset view; inspect clicks map through viewport transform.
4. **Shortcuts.** Documented keys: Space Play/Pause; `A`/`.` Advance; `1`/`2`/`3` layers; `[`/`]` speed; `N` New world; `` ` ``/`C` console; `D` dock; `R` reset view.
5. **Seed / world.** Random-seed control; Enter commits seed; confirm New world when Step > 0.
6. **Feedback + a11y.** Map busy overlay; offline banner + Retry; visible focus; toolbar aria; keyboard-reachable actions; `prefers-reduced-motion`.
7. **Unchanged world / host.** No `engine` edits; MapHost HTTP contracts unchanged; same seed + size + N Steps → identical fields. Incremental suite green.

Plain English: same living map, better tool window.

---

## Out of scope (this Goal)

- Climate, biomes, continent redesign, timeline / Guide / Explore flows
- Multiplayer, remote host, auth
- New MapHost endpoints or command-language expansion
- Engine ports / Systems / Pool changes

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Visual | Studio cartography (see style-guide) |
| Layout | Map-first; collapsible dock; console drawer |
| QoL | Pan/zoom, shortcuts, seed UX, feedback, a11y — all in this Goal |
| Host / engine | Unchanged |
| Code touch | `ui/web` + product docs |

---

## Product claims (tests by Goal end)

- [x] Style guide written; chrome matches studio direction
- [x] Map-first layout; dock collapse persisted; console on demand
- [x] Pan/zoom + correct cell pick + reset
- [x] Shortcuts + seed QoL + busy/offline + a11y
- [x] Same seed + spec + N Steps → identical fields; no `engine` production edits
- [x] Incremental suite: all prior Accepted Step tests remain green

---

## Planned Steps

| Step | Intent | Status |
|------|--------|--------|
| F-027 | Style guide + studio chrome + map-first shell (collapsible dock, console drawer) | done |
| F-028 | Pan / zoom + cell pick under transform + reset view | done |
| F-029 | Shortcuts, seed/world QoL, feedback, a11y; close G-007 | done |

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 3 / 3 |
| Claim boxes | 6 / 6 |
| Last Accept | F-029 |
