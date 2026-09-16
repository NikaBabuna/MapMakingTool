<!--
  File: docs/product/concept.md
  Purpose: Product vision — what Aethelgard is and why
  Audience: Humans and agents
  Update when: Product direction changes
-->

# Aethelgard

**Tagline:** A world that remembers how it was made.

---

## Problem

Hand-designing a fantasy map is enjoyable until consistency breaks down. Mountains placed for silhouette, deserts for story, rivers for aesthetics — then the river flows uphill, the desert sits on the wet side of the range, or the climate fails a second glance. Fixing it means becoming a geography teacher mid-project. Ignoring it means a map that quietly does not hold together.

**Noise generators** produce good-looking terrain with no causal reason — mountains exist because a function said so. **Hand-painted tools** offer total freedom and zero help staying consistent. Neither simulates a world; both produce a picture of one.

---

## Approach

Build worlds by **simulating the processes that make geography**, not by painting the result.

```
Tectonic plates → elevation → wind → rainfall → temperature → biomes
```

Nothing on the map is placed. Everything is **caused**.

A desert does not need to be checked against neighboring mountains — it exists as their consequence. The map cannot drift out of physical sense because it was never drawn freehand. It was worked out.

---

## Use modes

| Mode | Description |
|------|-------------|
| **Explore** | Generate seeds until a world feels right; use as-is or as foundation |
| **Guide** | Nudge what matters (range placement, coastline dryness); simulation fills the rest consistently |

Both modes are first-class. See [flows.md](flows.md) for journeys (to be detailed).

---

## Signature feature: world history

The map is not a finished image — it is a **timeline**.

- Scrub from first plate drift to present day
- Rewind coastlines before ice ages; fast-forward rising ranges
- Select any point and see **why** it looks that way and what had to happen first

Every feature answers "why is this here" with a real causal chain.

---

## Audience

Tabletop campaigns, novels, personal worldbuilding — anywhere the map must survive close inspection. For people who enjoy designing fantasy worlds and do not want physics to be the fight.

---

## Engine

Implemented on the [Pool-System Framework](../engine/specs/overview.md) — step-based simulation with deterministic merge and cross-step consequence chains suited to geological time. After **G-002**, the engine is a clean host: Aethelgard Systems and world rules plug in via `PoolCompute`, `FieldMergeType`, and `EventEmissionPolicy` without editing `engine`.
