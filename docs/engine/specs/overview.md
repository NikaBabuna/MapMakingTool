<!--
  File: docs/engine/specs/overview.md
  Purpose: Pool-System Framework — core idea
  Audience: Agents and humans
  Update when: Framework purpose changes
-->

# Overview

> **Code status (through F-012 / G-002):** The Pool-System loop is implemented and host-ready. Product plugs in via `PoolCompute`, `FieldMergeType`, and `EventEmissionPolicy` without editing engine internals. CLI and UI are product adapters (F-019 / ADR-010). Placeholder console dispatcher is F-023. **G-005** done.

**Scope:** General-purpose architecture for step-based computation and simulation. Independent of any particular game, product, or service.

The architecture rests on one shared state object, the **Pool**, and independent **Systems** that react to Pool changes without talking to each other directly. Time moves in discrete **Steps**. Each Step is a full transaction: the Pool computes and emits events, Systems whose categories match those events react and produce output, and results merge back into the Pool under deterministic rules before the next Step begins.

One problem recurs at three scales: independent writers need to update shared state without coordinating, and the result must be identical regardless of execution order. The answer is the same at every scale: attach a **type** to data, and let the type define how conflicting writes resolve. The engine does not hardcode merge rules; every merge rule lives on the data it merges (`FieldMergeType` — defaults plus custom types).

See [architecture-diagram.md](architecture-diagram.md) for the loop diagram. Host wiring: [../architecture.md](../architecture.md).
