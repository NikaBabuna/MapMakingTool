<!--
  File: docs/paperwork/decisions/ADR-018-scope-in-concept.md
  Purpose: Decision record ADR-018
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-018 — Scope lives in the concept

**Date:** 2026-09-24
**Status:** accepted

The product's scope is part of the concept. `docs/product/concept.md` states what the product is for, what a person can do with it today, what is in scope, and what it is not. Its **In scope** table and its **What it is not** list are binding. `docs/project/` is removed, and so is the scope blueprint that shaped it.

The stack moves to the program page. `docs/architecture/program.md` lists each choice of language, build, layout, and runtime dependency, with the decision records that made it.

**Why:** The scope page and the concept answered the same question, what the product includes and refuses, on two shelves. The scope page had fallen behind the concept: it still carried a placeholder exclusion and a note tied to an old Goal. Its stack table described the program, which the product shelf does not.

**Supersedes:** the sentence of [ADR-016](ADR-016-paperwork-shelf.md) that scope stays `docs/project/project.md`.

**Goal:** [G-011 Docs restructuring](../goals/G-011-docs-restructuring.md)
