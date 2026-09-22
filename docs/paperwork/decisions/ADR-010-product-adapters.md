<!--
  File: docs/paperwork/decisions/ADR-010-product-adapters.md
  Purpose: Decision record ADR-010
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-010 — UI and CLI are product adapters

**Date:** 2026-09-17  
**Status:** accepted

`ui` and `cli` depend on `product`. `product` depends on `engine`. `engine` never depends on `ui`, `cli`, or `product`.

| Module | Role |
|--------|------|
| **engine** | Abstract Pool-System loop |
| **product** | Aethelgard simulation — field values, Systems, session. **No Swing** |
| **ui** | View of those values (map window, tool chrome) |
| **cli** | Operator access — full headless runner + shared noun/verb language (F-048–F-049) |

`ui` may depend on `cli` **only** to reuse that command layer for an **in-window console**. `cli` must not depend on `ui`.

**Live access (G-005):** one in-process **session** owns the `Engine`. UI, console, and headless CLI call it. Advances are serialized. No socket.

**Commands (amended F-048 / F-049 / G-009):** Shared **noun-path + verb** language in `cli` (`session`, `pool`, `schema`, `systems`, `diag` + `list`/`get`/`advance`/…). Headless `CliRunner` owns one session per invocation (`--seed` / `--steps` / `-c`). Deprecated flat aliases (`status`, `advance`, …) remain through G-009. Do **not** put command names into product Systems, Pool fields, or merge types.

Skeleton heartbeat `ui` / `cli` as the product experience is retired (G-001 adapters were scaffolding). Engine tests still witness the loop without Aethelgard.

**Amends:** [ADR-007](ADR-007-modules-and-java-21.md) — sibling modules may depend on `product`, not only on `engine`. Engine still never depends on siblings. **F-048** retires “commands are placeholders only” for the noun/verb catalog; **F-049** retires the headless CLI as a dump-only placeholder (ADR-012).

**Why:** The engine is abstract. Product computes the world. UI displays it. CLI (including a console button in the UI) interrogates the same run. Deep CLI integration would freeze a throwaway command set into the simulator.

**Goal:** [G-005 Living map](../goals/G-005-living-map.md) · command language: [G-009](../goals/G-009-simulation-runner-harden.md) F-048–F-049
