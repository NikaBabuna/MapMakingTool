<!--
  File: docs/architecture/studio/session.md
  Purpose: ProductSession, the advance lock, dump, and the diagnostics hub
  Audience: Agents and humans
  Update when: ProductSession.advance or DiagnosticsHub changes
-->

# Session

`ProductSession` owns one `Engine` built by `ProductHost.create`. Callers advance and read grids through the session. The session is not a command parser.

## What it reads

A `WorldSpec`. `ofDefault()` uses 8×8, seed 0. The map window uses `WorldSpec.VIEW`, 1920×1080, seed 0.

## What it writes

Steps on that engine, under one lock. `advance` and `advance(n)` are serialized, so a UI poll and a CLI line cannot interleave `runStep`.

## Procedure

Create completes step 0 inside `ProductHost.create`. Further steps go through `advance`. While an advance holds the lock, another advance waits.

`DiagnosticsHub.withDefaults` registers ring collectors, default capacity 64: `advance.wall`, `heap.used`, `heap.max`, `paint.wall`, and the phase collectors `phase.trace`, `phase.interaction`, `phase.integrate`, `phase.apply`, `phase.orogeny`, `phase.isostasy`. Each collector can be enabled, disabled, and cleared. The rings are not Pool fields. `TimingSubSystem` records a phase duration around the sub-system it wraps. The engine's own `EngineDiagnostics` port is separate. It is [the host diagnostics page](../host/diagnostics.md).

`WorldDump` writes a canonical text of the settled fields. The golden dump is `WorldSpec.DEFAULT` after `advance(3)`. The header, elevation, plates, occupancy, lockers, velocities, registry, boundaries, area flux, and motion intent are in that dump.

`generationIndex` inside apply is the Pool heartbeat minus 1. The heartbeat is the skeleton compute, not a field.

## What is true afterwards

One session is one world. Grids returned to a caller are the settled fields after merge. A second advance does not start until the first has released the lock.

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| Session | `ProductSession` | `product/.../ProductSession.java` |
| Wiring | `ProductHost` | `product/.../ProductHost.java` |
| Hub | `DiagnosticsHub` | `product/.../DiagnosticsHub.java` |
| Dump | `WorldDump` | `product/.../WorldDump.java` |

Parent: [studio](README.md).
