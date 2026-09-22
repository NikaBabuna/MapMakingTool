<!--
  File: docs/paperwork/decisions/ADR-008-diagnostics.md
  Purpose: Decision record ADR-008
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-008 — Engine diagnostics via SLF4J + capturable port

**Date:** 2026-09-04  
**Status:** accepted

`engine` depends on **SLF4J API** only. `EngineDiagnostics` is the observability port (Step start/settle, emit, claim, unmatched). Default bridge logs to SLF4J (DEBUG for lifecycle/emit/claim; **WARN** for unmatched — ADR-006). Bindings (Logback, `slf4j-simple`, etc.) live in adapters or test scope — not as `engine` compile deps. Tests use `RecordingDiagnostics` to assert without scraping stdout. Diagnostics must not affect Pool determinism.

**Why:** Unmatched events are a correctness obligation; operators also need to see the machine run. A port keeps both testable and adapter-friendly.
