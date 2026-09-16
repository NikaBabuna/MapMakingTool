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
| **Product** | [product/architecture.md](product/architecture.md) | Active — F-017 Voronoi plates; G-004 in progress |

---

## Goals

**Active Goal:** [G-004 See the world](project/goals/G-004-see-the-world.md) (`in progress`)  
**Last completed:** [G-003 First product world](project/goals/G-003-first-product-world.md)  
**Prior:** [G-002 Engine host readiness](project/goals/G-002-engine-host-readiness.md) — done · [G-001 Engine skeleton](project/goals/G-001-engine-skeleton.md) — done

---

## Monorepo layout (F-001)

| Path | Purpose |
|------|---------|
| `pom.xml` | Parent aggregator `com.aethelgard:aethelgard` |
| `engine/` | Pool-System Framework (`com.aethelgard:engine`) — clean host |
| `cli/` | Active — F-007 (`com.aethelgard:cli`) |
| `ui/` | Active — F-008 (`com.aethelgard:ui`) |
| `product/` | Active — F-017 (`com.aethelgard:product`) |

**One-way rule:** `product` / `cli` / `ui` → `engine`; never the reverse. Details: [engine/architecture.md](engine/architecture.md).

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
