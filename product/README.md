<!--
  File: product/README.md
  Purpose: Landmark index for the product Maven module
  Audience: Agents and humans
  Update when: Product layout or host wiring changes
-->

# Product module

Maven artifact `com.aethelgard:product` — Aethelgard world generation on the Pool-System host.

**Depends on:** `engine` (one-way). Never depended on by `engine`, `cli`, or `ui`.

Package root: `com.aethelgard.product`.

`ProductHost` constructs an `Engine` via product `EngineSetup`. Generation: `elevation` + Voronoi `plates`, `world/tectonics` after Step 0, tectonics System (collision uplift). `WorldDump` prints a settled snapshot (tests + formatter; no product `Main`).

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md) · [wiki/world](../docs/product/wiki/world.md) · [wiki/elevation](../docs/product/wiki/elevation.md)
