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
| **Engine specs** | [engine/specs/](engine/specs/) | Active — through F-012 / G-002; category authorship ADR-009 |
| **Product** | [product/architecture.md](product/architecture.md) | Active — G-009 done (through F-054); G-008 boundary tectonics + studio done |

---

## Goals

**Active Goal:** none  
**Last completed:** [G-009 Simulation runner harden](project/goals/G-009-simulation-runner-harden.md)  
**Prior:** [G-008 Boundary tectonics + cartography studio](project/goals/G-008-boundary-tectonics-studio.md) — done · [G-007 Studio cartography tool](project/goals/G-007-studio-cartography.md) — done · [G-006 Local webview front](project/goals/G-006-webview-front.md) — done · [G-005 Living map](project/goals/G-005-living-map.md) — done · [G-004 See the world](project/goals/G-004-see-the-world.md) — done · [G-003 First product world](project/goals/G-003-first-product-world.md) — done · [G-002 Engine host readiness](project/goals/G-002-engine-host-readiness.md) — done · [G-001 Engine skeleton](project/goals/G-001-engine-skeleton.md) — done

---

## Monorepo layout (F-001)

| Path | Purpose |
|------|---------|
| `pom.xml` | Parent aggregator `com.aethelgard:aethelgard` |
| `engine/` | Pool-System Framework (`com.aethelgard:engine`) — clean host |
| `cli/` | Active — F-049 full headless runner + F-048 noun/verb (`com.aethelgard:cli`) |
| `ui/` | Active — F-050 Terminal + F-023 console path (`com.aethelgard:ui`) |
| `product/` | Active — F-021 orogeny; map chrome in `ui` (`com.aethelgard:product`) |

**One-way rule:** `ui` → `product` → `engine`; `cli` → `product` → `engine`; `ui` may depend on `cli` only for the console (ADR-010 / F-023). Engine never depends on siblings. Details: [engine/architecture.md](engine/architecture.md) · [project/decisions.md](project/decisions.md).

**Package root:** `com.aethelgard.engine` (+ `.pool`, `.event`, `.diag`, `.system`, `.merge`, `.user`) · `com.aethelgard.product` (F-013) · **Java:** 21

**Modules:** `engine`, `cli`, `ui`, `product`.

---

## Host extension points (G-002)

Product plugs into the engine without editing `engine` for ordinary feature growth. Detail: [engine/architecture.md](engine/architecture.md).

| Port | Wire via | Default |
|------|----------|---------|
| `PoolCompute` | `EngineSetup.poolCompute` | `SkeletonPoolCompute` |
| `FieldMergeType` | `FieldSchema` | `FieldType` (Static / Increment / Constant / Destructive) |
| `EventEmissionPolicy` | `EngineSetup.eventEmissionPolicy` | `ScriptedEventEmissionPolicy` |

---

## Per-Step architecture

When implementing F-0xx, extend this file or the layer-specific architecture doc with **only** what that Step needs. No deep trees ahead of need.
