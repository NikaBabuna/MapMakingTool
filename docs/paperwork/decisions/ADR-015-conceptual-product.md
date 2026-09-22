<!--
  File: docs/paperwork/decisions/ADR-015-conceptual-product.md
  Purpose: Decision record ADR-015
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-015 — Product pages are conceptual

**Date:** 2026-09-22
**Status:** accepted

`docs/product/` explains the product to a person. The pages do not describe how the application is built or how it operates. That record belongs on the architecture shelf. Until that shelf is gathered, the operational papers stay where they already are.

What a person does is `docs/product/journeys.md`. `docs/product/flows.md` is removed, so the word flow means a protocol flow.

**Why:** The old product pages mixed world rules with type names, file paths, and Step logs, and the journeys file shared a name with the bookkeeping algorithms.

**Supersedes:** the sentence that user journeys live in `docs/product/flows.md`.

**Goal:** [G-011 Docs restructuring](../goals/G-011-docs-restructuring.md)

