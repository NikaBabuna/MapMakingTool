<!--
  File: docs/architecture.md
  Purpose: Roll-up of known structure (protocol read-order entry)
  Audience: Agents and humans
  Update when: Engine or product structure is decided
-->

# Architecture

**Phase:** alpha — structure may change.

Roll-up of structural documentation. Another agent must not need to guess where things live.

---

## Layers

| Layer | Doc | Status |
|-------|-----|--------|
| **Engine** | [engine/architecture.md](engine/architecture.md) | Active — G-002 host ports done |
| **Engine specs** | [engine/specs/](engine/specs/) | Active |
| **Product** | _(implementation architecture TBD)_ | After G-002 |

---

## Current Goal

[G-002 Engine host readiness](project/goals/G-002-engine-host-readiness.md) — **done**.  
Propose next: product world generation (roadmap).  
Prior: [G-001 Engine skeleton](project/goals/G-001-engine-skeleton.md) — **done**.

---

## Monorepo layout (F-001)

| Path | Purpose |
|------|---------|
| `pom.xml` | Parent aggregator `com.aethelgard:aethelgard` |
| `engine/` | Pool-System Framework (`com.aethelgard:engine`) |
| `cli/` | Active — F-007 (`com.aethelgard:cli`) |
| `ui/` | Active — F-008 (`com.aethelgard:ui`) |
| `product/` | Planned — after G-002 |

**One-way rule:** `product` / `cli` / `ui` → `engine`; never the reverse. Details: [engine/architecture.md](engine/architecture.md).

**Package root:** `com.aethelgard.engine` (+ `.pool`, `.event`, `.diag`, `.system`, `.merge`, `.user`) · **Java:** 21

**Modules:** `engine`, `cli`, `ui` — product still later.

---

## Per-Step architecture

When implementing F-0xx, extend this file or the layer-specific architecture doc with **only** what that Step needs. No deep trees ahead of need.
