<!--
  File: docs/paperwork/decisions/ADR-004-goal-and-step.md
  Purpose: Decision record ADR-004
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-004 — Goal / Session / Step agent procedure

**Date:** 2026-09-04  
**Status:** accepted

Coding work uses Goal (multi-session) → Session (temporary chat focus) → Step (`F-0xx` with blockers). Order: negotiate job + functional requirements → user approval → mark `in progress` → plan → code → blockers/tests from requirements → witness → sync docs → final check → commit. Torn Steps roll back. Docs win over chat.

**Why:** Makes AI progress autonomous across chats, honest on failure, and grouped under durable Goals without cluttering the registry.

**Supersedes:** Earlier “blockers before any production code” ordering in the draft protocol. Requirements still precede code; executable blockers witness after code for that Step.

**Amended by:** ADR-014 — the Session level is retired. Goal and Step remain.
