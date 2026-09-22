<!--
  File: docs/paperwork/decisions/ADR-011-local-webview.md
  Purpose: Decision record ADR-011
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-011 — Local webview UI (Tauri + Next + Java HTTP host)

**Date:** 2026-09-19  
**Status:** accepted

The interactive map front is a **local web app** inside a **Tauri 2** webview. Simulation stays in Java behind a **localhost HTTP** facade over `ProductSession` / MapController-equivalent logic. Next.js owns presentation. Tauri owns the window and process lifecycle for the Java host.

| Layer | Role |
|-------|------|
| **engine** / **product** | Unchanged — world rules and session; no React/Tauri/HTTP UI deps; product still has no Swing |
| **Java host** | Thin HTTP adapter: one session, serialized advances, raster + tool ops, placeholder console via `cli` dispatch |
| **Next.js** | Tool UI (parity with F-022/F-023) |
| **Tauri** | Desktop shell; spawn/stop host; load UI |

**Amends ADR-010:** G-005 live access was in-process only (“no socket”). G-006 allows **same-machine localhost HTTP** between webview front and Java host. Still one session owner; advances stay serialized. Not a remote multiplayer host. Commands remain placeholders — do not put verb names into Systems or Pool fields.

**Swing:** **removed** as the product map (F-026). Headless map/raster tests remain the behavioral bar.

**Why:** Visual iteration and hot reload need a web front; a webview shell keeps the “local app” feel without moving simulation out of Java.

**Goal:** [G-006 Local webview front](../goals/G-006-webview-front.md)
