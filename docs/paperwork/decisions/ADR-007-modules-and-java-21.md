<!--
  File: docs/paperwork/decisions/ADR-007-modules-and-java-21.md
  Purpose: Decision record ADR-007
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-007 — Multi-module layout and Java 21

**Date:** 2026-09-04  
**Status:** accepted
**Amended by:** [ADR-010](ADR-010-product-adapters.md)

Monorepo Maven parent `com.aethelgard:aethelgard` with module `engine` (`com.aethelgard:engine`) first. Package root `com.aethelgard.engine`. Java 21. Future sibling modules `cli`, `ui`, `product` depend on `engine`; engine never depends on them. Empty sibling modules are not created until their Steps.

**Why:** Preserves a production-grade dependency boundary without pre-carving unused trees. Details: [../engine/architecture.md](../../architecture/engine/README.md).
