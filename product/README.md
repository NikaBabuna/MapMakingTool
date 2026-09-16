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

`ProductHost` constructs an `Engine` via product `EngineSetup`. F-013 uses engine defaults; later G-003 Steps add world fields and Systems here.

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md)
