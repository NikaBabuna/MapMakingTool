<!--
  File: docs/paperwork/decisions/ADR-009-category-tree.md
  Purpose: Decision record ADR-009
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-009 — Product authors the category tree in Java

**Date:** 2026-09-17  
**Status:** accepted

The event **category tree is application-owned**. For G-003, `product` builds it in Java (`CategoryTree.of("world/tectonics")` via `ProductCategories`). The engine provides `CategoryTree` / ancestry claiming only. No file format, DSL, or engine-owned world tree in this Goal.

**Why:** Open question #2a was an application concern. F-015 needs a real tree to dispatch generation; product Java is the smallest honest authorship model.

Resolves: [../engine/specs/open-questions.md](../../engine/specs/open-questions.md) #2a.
