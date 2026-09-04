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
| **Engine** | [engine/architecture.md](engine/architecture.md) | Active — F-001 scaffold |
| **Engine specs** | [engine/specs/](engine/specs/) | Active |
| **Product** | _(implementation architecture TBD)_ | After G-001 |

---

## Current Goal

[G-001 Engine skeleton](project/goals/G-001-engine-skeleton.md) — Pool-System runnable core before product features.

---

## Monorepo layout (F-001)

| Path | Purpose |
|------|---------|
| `pom.xml` | Parent aggregator `com.aethelgard:aethelgard` |
| `engine/` | Pool-System Framework (`com.aethelgard:engine`) |
| `cli/` | Planned — F-007 |
| `ui/` | Planned — F-008 |
| `product/` | Planned — after G-001 |

**One-way rule:** `product` / `cli` / `ui` → `engine`; never the reverse. Details: [engine/architecture.md](engine/architecture.md).

**Package root:** `com.aethelgard.engine` · **Java:** 21

---

## Per-Step architecture

When implementing F-0xx, extend this file or the layer-specific architecture doc with **only** what that Step needs. No deep trees ahead of need.
